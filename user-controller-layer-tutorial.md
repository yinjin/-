# 用户控制层开发教材

## 概述

本文档详细记录了高职人工智能学院实训耗材管理系统第二天工作"1.4 用户控制层"的完整开发过程。作为开发教材，本文档不仅记录了具体的操作步骤，还深入剖析了控制层设计的技术原理和最佳实践，帮助开发者理解企业级应用中RESTful API的设计模式。

## 开发背景

### 为什么需要控制层？

在分层架构中，控制层（Controller Layer）是连接前端和后端的桥梁：

1. **请求接收**：接收HTTP请求，解析请求参数
2. **数据转换**：在DTO和业务对象之间进行转换
3. **业务调用**：调用Service层执行业务逻辑
4. **响应封装**：统一封装API响应格式
5. **异常处理**：处理业务异常，返回友好的错误信息
6. **权限控制**：实现接口级别的权限验证
7. **日志记录**：记录API调用日志用于监控和调试

### 控制层设计原则

1. **RESTful设计**：遵循RESTful API设计规范
2. **统一响应**：所有接口使用统一的响应格式
3. **参数验证**：在Controller层进行参数校验
4. **异常统一处理**：使用全局异常处理器
5. **接口文档**：使用Swagger/OpenAPI生成API文档
6. **权限控制**：基于注解的接口权限管理
7. **日志记录**：记录关键操作的调用日志

## 详细开发过程

### 第一步：创建统一响应格式

#### 操作步骤：
1. 在 `common` 包下创建 `ApiResponse.java`
2. 定义统一的API响应结构
3. 提供静态工厂方法创建响应

#### 代码实现：

```java
package com.haocai.management.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一API响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer code;        // 响应状态码
    private String message;      // 响应消息
    private T data;             // 响应数据
    private LocalDateTime timestamp; // 响应时间戳
    private String requestId;   // 请求ID（可选）

    // 静态工厂方法...
}
```

#### 设计要点详解：

**1. 响应格式设计：**
```json
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": "2026-01-07T10:30:00",
  "requestId": "req-123456"
}
```
- **code**：状态码，200成功，其他失败
- **message**：响应消息，成功时为"success"
- **data**：业务数据，泛型支持任意类型
- **timestamp**：响应时间，便于调试
- **requestId**：请求追踪ID（可选）

**2. 静态工厂方法：**
```java
public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(200, "success", data, LocalDateTime.now(), null);
}

public static <T> ApiResponse<T> error(Integer code, String message) {
    return new ApiResponse<>(code, message, null, LocalDateTime.now(), null);
}
```
- **success()**：创建成功响应
- **error()**：创建失败响应
- **泛型支持**：支持不同类型的响应数据

### 第二步：创建用户Controller

#### 操作步骤：
1. 在 `controller` 包下创建 `SysUserController.java`
2. 定义RESTful API接口
3. 集成Swagger文档注解
4. 实现统一的异常处理

#### 代码实现：

```java
package com.haocai.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.common.ApiResponse;
import com.haocai.management.dto.UserLoginDTO;
import com.haocai.management.dto.UserRegisterDTO;
import com.haocai.management.dto.UserUpdateDTO;
import com.haocai.management.entity.SysUser;
import com.haocai.management.entity.UserStatus;
import com.haocai.management.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、登录、信息管理等API")
public class SysUserController {
    // 接口实现...
}
```

#### 技术要点详解：

**1. Controller注解配置：**
```java
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、登录、信息管理等API")
public class SysUserController
```
- **@RestController**：REST控制器，返回JSON
- **@RequestMapping**：基础路径映射
- **@RequiredArgsConstructor**：Lombok自动注入
- **@Tag**：Swagger分组标签

**2. 依赖注入：**
```java
private final ISysUserService userService;
```
- **接口注入**：依赖抽象而非实现
- **final修饰**：保证不可变性
- **构造器注入**：推荐的注入方式

### 第三步：实现用户注册接口

#### 操作步骤：
1. 创建POST `/api/user/register` 接口
2. 接收UserRegisterDTO参数
3. 调用Service层注册逻辑
4. 返回统一格式的响应

#### 代码实现：

```java
@PostMapping("/register")
@Operation(summary = "用户注册", description = "新用户注册账号")
public ApiResponse<SysUser> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
    log.info("收到用户注册请求，用户名: {}", registerDTO.getUsername());

    try {
        SysUser user = userService.register(registerDTO);
        log.info("用户注册成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
        return ApiResponse.success(user);
    } catch (Exception e) {
        log.error("用户注册失败，用户名: {}, 错误: {}", registerDTO.getUsername(), e.getMessage());
        return ApiResponse.error("用户注册失败: " + e.getMessage());
    }
}
```

