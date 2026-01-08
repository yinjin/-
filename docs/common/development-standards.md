# 高职人工智能学院实训耗材管理系统 - 开发规范

## 文档说明

本文档基于项目测试过程中发现的问题，总结了项目开发的技术规范和最佳实践，旨在帮助开发团队避免重复犯错，提高代码质量和开发效率。

**创建时间**：2026年1月5日  
**最后更新**：2026年1月7日  
**适用范围**：高职人工智能学院实训耗材管理系统后端开发  
**技术栈**：Spring Boot 3.1.6 + MyBatis-Plus + MySQL 8.0 + Vue 3 + Element Plus + Playwright

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

### 1.5 唯一索引与逻辑删除冲突处理

**问题场景**：
当表存在唯一索引且使用逻辑删除时，已逻辑删除的记录（deleted=1）仍然占用唯一键，导致重新插入相同唯一键值的数据时出现重复键错误。

**示例场景**：
```sql
-- sys_user_role表结构
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

-- 第一次插入
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1); -- 成功

-- 逻辑删除
UPDATE sys_user_role SET deleted=1 WHERE user_id=1 AND role_id=1; -- 成功

-- 第二次插入（失败！）
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1); 
-- 错误：Duplicate entry '1-1' for key 'sys_user_role.uk_user_role'
```

**解决方案**：
在重新插入相同唯一键值的数据前，必须物理删除已逻辑删除的记录。

**正确实现**：
```java
@Override
@Transactional(rollbackFor = Exception.class)
public ApiResponse<Void> assignRolesToUser(Long userId, List<Long> roleIds, HttpServletRequest request) {
    // 遵循：数据访问规范-第4条（唯一索引与逻辑删除冲突处理）
    
    // 删除用户原有的所有角色（包括已逻辑删除的记录）
    // 注意：由于存在唯一索引uk_user_role，需要物理删除已逻辑删除的记录，否则插入时会报重复键错误
    LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
    deleteWrapper.eq(SysUserRole::getUserId, userId);
    // 不添加deleted条件，物理删除所有记录（包括已逻辑删除的）
    userRoleMapper.delete(deleteWrapper);
    
    // 插入新的角色关联
    Long currentUserId = getCurrentUserId(request);
    for (Long roleId : roleIds) {
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setCreateTime(LocalDateTime.now());
        userRole.setCreateBy(currentUserId);
        userRoleMapper.insert(userRole);
    }
    
    return ApiResponse.success();
}
```

**错误实现**：
```java
// ❌ 错误做法：只逻辑删除
LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
deleteWrapper.eq(SysUserRole::getUserId, userId);
deleteWrapper.eq(SysUserRole::getDeleted, 0); // 只删除未逻辑删除的记录
userRoleMapper.delete(deleteWrapper);

// 问题：已逻辑删除的记录仍然存在，插入时会报重复键错误
```

**⚠️ 常见错误**：
- ❌ 只逻辑删除记录，导致唯一索引冲突
- ❌ 未意识到已逻辑删除的记录仍然占用唯一键
- ❌ 在关联表操作时未考虑唯一索引与逻辑删除的冲突

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

### 3.3 事务管理规范

**原则**：涉及多表操作的业务逻辑必须使用事务保证原子性

**正确实现**：
```java
@Override
@Transactional(rollbackFor = Exception.class)
public ApiResponse<Void> assignRolesToUser(Long userId, List<Long> roleIds, HttpServletRequest request) {
    // 遵循：事务管理规范-第2条（原子性操作）
    
    // 1. 删除用户原有角色
    userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
        .eq(SysUserRole::getUserId, userId));
    
    // 2. 插入新的角色关联
    for (Long roleId : roleIds) {
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setCreateTime(LocalDateTime.now());
        userRole.setCreateBy(getCurrentUserId(request));
        userRoleMapper.insert(userRole);
    }
    
    return ApiResponse.success();
}
```

**事务回滚策略**：
- 使用 `@Transactional(rollbackFor = Exception.class)` 确保任何异常都会触发回滚
- 角色分配涉及多表操作，必须保证原子性，避免部分成功导致数据不一致

**⚠️ 常见错误**：
- ❌ 未使用事务，导致删除和插入操作不在同一事务中
- ❌ 只回滚 RuntimeException，不回滚 Exception
- ❌ 事务范围过大，影响性能

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

## 五、前端开发规范

### 5.1 组件化开发规范

**原则**：使用Vue 3 + Element Plus进行组件化开发，遵循单一职责原则

**示例**：
```vue
<template>
  <div class="user-manage">
    <!-- 用户列表 -->
    <el-table :data="users" v-loading="loading">
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column label="角色">
        <template #default="{ row }">
          <el-tag
            v-for="role in row.roles"
            :key="role.id"
            type="success"
            size="small"
          >
            {{ role.roleName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 角色选择对话框 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="用户">
          <el-tag>{{ roleForm.username }}</el-tag>
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model="roleForm.roleIds"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
```

**⚠️ 常见错误**：
- ❌ 组件职责不清晰，一个组件包含过多功能
- ❌ 未使用Element Plus组件库，导致UI不统一
- ❌ 未使用响应式数据绑定，导致界面不更新

