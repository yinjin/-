# 角色权限数据访问层开发教程

## 概述

本文档详细记录了角色权限数据访问层的开发过程，包括四个Mapper接口的创建和异常处理配置。这些Mapper接口基于MyBatis-Plus框架，提供了完整的CRUD操作和自定义查询功能。

## 开发目标

根据day3-plan.md中的1.2任务要求，需要完成以下工作：

1. 创建角色Mapper接口 `SysRoleMapper`
2. 创建权限Mapper接口 `SysPermissionMapper`
3. 创建角色权限关联Mapper接口 `SysRolePermissionMapper`
4. 创建用户角色关联Mapper接口 `SysUserRoleMapper`
5. 配置数据访问层异常处理
6. 编译测试通过
7. 创建开发教程文档

## 技术栈

- **MyBatis-Plus 3.5.5**: ORM框架，提供BaseMapper基础CRUD方法
- **Spring Boot 3.1.6**: 后端框架
- **MySQL 8.0**: 数据库
- **RBAC权限模型**: 基于角色的访问控制

## 核心概念

### 1. MyBatis-Plus BaseMapper

所有Mapper接口都继承自`BaseMapper<T>`，自动获得以下基础CRUD方法：

- `insert(T entity)`: 插入记录
- `deleteById(Serializable id)`: 根据ID删除
- `updateById(T entity)`: 根据ID更新
- `selectById(Serializable id)`: 根据ID查询
- `selectList(Wrapper<T> queryWrapper)`: 查询列表
- `selectPage(IPage<T> page, Wrapper<T> queryWrapper)`: 分页查询

### 2. 逻辑删除

所有实体类都配置了逻辑删除功能，使用`@TableLogic`注解标记`deleted`字段：

- 删除操作会将`deleted`字段设置为1，而不是物理删除
- 所有查询自动添加`WHERE deleted = 0`条件
- 需要在自定义SQL中显式添加逻辑删除条件

### 3. 审计字段

所有实体类包含以下审计字段：

- `create_time`: 创建时间
- `update_time`: 更新时间
- `create_by`: 创建人
- `update_by`: 更新人
- `deleted`: 逻辑删除标记

这些字段通过`MyMetaObjectHandler`自动填充。

### 4. 注解说明

- `@Mapper`: 标识MyBatis Mapper接口
- `@Select`: 定义SQL查询语句
- `@Delete`: 定义SQL删除语句
- `@Param`: 参数绑定
- `<script>`: 动态SQL标签
- `<if>`: 条件判断标签

## Mapper接口详解

### 1. SysRoleMapper - 角色Mapper接口

**文件路径**: `backend/src/main/java/com/haocai/management/mapper/SysRoleMapper.java`

**继承关系**: `BaseMapper<SysRole>`

**功能说明**: 提供角色相关的数据访问操作，包括角色查询、统计等功能。

**核心方法**:

```java
// 根据角色编码查询角色
SysRole selectByRoleCode(@Param("roleCode") String roleCode);

// 根据状态查询角色列表
List<SysRole> selectByStatus(@Param("status") Integer status);

// 分页条件查询
IPage<SysRole> selectPageByCondition(
    Page<SysRole> page,
    @Param("roleName") String roleName,
    @Param("roleCode") String roleCode,
    @Param("status") Integer status
);

// 查询所有启用的角色
List<SysRole> selectAllEnabled();

// 根据角色名称模糊查询
List<SysRole> selectByRoleName(@Param("roleName") String roleName);

// 检查角色编码是否存在
Integer countByRoleCode(@Param("roleCode") String roleCode);

// 检查角色名称是否存在
Integer countByRoleName(@Param("roleName") String roleName);
```

**关键特性**:
- 所有查询都包含逻辑删除条件 `WHERE deleted = 0`
- 支持模糊查询和条件查询
- 提供唯一性检查方法，用于数据验证
- 支持分页查询

**SQL示例**:

```sql
-- 根据角色编码查询
SELECT id, role_name, role_code, description, status, 
       sort_order, create_time, update_time, create_by, update_by, deleted
FROM sys_role
WHERE role_code = #{roleCode}
  AND deleted = 0
LIMIT 1;

-- 分页条件查询
SELECT id, role_name, role_code, description, status, 
       sort_order, create_time, update_time, create_by, update_by, deleted
FROM sys_role
WHERE deleted = 0
  <if test="roleName != null and roleName != ''">
    AND role_name LIKE CONCAT('%', #{roleName}, '%')
  </if>
  <if test="roleCode != null and roleCode != ''">
    AND role_code LIKE CONCAT('%', #{roleCode}, '%')
  </if>
  <if test="status != null">
    AND status = #{status}
  </if>
ORDER BY sort_order ASC, create_time DESC;
```

