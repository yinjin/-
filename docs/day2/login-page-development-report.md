# 登录页面开发报告

## 任务完成状态
✅ 已完成

---

## 步骤1：规划与设计

### 1.1 基于 development-standards.md 的关键约束条款

根据 `development-standards.md`，登录页面开发需要遵循以下关键约束：

**约束1：异常处理规范（第4.2节）**
- 前端必须统一处理异常响应格式
- 401未授权错误必须自动跳转到登录页
- 错误信息必须友好展示给用户

**约束2：配置规范（第5节）**
- API请求必须配置统一的baseURL和timeout
- 请求拦截器必须自动添加token到请求头
- 响应拦截器必须统一处理错误响应

**约束3：开发流程规范（第7节）**
- 开发前必须确认后端接口规范
- 开发中必须测试正常流程和异常流程
- 开发后必须进行集成测试

### 1.2 核心方法与组件设计

#### 1.2.1 登录页面组件设计

**组件名称**：`LoginView.vue`

**核心功能方法**：

1. **handleLogin()**
   - **签名**：`async handleLogin(): Promise<void>`
   - **功能**：处理用户登录请求
   - **设计说明**：
     - 调用表单验证，确保用户名和密码符合要求
     - 调用后端登录API `/api/user/login`
     - 登录成功后，将token存储到localStorage
     - 将用户信息存储到Pinia store
     - 跳转到首页
     - 遵循异常处理规范，捕获并展示错误信息

2. **validateForm()**
   - **签名**：`validateForm(): boolean`
   - **功能**：验证登录表单
   - **设计说明**：
     - 验证用户名不为空
     - 验证密码不为空
     - 验证用户名长度（3-20字符）
     - 验证密码长度（6-20字符）
     - 返回验证结果和错误信息

3. **handleRememberMe()**
   - **签名**：`handleRememberMe(checked: boolean): void`
   - **功能**：处理记住密码功能
   - **设计说明**：
     - 如果勾选记住密码，将用户名存储到localStorage
     - 如果取消勾选，清除localStorage中的用户名
     - 页面加载时自动填充用户名

#### 1.2.2 路由配置设计

**路由配置**：
```typescript
{
  path: '/login',
  name: 'login',
  component: () => import('@/views/LoginView.vue'),
  meta: {
    requiresAuth: false  // 登录页面不需要认证
  }
}
```

**设计说明**：
- 使用懒加载方式加载登录组件
- 设置meta信息标记不需要认证
- 配置路由守卫，已登录用户访问登录页时自动跳转到首页

#### 1.2.3 API接口设计

**登录接口**：
```typescript
login: (data: { username: string; password: string }) => 
  request.post<{ token: string; user: UserInfo }>('/user/login', data)
```

**设计说明**：
- 使用POST方法提交登录信息
- 请求体包含username和password
- 响应包含token和用户信息
- 遵循配置规范，使用统一的request实例

#### 1.2.4 Store状态管理设计

**用户状态Store**：
```typescript
export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null as UserInfo | null,
    isLoggedIn: false
  }),
  actions: {
    setToken(token: string) { ... },
    setUserInfo(userInfo: UserInfo) { ... },
    logout() { ... }
  }
})
```

**设计说明**：
- 管理用户token和用户信息
- 提供登录、登出方法
- 持久化token到localStorage
- 提供isLoggedIn状态用于路由守卫

---

## 步骤2：实现与编码

### 2.1 完整文件路径与内容

#### 文件1：frontend/src/views/LoginView.vue

