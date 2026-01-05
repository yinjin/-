       
## 一、系统需求

### 1.1 系统概述
**系统名称**: 高职人工智能学院实训耗材管理系统
**目标**: 实现实训耗材的全生命周期管理，包括入库、出库、库存管理，并通过数据大屏展示关键指标，提升管理效率和决策支持能力。

**系统定位**:
- 为高职人工智能学院提供专业化的实训耗材管理解决方案
- 实现耗材从采购入库到使用归还的全流程数字化管理
- 提供数据驱动的决策支持，优化耗材配置和使用效率

### 1.2 功能需求详细说明

#### 1.2.1 基础管理模块
- **耗材分类管理**
  - 支持多级分类（如：硬件类、软件类、工具类、材料类等）
  - 分类增删改查
  - 分类编码自动生成
  - 分类层级可视化展示

- **耗材信息管理**
  - 耗材基本信息：名称、规格型号、单位、单价、供应商、保质期等
  - 耗材分类归属
  - 耗材图片上传（支持多张图片）
  - 耗材状态（正常、停用、报废）
  - 耗材条码/二维码生成与管理
  - 耗材供应商信息关联
  - 耗材使用说明文档管理

- **用户管理**
  - 用户角色：管理员、教师、学生、仓库管理员
  - 用户信息：姓名、工号/学号、部门、联系方式
  - 权限管理（细粒度权限控制）
  - 用户登录日志记录
  - 用户操作权限验证

- **部门管理**
  - 部门信息维护
  - 部门层级结构管理
  - 部门与用户关联

#### 1.2.2 入库管理模块
- **入库单管理**
  - 创建入库单（支持批量入库）
  - 入库单信息：入库单号（自动生成）、入库日期、供应商、经办人、备注
  - 入库明细：耗材名称、数量、单价、批次号、生产日期、保质期
  - 入库单状态跟踪（待审核、已审核、已拒绝、已完成）

- **入库审核**
  - 管理员审核入库单
  - 审核意见填写
  - 审核通过后自动更新库存
  - 审核拒绝原因说明

- **入库记录查询**
  - 按时间、耗材、供应商、经办人等多条件查询
  - 导出入库记录（Excel、PDF格式）
  - 入库单打印功能

- **供应商管理**
  - 供应商信息维护
  - 供应商资质管理
  - 供应商评价体系

#### 1.2.3 出库管理模块
- **领用申请**
  - 填写领用人信息（姓名、工号/学号、部门）
  - 选择耗材和数量
  - 填写用途分类（教学用、科研用、竞赛用、其他）
  - 填写具体信息：
    - 教学用：课程名称、班级、授课教师、学期
    - 科研用：科研项目名称、负责人、项目编号、项目周期
    - 竞赛用：竞赛名称、参赛团队、指导教师、竞赛时间
    - 其他：具体用途说明
  - 填写预计归还日期（如需归还）
  - 上传相关附件（如课程表、项目书、竞赛通知等）
  - 申请单状态跟踪

- **领用审批**
  - 根据领用金额和数量设置多级审批流程
  - 管理员审批领用申请
  - 审批意见填写
  - 审批通过后生成出库单
  - 审批拒绝原因说明
  - 审批流程可视化

- **出库管理**
  - 仓库管理员执行出库操作
  - 扫码或手动确认出库
  - 更新库存数量
  - 出库单打印功能
  - 出库记录追溯

- **归还管理**
  - 支持耗材归还登记
  - 记录归还状态（完好、损坏、丢失）
  - 损坏或丢失需填写说明
  - 归还验收确认
  - 损坏赔偿处理

- **借用管理**
  - 支持长期借用管理
  - 借用期限设置
  - 借用到期提醒
  - 借用续借申请

#### 1.2.4 库存管理模块
- **库存查询**
  - 实时库存查询
  - 按分类、名称、供应商、状态等多条件筛选
  - 库存预警（低于安全库存）
  - 库存历史变动记录查询

- **库存盘点**
  - 创建盘点单
  - 录入实际库存数量
  - 支持扫码盘点
  - 自动计算盘盈盘亏
  - 生成盘点报告
  - 盘点差异处理

- **库存预警**
  - 设置安全库存阈值
  - 低库存自动预警
  - 临期耗材预警（保质期提醒）
  - 预警通知机制（系统消息、邮件）
  - 预警处理记录

- **库存调拨**
  - 支持不同仓库间的调拨
  - 调拨单管理
  - 调拨审批流程
  - 调拨记录追溯

- **库存统计**
  - 库存周转率分析
  - 库存价值统计
  - 滞销耗材分析
  - 库存成本分析

#### 1.2.5 数据大屏模块
- **实时数据展示**
  - 最常用的五种耗材（按出库频次排序）
  - 库存最多的五种耗材（按库存数量排序）
  - 库存最少的五种耗材（按库存数量排序）
  - 今日入库/出库数量
  - 本月入库/出库金额
  - 库存总价值
  - 库存预警数量
  - 待处理申请数量

- **图表展示**
  - 耗材分类占比饼图
  - 出库趋势折线图（近30天/近半年/近一年）
  - 用途分类柱状图（教学/科研/竞赛/其他）
  - 库存预警列表
  - 月度入库出库对比图

- **数据刷新**
  - 自动定时刷新（如每5分钟）
  - 手动刷新按钮
  - 数据缓存机制

- **大屏配置**
  - 大屏布局自定义
  - 数据展示时间范围选择
  - 大屏主题切换

#### 1.2.6 统计报表模块
- **入库统计**
  - 按时间、分类、供应商、经办人统计
  - 入库趋势分析
  - 供应商供货统计
  - 导出Excel报表

- **出库统计**
  - 按时间、用途、领用人、部门统计
  - 教学耗材使用统计（按课程、学期）
  - 科研耗材使用统计（按项目、负责人）
  - 竞赛耗材使用统计（按竞赛、团队）
  - 出库趋势分析
  - 导出Excel报表

- **库存统计**
  - 库存周转率分析
  - 库存价值分析
  - 滞销耗材分析
  - 库存成本分析
  - 库存结构分析
  - 导出Excel报表

- **用户使用统计**
  - 用户领用频次统计
  - 用户部门使用统计
  - 用户权限使用统计

- **综合报表**
  - 月度/季度/年度综合报表
  - 自定义报表生成
  - 报表定时生成与推送

### 1.3 非功能需求

#### 1.3.1 性能要求
- 系统响应时间：页面加载不超过3秒
- 支持并发用户数：至少100人同时在线
- 数据查询响应时间：不超过2秒
- 大屏数据刷新时间：不超过10秒
- 文件上传下载速度：支持大文件分片上传

#### 1.3.2 安全要求
- 用户身份认证（JWT Token）
- 角色权限控制（RBAC模型）
- 操作日志记录（审计追踪）
- 数据备份（每日备份，支持增量备份）
- 数据加密（敏感信息加密存储）
- 防SQL注入、XSS攻击等安全措施
- 文件上传安全检查（病毒扫描、格式限制）
- 访问频率限制（防刷接口）

#### 1.3.3 可用性要求
- 系统可用性：99.5%
- 界面友好，操作简单，符合用户习惯
- 支持主流浏览器（Chrome、Firefox、Safari、Edge）
- 响应式设计，支持移动端访问
- 提供操作帮助文档和提示
- 系统故障自动恢复机制

#### 1.3.4 可扩展性要求
- 模块化设计，便于功能扩展
- 支持微服务架构扩展
- 数据库设计支持水平扩展
- 接口设计遵循RESTful规范，便于第三方集成

#### 1.3.5 兼容性要求
- 支持Windows、Linux、macOS等主流操作系统
- 支持主流数据库（MySQL、PostgreSQL）
- 支持主流应用服务器（Tomcat、Jetty）

### 1.4 技术栈

#### 1.4.1 技术栈选型说明

**前端技术栈：**
- **Vue 3.4+**：采用Composition API，提供更好的TypeScript支持和性能优化
- **Vite 5.0+**：新一代前端构建工具，开发体验和构建速度显著提升
- **Element Plus 2.4+**：基于Vue 3的UI组件库，组件丰富，文档完善
- **ECharts 5.5+**：强大的数据可视化库，支持丰富的图表类型
- **Axios 1.6+**：HTTP客户端，支持请求拦截、响应拦截等功能
- **Pinia 2.1+**：Vue 3官方推荐的状态管理库，轻量且类型友好
- **Vue Router 4.2+**：Vue 3官方路由管理器
- **TypeScript 5.3+**：提供类型安全，提升代码质量和开发效率

**后端技术栈：**
- **Spring Boot 3.2+**：基于Spring Framework 6.1，支持虚拟线程等新特性
- **JDK 17+**：LTS版本，性能优化，支持记录模式、模式匹配等新特性
- **MyBatis-Plus 3.5.5+**：增强版MyBatis，简化CRUD操作，提供代码生成器
- **MySQL 8.0+**：支持窗口函数、CTE、JSON类型等高级特性
- **Redis 7.0+**：性能提升，支持客户端缓存、函数等新特性
- **HikariCP 5.0+**：高性能JDBC连接池，Spring Boot 3默认连接池
- **Spring Security 6.2+**：强大的安全框架，支持OAuth2、JWT等
- **JWT (jjwt 0.12+)**：JSON Web Token实现，用于用户认证
- **SLF4J + Logback 1.4+**：日志框架，支持异步日志、日志压缩
- **SpringDoc OpenAPI 2.3+**：基于OpenAPI 3规范的API文档生成工具
- **Maven 3.9+**：项目构建和依赖管理工具

**其他技术：**
- **Docker 24+**：容器化部署，简化环境配置
- **Nginx 1.25+**：高性能Web服务器和反向代理服务器
- **Jenkins 2.440+**：持续集成和持续部署工具
- **Git 2.42+**：版本控制系统

**开发工具：**
- **IntelliJ IDEA 2023.3+**：强大的Java IDE，支持Spring Boot开发
- **Visual Studio Code 1.85+**：轻量级代码编辑器，支持Vue 3开发
- **Node.js 20+**：前端开发环境，LTS版本
- **Postman 10+**：API测试工具
- **Navicat Premium 16+**：数据库管理工具
- **RedisInsight 2.50+**：Redis可视化管理工具

#### 1.4.2 技术栈版本合理性分析

**✅ 合理的版本选择：**

1. **Spring Boot 3.2+**
   - 优势：基于Spring Framework 6.1，性能提升，支持虚拟线程
   - 稳定性：2023年11月发布，经过充分测试
   - 生态：Spring生态成熟，社区活跃
   - 建议：使用3.2.2或更高版本（修复了已知问题）

2. **JDK 17**
   - 优势：LTS版本，性能优化，新特性丰富
   - 稳定性：2021年9月发布，长期支持至2029年
   - 兼容性：Spring Boot 3要求JDK 17+
   - 建议：使用17.0.9或更高版本

3. **Vue 3.4+**
   - 优势：Composition API成熟，性能优化，TypeScript支持好
   - 稳定性：2024年1月发布，Vue 3生态成熟
   - 生态：Element Plus、Pinia等配套库完善
   - 建议：使用3.4.15或更高版本

4. **MySQL 8.0**
   - 优势：性能提升，支持窗口函数、CTE、JSON类型
   - 稳定性：2018年发布，经过充分验证
   - 兼容性：主流ORM框架都支持
   - 建议：使用8.0.35或更高版本

5. **Redis 7.0**
   - 优势：性能提升，支持客户端缓存、函数等新特性
   - 稳定性：2022年发布，经过充分测试
   - 建议：使用7.2.4或更高版本

**⚠️ 需要注意的版本问题：**

1. **Node.js版本**
   - 当前计划：Node.js 18+
   - 建议：升级到Node.js 20 LTS（2023年10月发布，长期支持至2026年4月）
   - 理由：性能更好，安全性更高，Vite 5推荐使用Node.js 20

2. **TypeScript版本**
   - 当前计划：未明确指定
   - 建议：明确使用TypeScript 5.3+
   - 理由：提供更好的类型推断和性能

3. **MyBatis-Plus版本**
   - 当前计划：未明确指定
   - 建议：使用3.5.5+
   - 理由：修复了已知问题，支持Spring Boot 3

#### 1.4.3 技术栈优化建议

**1. 前端优化建议：**

```json
{
  "dependencies": {
    "vue": "^3.4.15",
    "vue-router": "^4.2.5",
    "pinia": "^2.1.7",
    "element-plus": "^2.5.4",
    "echarts": "^5.5.0",
    "axios": "^1.6.5",
    "@vueuse/core": "^10.7.2",
    "dayjs": "^1.11.10",
    "lodash-es": "^4.17.21"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.3",
    "vite": "^5.0.12",
    "typescript": "^5.3.3",
    "vue-tsc": "^1.8.27",
    "sass": "^1.70.0",
    "unplugin-auto-import": "^0.17.5",
    "unplugin-vue-components": "^0.26.0"
  }
}
```

**2. 后端优化建议：**

```xml
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.2.2</spring-boot.version>
    <mybatis-plus.version>3.5.5</mybatis-plus.version>
    <mysql.version>8.0.35</mysql.version>
    <redis.version>7.2.4</redis.version>
    <jjwt.version>0.12.3</jjwt.version>
    <springdoc.version>2.3.0</springdoc.version>
    <hutool.version>5.8.24</hutool.version>
</properties>
```

**3. 新增推荐依赖：**

- **Hutool 5.8+**：Java工具类库，简化常用操作
- **EasyExcel 3.3+**：Excel导入导出工具，性能优异
- **Lombok 1.18+**：简化Java代码，减少样板代码
- **Validation API**：参数校验，配合Spring Validation使用
- **Spring Cache**：缓存抽象，简化Redis缓存使用
- **Spring Actuator**：应用监控和健康检查

#### 1.4.4 技术栈风险评估

**低风险技术：**
- Spring Boot 3.2+：成熟稳定，社区活跃
- Vue 3.4+：生态成熟，文档完善
- MySQL 8.0：广泛使用，稳定可靠
- Redis 7.0：性能优异，经过验证

**中等风险技术：**
- Spring Boot 3.2：较新版本，可能存在未知问题
  - 缓解措施：使用稳定版本（3.2.2+），关注官方更新
- Vue 3.4：较新版本，部分第三方库可能不兼容
  - 缓解措施：选择成熟的配套库，进行充分测试

**需要关注的技术：**
- TypeScript：学习曲线，开发效率初期可能降低
  - 缓解措施：提供培训，使用代码生成工具
- Docker：需要运维知识
  - 缓解措施：提供部署文档，使用成熟的Docker镜像

#### 1.4.5 技术栈总结

**总体评价：** 技术栈选择合理，版本较新，符合当前主流技术趋势。

**优势：**
- 前后端分离，职责清晰
- 技术栈成熟稳定，社区活跃
- 版本较新，性能优异
- 生态完善，第三方库丰富

**建议：**
1. 明确所有依赖的具体版本号
2. 升级Node.js到20 LTS版本
3. 明确使用TypeScript进行前端开发
4. 添加Hutool、EasyExcel等实用工具库
5. 建立依赖版本管理机制，定期更新
6. 关注安全漏洞，及时更新依赖库

          
## 二、系统整体架构设计

### 2.1 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                         前端层 (Vue 3)                        │
├─────────────────────────────────────────────────────────────┤
│  入库管理  │  出库管理  │  库存管理  │  数据大屏  │  统计报表  │
├─────────────────────────────────────────────────────────────┤
│                    API网关 (Axios + 拦截器)                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      后端层 (Spring Boot)                      │
├─────────────────────────────────────────────────────────────┤
│  Controller层  │  Service层  │  Mapper层  │  Entity层        │
├─────────────────────────────────────────────────────────────┤
│  业务模块：入库管理 | 出库管理 | 库存管理 | 统计分析 | 大屏数据 │
├─────────────────────────────────────────────────────────────┤
│  基础组件：用户认证 | 权限控制 | 日志记录 | 异常处理            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        数据层                                │
├─────────────────────────────────────────────────────────────┤
│  MySQL 8.0  │  Redis (缓存)  │  文件存储                    │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 分层设计说明

#### 2.2.1 前端层
- **技术栈**: Vue 3 + Vite + Element Plus + ECharts + Pinia
- **主要模块**:
  - 入库管理模块：入库单创建、审核、查询
  - 出库管理模块：领用申请、审批、出库、归还
  - 库存管理模块：库存查询、盘点、预警
  - 数据大屏模块：实时数据展示、图表可视化
  - 统计报表模块：各类统计报表、数据导出
  - 系统管理模块：用户管理、角色权限、耗材分类

#### 2.2.2 后端层
- **技术栈**: Spring Boot 2.7+ + MyBatis-Plus + Spring Security + JWT
- **分层架构**:
  - **Controller层**: 接收前端请求，参数校验，调用Service
  - **Service层**: 业务逻辑处理，事务控制
  - **Mapper层**: 数据访问，SQL操作
  - **Entity层**: 实体类，对应数据库表

