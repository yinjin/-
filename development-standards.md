# 高职人工智能学院实训耗材管理系统 - 开发规范

## 文档说明

本文档基于项目测试过程中发现的问题，总结了项目开发的技术规范和最佳实践，旨在帮助开发团队避免重复犯错，提高代码质量和开发效率。

**创建时间**：2026年1月5日  
**适用范围**：高职人工智能学院实训耗材管理系统后端开发  
**技术栈**：Spring Boot 3.1.6 + MyBatis-Plus + MySQL 8.0

---

## 一、数据库设计规范

### 1.1 字段命名规范

**原则**：数据库字段使用下划线命名法（snake_case），Java实体类字段使用驼峰命名法（camelCase）

**示例**：
```sql
-- 数据库字段（下划线）
department_id
create_time
update_time
real_name
```

```java
// Java实体类字段（驼峰）
private Long departmentId;
private LocalDateTime createTime;
private LocalDateTime updateTime;
private String name;  // 对应数据库的 real_name
```

**⚠️ 常见错误**：
- ❌ 数据库字段使用驼峰命名：`departmentId`
- ❌ Java字段使用下划线命名：`department_id`
- ❌ 字段名不一致导致映射失败

### 1.2 字段类型规范

**枚举类型字段**：
- **数据库**：使用 VARCHAR 类型存储枚举名称
- **Java**：使用枚举类型
- **转换器**：必须实现类型转换器

**示例**：
```sql
-- 数据库定义
status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE'
```

```java
// Java枚举定义
public enum UserStatus {
    ACTIVE,
    INACTIVE
}

// 实体类字段
@TableField(value = "status", typeHandler = UserStatusConverter.class)
private UserStatus status;
```

**⚠️ 常见错误**：
- ❌ 数据库使用 TINYINT 存储枚举，导致类型转换失败
- ❌ 未实现类型转换器，导致查询和插入失败

### 1.3 审计字段规范

**必须包含的审计字段**：
```sql
create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
create_by BIGINT COMMENT '创建人ID',
update_by BIGINT COMMENT '更新人ID',
deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除'
```

**Java实体类配置**：
```java
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createTime;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updateTime;

@TableField(fill = FieldFill.INSERT)
private Long createBy;

@TableField(fill = FieldFill.INSERT_UPDATE)
private Long updateBy;

@TableLogic
private Integer deleted;
```

**⚠️ 常见错误**：
- ❌ 缺少审计字段，导致数据无法追溯
- ❌ 未配置自动填充，导致字段为null
- ❌ 未配置逻辑删除，导致数据无法软删除

### 1.4 索引规范

**必须创建的索引**：
```sql
-- 唯一索引
UNIQUE KEY uk_username (username)
UNIQUE KEY uk_email (email)

-- 普通索引
INDEX idx_department_id (department_id)
INDEX idx_status (status)
INDEX idx_deleted (deleted)

-- 复合索引（根据查询需求）
INDEX idx_status_create_time (status, create_time)
```

**⚠️ 常见错误**：
- ❌ 未为常用查询字段创建索引，导致查询性能差
- ❌ 未为唯一约束字段创建唯一索引，导致数据重复

---

## 二、实体类设计规范

### 2.1 字段映射规范

**原则**：对于不符合驼峰命名规范的字段，必须使用 `@TableField` 注解明确指定映射关系

**示例**：
```java
public class SysUser {
    private Long id;  // 自动映射：id → id
    
    private String username;  // 自动映射：username → username
    
    @TableField("department_id")  // 必须指定
    private Long departmentId;
    
    @TableField("real_name")  // 如果数据库列名是 real_name
    private String name;
    
    private LocalDateTime createTime;  // 自动映射：createTime → create_time
}
```

**⚠️ 常见错误**：
- ❌ 未使用 @TableField 注解，导致字段映射失败
- ❌ 字段名与数据库列名不一致，导致查询结果为null

### 2.2 类型转换器规范

