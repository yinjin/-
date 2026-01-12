/**
 * 库存管理类型定义
 * 
 * 遵循规范：
 * - TypeScript 类型安全规范
 * 
 * @author haocai
 * @date 2026-01-13
 */

/**
 * 库存信息
 */
export interface Inventory {
  id: number
  materialId: number
  materialName: string
  materialCode: string
  specification: string
  unit: string
  brand: string
  unitPrice: number
  quantity: number
  availableQuantity: number
  safeQuantity: number
  maxQuantity: number
  warehouse: string
  location: string
  lastInTime: string
  lastOutTime: string
  totalInQuantity: number
  totalOutQuantity: number
  status: string
  statusDescription: string
  inventoryValue: number
  createTime: string
  updateTime: string
}

/**
 * 库存查询参数
 */
export interface InventoryQueryParams {
  materialId?: number
  materialName?: string
  materialCode?: string
  warehouse?: string
  status?: string
  current: number
  size: number
  orderBy?: string
  orderDirection?: string
}

/**
 * 库存更新请求
 */
export interface InventoryUpdateRequest {
  materialId: number
  warehouse?: string
  location?: string
  safeQuantity?: number
  maxQuantity?: number
  remark?: string
}

/**
 * 库存调整请求
 */
export interface InventoryAdjustRequest {
  materialId: number
  adjustQuantity: number
  adjustType?: string
  reason: string
}

/**
 * 库存分页响应
 */
export interface InventoryPageResponse {
  records: Inventory[]
  total: number
  size: number
  current: number
  pages: number
}

/**
 * 库存统计数据
 */
export interface InventoryStatistics {
  totalInventoryValue: number
  inventoryTurnoverRate: number
}