### 2. SysPermissionMapper - 权限Mapper接口

**文件路径**: `backend/src/main/java/com/haocai/management/mapper/SysPermissionMapper.java`

**继承关系**: `BaseMapper<SysPermission>`

**功能说明**: 提供权限相关的数据访问操作，支持树形结构查询和关联查询。

**核心方法**:

```java
// 根据权限编码查询
SysPermission selectByPermissionCode(@Param("permissionCode") String permissionCode);

// 根据父权限ID查询子权限列表
List<SysPermission> selectByParentId(@Param("parentId") Long parentId);

// 查询所有顶级权限（parent_id = 0）
List<SysPermission> selectTopLevelPermissions();

// 根据权限类型查询
List<SysPermission> selectByType(@Param("type") String type);

// 根据状态查询
List<SysPermission> selectByStatus(@Param("status") Integer status);

// 查询所有启用的权限
List<SysPermission> selectAllEnabled();

// 根据权限名称模糊查询
List<SysPermission> selectByPermissionName(@Param("permissionName") String permissionName);

// 分页条件查询
IPage<SysPermission> selectPageByCondition(
    Page<SysPermission> page,
    @Param("permissionName") String permissionName,
    @Param("permissionCode") String permissionCode,
    @Param("type") String type,
    @Param("status") Integer status
);

// 检查权限编码是否存在
Integer countByPermissionCode(@Param("permissionCode") String permissionCode);

// 检查权限名称是否存在
Integer countByPermissionName(@Param("permissionName") String permissionName);

// 根据角色ID查询权限列表
List<SysPermission> selectByRoleId(@Param("roleId") Long roleId);

// 根据用户ID查询权限列表
List<SysPermission> selectByUserId(@Param("userId") Long userId);

// 查询权限树形结构
List<SysPermission> selectAllForTree();
```

**关键特性**:
- 支持树形结构查询（通过parent_id字段）
- 支持根据角色ID和用户ID查询权限列表
- 提供关联查询功能
- 支持多条件组合查询

**SQL示例**:

```sql
-- 根据角色ID查询权限列表
SELECT p.id, p.permission_name, p.permission_code, p.type, p.parent_id,
       p.path, p.icon, p.component, p.permission_value, p.sort_order,
       p.status, p.remark, p.create_time, p.update_time, p.create_by, p.update_by, p.deleted
FROM sys_permission p
INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
WHERE rp.role_id = #{roleId}
  AND p.deleted = 0
  AND rp.deleted = 0
ORDER BY p.sort_order ASC, p.id ASC;

-- 查询权限树形结构
SELECT id, permission_name, permission_code, type, parent_id,
       path, icon, component, permission_value, sort_order,
       status, remark, create_time, update_time, create_by, update_by, deleted
FROM sys_permission
WHERE deleted = 0
ORDER BY sort_order ASC, id ASC;
```

### 3. SysRolePermissionMapper - 角色权限关联Mapper接口

**文件路径**: `backend/src/main/java/com/haocai/management/mapper/SysRolePermissionMapper.java`

**继承关系**: `BaseMapper<SysRolePermission>`

**功能说明**: 管理角色与权限的多对多关联关系，提供批量操作和唯一性检查。

**核心方法**:

```java
// 根据角色ID查询权限ID列表
List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

// 根据权限ID查询角色ID列表
List<Long> selectRoleIdsByPermissionId(@Param("permissionId") Long permissionId);

// 根据角色ID查询关联列表
List<SysRolePermission> selectByRoleId(@Param("roleId") Long roleId);

// 根据权限ID查询关联列表
List<SysRolePermission> selectByPermissionId(@Param("permissionId") Long permissionId);

// 检查关联是否存在
Integer countByRoleIdAndPermissionId(
    @Param("roleId") Long roleId,
    @Param("permissionId") Long permissionId
);

// 根据角色ID删除关联
int deleteByRoleId(@Param("roleId") Long roleId);

// 根据权限ID删除关联
int deleteByPermissionId(@Param("permissionId") Long permissionId);

// 批量删除关联
int deleteByRoleIdAndPermissionIds(
    @Param("roleId") Long roleId,
    @Param("permissionIds") List<Long> permissionIds
);

// 统计角色的权限数量
Integer countPermissionsByRoleId(@Param("roleId") Long roleId);

// 统计权限被分配给多少个角色
Integer countRolesByPermissionId(@Param("permissionId") Long permissionId);

// 检查角色是否拥有指定权限
boolean hasPermission(
    @Param("roleId") Long roleId,
    @Param("permissionId") Long permissionId
);

// 根据角色ID列表查询所有权限ID
List<Long> selectPermissionIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

// 批量检查关联是否存在
List<Long> selectExistingPermissionIds(
    @Param("roleId") Long roleId,
    @Param("permissionIds") List<Long> permissionIds
);
```

**关键特性**:
- 支持批量操作（批量查询、批量删除）
- 提供唯一性检查方法
- 支持统计功能
- 数据库表有唯一约束：`UNIQUE KEY uk_role_permission (role_id, permission_id)`

**SQL示例**:

```sql
-- 批量删除关联
DELETE FROM sys_role_permission
WHERE role_id = #{roleId}
  AND permission_id IN
  <foreach collection="permissionIds" item="pid" open="(" separator="," close=")">
    #{pid}
  </foreach>
  AND deleted = 0;

-- 批量检查关联是否存在
SELECT permission_id
FROM sys_role_permission
WHERE role_id = #{roleId}
  AND permission_id IN
  <foreach collection="permissionIds" item="pid" open="(" separator="," close=")">
    #{pid}
  </foreach>
  AND deleted = 0;
```

### 4. SysUserRoleMapper - 用户角色关联Mapper接口

**文件路径**: `backend/src/main/java/com/haocai/management/mapper/SysUserRoleMapper.java`

**继承关系**: `BaseMapper<SysUserRole>`

**功能说明**: 管理用户与角色的多对多关联关系，提供批量操作、唯一性检查和关联查询。

**核心方法**:

```java
// 根据用户ID查询角色ID列表
List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

// 根据角色ID查询用户ID列表
List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

// 根据用户ID查询关联列表
List<SysUserRole> selectByUserId(@Param("userId") Long userId);

// 根据角色ID查询关联列表
List<SysUserRole> selectByRoleId(@Param("roleId") Long roleId);

// 检查关联是否存在
Integer countByUserIdAndRoleId(
    @Param("userId") Long userId,
    @Param("roleId") Long roleId
);

// 根据用户ID删除关联
int deleteByUserId(@Param("userId") Long userId);

// 根据角色ID删除关联
int deleteByRoleId(@Param("roleId") Long roleId);

// 批量删除关联
int deleteByUserIdAndRoleIds(
    @Param("userId") Long userId,
    @Param("roleIds") List<Long> roleIds
);

// 统计用户的角色数量
Integer countRolesByUserId(@Param("userId") Long userId);

// 统计角色拥有多少个用户
Integer countUsersByRoleId(@Param("roleId") Long roleId);

// 检查用户是否拥有指定角色
boolean hasRole(
    @Param("userId") Long userId,
    @Param("roleId") Long roleId
);

// 根据用户ID列表查询所有角色ID
List<Long> selectRoleIdsByUserIds(@Param("userIds") List<Long> userIds);

// 批量检查关联是否存在
List<Long> selectExistingRoleIds(
    @Param("userId") Long userId,
    @Param("roleIds") List<Long> roleIds
);

// 查询关联列表（包含角色信息）
List<SysUserRole> selectWithRoleInfoByUserId(@Param("userId") Long userId);

// 查询关联列表（包含用户信息）
List<SysUserRole> selectWithUserInfoByRoleId(@Param("roleId") Long roleId);
```

**关键特性**:
- 支持批量操作（批量查询、批量删除）
- 提供唯一性检查方法
- 支持统计功能
- 支持关联查询（查询时包含角色或用户信息）
- 数据库表有唯一约束：`UNIQUE KEY uk_user_role (user_id, role_id)`

**SQL示例**:

