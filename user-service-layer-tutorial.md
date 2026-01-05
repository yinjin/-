# 用户业务逻辑层开发教材

## 概述

本文档详细记录了高职人工智能学院实训耗材管理系统第二天工作"1.3 用户业务逻辑层"的完整开发过程。作为开发教材，本文档不仅记录了具体的操作步骤，还深入剖析了业务逻辑层设计的技术原理和最佳实践，帮助开发者理解企业级应用中业务逻辑的设计模式。

## 开发背景

### 为什么需要业务逻辑层？

在分层架构中，业务逻辑层（Business Logic Layer）是整个应用的"心脏"：

1. **业务规则封装**：将复杂的业务规则和逻辑封装在Service层
2. **事务管理**：确保业务操作的原子性、一致性、隔离性和持久性
3. **数据转换**：在DTO和Entity之间进行数据转换
4. **异常处理**：统一处理业务逻辑异常
5. **依赖注入**：通过依赖注入实现组件的松耦合
6. **单元测试**：便于对业务逻辑进行单元测试

### 业务逻辑层设计原则

1. **单一职责**：每个Service类负责特定的业务领域
2. **接口分离**：通过接口定义Service契约
3. **依赖倒置**：依赖抽象接口，而非具体实现
4. **事务边界**：合理划分事务边界，确保数据一致性
5. **异常封装**：将底层异常转换为业务异常
6. **参数验证**：在Service层进行业务参数验证

## 详细开发过程

### 第一步：创建用户登录日志实体类

#### 操作步骤：
1. 在 `entity` 包下创建 `SysUserLoginLog.java`
2. 定义登录日志字段和JPA映射
3. 配置MyBatis-Plus注解

#### 代码实现：

```java
package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用户登录日志实体类
 * 记录用户的登录行为，用于安全审计和统计分析
 */
@Data
@TableName("sys_user_login_log")
@Entity
@Table(name = "sys_user_login_log")
public class SysUserLoginLog {
    // 字段定义...
}
```

#### 设计要点详解：

**1. 日志字段设计：**
```java
@NotNull(message = "用户ID不能为空")
@TableField("user_id")
@Column(name = "user_id", nullable = false)
private Long userId;
```
- **用户ID**：关联登录用户
- **用户名冗余**：便于查询时显示
- **登录IP**：记录客户端IP地址
- **登录时间**：精确到秒的时间戳
- **登录结果**：成功/失败状态
- **失败原因**：失败时的具体原因

**2. 安全审计字段：**
```java
@TableField("user_agent")
@Column(name = "user_agent", length = 500)
private String userAgent;
```
- **User-Agent**：浏览器和设备信息
- **地理位置**：可选的地理位置信息
- **会话ID**：关联同一登录会话

### 第二步：创建用户登录日志Mapper接口

#### 操作步骤：
1. 在 `mapper` 包下创建 `SysUserLoginLogMapper.java`
2. 继承 `BaseMapper<SysUserLoginLog>`
3. 添加日志查询和统计方法

#### 代码实现：

```java
package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.entity.SysUserLoginLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户登录日志数据访问层接口
 */
public interface SysUserLoginLogMapper extends BaseMapper<SysUserLoginLog> {
    // 查询和统计方法...
}
```

#### 设计要点详解：

**1. 分页查询方法：**
```java
@Select("<script>" +
        "SELECT * FROM sys_user_login_log WHERE 1=1" +
        "<if test='userId != null'> AND user_id = #{userId}</if>" +
        // 动态条件...
        " ORDER BY login_time DESC" +
        "</script>")
IPage<SysUserLoginLog> selectLoginLogPage(Page<SysUserLoginLog> page, ...);
```
- **动态SQL**：支持多条件组合查询
- **时间范围**：支持按时间段筛选
- **分页支持**：返回IPage对象

**2. 统计方法：**
```java
@Select("SELECT COUNT(*) FROM sys_user_login_log " +
        "WHERE user_id = #{userId} AND login_success = true" +
        "<if test='startTime != null'> AND login_time >= #{startTime}</if>")
int countUserLoginTimes(@Param("userId") Long userId, ...);
```
- **登录次数统计**：统计用户成功登录次数
- **失败次数统计**：用于安全监控
- **时间范围过滤**：支持按时间段统计

