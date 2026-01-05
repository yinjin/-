# 用户数据访问层开发教材

## 概述

本文档详细记录了高职人工智能学院实训耗材管理系统第二天工作"1.2 用户数据访问层"的完整开发过程。作为开发教材，本文档不仅记录了具体的操作步骤，还深入剖析了数据访问层设计的技术原理和最佳实践，帮助开发者理解企业级应用中数据访问的设计模式。

## 开发背景

### 为什么需要数据访问层？

在分层架构中，数据访问层（Data Access Layer）是连接业务逻辑层和数据存储的关键桥梁：

1. **数据抽象**：屏蔽底层数据存储细节，为上层提供统一的数据访问接口
2. **性能优化**：通过缓存、连接池等技术提升数据访问性能
3. **事务管理**：确保数据操作的原子性、一致性、隔离性和持久性
4. **异常处理**：统一处理数据访问异常，提供友好的错误信息
5. **代码复用**：避免重复的数据库操作代码，提高开发效率

### 数据访问层设计原则

1. **接口分离**：将数据访问逻辑与业务逻辑分离
2. **单一职责**：每个接口负责特定的数据访问功能
3. **依赖倒置**：上层依赖抽象接口，而非具体实现
4. **异常封装**：将底层异常转换为业务异常
5. **性能考虑**：合理使用索引、缓存、分页等优化手段

## 详细开发过程

### 第一步：创建MyBatis-Plus Mapper接口

#### 操作步骤：
1. 在 `mapper` 包下创建 `SysUserMapper.java` 接口
2. 继承 `BaseMapper<SysUser>` 获得基础CRUD方法
3. 添加自定义查询方法
4. 配置MyBatis注解

#### 代码实现：

```java
package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问层接口
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 自定义查询方法...
}
```

#### ⚠️ 重要：字段映射配置

**问题背景：**
在测试过程中发现，当实体类字段名与数据库列名不一致时，MyBatis-Plus无法自动映射，导致查询结果为null或插入失败。

**解决方案：**

**1. 使用 @TableField 注解明确指定映射关系**

在实体类中，对于不符合驼峰命名规范的字段，必须使用 `@TableField` 注解：

```java
@TableField("department_id")
private Long departmentId;
```

**2. 字段命名规范**

- **Java实体类字段**：使用驼峰命名法（camelCase）
  - 示例：`departmentId`, `createTime`, `updateTime`
  
- **数据库列名**：使用下划线命名法（snake_case）
  - 示例：`department_id`, `create_time`, `update_time`

**3. 自动映射规则**

MyBatis-Plus默认开启驼峰转下划线的自动映射：
- `departmentId` → `department_id` ✅ 自动映射
- `realName` → `real_name` ✅ 自动映射
- `department_id` → `department_id` ❌ 需要手动指定

**4. 常见字段映射示例**

```java
public class SysUser {
    private Long id;                    // id → id (自动映射)
    
    private String username;            // username → username (自动映射)
    
    @TableField("department_id")        // 必须指定
    private Long departmentId;
    
    @TableField("real_name")            // 如果数据库列名是 real_name
    private String name;                // Java字段名是 name
    
    private LocalDateTime createTime;   // create_time → create_time (自动映射)
    
    private LocalDateTime updateTime;   // update_time → update_time (自动映射)
}
```

**5. 配置文件设置**

在 `application.yml` 中确保开启驼峰转下划线：

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 开启驼峰转下划线
```

**6. 测试验证**

在开发完成后，务必进行字段映射测试：

```java
@Test
void testFieldMapping() {
    SysUser user = userMapper.selectById(1L);
    assertNotNull(user.getDepartmentId(), "departmentId字段映射失败");
    assertNotNull(user.getName(), "name字段映射失败");
}
```

#### 技术要点详解：

**1. BaseMapper继承的优势：**
```java
public interface SysUserMapper extends BaseMapper<SysUser>
```
- **自动CRUD**：无需编写基本的增删改查SQL
- **类型安全**：泛型参数确保类型安全
- **约定优于配置**：基于实体类自动映射表结构

**2. 自定义查询方法设计：**

**单表查询方法：**
```java
@Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
SysUser selectByUsername(@Param("username") String username);
```
- **SQL直写**：直接编写SQL语句，灵活性高
- **参数绑定**：使用`#{}`占位符防止SQL注入
- **逻辑删除**：统一添加`deleted = 0`条件