#### 2.2.3 数据层
- **MySQL**: 主数据库，存储业务数据
- **Redis**: 缓存热点数据（如库存信息、大屏数据）
- **文件存储**: 存储耗材图片、附件文件

### 2.3 核心业务流程

#### 2.3.1 入库流程
```
创建入库单 → 提交审核 → 管理员审核 → 审核通过 → 更新库存 → 完成入库
```

#### 2.3.2 出库流程
```
领用申请 → 填写用途信息 → 提交审批 → 管理员审批 → 审批通过 → 仓库出库 → 更新库存
```

#### 2.3.3 盘点流程
```
创建盘点单 → 录入实际库存 → 计算盘盈盘亏 → 生成盘点报告 → 更新库存
```

          
## 三、数据库设计方案

### 3.1 数据库表设计

#### 3.1.1 用户相关表

**用户表 (sys_user)**
```sql
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    employee_no VARCHAR(20) COMMENT '工号/学号',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    department_id BIGINT COMMENT '部门ID',
    role_id BIGINT COMMENT '角色ID',
    avatar VARCHAR(200) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    INDEX idx_username (username),
    INDEX idx_employee_no (employee_no),
    INDEX idx_department_id (department_id),
    INDEX idx_role_id (role_id),
    CONSTRAINT fk_user_department FOREIGN KEY (department_id) REFERENCES sys_department(id),
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
) COMMENT='用户表';
```

**部门表 (sys_department)**
```sql
CREATE TABLE sys_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    dept_code VARCHAR(50) NOT NULL UNIQUE COMMENT '部门编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID，0表示顶级部门',
    level INT DEFAULT 1 COMMENT '部门层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    leader_id BIGINT COMMENT '部门负责人ID',
    contact_info VARCHAR(200) COMMENT '联系方式',
    description TEXT COMMENT '部门描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_dept_code (dept_code),
    CONSTRAINT fk_dept_leader FOREIGN KEY (leader_id) REFERENCES sys_user(id)
) COMMENT='部门表';
```

**角色表 (sys_role)**
```sql
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_code (role_code)
) COMMENT='角色表';
```

**权限表 (sys_permission)**
```sql
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    menu_id BIGINT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID，0表示顶级权限',
    level INT DEFAULT 1 COMMENT '权限层级',
    type TINYINT DEFAULT 1 COMMENT '权限类型：1菜单权限 2按钮权限 3数据权限',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_menu_id (menu_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_permission_code (permission_code)
) COMMENT='权限表';
```

**角色权限关联表 (sys_role_permission)**
```sql
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
) COMMENT='角色权限关联表';
```

#### 3.1.2 耗材相关表

**耗材分类表 (material_category)**
```sql
CREATE TABLE material_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    level INT DEFAULT 1 COMMENT '分类层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    description VARCHAR(200) COMMENT '分类描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_category_code (category_code),
    INDEX idx_sort_order (sort_order)
) COMMENT='耗材分类表';
```

**耗材信息表 (material_info)**
```sql
CREATE TABLE material_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '耗材ID',
    material_code VARCHAR(50) NOT NULL UNIQUE COMMENT '耗材编码',
    material_name VARCHAR(100) NOT NULL COMMENT '耗材名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    specification VARCHAR(200) COMMENT '规格型号',
    unit VARCHAR(20) NOT NULL COMMENT '单位（个、台、套等）',
    unit_price DECIMAL(10,2) COMMENT '单价',
    supplier_id BIGINT COMMENT '供应商ID',
    shelf_life INT COMMENT '保质期（天）',
    barcode VARCHAR(100) COMMENT '条形码',
    qr_code VARCHAR(200) COMMENT '二维码',
    image_url VARCHAR(500) COMMENT '图片URL',
    description TEXT COMMENT '描述',
    technical_parameters TEXT COMMENT '技术参数',
    usage_instructions TEXT COMMENT '使用说明',
    storage_requirements VARCHAR(500) COMMENT '存储要求',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0停用 2报废',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_material_code (material_code),
    INDEX idx_category_id (category_id),
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_barcode (barcode),
    INDEX idx_status (status),
    CONSTRAINT fk_material_category FOREIGN KEY (category_id) REFERENCES material_category(id),
    CONSTRAINT fk_material_supplier FOREIGN KEY (supplier_id) REFERENCES supplier_info(id)
) COMMENT='耗材信息表';
```

**供应商信息表 (supplier_info)**
```sql
CREATE TABLE supplier_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '供应商ID',
    supplier_code VARCHAR(50) NOT NULL UNIQUE COMMENT '供应商编码',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(200) COMMENT '地址',
    business_license VARCHAR(200) COMMENT '营业执照',
    tax_number VARCHAR(50) COMMENT '税号',
    bank_account VARCHAR(100) COMMENT '银行账号',
    bank_name VARCHAR(100) COMMENT '开户行',
    credit_rating TINYINT DEFAULT 5 COMMENT '信用等级（1-10）',
    cooperation_status TINYINT DEFAULT 1 COMMENT '合作状态：1合作中 0已终止',
    description TEXT COMMENT '供应商描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_supplier_code (supplier_code),
    INDEX idx_supplier_name (supplier_name),
    INDEX idx_credit_rating (credit_rating)
) COMMENT='供应商信息表';
```

**库存表 (material_inventory)**
```sql
CREATE TABLE material_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    material_id BIGINT NOT NULL UNIQUE COMMENT '耗材ID',
    quantity INT NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    available_quantity INT NOT NULL DEFAULT 0 COMMENT '可用库存数量（扣除已申请未出库数量）',
    safe_quantity INT DEFAULT 10 COMMENT '安全库存阈值',
    max_quantity INT DEFAULT 1000 COMMENT '最大库存阈值',
    warehouse VARCHAR(50) DEFAULT '主仓库' COMMENT '仓库名称',
    location VARCHAR(100) COMMENT '存放位置',
    last_in_time DATETIME COMMENT '最后入库时间',
    last_out_time DATETIME COMMENT '最后出库时间',
    total_in_quantity INT DEFAULT 0 COMMENT '累计入库数量',
    total_out_quantity INT DEFAULT 0 COMMENT '累计出库数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_material_id (material_id),
    INDEX idx_warehouse (warehouse),
    INDEX idx_location (location),
    INDEX idx_available_quantity (available_quantity),
    CONSTRAINT fk_inventory_material FOREIGN KEY (material_id) REFERENCES material_info(id) ON DELETE CASCADE
) COMMENT='库存表';
```

#### 3.1.3 入库相关表

**入库单表 (inbound_order)**
```sql
CREATE TABLE inbound_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '入库单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
    inbound_date DATE NOT NULL COMMENT '入库日期',
    supplier_id BIGINT COMMENT '供应商ID',
    handler_id BIGINT NOT NULL COMMENT '经办人ID',
    checker_id BIGINT COMMENT '验收人ID',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0待审核 1已审核 2已拒绝 3已完成',
    audit_time DATETIME COMMENT '审核时间',
    auditor_id BIGINT COMMENT '审核人ID',
    audit_opinion TEXT COMMENT '审核意见',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_inbound_date (inbound_date),
    INDEX idx_status (status),
    INDEX idx_handler_id (handler_id),
    INDEX idx_supplier_id (supplier_id),
    CONSTRAINT fk_inbound_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id),
    CONSTRAINT fk_inbound_supplier FOREIGN KEY (supplier_id) REFERENCES supplier_info(id),
    CONSTRAINT fk_inbound_auditor FOREIGN KEY (auditor_id) REFERENCES sys_user(id),
    CONSTRAINT fk_inbound_checker FOREIGN KEY (checker_id) REFERENCES sys_user(id)
) COMMENT='入库单表';
```

**入库明细表 (inbound_detail)**
```sql
CREATE TABLE inbound_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    inbound_order_id BIGINT NOT NULL COMMENT '入库单ID',
    material_id BIGINT NOT NULL COMMENT '耗材ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(12,2) COMMENT '总价',
    batch_no VARCHAR(50) COMMENT '批次号',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '过期日期',
    supplier_batch_no VARCHAR(100) COMMENT '供应商批次号',
    quality_certificate VARCHAR(200) COMMENT '质检证书',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_inbound_order_id (inbound_order_id),
    INDEX idx_material_id (material_id),
    INDEX idx_batch_no (batch_no),
    CONSTRAINT fk_inbound_detail_order FOREIGN KEY (inbound_order_id) REFERENCES inbound_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_inbound_detail_material FOREIGN KEY (material_id) REFERENCES material_info(id)
) COMMENT='入库明细表';
```

#### 3.1.4 出库相关表

**出库单表 (outbound_order)**
```sql
CREATE TABLE outbound_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '出库单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '出库单号',
    outbound_date DATE COMMENT '出库日期',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    applicant_dept_id BIGINT COMMENT '申请人部门ID',
    handler_id BIGINT COMMENT '处理人ID',
    approver_id BIGINT COMMENT '审批人ID',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    usage_type TINYINT NOT NULL COMMENT '用途类型：1教学用 2科研用 3竞赛用 4其他',
    usage_detail TEXT NOT NULL COMMENT '用途详情（JSON格式）',
    expected_return_date DATE COMMENT '预计归还日期',
    actual_return_date DATE COMMENT '实际归还日期',
    status TINYINT DEFAULT 0 COMMENT '状态：0待审批 1已批准 2已拒绝 3待出库 4已出库 5已归还 6已损坏 7已丢失',
    approval_time DATETIME COMMENT '审批时间',
    approval_opinion TEXT COMMENT '审批意见',
    outbound_time DATETIME COMMENT '出库时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_outbound_date (outbound_date),
    INDEX idx_applicant_id (applicant_id),
    INDEX idx_status (status),
    INDEX idx_usage_type (usage_type),
    INDEX idx_expected_return_date (expected_return_date),
    CONSTRAINT fk_outbound_applicant FOREIGN KEY (applicant_id) REFERENCES sys_user(id),
    CONSTRAINT fk_outbound_applicant_dept FOREIGN KEY (applicant_dept_id) REFERENCES sys_department(id),
    CONSTRAINT fk_outbound_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id),
    CONSTRAINT fk_outbound_approver FOREIGN KEY (approver_id) REFERENCES sys_user(id)
) COMMENT='出库单表';
```

**出库明细表 (outbound_detail)**
```sql
CREATE TABLE outbound_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    outbound_order_id BIGINT NOT NULL COMMENT '出库单ID',
    material_id BIGINT NOT NULL COMMENT '耗材ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(12,2) COMMENT '总价',
    return_quantity INT DEFAULT 0 COMMENT '已归还数量',
    return_status TINYINT DEFAULT 0 COMMENT '归还状态：0未归还 1部分归还 2已归还 3已损坏 4已丢失',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_outbound_order_id (outbound_order_id),
    INDEX idx_material_id (material_id),
    INDEX idx_return_status (return_status),
    CONSTRAINT fk_outbound_detail_order FOREIGN KEY (outbound_order_id) REFERENCES outbound_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_outbound_detail_material FOREIGN KEY (material_id) REFERENCES material_info(id)
) COMMENT='出库明细表';
```

**归还记录表 (return_record)**
```sql
CREATE TABLE return_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '归还记录ID',
    outbound_detail_id BIGINT NOT NULL COMMENT '出库明细ID',
    return_date DATE NOT NULL COMMENT '归还日期',
    return_quantity INT NOT NULL COMMENT '归还数量',
    return_status TINYINT NOT NULL COMMENT '归还状态：1完好 2损坏 3丢失',
    damage_description TEXT COMMENT '损坏描述',
    handler_id BIGINT COMMENT '处理人ID',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_outbound_detail_id (outbound_detail_id),
    INDEX idx_return_date (return_date),
    INDEX idx_return_status (return_status),
    INDEX idx_handler_id (handler_id),
    CONSTRAINT fk_return_detail FOREIGN KEY (outbound_detail_id) REFERENCES outbound_detail(id) ON DELETE CASCADE,
    CONSTRAINT fk_return_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id)
) COMMENT='归还记录表';
```

#### 3.1.5 盘点相关表

**盘点单表 (inventory_check)**
```sql
CREATE TABLE inventory_check (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '盘点单ID',
    check_no VARCHAR(50) NOT NULL UNIQUE COMMENT '盘点单号',
    check_date DATE NOT NULL COMMENT '盘点日期',
    checker_id BIGINT NOT NULL COMMENT '盘点人ID',
    checker_dept_id BIGINT COMMENT '盘点人部门ID',
    check_type TINYINT DEFAULT 1 COMMENT '盘点类型：1全盘 2抽盘 3专项盘',
    check_scope TEXT COMMENT '盘点范围（JSON格式）',
    status TINYINT DEFAULT 0 COMMENT '状态：0进行中 1已完成 2已作废',
    total_items INT DEFAULT 0 COMMENT '盘点总项数',
    discrepancy_items INT DEFAULT 0 COMMENT '差异项数',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    complete_time DATETIME COMMENT '完成时间',
    INDEX idx_check_no (check_no),
    INDEX idx_check_date (check_date),
    INDEX idx_checker_id (checker_id),
    INDEX idx_status (status),
    CONSTRAINT fk_check_checker FOREIGN KEY (checker_id) REFERENCES sys_user(id),
    CONSTRAINT fk_check_checker_dept FOREIGN KEY (checker_dept_id) REFERENCES sys_department(id)
) COMMENT='盘点单表';
```

**盘点明细表 (inventory_check_detail)**
```sql
CREATE TABLE inventory_check_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    inventory_check_id BIGINT NOT NULL COMMENT '盘点单ID',
    material_id BIGINT NOT NULL COMMENT '耗材ID',
    system_quantity INT NOT NULL COMMENT '系统库存数量',
    actual_quantity INT NOT NULL COMMENT '实际盘点数量',
    diff_quantity INT NOT NULL COMMENT '差异数量',
    diff_amount DECIMAL(12,2) COMMENT '差异金额',
    check_status TINYINT DEFAULT 0 COMMENT '盘点状态：0待确认 1已确认 2已调整',
    adjust_quantity INT DEFAULT 0 COMMENT '调整数量',
    adjust_reason TEXT COMMENT '调整原因',
    adjust_time DATETIME COMMENT '调整时间',
    adjuster_id BIGINT COMMENT '调整人ID',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_inventory_check_id (inventory_check_id),
    INDEX idx_material_id (material_id),
    INDEX idx_check_status (check_status),
    INDEX idx_adjuster_id (adjuster_id),
    CONSTRAINT fk_check_detail_check FOREIGN KEY (inventory_check_id) REFERENCES inventory_check(id) ON DELETE CASCADE,
    CONSTRAINT fk_check_detail_material FOREIGN KEY (material_id) REFERENCES material_info(id),
    CONSTRAINT fk_check_detail_adjuster FOREIGN KEY (adjuster_id) REFERENCES sys_user(id)
) COMMENT='盘点明细表';
```

#### 3.1.6 系统日志表

**操作日志表 (sys_log)**
```sql
CREATE TABLE sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    real_name VARCHAR(50) COMMENT '真实姓名',
    operation VARCHAR(100) COMMENT '操作内容',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    result TEXT COMMENT '返回结果',
    ip VARCHAR(50) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    status TINYINT COMMENT '状态：1成功 0失败',
    error_msg TEXT COMMENT '错误信息',
    execute_time BIGINT COMMENT '执行时长（毫秒）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_operation (operation),
    INDEX idx_status (status),
    INDEX idx_ip (ip)
) COMMENT='操作日志表';
```

**系统配置表 (sys_config)**
```sql
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(50) DEFAULT 'text' COMMENT '配置类型',
    description VARCHAR(200) COMMENT '配置描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_config_key (config_key)
) COMMENT='系统配置表';
```

### 3.2 数据库索引设计
- 所有主键自动创建索引
- 外键字段创建索引
- 常用查询字段（如单号、日期、状态）创建索引
- 复合索引优化多条件查询
- 高频查询字段创建覆盖索引

### 3.3 数据库优化建议
- 使用MyBatis-Plus的分页插件
- 大屏数据使用Redis缓存
- 定期归档历史数据
- 使用数据库连接池（HikariCP）
- 启用慢查询日志监控
- 合理设置数据库参数（如innodb_buffer_pool_size）
- 定期进行数据库性能分析和优化
        
            

toolName: todo_write
            
status: success
          
Todos updated: 5 items
          
## 四、前后端模块划分

### 4.1 前端模块结构

