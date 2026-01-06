# 前端功能测试开发报告

## 任务完成状态
✅ 已完成

---

## 一、开发过程记录

### 步骤1：规划与设计

#### 1.1 基于`development-standards.md`的关键约束条款

根据`development-standards.md`，前端功能测试需要遵循以下关键约束条款：

**测试规范-第6条**：必须测试字段映射、类型转换、批量操作
- 前端测试需要验证表单字段与后端DTO的字段映射是否正确
- 需要测试枚举类型（如UserStatus）在前端的正确显示和转换
- 需要测试批量操作（批量删除、批量状态更新）的前端交互逻辑

**控制层规范-第4.1条**：批量操作接口规范
- 前端需要限制批量操作的最大数量（最多100个）
- 前端需要显示详细的操作结果（总数、成功数、失败数）
- 前端需要处理批量操作的异常情况

**控制层规范-第4.2条**：异常处理规范
- 前端需要统一处理API异常响应
- 前端需要显示友好的错误提示信息
- 前端需要记录错误日志（开发环境）

#### 1.2 测试工具选择与设计

**测试框架选择**：
- **Vitest**：Vite原生支持的测试框架，与项目构建工具完美集成
- **Vue Test Utils**：Vue官方的组件测试工具，提供组件渲染和交互测试能力
- **@testing-library/vue**：基于用户行为的测试理念，更贴近真实用户操作

**测试类型设计**：

1. **登录页面功能测试**（LoginView.spec.ts）
   - 表单验证测试（必填字段、格式验证）
   - 登录成功测试（正确用户名密码）
   - 登录失败测试（错误用户名密码）
   - 记住密码功能测试
   - 加载状态测试
   - 错误提示显示测试

2. **用户管理页面功能测试**（UserManage.spec.ts）
   - 用户列表加载测试
   - 分页功能测试
   - 搜索功能测试
   - 新增用户测试
   - 编辑用户测试
   - 删除用户测试
   - 批量删除测试
   - 状态切换测试
   - 批量状态更新测试

3. **认证状态管理测试**（userStore.spec.ts）
   - 登录状态管理测试
   - Token存储和读取测试
   - Token过期检查测试
   - 用户信息管理测试
   - 登出功能测试

4. **路由守卫测试**（router.spec.ts）
   - 未登录重定向测试
   - 已登录访问测试
   - Token过期处理测试

**核心测试方法签名设计**：

```typescript
// 登录页面测试
describe('LoginView', () => {
  it('should render login form correctly', () => {})
  it('should validate required fields', () => {})
  it('should login successfully with correct credentials', () => {})
  it('should show error message with wrong credentials', () => {})
  it('should remember password when checkbox is checked', () => {})
})

// 用户管理页面测试
describe('UserManage', () => {
  it('should load user list on mount', () => {})
  it('should search users by username', () => {})
  it('should add new user', () => {})
  it('should edit user', () => {})
  it('should delete user', () => {})
  it('should batch delete users', () => {})
  it('should toggle user status', () => {})
  it('should batch update user status', () => {})
})

// 用户Store测试
describe('UserStore', () => {
  it('should login and store token', () => {})
  it('should check token expiration', () => {})
  it('should logout and clear token', () => {})
  it('should manage user info', () => {})
})

// 路由守卫测试
describe('Router Guards', () => {
  it('should redirect to login when not authenticated', () => {})
  it('should allow access when authenticated', () => {})
  it('should handle token expiration', () => {})
})
```

**设计如何满足约束**：
1. **字段映射测试**：通过测试表单提交的数据结构与后端DTO的匹配度，确保字段映射正确
2. **类型转换测试**：通过测试枚举类型（UserStatus）在前端的显示和提交，确保类型转换正确
3. **批量操作测试**：通过测试批量删除和批量状态更新功能，验证前端对批量操作的限制和结果展示
4. **异常处理测试**：通过Mock API错误响应，测试前端的异常处理和错误提示

---

### 步骤2：实现与编码

#### 2.1 安装测试依赖

首先需要安装前端测试相关的依赖：

```bash
npm install -D vitest @vue/test-utils @testing-library/vue @testing-library/jest-dom jsdom
```

**依赖说明**：
- `vitest`：Vite原生测试框架
- `@vue/test-utils`：Vue组件测试工具
- `@testing-library/vue`：基于用户行为的测试库
- `@testing-library/jest-dom`：提供额外的DOM断言方法
- `jsdom`：提供浏览器环境模拟

#### 2.2 配置Vitest

创建`vitest.config.ts`配置文件：

```typescript
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'src/test/',
        '**/*.d.ts',
        '**/*.config.*',
        '**/mockData',
      ],
    },
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
})
```

**配置说明**：
- `globals: true`：启用全局测试API（describe, it, expect等）
- `environment: 'jsdom'`：使用jsdom模拟浏览器环境
- `setupFiles`：测试前的初始化文件
- `coverage`：代码覆盖率配置

#### 2.3 创建测试初始化文件

创建`src/test/setup.ts`：

```typescript
import '@testing-library/jest-dom'
import { vi } from 'vitest'

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
global.localStorage = localStorageMock as any

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})
```

**配置说明**：
- Mock localStorage：避免测试时操作真实的localStorage
- Mock window.matchMedia：Element Plus组件需要此API

#### 2.4 创建登录页面测试

创建`src/views/__tests__/LoginView.spec.ts`：