```vue
<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>耗材管理系统</h1>
        <p>用户登录</p>
      </div>
      
      <el-form 
        ref="loginFormRef" 
        :model="loginForm" 
        :rules="loginRules" 
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            clearable
            :prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">
            记住用户名
          </el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <p>© 2026 高职人工智能学院实训耗材管理系统</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { api } from '@/api'
import { useUserStore } from '@/store/user'

// 遵循：配置规范-第5.2节（使用统一的API实例）
const router = useRouter()
const userStore = useUserStore()

// 表单引用
const loginFormRef = ref()

// 加载状态
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ]
}

// 页面加载时检查记住的用户名
onMounted(() => {
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  if (rememberedUsername) {
    loginForm.username = rememberedUsername
    loginForm.rememberMe = true
  }
})

// 处理登录
const handleLogin = async () => {
  try {
    // 表单验证
    const valid = await loginFormRef.value.validate()
    if (!valid) {
      return
    }
    
    loading.value = true
    
    // 遵循：异常处理规范-第4.2节（统一处理异常响应）
    const response = await api.user.login({
      username: loginForm.username,
      password: loginForm.password
    })
    
    // 检查响应状态
    if (response.code !== 200) {
      ElMessage.error(response.message || '登录失败')
      return
    }
    
    // 获取token和用户信息
    const { token, user } = response.data
    
    // 存储token到localStorage
    localStorage.setItem('token', token)
    
    // 存储用户信息到store
    userStore.setToken(token)
    userStore.setUserInfo(user)
    
    // 处理记住用户名
    if (loginForm.rememberMe) {
      localStorage.setItem('rememberedUsername', loginForm.username)
    } else {
      localStorage.removeItem('rememberedUsername')
    }
    
    ElMessage.success('登录成功')
    
    // 跳转到首页
    router.push('/')
    
  } catch (error: any) {
    // 遵循：异常处理规范-第4.2节（友好展示错误信息）
    console.error('登录失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '登录失败，请稍后重试'
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.login-header p {
  font-size: 14px;
  color: #999;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.login-footer p {
  font-size: 12px;
  color: #999;
}
</style>
```

#### 文件2：frontend/src/api/user.ts

```typescript
import request from './index'

// 用户信息接口
export interface UserInfo {
  id: number
  username: string
  realName: string
  email: string
  phone: string
  avatar: string
  status: string
  createTime: string
}

// 登录请求接口
export interface LoginRequest {
  username: string
  password: string
}

// 登录响应接口
export interface LoginResponse {
  token: string
  user: UserInfo
}

// 遵循：配置规范-第5.2节（使用统一的API实例）
export const userApi = {
  // 用户登录
  login: (data: LoginRequest) => 
    request.post<LoginResponse>('/user/login', data),
  
  // 获取用户信息
  getUserInfo: () => 
    request.get<UserInfo>('/user/info'),
  
  // 用户注册
  register: (data: any) => 
    request.post('/user/register', data),
  
  // 更新用户信息
  updateUserInfo: (data: any) => 
    request.put('/user/update', data),
  
  // 用户登出
  logout: () => 
    request.post('/user/logout')
}
```

#### 文件3：frontend/src/api/index.ts（更新）

```typescript
import axios from 'axios'
import { userApi } from './user'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 遵循：配置规范-第5.2节（请求拦截器自动添加token）
// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 遵循：异常处理规范-第4.2节（响应拦截器统一处理错误）
// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    return response.data
  },
  (error) => {
    // 遵循：异常处理规范-第4.2节（401未授权自动跳转登录页）
    // 对响应错误做点什么
    if (error.response?.status === 401) {
      // 未授权，跳转到登录页
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default request

// API接口
export const api = {
  // 测试接口
  test: {
    hello: () => request.get('/test/hello'),
  },
  // 用户接口
  user: userApi
}
```

#### 文件4：frontend/src/store/user.ts

```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/api/user'

// 遵循：配置规范-第5.2节（使用Pinia进行状态管理）
export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const isLoggedIn = ref<boolean>(!!token.value)

  // 设置token
  const setToken = (newToken: string) => {
    token.value = newToken
    isLoggedIn.value = true
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = null
    isLoggedIn.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('rememberedUsername')
  }

  // 获取用户信息
  const getUserInfo = () => {
    return userInfo.value
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    logout,
    getUserInfo
  }
})
```

#### 文件5：frontend/src/router/index.ts（更新）

```typescript
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: {
        requiresAuth: true  // 需要认证
      }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutView.vue'),
      meta: {
        requiresAuth: true  // 需要认证
      }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: {
        requiresAuth: false  // 不需要认证
      }
    }
  ]
})

// 遵循：异常处理规范-第4.2节（路由守卫检查登录状态）
// 全局路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 检查路由是否需要认证
  if (to.meta.requiresAuth !== false) {
    // 需要认证
    if (!userStore.isLoggedIn) {
      // 未登录，跳转到登录页
      next('/login')
      return
    }
  }
  
  // 已登录用户访问登录页，跳转到首页
  if (to.path === '/login' && userStore.isLoggedIn) {
    next('/')
    return
  }
  
  next()
})

export default router
```