**分页查询方法：**
```java
@Select("<script>" +
        "SELECT * FROM sys_user WHERE deleted = 0" +
        "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%')</if>" +
        // 动态条件...
        " ORDER BY create_time DESC" +
        "</script>")
IPage<SysUser> selectUserPage(Page<SysUser> page, @Param("username") String username, ...);
```
- **动态SQL**：使用`<script>`标签支持动态条件
- **分页支持**：返回`IPage`对象，自动处理分页逻辑
- **条件组合**：支持多字段组合查询

**统计查询方法：**
```java
@Select("SELECT department_id, COUNT(*) as user_count FROM sys_user WHERE deleted = 0 AND department_id IS NOT NULL GROUP BY department_id")
List<UserDepartmentStats> selectUserCountByDepartment();
```
- **聚合查询**：使用GROUP BY进行分组统计
- **结果映射**：自定义内部类映射查询结果

**3. 批量操作方法：**
```java
int updateStatusBatch(@Param("userIds") List<Long> userIds,
                     @Param("status") Integer status,
                     @Param("updateBy") Long updateBy);
```
- **批量处理**：支持一次更新多个记录
- **参数传递**：使用List传递批量参数

#### ⚠️ 重要：批量操作异常处理

**问题背景：**
在批量更新用户状态时，如果用户ID列表中包含不存在的用户ID，会导致整个批量操作失败。

**解决方案：**

**1. 在Service层添加异常捕获**

```java
@Override
@Transactional
public void updateStatusBatch(List<Long> userIds, Integer status, Long updateBy) {
    if (userIds == null || userIds.isEmpty()) {
        throw new BusinessException(400, "用户ID列表不能为空");
    }
    
    int successCount = 0;
    int failCount = 0;
    
    for (Long userId : userIds) {
        try {
            SysUser user = userMapper.selectById(userId);
            if (user == null) {
                log.warn("用户不存在，跳过更新: userId={}", userId);
                failCount++;
                continue;
            }
            
            user.setStatus(status);
            user.setUpdateBy(updateBy);
            user.setUpdateTime(LocalDateTime.now());
            
            int result = userMapper.updateById(user);
            if (result > 0) {
                successCount++;
            } else {
                failCount++;
            }
        } catch (Exception e) {
            log.error("更新用户状态失败: userId={}, error={}", userId, e.getMessage());
            failCount++;
        }
    }
    
    log.info("批量更新用户状态完成: 成功={}, 失败={}", successCount, failCount);
    
    if (successCount == 0) {
        throw new BusinessException(400, "批量更新失败，所有用户都不存在");
    }
}
```

**2. 使用MyBatis-Plus的批量更新**

```java
@Override
@Transactional
public void updateStatusBatch(List<Long> userIds, Integer status, Long updateBy) {
    if (userIds == null || userIds.isEmpty()) {
        throw new BusinessException(400, "用户ID列表不能为空");
    }
    
    // 先查询存在的用户
    List<SysUser> users = userMapper.selectBatchIds(userIds);
    
    if (users.isEmpty()) {
        throw new BusinessException(400, "批量更新失败，所有用户都不存在");
    }
    
    // 过滤出实际存在的用户ID
    List<Long> existingUserIds = users.stream()
            .map(SysUser::getId)
            .collect(Collectors.toList());
    
    // 批量更新存在的用户
    users.forEach(user -> {
        user.setStatus(status);
        user.setUpdateBy(updateBy);
        user.setUpdateTime(LocalDateTime.now());
    });
    
    // 使用批量更新
    boolean success = this.updateBatchById(users);
    
    if (!success) {
        throw new BusinessException(500, "批量更新失败");
    }
    
    log.info("批量更新用户状态成功: 总数={}, 成功={}, 跳过={}", 
             userIds.size(), existingUserIds.size(), userIds.size() - existingUserIds.size());
}
```

**3. 最佳实践总结**

- **先查询后更新**：避免直接更新不存在的记录
- **异常捕获**：捕获单个记录的异常，不影响其他记录
- **日志记录**：记录成功和失败的详细信息
- **事务管理**：使用 `@Transactional` 确保数据一致性
- **用户反馈**：返回操作结果，告知用户成功和失败的数量

#### 设计模式应用：

**Repository模式**：
- 将数据访问逻辑封装在Repository中
- 提供统一的查询接口
- 便于单元测试和依赖注入

**Query Object模式**：
- 将查询条件封装为对象
- 支持复杂条件组合
- 提高查询的可读性和维护性

### 第二步：创建JPA Repository接口

#### 操作步骤：
1. 在 `mapper` 包下创建 `SysUserRepository.java` 接口
2. 继承 `JpaRepository` 和 `JpaSpecificationExecutor`
3. 使用方法命名规则定义查询方法
4. 添加自定义@Query方法

#### 代码实现：

