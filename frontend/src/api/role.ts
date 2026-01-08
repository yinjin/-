import request from './index'

// 角色信息接口
export interface RoleInfo {
  id?: number
  name: string  // 统一使用name而不是roleName
  code: string  // 统一使用code而不是roleCode
  description?: string
  status: number
  createTime?: string
  updateTime?: string
  createBy?: string
  updateBy?: string
  deleted?: number
  permissions?: PermissionInfo[]  // 角色拥有的权限列表
}

// 角色列表请求参数
export interface RoleListRequest {
  pageNum?: number
  pageSize?: number
  name?: string  // 改为name
  code?: string  // 改为code
  status?: number
}

// 创建角色请求
export interface CreateRoleRequest {
  name: string  // 改为name
  code: string  // 改为code
  description?: string
  status: number
}

// 更新角色请求
export interface UpdateRoleRequest {
  id: number
  name: string  // 改为name
  code: string  // 改为code
  description?: string
  status: number
}

// 分页响应
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// API响应
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 权限信息接口
export interface PermissionInfo {
  id: number
  permissionName: string
  permissionCode: string
  type: string
  parentId?: number
  path?: string
  component?: string
  icon?: string
  sortOrder?: number
  status: number
  children?: PermissionInfo[]
}

// 分配权限请求
export interface AssignPermissionRequest {
  roleId: number
  permissionIds: number[]
}

// 角色API对象
// 遵循：API规范-路径与后端Controller保持一致
// 后端路径：@RequestMapping("/api/role")
export const roleApi = {
  // 获取角色列表
  // 后端路径：GET /api/role/list
  // 遵循：API规范-响应拦截器返回ApiResponse<T>
  getRoleList(params: RoleListRequest) {
    return request.get<ApiResponse<PageResponse<RoleInfo>>>('/role/list', { params })
  },

  // 根据ID获取角色
  // 后端路径：GET /api/role/{id}
  getRoleById(id: number) {
    return request.get<ApiResponse<RoleInfo>>(`/role/${id}`)
  },

  // 创建角色
  // 后端路径：POST /api/role
  createRole(data: CreateRoleRequest) {
    return request.post<ApiResponse<RoleInfo>>('/role', data)
  },

  // 更新角色
  // 后端路径：PUT /api/role/{id}
  updateRole(data: UpdateRoleRequest) {
    return request.put<ApiResponse<RoleInfo>>(`/role/${data.id}`, data)
  },

  // 更新角色状态
  // 后端路径：PUT /api/role/{id}/status
  updateRoleStatus(id: number, status: number) {
    return request.put<ApiResponse<void>>(`/role/${id}/status`, { status })
  },

  // 批量更新角色状态
  // 后端路径：PUT /api/role/batch/status
  batchUpdateRoleStatus(ids: number[], status: number) {
    return request.put<ApiResponse<void>>('/role/batch/status', { ids, status })
  },

  // 删除角色
  // 后端路径：DELETE /api/role/{id}
  deleteRole(id: number) {
    return request.delete<ApiResponse<void>>(`/role/${id}`)
  },

  // 批量删除角色
  // 后端路径：DELETE /api/role/batch
  batchDeleteRoles(ids: number[]) {
    return request.delete<ApiResponse<void>>('/role/batch', { data: { ids } })
  },

  // 获取所有权限（树形结构）
  // 后端路径：GET /api/permission/tree
  getAllPermissions() {
    return request.get<ApiResponse<PermissionInfo[]>>('/permission/tree')
  },

  // 获取角色的权限
  // 后端路径：GET /api/role/{id}/permissions
  // 注意：后端返回的是权限对象列表，不是ID列表
  getRolePermissions(roleId: number) {
    return request.get<ApiResponse<PermissionInfo[]>>(`/role/${roleId}/permissions`)
  },

  // 分配权限
  // 后端路径：PUT /api/role/{id}/permissions
  assignPermissions(data: AssignPermissionRequest) {
    return request.put<ApiResponse<void>>(`/role/${data.roleId}/permissions`, data.permissionIds)
  }
}
