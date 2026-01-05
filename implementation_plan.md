# Implementation Plan

实现高职人工智能学院实训耗材管理系统的角色权限管理模块，完成RBAC权限控制系统的开发，包括角色管理、权限管理、角色权限分配、用户角色分配等功能。

本实现计划旨在完成系统基础管理模块中的角色权限管理部分，这是整个系统的核心安全基础。通过实现RBAC（基于角色的访问控制）模型，为后续所有业务模块提供细粒度的权限控制能力。该模块将支持菜单权限、按钮权限和接口权限三种类型，实现灵活的权限管理机制。

[Types]
角色权限管理模块涉及以下核心数据类型：

**SysRole（角色实体）**
- id: Long - 角色ID（主键）
- roleName: String - 角色名称（必填，最大50字符）
- roleCode: String - 角色编码（必填，唯一，最大50字符）
- description: String - 角色描述（可选，最大200字符）
- status: Integer - 状态（1正常，0禁用，默认1）
- createTime: LocalDateTime - 创建时间
- updateTime: LocalDateTime - 更新时间

**SysPermission（权限实体）**
- id: Long - 权限ID（主键）
- permissionName: String - 权限名称（必填，最大50字符）
- permissionCode: String - 权限编码（必填，唯一，最大100字符）
- menuId: Long - 菜单ID（可选）
- parentId: Long - 父权限ID（0表示顶级权限）
- level: Integer - 权限层级（默认1）
- type: Integer - 权限类型（1菜单权限，2按钮权限，3数据权限）
- sortOrder: Integer - 排序（默认0）
- status: Integer - 状态（1正常，0禁用，默认1）
- createTime: LocalDateTime - 创建时间

**SysRolePermission（角色权限关联实体）**
- id: Long - ID（主键）
- roleId: Long - 角色ID（必填）
- permissionId: Long - 权限ID（必填）
- createTime: LocalDateTime - 创建时间
- 唯一约束：roleId + permissionId

**SysUserRole（用户角色关联实体）**
- id: Long - ID（主键）
- userId: Long - 用户ID（必填）
- roleId: Long - 角色ID（必填）
- createTime: LocalDateTime - 创建时间
- 唯一约束：userId + roleId

**RoleCreateDTO（角色创建DTO）**
- roleName: String - 角色名称（@NotBlank）
- roleCode: String - 角色编码（@NotBlank, @Pattern）
- description: String - 角色描述（可选）

**RoleUpdateDTO（角色更新DTO）**
- id: Long - 角色ID（@NotNull）
- roleName: String - 角色名称（@NotBlank）
- roleCode: String - 角色编码（@NotBlank, @Pattern）
- description: String - 角色描述（可选）

**PermissionCreateDTO（权限创建DTO）**
- permissionName: String - 权限名称（@NotBlank）
- permissionCode: String - 权限编码（@NotBlank, @Pattern）
- menuId: Long - 菜单ID（可选）
- parentId: Long - 父权限ID（默认0）
- type: Integer - 权限类型（@NotNull, @Min, @Max）
- sortOrder: Integer - 排序（默认0）

**PermissionUpdateDTO（权限更新DTO）**
- id: Long - 权限ID（@NotNull）
- permissionName: String - 权限名称（@NotBlank）
- permissionCode: String - 权限编码（@NotBlank, @Pattern）
- menuId: Long - 菜单ID（可选）
- parentId: Long - 父权限ID
- type: Integer - 权限类型（@NotNull, @Min, @Max）
- sortOrder: Integer - 排序

**RoleVO（角色视图对象）**
- id: Long - 角色ID
- roleName: String - 角色名称
- roleCode: String - 角色编码
- description: String - 角色描述
- status: Integer - 状态
- createTime: LocalDateTime - 创建时间
- updateTime: LocalDateTime - 更新时间

