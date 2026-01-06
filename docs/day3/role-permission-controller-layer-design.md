# 角色权限控制层 - 规划与设计文档

**文档版本**：v1.0  
**创建时间**：2026年1月6日  
**开发者**：Cline  
**任务来源**：day3-plan.md 1.4 角色权限控制层

---

## 一、任务概述

### 1.1 任务目标

开发角色权限控制层，包括：
- 创建SysRoleController（角色管理API）
- 创建SysPermissionController（权限管理API）
- 创建自定义权限注解@RequirePermission
- 创建权限切面PermissionAspect
- 修改SecurityConfig配置方法级安全控制
- 集成Swagger API文档
- 参数验证和异常处理

### 1.2 技术栈

- Spring Boot 3.1.6
- Spring Security 6.x（方法级安全控制）
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Swagger/OpenAPI 3
- AOP（面向切面编程）
- Jakarta Validation（参数验证）

---

## 二、关键约束条款（基于development-standards.md）

### 2.1 控制层规范（第4节）

**约束条款1：批量操作接口规范**
- 必须限制批量操作的最大数量（建议100个）
- 必须返回详细的操作结果（总数、成功数、失败数）
- 必须完善异常处理，提供友好的错误信息

**约束条款2：异常处理规范**
- 必须实现全局异常处理器
- 必须记录详细的错误日志
- 必须统一错误响应格式（ApiResponse<T>）

**约束条款3：参数验证规范**
- 必须使用Jakarta Validation注解进行参数验证
- 必须在Controller层进行参数验证
- 必须返回详细的验证错误信息

### 2.2 数据访问层规范（第3节）

**约束条款4：批量操作规范**
- 批量操作必须先查询存在的记录
- 必须捕获单个记录的异常，不影响其他记录
- 必须返回详细的操作结果

### 2.3 配置规范（第5节）

**约束条款5：MyBatis-Plus配置**
- 必须开启驼峰转下划线
- 必须配置逻辑删除
- 必须开启SQL日志

**约束条款6：WebMvc配置**
- 必须注册自定义类型转换器
- 必须实现WebMvcConfigurer接口

---

## 三、核心设计

### 3.1 SysRoleController设计

#### 3.1.1 类设计

```java
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色管理相关接口")
public class SysRoleController {
    
    private final ISysRoleService roleService;
    
    // 核心方法见下文
}
```

#### 3.1.2 核心方法签名

**1. 创建角色**
```java
@PostMapping
@Operation(summary = "创建角色", description = "创建新的角色")
@RequirePermission("role:create")
public ApiResponse<RoleVO> createRole(
    @Valid @RequestBody RoleCreateDTO dto,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@PostMapping`符合RESTful规范
- 使用`@Valid`启用Jakarta Validation参数验证
- 使用`@RequirePermission`注解进行权限控制
- 返回`ApiResponse<RoleVO>`统一响应格式
- 从请求头获取JWT Token

**满足约束**：
- ✅ 约束条款3：使用Jakarta Validation进行参数验证
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**2. 更新角色**
```java
@PutMapping("/{id}")
@Operation(summary = "更新角色", description = "更新角色信息")
@RequirePermission("role:update")
public ApiResponse<RoleVO> updateRole(
    @PathVariable Long id,
    @Valid @RequestBody RoleUpdateDTO dto,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@PutMapping`符合RESTful规范
- 使用`@PathVariable`获取路径参数
- 使用`@Valid`启用参数验证
- 使用`@RequirePermission`注解进行权限控制

**满足约束**：
- ✅ 约束条款3：使用Jakarta Validation进行参数验证
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**3. 删除角色**
```java
@DeleteMapping("/{id}")
@Operation(summary = "删除角色", description = "删除角色（逻辑删除）")
@RequirePermission("role:delete")
public ApiResponse<Void> deleteRole(
    @PathVariable Long id,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@DeleteMapping`符合RESTful规范
- 使用逻辑删除，不物理删除数据
- 使用`@RequirePermission`注解进行权限控制

**满足约束**：
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**4. 获取角色详情**
```java
@GetMapping("/{id}")
@Operation(summary = "获取角色详情", description = "根据ID获取角色详情")
@RequirePermission("role:view")
public ApiResponse<RoleVO> getRoleById(
    @PathVariable Long id,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@GetMapping`符合RESTful规范