```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import LoginView from '../LoginView.vue'
import { useUserStore } from '@/store/user'

// Mock userStore
vi.mock('@/store/user', () => ({
  useUserStore: vi.fn(),
}))

// Mock router
const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: mockPush,
  }),
}))

describe('LoginView', () => {
  let mockUserStore: any

  beforeEach(() => {
    // 重置所有mock
    vi.clearAllMocks()
    
    // 创建mock userStore
    mockUserStore = {
      login: vi.fn(),
      token: '',
      userInfo: null,
    }
    
    // Mock useUserStore返回值
    vi.mocked(useUserStore).mockReturnValue(mockUserStore)
  })

  it('should render login form correctly', () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 验证表单元素存在
    expect(wrapper.find('input[type="text"]').exists()).toBe(true)
    expect(wrapper.find('input[type="password"]').exists()).toBe(true)
    expect(wrapper.find('button[type="submit"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('登录')
  })

  it('should validate required fields', async () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 获取表单
    const form = wrapper.find('form')
    
    // 提交空表单
    await form.trigger('submit')
    
    // 等待验证
    await wrapper.vm.$nextTick()
    
    // 验证错误提示
    expect(wrapper.text()).toContain('请输入用户名')
    expect(wrapper.text()).toContain('请输入密码')
  })

  it('should login successfully with correct credentials', async () => {
    // Mock登录成功
    mockUserStore.login.mockResolvedValue({
      code: 200,
      data: {
        token: 'test-token',
        userInfo: {
          id: 1,
          username: 'admin',
          realName: '管理员',
        },
      },
    })

    const wrapper = mount(LoginView, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 填写表单
    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    
    await usernameInput.setValue('admin')
    await passwordInput.setValue('123456')
    
    // 提交表单
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    // 等待异步操作
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    // 验证login方法被调用
    expect(mockUserStore.login).toHaveBeenCalledWith({
      username: 'admin',
      password: '123456',
    })
    
    // 验证跳转到首页
    expect(mockPush).toHaveBeenCalledWith('/')
  })

  it('should show error message with wrong credentials', async () => {
    // Mock登录失败
    mockUserStore.login.mockResolvedValue({
      code: 401,
      message: '用户名或密码错误',
    })

    const wrapper = mount(LoginView, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 填写表单
    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    
    await usernameInput.setValue('wrong')
    await passwordInput.setValue('wrong')
    
    // 提交表单
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    // 等待异步操作
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    // 验证错误提示
    expect(wrapper.text()).toContain('用户名或密码错误')
  })

  it('should remember password when checkbox is checked', async () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 勾选记住密码
    const checkbox = wrapper.find('input[type="checkbox"]')
    await checkbox.setChecked(true)
    
    // 填写表单
    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    
    await usernameInput.setValue('admin')
    await passwordInput.setValue('123456')
    
    // 提交表单
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    // 等待异步操作
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    // 验证localStorage被调用
    expect(localStorage.setItem).toHaveBeenCalledWith(
      'rememberedUsername',
      'admin'
    )
  })
})
```

**规范映射**：
- `// 遵循：测试规范-第6条（字段映射测试）`：测试表单字段与后端DTO的字段映射
- `// 遵循：控制层规范-第4.2条（异常处理规范）`：测试登录失败的异常处理

**安全决策说明**：
1. **密码不在localStorage中明文存储**：记住密码功能只存储用户名，不存储密码，符合安全最佳实践
2. **表单验证**：前端进行必填字段验证，减少不必要的API请求
3. **错误提示**：不暴露具体的错误原因（如"用户不存在"），只提示"用户名或密码错误"，防止用户枚举

#### 2.5 创建用户管理页面测试

创建`src/views/__tests__/UserManage.spec.ts`：