### 第三步：配置密码加密

#### 操作步骤：
1. 在 `config` 包下创建 `PasswordConfig.java`
2. 配置BCrypt密码编码器Bean
3. 设置加密强度和参数

#### 代码实现：

```java
package com.haocai.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置类
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

#### 技术要点详解：

**1. BCrypt算法优势：**
- **自适应复杂度**：可以调整计算强度
- **内置盐值**：自动生成随机盐值
- **抗暴力破解**：计算复杂度高
- **标准实现**：被广泛验证的安全算法

**2. 密码加密流程：**
```java
// 注册时加密密码
String encodedPassword = passwordEncoder.encode(rawPassword);

// 登录时验证密码
boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
```
- **单向加密**：无法从密文还原明文
- **相同明文不同密文**：每次加密结果不同
- **验证匹配**：使用matches方法验证

### 第四步：创建JWT工具类

#### 操作步骤：
1. 在 `utils` 包下创建 `JwtUtils.java`
2. 实现token生成、解析、验证方法
3. 配置token过期时间

#### 代码实现：

```java
package com.haocai.management.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret:haocai-management-system-secret-key-2024-01-07-very-long-secret-key}")
    private String jwtSecret;

    @Value("${jwt.expiration:604800000}")
    private long jwtExpiration;

    // token生成、解析、验证方法...
}
```

#### 技术要点详解：

**1. JWT结构：**
```
Header.Payload.Signature
```
- **Header**：算法和token类型
- **Payload**：用户信息和声明
- **Signature**：签名验证

**2. Token生成：**
```java
public String generateToken(String username, Long userId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);
    return createToken(claims, username, jwtExpiration);
}
```
- **自定义声明**：添加用户ID等信息
- **过期时间**：设置合理的过期时间
- **签名算法**：使用HS256算法

**3. Token验证：**
```java
public Boolean validateToken(String token, String username) {
    try {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    } catch (Exception e) {
        log.warn("JWT token验证失败: {}", e.getMessage());
        return false;
    }
}
```
- **用户名匹配**：验证token中的用户名
- **过期检查**：检查token是否过期
- **异常处理**：捕获各种验证异常

### 第五步：创建用户Service接口

#### 操作步骤：
1. 在 `service` 包下创建 `ISysUserService.java`
2. 定义所有用户业务方法接口
3. 添加详细的JavaDoc注释

#### 代码实现：

```java
package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.UserLoginDTO;
import com.haocai.management.dto.UserRegisterDTO;
import com.haocai.management.dto.UserUpdateDTO;
import com.haocai.management.entity.SysUser;
import com.haocai.management.entity.UserStatus;

import java.util.List;

/**
 * 用户业务逻辑接口
 */
public interface ISysUserService {

    /**
     * 用户注册
     */
    SysUser register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     */
    String login(UserLoginDTO loginDTO);

