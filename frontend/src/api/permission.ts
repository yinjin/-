import request from './index'

// 权限信息接口
export interface PermissionInfo {
  id?: number
  permissionName: string
  permissionCode: string
  type: string
  parentId?: number
  path?: string
  component?: string
  icon?: string
  sortOrder?: number
  status: number
  createTime?: string
  updateTime?: string
  createBy?: string
  updateBy?: string
  deleted?: number
  children?: PermissionInfo[]
}

// 权限列表请求参数
export interface PermissionListRequest {
  pageNum?: number
  pageSize?: number
  permissionName?: string
  permissionType?: string
}

// 创建权限请求
export interface CreatePermissionRequest {
  permissionName: string
  permissionCode: string
  type: string
  parentId?: number
  path?: string
  component?: string
  icon?: string
  sortOrder?: number
  status: number
}

// 更新权限请求
export interface UpdatePermissionRequest {
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

// 权限API对象
export const permissionApi = {
  // 获取权限树形结构
  getPermissionTree() {
    return request.get<ApiResponse<PermissionInfo[]>>('/permission/tree')
  },

  // 分页查询权限列表
  getPermissionList(params: PermissionListRequest) {
    return request.get<ApiResponse<PageResponse<PermissionInfo>>>('/permission/list', { params })
  },

  // 根据ID获取权限
  getPermissionById(id: number) {
    return request.get<ApiResponse<PermissionInfo>>(`/permission/${id}`)
  },

  // 创建权限
  createPermission(data: CreatePermissionRequest) {
    return request.post<ApiResponse<PermissionInfo>>('/permission', data)
  },

  // 更新权限
  updatePermission(data: UpdatePermissionRequest) {
    return request.put<ApiResponse<PermissionInfo>>(`/permission/${data.id}`, data)
  },

  // 删除权限
  deletePermission(id: number) {
    return request.delete<ApiResponse<void>>(`/permission/${id}`)
  }
}