**PermissionVO（权限视图对象）**
- id: Long - 权限ID
- permissionName: String - 权限名称
- permissionCode: String - 权限编码
- menuId: Long - 菜单ID
- parentId: Long - 父权限ID
- level: Integer - 权限层级
- type: Integer - 权限类型
- typeDesc: String - 权限类型描述
- sortOrder: Integer - 排序
- status: Integer - 状态
- createTime: LocalDateTime - 创建时间
- children: List<PermissionVO> - 子权限列表（树形结构）

[Files]
需要创建和修改的文件清单：

**新创建的文件：**
1. backend/src/main/java/com/material/system/service/SysPermissionService.java - 权限Service接口
2. backend/src/main/java/com/material/system/service/impl/SysPermissionServiceImpl.java - 权限Service实现类
3. backend/src/main/java/com/material/system/controller/SysRoleController.java - 角色管理Controller
4. backend/src/main/java/com/material/system/controller/SysPermissionController.java - 权限管理Controller
5. backend/src/main/java/com/material/system/annotation/RequirePermission.java - 自定义权限注解
6. backend/src/main/java/com/material/system/aspect/PermissionAspect.java - 权限切面
7. backend/src/main/java/com/material/system/config/PermissionConfig.java - 权限配置类
8. backend/src/main/resources/db/init_role_permission.sql - 角色权限初始化数据脚本

**需要修改的文件：**
1. backend/src/main/java/com/material/system/entity/SysUser.java - 添加roles字段
2. backend/src/main/java/com/material/system/service/SysUserService.java - 添加角色相关方法
3. backend/src/main/java/com/material/system/service/impl/SysUserServiceImpl.java - 实现角色相关方法
4. backend/src/main/java/com/material/system/controller/SysUserController.java - 添加角色分配接口
5. backend/src/main/java/com/material/system/config/SecurityConfig.java - 配置方法级权限控制
6. backend/src/main/java/com/material/system/common/ResultCode.java - 添加角色权限相关错误码
7. plan.md - 更新开发进度文档

**需要删除的文件：**
无

**配置文件更新：**
1. backend/src/main/resources/application.yml - 无需修改
2. backend/pom.xml - 无需修改（依赖已完整）

[Functions]
需要创建和修改的函数清单：

**新创建的函数：**

**SysPermissionService接口：**
- createPermission(PermissionCreateDTO dto): Long - 创建权限
- updatePermission(PermissionUpdateDTO dto): void - 更新权限
- deletePermission(Long id): void - 删除权限
- getPermissionById(Long id): PermissionVO - 根据ID查询权限
- getPermissionTree(): List<PermissionVO> - 获取权限树形结构
- getPermissionList(Integer current, Integer size, String permissionName, Integer type): Page<PermissionVO> - 分页查询权限列表
- getPermissionsByRole(Long roleId): List<PermissionVO> - 根据角色查询权限

**SysPermissionServiceImpl实现类：**
- 实现上述所有接口方法
- buildPermissionTree(List<PermissionVO> permissions): List<PermissionVO> - 构建权限树（私有方法）

**SysRoleController：**
- createRole(@RequestBody RoleCreateDTO dto): Result<Long> - 创建角色
- updateRole(@RequestBody RoleUpdateDTO dto): Result<Void> - 更新角色
- deleteRole(@PathVariable Long id): Result<Void> - 删除角色
- getRoleById(@PathVariable Long id): Result<RoleVO> - 获取角色详情
- getRolePage(@RequestParam Integer current, @RequestParam Integer size, @RequestParam(required = false) String roleName): Result<Page<RoleVO>> - 分页查询角色列表
- getAllRoles(): Result<List<RoleVO>> - 获取所有角色
- assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds): Result<Void> - 分配权限
- getRolePermissions(@PathVariable Long roleId): Result<List<Long>> - 获取角色权限

**SysPermissionController：**
- createPermission(@RequestBody PermissionCreateDTO dto): Result<Long> - 创建权限
- updatePermission(@RequestBody PermissionUpdateDTO dto): Result<Void> - 更新权限
- deletePermission(@PathVariable Long id): Result<Void> - 删除权限
- getPermissionById(@PathVariable Long id): Result<PermissionVO> - 获取权限详情
- getPermissionTree(): Result<List<PermissionVO>> - 获取权限树
- getPermissionList(@RequestParam Integer current, @RequestParam Integer size, @RequestParam(required = false) String permissionName, @RequestParam(required = false) Integer type): Result<Page<PermissionVO>> - 分页查询权限列表