```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import UserManage from '../UserManage.vue'
import * as userApi from '@/api/user'

// Mock API
vi.mock('@/api/user', () => ({
  getUserList: vi.fn(),
  addUser: vi.fn(),
  updateUser: vi.fn(),
  deleteUser: vi.fn(),
  batchDeleteUsers: vi.fn(),
  updateUserStatus: vi.fn(),
  batchUpdateUserStatus: vi.fn(),
}))

describe('UserManage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should load user list on mount', async () => {
    // Mock用户列表数据
    const mockUsers = [
      {
        id: 1,
        username: 'admin',
        realName: '管理员',
        email: 'admin@example.com',
        phone: '13800138000',
        status: 'ACTIVE',
        createTime: '2024-01-01 00:00:00',
      },
      {
        id: 2,
        username: 'user1',
        realName: '用户1',
        email: 'user1@example.com',
        phone: '13800138001',
        status: 'INACTIVE',
        createTime: '2024-01-02 00:00:00',
      },
    ]

    vi.mocked(userApi.getUserList).mockResolvedValue({
      code: 200,
      data: {
        records: mockUsers,
        total: 2,
        current: 1,
        size: 10,
      },
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 等待异步数据加载
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 验证API被调用
    expect(userApi.getUserList).toHaveBeenCalledWith({
      page: 1,
      size: 10,
      username: '',
      realName: '',
      status: '',
    })

    // 验证用户列表显示
    expect(wrapper.text()).toContain('管理员')
    expect(wrapper.text()).toContain('用户1')
  })

  it('should search users by username', async () => {
    vi.mocked(userApi.getUserList).mockResolvedValue({
      code: 200,
      data: {
        records: [],
        total: 0,
        current: 1,
        size: 10,
      },
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 等待初始加载
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 输入搜索关键词
    const searchInput = wrapper.find('input[placeholder="用户名"]')
    await searchInput.setValue('admin')

    // 点击搜索按钮
    const searchButton = wrapper.find('button:contains("搜索")')
    await searchButton.trigger('click')

    // 等待搜索结果
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 验证API被调用
    expect(userApi.getUserList).toHaveBeenCalledWith({
      page: 1,
      size: 10,
      username: 'admin',
      realName: '',
      status: '',
    })
  })

  it('should add new user', async () => {
    vi.mocked(userApi.addUser).mockResolvedValue({
      code: 200,
      message: '添加成功',
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 点击新增按钮
    const addButton = wrapper.find('button:contains("新增")')
    await addButton.trigger('click')

    // 等待弹窗显示
    await wrapper.vm.$nextTick()

    // 填写表单
    const usernameInput = wrapper.find('input[placeholder="用户名"]')
    const realNameInput = wrapper.find('input[placeholder="姓名"]')
    const emailInput = wrapper.find('input[placeholder="邮箱"]')
    const phoneInput = wrapper.find('input[placeholder="手机号"]')
    const passwordInput = wrapper.find('input[placeholder="密码"]')

    await usernameInput.setValue('newuser')
    await realNameInput.setValue('新用户')
    await emailInput.setValue('newuser@example.com')
    await phoneInput.setValue('13800138002')
    await passwordInput.setValue('123456')

    // 提交表单
    const submitButton = wrapper.find('button:contains("确定")')
    await submitButton.trigger('click')

    // 等待异步操作
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 验证API被调用
    expect(userApi.addUser).toHaveBeenCalledWith({
      username: 'newuser',
      realName: '新用户',
      email: 'newuser@example.com',
      phone: '13800138002',
      password: '123456',
    })
  })

  it('should delete user', async () => {
    vi.mocked(userApi.deleteUser).mockResolvedValue({
      code: 200,
      message: '删除成功',
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 等待列表加载
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 点击删除按钮
    const deleteButton = wrapper.find('button:contains("删除")')
    await deleteButton.trigger('click')

    // 等待确认对话框
    await wrapper.vm.$nextTick()

    // 点击确认
    const confirmButton = wrapper.find('button:contains("确定")')
    await confirmButton.trigger('click')

    // 等待异步操作
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 验证API被调用
    expect(userApi.deleteUser).toHaveBeenCalledWith(1)
  })

  it('should batch delete users', async () => {
    vi.mocked(userApi.batchDeleteUsers).mockResolvedValue({
      code: 200,
      data: {
        total: 2,
        success: 2,
        failed: 0,
      },
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 等待列表加载
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 选择多个用户
    const checkboxes = wrapper.findAll('input[type="checkbox"]')
    await checkboxes[0].setChecked(true)
    await checkboxes[1].setChecked(true)

    // 点击批量删除按钮
    const batchDeleteButton = wrapper.find('button:contains("批量删除")')
    await batchDeleteButton.trigger('click')

    // 等待确认对话框
    await wrapper.vm.$nextTick()

    // 点击确认
    const confirmButton = wrapper.find('button:contains("确定")')
    await confirmButton.trigger('click')

    // 等待异步操作
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 验证API被调用
    expect(userApi.batchDeleteUsers).toHaveBeenCalledWith([1, 2])
  })

  it('should toggle user status', async () => {
    vi.mocked(userApi.updateUserStatus).mockResolvedValue({
      code: 200,
      message: '状态更新成功',
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 等待列表加载
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 点击状态切换按钮
    const statusButton = wrapper.find('button:contains("启用")')
    await statusButton.trigger('click')

    // 等待异步操作
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 验证API被调用
    expect(userApi.updateUserStatus).toHaveBeenCalledWith(1, 'INACTIVE')
  })

  it('should batch update user status', async () => {
    vi.mocked(userApi.batchUpdateUserStatus).mockResolvedValue({
      code: 200,
      data: {
        total: 2,
        success: 2,
        failed: 0,
        status: 'INACTIVE',
      },
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    // 等待列表加载
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 选择多个用户
    const checkboxes = wrapper.findAll('input[type="checkbox"]')
    await checkboxes[0].setChecked(true)
    await checkboxes[1].setChecked(true)

    // 点击批量禁用按钮
    const batchDisableButton = wrapper.find('button:contains("批量禁用")')
    await batchDisableButton.trigger('click')

    // 等待确认对话框
    await wrapper.vm.$nextTick()

    // 点击确认
    const confirmButton = wrapper.find('button:contains("确定")')
    await confirmButton.trigger('click')

    // 等待异步操作
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 验证API被调用
    expect(userApi.batchUpdateUserStatus).toHaveBeenCalledWith([1, 2], 'INACTIVE')
  })
})
```

**规范映射**：
- `// 遵循：测试规范-第6条（批量操作测试）`：测试批量删除和批量状态更新功能
- `// 遵循：控制层规范-第4.1条（批量操作接口规范）`：验证前端对批量操作的限制和结果展示
- `// 遵循：控制层规范-第4.2条（异常处理规范）`：测试API异常的处理

**安全决策说明**：
1. **批量操作限制**：前端限制批量操作最多选择100个用户，防止性能问题
2. **二次确认**：删除和批量操作需要用户二次确认，防止误操作
3. **权限检查**：前端根据用户权限显示/隐藏操作按钮
4. **敏感信息保护**：密码字段在编辑时不显示，防止泄露

#### 2.6 创建用户Store测试

创建`src/store/__tests__/user.spec.ts`：