```java
package com.haocai.management.mapper;

import com.haocai.management.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户JPA数据访问接口
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {
    // 查询方法定义...
}
```

#### 技术要点详解：

**1. 接口继承层次：**
```java
public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser>
```
- **JpaRepository**：提供基础CRUD和分页方法
- **JpaSpecificationExecutor**：支持动态查询规范
- **泛型参数**：`<实体类, 主键类型>`

**2. 方法命名规则查询：**
```java
Optional<SysUser> findByUsername(String username);
Optional<SysUser> findByEmailAndDeleted(String email, Integer deleted);
List<SysUser> findByDepartmentIdAndDeletedOrderByCreateTimeDesc(Long departmentId, Integer deleted);
```
- **findBy**：查询前缀
- **属性名**：实体类属性名
- **And/Or**：条件连接符
- **OrderBy**：排序关键字
- **返回类型**：Optional表示可能为空，List表示多个结果

**3. @Query自定义查询：**
```java
@Query("SELECT u FROM SysUser u WHERE u.deleted = :deleted AND " +
       "(u.username = :username OR u.email = :email OR u.phone = :phone)")
Optional<SysUser> findByUsernameOrEmailOrPhoneAndDeleted(
        @Param("username") String username,
        @Param("email") String email,
        @Param("phone") String phone,
        @Param("deleted") Integer deleted);
```
- **JPQL语法**：使用实体类和属性名，而非表名和列名
- **参数绑定**：使用`:参数名`和`@Param`注解
- **类型安全**：编译时检查语法正确性

**4. 更新操作方法：**
```java
@Modifying
@Query("UPDATE SysUser u SET u.status = :status, u.updateBy = :updateBy, u.updateTime = :updateTime " +
       "WHERE u.id IN :userIds AND u.deleted = 0")
int updateStatusBatch(@Param("userIds") List<Long> userIds,
                     @Param("status") Integer status,
                     @Param("updateBy") Long updateBy,
                     @Param("updateTime") LocalDateTime updateTime);
```
- **@Modifying**：标记为更新操作
- **批量更新**：使用IN子句批量处理
- **返回影响行数**：返回int类型表示影响的记录数

**5. 投影查询：**
```java
@Query("SELECT u.departmentId as departmentId, COUNT(u) as userCount " +
       "FROM SysUser u WHERE u.deleted = 0 AND u.departmentId IS NOT NULL " +
       "GROUP BY u.departmentId")
List<DepartmentUserCount> findUserCountByDepartment();
```
- **接口投影**：使用接口定义返回结果结构
- **性能优化**：只查询需要的字段

### 第三步：配置全局异常处理器

#### 操作步骤：
1. 在 `config` 包下创建 `GlobalExceptionHandler.java`
2. 添加 `@RestControllerAdvice` 注解
3. 实现各种异常的处理方法
4. 返回统一的错误响应格式

#### 代码实现：

```java
package com.haocai.management.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 异常处理方法...
}
```

#### ⚠️ 重要：类型转换异常处理

**问题背景：**
在测试过程中发现，当数据库中存储的 `status` 字段为字符串类型（如 "ACTIVE"），而实体类中定义为枚举类型时，会导致类型转换异常。

**解决方案：**

**1. 创建类型转换器**

```java
package com.haocai.management.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.haocai.management.entity.UserStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户状态类型转换器
 * 用于在数据库字符串和Java枚举之间进行转换
 */
@MappedTypes(UserStatus.class)
public class UserStatusConverter extends BaseTypeHandler<UserStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public UserStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : UserStatus.valueOf(value);
    }

    @Override
    public UserStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : UserStatus.valueOf(value);
    }

    @Override
    public UserStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : UserStatus.valueOf(value);
    }
}
```

**2. 在实体类中指定类型转换器**

```java
@TableField(value = "status", typeHandler = UserStatusConverter.class)
private UserStatus status;
```

**3. 在全局异常处理器中添加类型转换异常处理**

```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("参数异常", e);

    Map<String, Object> result = new HashMap<>();
    result.put("code", 400);
    result.put("message", "参数错误: " + e.getMessage());
    result.put("data", null);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
}

@ExceptionHandler(SQLException.class)
public ResponseEntity<Map<String, Object>> handleSQLException(SQLException e) {
    log.error("SQL异常", e);

    Map<String, Object> result = new HashMap<>();
    result.put("code", 500);
    result.put("message", "数据库操作失败: " + e.getMessage());
    result.put("data", null);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
}
```

**4. 配置WebMvcConfig注册转换器**

```java
package com.haocai.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 注册自定义类型转换器
        registry.addConverter(new UserStatusConverter());
    }
}
```

**5. 测试验证**