#### 设计要点详解：

**1. 请求映射注解：**
```java
@PostMapping("/register")
@Operation(summary = "用户注册", description = "新用户注册账号")
```
- **@PostMapping**：POST请求映射
- **@Operation**：Swagger接口文档
- **路径设计**：RESTful风格

**2. 参数验证：**
```java
public ApiResponse<SysUser> register(@Valid @RequestBody UserRegisterDTO registerDTO)
```
- **@Valid**：启用参数验证
- **@RequestBody**：JSON请求体
- **DTO参数**：使用专用DTO类

**3. 异常处理：**
```java
try {
    // 业务逻辑
    return ApiResponse.success(user);
} catch (Exception e) {
    log.error("用户注册失败...", e.getMessage());
    return ApiResponse.error("用户注册失败: " + e.getMessage());
}
```
- **try-catch**：捕获所有异常
- **日志记录**：记录错误信息
- **统一响应**：返回错误响应

### 第四步：实现用户登录接口

#### 操作步骤：
1. 创建POST `/api/user/login` 接口
2. 接收UserLoginDTO参数
3. 调用Service层登录逻辑
4. 返回JWT token

#### 代码实现：

```java
@PostMapping("/login")
@Operation(summary = "用户登录", description = "用户账号登录")
public ApiResponse<String> login(@Valid @RequestBody UserLoginDTO loginDTO) {
    log.info("收到用户登录请求，用户名: {}", loginDTO.getUsername());

    try {
        String token = userService.login(loginDTO);
        log.info("用户登录成功，用户名: {}", loginDTO.getUsername());
        return ApiResponse.success(token);
    } catch (Exception e) {
        log.error("用户登录失败，用户名: {}, 错误: {}", loginDTO.getUsername(), e.getMessage());
        return ApiResponse.error("登录失败: " + e.getMessage());
    }
}
```

#### 设计要点详解：

**1. 登录流程：**
```
客户端请求 → 参数验证 → 调用Service → 生成Token → 返回响应
```

**2. 安全考虑：**
- **密码不记录**：日志中不记录密码
- **失败记录**：记录失败原因用于安全分析
- **IP记录**：记录登录IP用于审计

### 第五步：实现用户信息查询接口

#### 操作步骤：
1. 创建GET `/api/user/info` 接口
2. 接收用户ID参数
3. 返回用户信息

#### 代码实现：

```java
@GetMapping("/info")
@Operation(summary = "获取用户信息", description = "获取当前登录用户的信息")
public ApiResponse<SysUser> getUserInfo(@RequestParam Long userId) {
    log.info("收到获取用户信息请求，用户ID: {}", userId);

    try {
        SysUser user = userService.findById(userId);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success(user);
    } catch (Exception e) {
        log.error("获取用户信息失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        return ApiResponse.error("获取用户信息失败: " + e.getMessage());
    }
}
```

#### 设计要点详解：

**1. GET请求设计：**
```java
@GetMapping("/info")
public ApiResponse<SysUser> getUserInfo(@RequestParam Long userId)
```
- **@GetMapping**：GET请求
- **@RequestParam**：URL参数
- **路径参数 vs 查询参数**：根据场景选择

**2. 空值处理：**
```java
if (user == null) {
    return ApiResponse.error(404, "用户不存在");
}
```
- **业务判断**：Service可能返回null
- **HTTP状态码**：404表示资源不存在
- **友好提示**：明确的错误信息

### 第六步：实现用户列表查询接口

#### 操作步骤：
1. 创建GET `/api/user/list` 接口
2. 支持分页和多条件查询
3. 返回分页结果

#### 代码实现：

```java
@GetMapping("/list")
@Operation(summary = "用户列表", description = "分页查询用户列表")
public ApiResponse<IPage<SysUser>> getUserList(
        @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
        @Parameter(description = "用户名关键词") @RequestParam(required = false) String username,
        @Parameter(description = "真实姓名关键词") @RequestParam(required = false) String realName,
        @Parameter(description = "用户状态") @RequestParam(required = false) UserStatus status,
        @Parameter(description = "部门ID") @RequestParam(required = false) Long departmentId) {

    try {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        IPage<SysUser> result = userService.findUserPage(page, username, realName, status, departmentId);
        return ApiResponse.success(result);
    } catch (Exception e) {
        return ApiResponse.error("查询用户列表失败: " + e.getMessage());
    }
}
```

#### 设计要点详解：