**需要修改的函数：**

**SysUserService接口：**
- 新增：assignRoles(Long userId, List<Long> roleIds): void - 分配角色
- 新增：getUserRoles(Long userId): List<RoleVO> - 获取用户角色
- 新增：getUserPermissions(Long userId): List<String> - 获取用户权限编码列表

**SysUserServiceImpl实现类：**
- 实现上述新增方法
- 修改login方法：登录时加载用户权限信息

**SysUserController：**
- 新增：assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds): Result<Void> - 分配角色
- 新增：getUserRoles(@PathVariable Long userId): Result<List<RoleVO>> - 获取用户角色
- 新增：getUserPermissions(@PathVariable Long userId): Result<List<String>> - 获取用户权限

**SecurityConfig：**
- 修改configure方法：配置方法级安全控制
- 新增：customPermissionEvaluator() - 自定义权限评估器

**需要删除的函数：**
无

[Classes]
需要创建和修改的类清单：

**新创建的类：**

**SysPermissionService（接口）**
- 包路径：com.material.system.service
- 作用：定义权限管理业务接口
- 关键方法：createPermission、updatePermission、deletePermission、getPermissionTree等

**SysPermissionServiceImpl（实现类）**
- 包路径：com.material.system.service.impl
- 作用：实现权限管理业务逻辑
- 继承：ServiceImpl<SysPermissionMapper, SysPermission>
- 实现：SysPermissionService
- 依赖：SysPermissionMapper、SysRolePermissionMapper
- 关键方法：buildPermissionTree（递归构建权限树）

**SysRoleController（控制器）**
- 包路径：com.material.system.controller
- 作用：提供角色管理REST API
- 注解：@RestController、@RequestMapping("/api/role")、@Tag
- 依赖：SysRoleService
- 关键方法：createRole、updateRole、deleteRole、assignPermissions等

**SysPermissionController（控制器）**
- 包路径：com.material.system.controller
- 作用：提供权限管理REST API
- 注解：@RestController、@RequestMapping("/api/permission")、@Tag
- 依赖：SysPermissionService
- 关键方法：createPermission、updatePermission、deletePermission、getPermissionTree等

**RequirePermission（注解）**
- 包路径：com.material.system.annotation
- 作用：自定义权限控制注解
- 注解类型：@Target({ElementType.METHOD, ElementType.TYPE})
- 保留策略：@Retention(RetentionPolicy.RUNTIME)
- 属性：String[] value() - 权限编码数组；String logical() - 逻辑关系（AND/OR）

**PermissionAspect（切面）**
- 包路径：com.material.system.aspect
- 作用：权限拦截切面
- 注解：@Aspect、@Component
- 关键方法：checkPermission - 权限验证逻辑

**PermissionConfig（配置类）**
- 包路径：com.material.system.config
- 作用：权限配置类
- 注解：@Configuration
- 关键方法：customPermissionEvaluator - 自定义权限评估器

**需要修改的类：**

**SysUser（实体类）**
- 包路径：com.material.system.entity
- 修改内容：添加roles字段（List<SysRole>类型，@TableField(exist = false)）

**SysUserService（接口）**
- 包路径：com.material.system.service
- 修改内容：添加角色相关方法声明

**SysUserServiceImpl（实现类）**
- 包路径：com.material.system.service.impl
- 修改内容：实现角色相关方法，修改登录逻辑

**SysUserController（控制器）**
- 包路径：com.material.system.controller
- 修改内容：添加角色分配相关接口

**SecurityConfig（配置类）**
- 包路径：com.material.system.config
- 修改内容：配置方法级安全控制，启用@PreAuthorize注解

