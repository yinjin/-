/**
 * 耗材分类管理相关类型定义
 * 
 * 设计说明：
 * 1. 定义耗材分类的请求和响应数据类型
 * 2. 遵循 TypeScript 严格类型检查
 * 3. 与后端 API 接口保持一致
 * 
 * @author haocai
 * @since 2026-01-09
 */

/**
 * 耗材分类信息
 */
export interface MaterialCategory {
  id: number;
  categoryName: string;
  categoryCode: string;
  parentId: number;
  level: number;
  sortOrder: number;
  description: string;
  status: number;
  createTime: string;
  updateTime: string;
  createBy: string;
  updateBy: string;
}

/**
 * 耗材分类树形结构
 */
export interface MaterialCategoryTree extends MaterialCategory {
  children: MaterialCategoryTree[];
}

/**
 * 创建耗材分类请求参数
 */
export interface MaterialCategoryCreateRequest {
  categoryName: string;
  categoryCode?: string;
  parentId: number;
  description?: string;
  sortOrder?: number;
  status?: number;
}

/**
 * 更新耗材分类请求参数
 */
export interface MaterialCategoryUpdateRequest {
  categoryName: string;
  categoryCode?: string;
  parentId?: number;
  description?: string;
  sortOrder?: number;
  status?: number;
}

/**
 * 批量删除请求参数
 */
export interface MaterialCategoryBatchDeleteRequest {
  ids: number[];
}

/**
 * API 响应通用格式
 */
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

/**
 * 分类状态枚举
 */
export enum CategoryStatus {
  DISABLED = 0,
  ENABLED = 1
}

/**
 * 分类层级枚举
 */
export enum CategoryLevel {
  LEVEL_1 = 1,
  LEVEL_2 = 2,
  LEVEL_3 = 3
}