### 5.2 前端交互规范

**加载状态管理**：
```typescript
// 遵循：前端交互规范-第1条（加载状态管理）
const loading = ref(false)

const handleAssignRoles = async () => {
  try {
    loading.value = true
    await userApi.assignRoles(roleForm.userId, roleForm.roleIds)
    ElMessage.success('角色分配成功')
    await fetchUsers()
    roleDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '角色分配失败')
  } finally {
    loading.value = false
  }
}
```

**用户反馈**：
```typescript
// 遵循：前端交互规范-第2条（用户反馈）
// 操作成功
ElMessage.success('角色分配成功')

// 操作失败
ElMessage.error(error.response?.data?.message || '角色分配失败')
```

**数据刷新**：
```typescript
// 遵循：数据刷新规范-第1条（操作后刷新）
const handleAssignRoles = async () => {
  try {
    await userApi.assignRoles(roleForm.userId, roleForm.roleIds)
    ElMessage.success('角色分配成功')
    await fetchUsers() // 操作成功后立即刷新数据
    roleDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '角色分配失败')
  }
}
```

**⚠️ 常见错误**：
- ❌ 未使用加载状态，用户不知道操作是否在进行
- ❌ 未提供用户反馈，用户不知道操作是否成功
- ❌ 操作成功后未刷新数据，界面显示旧数据

### 5.3 错误处理规范

**API调用错误处理**：
```typescript
// 遵循：错误处理规范-第1条（错误提示）
const handleAssignRoles = async () => {
  try {
    loading.value = true
    await userApi.assignRoles(roleForm.userId, roleForm.roleIds)
    ElMessage.success('角色分配成功')
    await fetchUsers()
    roleDialogVisible.value = false
  } catch (error: any) {
    // 显示友好的错误提示
    ElMessage.error(error.response?.data?.message || '角色分配失败')
  } finally {
    loading.value = false
  }
}
```

**⚠️ 常见错误**：
- ❌ 未捕获异常，导致应用崩溃
- ❌ 未显示友好的错误提示，用户体验差
- ❌ 未在finally中重置加载状态，导致界面卡住

---

## 六、E2E测试规范

### 6.1 测试框架选择

**推荐工具**：Playwright
- 支持多浏览器（Chromium、Firefox、WebKit）
- 支持并行测试，提高测试效率
- 提供丰富的API，易于编写测试用例
- 自动等待机制，减少测试不稳定性

### 6.2 测试用例设计

**测试用例清单**：
1. **功能测试**：验证核心功能是否正常工作
2. **边界测试**：验证边界条件下的行为
3. **异常测试**：验证异常情况下的处理

**示例**：
```typescript
import { test, expect } from '@playwright/test';

test.describe('用户管理页面 - 角色功能测试', () => {
  test.beforeEach(async ({ page }) => {
    // 登录系统
    await page.goto('http://localhost:5173/login');
    await page.fill('input[name="username"]', 'admin');
    await page.fill('input[name="password"]', 'admin123');
    await page.click('button[type="submit"]');
    await page.waitForURL('http://localhost:5173/');
    
    // 导航到用户管理页面
    await page.click('text=用户管理');
    await page.waitForURL('http://localhost:5173/users');
  });

  test('应该显示用户的角色信息', async ({ page }) => {
    // 验证用户列表中显示角色标签
    const roleTags = await page.locator('.el-tag').all();
    expect(roleTags.length).toBeGreaterThan(0);
  });

  test('应该能够为用户分配角色', async ({ page }) => {
    // 点击编辑按钮
    await page.click('button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 选择角色
    await page.click('.el-select');
    await page.click('text=管理员');
    
    // 点击确定
    await page.click('button:has-text("确定")');
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 验证角色标签显示
    await expect(page.locator('text=管理员')).toBeVisible();
  });

  test('应该能够移除用户的角色', async ({ page }) => {
    // 点击编辑按钮
    await page.click('button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 取消选择角色
    await page.click('.el-select');
    await page.click('text=管理员');
    
    // 点击确定
    await page.click('button:has-text("确定")');
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 验证角色标签不显示
    await expect(page.locator('text=管理员')).not.toBeVisible();
  });
});
```

### 6.3 测试执行规范

**运行测试**：
```bash
# 运行所有测试
npx playwright test

# 运行特定测试文件
npx playwright test user-role-management.spec.ts

# 运行测试并显示浏览器
npx playwright test --headed

# 运行测试并生成报告
npx playwright test --reporter=html
```

**测试结果示例**：
```
Running 6 tests using 6 workers

[1/6] [chromium] › e2e\user-role-management.spec.ts:143:3 › 用户管理页面 - 角色功能测试 › 应该能够移除用户的角色
[2/6] [chromium] › e2e\user-role-management.spec.ts:25:3 › 用户管理页面 - 角色功能测试 › 应该显示用户的角色信息
[3/6] [chromium] › e2e\user-role-management.spec.ts:41:3 › 用户管理页面 - 角色功能测试 › 编辑用户时应该显示角色选择框
[4/6] [chromium] › e2e\user-role-management.spec.ts:183:3 › 用户管理页面 - 角色功能测试 › 应该显示无角色的用户
[5/6] [chromium] › e2e\user-role-management.spec.ts:81:3 › 用户管理页面 - 角色功能测试 › 应该能够为用户分配角色
[6/6] [chromium] › e2e\user-role-management.spec.ts:193:3 › 用户管理页面 - 角色功能测试 › 角色标签应该正确显示角色名称

  6 passed (7.1s)
```