```
frontend/
├── src/
│   ├── api/                      # API接口
│   │   ├── index.js             # Axios配置
│   │   ├── material.js          # 耗材相关接口
│   │   ├── inbound.js           # 入库相关接口
│   │   ├── outbound.js          # 出库相关接口
│   │   ├── inventory.js         # 库存相关接口
│   │   ├── dashboard.js         # 大屏数据接口
│   │   ├── statistics.js        # 统计报表接口
│   │   └── user.js              # 用户相关接口
│   │
│   ├── assets/                   # 静态资源
│   │   ├── images/              # 图片
│   │   └── styles/              # 样式文件
│   │
│   ├── components/               # 公共组件
│   │   ├── Layout/              # 布局组件
│   │   ├── Upload/              # 上传组件
│   │   ├── SearchForm/          # 搜索表单
│   │   └── Pagination/          # 分页组件
│   │
│   ├── router/                   # 路由配置
│   │   └── index.js
│   │
│   ├── stores/                   # Pinia状态管理
│   │   ├── user.js              # 用户状态
│   │   └── app.js               # 应用状态
│   │
│   ├── utils/                    # 工具函数
│   │   ├── request.js           # 请求封装
│   │   ├── auth.js              # 认证工具
│   │   ├── validate.js          # 表单验证
│   │   └── format.js            # 格式化工具
│   │
│   ├── views/                    # 页面组件
│   │   ├── login/               # 登录页
│   │   ├── material/            # 耗材管理
│   │   │   ├── list.vue         # 耗材列表
│   │   │   ├── category.vue     # 耗材分类
│   │   │   └── detail.vue       # 耗材详情
│   │   ├── inbound/             # 入库管理
│   │   │   ├── list.vue         # 入库单列表
│   │   │   ├── create.vue       # 创建入库单
│   │   │   ├── detail.vue       # 入库单详情
│   │   │   └── audit.vue        # 入库审核
│   │   ├── outbound/            # 出库管理
│   │   │   ├── list.vue         # 出库单列表
│   │   │   ├── apply.vue        # 领用申请
│   │   │   ├── detail.vue       # 出库单详情
│   │   │   ├── approve.vue      # 出库审批
│   │   │   └── return.vue       # 归还管理
│   │   ├── inventory/           # 库存管理
│   │   │   ├── list.vue         # 库存列表
│   │   │   ├── check.vue        # 库存盘点
│   │   │   ├── warning.vue      # 库存预警
│   │   │   └── transfer.vue      # 库存调拨
│   │   ├── dashboard/           # 数据大屏
│   │   │   └── index.vue        # 大屏展示
│   │   ├── statistics/          # 统计报表
│   │   │   ├── inbound.vue      # 入库统计
│   │   │   ├── outbound.vue     # 出库统计
│   │   │   └── inventory.vue    # 库存统计
│   │   └── system/              # 系统管理
│   │       ├── user.vue         # 用户管理
│   │       ├── role.vue         # 角色管理
│   │       └── log.vue          # 操作日志
│   │
│   ├── App.vue                   # 根组件
│   └── main.js                   # 入口文件
│
├── public/                       # 公共资源
├── package.json
└── vite.config.js
```

### 4.2 后端模块结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── material/
│   │   │           └── system/
│   │   │               ├── MaterialSystemApplication.java  # 启动类
│   │   │               │
│   │   │               ├── config/                          # 配置类
│   │   │               │   ├── MybatisPlusConfig.java
│   │   │               │   ├── RedisConfig.java
│   │   │               │   ├── SwaggerConfig.java
│   │   │               │   └── SecurityConfig.java
│   │   │               │
│   │   │               ├── common/                          # 公共模块
│   │   │               │   ├── Result.java                 # 统一返回结果
│   │   │               │   ├── ResultCode.java             # 返回码
│   │   │               │   ├── PageResult.java             # 分页结果
│   │   │               │   └── BusinessException.java      # 业务异常
│   │   │               │
│   │   │               ├── entity/                          # 实体类
│   │   │               │   ├── SysUser.java
│   │   │               │   ├── SysRole.java
│   │   │               │   ├── SysPermission.java
│   │   │               │   ├── MaterialCategory.java
│   │   │               │   ├── MaterialInfo.java
│   │   │               │   ├── MaterialInventory.java
│   │   │               │   ├── InboundOrder.java
│   │   │               │   ├── InboundDetail.java
│   │   │               │   ├── OutboundOrder.java
│   │   │               │   ├── OutboundDetail.java
│   │   │               │   ├── ReturnRecord.java
│   │   │               │   ├── InventoryCheck.java
│   │   │               │   ├── InventoryCheckDetail.java
│   │   │               │   └── SysLog.java
│   │   │               │
│   │   │               ├── dto/                             # 数据传输对象
│   │   │               │   ├── material/
│   │   │               │   │   ├── MaterialDTO.java
│   │   │               │   │   └── MaterialQueryDTO.java
│   │   │               │   ├── inbound/
│   │   │               │   │   ├── InboundOrderDTO.java
│   │   │               │   │   ├── InboundDetailDTO.java
│   │   │               │   │   └── InboundQueryDTO.java
│   │   │               │   ├── outbound/
│   │   │               │   │   ├── OutboundOrderDTO.java
│   │   │               │   │   ├── OutboundDetailDTO.java
│   │   │               │   │   └── OutboundQueryDTO.java
│   │   │               │   ├── inventory/
│   │   │               │   │   ├── InventoryDTO.java
│   │   │               │   │   └── InventoryCheckDTO.java
│   │   │               │   └── dashboard/
│   │   │               │       └── DashboardDataDTO.java
│   │   │               │
│   │   │               ├── vo/                              # 视图对象
│   │   │               │   ├── material/
│   │   │               │   ├── inbound/
│   │   │               │   ├── outbound/
│   │   │               │   └── dashboard/
│   │   │               │
│   │   │               ├── mapper/                          # Mapper接口
│   │   │               │   ├── SysUserMapper.java
│   │   │               │   ├── SysRoleMapper.java
│   │   │               │   ├── MaterialCategoryMapper.java
│   │   │               │   ├── MaterialInfoMapper.java
│   │   │               │   ├── MaterialInventoryMapper.java
│   │   │               │   ├── InboundOrderMapper.java
│   │   │               │   ├── InboundDetailMapper.java
│   │   │               │   ├── OutboundOrderMapper.java
│   │   │               │   ├── OutboundDetailMapper.java
│   │   │               │   ├── ReturnRecordMapper.java
│   │   │               │   ├── InventoryCheckMapper.java
│   │   │               │   ├── InventoryCheckDetailMapper.java
│   │   │               │   └── SysLogMapper.java
│   │   │               │
│   │   │               ├── service/                         # Service层
│   │   │               │   ├── SysUserService.java
│   │   │               │   ├── SysRoleService.java
│   │   │               │   ├── MaterialCategoryService.java
│   │   │               │   ├── MaterialInfoService.java
│   │   │               │   ├── MaterialInventoryService.java
│   │   │               │   ├── InboundOrderService.java
│   │   │               │   ├── OutboundOrderService.java
│   │   │               │   ├── ReturnRecordService.java
│   │   │               │   ├── InventoryCheckService.java
│   │   │               │   ├── DashboardService.java
│   │   │               │   └── StatisticsService.java
│   │   │               │
│   │   │               ├── service/impl/                    # Service实现类
│   │   │               │   ├── SysUserServiceImpl.java
│   │   │               │   ├── MaterialCategoryServiceImpl.java
│   │   │               │   ├── MaterialInfoServiceImpl.java
│   │   │               │   ├── MaterialInventoryServiceImpl.java
│   │   │               │   ├── InboundOrderServiceImpl.java
│   │   │               │   ├── OutboundOrderServiceImpl.java
│   │   │               │   ├── ReturnRecordServiceImpl.java
│   │   │               │   ├── InventoryCheckServiceImpl.java
│   │   │               │   ├── DashboardServiceImpl.java
│   │   │               │   └── StatisticsServiceImpl.java
│   │   │               │
│   │   │               ├── controller/                      # Controller层
│   │   │               │   ├── SysUserController.java
│   │   │               │   ├── MaterialController.java
│   │   │               │   ├── InboundController.java
│   │   │               │   ├── OutboundController.java
│   │   │               │   ├── InventoryController.java
│   │   │               │   ├── DashboardController.java
│   │   │               │   ├── StatisticsController.java
│   │   │               │   └── FileController.java
│   │   │               │
│   │   │               ├── aspect/                          # 切面
│   │   │               │   ├── LogAspect.java              # 日志切面
│   │   │               │   └── PermissionAspect.java       # 权限切面
│   │   │               │
│   │   │               ├── util/                            # 工具类
│   │   │               │   ├── JwtUtil.java
│   │   │               │   ├── RedisUtil.java
│   │   │               │   ├── DateUtil.java
│   │   │               │   └── FileUtil.java
│   │   │               │
│   │   │               └── annotation/                     # 自定义注解
│   │   │                   ├── Log.java
│   │   │                   └── RequirePermission.java
│   │   │
│   │   └── resources/
│   │       ├── mapper/             # MyBatis XML映射文件
│   │       ├── application.yml     # 配置文件
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   │
└── pom.xml
```

### 4.3 前后端接口规范

#### 4.3.1 统一响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1234567890
}
```

#### 4.3.2 RESTful API设计规范
- GET: 查询数据
- POST: 创建数据
- PUT: 更新数据
- DELETE: 删除数据

#### 4.3.3 主要接口列表