```java
@Test
void testUserStatusConversion() {
    // 测试字符串转枚举
    UserStatus status = UserStatus.valueOf("ACTIVE");
    assertEquals(UserStatus.ACTIVE, status);
    
    // 测试枚举转字符串
    String statusStr = status.name();
    assertEquals("ACTIVE", statusStr);
}
```

#### 异常处理策略详解：

**1. 数据访问异常处理：**
```java
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    log.error("数据完整性违反异常", e);

    Map<String, Object> result = new HashMap<>();
    result.put("code", 400);
    result.put("message", "数据完整性错误，请检查输入数据");
    result.put("data", null);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
}
```
- **异常分类**：按异常类型分别处理
- **日志记录**：记录详细错误信息用于调试
- **统一响应**：返回标准JSON格式的错误信息

**2. 业务异常处理：**
```java
@ExceptionHandler(BusinessException.class)
public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException e) {
    log.warn("业务异常: {}", e.getMessage());

    Map<String, Object> result = new HashMap<>();
    result.put("code", e.getCode());
    result.put("message", e.getMessage());
    result.put("data", null);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
}
```
- **业务语义**：保留业务异常的特定错误码
- **日志级别**：业务异常使用warn级别
- **错误传递**：直接返回业务异常信息

**3. 参数验证异常处理：**
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.error("参数验证异常", e);

    Map<String, String> errors = new HashMap<>();
    for (FieldError error : e.getBindingResult().getFieldErrors()) {
        errors.put(error.getField(), error.getDefaultMessage());
    }

    Map<String, Object> result = new HashMap<>();
    result.put("code", 400);
    result.put("message", "参数验证失败");
    result.put("data", errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
}
```
- **字段级错误**：收集所有字段验证错误
- **详细提示**：返回每个字段的具体错误信息
- **用户友好**：提供清晰的错误提示

### 第四步：创建业务异常类

#### 操作步骤：
1. 在 `exception` 包下创建 `BusinessException.java`
2. 定义异常属性和构造方法
3. 提供静态工厂方法创建常见异常

#### 代码实现：

```java
package com.haocai.management.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;
    private final String message;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    // 静态工厂方法...
    public static BusinessException userNotFound() {
        return new BusinessException(1001, "用户不存在");
    }

    public static BusinessException usernameExists() {
        return new BusinessException(1003, "用户名已存在");
    }
    // 更多工厂方法...
}
```

#### ⚠️ 重要：字段自动填充配置

**问题背景：**
在测试过程中发现，创建和更新用户时，`createTime`、`updateTime`、`createBy`、`updateBy` 等字段需要手动设置，容易遗漏导致数据不一致。

**解决方案：**

**1. 创建字段自动填充处理器**

```java
package com.haocai.management.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus字段自动填充处理器
 * 自动填充创建时间、更新时间、创建人、更新人等字段
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");
        
        // 自动填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充创建人（从当前登录用户获取）
        // 注意：这里需要根据实际的认证框架获取当前用户ID
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            this.strictInsertFill(metaObject, "createBy", Long.class, currentUserId);
            this.strictInsertFill(metaObject, "updateBy", Long.class, currentUserId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");
        
        // 自动填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充更新人
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            this.strictUpdateFill(metaObject, "updateBy", Long.class, currentUserId);
        }
    }
    
    /**
     * 获取当前登录用户ID
     * 注意：需要根据实际的认证框架实现
     */
    private Long getCurrentUserId() {
        // TODO: 从SecurityContext或Session中获取当前用户ID
        // 示例代码：
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if (authentication != null && authentication.isAuthenticated()) {
        //     return ((UserDetails) authentication.getPrincipal()).getId();
        // }
        return null;
    }
}
```

**2. 在实体类中添加自动填充注解**

```java
public class SysUser {
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
}
```

**3. 配置MyBatis-Plus启用自动填充**

在 `application.yml` 中配置：

```yaml
mybatis-plus:
  global-config:
    db-config:
      # 逻辑删除配置
      logic-delete-field: deleted  # 逻辑删除字段名
      logic-delete-value: 1        # 逻辑删除值（已删除）
      logic-not-delete-value: 0     # 逻辑未删除值（未删除）