**1. 分页参数设计：**
```java
@RequestParam(defaultValue = "1") Integer pageNum,
@RequestParam(defaultValue = "10") Integer pageSize,
```
- **默认值**：提供合理的默认值
- **参数命名**：pageNum/pageSize是常见约定
- **Swagger文档**：@Parameter注解描述参数

**2. 可选参数处理：**
```java
@RequestParam(required = false) String username,
@RequestParam(required = false) UserStatus status,
```
- **required = false**：参数可选
- **null值处理**：Service层处理null值
- **枚举参数**：自动转换字符串到枚举

**3. 分页响应格式：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],     // 数据列表
    "total": 100,         // 总记录数
    "size": 10,           // 每页大小
    "current": 1,         // 当前页码
    "pages": 10           // 总页数
  }
}
```

### 第七步：实现用户状态管理接口

#### 操作步骤：
1. 创建PUT `/api/user/status` 接口
2. 实现批量状态更新接口
3. 支持单个和批量操作

#### 代码实现：

```java
@PutMapping("/status")
@Operation(summary = "更新用户状态", description = "启用或禁用用户账号")
public ApiResponse<Void> updateUserStatus(@RequestParam Long userId,
                                         @RequestParam UserStatus status,
                                         @RequestParam Long updateBy) {
    try {
        boolean success = userService.updateUserStatus(userId, status, updateBy);
        return success ? ApiResponse.success() : ApiResponse.error("用户状态更新失败");
    } catch (Exception e) {
        return ApiResponse.error("更新用户状态失败: " + e.getMessage());
    }
}

@PutMapping("/batch/status")
@Operation(summary = "批量更新用户状态", description = "批量启用或禁用多个用户")
public ApiResponse<Map<String, Object>> batchUpdateStatus(
        @RequestBody java.util.List<Long> userIds,
        @RequestParam UserStatus status,
        @RequestParam Long updateBy) {
    try {
        userService.batchUpdateStatus(userIds, status, updateBy);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", userIds.size());
        result.put("status", status);
        result.put("message", "批量更新操作已提交");
        
        return ApiResponse.success(result);
    } catch (Exception e) {
        log.error("批量更新用户状态失败: {}", e.getMessage(), e);
        return ApiResponse.error("批量更新用户状态失败: " + e.getMessage());
    }
}
```

#### ⚠️ 重要：批量操作接口设计

**问题背景：**
在测试过程中发现，批量更新用户状态时，如果用户ID列表中包含不存在的用户ID，需要优雅地处理这种情况，而不是让整个操作失败。

**解决方案：**

**1. 改进批量操作接口设计**

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

**2. 创建批量更新结果类**

```java
package com.haocai.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量更新结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateResult {
    private int total;      // 总数
    private int success;    // 成功数
    private int failed;     // 失败数
    private String message; // 消息
    
    public static BatchUpdateResult of(int total, int success, int failed) {
        return new BatchUpdateResult(total, success, failed, 
            String.format("总数: %d, 成功: %d, 失败: %d", total, success, failed));
    }
}
```

**3. Service层实现批量更新逻辑**

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
        log.warn("批量更新失败：所有用户都不存在");
        throw new BusinessException(400, "批量更新失败，所有用户都不存在");
    }
    
    // 过滤出实际存在的用户ID
    Set<Long> existingUserIds = users.stream()
            .map(SysUser::getId)
            .collect(Collectors.toSet());
    
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

**4. 测试用例**

```java
@Test
void testBatchUpdateStatus() {
    // 准备测试数据
    List<Long> userIds = Arrays.asList(1L, 2L, 3L, 999L, 1000L); // 包含不存在的用户
    
    // 执行批量更新
    ApiResponse<Map<String, Object>> response = 
        userController.batchUpdateStatus(userIds, UserStatus.INACTIVE, 1L);
    
    // 验证结果
    assertEquals(200, response.getCode());
    assertNotNull(response.getData());
    
    Map<String, Object> data = response.getData();
    assertEquals(5, data.get("total")); // 总数5个
    assertTrue((Integer)data.get("success") >= 3); // 至少3个成功
    assertTrue((Integer)data.get("failed") >= 2); // 至少2个失败
}
```

**5. 最佳实践总结**

- **详细响应**：返回成功和失败的详细统计
- **异常隔离**：单个用户失败不影响其他用户
- **日志记录**：记录批量操作的详细信息
- **参数限制**：限制批量操作的最大数量
- **用户反馈**：提供清晰的操作结果反馈

#### 设计要点详解：

**1. RESTful设计：**
- **PUT /status**：更新单个用户状态
- **PUT /batch/status**：批量更新状态
- **语义明确**：HTTP方法表达操作意图

**2. 批量操作设计：**
```java
@RequestBody java.util.List<Long> userIds,
@RequestParam UserStatus status,
```
- **@RequestBody**：JSON数组作为请求体
- **统一状态**：批量操作使用相同状态
- **返回影响行数**：告知操作结果

### 第八步：实现数据验证接口

#### 操作步骤：
1. 创建用户名/邮箱/手机号验证接口
2. 支持排除当前用户的验证
3. 用于前端实时验证

#### 代码实现：

```java
@GetMapping("/check/username")
@Operation(summary = "检查用户名", description = "检查用户名是否已被使用")
public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
    try {
        boolean exists = userService.existsByUsername(username);
        return ApiResponse.success(exists);
    } catch (Exception e) {
        return ApiResponse.error("检查用户名失败: " + e.getMessage());
    }
}