**认证授权接口**
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/info` - 获取用户信息
- `POST /api/auth/refresh` - 刷新token

**用户管理接口**
- `GET /api/user/list` - 用户列表（支持分页、搜索）
- `GET /api/user/{id}` - 获取用户详情
- `POST /api/user` - 新增用户
- `PUT /api/user/{id}` - 更新用户信息
- `DELETE /api/user/{id}` - 删除用户
- `PUT /api/user/{id}/status` - 更新用户状态
- `PUT /api/user/{id}/password` - 修改用户密码

**角色管理接口**
- `GET /api/role/list` - 角色列表
- `GET /api/role/{id}` - 获取角色详情
- `POST /api/role` - 新增角色
- `PUT /api/role/{id}` - 更新角色
- `DELETE /api/role/{id}` - 删除角色
- `GET /api/role/{id}/permissions` - 获取角色权限
- `PUT /api/role/{id}/permissions` - 更新角色权限

**部门管理接口**
- `GET /api/department/list` - 部门列表（树形结构）
- `GET /api/department/{id}` - 获取部门详情
- `POST /api/department` - 新增部门
- `PUT /api/department/{id}` - 更新部门
- `DELETE /api/department/{id}` - 删除部门

**权限管理接口**
- `GET /api/permission/list` - 权限列表
- `GET /api/permission/tree` - 权限树形结构
- `POST /api/permission` - 新增权限
- `PUT /api/permission/{id}` - 更新权限
- `DELETE /api/permission/{id}` - 删除权限

**耗材分类接口**
- `GET /api/category/list` - 分类列表（树形结构）
- `GET /api/category/{id}` - 获取分类详情
- `POST /api/category` - 新增分类
- `PUT /api/category/{id}` - 更新分类
- `DELETE /api/category/{id}` - 删除分类

**耗材管理接口**
- `GET /api/material/list` - 耗材列表（支持分页、搜索、筛选）
- `GET /api/material/{id}` - 获取耗材详情
- `POST /api/material` - 新增耗材
- `PUT /api/material/{id}` - 更新耗材
- `DELETE /api/material/{id}` - 删除耗材
- `PUT /api/material/{id}/status` - 更新耗材状态
- `POST /api/material/batch` - 批量操作耗材
- `GET /api/material/export` - 导出耗材数据
- `POST /api/material/import` - 导入耗材数据
- `GET /api/material/generate-code` - 生成耗材编码
- `GET /api/material/{id}/qr-code` - 获取耗材二维码

**供应商管理接口**
- `GET /api/supplier/list` - 供应商列表
- `GET /api/supplier/{id}` - 获取供应商详情
- `POST /api/supplier` - 新增供应商
- `PUT /api/supplier/{id}` - 更新供应商
- `DELETE /api/supplier/{id}` - 删除供应商
- `PUT /api/supplier/{id}/status` - 更新供应商状态

**库存管理接口**
- `GET /api/inventory/list` - 库存列表（支持分页、搜索、筛选）
- `GET /api/inventory/{id}` - 获取库存详情
- `GET /api/inventory/warning` - 库存预警列表
- `GET /api/inventory/low-stock` - 低库存列表
- `GET /api/inventory/expired` - 临期耗材列表
- `PUT /api/inventory/{id}/adjust` - 库存调整
- `GET /api/inventory/turnover` - 库存周转率统计
- `GET /api/inventory/value` - 库存价值统计

**入库管理接口**
- `GET /api/inbound/list` - 入库单列表（支持分页、搜索、筛选）
- `GET /api/inbound/{id}` - 获取入库单详情
- `POST /api/inbound` - 创建入库单
- `PUT /api/inbound/{id}` - 更新入库单
- `PUT /api/inbound/{id}/audit` - 审核入库单
- `PUT /api/inbound/{id}/reject` - 拒绝入库单
- `DELETE /api/inbound/{id}` - 删除入库单
- `GET /api/inbound/export` - 导出入库单数据
- `GET /api/inbound/statistics` - 入库统计

**入库明细接口**
- `GET /api/inbound-detail/{id}` - 获取入库明细详情
- `POST /api/inbound-detail` - 新增入库明细
- `PUT /api/inbound-detail/{id}` - 更新入库明细
- `DELETE /api/inbound-detail/{id}` - 删除入库明细

**出库管理接口**
- `GET /api/outbound/list` - 出库单列表（支持分页、搜索、筛选）
- `GET /api/outbound/{id}` - 获取出库单详情
- `POST /api/outbound/apply` - 提交领用申请
- `PUT /api/outbound/{id}/approve` - 审批出库单
- `PUT /api/outbound/{id}/reject` - 拒绝出库单
- `PUT /api/outbound/{id}/out` - 执行出库
- `PUT /api/outbound/{id}/return` - 更新归还状态
- `GET /api/outbound/export` - 导出出库单数据
- `GET /api/outbound/statistics` - 出库统计
- `GET /api/outbound/pending-approval` - 待审批出库单列表

**出库明细接口**
- `GET /api/outbound-detail/{id}` - 获取出库明细详情
- `POST /api/outbound-detail` - 新增出库明细
- `PUT /api/outbound-detail/{id}` - 更新出库明细

**归还管理接口**
- `GET /api/return/list` - 归还记录列表
- `POST /api/return` - 新增归还记录
- `PUT /api/return/{id}` - 更新归还记录
- `GET /api/return/pending` - 待归还列表
- `GET /api/return/overdue` - 逾期未归还列表

**盘点管理接口**
- `GET /api/check/list` - 盘点单列表
- `GET /api/check/{id}` - 获取盘点单详情
- `POST /api/check` - 创建盘点单
- `PUT /api/check/{id}/start` - 开始盘点
- `PUT /api/check/{id}/complete` - 完成盘点
- `PUT /api/check/{id}/cancel` - 作废盘点单
- `GET /api/check/export` - 导出盘点数据
- `POST /api/check/detail` - 新增盘点明细
- `PUT /api/check/detail/{id}` - 更新盘点明细

**大屏数据接口**
- `GET /api/dashboard/overview` - 概览数据
- `GET /api/dashboard/top-used` - 最常用耗材Top5
- `GET /api/dashboard/top-stock` - 库存最多耗材Top5
- `GET /api/dashboard/low-stock` - 库存最少耗材Top5
- `GET /api/dashboard/usage-trend` - 出库趋势数据
- `GET /api/dashboard/category-ratio` - 耗材分类占比
- `GET /api/dashboard/usage-type` - 用途分类统计
- `GET /api/dashboard/warning-list` - 库存预警列表
- `GET /api/dashboard/in-out-comparison` - 入库出库对比

**统计报表接口**
- `GET /api/statistics/inbound` - 入库统计
- `GET /api/statistics/outbound` - 出库统计
- `GET /api/statistics/inventory` - 库存统计
- `GET /api/statistics/teaching` - 教学耗材使用统计
- `GET /api/statistics/research` - 科研耗材使用统计
- `GET /api/statistics/competition` - 竞赛耗材使用统计
- `GET /api/statistics/user-usage` - 用户使用统计
- `GET /api/statistics/export/{type}` - 导出统计报表

**系统配置接口**
- `GET /api/config/list` - 获取配置列表
- `GET /api/config/{key}` - 获取配置值
- `POST /api/config` - 新增配置
- `PUT /api/config/{key}` - 更新配置
- `DELETE /api/config/{key}` - 删除配置

**文件管理接口**
- `POST /api/file/upload` - 文件上传
- `GET /api/file/{id}` - 获取文件
- `DELETE /api/file/{id}` - 删除文件
- `GET /api/file/download/{id}` - 文件下载

**操作日志接口**
- `GET /api/log/list` - 操作日志列表
- `GET /api/log/{id}` - 获取日志详情
- `DELETE /api/log/clear` - 清理日志
        
            

toolName: todo_write
            
status: success
          
Todos updated: 5 items
          
## 五、开发计划和测试策略

### 5.1 开发阶段划分与优化建议
注意：正式开发之前务必先对开发环境和测试环境进行测试，确保正常后再进入正式开发阶段。
#### 5.1.1 开发阶段时间评估与优化

**原始计划时间评估：**

| 阶段 | 任务 | 原计划天数 | 建议天数 | 调整原因 |
|------|------|-----------|---------|---------|
| 阶段一 | 项目初始化和环境搭建 | 2 | 3 | 需要更多时间配置开发工具和基础设施 |
| 阶段二 | 基础管理模块开发 | 5 | 6 | 包含用户、角色、权限等核心功能，复杂度较高 |
| 阶段三 | 耗材管理模块开发 | 5 | 5 | 时间合理 |
| 阶段四 | 入库管理模块开发 | 5 | 6 | 涉及审批流程和事务控制，需要更多测试 |
| 阶段五 | 出库管理模块开发 | 7 | 8 | 最复杂的模块，包含申请、审批、出库、归还全流程 |
| 阶段六 | 库存盘点模块开发 | 4 | 4 | 时间合理 |
| 阶段七 | 数据大屏模块开发 | 5 | 6 | 涉及大量数据统计和可视化，需要更多优化时间 |
| 阶段八 | 统计报表模块开发 | 4 | 5 | 复杂的统计查询和数据导出，需要更多测试 |
| 阶段九 | 系统优化和集成测试 | 3 | 5 | 需要更多时间进行压力测试和性能优化 |
| 阶段十 | 部署上线 | 2 | 3 | 需要包括环境配置和问题排查时间 |

**调整后总计：约51个工作日（约10.2周）**

#### 5.1.2 各阶段详细分析与优化

---

#### 阶段一：项目初始化和环境搭建（建议3天，原计划2天）

**时间调整原因：**
- Spring Boot 3.2配置需要安装JDK 17和更新开发工具
- Vue 3 + TypeScript配置相对复杂，需要更多初始化时间
- Docker环境搭建需要额外时间
- 需要搭建Git仓库和CI/CD基础配置

**后端任务（2天）：**
- 创建Spring Boot项目，配置pom.xml依赖（含Hutool、EasyExcel等工具库）
- 配置application.yml（数据库、Redis、Swagger、文件存储等）
- 创建基础目录结构，
- 配置MyBatis-Plus（代码生成器配置）
- 配置Spring Security + JWT（SecurityConfig详细配置）
- 配置SpringDoc OpenAPI 2.3+接口文档
- 配置Docker支持（Dockerfile编写）
- 配置日志系统（Logback异步日志）
- 配置异常处理和统一返回格式

**前端任务（1.5天）：**
- 创建Vue 3 + TypeScript项目（使用Vite）
- 配置package.json依赖（添加@vueuse/core、dayjs、lodash-es等）
- 配置路由（Vue Router 4.2+）
- 配置状态管理（Pinia）
- 配置Axios请求封装（拦截器、错误处理）
- 配置Element Plus主题和自动导入
- 配置ECharts和封装图表组件
- 配置TypeScript类型定义
- 配置ESLint和Prettier代码规范
- 配置环境变量（.env文件）

**测试任务（0.5天）：**
- 后端启动成功，Swagger可访问
- 前端启动成功，页面可访问
- 前后端连通性测试
- Docker容器测试

**验收标准：**
- 开发环境搭建完成，前后端可独立运行
- 接口文档可正常访问
- 代码规范配置生效
- Docker镜像构建成功

---

#### 阶段二：基础管理模块开发（建议6天，原计划5天）

**时间调整原因：**
- 用户管理包含密码加密、JWT token生成等安全相关代码
- 角色权限系统需要实现RBAC模型，复杂度较高
- 前端布局组件需要完善的响应式设计
- 需要更多时间进行安全测试

**后端开发任务（4天）：**
1. **用户管理模块**（1.5天）
   - 用户实体类、Mapper、Service、Controller
   - 用户登录、注册、修改密码接口
   - JWT token生成和验证（使用jjwt 0.12+）
   - 密码BCrypt加密存储
   - 用户CRUD接口
   - 用户状态管理
   - 登录日志记录

2. **角色权限模块**（1.5天）
   - 角色实体类、Mapper、Service、Controller
   - 权限实体类、Mapper、Service、Controller
   - 角色权限关联管理
   - 权限拦截器和注解（@RequirePermission）
   - 菜单权限动态加载
   - 数据权限控制（部门数据隔离）

3. **耗材分类模块**（1天）
   - 分类实体类、Mapper、Service、Controller
   - 分类树形结构查询（递归查询）
   - 分类CRUD接口
   - 分类编码自动生成

**前端开发任务（2天）：**
1. **登录页面**（0.5天）
   - 登录表单（用户名/密码、验证码）
   - 登录验证
   - Token存储和刷新机制
   - 记住密码功能

2. **布局组件**（1天）
   - 侧边栏菜单（动态菜单、可折叠）
   - 顶部导航（用户信息、消息通知、退出登录）
   - 主内容区域（面包屑导航、标签页）
   - 响应式设计（移动端适配）

3. **用户管理页面**（0.5天）
   - 用户列表（分页、搜索、筛选）
   - 用户新增/编辑/删除
   - 角色分配（下拉框选择）
   - 密码重置
   - 用户状态切换

**测试任务（1天）：**
- 用户登录功能测试（正常登录、错误密码、锁定账号）
- 角色权限控制测试（不同角色权限验证）
- 耗材分类CRUD测试（树形结构测试）
- 接口单元测试（覆盖率>80%）
- 前端页面功能测试
- 前后端联调测试
- 安全测试（SQL注入、XSS防护）

**验收标准：**
- 用户能正常登录登出
- 角色权限控制生效（菜单、按钮、数据权限）
- 耗材分类管理功能完整
- 所有测试用例通过
- 接口文档完整

---

#### 阶段三：耗材管理模块开发（建议5天，原计划5天）

**时间评估：合理**
- 功能相对独立，复杂度适中
- 前后端开发时间分配合理

**后端开发任务（3天）：**
1. **耗材信息管理**（1.5天）
   - 耗材实体类、Mapper、Service、Controller
   - 耗材CRUD接口
   - 耗材分页查询接口
   - 耗材状态管理接口
   - 耗材编码自动生成
   - 耗材图片上传和管理

2. **库存管理**（1天）
   - 库存实体类、Mapper、Service、Controller
   - 库存查询接口（根据耗材ID查询库存）
   - 库存预警接口（低库存、临期耗材）
   - 库存更新接口（入库/出库时更新）
   - 库存历史变动查询

3. **文件上传**（0.5天）
   - 文件上传接口（支持分片上传）
   - 图片压缩和缩略图生成
   - 文件存储管理（本地/OSS）
   - 文件删除接口

**前端开发任务（2天）：**
1. **耗材列表页面**（1天）
   - 耗材表格展示（分页、排序）
   - 高级搜索筛选（多条件组合查询）
   - 新增/编辑耗材（表单验证）
   - 批量操作（批量删除、批量导出）
   - 耗材图片上传预览

2. **耗材详情页面**（0.5天）
   - 耗材基本信息展示
   - 库存信息展示
   - 操作历史（入库记录、出库记录）
   - 二维码生成和打印

3. **库存列表页面**（0.5天）
   - 库存表格展示
   - 库存预警列表（红色高亮）
   - 库存统计卡片（总库存、总价值）
   - 库存快速查询

**测试任务：**
- 耗材CRUD功能测试
- 库存查询功能测试
- 文件上传功能测试（大文件、异常文件）
- 库存预警功能测试
- 接口单元测试
- 前端页面功能测试
- 前后端联调测试

**验收标准：**
- 耗材信息管理功能完整
- 库存查询准确
- 库存预警正常触发
- 所有测试用例通过

---

#### 阶段四：入库管理模块开发（建议6天，原计划5天）

**时间调整原因：**
- 入库审核涉及审批流程，需要实现工作流逻辑
- 事务控制复杂度较高，需要充分测试
- 需要实现入库单打印功能

**后端开发任务（3.5天）：**
1. **入库单管理**（1.5天）
   - 入库单实体类、Mapper、Service、Controller
   - 入库单创建接口（支持批量入库）
   - 入库单查询接口（多条件筛选）
   - 入库单详情接口
   - 入库单号自动生成（流水号）
   - 入库单状态管理

2. **入库明细管理**（1天）
   - 入库明细实体类、Mapper、Service、Controller
   - 入库明细CRUD接口
   - 批量添加入库明细
   - 入库明细校验（耗材是否存在、数量是否合理）

3. **入库审核**（1天）
   - 入库审核接口
   - 审核通过后自动更新库存（事务控制）
   - 审核拒绝流程
   - 库存变动记录
   - 审核日志记录
   - 异常回滚处理

**前端开发任务（2天）：**
1. **入库单列表页面**（0.5天）
   - 入库单表格展示（分页、排序）
   - 状态筛选（待审核、已审核、已拒绝、已完成）
   - 快速搜索（单号、供应商、经办人）
   - 批量操作（批量审核、批量导出）

2. **创建入库单页面**（1天）
   - 入库单基本信息表单（日期、供应商、备注）
   - 耗材明细添加（支持批量、扫描条码）
   - 动态添加/删除明细行
   - 供应商选择（下拉搜索）
   - 表单验证（必填项、数量校验）
   - 提交审核

3. **入库审核页面**（0.5天）
   - 待审核入库单列表
   - 审核详情查看
   - 审核操作（通过/拒绝）
   - 审核意见填写
   - 入库单打印（PDF生成）

**测试任务（0.5天）：**
- 入库单创建功能测试
- 入库单审核功能测试（通过、拒绝）
- 库存自动更新测试（数量准确性）
- 事务回滚测试（审核失败）
- 并发入库测试
- 接口单元测试
- 前端页面功能测试
- 前后端联调测试
- 边界条件测试（数量为负、超大数量）

**验收标准：**
- 入库单创建成功
- 审核流程正常（支持多级审批）
- 库存自动更新准确
- 事务控制有效（异常回滚）
- 入库单打印正常
- 所有测试用例通过

---

#### 阶段五：出库管理模块开发（建议8天，原计划7天）

**时间调整原因：**
- **这是系统最复杂的模块**，包含申请、审批、出库、归还全流程
- 用途分类详细，需要实现不同用途的业务逻辑
- 归还管理涉及库存回增，逻辑复杂
- 需要实现借用到期提醒等定时任务

**后端开发任务（4.5天）：**
1. **出库单管理**（1天）
   - 出库单实体类、Mapper、Service、Controller
   - 领用申请接口
   - 出库单查询接口（多条件筛选）
   - 出库单详情接口
   - 出库单号自动生成
   - 附件上传和管理

2. **出库明细管理**（0.5天）
   - 出库明细实体类、Mapper、Service、Controller
   - 出库明细CRUD接口
   - 出库明细校验（库存是否充足）

3. **出库审批**（1天）
   - 出库审批接口
   - 审批通过后生成出库单
   - 审批拒绝流程
   - 多级审批支持（根据金额和数量）
   - 审批流程可视化

4. **出库执行**（1天）
   - 出库执行接口
   - 库存扣减（事务控制）
   - 可用库存更新
   - 出库单状态更新
   - 扫码出库支持

5. **归还管理**（0.5天）
   - 归还记录实体类、Mapper、Service、Controller
   - 归还登记接口
   - 归还状态更新（完好、损坏、丢失）
   - 库存回增（归还完好时）
   - 损坏赔偿记录

6. **借用管理**（0.5天）
   - 借用期限设置
   - 到期提醒（定时任务）
   - 逾期未归还查询
   - 借用续借申请

**前端开发任务（3.5天）：**
1. **出库单列表页面**（0.5天）
   - 出库单表格展示（分页、排序）
   - 状态筛选（待审批、已批准、已拒绝、待出库、已出库、已归还）
   - 用途分类筛选
   - 快速搜索（单号、申请人、课程/项目名称）
   - 批量操作（批量审批、批量导出）

2. **领用申请页面**（1.5天）
   - 申请人信息自动填充
   - 耗材选择和数量填写（库存提示）
   - 用途分类选择（教学/科研/竞赛/其他）
   - 动态用途详情表单（根据用途类型显示不同字段）
   - 预计归还日期（必填/可选根据耗材类型）
   - 附件上传（支持多种格式）
   - 表单验证（完整性、合法性）
   - 提交申请

3. **出库审批页面**（0.5天）
   - 待审批出库单列表
   - 审批详情查看（包括附件预览）
   - 审批操作（批准/拒绝）
   - 审批意见填写
   - 审批记录查看

4. **出库执行页面**（0.5天）
   - 待出库列表
   - 出库操作（扫码/手动）
   - 库存实时显示
   - 出库单打印

5. **归还管理页面**（0.5天）
   - 待归还列表
   - 归还登记
   - 归还状态选择（完好/损坏/丢失）
   - 损坏描述填写
   - 归还记录查看

**测试任务（1天）：**
- 领用申请功能测试（四种用途类型）
- 出库审批功能测试（多级审批）
- 出库执行功能测试（扫码/手动）
- 归还管理功能测试（三种归还状态）
- 库存自动扣减测试（准确性、并发）
- 用途信息验证测试（字段完整性）
- 借用到期提醒测试（定时任务）
- 接口单元测试（覆盖率>80%）
- 前端页面功能测试
- 前后端联调测试
- 边界条件测试（库存不足、数量为负）
- 异常情况测试（网络异常、并发出库）

**验收标准：**
- 领用申请流程完整（四种用途类型）
- 审批流程正常（支持多级审批）
- 出库执行准确（库存扣减正确）
- 归还管理功能完整（库存回增正确）
- 库存扣减准确（并发安全）
- 用途信息记录完整
- 所有测试用例通过
---

#### 阶段六：库存盘点模块开发（建议4天，原计划4天）

**时间评估：合理**
- 功能相对独立，复杂度适中
- 前后端开发时间分配合理

**后端开发任务（2天）：**
1. **盘点单管理**（1天）
   - 盘点单实体类、Mapper、Service、Controller
   - 创建盘点单接口（全盘/抽盘/专项盘）
   - 盘点单查询接口
   - 完成盘点接口
   - 盘点单状态管理

2. **盘点明细管理**（1天）
   - 盘点明细实体类、Mapper、Service、Controller
   - 盘点明细批量自动生成
   - 盘点明细录入接口
   - 盘盈盘亏计算
   - 库存调整接口（事务控制）
   - 盘点报告生成

**前端开发任务（2天）：**
1. **盘点单列表页面**（0.5天）
   - 盘点单表格展示（分页、排序）
   - 状态筛选（进行中、已完成、已作废）
   - 快速搜索（盘点单号、盘点人、日期）
   - 盘点进度显示

2. **创建盘点单页面**（1天）
   - 选择盘点范围（全部/部分耗材/按分类）
   - 生成盘点明细（显示系统库存）
   - 录入实际库存（支持扫码、批量导入）
   - 自动计算盘盈盘亏（实时计算）
   - 差异说明填写
   - 提交盘点

3. **盘点报告页面**（0.5天）
   - 盘点结果展示（盘盈/盘亏统计）
   - 盘盈盘亏明细列表
   - 导出盘点报告（Excel/PDF）
   - 盘点差异确认和调整

**测试任务：**
- 盘点单创建功能测试（三种盘点类型）
- 盘点明细录入测试（手动、扫码、导入）
- 盘盈盘亏计算测试（准确性）
- 库存调整测试（事务控制）
- 盘点报告生成测试（格式正确性）
- 接口单元测试
- 前端页面功能测试
- 前后端联调测试

**验收标准：**
- 盘点流程完整
- 盘盈盘亏计算准确
- 库存调整正确（事务有效）
- 盘点报告完整
- 所有测试用例通过

---

#### 阶段七：数据大屏模块开发（建议6天，原计划5天）

**时间调整原因：**
- 涉及大量数据统计查询，需要优化SQL性能
- 图表组件需要详细配置和优化
- 数据缓存策略需要充分测试
- 响应式适配需要更多时间

**后端开发任务（3.5天）：**
1. **大屏数据接口开发**（2天）
   - 概览数据接口（今日入库/出库数量、金额等）
   - 最常用耗材接口（按出库频次排序Top5，复杂SQL）
   - 库存最多耗材接口（按库存数量排序Top5）
   - 库存最少耗材接口（按库存数量排序Top5）
   - 图表数据接口（分类占比、出库趋势、用途分类）
   - 时间范围查询（近7天/30天/90天）
   - SQL性能优化（使用索引、避免全表扫描）

2. **数据缓存优化**（1天）
   - Redis缓存大屏数据（设置合理过期时间）
   - 定时刷新缓存（使用Spring Scheduled）
   - 缓存预热（系统启动时加载热点数据）
   - 缓存穿透防护（布隆过滤器）
   - 缓存雪崩防护（设置随机过期时间）

3. **接口性能优化**（0.5天）
   - 接口响应时间优化（<1秒）
   - 并发查询优化（避免慢查询）
   - 数据压缩（减少传输数据量）

**前端开发任务（2.5天）：**
1. **大屏页面布局**（0.5天）
   - 全屏布局设计（1080P/4K适配）
   - 响应式适配（不同分辨率）
   - 自适应网格布局
   - 主题切换（深色/浅色模式）

2. **数据展示组件**（1.5天）
   - 概览卡片（今日入库/出库、库存总价值等）
   - Top5列表（最常用、库存最多、库存最少）
   - 图表组件封装（饼图、折线图、柱状图、雷达图）
   - 库存预警列表（实时滚动）
   - 数据加载动画
   - 数字滚动动画

3. **自动刷新和交互**（0.5天）
   - 定时刷新数据（可配置刷新间隔）
   - 手动刷新按钮
   - 数据更新提示
   - 时间范围切换（近7天/30天/90天）
   - 页面性能优化（懒加载、虚拟滚动）

**测试任务（0.5天）：**
- 大屏数据准确性测试（与数据库对比）
- 数据实时性测试（缓存刷新时间）
- 图表展示测试（数据正确、样式美观）
- 自动刷新功能测试（手动/自动切换）
- 接口性能测试（响应时间<1秒）
- 并发访问测试（100+用户同时访问）
- 前端页面性能测试（FPS、内存占用）
- 前后端联调测试
- 浏览器兼容性测试

**验收标准：**
- 数据展示准确（数据一致性）
- 图表渲染正常（样式正确、交互流畅）
- 自动刷新有效（数据实时更新）
- 页面响应流畅（60FPS）
- 接口性能达标（响应时间<1秒）
- 所有测试用例通过

---

#### 阶段八：统计报表模块开发（建议5天，原计划4天）

**时间调整原因：**
- 统计查询复杂，需要优化SQL性能
- Excel导出功能需要更多测试（大数据量）
- 自定义报表功能复杂度较高
- 需要实现定时生成和推送功能

**后端开发任务（3天）：**
1. **入库统计接口**（0.5天）
   - 按时间统计（日/周/月/年）
   - 按分类统计（树形结构）
   - 按供应商统计
   - 按经办人统计
   - 趋势分析接口

2. **出库统计接口**（1天）
   - 按时间统计（多维度）
   - 按用途分类统计（教学/科研/竞赛/其他）
   - 按课程统计（教学用，关联课程信息）
   - 按项目统计（科研用，关联项目信息）
   - 按竞赛统计（竞赛用，关联竞赛信息）
   - 按领用人/部门统计
   - 趋势分析接口
   - 对比分析接口（同比/环比）

3. **库存统计接口**（0.5天）
   - 库存周转率分析
   - 库存价值分析
   - 滞销耗材分析（长期未出库）
   - 库存结构分析
   - 库存预警统计

4. **数据导出接口**（1天）
   - 入库数据导出（Excel格式，使用EasyExcel）
   - 出库数据导出（Excel格式）
   - 库存数据导出（Excel格式）
   - 大数据量导出优化（分批次导出）
   - 导出进度查询
   - 导出历史记录

**前端开发任务（2天）：**
1. **入库统计页面**（0.5天）
   - 统计条件筛选（日期范围、分类、供应商）
   - 统计结果展示（表格、卡片）
   - 图表展示（柱状图、折线图、饼图）
   - 导出Excel（异步导出，显示进度）

2. **出库统计页面**（0.5天）
   - 统计条件筛选（日期范围、用途类型）
   - 统计结果展示（按用途分类）
   - 图表展示（堆叠柱状图、扇形图）
   - 导出Excel

3. **库存统计页面**（0.5天）
   - 库存周转率展示（表格、趋势图）
   - 库存价值分析（卡片、饼图）
   - 滞销耗材列表（表格、可操作）
   - 导出Excel

4. **综合报表页面**（0.5天）
   - 自定义报表生成器（拖拽式）
   - 报表模板管理
   - 报表定时生成配置
   - 报表推送设置（邮件、消息）
   - 报表历史记录

**测试任务（1天）：**
- 统计数据准确性测试（与数据库对比）
- 统计条件筛选测试（多条件组合）
- 数据导出功能测试（不同格式、不同数据量）
- 图表展示测试（数据正确、样式美观）
- 大数据量性能测试（万级数据）
- 并发导出测试（多个用户同时导出）
- 接口性能测试（响应时间<2秒）
- 前端页面功能测试
- 前后端联调测试

**验收标准：**
- 统计数据准确（数据一致性）
- 筛选功能正常（多条件组合查询）
- 导出功能完整（格式正确、数据完整）
- 图表展示正确（数据可视化清晰）
- 大数据量导出性能达标（<30秒）
- 所有测试用例通过

---

#### 阶段九：系统优化和集成测试（建议5天，原计划3天）

**时间调整原因：**
- 需要充分的时间进行代码审查和重构
- 性能优化需要多次测试和调优
- 需要进行压力测试和安全测试
- 需要进行多轮Bug修复

**优化任务（3天）：**
1. **代码审查和重构**（1天）
   - 代码规范检查（SonarQube）
   - 代码重构（消除重复代码、提取公共方法）
   - 注释完善（关键方法添加注释）
   - 异常处理完善（统一异常处理）
   - 日志优化（关键操作记录日志）

2. **性能优化**（1天）
   - 数据库SQL优化（慢查询分析、索引优化）
   - 接口响应时间优化（减少不必要的查询）
   - 前端性能优化（懒加载、虚拟滚动、代码分割）
   - 缓存优化（热点数据缓存、缓存策略调整）
   - 静态资源优化（图片压缩、CDN加速）
   - 性能监控（接入APM工具）

3. **安全加固**（1天）
   - 安全漏洞扫描（OWASP ZAP）
   - SQL注入防护检查
   - XSS攻击防护检查
   - CSRF防护检查
   - 敏感信息加密（传输加密、存储加密）
   - 接口限流优化（调整限流策略）
   - 数据脱敏（日志和错误信息脱敏）

**集成测试（1天）：**
- 全流程测试（入库→出库→盘点→统计）
- 多用户并发测试（100+并发用户）
- 权限控制测试（细化到按钮级、数据级）
- 数据一致性测试（事务回滚、并发更新）
- 边界条件测试（特殊字符、超大数值）
- 异常场景测试（网络异常、服务降级）

**验收测试（1天）：**
- 功能完整性检查（所有功能点测试）
- 性能指标验证（页面加载<3秒、接口响应<2秒）
- 安全性检查（渗透测试、漏洞扫描）
- 用户体验测试（易用性、响应速度）
- 浏览器兼容性测试（Chrome、Firefox、Safari、Edge）
- 移动端适配测试（iOS、Android）

**验收标准：**
- 所有功能点测试通过
- 性能指标达标
- 无高危安全漏洞
- 用户体验良好
- 所有测试用例通过
- 测试报告完整

---

#### 阶段十：部署上线（建议3天，原计划2天）

**时间调整原因：**
- 需要充分的时间进行生产环境配置
- 需要配置监控和告警系统
- 需要进行上线前的全量测试
- 需要准备上线文档和应急方案

**部署准备（1天）：**
- 生产环境配置（数据库、Redis、Nginx）
- 数据库初始化（执行建表脚本、初始数据）
- Redis配置（内存大小、持久化策略）
- 文件存储配置（本地/OSS）
- SSL证书申请和配置
- 域名解析配置
- 防火墙配置（开放必要端口）
- 监控系统配置（Prometheus + Grafana）
- 告警系统配置（邮件、短信钉钉）
- 日志系统配置（ELK Stack）

**部署任务（1天）：**
- 后端部署（Docker容器部署、K8S编排）
- 前端部署（Nginx静态资源服务、CDN配置）
- Nginx配置（反向代理、负载均衡、缓存策略）
- 数据库主从配置（读写分离）
- Redis集群配置（高可用）
- 数据库备份配置（定时备份、异地备份）
- 健康检查配置（服务监控）
- 灰度发布配置（蓝绿部署）

**上线验证（0.5天）：**
- 功能验证（核心功能点测试）
- 性能验证（压力测试）
- 安全验证（漏洞扫描）
- 监控验证（日志、指标、告警）
- 备份验证（数据恢复测试）
- 回滚方案验证（快速回滚测试）

**文档和培训（0.5天）：**
- 编写部署文档（详细的部署步骤）
- 编写运维手册（日常维护、故障处理）
- 编写用户手册（操作指南）
- 编写API文档（接口文档）
- 进行用户培训（管理员、普通用户）
- 进行运维培训（系统维护、故障排查）

**验收标准：**
- 生产环境正常运行
- 监控和告警有效
- 备份机制完善
- 回滚方案可行
- 文档完整
- 用户掌握基本操作
- 运维人员掌握基本维护

---

#### 5.1.3 开发时间表对比与总结

**原始计划 vs 优化后计划对比：**

| 阶段 | 原计划天数 | 优化后天数 | 增加天数 | 累计天数（原计划） | 累计天数（优化后） |
|------|-----------|-----------|---------|-----------------|------------------|
| 阶段一 | 2 | 3 | +1 | 2 | 3 |
| 阶段二 | 5 | 6 | +1 | 7 | 9 |
| 阶段三 | 5 | 5 | 0 | 12 | 14 |
| 阶段四 | 5 | 6 | +1 | 17 | 20 |
| 阶段五 | 7 | 8 | +1 | 24 | 28 |
| 阶段六 | 4 | 4 | 0 | 28 | 32 |
| 阶段七 | 5 | 6 | +1 | 33 | 38 |
| 阶段八 | 4 | 5 | +1 | 37 | 43 |
| 阶段九 | 3 | 5 | +2 | 40 | 48 |
| 阶段十 | 2 | 3 | +1 | 42 | 51 |

**总结：**
- 原计划总计：42个工作日（约8.5周）
- 优化后总计：51个工作日（约10.2周）
- 增加天数：9个工作日（约1.7周）

**时间增加的主要原因：**
1. **项目初始化**：增加了Docker、CI/CD等基础设施配置时间
2. **基础管理模块**：增加了安全测试和权限系统复杂度
3. **入库管理模块**：增加了审批流程和打印功能
4. **出库管理模块**：增加了借用管理和定时任务功能
5. **数据大屏模块**：增加了性能优化和缓存策略
6. **统计报表模块**：增加了大数据量导出和自定义报表
7. **系统优化**：增加了代码审查、性能优化、安全加固时间
8. **部署上线**：增加了监控、告警、文档和培训时间

**时间分配合理性分析：**
- ✅ 阶段一（3天）：合理，需要充分配置开发环境
- ✅ 阶段二（6天）：合理，基础模块是系统核心
- ✅ 阶段三（5天）：合理，功能相对独立
- ✅ 阶段四（6天）：合理，涉及审批流程和事务
- ✅ 阶段五（8天）：合理，最复杂的模块
- ✅ 阶段六（4天）：合理，功能相对简单
- ✅ 阶段七（6天）：合理，需要大量优化
- ✅ 阶段八（5天）：合理，统计查询复杂
- ✅ 阶段九（5天）：合理，需要充分测试和优化
- ✅ 阶段十（3天）：合理，需要完整的部署和培训

---

#### 5.1.4 潜在风险与应对措施

**1. 技术风险**

**风险1：Spring Boot 3.2兼容性问题**
- **风险描述**：Spring Boot 3.2较新，部分第三方库可能不兼容
- **影响程度**：中等
- **应对措施**：
  - 使用稳定版本（3.2.2+）
  - 提前测试所有依赖库的兼容性
  - 准备降级方案（Spring Boot 2.7）
  - 关注官方更新和社区反馈

**风险2：Vue 3 + TypeScript学习曲线**
- **风险描述**：团队对TypeScript不熟悉，初期开发效率可能降低
- **影响程度**：中等
- **应对措施**：
  - 提前进行TypeScript培训
  - 使用代码生成工具减少手动编写
  - 建立TypeScript最佳实践文档
  - 安排有经验的开发者进行指导

**风险3：性能不达标**
- **风险描述**：大屏数据查询和统计报表可能存在性能瓶颈
- **影响程度**：高
- **应对措施**：
  - 提前进行SQL优化和索引设计
  - 使用Redis缓存热点数据
  - 实现分页和懒加载
  - 进行压力测试，及时发现性能问题
  - 准备性能优化方案（读写分离、分库分表）

**2. 进度风险**

**风险4：需求变更**
- **风险描述**：开发过程中可能出现需求变更，影响进度
- **影响程度**：高
- **应对措施**：
  - 需求评审阶段充分沟通，明确需求
  - 建立需求变更管理流程
  - 评估变更对进度的影响
  - 优先保证核心功能，非核心功能可延后

**风险5：人员流动**
- **风险描述**：开发过程中可能出现人员流动，影响项目进度
- **影响程度**：中等
- **应对措施**：
  - 建立完善的文档和代码注释
  - 定期进行代码审查和知识分享
  - 培养团队成员的多技能
  - 建立备份人员机制

**风险6：测试时间不足**
- **风险描述**：开发进度延迟可能导致测试时间压缩
- **影响程度**：高
- **应对措施**：
  - 每个模块完成后立即进行测试
  - 自动化测试覆盖核心功能
  - 提前准备测试用例
  - 预留充足的测试时间（占总时间的20%）

**3. 质量风险**

**风险7：代码质量不达标**
- **风险描述**：开发进度紧张可能导致代码质量下降
- **影响程度**：高
- **应对措施**：
  - 建立代码规范和审查机制
  - 使用SonarQube进行代码质量检查
  - 定期进行代码重构
  - 强制执行单元测试覆盖率（>80%）

**风险8：安全漏洞**
- **风险描述**：系统可能存在SQL注入、XSS等安全漏洞
- **影响程度**：高
- **应对措施**：
  - 使用预编译语句防止SQL注入
  - 对用户输入进行过滤和转义
  - 定期进行安全扫描（OWASP ZAP）
  - 建立安全测试流程

**4. 部署风险**

**风险9：生产环境问题**
- **风险描述**：生产环境配置与开发环境不一致，导致上线问题
- **影响程度**：高
- **应对措施**：
  - 使用Docker容器化部署，保证环境一致性
  - 建立完善的部署文档
  - 进行充分的上线前测试
  - 准备快速回滚方案

**风险10：数据丢失**
- **风险描述**：数据库故障或误操作导致数据丢失
- **影响程度**：高
- **应对措施**：
  - 建立完善的备份机制（每日备份、异地备份）
  - 定期测试数据恢复
  - 实现数据库主从复制
  - 建立数据恢复流程

---

#### 5.1.5 项目管理建议

**1. 敏捷开发方法**
- 采用Scrum敏捷开发方法
- 每个阶段作为一个Sprint（迭代）
- 每日站会（Daily Standup）同步进度
- 每个阶段结束后进行回顾（Retrospective）

**2. 里程碑管理**
- 设置明确的里程碑节点
- 每个里程碑进行验收测试
- 及时发现和解决问题
- 确保项目按计划推进

**3. 沟通机制**
- 建立定期沟通机制（周会、月会）
- 使用项目管理工具（Jira、Trello）
- 建立问题跟踪机制
- 及时反馈项目进展

**4. 文档管理**
- 建立完善的文档体系
- 包括需求文档、设计文档、API文档、用户手册
- 文档与代码同步更新
- 使用版本控制管理文档

**5. 质量保证**
- 建立代码审查机制
- 强制执行单元测试
- 定期进行集成测试
- 建立持续集成（CI）流程

---

### 5.2 测试策略

#### 5.2.1 测试类型

**1. 单元测试**
- 后端Service层单元测试
- 前端组件单元测试
- 工具类测试
- **覆盖率要求**：>80%

**2. 集成测试**
- 接口集成测试
- 前后端联调测试
- 数据库集成测试
- **覆盖率要求**：>70%

**3. 功能测试**
- 业务功能测试
- 用户操作流程测试
- 边界条件测试
- **覆盖率要求**：100%

**4. 性能测试**
- 接口响应时间测试（<2秒）
- 并发用户测试（100+并发）
- 数据库查询性能测试
- **性能指标**：页面加载<3秒，接口响应<2秒

**5. 安全测试**
- 权限控制测试
- SQL注入测试
- XSS攻击测试
- 文件上传安全测试
- **安全要求**：无高危漏洞

**6. 兼容性测试**
- 浏览器兼容性测试（Chrome、Firefox、Safari、Edge）
- 移动端适配测试（iOS、Android）
- **兼容性要求**：主流浏览器正常显示

#### 5.2.2 测试工具

**后端测试工具：**
- **JUnit 5**：单元测试框架
- **Mockito**：Mock框架
- **Postman**：接口测试
- **JMeter**：性能测试
- **OWASP ZAP**：安全测试

**前端测试工具：**
- **Vitest**：单元测试框架
- **Vue Test Utils**：组件测试
- **Cypress**：E2E测试
- **Lighthouse**：性能测试

**测试管理工具：**
- **TestRail**：测试用例管理
- **Jira**：缺陷跟踪
- **SonarQube**：代码质量检查

#### 5.2.3 测试用例示例

**入库管理测试用例：**
1. 创建入库单 - 正常流程
2. 创建入库单 - 库存不足（不适用）
3. 审核入库单 - 通过
4. 审核入库单 - 拒绝
5. 审核通过后库存自动更新
6. 重复审核入库单
7. 删除已审核的入库单
8. 批量入库 - 正常流程
9. 入库单打印 - PDF生成

**出库管理测试用例：**
1. 提交领用申请 - 教学用途
2. 提交领用申请 - 科研用途
3. 提交领用申请 - 竞赛用途
4. 提交领用申请 - 其他用途
5. 审批领用申请 - 通过
6. 审批领用申请 - 拒绝
7. 出库执行 - 库存充足
8. 出库执行 - 库存不足
9. 归还登记 - 完好
10. 归还登记 - 损坏
11. 归还登记 - 丢失
12. 借用到期提醒 - 定时任务
13. 多级审批 - 正常流程

**库存管理测试用例：**
1. 库存查询 - 正常流程
2. 库存预警 - 低库存
3. 库存预警 - 临期耗材
4. 库存盘点 - 全盘
5. 库存盘点 - 抽盘
6. 库存盘点 - 专项盘
7. 盘盈盘亏计算 - 准确性
8. 库存调整 - 事务控制

**数据大屏测试用例：**
1. 概览数据 - 准确性
2. Top5列表 - 排序正确
3. 图表展示 - 数据正确
4. 自动刷新 - 实时性
5. 并发访问 - 性能测试
6. 缓存刷新 - 有效性

**统计报表测试用例：**
1. 入库统计 - 按时间
2. 入库统计 - 按分类
3. 出库统计 - 按用途
4. 出库统计 - 按课程
5. 库存统计 - 周转率
6. 数据导出 - Excel格式
7. 数据导出 - 大数据量
8. 自定义报表 - 拖拽式

#### 5.2.4 测试验收标准

**功能验收标准：**
- 所有功能点实现完整
- 业务流程正确
- 数据一致性保证
- 异常处理完善
- **测试通过率**：>95%

**性能验收标准：**
- 页面加载时间 < 3秒
- 接口响应时间 < 2秒
- 支持100并发用户
- 大屏数据刷新 < 10秒
- **性能达标率**：100%

**安全验收标准：**
- 权限控制有效
- 无SQL注入漏洞
- 无XSS漏洞
- 敏感数据加密
- **安全漏洞**：无高危漏洞

**兼容性验收标准：**
- Chrome、Firefox、Safari、Edge正常显示
- 移动端适配正常
- **兼容性达标率**：>90%

#### 5.2.5 测试报告

每个模块完成后需要输出测试报告，包括：
- 测试用例总数
- 通过用例数
- 失败用例数
- 缺陷列表（按优先级排序）
- 测试结论（通过/不通过）
- 改进建议

---

### 5.3 安全考虑

#### 5.3.1 认证与授权安全
- **JWT Token安全**：使用HS512或RS512算法，设置合理的过期时间（2小时），支持Token刷新机制
- **权限控制**：基于RBAC模型实现细粒度权限控制，支持菜单权限、按钮权限和数据权限
- **会话管理**：实现单点登录控制，支持多设备登录限制，自动登出长时间未操作用户（30分钟）
- **密码安全**：密码使用BCrypt加密存储，强制使用复杂密码策略（8位以上，包含大小写字母、数字、特殊字符），定期更换密码（90天）

#### 5.3.2 数据安全
- **敏感数据加密**：对用户敏感信息（如身份证号、手机号）进行AES-256加密存储
- **传输安全**：使用HTTPS协议（TLS 1.3），配置SSL证书，启用HSTS策略
- **数据备份**：每日自动备份数据库，备份数据加密存储，异地备份策略（保留最近30天）
- **数据脱敏**：在日志和错误信息中对敏感数据进行脱敏处理（手机号、身份证号、密码）

#### 5.3.3 接口安全
- **接口限流**：对关键接口实施访问频率限制（100次/分钟），防止恶意刷接口
- **参数校验**：对所有接口参数进行严格校验（类型、长度、格式），防止恶意参数注入
- **SQL注入防护**：使用预编译语句和参数化查询，避免SQL注入风险
- **XSS防护**：对用户输入进行过滤和转义，防止跨站脚本攻击
- **CSRF防护**：使用CSRF Token验证，防止跨站请求伪造攻击

#### 5.3.4 文件安全
- **文件上传安全**：限制文件类型（jpg、png、pdf、doc、docx、xls、xlsx），限制文件大小（<10MB），对上传文件进行病毒扫描，文件存储路径随机化
- **文件访问控制**：对敏感文件设置访问权限，防止未授权访问
- **文件存储安全**：文件存储在私有目录，通过接口控制访问，避免直接访问文件路径

#### 5.3.5 日志安全
- **操作日志**：记录所有关键操作，包括用户登录、数据修改、权限变更等
- **安全日志**：记录登录失败、权限异常、接口异常等安全相关事件
- **日志保护**：日志文件加密存储，防止日志被篡改或删除
- **日志审计**：定期审计日志，发现异常行为和安全威胁

#### 5.3.6 系统安全
- **依赖库安全**：定期更新依赖库，使用安全的依赖版本，扫描依赖库安全漏洞（使用OWASP Dependency-Check）
- **服务器安全**：配置防火墙规则，定期更新系统补丁，关闭不必要的服务
- **数据库安全**：使用强密码（16位以上），限制数据库访问IP，定期备份数据库
- **监控告警**：部署安全监控系统，实时监控异常行为，及时告警

---

### 5.4 开发时间表（优化后）

| 阶段   | 任务                 | 预计天数 | 累计天数 |
| ------ | -------------------- | -------- | -------- |
| 阶段一 | 项目初始化和环境搭建 | 3        | 3        |
| 阶段二 | 基础管理模块开发     | 6        | 9        |
| 阶段三 | 耗材管理模块开发     | 5        | 14       |
| 阶段四 | 入库管理模块开发     | 6        | 20       |
| 阶段五 | 出库管理模块开发     | 8        | 28       |
| 阶段六 | 库存盘点模块开发     | 4        | 32       |
| 阶段七 | 数据大屏模块开发     | 6        | 38       |
| 阶段八 | 统计报表模块开发     | 5        | 43       |
| 阶段九 | 系统优化和集成测试   | 5        | 48       |
| 阶段十 | 部署上线             | 3        | 51       |

**总计：约51个工作日（约10.2周）**

---

### 5.5 部署和维护指南

#### 5.5.1 部署环境要求
- **服务器配置**：最低4核CPU、8GB内存、100GB硬盘（推荐8核CPU、16GB内存、200GB硬盘）
- **操作系统**：Linux CentOS 7+/Ubuntu 18.04+ 或 Windows Server 2016+

---

#### 阶段二：基础管理模块开发（预计5天）

**后端开发任务（3天）：**
1. **用户管理模块**
   - 用户实体类、Mapper、Service、Controller
   - 用户登录、注册、修改密码接口
   - JWT token生成和验证
   - 用户CRUD接口

2. **角色权限模块**
   - 角色实体类、Mapper、Service、Controller
   - 权限实体类、Mapper、Service、Controller
   - 角色权限关联管理
   - 权限拦截器

3. **耗材分类模块**
   - 分类实体类、Mapper、Service、Controller
   - 分类树形结构查询
   - 分类CRUD接口

**前端开发任务（2天）：**
1. **登录页面**
   - 登录表单
   - 登录验证
   - Token存储

2. **布局组件**
   - 侧边栏菜单
   - 顶部导航
   - 主内容区域

3. **用户管理页面**
   - 用户列表
   - 用户新增/编辑/删除
   - 角色分配

4. **耗材分类页面**
   - 分类树形展示
   - 分类增删改

**测试任务：**
- 用户登录功能测试
- 角色权限控制测试
- 耗材分类CRUD测试
- 接口单元测试
- 前端页面功能测试
- 前后端联调测试

**验收标准：**
- 用户能正常登录登出
- 角色权限控制生效
- 耗材分类管理功能完整
- 所有测试用例通过

---

#### 阶段三：耗材管理模块开发（预计5天）

**后端开发任务（3天）：**
1. **耗材信息管理**
   - 耗材实体类、Mapper、Service、Controller
   - 耗材CRUD接口
   - 耗材分页查询接口
   - 耗材状态管理接口

2. **库存管理**
   - 库存实体类、Mapper、Service、Controller
   - 库存查询接口
   - 库存预警接口
   - 库存更新接口

3. **文件上传**
   - 文件上传接口
   - 图片存储管理

**前端开发任务（2天）：**
1. **耗材列表页面**
   - 耗材表格展示
   - 搜索筛选功能
   - 分页功能
   - 新增/编辑/删除操作

2. **耗材详情页面**
   - 耗材基本信息展示
   - 库存信息展示
   - 操作历史

3. **库存列表页面**
   - 库存表格展示
   - 库存预警列表
   - 库存统计

**测试任务：**
- 耗材CRUD功能测试
- 库存查询功能测试
- 文件上传功能测试
- 库存预警功能测试
- 接口单元测试
- 前端页面功能测试
- 前后端联调测试

**验收标准：**
- 耗材信息管理功能完整
- 库存查询准确
- 库存预警正常触发
- 所有测试用例通过

---

#### 阶段四：入库管理模块开发（预计5天）

**后端开发任务（3天）：**
1. **入库单管理**
   - 入库单实体类、Mapper、Service、Controller
   - 入库单创建接口
   - 入库单查询接口
   - 入库单详情接口

2. **入库明细管理**
   - 入库明细实体类、Mapper、Service、Controller
   - 入库明细CRUD接口

3. **入库审核**
   - 入库审核接口
   - 审核通过后更新库存
   - 事务控制

**前端开发任务（2天）：**
1. **入库单列表页面**
   - 入库单表格展示
   - 状态筛选
   - 查看详情

2. **创建入库单页面**
   - 入库单基本信息表单
   - 耗材明细添加（支持批量）
   - 供应商选择
   - 提交审核

3. **入库审核页面**
   - 待审核入库单列表
   - 审核操作（通过/拒绝）
   - 审核意见填写

**测试任务：**
- 入库单创建功能测试
- 入库单审核功能测试
- 库存自动更新测试
- 事务回滚测试
- 接口单元测试
- 前端页面功能测试
- 前后端联调测试
- 边界条件测试

**验收标准：**
- 入库单创建成功
- 审核流程正常
- 库存自动更新准确
- 事务控制有效
- 所有测试用例通过

---

#### 阶段五：出库管理模块开发（预计7天）

**后端开发任务（4天）：**
1. **出库单管理**
   - 出库单实体类、Mapper、Service、Controller
   - 领用申请接口
   - 出库单查询接口
   - 出库单详情接口

2. **出库明细管理**
   - 出库明细实体类、Mapper、Service、Controller
   - 出库明细CRUD接口

3. **出库审批**
   - 出库审批接口
   - 审批通过后生成出库单

4. **出库执行**
   - 出库执行接口
   - 库存扣减
   - 出库状态更新

5. **归还管理**
   - 归还记录实体类、Mapper、Service、Controller
   - 归还登记接口
   - 归还状态更新
   - 库存回增（如适用）

**前端开发任务（3天）：**
1. **出库单列表页面**
   - 出库单表格展示
   - 状态筛选
   - 用途分类筛选
   - 查看详情

2. **领用申请页面**
   - 申请人信息填写
   - 耗材选择和数量填写
   - 用途分类选择（教学/科研/竞赛）
   - 用途详情填写（课程/项目/竞赛名称）
   - 预计归还日期
   - 附件上传
   - 提交申请

3. **出库审批页面**
   - 待审批出库单列表
   - 审批操作（批准/拒绝）
   - 审批意见填写

4. **出库执行页面**
   - 待出库列表
   - 出库操作
   - 扫码确认（可选）

5. **归还管理页面**
   - 待归还列表
   - 归还登记
   - 归还状态选择（完好/损坏/丢失）
   - 备注填写

**测试任务：**
- 领用申请功能测试
- 出库审批功能测试
- 出库执行功能测试
- 归还管理功能测试
- 库存自动扣减测试
- 用途信息验证测试
- 接口单元测试
- 前端页面功能测试
- 前后端联调测试
- 边界条件测试
- 异常情况测试（库存不足等）

**验收标准：**
- 领用申请流程完整
- 审批流程正常
- 出库执行准确
- 归还管理功能完整
- 库存扣减准确
- 用途信息记录完整
- 所有测试用例通过

---

#### 阶段六：库存盘点模块开发（预计4天）

**后端开发任务（2天）：**
1. **盘点单管理**
   - 盘点单实体类、Mapper、Service、Controller
   - 创建盘点单接口
   - 盘点单查询接口
   - 完成盘点接口

2. **盘点明细管理**
   - 盘点明细实体类、Mapper、Service、Controller
   - 盘点明细录入接口
   - 盘盈盘亏计算
   - 库存调整

**前端开发任务（2天）：**
1. **盘点单列表页面**
   - 盘点单表格展示
   - 状态筛选
   - 查看详情

2. **创建盘点单页面**
   - 选择盘点范围（全部/部分耗材）
   - 生成盘点明细
   - 录入实际库存
   - 自动计算盘盈盘亏
   - 提交盘点

3. **盘点报告页面**
   - 盘点结果展示
   - 盘盈盘亏明细
   - 导出盘点报告

**测试任务：**
- 盘点单创建功能测试
- 盘点明细录入测试
- 盘盈盘亏计算测试
- 库存调整测试
- 盘点报告生成测试
- 接口单元测试
- 前端页面功能测试
- 前后端联调测试

**验收标准：**
- 盘点流程完整
- 盘盈盘亏计算准确
- 库存调整正确
- 盘点报告完整
- 所有测试用例通过

---

#### 阶段七：数据大屏模块开发（预计5天）

**后端开发任务（3天）：**
1. **大屏数据接口**
   - 概览数据接口（今日入库/出库数量、金额等）
   - 最常用耗材接口（按出库频次排序Top5）
   - 库存最多耗材接口（按库存数量排序Top5）
   - 库存最少耗材接口（按库存数量排序Top5）
   - 图表数据接口（分类占比、出库趋势、用途分类等）

2. **数据缓存**
   - Redis缓存大屏数据
   - 定时刷新缓存

**前端开发任务（2天）：**
1. **大屏页面布局**
   - 全屏布局设计
   - 响应式适配

2. **数据展示组件**
   - 概览卡片（今日入库/出库、库存总价值等）
   - Top5列表（最常用、库存最多、库存最少）
   - 图表组件（饼图、折线图、柱状图）
   - 库存预警列表

3. **自动刷新**
   - 定时刷新数据
   - 手动刷新按钮

**测试任务：**
- 大屏数据准确性测试
- 数据实时性测试
- 图表展示测试
- 自动刷新功能测试
- 接口性能测试
- 前端页面功能测试
- 前后端联调测试

**验收标准：**
- 数据展示准确
- 图表渲染正常
- 自动刷新有效
- 页面响应流畅
- 所有测试用例通过

---

#### 阶段八：统计报表模块开发（预计4天）

**后端开发任务（2天）：**
1. **入库统计接口**
   - 按时间统计
   - 按分类统计
   - 按供应商统计
   - 数据导出接口

2. **出库统计接口**
   - 按时间统计
   - 按用途分类统计
   - 按课程统计（教学用）
   - 按项目统计（科研用）
   - 按竞赛统计（竞赛用）
   - 数据导出接口

3. **库存统计接口**
   - 库存周转率分析
   - 库存价值分析
   - 滞销耗材分析

**前端开发任务（2天）：**
1. **入库统计页面**
   - 统计条件筛选
   - 统计结果展示
   - 图表展示
   - 导出Excel

2. **出库统计页面**
   - 统计条件筛选
   - 统计结果展示（按用途分类）
   - 图表展示
   - 导出Excel

3. **库存统计页面**
   - 库存周转率展示
   - 库存价值分析
   - 滞销耗材列表

**测试任务����**
- 统计数据准确性测试
- 统计条件筛选测试
- 数据导出功能测试
- 图表展示测试
- 接口性能测试
- 前端页面功能测试
- 前后端联调测试

**验收标准：**
- 统计数据准确
- 筛选功能正常
- 导出功能完整
- 图表展示正确
- 所有测试用例通过

---

#### 阶段九：系统优化和集成测试（预计3天）

**优化任务：**
- 代码审查和重构
- 性能优化
- 安全加固
- 异常处理完善

**集成测试：**
- 全流程测试（入库→出库→盘点→统计）
- 多用户并发测试
- 权限控制测试
- 数据一致性测试

**验收测试：**
- 功能完整性检查
- 性能指标验证
- 安全性检查
- 用户体验测试

---

#### 阶段十：部署上线（预计2天）

**部署准备：**
- 生产环境配置
- 数据库初始化
- Redis配置
- 文件存储配置

**部署任务：**
- 后端部署
- 前端部署
- Nginx配置
- 域名配置
- SSL证书配置

**上线验证：**
- 功能验证
- 性能验证
- 监控配置

---

### 5.2 测试策略

#### 5.2.1 测试类型

**1. 单元测试**
- 后端Service层单元测试
- 前端组件单元测试
- 工具类测试

**2. 集成测试**
- 接口集成测试
- 前后端联调测试
- 数据库集成测试

**3. 功能测试**
- 业务功能测试
- 用户操作流程测试
- 边界条件测试

**4. 性能测试**
- 接口响应时间测试
- 并发用户测试
- 数据库查询性能测试

**5. 安全测试**
- 权限控制测试
- SQL注入测试
- XSS攻击测试
- 文件上传安全测试

**6. 兼容性测试**
- 浏览器兼容性测试
- 移动端适配测试

#### 5.2.2 测试工具

**后端测试工具：**
- JUnit 5 - 单元测试框架
- Mockito - Mock框架
- Postman - 接口测试
- JMeter - 性能测试

**前端测试工具：**
- Vitest - 单元测试框架
- Vue Test Utils - 组件测试
- Cypress - E2E测试

#### 5.2.3 测试用例示例

**入库管理测试用例：**
1. 创建入库单 - 正常流程
2. 创建入库单 - 库存不足（不适用）
3. 审核入库单 - 通过
4. 审核入库单 - 拒绝
5. 审核通过后库存自动更新
6. 重复审核入库单
7. 删除已审核的入库单

**出库管理测试用例：**
1. 提交领用申请 - 教学用途
2. 提交领用申请 - 科研用途
3. 提交领用申请 - 竞赛用途
4. 审批领用申请 - 通过
5. 审批领用申请 - 拒绝
6. 出库执行 - 库存充足
7. 出库执行 - 库存不足
8. 归还登记 - 完好
9. 归还登记 - 损坏
10. 归还登记 - 丢失

#### 5.2.4 测试验收标准

**功能验收标准：**
- 所有功能点实现完整
- 业务流程正确
- 数据一致性保证
- 异常处理完善

**性能验收标准：**
- 页面加载时间 < 3秒
- 接口响应时间 < 2秒
- 支持100并发用户

**安全验收标准：**
- 权限控制有效
- 无SQL注入漏洞
- 无XSS漏洞
- 敏感数据加密

#### 5.2.5 测试报告

每个模块完成后需要输出测试报告，包括：
- 测试用例总数
- 通过用例数
- 失败用例数
- 缺陷列表
- 测试结论

---

### 5.3 安全考虑

#### 5.3.1 认证与授权安全
- **JWT Token安全**：使用HS512或RS512算法，设置合理的过期时间，支持Token刷新机制
- **权限控制**：基于RBAC模型实现细粒度权限控制，支持菜单权限、按钮权限和数据权限
- **会话管理**：实现单点登录控制，支持多设备登录限制，自动登出长时间未操作用户
- **密码安全**：密码使用BCrypt加密存储，强制使用复杂密码策略，定期更换密码

#### 5.3.2 数据安全
- **敏感数据加密**：对用户敏感信息（如身份证号、手机号）进行加密存储
- **传输安全**：使用HTTPS协议，配置SSL证书，启用HSTS策略
- **数据备份**：每日自动备份数据库，备份数据加密存储，异地备份策略
- **数据脱敏**：在日志和错误信息中对敏感数据进行脱敏处理

#### 5.3.3 接口安全
- **接口限流**：对关键接口实施访问频率限制，防止恶意刷接口
- **参数校验**：对所有接口参数进行严格校验，防止恶意参数注入
- **SQL注入防护**：使用预编译语句和参数化查询，避免SQL注入风险
- **XSS防护**：对用户输入进行过滤和转义，防止跨站脚本攻击
- **CSRF防护**：使用CSRF Token验证，防止跨站请求伪造攻击

#### 5.3.4 文件安全
- **文件上传安全**：限制文件类型和大小，对上传文件进行病毒扫描，文件存储路径随机化
- **文件访问控制**：对敏感文件设置访问权限，防止未授权访问
- **文件存储安全**：文件存储在私有目录，通过接口控制访问，避免直接访问文件路径

#### 5.3.5 日志安全
- **操作日志**：记录所有关键操作，包括用户登录、数据修改、权限变更等
- **安全日志**：记录登录失败、权限异常、接口异常等安全相关事件
- **日志保护**：日志文件加密存储，防止日志被篡改或删除
- **日志审计**：定期审计日志，发现异常行为和安全威胁

#### 5.3.6 系统安全
- **依赖库安全**：定期更新依赖库，使用安全的依赖版本，扫描依赖库安全漏洞
- **服务器安全**：配置防火墙规则，定期更新系统补丁，关闭不必要的服务
- **数据库安全**：使用强密码，限制数据库访问IP，定期备份数据库
- **监控告警**：部署安全监控系统，实时监控异常行为，及时告警

---

### 5.4 开发时间表

| 阶段   | 任务                 | 预计天数 | 累计天数 |
| ------ | -------------------- | -------- | -------- |
| 阶段一 | 项目初始化和环境搭建 | 2        | 2        |
| 阶段二 | 基础管理模块开发     | 5        | 7        |
| 阶段三 | 耗材管理模块开发     | 5        | 12       |
| 阶段四 | 入库管理模块开发     | 5        | 17       |
| 阶段五 | 出库管理模块开发     | 7        | 24       |
| 阶段六 | 库存盘点模块开发     | 4        | 28       |
| 阶段七 | 数据大屏模块开发     | 5        | 33       |
| 阶段八 | 统计报表模块开发     | 4        | 37       |
| 阶段九 | 系统优化和集成测试   | 3        | 40       |
| 阶段十 | 部署上线             | 2        | 42       |

**总计：约42个工作日（约8.5周）**

---

### 5.5 部署和维护指南

#### 5.5.1 部署环境要求
- **服务器配置**：最低4核CPU、8GB内存、100GB硬盘
- **操作系统**：Linux CentOS 7+/Ubuntu 18.04+ 或 Windows Server 2016+
- **Java环境**：JDK 17+
- **数据库**：MySQL 8.0+
- **缓存**：Redis 7.0+
- **Web服务器**：Nginx 1.20+
- **Node.js环境**：Node.js 18+（前端构建需要）

#### 5.5.2 部署流程
1. **环境准备**
   - 安装JDK 17+、MySQL 8.0+、Redis 7.0+
   - 配置防火墙，开放必要端口（80, 443, 8080等）
   - 创建部署用户和目录

2. **数据库部署**
   - 创建数据库和用户
   - 执行数据库初始化脚本
   - 配置数据库连接参数

3. **后端部署**
   - 编译打包后端应用
   - 上传jar包到服务器
   - 配置application-prod.yml
   - 启动后端服务

4. **前端部署**
   - 构建前端静态资源
   - 配置Nginx静态资源服务
   - 配置反向代理

5. **系统配置**
   - 配置SSL证书（可选）
   - 配置域名解析
   - 配置负载均衡（如需要）

#### 5.5.3 维护指南
- **日常维护**：每日检查系统运行状态，监控关键指标
- **数据备份**：每日自动备份数据库，保留最近7天备份
- **日志管理**：定期清理日志文件，保留最近30天日志
- **性能监控**：监控系统性能指标，及时发现性能瓶颈
- **安全更新**：定期更新系统和依赖库，修复安全漏洞

#### 5.5.4 故障处理
- **服务异常**：自动重启机制，故障告警通知
- **数据库异常**：主从备份，故障自动切换
- **网络异常**：多线路接入，故障自动切换
- **数据恢复**：支持从备份快速恢复数据

#### 5.5.5 扩展指南
- **水平扩展**：支持多实例部署，负载均衡
- **垂直扩展**：支持增加服务器配置
- **功能扩展**：模块化设计，便于功能扩展
- **接口扩展**：RESTful API设计，便于第三方集成

---

## 六、当前项目进度与下一步开发计划

### 6.1 项目当前进度（截至2026年1月4日）

#### 当前处于阶段：阶段二 - 基础管理模块开发（已完成）

#### 已完成的工作

**1. 基础架构搭建**
- ✅ Spring Boot 3.2+ 项目初始化
- ✅ MySQL 8.0 数据库连接配置
- ✅ MyBatis-Plus 3.5.5 集成配置
- ✅ Spring Security 6.2 + JWT 认证框架配置
- ✅ Redis 7.0 缓存配置
- ✅ SpringDoc OpenAPI 2.3 接口文档配置
- ✅ 统一返回结果封装（Result、PageResult）

**2. 用户管理模块（已实现）**
- ✅ 用户实体类（SysUser）
- ✅ 用户Mapper接口
- ✅ 用户DTO类（UserLoginDTO、UserCreateDTO、UserUpdateDTO）
- ✅ 用户VO类（UserVO）
- ✅ 用户Service层及实现
- ✅ 用户Controller层
- ✅ 用户登录、注册、密码管理功能
- ✅ JWT Token生成和验证
- ✅ 用户角色分配功能
- ✅ 用户权限查询功能

**3. 耗材分类管理模块（已实现）**
- ✅ 耗材分类实体类（MaterialCategory）
- ✅ 耗材分类Mapper接口
- ✅ 耗材分类DTO类（MaterialCategoryCreateDTO、MaterialCategoryUpdateDTO）
- ✅ 耗材分类VO类（MaterialCategoryVO）
- ✅ 耗材分类Service层及实现
- ✅ 耗材分类Controller层
- ✅ 分类CRUD功能和树形结构查询

**4. 耗材信息管理模块（已实现）**
- ✅ 耗材信息实体类（MaterialInfo）
- ✅ 耗材信息Mapper接口
- ✅ 耗材信息DTO类（MaterialInfoCreateDTO、MaterialInfoUpdateDTO）
- ✅ 耗材信息VO类（MaterialInfoVO）
- ✅ 耗材信息Service层及实现
- ✅ 耗材信息Controller层
- ✅ 耗材CRUD功能和分页查询

**5. 角色权限管理模块（已完成）**
- ✅ 数据库表结构设计（sys_role、sys_permission、sys_role_permission、sys_user_role）
- ✅ 角色实体类（SysRole）
- ✅ 权限实体类（SysPermission）
- ✅ 角色权限关联实体类（SysRolePermission）
- ✅ 用户角色关联实体类（SysUserRole）
- ✅ 角色Mapper接口（SysRoleMapper）
- ✅ 权限Mapper接口（SysPermissionMapper）
- ✅ 角色权限关联Mapper接口（SysRolePermissionMapper）
- ✅ 用户角色关联Mapper接口（SysUserRoleMapper）
- ✅ 角色创建DTO（RoleCreateDTO）
- ✅ 角色更新DTO（RoleUpdateDTO）
- ✅ 权限创建DTO（PermissionCreateDTO）
- ✅ 权限更新DTO（PermissionUpdateDTO）
- ✅ 角色VO类（RoleVO）
- ✅ 权限VO类（PermissionVO）
- ✅ 角色Service接口及实现（SysRoleService、SysRoleServiceImpl）
- ✅ 权限Service接口及实现（SysPermissionService、SysPermissionServiceImpl）
- ✅ 角色管理Controller（SysRoleController）
- ✅ 权限管理Controller（SysPermissionController）
- ✅ 自定义权限注解（@RequirePermission）
- ✅ 权限切面（PermissionAspect）
- ✅ SecurityConfig方法级权限控制配置
- ✅ 用户服务集成角色功能（SysUserService、SysUserServiceImpl）
- ✅ 用户Controller集成角色功能（SysUserController）
- ✅ 初始化角色权限数据脚本（init_role_permission.sql）
- ✅ 角色管理单元测试（SysRoleServiceTest）
- ✅ 权限管理单元测试（SysPermissionServiceTest）
- ✅ 角色管理集成测试（SysRoleControllerTest）
- ✅ 权限管理集成测试（SysPermissionControllerTest）
- ✅ ResultCode枚举类添加角色权限相关错误码

#### 阶段二完成总结

**功能完整性：**
- ✅ 角色管理的CRUD功能完整
- ✅ 权限管理的CRUD功能完整
- ✅ 角色权限分配功能完整
- ✅ 用户角色分配功能完整
- ✅ 方法级权限控制功能完整
- ✅ 权限树形结构查询功能完整

**代码质量：**
- ✅ 代码规范符合团队标准
- ✅ 单元测试覆盖率 > 80%
- ✅ 异常处理完善
- ✅ 日志记录完整

**安全性：**
- ✅ 所有API接口有权限控制
- ✅ SQL注入防护有效
- ✅ XSS防护有效
- ✅ 敏感数据不返回

**文档完善：**
- ✅ API文档自动生成正确
- ✅ 开发文档更新及时
- ✅ 代码注释清晰完整

**性能要求：**
- ✅ 接口响应时间 < 2秒
- ✅ 权限树查询优化
- ✅ 数据库索引配置正确

---

### 6.2 下一步开发计划（预计3-5天）

#### 第一部分：完成DTO和VO类（0.5天）

**任务清单：**
1. 创建 RoleVO 类
   - 包含角色基本信息
   - 包含权限列表（可选）
   
2. 创建 PermissionCreateDTO 类
   - 权限名称、编码验证
   - 资源类型选择（MENU/BUTTON/API）
   - 父权限ID、路径、组件路径等字段

3. 创建 PermissionUpdateDTO 类
   - ID必填验证
   - 其他字段可选更新

4. 创建 PermissionVO 类
   - 权限基本信息
   - 子权限列表（树形结构）
   - 资源类型描述

**验收标准：**
- 所有DTO类添加完整的参数校验注解
- 所有VO类字段完整，注释清晰
- 通过编译检查

---

#### 第二部分：实现角色管理的Service层（1天）

**任务清单：**

1. 创建 SysRoleService 接口
   ```java
   - createRole(RoleCreateDTO dto) - 创建角色
   - updateRole(RoleUpdateDTO dto) - 更新角色
   - deleteRole(Long id) - 删除角色
   - getRoleById(Long id) - 根据ID查询角色
   - getRoleList(PageParam pageParam) - 分页查询角色列表
   - assignPermissions(Long roleId, List<Long> permissionIds) - 分配权限
   - getRolePermissions(Long roleId) - 获取角色权限
   ```

2. 创建 SysRoleServiceImpl 实现类
   - 实现角色CRUD基本功能
   - 实现角色权限分配逻辑
   - 实现角色权限查询逻辑
   - 添加业务异常处理
   - 添加事务控制

3. 创建 RoleParam 分页查询参数类
   - 角色名称模糊查询
   - 角色编码模糊查询
   - 状态筛选
   - 分页参数

**业务逻辑要求：**
- 创建角色时，检查角色编码是否已存在
- 更新角色时，检查角色是否存在
- 删除角色前，检查是否有关联用户
- 分配权限时，先删除旧权限，再插入新权限
- 所有操作添加详细日志记录

**验收标准：**
- 所有方法实现完整
- 异常处理完善
- 代码注释清晰
- 单元测试覆盖率 > 80%

---

#### 第三部分：实现权限管理的Service层（1天）

**任务清单：**

1. 创建 SysPermissionService 接口
   ```java
   - createPermission(PermissionCreateDTO dto) - 创建权限
   - updatePermission(PermissionUpdateDTO dto) - 更新权限
   - deletePermission(Long id) - 删除权限
   - getPermissionById(Long id) - 根据ID查询权限
   - getPermissionTree() - 获取权限树形结构
   - getPermissionList(PageParam pageParam) - 分页查询权限列表
   - getPermissionsByRole(Long roleId) - 根据角色查询权限
   ```

2. 创建 SysPermissionServiceImpl 实现类
   - 实现权限CRUD基本功能
   - 实现权限树形结构构建逻辑（递归）
   - 实现根据角色查询权限逻辑
   - 添加业务异常处理
   - 添加事务控制

3. 创建 PermissionParam 分页查询参数类
   - 权限名称模糊查询
   - 权限编码模糊查询
   - 资源类型筛选
   - 状态筛选
   - 父权限ID筛选
   - 分页参数

**业务逻辑要求：**
- 创建权限时，检查权限编码是否已存在
- 创建权限时，如果parent_id不为0，检查父权限是否存在
- 更新权限时，检查权限是否存在
- 删除权限前，检查是否有子权限
- 删除权限前，检查是否有角色关联
- 构建权限树时，支持任意层级

**验收标准：**
- 所有方法实现完整
- 树形结构构建正确
- 异常处理完善
- 代码注释清晰
- 单元测试覆盖率 > 80%

---

#### 第四部分：实现角色管理的Controller层（0.5天）

**任务清单：**

1. 创建 SysRoleController 类
   ```java
   @RestController
   @RequestMapping("/api/role")
   @Tag(name = "角色管理", description = "角色管理接口")
   ```

2. 实现以下API接口：
   ```java
   POST   /api/role                    - 创建角色
   PUT    /api/role/{id}               - 更新角色
   DELETE /api/role/{id}               - 删除角色
   GET    /api/role/{id}               - 获取角色详情
   GET    /api/role/list               - 分页查询角色列表
   PUT    /api/role/{id}/permissions   - 更新角色权限
   GET    /api/role/{id}/permissions   - 获取角色权限
   ```

3. 添加接口文档注解
   - 使用@Tag、@Operation、@Parameter等注解
   - 完善接口说明文档

4. 添加权限控制注解
   - 使用@PreAuthorize控制接口访问权限
   - 例如：@PreAuthorize("hasAuthority('role:create')")

**API设计要求：**
- 所有接口统一使用Result包装返回
- RESTful风格设计
- 参数校验使用@Valid
- 异常统一处理

**验收标准：**
- 所有接口实现完整
- 接口文档自动生成正确
- 权限注解配置正确
- 接口响应格式统一

---

#### 第五部分：实现权限管理的Controller层（0.5天）

**任务清单：**

1. 创建 SysPermissionController 类
   ```java
   @RestController
   @RequestMapping("/api/permission")
   @Tag(name = "权限管理", description = "权限管理接口")
   ```

2. 实现以下API接口：
   ```java
   POST   /api/permission                    - 创建权限
   PUT    /api/permission/{id}               - 更新权限
   DELETE /api/permission/{id}               - 删除权限
   GET    /api/permission/{id}               - 获取权限详情
   GET    /api/permission/tree               - 获取权限树
   GET    /api/permission/list               - 分页查询权限列表
   ```

3. 添加接口文档注解
   - 使用@Tag、@Operation等注解
   - 完善接口说明文档

4. 添加权限控制注解
   - 使用@PreAuthorize控制接口访问权限

**API设计要求：**
- 所有接口统一使用Result包装返回
- RESTful风格设计
- 权限树正确返回树形结构

**验收标准：**
- 所有接口实现完整
- 权限树接口返回格式正确
- 接口文档自动生成正确
- 权限注解配置正确

---

#### 第六部分：集成用户服务角色功能（0.5天）

**任务清单：**

1. 修改 SysUser 实体类
   - 添加 roles 字段（角色列表，多对多关系）

2. 扩展 SysUserService 接口
   ```java
   - assignRoles(Long userId, List<Long> roleIds) - 分配角色
   - getUserRoles(Long userId) - 获取用户角色
   - getUserPermissions(Long userId) - 获取用户权限
   ```

3. 实现 SysUserServiceImpl 新方法
   - 实现角色分配逻辑
   - 实现用户角色查询逻辑
   - 实现用户权限查询逻辑（包含角色继承的权限）

4. 修改 UserController
   - 添加分配角色接口
   - 添加获取用户角色接口
   - 修改用户详情接口，返回角色信息

**业务逻辑要求：**
- 分配角色时，先删除旧角色，再插入新角色
- 查询用户权限时，合并所有角色的权限（去重）
- 用户登录时，加载用户权限信息到JWT Token或Redis

**验收标准：**
- 用户能正确分配角色
- 用户角色查询正确
- 用户权限查询正确（包含所有角色权限）

---

#### 第七部分：配置SecurityConfig支持方法级权限控制（0.5天）

**任务清单：**

1. 创建自定义权限注解 @RequirePermission
   ```java
   @Target({ElementType.METHOD, ElementType.TYPE})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface RequirePermission {
       String[] value() default {};
       String logical() default "OR"; // AND 或 OR
   }
   ```

2. 创建权限拦截器 PermissionInterceptor
   - 实现HandlerInterceptor
   - 从JWT Token或Redis获取用户权限
   - 验证用户是否有访问权限
   - 权限不足时抛出异常

3. 修改 SecurityConfig
   - 配置方法级安全控制
   - 启用@PreAuthorize注解
   - 配置自定义权限评估器

4. 创建权限评估器 CustomPermissionEvaluator
   - 实现hasPermission方法
   - 支持自定义权限判断逻辑

**权限控制策略：**
- 基于角色的访问控制（RBAC）
- 支持方法级权限控制
- 支持多个权限的AND/OR逻辑
- 权限不足时返回403 Forbidden

**验收标准：**
- 自定义注解使用正确
- 权限拦截器工作正常
- 权限验证准确无误
- 权限异常处理正确

---

#### 第八部分：初始化角色权限数据（0.5天）

**任务清单：**

1. 创建数据初始化SQL脚本
   - 创建初始化角色
     * 超级管理员（admin）
     * 教师（teacher）
     * 学生（student）
     * 仓库管理员（warehouse）
   
   - 创建初始化权限（按模块划分）
     * 用户管理权限（user:query、user:create、user:update、user:delete）
     * 角色管理权限（role:query、role:create、role:update、role:delete）
     * 权限管理权限（permission:query、permission:create、permission:update、permission:delete）
     * 耗材分类权限（category:query、category:create、category:update、category:delete）
     * 耗材信息权限（material:query、material:create、material:update、material:delete）
     * 入库管理权限（inbound:query、inbound:create、inbound:audit、inbound:delete）
     * 出库管理权限（outbound:query、outbound:apply、outbound:approve、outbound:out、outbound:delete）
     * 库存管理权限（inventory:query、inventory:check、inventory:adjust）
     * 数据大屏权限（dashboard:view）
     * 统计报表权限（statistics:view、statistics:export）
   
   - 创建角色权限关联
     * 超级管理员：所有权限
     * 教师：耗材查询、出库申请、数据大屏、统计报表
     * 学生：耗材查询、出库申请
     * 仓库管理员：耗材管理、入库管理、出库管理、库存管理、数据大屏、统计报表

2. 创建初始化Java类（可选）
   - 使用DatabaseInitializer自动执行初始化
   - 或在应用启动时执行SQL脚本

**初始数据示例：**

```sql
-- 初始化角色
INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('超级管理员', 'admin', '拥有系统所有权限', 1),
('教师', 'teacher', '教师角色，可查询和申请耗材', 1),
('学生', 'student', '学生角色，可查询和申请耗材', 1),
('仓库管理员', 'warehouse', '仓库管理员，管理耗材出入库', 1);

