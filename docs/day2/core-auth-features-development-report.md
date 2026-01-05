# 核心认证功能开发报告

## 任务概述

**开发时间**：2026年1月5日  
**开发目标**：完成6项核心认证功能，为前端集成和Redis优化奠定基础  
**遵循规范**：development-standards.md

## 开发任务清单

### 第一阶段：后端接口完善
1. ✅ 完善用户信息获取接口（从JWT token获取用户ID）
2. ✅ 添加令牌刷新接口
3. ✅ 添加登出接口

### 第二阶段：前端状态管理
4. ✅ 完善用户认证Store

### 第三阶段：前端API封装
5. ✅ 封装用户API接口

### 第四阶段：前端拦截器优化
6. ✅ 优化Axios拦截器

---

## 步骤1：后端接口完善

### 1.1 完善用户信息获取接口（从JWT token获取用户ID）

#### 规划与设计

**关键约束条款**：
1. **安全规范**：JWT token必须经过验证才能提取用户信息
2. **异常处理规范**：token无效或过期时必须抛出明确的异常
3. **日志规范**：必须记录token验证和用户信息提取的日志

**核心方法设计**：
```java
/**
 * 从JWT token中获取当前用户ID
 * 
 * 遵循：安全规范-从认证上下文获取用户信息
 * 
 * @return 用户ID
 * @throws BusinessException token无效或过期
 */
private Long getCurrentUserIdFromToken(HttpServletRequest request) {
    // 1. 从请求头获取token
    // 2. 验证token有效性
    // 3. 提取用户ID
    // 4. 返回用户ID
}
```

#### 实现与编码

**文件路径**：`backend/src/main/java/com/haocai/management/controller/SysUserController.java`

**修改内容**：
1. 添加从JWT token获取用户ID的工具方法
2. 替换所有硬编码的用户ID为从token获取的真实用户ID

**规范映射**：
- `// 遵循：安全规范-从认证上下文获取用户信息`
- `// 遵循：异常处理规范-统一异常处理`
- `// 遵循：日志规范-记录关键操作`

**安全决策说明**：
- 从SecurityContext获取认证信息，而不是直接解析token
- 使用Spring Security的认证机制，确保安全性
- token验证失败时抛出BusinessException，统一异常处理

---

### 1.2 添加令牌刷新接口

#### 规划与设计

**关键约束条款**：
1. **安全规范**：refresh token必须验证有效性
2. **配置规范**：access token和refresh token的过期时间要合理配置
3. **异常处理规范**：refresh token无效时必须返回明确的错误信息

**核心方法设计**：
```java
/**
 * 刷新JWT token
 * 
 * 遵循：安全规范-refresh token验证
 * 
 * @param refreshToken 刷新令牌
 * @return 新的access token
 */
String refreshToken(String refreshToken);
```

#### 实现与编码

**文件路径**：
- `backend/src/main/java/com/haocai/management/service/ISysUserService.java`
- `backend/src/main/java/com/haocai/management/service/impl/SysUserServiceImpl.java`
- `backend/src/main/java/com/haocai/management/controller/SysUserController.java`

**规范映射**：
- `// 遵循：安全规范-refresh token验证`
- `// 遵循：配置规范-token过期时间配置`
- `// 遵循：异常处理规范-统一异常处理`

**安全决策说明**：
- refresh token的过期时间设置为access token的2倍（7天）
- 验证refresh token时检查用户状态是否正常
- 生成新的access token时使用相同的用户信息

---

### 1.3 添加登出接口

#### 规划与设计

**关键约束条款**：
1. **安全规范**：登出时必须清除认证信息
2. **日志规范**：必须记录登出操作日志
3. **异常处理规范**：登出失败时必须返回明确的错误信息

**核心方法设计**：
```java
/**
 * 用户登出
 * 
 * 遵循：安全规范-清除认证信息
 * 
 * @param token JWT token
 */
void logout(String token);
```

#### 实现与编码

**文件路径**：
- `backend/src/main/java/com/haocai/management/service/ISysUserService.java`
- `backend/src/main/java/com/haocai/management/service/impl/SysUserServiceImpl.java`
- `backend/src/main/java/com/haocai/management/controller/SysUserController.java`

**规范映射**：
- `// 遵循：安全规范-清除认证信息`
- `// 遵循：日志规范-记录登出操作`
- `// 遵循：异常处理规范-统一异常处理`

**安全决策说明**：
- 登出时清除SecurityContext中的认证信息
- 记录登出日志，包括用户ID、登出时间、IP地址
- 为后续Redis集成预留黑名单接口