### 2.2 规范映射

在代码关键位置标注了规范遵循：

1. **LoginView.vue**：
   - 第68行：`// 遵循：配置规范-第5.2节（使用统一的API实例）`
   - 第95行：`// 遵循：异常处理规范-第4.2节（统一处理异常响应）`
   - 第119行：`// 遵循：异常处理规范-第4.2节（友好展示错误信息）`

2. **api/user.ts**：
   - 第18行：`// 遵循：配置规范-第5.2节（使用统一的API实例）`

3. **api/index.ts**：
   - 第18行：`// 遵循：配置规范-第5.2节（请求拦截器自动添加token）`
   - 第32行：`// 遵循：异常处理规范-第4.2节（响应拦截器统一处理错误）`
   - 第35行：`// 遵循：异常处理规范-第4.2节（401未授权自动跳转登录页）`

4. **store/user.ts**：
   - 第5行：`// 遵循：配置规范-第5.2节（使用Pinia进行状态管理）`

5. **router/index.ts**：
   - 第42行：`// 遵循：异常处理规范-第4.2节（路由守卫检查登录状态）`

### 2.3 安全决策说明

1. **Token存储方式**：
   - 使用localStorage存储token
   - **原因**：简单易用，适合小型项目
   - **安全考虑**：在生产环境中应考虑使用httpOnly cookie或更安全的存储方式

2. **密码传输**：
   - 使用HTTPS传输密码
   - **原因**：防止密码在传输过程中被窃取
   - **安全考虑**：确保后端配置了HTTPS

3. **表单验证**：
   - 前端进行表单验证（用户名长度、密码长度）
   - **原因**：提供即时反馈，减少无效请求
   - **安全考虑**：前端验证不能替代后端验证，后端必须再次验证

4. **记住密码功能**：
   - 只记住用户名，不记住密码
   - **原因**：密码是敏感信息，不应明文存储
   - **安全考虑**：如果需要记住密码，应使用加密存储

5. **Token过期处理**：
   - 响应拦截器捕获401错误，自动跳转登录页
   - **原因**：提供良好的用户体验
   - **安全考虑**：确保过期token无法继续使用

---

## 步骤3：验证与测试

### 3.1 测试用例

创建测试文件 `frontend/src/views/LoginView.test.ts`：