**⚠️ 常见错误**：
- ❌ 测试用例覆盖不全，遗漏重要场景
- ❌ 未使用自动等待机制，导致测试不稳定
- ❌ 未并行执行测试，导致测试效率低

---

## 七、配置规范

### 7.1 MyBatis-Plus 配置

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

### 7.2 WebMvc 配置

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

## 八、开发流程规范

### 8.1 开发前准备

1. **数据库设计**：
   - 确认字段命名规范（下划线）
   - 确认字段类型（枚举使用VARCHAR）
   - 确认审计字段（create_time, update_time, create_by, update_by, deleted）
   - 确认索引设计
   - **特别注意**：如果表存在唯一索引且使用逻辑删除，需要考虑唯一索引与逻辑删除的冲突

2. **实体类设计**：
   - 使用驼峰命名法
   - 为不符合规范的字段添加 @TableField 注解
   - 为枚举类型实现类型转换器
   - 配置字段自动填充

3. **配置文件**：
   - 开启驼峰转下划线
   - 配置逻辑删除
   - 注册类型转换器

### 8.2 开发中检查

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

4. **唯一索引与逻辑删除检查**：
   - 确认表是否存在唯一索引
   - 确认是否使用逻辑删除
   - 如果两者都存在，确认是否需要物理删除已逻辑删除的记录

### 8.3 开发后测试

1. **单元测试**：
   - 测试字段映射
   - 测试类型转换
   - 测试批量操作

2. **集成测试**：
   - 测试完整的业务流程
   - 测试异常场景

3. **E2E测试**：
   - 使用Playwright编写E2E测试用例
   - 测试前端交互
   - 测试前后端集成

4. **性能测试**：
   - 测试批量操作性能
   - 测试查询性能

---

## 九、常见问题及解决方案

### 9.1 字段映射失败

**问题**：查询结果为null或插入失败

**原因**：字段名与数据库列名不一致

**解决方案**：
```java
@TableField("department_id")
private Long departmentId;
```

### 9.2 类型转换失败

**问题**：枚举类型无法正确映射

**原因**：未实现类型转换器

**解决方案**：
1. 实现 BaseTypeHandler
2. 在实体类中指定 typeHandler
3. 在 WebMvcConfig 中注册转换器

### 9.3 批量操作失败

**问题**：批量更新时，包含不存在的ID导致整个操作失败

**原因**：直接批量更新未检查记录是否存在

**解决方案**：
1. 先查询存在的记录
2. 只更新存在的记录
3. 返回详细的操作结果

### 9.4 审计字段为null

**问题**：create_time, update_time 等字段为null

**原因**：未配置字段自动填充

**解决方案**：
1. 实现 MetaObjectHandler
2. 在实体类中添加 @TableField(fill = ...) 注解
3. 在配置文件中启用自动填充

### 9.5 唯一索引与逻辑删除冲突

**问题**：重新插入相同唯一键值的数据时出现重复键错误

**原因**：已逻辑删除的记录仍然占用唯一键

**解决方案**：
1. 在重新插入前，物理删除已逻辑删除的记录
2. 不添加deleted条件，删除所有记录

**示例**：
```java
// 错误做法：只逻辑删除
userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
    .eq(SysUserRole::getUserId, userId)
    .eq(SysUserRole::getDeleted, 0)); // ❌ 已逻辑删除的记录仍然存在

// 正确做法：物理删除所有记录
userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
    .eq(SysUserRole::getUserId, userId)); // ✅ 删除所有记录
```

---

## 十、总结

### 核心原则

1. **命名规范**：数据库下划线，Java驼峰
2. **类型安全**：枚举类型必须实现转换器
3. **字段映射**：不一致的字段必须使用 @TableField
4. **批量操作**：先查询后更新，异常隔离
5. **审计追踪**：必须包含审计字段并配置自动填充
6. **异常处理**：分层处理，统一响应格式
7. **测试覆盖**：必须测试字段映射、类型转换、批量操作、E2E测试
8. **唯一索引处理**：存在唯一索引且使用逻辑删除时，必须物理删除已逻辑删除的记录
9. **前端交互**：使用加载状态、提供用户反馈、操作后刷新数据
10. **E2E测试**：使用Playwright编写完整的测试用例

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
- [ ] 编写E2E测试用例
- [ ] 检查唯一索引与逻辑删除的冲突
- [ ] 前端使用加载状态
- [ ] 前端提供用户反馈
- [ ] 前端操作后刷新数据

---

**文档维护**：本文档应根据项目开发过程中发现的新问题持续更新和完善。

**最后更新**：2026年1月7日