---

## 步骤2：前端状态管理

### 2.1 完善用户认证Store

#### 规划与设计

**关键约束条款**：
1. **配置规范**：token存储在localStorage中
2. **安全规范**：敏感信息（如密码）不存储在store中
3. **代码规范**：使用TypeScript类型定义

**核心状态设计**：
```typescript
interface AuthState {
  token: string | null
  user: User | null
  isAuthenticated: boolean
}
```

**核心方法设计**：
```typescript
interface AuthActions {
  setToken(token: string): void
  clearToken(): void
  setUser(user: User): void
  clearUser(): void
  login(credentials: LoginDTO): Promise<void>
  logout(): void
  refreshToken(): Promise<string>
}
```

#### 实现与编码

**文件路径**：`frontend/src/store/auth.ts`

**规范映射**：
- `// 遵循：配置规范-token存储在localStorage`
- `// 遵循：安全规范-不存储敏感信息`
- `// 遵循：代码规范-TypeScript类型定义`

**安全决策说明**：
- token存储在localStorage中，方便跨标签页共享
- 用户信息从后端获取，不存储敏感信息
- 提供自动刷新token的方法

---

## 步骤3：前端API封装

### 3.1 封装用户API接口

#### 规划与设计

**关键约束条款**：
1. **配置规范**：API基础路径统一配置
2. **异常处理规范**：统一错误处理
3. **代码规范**：使用TypeScript类型定义

**核心API设计**：
```typescript
export const userApi = {
  login(data: LoginDTO): Promise<ApiResponse<LoginResponse>>
  register(data: RegisterDTO): Promise<ApiResponse<UserVO>>
  getCurrentUser(): Promise<ApiResponse<UserVO>>
  getUserList(params: UserListParams): Promise<ApiResponse<UserListResponse>>
  updateUser(id: number, data: UpdateUserDTO): Promise<ApiResponse<UserVO>>
  updateUserStatus(id: number, status: UserStatus): Promise<ApiResponse<void>>
  deleteUser(id: number): Promise<ApiResponse<void>>
  refreshToken(refreshToken: string): Promise<ApiResponse<TokenResponse>>
  logout(): Promise<ApiResponse<void>>
}
```

#### 实现与编码

**文件路径**：`frontend/src/api/user.ts`

**规范映射**：
- `// 遵循：配置规范-API基础路径统一配置`
- `// 遵循：异常处理规范-统一错误处理`
- `// 遵循：代码规范-TypeScript类型定义`

**安全决策说明**：
- 所有API调用都通过统一的axios实例
- 自动添加Authorization头
- 统一处理响应数据

---

## 步骤4：前端拦截器优化

### 4.1 优化Axios拦截器

#### 规划与设计

**关键约束条款**：
1. **安全规范**：自动添加Authorization头
2. **异常处理规范**：统一处理401和403错误
3. **用户体验规范**：友好的错误提示

**核心功能设计**：
1. 请求拦截器：
   - 自动添加Authorization头
   - 检查token是否即将过期
   - 自动刷新token

2. 响应拦截器：
   - 处理401错误：清除token，跳转登录页
   - 处理403错误：显示权限不足提示
   - 处理token过期：尝试刷新token
   - 统一错误提示

#### 实现与编码

**文件路径**：`frontend/src/api/index.ts`

**规范映射**：
- `// 遵循：安全规范-自动添加Authorization头`
- `// 遵循：异常处理规范-统一处理401和403错误`
- `// 遵循：用户体验规范-友好的错误提示`

**安全决策说明**：
- token即将过期时自动刷新，提升用户体验
- 401错误时清除token并跳转登录页
- 403错误时显示权限不足提示
- 使用Element Plus的Message组件显示错误信息

---

## 验证与测试

### 测试用例

#### 后端接口测试

**测试文件**：`backend/src/test/java/com/haocai/management/controller/AuthControllerTest.java`

**测试场景**：
1. 测试从JWT token获取用户ID
2. 测试令牌刷新功能
3. 测试登出功能
4. 测试token过期场景
5. 测试无效token场景

#### 前端功能测试

**测试场景**：
1. 测试登录流程
2. 测试token自动刷新
3. 测试登出流程
4. 测试401错误处理
5. 测试403错误处理

---

## 规范遵循与更新摘要

### 遵循的规范条款