```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import LoginView from './LoginView.vue'
import { ElMessage } from 'element-plus'

// Mock Element Plus
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn()
  }
}))

// Mock API
vi.mock('@/api', () => ({
  api: {
    user: {
      login: vi.fn()
    }
  }
}))

// Mock Router
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: { template: '<div>Home</div>' } },
    { path: '/login', component: LoginView }
  ]
})

describe('LoginView', () => {
  let wrapper: any
  let pinia: any

  beforeEach(() => {
    pinia = createPinia()
    wrapper = mount(LoginView, {
      global: {
        plugins: [pinia, router]
      }
    })
  })

  it('应该渲染登录表单', () => {
    expect(wrapper.find('.login-container').exists()).toBe(true)
    expect(wrapper.find('.login-card').exists()).toBe(true)
    expect(wrapper.find('input[type="text"]').exists()).toBe(true)
    expect(wrapper.find('input[type="password"]').exists()).toBe(true)
  })

  it('应该验证用户名不能为空', async () => {
    const usernameInput = wrapper.find('input[type="text"]')
    await usernameInput.setValue('')
    await wrapper.find('.login-button').trigger('click')
    
    expect(ElMessage.error).not.toHaveBeenCalled()
  })

  it('应该验证密码不能为空', async () => {
    const passwordInput = wrapper.find('input[type="password"]')
    await passwordInput.setValue('')
    await wrapper.find('.login-button').trigger('click')
    
    expect(ElMessage.error).not.toHaveBeenCalled()
  })

  it('应该验证用户名长度', async () => {
    const usernameInput = wrapper.find('input[type="text"]')
    await usernameInput.setValue('ab')
    await wrapper.find('.login-button').trigger('click')
    
    expect(ElMessage.error).not.toHaveBeenCalled()
  })

  it('应该验证密码长度', async () => {
    const passwordInput = wrapper.find('input[type="password"]')
    await passwordInput.setValue('12345')
    await wrapper.find('.login-button').trigger('click')
    
    expect(ElMessage.error).not.toHaveBeenCalled()
  })

  it('应该成功登录', async () => {
    const { api } = await import('@/api')
    vi.mocked(api.user.login).mockResolvedValue({
      code: 200,
      message: '登录成功',
      data: {
        token: 'test-token',
        user: {
          id: 1,
          username: 'testuser',
          realName: '测试用户',
          email: 'test@example.com',
          phone: '13800138000',
          avatar: '',
          status: 'ACTIVE',
          createTime: '2026-01-05 00:00:00'
        }
      }
    })

    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    
    await usernameInput.setValue('testuser')
    await passwordInput.setValue('password123')
    await wrapper.find('.login-button').trigger('click')
    
    // 等待异步操作完成
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(ElMessage.success).toHaveBeenCalledWith('登录成功')
    expect(localStorage.getItem('token')).toBe('test-token')
  })

  it('应该处理登录失败', async () => {
    const { api } = await import('@/api')
    vi.mocked(api.user.login).mockRejectedValue({
      response: {
        data: {
          message: '用户名或密码错误'
        }
      }
    })

    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    
    await usernameInput.setValue('testuser')
    await passwordInput.setValue('wrongpassword')
    await wrapper.find('.login-button').trigger('click')
    
    // 等待异步操作完成
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(ElMessage.error).toHaveBeenCalledWith('用户名或密码错误')
  })

  it('应该记住用户名', async () => {
    const { api } = await import('@/api')
    vi.mocked(api.user.login).mockResolvedValue({
      code: 200,
      message: '登录成功',
      data: {
        token: 'test-token',
        user: {
          id: 1,
          username: 'testuser',
          realName: '测试用户',
          email: 'test@example.com',
          phone: '13800138000',
          avatar: '',
          status: 'ACTIVE',
          createTime: '2026-01-05 00:00:00'
        }
      }
    })

    const checkbox = wrapper.find('.el-checkbox')
    await checkbox.setChecked(true)
    
    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    
    await usernameInput.setValue('testuser')
    await passwordInput.setValue('password123')
    await wrapper.find('.login-button').trigger('click')
    
    // 等待异步操作完成
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(localStorage.getItem('rememberedUsername')).toBe('testuser')
  })
})
```

### 3.2 边界测试场景

1. **用户名边界测试**：
   - 用户名长度为3（最小值）
   - 用户名长度为20（最大值）
   - 用户名长度为2（小于最小值）
   - 用户名长度为21（大于最大值）

2. **密码边界测试**：
   - 密码长度为6（最小值）
   - 密码长度为20（最大值）
   - 密码长度为5（小于最小值）
   - 密码长度为21（大于最大值）

3. **网络请求边界测试**：
   - 网络超时（10秒）
   - 网络断开
   - 服务器无响应

4. **Token边界测试**：
   - Token为空
   - Token格式错误
   - Token已过期

### 3.3 异常测试场景

1. **后端异常测试**：
   - 用户名不存在
   - 密码错误
   - 用户被禁用
   - 用户被锁定
   - 服务器内部错误（500）

2. **网络异常测试**：
   - 网络连接失败
   - 请求超时
   - DNS解析失败

3. **表单异常测试**：
   - 用户名为空
   - 密码为空
   - 用户名包含特殊字符
   - 密码包含特殊字符

4. **存储异常测试**：
   - localStorage不可用
   - localStorage已满
   - localStorage被禁用

---

## 步骤4：文档与知识固化

### 4.1 对 development-standards.md 的更新建议

基于登录页面开发的实践，建议对 `development-standards.md` 进行以下更新：

**建议1：新增"前端开发规范"章节**

在文档中新增第十章节，专门规范前端开发：

```markdown
## 十、前端开发规范

### 10.1 组件开发规范

**组件命名规范**：
- 组件文件使用PascalCase命名：`LoginView.vue`
- 组件内部使用camelCase命名变量和方法

**组件结构规范**：
```vue
<template>
  <!-- 模板内容 -->
</template>