@GetMapping("/check/email")
@Operation(summary = "检查邮箱", description = "检查邮箱是否已被使用")
public ApiResponse<Boolean> checkEmail(@RequestParam String email,
                                      @RequestParam(required = false) Long excludeUserId) {
    try {
        boolean exists = userService.existsByEmail(email, excludeUserId);
        return ApiResponse.success(exists);
    } catch (Exception e) {
        return ApiResponse.error("检查邮箱失败: " + e.getMessage());
    }
}
```

#### 设计要点详解：

**1. 验证接口设计：**
- **实时验证**：支持前端实时检查
- **排除自身**：修改时不检查自身数据
- **快速响应**：轻量级查询操作

**2. 参数设计：**
```java
@RequestParam(required = false) Long excludeUserId
```
- **可选参数**：注册时不需要排除
- **修改时使用**：更新时排除当前用户

## RESTful API设计规范

### 1. 资源命名

**用户资源：**
```
GET    /api/user/list     - 查询用户列表
GET    /api/user/info     - 获取单个用户信息
POST   /api/user/register - 创建新用户
PUT    /api/user/update   - 更新用户信息
PUT    /api/user/status   - 更新用户状态
DELETE /api/user/delete   - 删除用户
```

### 2. HTTP状态码使用

**成功状态码：**
- **200 OK**：请求成功
- **201 Created**：资源创建成功
- **204 No Content**：请求成功，无返回内容

**客户端错误：**
- **400 Bad Request**：请求参数错误
- **401 Unauthorized**：未认证
- **403 Forbidden**：权限不足
- **404 Not Found**：资源不存在
- **409 Conflict**：资源冲突

**服务器错误：**
- **500 Internal Server Error**：服务器内部错误

### 3. 请求参数设计

**查询参数：**
```java
@RequestParam(defaultValue = "1") Integer pageNum,
@RequestParam(required = false) String keyword,
```

**路径参数：**
```java
@GetMapping("/{id}")
public ApiResponse<User> getById(@PathVariable Long id)
```

**请求体参数：**
```java
@PostMapping
public ApiResponse<User> create(@Valid @RequestBody UserCreateDTO dto)
```

## Swagger API文档

### 1. 控制器注解

```java
@Tag(name = "用户管理", description = "用户注册、登录、信息管理等API")
public class SysUserController
```

### 2. 接口注解

```java
@Operation(summary = "用户注册", description = "新用户注册账号")
@PostMapping("/register")
public ApiResponse<SysUser> register(...)
```

### 3. 参数注解

```java
@Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
@Parameter(description = "用户名关键词") @RequestParam(required = false) String username,
```

## 异常处理策略

### 1. Controller层异常处理

**try-catch包装：**
```java
try {
    // 业务逻辑
    return ApiResponse.success(result);
} catch (Exception e) {
    log.error("操作失败: {}", e.getMessage());
    return ApiResponse.error("操作失败: " + e.getMessage());
}
```

**优势：**
- **接口级别控制**：每个接口独立处理
- **详细日志**：记录具体的错误信息
- **友好响应**：返回用户友好的错误信息

### 2. 全局异常处理

**@RestControllerAdvice：**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }
}
```

**优势：**
- **统一处理**：全局异常统一处理
- **代码简洁**：Controller无需try-catch
- **类型安全**：根据异常类型处理

### ⚠️ 重要：接口异常处理最佳实践

**问题背景：**
在测试过程中发现，不同类型的异常需要不同的处理策略，以确保前端能够正确处理错误响应。

**解决方案：**

