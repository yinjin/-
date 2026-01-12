/**
 * 库存管理API接口
 * 
 * 遵循规范：
 * - 前端交互规范-第1条（加载状态管理）
 * - 前端交互规范-第2条（用户反馈）
 * - 前端错误处理规范-第1条（错误提示）
 * 
 * @author haocai
 * @date 2026-01-13
 */

import request from './index'
import type {
  Inventory,
  InventoryQueryParams,
  InventoryUpdateRequest,
  InventoryAdjustRequest,
  InventoryPageResponse,
  InventoryStatistics
} from '../types/inventory'

/**
 * 库存管理API
 * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
 */
export const inventoryApi = {
  /**
   * 获取库存列表（分页）
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  getInventoryPage(params: InventoryQueryParams): Promise<ApiResponse<InventoryPageResponse>> {
    return request.get('/inventory/list', { params })
  },

  /**
   * 获取库存详情
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  getInventoryById(id: number): Promise<ApiResponse<Inventory>> {
    return request.get(`/inventory/${id}`)
  },

  /**
   * 获取库存预警列表
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  getInventoryWarning(): Promise<ApiResponse<Inventory[]>> {
    return request.get('/inventory/warning')
  },

  /**
   * 获取低库存列表
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  getLowStockList(): Promise<ApiResponse<Inventory[]>> {
    return request.get('/inventory/low-stock')
  },

  /**
   * 获取超储列表
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  getOverStockList(): Promise<ApiResponse<Inventory[]>> {
    return request.get('/inventory/over-stock')
  },

  /**
   * 获取临期列表
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  getExpiredList(): Promise<ApiResponse<Inventory[]>> {
    return request.get('/inventory/expired')
  },

  /**
   * 更新库存信息
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  updateInventory(id: number, data: InventoryUpdateRequest): Promise<ApiResponse<boolean>> {
    return request.put(`/inventory/${id}`, data)
  },

  /**
   * 调整库存数量
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  adjustInventory(data: InventoryAdjustRequest): Promise<ApiResponse<boolean>> {
    return request.post('/inventory/adjust', data)
  },

  /**
   * 获取库存统计数据
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  getInventoryStatistics(): Promise<ApiResponse<InventoryStatistics>> {
    return request.get('/inventory/statistics')
  },

  /**
   * 获取库存总价值
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  getInventoryValue(): Promise<ApiResponse<number>> {
    return request.get('/inventory/value')
  },

  /**
   * 获取库存周转率
   * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
   */
  getInventoryTurnover(): Promise<ApiResponse<number>> {
    return request.get('/inventory/turnover')
  }
}

export default inventoryApi
