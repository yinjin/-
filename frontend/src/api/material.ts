/**
 * 耗材管理API接口
 * 
 * 遵循规范：
 * - 前端交互规范-第1条（加载状态管理）
 * - 前端交互规范-第2条（用户反馈）
 * - 前端错误处理规范-第1条（错误提示）
 * 
 * @author haocai
 * @date 2026-01-09
 */

import request from './index'
import type {
  Material,
  MaterialCreateRequest,
  MaterialUpdateRequest,
  MaterialQueryParams,
  MaterialSearchParams,
  MaterialPageResponse,
  MaterialBatchDeleteRequest,
  MaterialCheckCodeRequest
} from '../types/material'

/**
 * API响应类型扩展
 */
type ApiResponse<T> = {
  code: number
  message: string
  data: T
}

/**
 * 耗材管理API
 */
export const materialApi = {
  /**
   * 创建耗材
   */
  createMaterial(data: MaterialCreateRequest): Promise<ApiResponse<Material>> {
    return request.post('/material', data)
  },

  /**
   * 更新耗材
   */
  updateMaterial(id: number, data: MaterialUpdateRequest): Promise<ApiResponse<Material>> {
    return request.put(`/material/${id}`, data)
  },

  /**
   * 删除耗材
   */
  deleteMaterial(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/material/${id}`)
  },

  /**
   * 获取耗材详情
   */
  getMaterialById(id: number): Promise<ApiResponse<Material>> {
    return request.get(`/material/${id}`)
  },

  /**
   * 分页查询耗材列表
   */
  getMaterialPage(params: MaterialQueryParams): Promise<ApiResponse<MaterialPageResponse>> {
    return request.get('/material/page', { params })
  },

  /**
   * 批量删除耗材
   */
  batchDeleteMaterials(data: MaterialBatchDeleteRequest): Promise<ApiResponse<void>> {
    return request.delete('/material/batch', { data })
  },

  /**
   * 切换耗材状态
   */
  toggleMaterialStatus(id: number): Promise<ApiResponse<void>> {
    return request.put(`/material/${id}/toggle-status`)
  },

  /**
   * 检查耗材编码是否存在
   */
  checkMaterialCode(materialCode: string, excludeId?: number): Promise<ApiResponse<boolean>> {
    return request.get('/material/check/code', { 
      params: { materialCode, excludeId } 
    })
  },

  /**
   * 根据分类ID查询耗材列表
   */
  getMaterialsByCategory(categoryId: number): Promise<ApiResponse<Material[]>> {
    return request.get(`/material/category/${categoryId}`)
  },

  /**
   * 搜索耗材
   */
  searchMaterials(keyword: string): Promise<ApiResponse<Material[]>> {
    return request.get('/material/search', { 
      params: { keyword } 
    })
  }
}

export default materialApi