```sql
-- 查询关联列表（包含角色信息）
SELECT ur.id, ur.user_id, ur.role_id, ur.create_time, ur.update_time,
       r.role_name, r.role_code, r.description, r.status
FROM sys_user_role ur
INNER JOIN sys_role r ON ur.role_id = r.id
WHERE ur.user_id = #{userId}
  AND ur.deleted = 0
  AND r.deleted = 0
ORDER BY ur.create_time DESC;

-- 批量删除关联
DELETE FROM sys_user_role
WHERE user_id = #{userId}
  AND role_id IN
  <foreach collection="roleIds" item="rid" open="(" separator="," close=")">
    #{rid}
  </foreach>
  AND deleted = 0;
```

## 数据访问层异常处理

### DataAccessExceptionHandler

**文件路径**: `backend/src/main/java/com/haocai/management/config/DataAccessExceptionHandler.java`

**功能说明**: 使用`@RestControllerAdvice`注解的全局异常处理器，专门处理数据访问层的异常。

**处理的异常类型**:

1. **MyBatisPlusException**: MyBatis-Plus框架异常
2. **DataAccessException**: 数据访问异常（父类）
3. **DataIntegrityViolationException**: 数据完整性违反异常
4. **DuplicateKeyException**: 唯一键重复异常
5. **SQLException**: SQL异常
6. **QueryTimeoutException**: 查询超时异常
7. **OptimisticLockingFailureException**: 乐观锁异常
8. **PessimisticLockingFailureException**: 悲观锁异常
9. **InvalidDataAccessResourceUsageException**: 批量操作异常

**关键方法**:

```java
@ExceptionHandler(MyBatisPlusException.class)
public ResponseEntity<ApiResponse<Void>> handleMybatisPlusException(MyBatisPlusException ex) {
    log.error("MyBatis-Plus操作异常", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("数据库操作失败：" + ex.getMessage()));
}

@ExceptionHandler(DuplicateKeyException.class)
public ResponseEntity<ApiResponse<Void>> handleDuplicateKeyException(DuplicateKeyException ex) {
    log.error("唯一键重复异常", ex);
    String message = extractDuplicateKeyMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(message));
}
```

**错误消息解析**:

对于唯一键重复异常，会解析具体的重复字段信息：

```java
private String extractDuplicateKeyMessage(String errorMessage) {
    if (errorMessage.contains("uk_role_permission")) {
        return "该角色已拥有此权限，请勿重复分配";
    } else if (errorMessage.contains("uk_user_role")) {
        return "该用户已拥有此角色，请勿重复分配";
    } else if (errorMessage.contains("uk_role_code")) {
        return "角色编码已存在";
    } else if (errorMessage.contains("uk_permission_code")) {
        return "权限编码已存在";
    }
    return "数据已存在，请勿重复添加";
}
```

**返回格式**:

所有异常都返回统一的`ApiResponse`格式：

```json
{
  "code": 500,
  "message": "错误消息",
  "data": null
}
```

## 开发流程

### 步骤1: 创建SysRoleMapper

1. 在`backend/src/main/java/com/haocai/management/mapper/`目录下创建`SysRoleMapper.java`
2. 添加`@Mapper`注解
3. 继承`BaseMapper<SysRole>`
4. 根据业务需求添加自定义查询方法
5. 使用`@Select`注解定义SQL语句
6. 所有查询都添加逻辑删除条件`WHERE deleted = 0`

### 步骤2: 创建SysPermissionMapper

1. 创建`SysPermissionMapper.java`
2. 继承`BaseMapper<SysPermission>`
3. 添加树形结构查询方法（通过parent_id）
4. 添加关联查询方法（根据角色ID和用户ID查询）
5. 实现权限树形结构查询

### 步骤3: 创建SysRolePermissionMapper

1. 创建`SysRolePermissionMapper.java`
2. 继承`BaseMapper<SysRolePermission>`
3. 添加批量操作方法
4. 添加唯一性检查方法
5. 添加统计方法
6. 使用`<foreach>`标签实现批量SQL

### 步骤4: 创建SysUserRoleMapper

1. 创建`SysUserRoleMapper.java`
2. 继承`BaseMapper<SysUserRole>`
3. 添加批量操作方法
4. 添加唯一性检查方法
5. 添加统计方法
6. 添加关联查询方法（包含角色或用户信息）

### 步骤5: 配置异常处理

1. 创建`DataAccessExceptionHandler.java`
2. 添加`@RestControllerAdvice`注解
3. 为每种异常类型添加处理方法
4. 使用`@ExceptionHandler`注解指定异常类型
5. 实现友好的错误消息解析
6. 返回统一的`ApiResponse`格式