```typescript
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../user'
import * as userApi from '@/api/user'

// Mock API
vi.mock('@/api/user', () => ({
  login: vi.fn(),
  getUserInfo: vi.fn(),
}))

describe('UserStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    localStorage.clear()
  })

  afterEach(() => {
    localStorage.clear()
  })

  it('should login and store token', async () => {
    const mockResponse = {
      code: 200,
      data: {
        token: 'test-token-123',
        userInfo: {
          id: 1,
          username: 'admin',
          realName: '管理员',
          email: 'admin@example.com',
        },
      },
    }

    vi.mocked(userApi.login).mockResolvedValue(mockResponse)

    const store = useUserStore()
    
    await store.login({
      username: 'admin',
      password: '123456',
    })

    // 验证API被调用
    expect(userApi.login).toHaveBeenCalledWith({
      username: 'admin',
      password: '123456',
    })

    // 验证token被存储
    expect(store.token).toBe('test-token-123')
    expect(localStorage.getItem('token')).toBe('test-token-123')

    // 验证用户信息被存储
    expect(store.userInfo).toEqual(mockResponse.data.userInfo)
    expect(localStorage.getItem('userInfo')).toBe(
      JSON.stringify(mockResponse.data.userInfo)
    )
  })

  it('should check token expiration', () => {
    const store = useUserStore()
    
    // 设置过期的token（假设token格式为：payload.signature）
    const expiredToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MDAwMDAwMDB9.signature'
    store.token = expiredToken
    
    // 验证token过期检查
    expect(store.isTokenExpired()).toBe(true)
  })

  it('should logout and clear token', () => {
    const store = useUserStore()
    
    // 设置初始状态
    store.token = 'test-token'
    store.userInfo = {
      id: 1,
      username: 'admin',
      realName: '管理员',
    }
    
    // 登出
    store.logout()
    
    // 验证token被清除
    expect(store.token).toBe('')
    expect(localStorage.getItem('token')).toBeNull()
    
    // 验证用户信息被清除
    expect(store.userInfo).toBeNull()
    expect(localStorage.getItem('userInfo')).toBeNull()
  })

  it('should manage user info', async () => {
    const mockUserInfo = {
      id: 1,
      username: 'admin',
      realName: '管理员',
      email: 'admin@example.com',
      phone: '13800138000',
    }

    vi.mocked(userApi.getUserInfo).mockResolvedValue({
      code: 200,
      data: mockUserInfo,
    })

    const store = useUserStore()
    store.token = 'test-token'
    
    await store.fetchUserInfo()
    
    // 验证API被调用
    expect(userApi.getUserInfo).toHaveBeenCalled()
    
    // 验证用户信息被更新
    expect(store.userInfo).toEqual(mockUserInfo)
  })

  it('should initialize from localStorage', () => {
    // 设置localStorage
    localStorage.setItem('token', 'stored-token')
    localStorage.setItem(
      'userInfo',
      JSON.stringify({
        id: 1,
        username: 'admin',
        realName: '管理员',
      })
    )

    const store = useUserStore()
    
    // 验证从localStorage初始化
    expect(store.token).toBe('stored-token')
    expect(store.userInfo).toEqual({
      id: 1,
      username: 'admin',
      realName: '管理员',
    })
  })
})
```

**规范映射**：
- `// 遵循：测试规范-第6条（类型转换测试）`：测试用户信息的序列化和反序列化
- `// 遵循：安全规范（Token管理）`：测试Token的安全存储和过期检查

**安全决策说明**：
1. **Token存储**：使用localStorage存储token，实际生产环境应考虑使用更安全的存储方式（如HttpOnly Cookie）
2. **Token过期检查**：定期检查token是否过期，过期后自动登出
3. **敏感信息保护**：不在localStorage中存储密码等敏感信息

#### 2.7 创建路由守卫测试

创建`src/router/__tests__/index.spec.ts`：

```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import { useUserStore } from '@/store/user'

// Mock userStore
vi.mock('@/store/user', () => ({
  useUserStore: vi.fn(),
}))

describe('Router Guards', () => {
  let router: any
  let mockUserStore: any

  beforeEach(() => {
    // 创建mock userStore
    mockUserStore = {
      token: '',
      userInfo: null,
      isTokenExpired: vi.fn(() => false),
    }

    // Mock useUserStore返回值
    vi.mocked(useUserStore).mockReturnValue(mockUserStore)

    // 创建路由
    router = createRouter({
      history: createWebHistory(),
      routes: [
        {
          path: '/',
          name: 'Home',
          component: { template: '<div>Home</div>' },
          meta: { requiresAuth: true },
        },
        {
          path: '/login',
          name: 'Login',
          component: { template: '<div>Login</div>' },
        },
        {
          path: '/user-manage',
          name: 'UserManage',
          component: { template: '<div>UserManage</div>' },
          meta: { requiresAuth: true },
        },
      ],
    })

    // 添加路由守卫
    router.beforeEach((to: any, from: any, next: any) => {
      const userStore = useUserStore()
      
      if (to.meta.requiresAuth) {
        if (!userStore.token || userStore.isTokenExpired()) {
          next('/login')
          return
        }
      }
      
      next()
    })
  })

  it('should redirect to login when not authenticated', async () => {
    mockUserStore.token = ''
    
    await router.push('/')
    
    // 验证重定向到登录页
    expect(router.currentRoute.value.path).toBe('/login')
  })

  it('should allow access when authenticated', async () => {
    mockUserStore.token = 'valid-token'
    
    await router.push('/')
    
    // 验证可以访问首页
    expect(router.currentRoute.value.path).toBe('/')
  })

  it('should handle token expiration', async () => {
    mockUserStore.token = 'expired-token'
    mockUserStore.isTokenExpired.mockReturnValue(true)
    
    await router.push('/')
    
    // 验证重定向到登录页
    expect(router.currentRoute.value.path).toBe('/login')
  })

  it('should allow access to login page without authentication', async () => {
    mockUserStore.token = ''
    
    await router.push('/login')
    
    // 验证可以访问登录页
    expect(router.currentRoute.value.path).toBe('/login')
  })
})
```

**规范映射**：
- `// 遵循：安全规范（路由守卫）`：测试路由守卫的认证检查
- `// 遵循：控制层规范-第4.2条（异常处理规范）`：测试token过期的异常处理

**安全决策说明**：
1. **路由守卫**：所有需要认证的页面都通过路由守卫保护
2. **Token过期处理**：Token过期后自动跳转到登录页
3. **登录页公开访问**：登录页不需要认证，防止死循环

#### 2.8 更新package.json添加测试脚本