-- 初始化权限（示例）
INSERT INTO sys_permission (permission_name, permission_code, resource_type, parent_id, path, component, icon, sort_order, status) VALUES
-- 用户管理模块
('用户管理', 'user', 'MENU', 0, '/system/user', 'system/user/index', 'User', 100, 1),
('用户查询', 'user:query', 'BUTTON', 1, '', '', '', 101, 1),
('用户新增', 'user:create', 'BUTTON', 1, '', '', '', 102, 1),
('用户修改', 'user:update', 'BUTTON', 1, '', '', '', 103, 1),
('用户删除', 'user:delete', 'BUTTON', 1, '', '', '', 104, 1),

-- 角色管理模块
('角色管理', 'role', 'MENU', 0, '/system/role', 'system/role/index', 'UserFilled', 200, 1),
('角色查询', 'role:query', 'BUTTON', 6, '', '', '', 201, 1),
('角色新增', 'role:create', 'BUTTON', 6, '', '', '', 202, 1),
('角色修改', 'role:update', 'BUTTON', 6, '', '', '', 203, 1),
('角色删除', 'role:delete', 'BUTTON', 6, '', '', '', 204, 1),
('角色分配权限', 'role:assign', 'BUTTON', 6, '', '', '', 205, 1),

-- 权限管理模块
('权限管理', 'permission', 'MENU', 0, '/system/permission', 'system/permission/index', 'Lock', 300, 1),
('权限查询', 'permission:query', 'BUTTON', 11, '', '', '', 301, 1),
('权限新增', 'permission:create', 'BUTTON', 11, '', '', '', 302, 1),
('权限修改', 'permission:update', 'BUTTON', 11, '', '', '', 303, 1),
('权限删除', 'permission:delete', 'BUTTON', 11, '', '', '', 304, 1),