### 步骤6: 编译测试

执行Maven编译命令验证代码：

```bash
cd backend
mvn clean compile -DskipTests
```

编译成功后，检查是否有编译错误或警告。

### 步骤7: 创建文档

记录开发过程、设计决策和使用说明，形成开发教程文档。

## 最佳实践

### 1. SQL编写规范

- 所有查询都包含逻辑删除条件
- 使用参数化查询，避免SQL注入
- 模糊查询使用`LIKE CONCAT('%', #{param}, '%')`
- 排序使用`ORDER BY`子句
- 分页查询使用`Page`和`IPage`对象

### 2. 方法命名规范

- 查询方法以`select`开头
- 统计方法以`count`开头
- 删除方法以`delete`开头
- 检查方法以`has`或`count`开头
- 批量方法包含`Batch`或使用复数形式

### 3. 参数绑定

- 使用`@Param`注解明确指定参数名
- 参数名与SQL中的占位符保持一致
- 复杂参数使用对象传递

### 4. 动态SQL

- 使用`<script>`标签包裹动态SQL
- 使用`<if>`标签进行条件判断
- 使用`<foreach>`标签实现循环
- 注意空值判断和空字符串判断

### 5. 异常处理

- 提供友好的错误消息
- 记录详细的错误日志
- 返回适当的HTTP状态码
- 解析数据库约束错误

### 6. 性能优化

- 使用索引字段进行查询
- 避免全表扫描
- 使用批量操作减少数据库访问次数
- 合理使用缓存

## 测试建议

### 单元测试

为每个Mapper接口编写单元测试，覆盖以下场景：

1. 基础CRUD操作
2. 自定义查询方法
3. 批量操作
4. 唯一性检查
5. 统计功能
6. 异常场景

### 集成测试

测试Mapper接口与数据库的集成：

1. 测试SQL执行是否正确
2. 测试逻辑删除是否生效
3. 测试事务是否正确
4. 测试并发场景

### 测试覆盖率

确保单元测试覆盖率达到80%以上。

## 常见问题

### 1. 逻辑删除不生效

**问题**: 自定义SQL中没有添加逻辑删除条件

**解决**: 在所有自定义SQL中添加`WHERE deleted = 0`条件

### 2. 唯一键重复异常

**问题**: 插入或更新时违反唯一约束

**解决**: 在插入前调用唯一性检查方法，或捕获异常并处理

### 3. 批量操作性能问题

**问题**: 批量插入或删除速度慢

**解决**: 使用MyBatis-Plus的批量操作方法，或使用`<foreach>`标签优化SQL

### 4. 关联查询数据不完整

**问题**: 关联查询时缺少某些字段

**解决**: 确保JOIN条件正确，检查逻辑删除条件

### 5. 分页查询结果不准确

**问题**: 分页查询的总数或数据不正确

**解决**: 检查分页参数，确保SQL包含正确的条件

## 总结

角色权限数据访问层的开发已完成，包括：

1. ✅ 创建了SysRoleMapper接口，提供7个自定义查询方法
2. ✅ 创建了SysPermissionMapper接口，提供14个自定义查询方法
3. ✅ 创建了SysRolePermissionMapper接口，提供12个自定义查询方法
4. ✅ 创建了SysUserRoleMapper接口，提供14个自定义查询方法
5. ✅ 配置了DataAccessExceptionHandler，处理9种异常类型
6. ✅ 编译测试通过
7. ✅ 创建了开发教程文档

所有Mapper接口都继承自BaseMapper，提供了完整的CRUD操作和丰富的自定义查询功能。异常处理器能够处理各种数据访问异常，并提供友好的错误消息。

## 下一步工作

根据day3-plan.md，下一步将进行1.3任务：角色权限服务层开发，包括：

1. 创建角色Service接口和实现类
2. 创建权限Service接口和实现类
3. 创建角色权限关联Service接口和实现类
4. 创建用户角色关联Service接口和实现类
5. 编写业务逻辑
6. 编写单元测试
7. 创建开发教程文档

## 参考资料

- [MyBatis-Plus官方文档](https://baomidou.com/)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [MySQL官方文档](https://dev.mysql.com/doc/)
- [day3-plan.md](./day3-plan.md)
- [development-standards.md](../common/development-standards.md)
- [plan.md](../common/plan.md)