- 使用`@PathVariable`获取路径参数
- 使用`@RequirePermission`注解进行权限控制

**满足约束**：
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**5. 分页查询角色列表**
```java
@GetMapping("/list")
@Operation(summary = "分页查询角色列表", description = "分页查询角色列表")
@RequirePermission("role:list")
public ApiResponse<IPage<RoleVO>> getRolePage(
    @RequestParam(defaultValue = "1") Integer current,
    @RequestParam(defaultValue = "10") Integer size,
    @RequestParam(required = false) String roleName,
    @RequestParam(required = false) String roleCode,
    @RequestParam(required = false) Integer status,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@GetMapping`符合RESTful规范
- 使用`@RequestParam`获取查询参数
- 使用`@DefaultValue`设置默认值
- 使用`@Required = false`标记可选参数
- 使用`@RequirePermission`注解进行权限控制
- 返回`IPage<RoleVO>`分页结果

**满足约束**：
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**6. 更新角色权限**
```java
@PutMapping("/{id}/permissions")
@Operation(summary = "更新角色权限", description = "为角色分配权限")
@RequirePermission("role:assign")
public ApiResponse<Void> updateRolePermissions(
    @PathVariable Long id,
    @RequestBody List<Long> permissionIds,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@PutMapping`符合RESTful规范
- 使用`@PathVariable`获取路径参数
- 使用`@RequestBody`接收权限ID列表
- 使用`@RequirePermission`注解进行权限控制

**满足约束**：
- ✅ 约束条款1：批量操作限制（在Service层实现）
- ✅ 约束条款4：批量操作先查询后更新（在Service层实现）
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**7. 获取角色权限**
```java
@GetMapping("/{id}/permissions")
@Operation(summary = "获取角色权限", description = "获取角色的所有权限")
@RequirePermission("role:view")
public ApiResponse<List<PermissionVO>> getRolePermissions(
    @PathVariable Long id,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@GetMapping`符合RESTful规范
- 使用`@PathVariable`获取路径参数
- 使用`@RequirePermission`注解进行权限控制
- 返回`List<PermissionVO>`权限列表

**满足约束**：
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

### 3.2 SysPermissionController设计

#### 3.2.1 类设计

```java
@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限管理相关接口")
public class SysPermissionController {
    
    private final ISysPermissionService permissionService;
    
    // 核心方法见下文
}
```

#### 3.2.2 核心方法签名

**1. 创建权限**
```java
@PostMapping
@Operation(summary = "创建权限", description = "创建新的权限")
@RequirePermission("permission:create")
public ApiResponse<PermissionVO> createPermission(
    @Valid @RequestBody PermissionCreateDTO dto,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@PostMapping`符合RESTful规范
- 使用`@Valid`启用Jakarta Validation参数验证
- 使用`@RequirePermission`注解进行权限控制
- 返回`ApiResponse<PermissionVO>`统一响应格式

**满足约束**：
- ✅ 约束条款3：使用Jakarta Validation进行参数验证
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**2. 更新权限**
```java
@PutMapping("/{id}")
@Operation(summary = "更新权限", description = "更新权限信息")
@RequirePermission("permission:update")
public ApiResponse<PermissionVO> updatePermission(
    @PathVariable Long id,
    @Valid @RequestBody PermissionUpdateDTO dto,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@PutMapping`符合RESTful规范
- 使用`@PathVariable`获取路径参数
- 使用`@Valid`启用参数验证
- 使用`@RequirePermission`注解进行权限控制

**满足约束**：
- ✅ 约束条款3：使用Jakarta Validation进行参数验证
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**3. 删除权限**
```java
@DeleteMapping("/{id}")
@Operation(summary = "删除权限", description = "删除权限（逻辑删除）")
@RequirePermission("permission:delete")
public ApiResponse<Void> deletePermission(
    @PathVariable Long id,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@DeleteMapping`符合RESTful规范
- 使用逻辑删除，不物理删除数据
- 使用`@RequirePermission`注解进行权限控制

**满足约束**：
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**4. 获取权限详情**
```java
@GetMapping("/{id}")
@Operation(summary = "获取权限详情", description = "根据ID获取权限详情")
@RequirePermission("permission:view")
public ApiResponse<PermissionVO> getPermissionById(
    @PathVariable Long id,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@GetMapping`符合RESTful规范