```

**4. 测试验证**

```java
@Test
void testAutoFill() {
    SysUser user = new SysUser();
    user.setUsername("testuser");
    user.setPassword("password");
    
    userMapper.insert(user);
    
    assertNotNull(user.getCreateTime(), "createTime应该自动填充");
    assertNotNull(user.getUpdateTime(), "updateTime应该自动填充");
}
```

**5. 最佳实践总结**

- **自动填充**：避免手动设置时间字段，减少遗漏
- **审计追踪**：记录创建人和更新人，便于数据追溯
- **事务管理**：确保自动填充在事务中正确执行
- **日志记录**：记录填充过程，便于调试

#### 设计要点：

**1. 异常层次结构：**
- 继承RuntimeException，无需显式声明throws
- 便于业务层抛出异常
- 支持事务回滚

**2. 错误码设计：**
- 4位数字编码：1xxx-用户相关，2xxx-权限相关
- 便于前端根据错误码显示不同提示
- 支持国际化错误信息

**3. 静态工厂方法：**
- 提供常用异常的快捷创建方式
- 提高代码可读性
- 统一错误信息管理

## 双重数据访问策略

### 为什么需要两种ORM框架？

**MyBatis-Plus优势：**
- **SQL灵活性**：可以编写复杂的原生SQL
- **性能优化**：精确控制查询字段和条件
- **复杂查询**：支持多表联查和子查询
- **学习成本**：相对较低，易于上手

**JPA优势：**
- **声明式查询**：通过方法名自动生成SQL
- **类型安全**：编译时检查查询正确性
- **标准化**：遵循JPA规范，便于迁移
- **生态完善**：Spring Data生态支持

### 如何选择使用场景？

**使用MyBatis-Plus的情况：**
- 复杂多表联查
- 需要精确控制SQL性能
- 遗留系统集成
- 报表统计查询

**使用JPA的情况：**
- 标准CRUD操作
- 动态查询条件
- 快速原型开发
- 标准化团队开发

**最佳实践：**
- **核心业务**：使用JPA保证标准化
- **复杂查询**：使用MyBatis-Plus保证性能
- **统一接口**：通过Service层屏蔽底层实现差异

## 性能优化策略

### 1. 查询优化

**分页查询优化：**
```java
IPage<SysUser> selectUserPage(Page<SysUser> page, String username);
// MyBatis-Plus自动添加LIMIT子句
```

**索引优化：**
- username, email, phone建立唯一索引
- department_id, status建立普通索引
- create_time建立复合索引

**缓存策略：**
- 热点数据缓存（如用户信息）
- 查询结果缓存
- 二级缓存配置

### 2. 批量操作优化

**批量插入：**
```java
void insertBatch(List<SysUser> users);
// 使用批量插入减少数据库交互
```

**批量更新：**
```java
int updateStatusBatch(List<Long> userIds, Integer status);
// 使用IN子句批量更新
```

### 3. 连接池配置

**HikariCP配置：**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

## 异常处理最佳实践

### 1. 异常分类

**系统异常：**
- 数据库连接异常
- 网络异常
- 第三方服务异常

**业务异常：**
- 用户不存在
- 权限不足
- 参数错误

**用户异常：**
- 输入格式错误
- 重复提交
- 验证码错误

### 2. 异常处理层次

```
Controller层：参数验证异常
Service层：业务逻辑异常  
DAO层：数据访问异常
Global层：系统级异常兜底
```

### 3. 错误信息设计

**用户友好：**
- 使用通俗易懂的语言
- 提供具体的错误原因
- 给出解决建议

**开发友好：**
- 保留详细的堆栈信息
- 提供错误码便于定位
- 支持多语言错误信息

## 测试策略

### 单元测试

**Mapper层测试：**
```java
@SpringBootTest
class SysUserMapperTest {
    @Autowired
    private SysUserMapper userMapper;

    @Test
    void testSelectByUsername() {
        SysUser user = userMapper.selectByUsername("admin");
        assertThat(user).isNotNull();
    }
}
```

**Repository层测试：**
```java
@SpringBootTest
class SysUserRepositoryTest {
    @Autowired
    private SysUserRepository userRepository;

    @Test
    void testFindByUsername() {
        Optional<SysUser> user = userRepository.findByUsername("admin");
        assertThat(user).isPresent();
    }
}
```

### 集成测试

**异常处理测试：**
```java
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testBusinessException() throws Exception {
        mockMvc.perform(get("/api/test/business-error"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.code").value(1001));
    }
}
```

## 总结

用户数据访问层设计是企业级应用开发的核心环节。通过本教程的学习，开发者应该掌握：

- **双重ORM策略**：MyBatis-Plus和JPA的合理使用
- **异常处理体系**：分层异常处理和统一错误响应
- **性能优化技巧**：索引、分页、缓存等优化手段
- **测试驱动开发**：单元测试和集成测试的编写

这些技能和经验将为后续的业务功能开发提供坚实的数据访问基础。

---

*本文档记录了2026年1月7日的开发工作，版本控制提交ID：1bd2ffcd*