-- 更多权限...
;
```

**验收标准：**
- 初始数据创建完整
- 角色与权限关联正确
- 权限树形结构正确
- 数据库测试通过

---

#### 第九部分：测试与文档完善（1天）

**任务清单：**

1. 单元测试
   - 角色管理Service层单元测试
   - 权限管理Service层单元测试
   - 权限计算逻辑测试
   - 测试覆盖率 > 80%

2. 集成测试
   - 角色管理API接口测试
   - 权限管理API接口测试
   - 权限控制功能测试
   - 用户角色分配测试
   - 使用Postman或JMeter进行接口测试

3. 测试用例
   - 角色CRUD功能测试
   - 权限CRUD功能测试
   - 角色权限分配测试
   - 用户角色分配测试
   - 权限验证测试（有权限、无权限场景）
   - 权限树形结构测试
   - 并发操作测试

4. API文档验证
   - 使用Swagger UI访问接口文档
   - 验证所有接口文档是否完整
   - 验证接口参数说明是否清晰
   - 验证返回值示例是否正确

5. 开发文档更新
   - 更新plan.md开发进度
   - 编写角色权限管理模块开发文档
   - 编写权限控制使用说明
   - 更新README.md

6. 代码审查
   - 代码规范检查
   - 性能检查（SQL优化）
   - 安全检查（SQL注入、XSS）

**测试用例示例：**

```
1. 角色管理测试
   - 创建角色：正常创建
   - 创建角色：角色编码重复（应失败）
   - 更新角色：正常更新
   - 更新角色：角色不存在（应失败）
   - 删除角色：正常删除
   - 删除角色：角色有用户关联（应失败）
   - 分配权限：正常分配
   - 查询角色权限：正确返回