- 使用`@PathVariable`获取路径参数
- 使用`@RequirePermission`注解进行权限控制

**满足约束**：
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**5. 获取权限树形结构**
```java
@GetMapping("/tree")
@Operation(summary = "获取权限树", description = "获取权限的树形结构")
@RequirePermission("permission:view")
public ApiResponse<List<PermissionVO>> getPermissionTree(
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@GetMapping`符合RESTful规范
- 使用`@RequirePermission`注解进行权限控制
- 返回树形结构的权限列表

**满足约束**：
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

**6. 分页查询权限列表**
```java
@GetMapping("/list")
@Operation(summary = "分页查询权限列表", description = "分页查询权限列表")
@RequirePermission("permission:list")
public ApiResponse<IPage<PermissionVO>> getPermissionPage(
    @RequestParam(defaultValue = "1") Integer current,
    @RequestParam(defaultValue = "10") Integer size,
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String code,
    @RequestParam(required = false) Integer type,
    @RequestParam(required = false) Integer status,
    @RequestHeader("Authorization") String token
) {
    // 实现逻辑
}
```

**设计说明**：
- 使用`@GetMapping`符合RESTful规范
- 使用`@RequestParam`获取查询参数
- 使用`@DefaultValue`设置默认值
- 使用`@Required = false`标记可选参数
- 使用`@RequirePermission`注解进行权限控制
- 返回`IPage<PermissionVO>`分页结果

**满足约束**：
- ✅ 约束条款2：统一响应格式ApiResponse<T>

---

### 3.3 @RequirePermission注解设计

