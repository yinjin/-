/**
 * 部门管理API调用
 * 
 * 遵循规范：
 * - 前端交互规范-第1条（加载状态管理）
 * - 前端交互规范-第2条（用户反馈）
 * - 前端错误处理规范-第1条（错误提示）
 * 
 * @author haocai
 * @date 2026-01-08
 */

import request from './index'
import type {
  DepartmentCreateDTO,
  DepartmentUpdateDTO,
  DepartmentQueryParams,
  DepartmentVO,
  DepartmentTreeVO,
  BatchDeleteResult,
  CanDeleteResult
} from '@/types/department'

/**
 * API响应类型扩展
 */
type ApiResponse<T> = {
  code: number
  message: string
  data: T
}

/**
 * 部门管理API
 */
export const departmentApi = {
  // ==================== 基础CRUD操作 ====================

  /**
   * 创建部门
   * @param data 部门创建请求
   * @returns 创建后的部门信息
   */
  createDepartment(data: DepartmentCreateDTO): Promise<ApiResponse<DepartmentVO>> {
    // 遵循：前端交互规范-第1条（加载状态管理由调用方处理）
    return request.post('/department', data)
  },

  /**
   * 更新部门
   * @param data 部门更新请求
   * @returns 更新后的部门信息
   */
  updateDepartment(data: DepartmentUpdateDTO): Promise<ApiResponse<DepartmentVO>> {
    return request.put('/department', data)
  },

  /**
   * 删除部门
   * @param id 部门ID
   * @returns 删除结果
   */
  deleteDepartment(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/department/${id}`)
  },

  /**
   * 获取部门详情
   * @param id 部门ID
   * @returns 部门详情
   */
  getDepartment(id: number): Promise<ApiResponse<DepartmentVO>> {
    return request.get(`/department/${id}`)
  },

  /**
   * 分页查询部门列表
   * @param params 查询参数
   * @returns 分页结果
   */
  listDepartments(params: DepartmentQueryParams): Promise<ApiResponse<{ records: DepartmentVO[]; total: number; size: number; current: number; pages: number }>> {
    const { page, size, keyword, status, parentId, orderBy, orderDir } = params
    return request.get('/department/list', {
      params: {
        page,
        size,
        keyword: keyword || undefined,
        status: status || undefined,
        parentId: parentId || undefined,
        orderBy: orderBy || undefined,
        orderDir: orderDir || undefined
      }
    })
  },

  // ==================== 批量操作 ====================

  /**
   * 批量删除部门
   * @param ids 部门ID列表
   * @returns 批量操作结果
   */
  batchDeleteDepartments(ids: number[]): Promise<ApiResponse<BatchDeleteResult>> {
    return request.delete('/department/batch', { data: ids })
  },

  /**
   * 批量更新部门状态
   * @param ids 部门ID列表
   * @param status 目标状态
   * @returns 批量操作结果
   */
  batchUpdateStatus(ids: number[], status: string): Promise<ApiResponse<BatchDeleteResult>> {
    return request.put('/department/batch/status', { ids, status })
  },

  // ==================== 树形结构操作 ====================

  /**
   * 获取部门树形结构
   * @param includeDisabled 是否包含禁用部门，默认false
   * @returns 部门树形结构列表
   */
  getDepartmentTree(includeDisabled: boolean = false): Promise<ApiResponse<DepartmentTreeVO[]>> {
    return request.get('/department/tree', {
      params: { includeDisabled }
    })
  },

  /**
   * 获取指定部门的树形结构（包含所有子部门）
   * @param id 部门ID
   * @returns 部门树形结构
   */
  getDepartmentTreeById(id: number): Promise<ApiResponse<DepartmentTreeVO>> {
    return request.get(`/department/${id}/tree`)
  },

  /**
   * 懒加载获取子部门
   * @param parentId 父部门ID
   * @returns 子部门列表
   */
  getChildrenByParentId(parentId: number): Promise<ApiResponse<DepartmentTreeVO[]>> {
    return request.get(`/department/${parentId}/children`)
  },

  // ==================== 部门移动操作 ====================

  /**
   * 移动部门（调整部门层级）
   * @param id 要移动的部门ID
   * @param newParentId 新的父部门ID（传null或undefined表示移动为顶级部门）
   * @returns 移动后的部门信息
   */
  moveDepartment(id: number, newParentId: number | null | undefined): Promise<ApiResponse<DepartmentVO>> {
    return request.put(`/department/${id}/move`, null, {
      params: { newParentId: newParentId || undefined }
    })
  },

  // ==================== 部门负责人管理 ====================

  /**
   * 设置部门负责人
   * @param id 部门ID
   * @param leaderId 负责人ID（传null表示移除负责人）
   * @returns 设置结果
   */
  setDepartmentLeader(id: number, leaderId: number | null): Promise<ApiResponse<void>> {
    return request.put(`/department/${id}/leader`, null, {
      params: { leaderId: leaderId || undefined }
    })
  },

  /**
   * 移除部门负责人
   * @param id 部门ID
   * @returns 移除结果
   */
  removeDepartmentLeader(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/department/${id}/leader`)
  },

  // ==================== 部门关联查询 ====================

  /**
   * 查询部门下的所有用户ID
   * @param id 部门ID
   * @returns 用户ID列表
   */
  getUserIdsByDepartmentId(id: number): Promise<ApiResponse<number[]>> {
    return request.get(`/department/${id}/users`)
  },

  /**
   * 查询用户所属部门
   * @param userId 用户ID
   * @returns 部门列表
   */
  getDepartmentsByUserId(userId: number): Promise<ApiResponse<DepartmentVO[]>> {
    return request.get(`/department/user/${userId}`)
  },

  // ==================== 验证操作 ====================

  /**
   * 检查部门编码是否存在
   * @param code 部门编码
   * @param excludeId 排除的部门ID（更新时使用）
   * @returns 是否存在
   */
  checkDepartmentCode(code: string, excludeId?: number): Promise<ApiResponse<boolean>> {
    return request.get('/department/check/code', {
      params: { code, excludeId }
    })
  },

  /**
   * 检查部门名称在父部门下是否重复
   * @param name 部门名称
   * @param parentId 父部门ID
   * @param excludeId 排除的部门ID
   * @returns 是否重复
   */
  checkDepartmentName(name: string, parentId?: number, excludeId?: number): Promise<ApiResponse<boolean>> {
    return request.get('/department/check/name', {
      params: { name, parentId, excludeId }
    })
  },

  /**
   * 检查部门是否可以删除
   * @param id 部门ID
   * @returns 检查结果
   */
  checkCanDelete(id: number): Promise<ApiResponse<CanDeleteResult>> {
    return request.get(`/department/${id}/can-delete`)
  }
}

export default departmentApi