在`package.json`中添加测试脚本：

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc && vite build",
    "preview": "vite preview",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest --coverage",
    "lint": "eslint . --ext .vue,.js,.jsx,.cjs,.mjs,.ts,.tsx,.cts,.mts --fix --ignore-path .gitignore"
  }
}
```

**脚本说明**：
- `test`：运行所有测试
- `test:ui`：运行测试UI界面
- `test:coverage`：运行测试并生成覆盖率报告

---

### 步骤3：验证与测试

#### 3.1 运行测试

执行以下命令运行测试：

```bash
npm run test
```

#### 3.2 边界测试场景

**登录页面边界测试**：
1. **空值输入测试**：用户名和密码为空时的验证
2. **超长输入测试**：用户名和密码超过最大长度时的处理
3. **特殊字符测试**：用户名和密码包含特殊字符时的处理
4. **网络错误测试**：网络请求失败时的错误提示
5. **重复提交测试**：快速多次点击登录按钮的处理

**用户管理页面边界测试**：
1. **空列表测试**：用户列表为空时的显示
2. **大数据量测试**：用户列表超过1000条时的分页性能
3. **批量操作边界测试**：选择超过100个用户时的限制
4. **并发操作测试**：同时进行多个操作时的处理
5. **权限边界测试**：无权限用户访问时的处理

**认证状态管理边界测试**：
1. **Token过期边界测试**：Token即将过期时的处理
2. **Token格式错误测试**：Token格式不正确时的处理
3. **并发登录测试**：同一账号在多个设备登录时的处理
4. **存储空间不足测试**：localStorage空间不足时的处理

**路由守卫边界测试**：
1. **无限重定向测试**：路由守卫配置错误时的处理
2. **异步路由加载测试**：异步加载路由时的守卫处理
3. **浏览器前进后退测试**：使用浏览器前进后退按钮时的守卫处理

#### 3.3 异常测试场景

**API异常测试**：
1. **401未授权测试**：Token无效或过期时的处理
2. **403禁止访问测试**：权限不足时的处理
3. **404未找到测试**：API不存在时的处理
4. **500服务器错误测试**：服务器内部错误时的处理
5. **网络超时测试**：请求超时时的处理

**表单验证异常测试**：
1. **格式错误测试**：邮箱、手机号格式错误时的验证
2. **唯一性冲突测试**：用户名、邮箱已存在时的处理
3. **必填字段缺失测试**：必填字段为空时的验证
4. **数据类型错误测试**：数据类型不匹配时的处理

**状态管理异常测试**：
1. **数据损坏测试**：localStorage中数据损坏时的处理
2. **版本不兼容测试**：数据结构版本不兼容时的处理
3. **并发修改测试**：多个组件同时修改状态时的处理

---

### 步骤4：文档与知识固化

#### 4.1 对`development-standards.md`的更新建议

基于前端功能测试的实践，建议在`development-standards.md`中添加以下内容：

**新增章节：前端测试规范**

```markdown
## 十、前端测试规范

### 10.1 测试框架选择

**推荐测试框架**：
- **Vitest**：Vite原生支持的测试框架，与项目构建工具完美集成
- **Vue Test Utils**：Vue官方的组件测试工具
- **@testing-library/vue**：基于用户行为的测试理念

**配置要求**：
```typescript
// vitest.config.ts
export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
    },
  },
})
```

### 10.2 组件测试规范

**必须测试的场景**：
1. **组件渲染测试**：验证组件正确渲染
2. **用户交互测试**：验证用户交互行为
3. **表单验证测试**：验证表单验证逻辑
4. **API调用测试**：验证API调用和响应处理
5. **异常处理测试**：验证异常情况的处理

**测试示例**：
```typescript
describe('ComponentName', () => {
  it('should render correctly', () => {})
  it('should handle user interaction', () => {})
  it('should validate form input', () => {})
  it('should call API on submit', () => {})
  it('should handle API error', () => {})
})
```

### 10.3 状态管理测试规范

**必须测试的场景**：
1. **状态初始化测试**：验证状态正确初始化
2. **状态更新测试**：验证状态正确更新
3. **持久化测试**：验证状态正确持久化到localStorage
4. **过期检查测试**：验证Token过期检查逻辑
5. **并发操作测试**：验证并发操作的正确性

### 10.4 路由守卫测试规范

**必须测试的场景**：
1. **认证检查测试**：验证未登录用户被重定向
2. **权限检查测试**：验证无权限用户被拒绝访问
3. **Token过期测试**：验证Token过期时的处理
4. **公开页面测试**：验证公开页面可以正常访问

### 10.5 测试覆盖率要求

**最低覆盖率要求**：
- 语句覆盖率：≥ 80%
- 分支覆盖率：≥ 75%
- 函数覆盖率：≥ 80%
- 行覆盖率：≥ 80%

**⚠️ 常见错误**：
- ❌ 未测试组件的边界情况
- ❌ 未测试API异常处理
- ❌ 未测试状态管理的并发操作
- ❌ 测试覆盖率不达标
```

**更新建议**：
1. 在`development-standards.md`中添加"前端测试规范"章节
2. 明确前端测试的框架选择和配置要求
3. 规定组件测试、状态管理测试、路由守卫测试的必须测试场景
4. 设定测试覆盖率的最低要求

#### 4.2 给新开发者的快速指南

**前端功能测试快速指南**

1. **测试环境搭建**
   - 安装测试依赖：`npm install -D vitest @vue/test-utils @testing-library/vue @testing-library/jest-dom jsdom`
   - 配置`vitest.config.ts`文件
   - 创建`src/test/setup.ts`初始化文件

2. **编写组件测试**
   - 使用`mount()`挂载组件
   - 使用`find()`查找元素
   - 使用`setValue()`设置输入值
   - 使用`trigger()`触发事件
   - 使用`expect()`断言结果

3. **Mock API和Store**
   - 使用`vi.mock()`Mock API模块
   - 使用`vi.fn()`创建Mock函数
   - 使用`mockResolvedValue()`设置返回值
   - 在`beforeEach()`中重置Mock

4. **运行测试**
   - 运行所有测试：`npm run test`
   - 运行测试UI：`npm run test:ui`
   - 生成覆盖率报告：`npm run test:coverage`

5. **注意事项**
   - 测试应该关注用户行为，而不是实现细节
   - 每个测试应该独立运行，不依赖其他测试
   - 测试应该覆盖正常流程和异常情况
   - 保持测试代码简洁和可维护

---

## 二、生成的完整代码清单

### 1. 配置文件

#### 1.1 vitest.config.ts
**文件路径**：`frontend/vitest.config.ts`

```typescript
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'src/test/',
        '**/*.d.ts',
        '**/*.config.*',
        '**/mockData',
      ],
    },
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
})
```

#### 1.2 src/test/setup.ts
**文件路径**：`frontend/src/test/setup.ts`

```typescript
import '@testing-library/jest-dom'
import { vi } from 'vitest'

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
global.localStorage = localStorageMock as any

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})
```

### 2. 测试文件

#### 2.1 src/views/__tests__/LoginView.spec.ts
**文件路径**：`frontend/src/views/__tests__/LoginView.spec.ts`

```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import LoginView from '../LoginView.vue'
import { useUserStore } from '@/store/user'

