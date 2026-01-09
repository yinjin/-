/**
 * 耗材相关类型定义
 */

/**
 * 耗材实体
 */
export interface Material {
  id: number
  materialName: string
  materialCode: string
  categoryId: number | null
  categoryName: string
  specification: string | null
  unit: string
  brand: string | null
  manufacturer: string | null
  minStock: number
  maxStock: number
  safetyStock: number
  price: number | null
  description: string | null
  status: number
  createTime: string
  updateTime: string
  createBy: string
  updateBy: string
}

/**
 * 创建耗材请求
 */
export interface MaterialCreateRequest {
  materialName: string
  materialCode: string
  categoryId: number
  specification?: string
  unit: string
  brand?: string
  manufacturer?: string
  minStock?: number
  maxStock?: number
  safetyStock?: number
  price?: number
  description?: string
}

/**
 * 更新耗材请求
 */
export interface MaterialUpdateRequest {
  materialName: string
  materialCode?: string
  categoryId?: number
  specification?: string
  unit: string
  brand?: string
  manufacturer?: string
  minStock?: number
  maxStock?: number
  safetyStock?: number
  price?: number
  description?: string
  status?: number
}

/**
 * 耗材查询参数
 */
export interface MaterialQueryParams {
  current: number
  size: number
  materialName?: string
  materialCode?: string
  categoryId?: number
  brand?: string
  manufacturer?: string
  status?: number
  startTime?: string
  endTime?: string
}

/**
 * 耗材搜索参数
 */
export interface MaterialSearchParams {
  keyword?: string
  categoryId?: number
  status?: number
  current?: number
  size?: number
}

/**
 * 分页响应
 */
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/**
 * 耗材分页响应
 */
export type MaterialPageResponse = PageResponse<Material>

/**
 * 批量删除请求
 */
export interface MaterialBatchDeleteRequest {
  ids: number[]
}

/**
 * 检查编码请求
 */
export interface MaterialCheckCodeRequest {
  materialCode: string
  excludeId?: number
}