**1. 分层异常处理策略**

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 参数验证异常处理
     */
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

    /**
     * 参数绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBindException(BindException e) {
        log.error("参数绑定异常", e);
        
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        ApiResponse<Map<String, String>> response = ApiResponse.error(400, "参数绑定失败");
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 非法参数异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        log.error("非法参数异常: {}", e.getMessage(), e);
        
        ApiResponse<Void> response = ApiResponse.error(400, "参数错误: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 数据库异常处理
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(DataAccessException e) {
        log.error("数据库访问异常", e);
        
        String message = "数据库操作失败";
        if (e.getCause() instanceof SQLException) {
            SQLException sqlEx = (SQLException) e.getCause();
            message = "数据库错误: " + sqlEx.getMessage();
        }
        
        ApiResponse<Void> response = ApiResponse.error(500, message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("系统异常", e);
        
        ApiResponse<Void> response = ApiResponse.error(500, "系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

**2. Controller层异常处理模式**

**模式一：简单try-catch**
```java
@PostMapping("/register")
public ApiResponse<SysUser> register(@Valid @RequestBody UserRegisterDTO dto) {
    try {
        SysUser user = userService.register(dto);
        return ApiResponse.success(user);
    } catch (Exception e) {
        log.error("用户注册失败: {}", e.getMessage(), e);
        return ApiResponse.error("用户注册失败: " + e.getMessage());
    }
}
```

**模式二：不使用try-catch（依赖全局异常处理器）**
```java
@PostMapping("/register")
public ApiResponse<SysUser> register(@Valid @RequestBody UserRegisterDTO dto) {
    log.info("收到用户注册请求，用户名: {}", dto.getUsername());
    SysUser user = userService.register(dto);
    log.info("用户注册成功，用户ID: {}", user.getId());
    return ApiResponse.success(user);
}
```

**3. 错误响应格式设计**

**业务错误响应：**
```json
{
  "code": 1001,
  "message": "用户不存在",
  "data": null,
  "timestamp": "2026-01-07T10:30:00"
}
```

**参数验证错误响应：**
```json
{
  "code": 400,
  "message": "参数验证失败",
  "data": {
    "username": "用户名不能为空",
    "email": "邮箱格式不正确"
  },
  "timestamp": "2026-01-07T10:30:00"
}
```

**系统错误响应：**
```json
{
  "code": 500,
  "message": "系统内部错误",
  "data": null,
  "timestamp": "2026-01-07T10:30:00"
}
```

**4. 最佳实践总结**

- **分层处理**：Controller层处理接口级异常，全局处理器处理系统级异常
- **日志记录**：记录详细的错误信息，包括堆栈跟踪
- **用户友好**：返回用户可理解的错误信息
- **错误码规范**：使用统一的错误码体系
- **HTTP状态码**：正确使用HTTP状态码表达错误类型

## 性能优化策略

### 1. 分页查询优化

**合理分页大小：**
```java
@RequestParam(defaultValue = "10") Integer pageSize
```
- **默认10条**：平衡性能和用户体验
- **最大限制**：防止一次性查询过多数据
- **前端配合**：支持动态调整分页大小

### 2. 参数验证优化

**分组验证：**
```java
public interface CreateGroup {}
public interface UpdateGroup {}

@Validated(CreateGroup.class)
public ApiResponse<User> create(@Valid UserDTO dto)

@Validated(UpdateGroup.class)
public ApiResponse<User> update(@Valid UserDTO dto)
```

### 3. 缓存策略

**接口缓存：**
```java
@Cacheable(value = "user", key = "#userId")
@GetMapping("/info")
public ApiResponse<User> getUserInfo(@RequestParam Long userId)
```

## 测试策略

### 单元测试

**Controller测试：**
```java
@SpringBootTest
@AutoConfigureMockMvc
class SysUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegister() throws Exception {
        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"test\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
```

### 集成测试

**API测试：**
```java
@Test
void testUserLifecycle() {
    // 注册 → 登录 → 查询 → 更新 → 删除
}
```

### 性能测试

**压力测试：**
```java
@LoadTest
void testConcurrentRequests() {
    // 并发请求测试
}
```

## 总结

用户控制层设计是RESTful API开发的核心环节。通过本教程的学习，开发者应该掌握：

- **RESTful设计**：资源命名、HTTP方法、状态码使用
- **统一响应**：ApiResponse设计和使用
- **参数处理**：请求参数、路径参数、请求体的处理
- **异常处理**：Controller层和全局异常处理
- **API文档**：Swagger注解的使用
- **性能优化**：分页、验证、缓存策略

这些技能和经验将为后续的前端集成和JWT认证实现提供坚实的基础。

---

*本文档记录了2026年1月7日的开发工作，版本控制提交ID：待定*