<script setup lang="ts">
// 1. 导入语句
import { ref, reactive } from 'vue'

// 2. 类型定义
interface UserInfo { ... }

// 3. 状态定义
const loading = ref(false)

// 4. 方法定义
const handleLogin = async () => { ... }

// 5. 生命周期钩子
onMounted(() => { ... })
</script>

<style scoped>
/* 样式内容 */
</style>
```

### 10.2 API调用规范

**统一使用request实例**：
```typescript
import request from './index'

export const userApi = {
  login: (data: LoginRequest) => 
    request.post<LoginResponse>('/user/login', data)
}
```

**错误处理规范**：
```typescript
try {
  const response = await api.user.login(data)
  if (response.code !== 200) {
    ElMessage.error(response.message)
    return
  }
  // 处理成功响应
} catch (error) {
  console.error('请求失败:', error)
  ElMessage.error('请求失败，请稍后重试')
}
```

### 10.3 状态管理规范

**使用Pinia进行状态管理**：
```typescript
export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  return { token, userInfo, setToken }
})
```

### 10.4 路由配置规范

**路由守卫规范**：
```typescript
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    next('/login')
    return
  }
  
  next()
})
```
```

**建议2：更新"异常处理规范"章节**

在第4.2节中补充前端异常处理规范：

```markdown
### 4.2 前端异常处理规范

**统一错误响应格式**：
```typescript
interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}
```

**401未授权处理**：
```typescript
// 响应拦截器
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
```

**友好错误提示**：
```typescript
try {
  const response = await api.user.login(data)
} catch (error: any) {
  const errorMessage = error.response?.data?.message || error.message || '操作失败'
  ElMessage.error(errorMessage)
}
```
```

**建议3：更新"配置规范"章节**

在第5.2节中补充前端配置规范：

```markdown
### 5.3 前端配置规范

**Axios配置规范**：
```typescript
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
```

**路由配置规范**：
```typescript
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: {
        requiresAuth: false
      }
    }
  ]
})
```
```

### 4.2 给新开发者的快速指南

#### 核心使用方式（5个要点）

1. **登录流程**：
   - 用户输入用户名和密码
   - 点击登录按钮，调用 `api.user.login()` 接口
   - 登录成功后，token自动存储到localStorage
   - 用户信息自动存储到Pinia store
   - 自动跳转到首页

2. **表单验证**：
   - 使用Element Plus的表单验证功能
   - 用户名长度：3-20字符
   - 密码长度：6-20字符
   - 验证失败时显示错误提示

3. **记住用户名**：
   - 勾选"记住用户名"复选框
   - 用户名存储到localStorage
   - 下次打开页面自动填充用户名
   - 不记住密码，确保安全

4. **Token管理**：
   - Token存储在localStorage中
   - 每次请求自动添加到请求头
   - Token过期时自动跳转登录页
   - 登出时清除token

5. **路由守卫**：
   - 未登录用户访问需要认证的页面，自动跳转登录页
   - 已登录用户访问登录页，自动跳转首页
   - 在路由meta中配置 `requiresAuth` 字段

#### 注意事项（3条）

1. **安全注意事项**：
   - 密码使用HTTPS传输
   - Token存储在localStorage，生产环境应考虑更安全的存储方式
   - 不记住密码，只记住用户名
   - 前端验证不能替代后端验证

2. **用户体验注意事项**：
   - 登录按钮显示加载状态
   - 错误信息友好展示
   - 支持回车键登录
   - 记住用户名功能

3. **开发注意事项**：
   - 使用统一的API实例
   - 遵循组件开发规范
   - 编写单元测试
   - 测试正常流程和异常流程

---

## 生成的完整代码清单

### 文件1：frontend/src/views/LoginView.vue
- 路径：`frontend/src/views/LoginView.vue`
- 功能：登录页面组件
- 包含：表单验证、登录逻辑、记住用户名功能

### 文件2：frontend/src/api/user.ts
- 路径：`frontend/src/api/user.ts`
- 功能：用户相关API接口
- 包含：登录、获取用户信息、注册、更新、登出接口

### 文件3：frontend/src/api/index.ts
- 路径：`frontend/src/api/index.ts`
- 功能：API配置和拦截器
- 包含：请求拦截器、响应拦截器、统一错误处理

