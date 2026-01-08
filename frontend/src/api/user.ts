import request from './index'
import type { DepartmentVO } from '@/types/department'

// 用户信息类型
export interface UserInfo {
  id: number
  username: string
  name: string
  email: string
  phone: string
  status: number  // 0-正常，1-禁用，2-锁定
  createTime: string
  updateTime: string
  roles?: RoleInfo[]  // 用户角色列表
  department?: DepartmentVO  // 遵循：前端类型规范-与后端DTO/VO保持一致
}

// 角色信息类型
export interface RoleInfo {
  id: number
  name: string  // 后端返回的是name，不是roleName
  code: string  // 后端返回的是code，不是roleCode
  description?: string
  status: number
  createTime?: string
  updateTime?: string
}

// 登录请求类型
export interface LoginRequest {
  username: string
  password: string
  rememberMe?: boolean
}

// 登录响应类型
export interface LoginResponse {
  token: string
  user: UserInfo
}

// 注册请求类型
export interface RegisterRequest {
  username: string
  password: string
  name: string
  email: string
  phone: string
  verificationCode: string
}

// 更新用户信息请求类型
export interface UpdateUserRequest {
  name?: string
  email?: string
  phone?: string
  status?: number
  departmentId?: number  // 遵循：前端类型规范-与后端DTO/VO保持一致
}

// 用户列表查询请求类型
export interface UserListRequest {
  page?: number
  size?: number
  username?: string
  name?: string
  status?: number
  departmentId?: number  // 遵循：前端类型规范-与后端DTO/VO保持一致
}

// 创建用户请求类型
export interface CreateUserRequest {
  username: string
  password: string
  confirmPassword: string
  name: string
  email: string
  phone: string
  agreeToTerms: boolean
  departmentId?: number  // 遵循：前端类型规范-与后端DTO/VO保持一致
}

// 分页响应类型
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// API响应类型
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 用户相关API
export const userApi = {
  // 登录
  login: (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    return request.post('/users/login', data)
  },

  // 获取当前用户信息
  getUserInfo: (): Promise<ApiResponse<UserInfo>> => {
    return request.get('/users/current')
  },

  // 注册
  register: (data: RegisterRequest): Promise<ApiResponse<UserInfo>> => {
    return request.post('/users/register', data)
  },

  // 更新用户信息
  updateUserInfo: (data: UpdateUserRequest): Promise<ApiResponse<UserInfo>> => {
    return request.put('/users/current', data)
  },

  // 退出登录
  logout: (): Promise<ApiResponse<void>> => {
    return request.post('/users/logout')
  },

  // 获取用户列表（分页）
  getUserList: (params: UserListRequest): Promise<ApiResponse<PageResponse<UserInfo>>> => {
    return request.get('/users', { params })
  },

  // 根据ID获取用户信息
  getUserById: (id: number): Promise<ApiResponse<UserInfo>> => {
    return request.get(`/users/${id}`)
  },

  // 创建用户
  createUser: (data: CreateUserRequest): Promise<ApiResponse<UserInfo>> => {
    return request.post('/users/register', data)
  },

  // 更新用户信息
  updateUser: (id: number, data: UpdateUserRequest): Promise<ApiResponse<UserInfo>> => {
    return request.put(`/users/${id}`, data)
  },

  // 更新用户状态
  updateUserStatus: (id: number, status: number): Promise<ApiResponse<void>> => {
    return request.patch(`/users/${id}/status`, null, { params: { status } })
  },

  // 批量更新用户状态
  batchUpdateStatus: (ids: number[], status: number): Promise<ApiResponse<void>> => {
    return request.patch('/users/batch/status', ids, { params: { status } })
  },

  // 删除用户
  deleteUser: (id: number): Promise<ApiResponse<void>> => {
    return request.delete(`/users/${id}`)
  },

  // 批量删除用户
  batchDeleteUsers: (ids: number[]): Promise<ApiResponse<void>> => {
    return request.delete('/users/batch', { data: ids })
  },

  // 获取用户的角色列表
  getUserRoles: (userId: number): Promise<ApiResponse<RoleInfo[]>> => {
    return request.get(`/users/${userId}/roles`)
  },

  // 为用户分配角色
  assignRoles: (userId: number, roleIds: number[]): Promise<ApiResponse<void>> => {
    return request.post(`/users/${userId}/roles`, roleIds)
  },

  // 移除用户的角色
  removeUserRole: (userId: number, roleId: number): Promise<ApiResponse<void>> => {
    return request.delete(`/users/${userId}/roles/${roleId}`)
  }
}

// 导出各个API方法
export const login = userApi.login
export const getUserInfo = userApi.getUserInfo
export const register = userApi.register
export const updateUserInfo = userApi.updateUserInfo
export const logout = userApi.logout
export const getUserList = userApi.getUserList
export const getUserById = userApi.getUserById
export const createUser = userApi.createUser
export const updateUser = userApi.updateUser
export const updateUserStatus = userApi.updateUserStatus
export const batchUpdateStatus = userApi.batchUpdateStatus
export const deleteUser = userApi.deleteUser
export const batchDeleteUsers = userApi.batchDeleteUsers
export const getUserRoles = userApi.getUserRoles
export const assignRoles = userApi.assignRoles
export const removeUserRole = userApi.removeUserRole