| 规范类别 | 条款内容 | 应用场景 |
|---------|---------|---------|
| 安全规范 | JWT token必须经过验证才能提取用户信息 | 从token获取用户ID |
| 安全规范 | refresh token必须验证有效性 | 令牌刷新接口 |
| 安全规范 | 登出时必须清除认证信息 | 登出接口 |
| 安全规范 | 自动添加Authorization头 | Axios拦截器 |
| 异常处理规范 | token无效或过期时必须抛出明确的异常 | 所有认证相关接口 |
| 异常处理规范 | 统一异常处理 | 全局异常处理器 |
| 日志规范 | 必须记录token验证和用户信息提取的日志 | 所有认证相关操作 |
| 日志规范 | 必须记录登出操作日志 | 登出接口 |
| 配置规范 | token存储在localStorage中 | 前端Store |
| 配置规范 | API基础路径统一配置 | 前端API封装 |
| 配置规范 | access token和refresh token的过期时间要合理配置 | JWT配置 |
| 代码规范 | 使用TypeScript类型定义 | 前端代码 |
| 用户体验规范 | 友好的错误提示 | Axios拦截器 |

### 规范更新建议

1. **新增规范**：JWT token刷新机制规范
   - 建议在安全规范中添加token刷新的详细说明
   - 包括refresh token的过期时间设置
   - 包括token刷新的触发条件

2. **新增规范**：前端认证状态管理规范
   - 建议在配置规范中添加前端认证状态管理的说明
   - 包括token存储方式
   - 包括用户信息管理方式

3. **新增规范**：Axios拦截器配置规范
   - 建议在配置规范中添加Axios拦截器的详细说明
   - 包括请求拦截器的配置
   - 包括响应拦截器的配置
   - 包括错误处理的配置

---

## 给新开发者的快速指南

### 核心使用方式

1. **后端接口使用**：
   - 所有需要认证的接口都会自动从JWT token获取用户ID
   - 使用 `getCurrentUserIdFromToken()` 方法获取当前用户ID
   - token刷新接口：`POST /api/users/refresh`
   - 登出接口：`POST /api/users/logout`

2. **前端Store使用**：
   - 使用 `useAuthStore()` 获取认证状态
   - 调用 `login()` 方法进行登录
   - 调用 `logout()` 方法进行登出
   - 调用 `refreshToken()` 方法刷新token

3. **前端API使用**：
   - 使用 `userApi` 对象调用用户相关API
   - 所有API调用都会自动添加Authorization头
   - 错误会统一处理并显示提示

### 注意事项

1. **安全性**：
   - 不要在前端存储敏感信息（如密码）
   - token存储在localStorage中，注意XSS攻击
   - 定期刷新token，避免token过期

2. **用户体验**：
   - token即将过期时自动刷新，避免用户感知
   - 401错误时自动跳转登录页
   - 403错误时显示权限不足提示

3. **错误处理**：
   - 所有错误都会统一处理
   - 使用Element Plus的Message组件显示错误信息
   - 记录详细的错误日志，方便排查问题

---

## 后续步骤建议

### 在day2-plan.md中标注

1. 标记2.1 JWT工具类开发为已完成 ✅
2. 标记2.2 Spring Security配置为已完成 ✅
3. 添加新的任务项：核心认证功能开发（已完成）✅

### 集成到项目的下一步工作

1. **Redis集成**：
   - 添加Redis依赖
   - 创建Redis配置类
   - 实现令牌黑名单功能
   - 测试Redis连接和功能

2. **前端页面开发**：
   - 创建登录页面
   - 配置路由守卫
   - 创建用户管理页面
   - 前后端联调测试

3. **权限控制**：
   - 创建权限相关表
   - 实现权限验证逻辑
   - 实现权限控制UI
   - 权限控制测试

---

## 总结

### 开发成果

1. ✅ 完善了用户信息获取接口，从JWT token获取用户ID
2. ✅ 添加了令牌刷新接口，支持自动刷新token
3. ✅ 添加了登出接口，支持用户主动登出
4. ✅ 完善了用户认证Store，统一管理认证状态
5. ✅ 封装了用户API接口，统一API调用方式
6. ✅ 优化了Axios拦截器，完善错误处理

### 规范遵循

- 严格遵循了development-standards.md中的所有相关规范
- 在代码关键位置标注了规范遵循注释
- 对关键安全决策进行了详细解释

### 教学价值

- 详细的开发过程记录，可作为团队核心教材
- 清晰的规范映射，帮助新开发者理解规范应用
- 完整的测试用例，确保功能正确性

---

**文档创建时间**：2026年1月5日  
**最后更新时间**：2026年1月5日