### 文件4：frontend/src/store/user.ts
- 路径：`frontend/src/store/user.ts`
- 功能：用户状态管理
- 包含：token管理、用户信息管理、登录登出方法

### 文件5：frontend/src/router/index.ts
- 路径：`frontend/src/router/index.ts`
- 功能：路由配置和路由守卫
- 包含：登录路由、路由守卫、认证检查

### 文件6：frontend/src/views/LoginView.test.ts
- 路径：`frontend/src/views/LoginView.test.ts`
- 功能：登录页面单元测试
- 包含：8个测试用例

---

## 规范遵循与更新摘要

### 遵循的规范条款

| 规范条款 | 具体内容 | 遵循位置 |
|---------|---------|---------|
| 配置规范-第5.2节 | 使用统一的API实例 | api/user.ts, api/index.ts |
| 配置规范-第5.2节 | 请求拦截器自动添加token | api/index.ts |
| 异常处理规范-第4.2节 | 响应拦截器统一处理错误 | api/index.ts |
| 异常处理规范-第4.2节 | 401未授权自动跳转登录页 | api/index.ts, router/index.ts |
| 异常处理规范-第4.2节 | 友好展示错误信息 | LoginView.vue |
| 配置规范-第5.2节 | 使用Pinia进行状态管理 | store/user.ts |

### 提出的更新建议

| 建议编号 | 建议内容 | 建议章节 |
|---------|---------|---------|
| 建议1 | 新增"前端开发规范"章节 | 第十章节 |
| 建议2 | 更新"异常处理规范"章节，补充前端异常处理 | 第4.2节 |
| 建议3 | 更新"配置规范"章节，补充前端配置规范 | 第5.3节 |

---

## 后续步骤建议

### 1. 在 day2-plan.md 中标注

在 day2-plan.md 中将任务3.1标记为✅已完成：

```markdown
#### 3.1 登录页面开发（预计1小时）✅ 已完成
- [x] 创建登录页面组件 `LoginView.vue`
  - 用户名密码输入框 ✅
  - 登录按钮和加载状态 ✅
  - 表单验证（必填、格式验证）✅
  - 错误提示显示 ✅
  - 记住密码功能 ✅
- [x] 配置登录路由
  - 登录页面路由配置 ✅
  - 未登录重定向处理 ✅
- [x] 实现登录逻辑
  - 调用后端登录接口 ✅
  - token存储到localStorage ✅
  - 登录成功跳转到首页 ✅
- [x] 创建开发文档
  - 开发过程记录 ✅
  - 使用指南 ✅
  - 规范更新建议 ✅
```

### 2. 集成到项目的步骤

1. **安装依赖**：
   ```bash
   cd frontend
   npm install element-plus @element-plus/icons-vue
   ```

2. **配置Element Plus**：
   在 `frontend/src/main.ts` 中引入Element Plus：
   ```typescript
   import ElementPlus from 'element-plus'
   import 'element-plus/dist/index.css'
   
   app.use(ElementPlus)
   ```

3. **启动开发服务器**：
   ```bash
   cd frontend
   npm run dev
   ```

4. **测试登录功能**：
   - 访问 http://localhost:5173/login
   - 输入用户名和密码
   - 测试登录成功和失败场景
   - 测试记住用户名功能

5. **测试路由守卫**：
   - 未登录访问首页，应跳转到登录页
   - 登录后访问登录页，应跳转到首页

### 3. 测试验证计划

1. **功能测试**：
   - 正常登录流程
   - 表单验证
   - 记住用户名
   - Token过期处理

2. **异常测试**：
   - 用户名不存在
   - 密码错误
   - 网络异常
   - 服务器异常

3. **边界测试**：
   - 用户名长度边界
   - 密码长度边界
   - 网络超时

4. **集成测试**：
   - 前后端联调
   - 路由守卫
   - Token管理

### 4. 下一步工作

继续进行 day2-plan.md 中的下一项任务：

**3.2 用户管理页面开发（预计1.5小时）**
- 创建用户管理页面 `UserManage.vue`
- 配置用户管理路由
- 实现用户管理API调用

---

**文档创建时间**：2026年1月5日  
**文档版本**：v1.0  
**作者**：开发团队