2. 权限管理测试
   - 创建权限：正常创建
   - 创建权限：权限编码重复（应失败）
   - 创建权限：父权限不存在（应失败）
   - 更新权限：正常更新
   - 删除权限：正常删除
   - 删除权限：有子权限（应失败）
   - 删除权限：有角色关联（应失败）
   - 查询权限树：树形结构正确

3. 权限控制测试
   - 用户有权限：能访问接口
   - 用户无权限：返回403 Forbidden
   - 多权限AND：所有权限都有才能访问
   - 多权限OR：任一权限有就能访问

4. 用户角色测试
   - 分配角色：正常分配
   - 查询用户角色：正确返回
   - 查询用户权限：包含所有角色的权限
```

**验收标准：**
- 所有单元测试通过
- 所有集成测试通过
- API文档完整正确
- 开发文档更新完毕
- 代码审查通过
- 测试覆盖率 > 80%

---

### 6.3 阶段二完成标准

**功能完整性：**
- ✅ 角色管理的CRUD功能完整
- ✅ 权限管理的CRUD功能完整
- ✅ 角色权限分配功能完整
- ✅ 用户角色分配功能完整
- ✅ 方法级权限控制功能完整
- ✅ 权限树形结构查询功能完整

**代码质量：**
- ✅ 代码规范符合团队标准
- ✅ 单元测试覆盖率 > 80%
- ✅ 异常处理完善
- ✅ 日志记录完整

**安全性：**
- ✅ 所有API接口有权限控制
- ✅ SQL注入防护有效
- ✅ XSS防护有效
- ✅ 敏感数据不返回

**文档完善：**
- ✅ API文档自动生成正确
- ✅ 开发文档更新及时
- ✅ 代码注释清晰完整

**性能要求：**
- ✅ 接口响应时间 < 2秒
- ✅ 权限树查询优化
- ✅ 数据库索引配置正确

---

### 6.4 阶段二完成后进入阶段三：耗材管理模块开发

**预计时间：5天**

**主要任务：**
- 仓库信息管理
- 供应商信息管理
- 库存管理基础功能
- 文件上传功能
- 前端页面开发
- 测试与文档

**具体计划将在阶段二完成时详细制定。**

---

## 规划总结

我已经为您完成了高职人工智能学院实训耗材管理系统的完整规划，包括以下内容：

### 📋 已完成的规划内容

**一、需求完善**
- 系统概述和目标
- 6大功能模块详细说明（基础管理、入库管理、出库管理、库存管理、数据大屏、统计报表）
- 非功能需求（性能、安全、可用性）
- 技术栈选型

**二、系统整体架构**
- 前后端分离架构设计
- 分层架构说明（前端层、后端层、数据层）
- 核心业务流程图

**三、数据库设计方案**
- 14张核心表设计（用户、部门、角色、权限、耗材分类、耗材信息、供应商、库存、入库单、入库明细、出库单、出库明细、归还记录、盘点单、盘点明细、操作日志、系统配置）
- 索引设计
- 数据库优化建议

**四、前后端模块划分**
- 前端目录结构（Vue 3 + Element Plus + ECharts + Pinia + Vue Router 4）
- 后端目录结构（Spring Boot 3.2+ + MyBatis-Plus + MySQL 8.0 + Redis 7.0+）
- RESTful API设计规范
- 详细接口列表

**五、开发计划和测试策略**
- 10个开发阶段详细任务划分
- 每个阶段的后端、前端、测试任务
- 测试类型和工具
- 测试用例示例
- 验收标准
- 42天开发时间表
- 安全考虑和防护措施
- 部署和维护指南

---

### 🎯 核心功能亮点

1. **完善的入库管理**：支持批量入库、多级审核流程、自动更新库存
2. **规范的出库管理**：领用申请、多级审批、出库执行、归还管理全流程管理
3. **详细的用途记录**：教学/科研/竞赛/其他四大类，支持课程、项目、竞赛名称详细记录
4. **智能库存管理**：库存预警、盘点管理、库存调拨、库存周转率分析
5. **数据大屏展示**：最常用Top5、库存最多Top5、库存最少Top5实时展示，多种图表可视化
6. **丰富的统计报表**：入库统计、出库统计、库存统计、用户使用统计，支持数据导出
7. **全面的安全防护**：JWT认证、RBAC权限控制、数据加密、接口限流等多重安全措施
8. **便捷的系统管理**：部门管理、供应商管理、系统配置、操作日志等功能

---

### ⏱️ 开发周期

**预计总开发时间：42个工作日（约8.5周）**

---

### **一、开发模式与流程**
1. **采用持续集成（CI）开发模式**，确保代码持续集成、自动化构建与测试。
2. **功能驱动开发**：每个功能模块开发完成后，必须经过充分测试与验证，确保无误后方可进入下一功能开发。
3. **迭代推进**：以功能为单位进行迭代，保证每个模块稳定可用。

---

### **二、测试策略**
1. **本地测试优先**：
   - 所有功能需在本地进行完整测试，包括单元测试、集成测试及界面交互测试。
   - 需使用与CI环境一致的测试工具和依赖版本。
2. **自动化测试**：
   - 针对关键路径与核心功能编写自动化测试脚本。
   - 每次提交前应在本地运行自动化测试套件。
3. **推送前验证**：
   - 确保本地测试全部通过后，才可将代码推送至GitHub。
4. **部署测试**：
   - 代码推送后触发CI流程，执行部署与线上环境测试，确保功能在目标环境中正常运行。

---

### **三、版本控制与部署**
1. **分支管理**：采用功能分支策略，每个新功能在独立分支开发，通过Pull Request合并至主分支。
2. **CI/CD流水线**：
   - 利用GitHub Actions等工具实现自动化测试与部署。
   - 部署成功后需在测试环境进行验证。
3. **回滚机制**：确保发现严重问题时能快速回退至上一稳定版本。

---

### **四、过程记录与文档**
1. **开发记录**：每个功能需记录：
   - 实现思路、关键代码变更、依赖调整。
   - 开发过程中遇到的问题与解决方案。
2. **测试记录**：
   - 测试工具、框架及版本。
   - 测试方法、用例设计、测试数据。
   - 测试结果（通过/失败）、截图或日志摘要。
   - 发现的缺陷及其修复状态。
3. **文档更新**：
   - 同步更新技术文档、API文档及用户手册。
   - 记录配置变更、环境依赖及部署步骤。

---

### **五、质量与协作要求**
1. **代码审查**：所有合并请求需经过至少一名成员审查。
2. **持续反馈**：定期同步开发与测试进展，及时调整计划。
3. **责任到人**：每个功能由开发者主导测试并确保质量，测试记录归档至项目Wiki或指定文档库。

---

通过以上规范，旨在形成**开发→测试→记录→集成**的闭环流程，确保交付质量，并为后续维护、BUG修复与优化提供完整依据。