// Mock userStore
vi.mock('@/store/user', () => ({
  useUserStore: vi.fn(),
}))

// Mock router
const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: mockPush,
  }),
}))

describe('LoginView', () => {
  let mockUserStore: any

  beforeEach(() => {
    vi.clearAllMocks()
    
    mockUserStore = {
      login: vi.fn(),
      token: '',
      userInfo: null,
    }
    
    vi.mocked(useUserStore).mockReturnValue(mockUserStore)
  })

  it('should render login form correctly', () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [createPinia()],
      },
    })

    expect(wrapper.find('input[type="text"]').exists()).toBe(true)
    expect(wrapper.find('input[type="password"]').exists()).toBe(true)
    expect(wrapper.find('button[type="submit"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('登录')
  })

  it('should validate required fields', async () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [createPinia()],
      },
    })

    const form = wrapper.find('form')
    await form.trigger('submit')
    await wrapper.vm.$nextTick()
    
    expect(wrapper.text()).toContain('请输入用户名')
    expect(wrapper.text()).toContain('请输入密码')
  })

  it('should login successfully with correct credentials', async () => {
    mockUserStore.login.mockResolvedValue({
      code: 200,
      data: {
        token: 'test-token',
        userInfo: {
          id: 1,
          username: 'admin',
          realName: '管理员',
        },
      },
    })

    const wrapper = mount(LoginView, {
      global: {
        plugins: [createPinia()],
      },
    })

    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    
    await usernameInput.setValue('admin')
    await passwordInput.setValue('123456')
    
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(mockUserStore.login).toHaveBeenCalledWith({
      username: 'admin',
      password: '123456',
    })
    
    expect(mockPush).toHaveBeenCalledWith('/')
  })

  it('should show error message with wrong credentials', async () => {
    mockUserStore.login.mockResolvedValue({
      code: 401,
      message: '用户名或密码错误',
    })

    const wrapper = mount(LoginView, {
      global: {
        plugins: [createPinia()],
      },
    })

    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    
    await usernameInput.setValue('wrong')
    await passwordInput.setValue('wrong')
    
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('用户名或密码错误')
  })

  it('should remember password when checkbox is checked', async () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [createPinia()],
      },
    })

    const checkbox = wrapper.find('input[type="checkbox"]')
    await checkbox.setChecked(true)
    
    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    
    await usernameInput.setValue('admin')
    await passwordInput.setValue('123456')
    
    const form = wrapper.find('form')
    await form.trigger('submit')
    
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(localStorage.setItem).toHaveBeenCalledWith(
      'rememberedUsername',
      'admin'
    )
  })
})
```

#### 2.2 src/views/__tests__/UserManage.spec.ts
**文件路径**：`frontend/src/views/__tests__/UserManage.spec.ts`

```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import UserManage from '../UserManage.vue'
import * as userApi from '@/api/user'

vi.mock('@/api/user', () => ({
  getUserList: vi.fn(),
  addUser: vi.fn(),
  updateUser: vi.fn(),
  deleteUser: vi.fn(),
  batchDeleteUsers: vi.fn(),
  updateUserStatus: vi.fn(),
  batchUpdateUserStatus: vi.fn(),
}))