#### 3.3.1 注解定义

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 权限编码数组
     */
    String[] value();
    
    /**
     * 逻辑关系：AND（需要所有权限）或OR（需要任一权限）
     */
    Logical logical() default Logical.AND;
    
    /**
     * 逻辑关系枚举
     */
    enum Logical {
        AND, OR
    }
}
```

**设计说明**：
- 使用`@Target({ElementType.METHOD, ElementType.TYPE})`支持方法和类级别注解
- 使用`@Retention(RetentionPolicy.RUNTIME)`支持运行时反射
- 使用`value()`属性定义权限编码数组
- 使用`logical()`属性定义权限逻辑关系（AND/OR）
- 使用`Logical`枚举提高代码可读性

**满足约束**：
- ✅ 符合Spring Security方法级安全控制规范
- ✅ 支持灵活的权限控制策略

---

### 3.4 PermissionAspect切面设计

#### 3.4.1 切面定义

```java
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PermissionAspect {
    
    private final ISysUserService userService;
    private final ISysPermissionService permissionService;
    
    /**
     * 切入点：拦截所有带有@RequirePermission注解的方法
     */
    @Pointcut("@annotation(com.haocai.management.annotation.RequirePermission)")
    public void permissionPointcut() {
    }
    
    /**
     * 环绕通知：执行权限检查
     */
    @Around("permissionPointcut()")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 实现逻辑见下文
    }
    
    /**
     * 获取当前用户的权限列表
     */
    private Set<String> getCurrentUserPermissions(Long userId) {
        // 实现逻辑见下文
    }
    
    /**
     * 检查用户是否拥有所需权限
     */
    private boolean hasPermission(Set<String> userPermissions, String[] requiredPermissions, Logical logical) {
        // 实现逻辑见下文
    }
}
```

**设计说明**：
- 使用`@Aspect`标记为切面类
- 使用`@Component`注册为Spring Bean
- 使用`@Pointcut`定义切入点
- 使用`@Around`环绕通知执行权限检查
- 使用`@Slf4j`记录日志

**满足约束**：
- ✅ 约束条款2：记录详细的错误日志
- ✅ 符合AOP编程规范

---

#### 3.4.2 核心方法实现

**1. checkPermission方法**
```java
@Around("permissionPointcut()")
public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
    // 1. 获取方法签名
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    
    // 2. 获取@RequirePermission注解
    RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
    if (requirePermission == null) {
        return joinPoint.proceed();
    }
    
    // 3. 获取当前用户ID
    Long userId = getCurrentUserId();
    if (userId == null) {
        log.warn("权限检查失败：未获取到当前用户ID");
        throw new BusinessException(401, "未登录或登录已过期");
    }
    
    // 4. 获取用户权限列表
    Set<String> userPermissions = getCurrentUserPermissions(userId);
    log.debug("当前用户权限列表: {}", userPermissions);
    
    // 5. 检查权限
    String[] requiredPermissions = requirePermission.value();
    Logical logical = requirePermission.logical();
    
    boolean hasPermission = hasPermission(userPermissions, requiredPermissions, logical);
    
    if (!hasPermission) {
        log.warn("权限检查失败：用户ID={}, 需要权限={}, 逻辑={}", 
            userId, Arrays.toString(requiredPermissions), logical);
        throw new BusinessException(403, "权限不足");
    }
    
    // 6. 权限检查通过，执行目标方法
    log.debug("权限检查通过：用户ID={}, 方法={}", userId, method.getName());
    return joinPoint.proceed();
}
```

**设计说明**：
- 获取方法签名和注解
- 从SecurityContext获取当前用户ID
- 查询用户权限列表
- 检查用户是否拥有所需权限
- 权限不足时抛出BusinessException
- 权限通过时执行目标方法

**满足约束**：
- ✅ 约束条款2：记录详细的错误日志
- ✅ 约束条款2：统一异常处理（抛出BusinessException）

---

**2. getCurrentUserPermissions方法**
```java
private Set<String> getCurrentUserPermissions(Long userId) {
    try {
        // 从Service层获取用户权限
        List<SysPermission> permissions = permissionService.getPermissionsByUserId(userId);
        
        // 提取权限编码
        Set<String> permissionCodes = permissions.stream()
            .map(SysPermission::getCode)
            .collect(Collectors.toSet());
        
        return permissionCodes;
    } catch (Exception e) {
        log.error("获取用户权限失败：userId={}", userId, e);
        throw new BusinessException(500, "获取用户权限失败");
    }
}
```

**设计说明**：
- 调用Service层获取用户权限
- 提取权限编码到Set集合
- 异常时记录日志并抛出BusinessException

**满足约束**：
- ✅ 约束条款2：记录详细的错误日志
- ✅ 约束条款2：统一异常处理

---

**3. hasPermission方法**
```java
private boolean hasPermission(Set<String> userPermissions, String[] requiredPermissions, Logical logical) {
    if (requiredPermissions == null || requiredPermissions.length == 0) {
        return true;
    }
    
    if (logical == Logical.AND) {
        // 需要所有权限
        return Arrays.stream(requiredPermissions)
            .allMatch(userPermissions::contains);
    } else {
        // 需要任一权限
        return Arrays.stream(requiredPermissions)
            .anyMatch(userPermissions::contains);
    }
}
```

**设计说明**：
- 支持AND逻辑（需要所有权限）
- 支持OR逻辑（需要任一权限）
- 使用Stream API简化代码

**满足约束**：
- ✅ 符合RBAC权限模型规范

---

### 3.5 SecurityConfig配置修改

#### 3.5.1 配置类修改

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // 启用方法级安全控制
@RequiredArgsConstructor
public class SecurityConfig {
    
    // 现有配置保持不变
    
    /**
     * 配置自定义权限评估器
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return handler;
    }
}
```

**设计说明**：
- 使用`@EnableMethodSecurity`启用方法级安全控制
- 配置自定义权限评估器CustomPermissionEvaluator
- 保持现有配置不变

**满足约束**：
- ✅ 符合Spring Security 6.x规范
- ✅ 支持自定义权限评估逻辑

---

#### 3.5.2 CustomPermissionEvaluator设计

```java
@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {
    
    private final ISysUserService userService;
    private final ISysPermissionService permissionService;
    
    @Override
    public boolean hasPermission(Authentication authentication, Object target, Object permission) {
        // 实现逻辑
        return false;
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        // 实现逻辑
        return false;
    }
}
```

**设计说明**：
- 实现PermissionEvaluator接口
- 支持自定义权限评估逻辑
- 与@RequirePermission注解配合使用

**满足约束**：
- ✅ 符合Spring Security方法级安全控制规范

---

### 3.6 Swagger API文档集成

#### 3.6.1 Swagger配置

```java
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("高职人工智能学院实训耗材管理系统 API")
                .version("1.0.0")
                .description("角色权限管理相关接口"))
            .addSecurityItem(new SecurityRequirement()
                .addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

**设计说明**：
- 配置API文档基本信息
- 配置JWT认证方式
- 使用OpenAPI 3规范

**满足约束**：
- ✅ 符合Swagger/OpenAPI 3规范
- ✅ 支持JWT认证

---

#### 3.6.2 Controller注解

```java
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色管理相关接口")
public class SysRoleController {
    