    // 其他业务方法...
}
```

#### 设计要点详解：

**1. 方法命名规范：**
- **register**：用户注册
- **login**：用户登录
- **findByUsername**：根据用户名查找
- **updateUser**：更新用户信息
- **deleteUser**：删除用户

**2. 参数设计：**
- **DTO参数**：使用专门的DTO类
- **基本类型**：ID使用Long类型
- **枚举类型**：状态使用枚举
- **集合类型**：批量操作使用List

**3. 返回值设计：**
- **实体对象**：返回完整的用户信息
- **基本类型**：boolean表示操作结果
- **分页对象**：IPage封装分页结果
- **字符串**：JWT token

### 第六步：创建用户Service实现类

#### 操作步骤：
1. 在 `service.impl` 包下创建 `SysUserServiceImpl.java`
2. 实现所有接口方法
3. 添加事务管理和异常处理
4. 集成数据访问层

#### 代码实现：

```java
package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.UserLoginDTO;
import com.haocai.management.dto.UserRegisterDTO;
import com.haocai.management.dto.UserUpdateDTO;
import com.haocai.management.entity.SysUser;
import com.haocai.management.entity.SysUserLoginLog;
import com.haocai.management.entity.UserStatus;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.mapper.SysUserLoginLogMapper;
import com.haocai.management.mapper.SysUserMapper;
import com.haocai.management.mapper.SysUserRepository;
import com.haocai.management.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户业务逻辑实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements ISysUserService {
    // 依赖注入的Mapper和工具类...

    @Override
    @Transactional
    public SysUser register(UserRegisterDTO registerDTO) {
        // 注册逻辑实现...
    }

    @Override
    public String login(UserLoginDTO loginDTO) {
        // 登录逻辑实现...
    }

    // 其他方法实现...
}
```

#### 业务逻辑实现详解：

**1. 用户注册逻辑：**
```java
@Override
@Transactional
public SysUser register(UserRegisterDTO registerDTO) {
    // 1. 检查用户名是否已存在
    if (existsByUsername(registerDTO.getUsername())) {
        throw BusinessException.usernameExists();
    }

    // 2. 检查邮箱和手机号唯一性
    // 3. 创建用户对象并加密密码
    // 4. 保存到数据库
    // 5. 返回用户信息
}
```
- **唯一性检查**：用户名、邮箱、手机号
- **密码加密**：使用BCrypt加密
- **事务管理**：确保数据一致性
- **异常处理**：抛出业务异常

**2. 用户登录逻辑：**
```java
@Override
public String login(UserLoginDTO loginDTO) {
    // 1. 根据用户名查找用户
    SysUser user = findByUsername(loginDTO.getUsername());

    // 2. 检查用户状态
    if (user.getStatus() == UserStatus.DISABLED) {
        recordLoginLog(user.getId(), loginDTO.getIpAddress(), false, "用户已被禁用");
        throw BusinessException.userDisabled();
    }

    // 3. 验证密码
    if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
        recordLoginLog(user.getId(), loginDTO.getIpAddress(), false, "密码错误");
        throw BusinessException.passwordError();
    }

    // 4. 更新最后登录时间
    // 5. 记录登录成功日志
    // 6. 生成JWT token
}
```
- **状态检查**：禁用和锁定状态验证
- **密码验证**：使用BCrypt验证
- **日志记录**：成功/失败都记录日志
- **时间更新**：更新最后登录时间

**3. 用户信息更新逻辑：**
```java
@Override
@Transactional
public SysUser updateUser(Long userId, UserUpdateDTO updateDTO) {
    // 1. 检查用户是否存在
    SysUser existingUser = findById(userId);
    if (existingUser == null) {
        throw BusinessException.userNotFound();
    }

    // 2. 检查邮箱和手机号唯一性
    // 3. 更新用户信息
    // 4. 保存到数据库
    // 5. 返回更新后的信息
}
```
- **存在性检查**：确保用户存在
- **唯一性验证**：更新时的唯一性检查
- **部分更新**：只更新提供的字段

**4. 用户状态管理逻辑：**
```java
@Override
@Transactional
public boolean updateUserStatus(Long userId, UserStatus status, Long updateBy) {
    // 1. 检查用户是否存在
    // 2. 更新用户状态
    // 3. 记录操作日志
}
```
- **权限检查**：验证操作人权限
- **状态转换**：处理状态转换逻辑
- **日志记录**：记录状态变更

**5. 用户列表查询逻辑：**
```java
@Override
public IPage<SysUser> findUserPage(Page<SysUser> page, String username,
                                 String realName, UserStatus status, Long departmentId) {
    return sysUserMapper.selectUserPage(page, username, realName,
                                      status != null ? status.getCode() : null, departmentId);
}
```
- **多条件查询**：支持多种筛选条件
- **分页查询**：使用MyBatis-Plus分页
- **动态条件**：条件为空时不参与查询

## 事务管理策略

### 1. 声明式事务

**@Transactional注解：**
```java
@Override
@Transactional
public SysUser register(UserRegisterDTO registerDTO) {
    // 注册逻辑...
}
```
- **自动事务**：方法执行前开启事务
- **异常回滚**：运行时异常自动回滚
- **提交事务**：方法正常结束提交事务

### 2. 事务传播行为

**REQUIRED（默认）：**
- 如果当前存在事务，则加入该事务
- 如果当前没有事务，则创建一个新事务

**SUPPORTS：**
- 如果当前存在事务，则加入该事务
- 如果当前没有事务，则以非事务方式执行

### 3. 事务隔离级别

**READ_COMMITTED：**
- 避免脏读，保证读取已提交的数据
- 适用于大多数业务场景

**REPEATABLE_READ：**
- 避免不可重复读，保证多次读取结果一致
- MySQL默认隔离级别

## 异常处理体系

### 1. 业务异常分类

**用户相关异常：**
- `userNotFound()`：用户不存在
- `usernameExists()`：用户名已存在
- `userDisabled()`：用户已被禁用
- `userLocked()`：用户已被锁定

**权限相关异常：**
- `noPermission()`：操作无权限
- `paramError()`：参数错误

**数据相关异常：**
- `dataNotFound()`：数据不存在
- `operationFailed()`：操作失败

### 2. 异常处理层次

```
Controller层：参数绑定异常
Service层：业务逻辑异常
DAO层：数据访问异常
Global层：系统级异常兜底
```

### 3. 异常信息设计

**用户友好：**
- 使用通俗易懂的语言
- 提供具体的错误原因
- 给出解决建议

**开发友好：**
- 保留详细的错误码
- 支持国际化
- 便于前端处理

## 安全设计

### 1. 密码安全

**加密算法选择：**
- 使用BCrypt算法
- 设置合理的计算强度
- 自动生成随机盐值

**密码复杂度要求：**
- 最少8位，最大20位
- 必须包含大小写字母、数字、特殊字符

### 2. 登录安全

**失败处理：**
- 记录失败日志
- 累计失败次数
- 触发账户锁定

**会话管理：**
- JWT token有过期时间
- 支持token刷新
- 单点登录控制

### 3. 审计日志

**登录日志：**
- 记录每次登录尝试
- 包含IP地址和User-Agent
- 区分成功和失败

**操作日志：**
- 记录重要操作
- 包含操作人和时间
- 支持操作回溯

## 性能优化策略

### 1. 查询优化

**索引设计：**
- 用户名唯一索引
- 邮箱唯一索引
- 手机号唯一索引
- 状态和时间复合索引

**分页查询：**
- 使用MyBatis-Plus分页插件
- 避免大结果集查询
- 合理设置页面大小

### 2. 缓存策略

**用户信息缓存：**
- 热点用户信息缓存
- 缓存过期策略
- 缓存更新机制

**查询结果缓存：**
- 统计数据缓存
- 部门用户列表缓存

### 3. 批量操作

**批量更新：**
```java
int batchUpdateStatus(List<Long> userIds, UserStatus status, Long updateBy);
```
- 减少数据库交互次数
- 使用IN子句批量处理
- 控制批量大小

## 测试策略

### 单元测试

**Service层测试：**
```java
@SpringBootTest
class SysUserServiceImplTest {

    @Autowired
    private ISysUserService userService;

    @MockBean
    private SysUserMapper userMapper;

    @Test
    void testRegister() {
        // 模拟数据和验证逻辑
    }
}
```
- **Mock依赖**：模拟数据访问层
- **验证逻辑**：测试业务逻辑正确性
- **异常测试**：测试异常处理

### 集成测试

**完整流程测试：**
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTest {

    @Test
    void testUserRegisterAndLogin() {
        // 测试完整注册登录流程
    }
}
```
- **端到端测试**：测试完整业务流程
- **数据库测试**：验证数据持久化
- **事务测试**：验证事务回滚

## 总结

用户业务逻辑层设计是企业级应用开发的核心环节。通过本教程的学习，开发者应该掌握：

- **业务逻辑分层**：Service层的职责和设计原则
- **事务管理**：Spring事务的配置和使用
- **异常处理**：业务异常的定义和处理
- **安全设计**：密码加密和登录安全
- **性能优化**：查询优化和缓存策略

这些技能和经验将为后续的Controller层开发和前端集成提供坚实的基础。

---

*本文档记录了2026年1月7日的开发工作，版本控制提交ID：待定*