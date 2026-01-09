# 前端请求风暴性能优化报告

## 问题概述

在系统运行过程中，发现了一个严重的性能问题：**前端请求风暴**。在极短的时间窗口内（约08:49:35到08:49:37），系统收到了大量重复的请求，主要集中在两个端点：

1. `/api/users/login` - 用户登录接口
2. `/api/material-categories/tree` - 获取耗材分类树接口

## 问题分析

### 1. 登录接口的并发问题

**现象：**
- 同一用户（admin）在同一秒内发起了多次登录请求
- 每次请求都完整执行了数据库查询和更新操作
- 浪费了服务器资源，增加了数据库负载

**根本原因：**
- 登录按钮没有防抖机制
- 用户可能快速多次点击登录按钮
- 按Enter键也可能触发多次提交

### 2. 获取分类树接口的重复请求

**现象：**
- 登录成功后，前端立即发起了多次获取分类树的请求
- 每次请求都重新查询数据库并构建树形结构
- 没有请求去重机制

**根本原因：**
- 登录后可能有多个组件同时挂载并请求数据
- 没有请求去重机制
- 没有缓存机制

### 3. 数据库连接池压力

**现象：**
- 大量的并发请求导致数据库连接池被快速占用
- 日志中出现大量的 `Creating a new SqlSession` 和 `JDBC Connection` 消息
- 连接池配置可能不够优化

**根本原因：**
- HikariCP连接池配置使用默认值
- 没有针对高并发场景进行优化

## 解决方案

### 1. 前端解决方案

#### 1.1 登录页面防抖优化

**文件：** `frontend/src/views/LoginView.vue`

**实现：**
```typescript
// 防抖定时器
let loginDebounceTimer: ReturnType<typeof setTimeout> | null = null

// 处理登录（带防抖）
const handleLogin = async () => {
  // 如果正在加载，直接返回
  if (loading.value) {
    return
  }

  // 清除之前的防抖定时器
  if (loginDebounceTimer) {
    clearTimeout(loginDebounceTimer)
  }

  // 设置新的防抖定时器（500ms）
  loginDebounceTimer = setTimeout(async () => {
    // 登录逻辑...
  }, 500)
}
```

**效果：**
- 500ms内的多次点击只会触发一次登录请求
- 配合loading状态，防止重复提交
- 显著减少重复的登录请求

#### 1.2 耗材分类管理页面请求去重

**文件：** `frontend/src/views/MaterialCategoryManage.vue`

**实现：**
```typescript
// 请求去重标志
let isFetchingTree = false

// 获取分类树（带请求去重）
const getCategoryTree = async () => {
  // 如果正在请求，直接返回
  if (isFetchingTree) {
    return
  }

  isFetchingTree = true
  try {
    const response = await getMaterialCategoryTree(searchForm.status)
    // 处理响应...
  } catch (error) {
    ElMessage.error('获取分类树失败')
  } finally {
    isFetchingTree = false
  }
}
```

**效果：**
- 防止同时发起多个相同的请求
- 确保同一时间只有一个请求在执行
- 减少不必要的数据库查询

### 2. 后端解决方案

#### 2.1 数据库连接池优化

**文件：** `backend/src/main/resources/application.yml`

**实现：**
```yaml
spring:
  datasource:
    hikari:
      # 连接池大小配置
      maximum-pool-size: 20
      minimum-idle: 5
      # 连接超时配置
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      # 连接测试配置
      connection-test-query: SELECT 1
      # 性能优化配置
      pool-name: HaocaiHikariCP
      register-mbeans: true
```

**配置说明：**
- `maximum-pool-size: 20` - 最大连接数增加到20，可以处理更多并发请求
- `minimum-idle: 5` - 最小空闲连接数5，保证有足够的连接可用
- `connection-timeout: 30000` - 连接超时30秒，避免长时间等待
- `idle-timeout: 600000` - 空闲连接超时10分钟，及时释放不用的连接
- `max-lifetime: 1800000` - 连接最大生命周期30分钟，避免使用过期连接
- `connection-test-query: SELECT 1` - 连接测试查询，确保连接可用
- `pool-name: HaocaiHikariCP` - 连接池名称，便于监控
- `register-mbeans: true` - 注册MBeans，便于性能监控

**效果：**
- 提高数据库连接池的并发处理能力
- 优化连接的生命周期管理
- 减少连接创建和销毁的开销
- 提供更好的监控能力

## 优化效果

### 1. 减少重复请求
- 登录请求：从多次重复请求减少到单次请求
- 分类树请求：从多次重复请求减少到单次请求

### 2. 降低服务器负载
- 减少了不必要的数据库查询
- 减少了JWT token的生成和验证
- 降低了CPU和内存使用率

### 3. 提高响应速度
- 减少了网络传输时间
- 减少了数据库查询时间
- 提升了用户体验

### 4. 优化资源利用
- 数据库连接池得到更合理的利用
- 减少了连接创建和销毁的开销
- 提高了系统的整体吞吐量

## 后续建议

### 1. 前端优化
- **实现全局请求拦截器**：在axios拦截器中实现全局的请求去重
- **添加缓存机制**：对不常变化的数据（如分类树）实现前端缓存
- **实现请求队列**：对关键请求实现队列机制，确保顺序执行

### 2. 后端优化
- **实现Redis缓存**：对分类树等不常变化的数据实现Redis缓存
- **实现接口幂等性**：对登录等关键接口实现幂等性控制
- **添加限流机制**：使用Spring Cloud Gateway或Sentinel实现接口限流
- **优化SQL查询**：对频繁查询的SQL进行优化，添加必要的索引

### 3. 监控和告警
- **添加性能监控**：使用Prometheus + Grafana监控系统性能
- **设置告警规则**：对异常请求频率设置告警
- **日志分析**：定期分析日志，发现潜在的性能问题

## 总结

通过本次优化，我们成功解决了前端请求风暴的问题，显著提升了系统的性能和稳定性。主要改进包括：

1. ✅ 登录页面添加防抖机制，防止重复提交
2. ✅ 耗材分类管理页面添加请求去重机制
3. ✅ 优化数据库连接池配置，提高并发处理能力

这些优化措施有效减少了重复请求，降低了服务器负载，提高了系统的响应速度和用户体验。建议后续继续实施缓存、限流等优化措施，进一步提升系统性能。

## 相关文件

- `frontend/src/views/LoginView.vue` - 登录页面（已优化）
- `frontend/src/views/MaterialCategoryManage.vue` - 耗材分类管理页面（已优化）
- `backend/src/main/resources/application.yml` - 后端配置文件（已优化）