**ResultCode（枚举类）**
- 包路径：com.material.system.common
- 修改内容：添加角色权限相关错误码（ROLE_NOT_EXIST、ROLE_CODE_ALREADY_EXIST、PERMISSION_NOT_EXIST、PERMISSION_CODE_ALREADY_EXIST等）

**需要删除的类：**
无

[Dependencies]
本实现计划不需要添加新的依赖，现有依赖已满足需求。

**现有依赖清单：**
- Spring Boot 3.2+ - 核心框架
- Spring Security 6.2+ - 安全框架
- MyBatis-Plus 3.5.5+ - ORM框架
- MySQL 8.0+ - 数据库
- Redis 7.0+ - 缓存
- JWT (jjwt 0.12+) - Token认证
- SpringDoc OpenAPI 2.3+ - API文档
- Lombok - 简化代码
- Validation API - 参数校验

**依赖版本要求：**
- 所有依赖版本保持与现有项目一致
- 无需升级或降级

**集成要求：**
- 角色权限模块与现有用户管理模块集成
- 权限控制与Spring Security集成
- 权限数据与JWT Token集成

[Testing]
测试策略和测试用例要求：

**单元测试：**
- 测试文件：backend/src/test/java/com/material/system/SysRoleServiceTest.java
- 测试文件：backend/src/test/java/com/material/system/SysPermissionServiceTest.java
- 测试覆盖率要求：>80%
- 测试框架：JUnit 5 + Mockito

**集成测试：**
- 测试文件：backend/src/test/java/com/material/system/RolePermissionIntegrationTest.java
- 测试内容：角色权限管理全流程测试
- 测试工具：Postman或JMeter

**测试用例清单：**

**角色管理测试用例：**
1. 创建角色 - 正常流程
2. 创建角色 - 角色编码重复（应失败）
3. 更新角色 - 正常更新
4. 更新角色 - 角色不存在（应失败）
5. 删除角色 - 正常删除
6. 删除角色 - 角色有用户关联（应失败）
7. 分配权限 - 正常分配
8. 查询角色权限 - 正确返回
9. 分页查询角色 - 正常分页
10. 获取所有角色 - 返回启用状态的角色

**权限管理测试用例：**
1. 创建权限 - 正常创建
2. 创建权限 - 权限编码重复（应失败）
3. 创建权限 - 父权限不存在（应失败）
4. 更新权限 - 正常更新
5. 删除权限 - 正常删除
6. 删除权限 - 有子权限（应失败）
7. 删除权限 - 有角色关联（应失败）
8. 查询权限树 - 树形结构正确
9. 分页查询权限 - 正常分页
10. 根据角色查询权限 - 正确返回

**权限控制测试用例：**
1. 用户有权限 - 能访问接口
2. 用户无权限 - 返回403 Forbidden
3. 多权限AND - 所有权限都有才能访问
4. 多权限OR - 任一权限有就能访问
5. 角色权限继承 - 用户继承角色权限
6. 权限缓存 - Redis缓存生效

**用户角色测试用例：**
1. 分配角色 - 正常分配
2. 查询用户角色 - 正确返回
3. 查询用户权限 - 包含所有角色的权限
4. 用户登录 - 加载权限信息
5. Token刷新 - 权限信息保持

**性能测试：**
- 权限树查询响应时间 < 1秒
- 权限验证响应时间 < 100ms
- 并发100用户权限验证正常

**安全测试：**
- SQL注入防护测试
- XSS防护测试
- 权限绕过测试
- 未授权访问测试

**验收标准：**
- 所有单元测试通过
- 所有集成测试通过
- 测试覆盖率 > 80%
- 性能指标达标
- 无安全漏洞

[Implementation Order]
按照以下顺序实现角色权限管理模块，确保依赖关系正确，避免冲突：

1. **完善DTO和VO类（0.5天）**
   - 创建PermissionCreateDTO类，添加参数校验注解
   - 创建PermissionUpdateDTO类，添加参数校验注解
   - 创建PermissionVO类，添加typeDesc字段和children字段
   - 验证所有DTO和VO类编译通过