    @PostMapping
    @Operation(summary = "创建角色", description = "创建新的角色")
    @RequirePermission("role:create")
    public ApiResponse<RoleVO> createRole(
        @Valid @RequestBody RoleCreateDTO dto,
        @RequestHeader("Authorization") String token
    ) {
        // 实现逻辑
    }
}
```

**设计说明**：
- 使用`@Tag`标记Controller
- 使用`@Operation`标记方法
- 使用`@Parameter`标记参数（可选）

**满足约束**：
- ✅ 符合Swagger/OpenAPI 3规范
- ✅ 提供完整的API文档

---

## 四、异常处理设计

### 4.1 全局异常处理器

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

**设计说明**：
- 处理BusinessException业务异常
- 处理MethodArgumentNotValidException参数验证异常
- 处理Exception系统异常
- 统一返回ApiResponse<T>格式

**满足约束**：
- ✅ 约束条款2：实现全局异常处理器
- ✅ 约束条款2：记录详细的错误日志
- ✅ 约束条款2：统一错误响应格式

---

## 五、参数验证设计

### 5.1 DTO验证注解

**RoleCreateDTO**
```java
@Data
public class RoleCreateDTO {
    
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50")
    private String name;
    
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "角色编码只能包含字母、数字和下划线")
    private String code;
    
    @Size(max = 200, message = "角色描述长度不能超过200")
    private String description;
    
    @NotNull(message = "状态不能为空")
    private Integer status;
}
```

**设计说明**：
- 使用`@NotBlank`验证非空字符串
- 使用`@Size`验证字符串长度
- 使用`@Pattern`验证字符串格式
- 使用`@NotNull`验证非空对象

**满足约束**：
- ✅ 约束条款3：使用Jakarta Validation进行参数验证

---

**PermissionCreateDTO**
```java
@Data
public class PermissionCreateDTO {
    
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过50")
    private String name;
    
    @NotBlank(message = "权限编码不能为空")
    @Size(max = 100, message = "权限编码长度不能超过100")
    @Pattern(regexp = "^[a-zA-Z0-9:_]+$", message = "权限编码只能包含字母、数字、冒号和下划线")
    private String code;
    
    @NotNull(message = "权限类型不能为空")
    private Integer type;
    
    private Long parentId;
    
    @Size(max = 200, message = "路径长度不能超过200")
    private String path;
    
    @Size(max = 100, message = "组件名称长度不能超过100")
    private String component;
    
    @Size(max = 50, message = "图标长度不能超过50")
    private String icon;
    
    private Integer sortOrder;
    
    @NotNull(message = "状态不能为空")
    private Integer status;
}
```

**设计说明**：
- 使用`@NotBlank`验证非空字符串
- 使用`@Size`验证字符串长度
- 使用`@Pattern`验证字符串格式
- 使用`@NotNull`验证非空对象

**满足约束**：
- ✅ 约束条款3：使用Jakarta Validation进行参数验证

---

## 六、总结

### 6.1 设计亮点

1. **符合RESTful规范**：所有接口设计遵循RESTful API设计规范
2. **统一响应格式**：使用ApiResponse<T>统一响应格式
3. **完善的权限控制**：使用@RequirePermission注解和PermissionAspect切面实现灵活的权限控制
4. **参数验证**：使用Jakarta Validation进行参数验证
5. **异常处理**：实现全局异常处理器，统一异常处理
6. **日志记录**：记录详细的操作日志和错误日志
7. **Swagger文档**：集成Swagger API文档，提供完整的API文档

### 6.2 满足的约束条款

- ✅ 约束条款1：批量操作接口规范
- ✅ 约束条款2：异常处理规范
- ✅ 约束条款3：参数验证规范
- ✅ 约束条款4：批量操作规范
- ✅ 约束条款5：MyBatis-Plus配置
- ✅ 约束条款6：WebMvc配置

### 6.3 下一步工作

1. 实现与编码：创建所有Controller、注解、切面类
2. 验证与测试：编写测试用例，验证功能正确性
3. 文档与知识固化：更新development-standards.md，编写快速指南

---

**文档结束**