**枚举类型必须实现类型转换器**：

```java
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

**实体类中使用**：
```java
@TableField(value = "status", typeHandler = UserStatusConverter.class)
private UserStatus status;
```

**⚠️ 常见错误**：
- ❌ 未实现类型转换器，导致枚举类型无法正确映射
- ❌ 未在实体类中指定 typeHandler，导致转换器不生效

### 2.3 字段自动填充规范

**必须实现 MetaObjectHandler**：

```java
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");
        
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            this.strictInsertFill(metaObject, "createBy", Long.class, currentUserId);
            this.strictInsertFill(metaObject, "updateBy", Long.class, currentUserId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");
        
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            this.strictUpdateFill(metaObject, "updateBy", Long.class, currentUserId);
        }
    }
    
    private Long getCurrentUserId() {
        // TODO: 从SecurityContext或Session中获取当前用户ID
        return null;
    }
}
```

**⚠️ 常见错误**：
- ❌ 未实现自动填充，导致审计字段为null
- ❌ 未在实体类中添加 @TableField(fill = ...) 注解
- ❌ 未在配置文件中启用自动填充

---

## 三、数据访问层规范

### 3.1 批量操作规范

**原则**：批量操作必须先查询存在的记录，避免直接操作不存在的记录导致失败

**正确实现**：
```java
@Override
@Transactional
public BatchUpdateResult batchUpdateStatus(List<Long> userIds, UserStatus status, Long updateBy) {
    if (userIds == null || userIds.isEmpty()) {
        throw new BusinessException(400, "用户ID列表不能为空");
    }
    
    int total = userIds.size();
    int success = 0;
    int failed = 0;
    
    // 先查询存在的用户
    List<SysUser> users = userMapper.selectBatchIds(userIds);
    
    if (users.isEmpty()) {
        throw new BusinessException(400, "批量更新失败，所有用户都不存在");
    }
    
    // 批量更新存在的用户
    for (SysUser user : users) {
        try {
            user.setStatus(status);
            user.setUpdateBy(updateBy);
            user.setUpdateTime(LocalDateTime.now());
            
            int result = userMapper.updateById(user);
            if (result > 0) {
                success++;
            } else {
                failed++;
            }
        } catch (Exception e) {
            log.error("更新用户状态失败: userId={}, error={}", user.getId(), e.getMessage());
            failed++;
        }
    }
    
    failed = total - success;
    
    log.info("批量更新用户状态完成: 总数={}, 成功={}, 失败={}", total, success, failed);
    
    return BatchUpdateResult.of(total, success, failed);
}
```

**⚠️ 常见错误**：
- ❌ 直接批量更新不存在的记录，导致整个操作失败
- ❌ 未捕获单个记录的异常，导致其他记录也无法更新
- ❌ 未返回详细的操作结果，用户无法知道哪些成功哪些失败

### 3.2 异常处理规范

**Service层异常处理**：
```java
try {
    // 业务逻辑
    return result;
} catch (BusinessException e) {
    log.warn("业务异常: {}", e.getMessage());
    throw e;  // 重新抛出业务异常
} catch (Exception e) {
    log.error("系统异常", e);
    throw new BusinessException(500, "系统内部错误");
}
```

**Mapper层异常处理**：
- Mapper层不处理异常，将异常抛给Service层处理
- 使用 try-catch 捕获单个记录的异常，不影响其他记录

**⚠️ 常见错误**：
- ❌ Service层吞掉异常，导致前端无法获取错误信息
- ❌ 未记录详细的错误日志，导致问题难以排查
- ❌ 异常处理层次混乱，职责不清

---

## 四、控制层规范

### 4.1 批量操作接口规范

**接口设计**：
```java
@PutMapping("/batch/status")
@Operation(summary = "批量更新用户状态", description = "批量启用或禁用多个用户")
public ApiResponse<Map<String, Object>> batchUpdateStatus(
        @RequestBody java.util.List<Long> userIds,
        @RequestParam UserStatus status,
        @RequestParam Long updateBy) {
    
    log.info("收到批量更新用户状态请求，用户ID数量: {}, 目标状态: {}", userIds.size(), status);
    
    try {
        // 参数验证
        if (userIds == null || userIds.isEmpty()) {
            return ApiResponse.error(400, "用户ID列表不能为空");
        }
        
        if (userIds.size() > 100) {
            return ApiResponse.error(400, "批量操作最多支持100个用户");
        }
        
        // 调用Service层批量更新
        BatchUpdateResult result = userService.batchUpdateStatus(userIds, status, updateBy);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("total", result.getTotal());
        response.put("success", result.getSuccess());
        response.put("failed", result.getFailed());
        response.put("status", status);
        response.put("message", String.format("批量更新完成：成功%d个，失败%d个", 
                result.getSuccess(), result.getFailed()));
        
        log.info("批量更新用户状态完成: {}", response);
        return ApiResponse.success(response);
        
    } catch (BusinessException e) {
        log.warn("批量更新用户状态业务异常: {}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    } catch (Exception e) {
        log.error("批量更新用户状态系统异常", e);
        return ApiResponse.error(500, "批量更新用户状态失败: " + e.getMessage());
    }
}
```

**⚠️ 常见错误**：
- ❌ 未限制批量操作的最大数量，可能导致性能问题
- ❌ 未返回详细的操作结果，用户无法知道操作详情
- ❌ 异常处理不完善，导致错误信息不友好

### 4.2 异常处理规范

**全局异常处理器**：
```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        ApiResponse<Void> response = ApiResponse.error(e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e) {
        log.error("参数验证异常", e);
        
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        ApiResponse<Map<String, String>> response = ApiResponse.error(400, "参数验证失败");
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("系统异常", e);
        ApiResponse<Void> response = ApiResponse.error(500, "系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

**⚠️ 常见错误**：
- ❌ 未实现全局异常处理器，导致异常信息不统一
- ❌ 未记录详细的错误日志，导致问题难以排查
- ❌ 错误响应格式不统一，前端难以处理

---

## 五、配置规范

### 5.1 MyBatis-Plus 配置

**application.yml 配置**：
```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 开启驼峰转下划线
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 开启SQL日志
  global-config:
    db-config:
      logic-delete-field: deleted  # 逻辑删除字段名
      logic-delete-value: 1        # 逻辑删除值（已删除）
      logic-not-delete-value: 0     # 逻辑未删除值（未删除）
```

**⚠️ 常见错误**：
- ❌ 未开启驼峰转下划线，导致字段映射失败
- ❌ 未配置逻辑删除，导致无法实现软删除

### 5.2 WebMvc 配置

**注册类型转换器**：
```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 注册自定义类型转换器
        registry.addConverter(new UserStatusConverter());
    }
}
```

**⚠️ 常见错误**：
- ❌ 未注册类型转换器，导致枚举类型无法正确转换
- ❌ 未实现 WebMvcConfigurer 接口，导致配置不生效

---

## 六、测试规范

### 6.1 字段映射测试

**必须测试的字段映射**：
```java
@Test
void testFieldMapping() {
    SysUser user = userMapper.selectById(1L);
    assertNotNull(user.getDepartmentId(), "departmentId字段映射失败");
    assertNotNull(user.getName(), "name字段映射失败");
    assertNotNull(user.getStatus(), "status字段映射失败");
}
```

### 6.2 批量操作测试

**必须测试的场景**：
```java
@Test
void testBatchUpdateStatus() {
    // 测试包含不存在的用户ID
    List<Long> userIds = Arrays.asList(1L, 2L, 3L, 999L, 1000L);
    
    ApiResponse<Map<String, Object>> response = 
        userController.batchUpdateStatus(userIds, UserStatus.INACTIVE, 1L);
    
    assertEquals(200, response.getCode());
    assertNotNull(response.getData());
    
    Map<String, Object> data = response.getData();
    assertEquals(5, data.get("total"));
    assertTrue((Integer)data.get("success") >= 3);
    assertTrue((Integer)data.get("failed") >= 2);
}
```

### 6.3 类型转换测试

**必须测试的类型转换**：
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

**⚠️ 常见错误**：
- ❌ 未测试字段映射，导致上线后才发现映射问题
- ❌ 未测试批量操作的异常场景，导致生产环境出错
- ❌ 未测试类型转换，导致枚举类型无法正确处理

---

## 七、开发流程规范

### 7.1 开发前准备

1. **数据库设计**：
   - 确认字段命名规范（下划线）
   - 确认字段类型（枚举使用VARCHAR）
   - 确认审计字段（create_time, update_time, create_by, update_by, deleted）
   - 确认索引设计

2. **实体类设计**：
   - 使用驼峰命名法
   - 为不符合规范的字段添加 @TableField 注解
   - 为枚举类型实现类型转换器
   - 配置字段自动填充

3. **配置文件**：
   - 开启驼峰转下划线
   - 配置逻辑删除
   - 注册类型转换器

### 7.2 开发中检查

1. **字段映射检查**：
   - 确认所有字段都能正确映射
   - 测试查询和插入操作

2. **类型转换检查**：
   - 确认枚举类型能正确转换
   - 测试数据库读写操作

3. **批量操作检查**：
   - 确认先查询后更新
   - 确认异常处理完善
   - 确认返回详细结果

### 7.3 开发后测试

1. **单元测试**：
   - 测试字段映射
   - 测试类型转换
   - 测试批量操作

2. **集成测试**：
   - 测试完整的业务流程
   - 测试异常场景

3. **性能测试**：
   - 测试批量操作性能
   - 测试查询性能

---

## 八、常见问题及解决方案

### 8.1 字段映射失败

**问题**：查询结果为null或插入失败

**原因**：字段名与数据库列名不一致

**解决方案**：
```java
@TableField("department_id")
private Long departmentId;
```

### 8.2 类型转换失败

**问题**：枚举类型无法正确映射

**原因**：未实现类型转换器

**解决方案**：
1. 实现 BaseTypeHandler
2. 在实体类中指定 typeHandler
3. 在 WebMvcConfig 中注册转换器

### 8.3 批量操作失败

**问题**：批量更新时，包含不存在的ID导致整个操作失败

**原因**：直接批量更新未检查记录是否存在

**解决方案**：
1. 先查询存在的记录
2. 只更新存在的记录
3. 返回详细的操作结果

### 8.4 审计字段为null

**问题**：create_time, update_time 等字段为null

**原因**：未配置字段自动填充

**解决方案**：
1. 实现 MetaObjectHandler
2. 在实体类中添加 @TableField(fill = ...) 注解
3. 在配置文件中启用自动填充

---

## 九、总结

### 核心原则

1. **命名规范**：数据库下划线，Java驼峰
2. **类型安全**：枚举类型必须实现转换器
3. **字段映射**：不一致的字段必须使用 @TableField
4. **批量操作**：先查询后更新，异常隔离
5. **审计追踪**：必须包含审计字段并配置自动填充
6. **异常处理**：分层处理，统一响应格式
7. **测试覆盖**：必须测试字段映射、类型转换、批量操作

### 开发检查清单

- [ ] 数据库字段使用下划线命名
- [ ] Java实体类字段使用驼峰命名
- [ ] 不一致的字段添加 @TableField 注解
- [ ] 枚举类型实现类型转换器
- [ ] 实体类包含审计字段
- [ ] 配置字段自动填充
- [ ] 配置逻辑删除
- [ ] 批量操作先查询后更新
- [ ] 实现全局异常处理器
- [ ] 编写单元测试和集成测试

---

**文档维护**：本文档应根据项目开发过程中发现的新问题持续更新和完善。

**最后更新**：2026年1月5日