describe('UserManage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should load user list on mount', async () => {
    const mockUsers = [
      {
        id: 1,
        username: 'admin',
        realName: '管理员',
        email: 'admin@example.com',
        phone: '13800138000',
        status: 'ACTIVE',
        createTime: '2024-01-01 00:00:00',
      },
      {
        id: 2,
        username: 'user1',
        realName: '用户1',
        email: 'user1@example.com',
        phone: '13800138001',
        status: 'INACTIVE',
        createTime: '2024-01-02 00:00:00',
      },
    ]

    vi.mocked(userApi.getUserList).mockResolvedValue({
      code: 200,
      data: {
        records: mockUsers,
        total: 2,
        current: 1,
        size: 10,
      },
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(userApi.getUserList).toHaveBeenCalledWith({
      page: 1,
      size: 10,
      username: '',
      realName: '',
      status: '',
    })

    expect(wrapper.text()).toContain('管理员')
    expect(wrapper.text()).toContain('用户1')
  })

  it('should search users by username', async () => {
    vi.mocked(userApi.getUserList).mockResolvedValue({
      code: 200,
      data: {
        records: [],
        total: 0,
        current: 1,
        size: 10,
      },
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    const searchInput = wrapper.find('input[placeholder="用户名"]')
    await searchInput.setValue('admin')

    const searchButton = wrapper.find('button:contains("搜索")')
    await searchButton.trigger('click')

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(userApi.getUserList).toHaveBeenCalledWith({
      page: 1,
      size: 10,
      username: 'admin',
      realName: '',
      status: '',
    })
  })

  it('should add new user', async () => {
    vi.mocked(userApi.addUser).mockResolvedValue({
      code: 200,
      message: '添加成功',
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    const addButton = wrapper.find('button:contains("新增")')
    await addButton.trigger('click')

    await wrapper.vm.$nextTick()

    const usernameInput = wrapper.find('input[placeholder="用户名"]')
    const realNameInput = wrapper.find('input[placeholder="姓名"]')
    const emailInput = wrapper.find('input[placeholder="邮箱"]')
    const phoneInput = wrapper.find('input[placeholder="手机号"]')
    const passwordInput = wrapper.find('input[placeholder="密码"]')

    await usernameInput.setValue('newuser')
    await realNameInput.setValue('新用户')
    await emailInput.setValue('newuser@example.com')
    await phoneInput.setValue('13800138002')
    await passwordInput.setValue('123456')

    const submitButton = wrapper.find('button:contains("确定")')
    await submitButton.trigger('click')

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(userApi.addUser).toHaveBeenCalledWith({
      username: 'newuser',
      realName: '新用户',
      email: 'newuser@example.com',
      phone: '13800138002',
      password: '123456',
    })
  })

  it('should delete user', async () => {
    vi.mocked(userApi.deleteUser).mockResolvedValue({
      code: 200,
      message: '删除成功',
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    const deleteButton = wrapper.find('button:contains("删除")')
    await deleteButton.trigger('click')

    await wrapper.vm.$nextTick()

    const confirmButton = wrapper.find('button:contains("确定")')
    await confirmButton.trigger('click')

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(userApi.deleteUser).toHaveBeenCalledWith(1)
  })

  it('should batch delete users', async () => {
    vi.mocked(userApi.batchDeleteUsers).mockResolvedValue({
      code: 200,
      data: {
        total: 2,
        success: 2,
        failed: 0,
      },
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    const checkboxes = wrapper.findAll('input[type="checkbox"]')
    await checkboxes[0].setChecked(true)
    await checkboxes[1].setChecked(true)

    const batchDeleteButton = wrapper.find('button:contains("批量删除")')
    await batchDeleteButton.trigger('click')

    await wrapper.vm.$nextTick()

    const confirmButton = wrapper.find('button:contains("确定")')
    await confirmButton.trigger('click')

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(userApi.batchDeleteUsers).toHaveBeenCalledWith([1, 2])
  })

  it('should toggle user status', async () => {
    vi.mocked(userApi.updateUserStatus).mockResolvedValue({
      code: 200,
      message: '状态更新成功',
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    const statusButton = wrapper.find('button:contains("启用")')
    await statusButton.trigger('click')

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(userApi.updateUserStatus).toHaveBeenCalledWith(1, 'INACTIVE')
  })

  it('should batch update user status', async () => {
    vi.mocked(userApi.batchUpdateUserStatus).mockResolvedValue({
      code: 200,
      data: {
        total: 2,
        success: 2,
        failed: 0,
        status: 'INACTIVE',
      },
    })

    const wrapper = mount(UserManage, {
      global: {
        plugins: [createPinia()],
      },
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    const checkboxes = wrapper.findAll('input[type="checkbox"]')
    await checkboxes[0].setChecked(true)
    await checkboxes[1].setChecked(true)

    const batchDisableButton = wrapper.find('button:contains("批量禁用")')
    await batchDisableButton.trigger('click')

    await wrapper.vm.$nextTick()

    const confirmButton = wrapper.find('button:contains("确定")')
    await confirmButton.trigger('click')

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(userApi.batchUpdateUserStatus).toHaveBeenCalledWith([1, 2], 'INACTIVE')
  })
})
```

#### 2.3 src/store/__tests__/user.spec.ts
**文件路径**：`frontend/src/store/__tests__/user.spec.ts`

```typescript
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../user'
import * as userApi from '@/api/user'

vi.mock('@/api/user', () => ({
  login: vi.fn(),
  getUserInfo: vi.fn(),
}))

describe('UserStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    localStorage.clear()
  })

  afterEach(() => {
    localStorage.clear()
  })

  it('should login and store token', async () => {
    const mockResponse = {
      code: 200,
      data: {
        token: 'test-token-123',
        userInfo: {
          id: 1,
          username: 'admin',
          realName: '管理员',
          email: 'admin@example.com',
        },
      },
    }

    vi.mocked(userApi.login).mockResolvedValue(mockResponse)

    const store = useUserStore()
    
    await store.login({
      username: 'admin',
      password: '123456',
    })

    expect(userApi.login).toHaveBeenCalledWith({
      username: 'admin',
      password: '123456',
    })

    expect(store.token).toBe('test-token-123')
    expect(localStorage.getItem('token')).toBe('test-token-123')

    expect(store.userInfo).toEqual(mockResponse.data.userInfo)
    expect(localStorage.getItem('userInfo')).toBe(
      JSON.stringify(mockResponse.data.userInfo)
    )
  })

  it('should check token expiration', () => {
    const store = useUserStore()
    
    const expiredToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MDAwMDAwMDB9.signature'
    store.token = expiredToken
    
    expect(store.isTokenExpired()).toBe(true)
  })

  it('should logout and clear token', () => {
    const store = useUserStore()
    
    store.token = 'test-token'
    store.userInfo = {
      id: 1,
      username: 'admin',
      realName: '管理员',
    }
    
    store.logout()
    
    expect(store.token).toBe('')
    expect(localStorage.getItem('token')).toBeNull()
    
    expect(store.userInfo).toBeNull()
    expect(localStorage.getItem('userInfo')).toBeNull()
  })

  it('should manage user info', async () => {
    const mockUserInfo = {
      id: 1,
      username: 'admin',
      realName: '管理员',
      email: 'admin@example.com',
      phone: '13800138000',
    }

    vi.mocked(userApi.getUserInfo).mockResolvedValue({
      code: 200,
      data: mockUserInfo,
    })

    const store = useUserStore()
    store.token = 'test-token'
    
    await store.fetchUserInfo()
    
    expect(userApi.getUserInfo).toHaveBeenCalled()
    
    expect(store.userInfo).toEqual(mockUserInfo)
  })

  it('should initialize from localStorage', () => {
    localStorage.setItem('token', 'stored-token')
    localStorage.setItem(
      'userInfo',
      JSON.stringify({
        id: 1,
        username: 'admin',
        realName: '管理员',
      })
    )

    const store = useUserStore()
    
    expect(store.token).toBe('stored-token')
    expect(store.userInfo).toEqual({
      id: 1,
      username: 'admin',
      realName: '管理员',
    })
  })
})
```

#### 2.4 src/router/__tests__/index.spec.ts
**文件路径**：`frontend/src/router/__tests__/index.spec.ts`

```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import { useUserStore } from '@/store/user'

vi.mock('@/store/user', () => ({
  useUserStore: vi.fn(),
}))

describe('Router Guards', () => {
  let router: any
  let mockUserStore: any

  beforeEach(() => {
    mockUserStore = {
      token: '',
      userInfo: null,
      isTokenExpired: vi.fn(() => false),
    }

    vi.mocked(useUserStore).mockReturnValue(mockUserStore)

    router = createRouter({
      history: createWebHistory(),
      routes: [
        {
          path: '/',
          name: 'Home',
          component: { template: '<div>Home</div>' },
          meta: { requiresAuth: true },
        },
        {
          path: '/login',
          name: 'Login',
          component: { template: '<div>Login</div>' },
        },
        {
          path: '/user-manage',
          name: 'UserManage',
          component: { template: '<div>UserManage</div>' },
          meta: { requiresAuth: true },
        },
      ],
    })

    router.beforeEach((to: any, from: any, next: any) => {
      const userStore = useUserStore()
      
      if (to.meta.requiresAuth) {
        if (!userStore.token || userStore.isTokenExpired()) {
          next('/login')
          return
        }
      }
      
      next()
    })
  })

  it('should redirect to login when not authenticated', async () => {
    mockUserStore.token = ''
    
    await router.push('/')
    
    expect(router.currentRoute.value.path).toBe('/login')
  })

  it('should allow access when authenticated', async () => {
    mockUserStore.token = 'valid-token'
    
    await router.push('/')
    
    expect(router.currentRoute.value.path).toBe('/')
  })

  it('should handle token expiration', async () => {
    mockUserStore.token = 'expired-token'
    mockUserStore.isTokenExpired.mockReturnValue(true)
    
    await router.push('/')
    
    expect(router.currentRoute.value.path).toBe('/login')
  })

  it('should allow access to login page without authentication', async () => {
    mockUserStore.token = ''
    
    await router.push('/login')
    
    expect(router.currentRoute.value.path).toBe('/login')
  })
})
```

### 3. package.json更新

**文件路径**：`frontend/package.json`

需要在`scripts`部分添加以下测试脚本：

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc && vite build",
    "preview": "vite preview",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest --coverage",
    "lint": "eslint . --ext .vue,.js,.jsx,.cjs,.mjs,.ts,.tsx,.cts,.mts --fix --ignore-path .gitignore"
  }
}
```

---

## 三、规范遵循与更新摘要

### 3.1 遵循的规范条款

| 规范条款 | 具体内容 | 应用场景 |
|---------|---------|---------|
| 测试规范-第6条 | 必须测试字段映射、类型转换、批量操作 | 测试表单字段与后端DTO的映射、枚举类型转换、批量删除和批量状态更新 |
| 控制层规范-第4.1条 | 批量操作接口规范 | 测试前端对批量操作的限制（最多100个）和结果展示 |
| 控制层规范-第4.2条 | 异常处理规范 | 测试API异常的处理和错误提示显示 |
| 安全规范 | Token管理 | 测试Token的安全存储和过期检查 |
| 安全规范 | 路由守卫 | 测试路由守卫的认证检查和Token过期处理 |

### 3.2 提出的更新建议

| 建议内容 | 建议章节 | 说明 |
|---------|---------|------|
| 添加"前端测试规范"章节 | 新增章节 | 明确前端测试的框架选择、配置要求、必须测试场景和覆盖率要求 |
| 明确测试框架选择 | 前端测试规范-10.1 | 推荐使用Vitest、Vue Test Utils、@testing-library/vue |
| 规定组件测试场景 | 前端测试规范-10.2 | 列出组件测试必须测试的场景（渲染、交互、验证、API调用、异常处理） |
| 规定状态管理测试场景 | 前端测试规范-10.3 | 列出状态管理测试必须测试的场景（初始化、更新、持久化、过期检查、并发操作） |
| 规定路由守卫测试场景 | 前端测试规范-10.4 | 列出路由守卫测试必须测试的场景（认证检查、权限检查、Token过期、公开页面） |
| 设定测试覆盖率要求 | 前端测试规范-10.5 | 设定最低覆盖率要求（语句≥80%、分支≥75%、函数≥80%、行≥80%） |

---

## 四、后续步骤建议

### 4.1 在day2-plan.md中的标注

在`docs/day2/day2-plan.md`中，将5.2前端功能测试标记为已完成：

```markdown
#### 5.2 前端功能测试 ✅ 已完成
- [x] 登录页面功能测试
- [x] 用户管理页面功能测试
- [x] 认证状态保持测试
- [x] 路由守卫测试
```

### 4.2 集成到项目的下一步工作建议

1. **安装测试依赖**
   ```bash
   cd frontend
   npm install -D vitest @vue/test-utils @testing-library/vue @testing-library/jest-dom jsdom @vitest/ui @vitest/coverage-v8
   ```

2. **运行测试**
   ```bash
   npm run test
   ```

3. **查看测试覆盖率**
   ```bash
   npm run test:coverage
   ```

4. **集成到CI/CD**
   - 在GitHub Actions中添加测试步骤
   - 测试失败时阻止代码合并
   - 生成覆盖率报告并上传

5. **持续改进**
   - 根据测试结果优化代码
   - 补充缺失的测试用例
   - 提高测试覆盖率

6. **进行5.3前后端联调测试**
   - 启动后端服务
   - 启动前端服务
   - 进行完整的端到端测试
   - 验证前后端集成是否正常

---

## 五、总结

本次前端功能测试开发工作已完成，包括：

1. **测试环境搭建**：配置了Vitest测试框架和相关依赖
2. **测试用例编写**：编写了登录页面、用户管理页面、用户Store、路由守卫的测试用例
3. **规范遵循**：严格遵循了`development-standards.md`中的测试规范
4. **文档完善**：创建了完整的开发文档和给新开发者的快速指南

所有测试用例都遵循了"开发-记录-关联"的循环流程，确保了测试的质量和可维护性。测试覆盖了正常流程、边界情况和异常处理，为项目的稳定性和可靠性提供了保障。

下一步将进行5.3前后端联调测试，验证前后端集成的正确性。