2. **实现权限管理Service层（1天）**
   - 创建SysPermissionService接口，定义所有业务方法
   - 创建SysPermissionServiceImpl实现类
   - 实现权限CRUD基本功能（创建、更新、删除、查询）
   - 实现权限树形结构构建逻辑（递归方法buildPermissionTree）
   - 实现根据角色查询权限逻辑
   - 添加业务异常处理（权限编码重复、父权限不存在等）
   - 添加事务控制（@Transactional）
   - 编写单元测试，确保测试覆盖率 > 80%

3. **实现角色管理Controller层（0.5天）**
   - 创建SysRoleController类，添加@RestController和@RequestMapping注解
   - 实现角色CRUD接口（创建、更新、删除、查询）
   - 实现角色权限分配接口（assignPermissions、getRolePermissions）
   - 添加Swagger文档注解（@Tag、@Operation、@Parameter）
   - 添加权限控制注解（@PreAuthorize）
   - 验证所有接口返回格式统一（Result包装）
   - 使用Postman测试所有接口

4. **实现权限管理Controller层（0.5天）**
   - 创建SysPermissionController类，添加@RestController和@RequestMapping注解
   - 实现权限CRUD接口（创建、更新、删除、查询）
   - 实现权限树查询接口（getPermissionTree）
   - 添加Swagger文档注解
   - 添加权限控制注解
   - 验证权限树接口返回格式正确
   - 使用Postman测试所有接口

5. **集成用户服务角色功能（0.5天）**
   - 修改SysUser实体类，添加roles字段（@TableField(exist = false)）
   - 扩展SysUserService接口，添加角色相关方法声明
   - 实现SysUserServiceImpl新增方法（assignRoles、getUserRoles、getUserPermissions）
   - 修改login方法，登录时加载用户权限信息到JWT Token或Redis
   - 修改UserController，添加角色分配相关接口
   - 测试用户角色分配功能
   - 测试用户权限查询功能

6. **配置SecurityConfig支持方法级权限控制（0.5天）**
   - 创建自定义权限注解@RequirePermission
   - 创建权限切面PermissionAspect，实现权限验证逻辑
   - 创建权限配置类PermissionConfig，配置自定义权限评估器
   - 修改SecurityConfig，启用@PreAuthorize注解
   - 配置方法级安全控制
   - 测试权限控制功能（有权限、无权限场景）
   - 测试多权限AND/OR逻辑

7. **初始化角色权限数据（0.5天）**
   - 创建init_role_permission.sql脚本
   - 初始化角色数据（超级管理员、教师、学生、仓库管理员）
   - 初始化权限数据（按模块划分：用户管理、角色管理、权限管理、耗材分类、耗材信息、入库管理、出库管理、库存管理、数据大屏、统计报表）
   - 初始化角色权限关联数据
   - 执行SQL脚本，验证数据创建成功
   - 测试权限树形结构是否正确

8. **测试与文档完善（1天）**
   - 编写角色管理Service层单元测试
   - 编写权限管理Service层单元测试
   - 编写权限计算逻辑测试
   - 确保测试覆盖率 > 80%
   - 进行集成测试（角色权限管理全流程）
   - 使用Postman测试所有API接口
   - 验证Swagger UI接口文档是否完整
   - 更新plan.md开发进度文档
   - 编写角色权限管理模块开发文档
   - 编写权限控制使用说明
   - 进行代码审查（代码规范、性能、安全）
   - 修复测试中发现的问题

9. **最终验收（0.5天）**
   - 验证所有功能完整性
   - 验证代码质量（规范、注释、异常处理）
   - 验证安全性（权限控制、SQL注入、XSS）
   - 验证性能（接口响应时间、权限树查询）
   - 验证文档完整性（API文档、开发文档）
   - 确认所有测试用例通过
   - 输出测试报告
   - 准备进入下一阶段开发

**总计：约5.5天**

**注意事项：**
- 每个步骤完成后进行自测，确保功能正常
- 遇到问题及时记录和解决
- 保持代码规范和注释清晰
- 及时更新开发文档
- 与团队成员保持沟通
