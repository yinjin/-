/**
 * 耗材分类管理 API 接口
 * 
 * 设计说明：
 * 1. 封装所有耗材分类相关的 API 调用
 * 2. 使用 axios 进行 HTTP 请求
 * 3. 统一错误处理和响应拦截
 * 4. 遵循 RESTful API 设计规范
 * 
 * @author haocai
 * @since 2026-01-09
 */

import request from './index';
import type {
  MaterialCategory,
  MaterialCategoryTree,
  MaterialCategoryCreateRequest,
  MaterialCategoryUpdateRequest,
  MaterialCategoryBatchDeleteRequest,
  ApiResponse
} from '@/types/material-category';

/**
 * 创建耗材分类
 * @param data 创建分类请求参数
 * @returns 分类信息
 */
export function createMaterialCategory(data: MaterialCategoryCreateRequest): Promise<ApiResponse<MaterialCategory>> {
  return request({
    url: '/material-categories',
    method: 'post',
    data
  });
}

/**
 * 更新耗材分类
 * @param id 分类ID
 * @param data 更新分类请求参数
 * @returns 分类信息
 */
export function updateMaterialCategory(id: number, data: MaterialCategoryUpdateRequest): Promise<ApiResponse<MaterialCategory>> {
  return request({
    url: `/material-categories/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除耗材分类
 * @param id 分类ID
 * @returns 删除结果
 */
export function deleteMaterialCategory(id: number): Promise<ApiResponse<boolean>> {
  return request({
    url: `/material-categories/${id}`,
    method: 'delete'
  });
}

/**
 * 获取耗材分类详情
 * @param id 分类ID
 * @returns 分类信息
 */
export function getMaterialCategoryDetail(id: number): Promise<ApiResponse<MaterialCategory>> {
  return request({
    url: `/material-categories/${id}`,
    method: 'get'
  });
}

/**
 * 批量删除耗材分类
 * @param data 批量删除请求参数
 * @returns 删除结果
 */
export function batchDeleteMaterialCategory(data: MaterialCategoryBatchDeleteRequest): Promise<ApiResponse<boolean>> {
  return request({
    url: '/material-categories/batch',
    method: 'delete',
    data
  });
}

/**
 * 获取耗材分类树形结构
 * @param status 状态过滤（可选）
 * @returns 分类树形结构
 */
export function getMaterialCategoryTree(status?: number): Promise<ApiResponse<MaterialCategoryTree[]>> {
  return request({
    url: '/material-categories/tree',
    method: 'get',
    params: status !== undefined ? { status } : undefined
  });
}

/**
 * 根据父分类ID查询子分类列表
 * @param parentId 父分类ID
 * @param status 状态过滤（可选）
 * @returns 子分类列表
 */
export function getMaterialCategoryChildren(parentId: number, status?: number): Promise<ApiResponse<MaterialCategory[]>> {
  return request({
    url: `/material-categories/children/${parentId}`,
    method: 'get',
    params: status !== undefined ? { status } : undefined
  });
}

/**
 * 查询所有顶级分类
 * @param status 状态过滤（可选）
 * @returns 顶级分类列表
 */
export function getTopLevelMaterialCategories(status?: number): Promise<ApiResponse<MaterialCategory[]>> {
  return request({
    url: '/material-categories/top-level',
    method: 'get',
    params: status !== undefined ? { status } : undefined
  });
}

/**
 * 切换分类状态
 * @param id 分类ID
 * @returns 切换结果
 */
export function toggleMaterialCategoryStatus(id: number): Promise<ApiResponse<boolean>> {
  return request({
    url: `/material-categories/${id}/toggle-status`,
    method: 'put'
  });
}

/**
 * 检查分类编码是否存在
 * @param categoryCode 分类编码
 * @param excludeId 排除的分类ID（用于更新时检查）
 * @returns 是否存在
 */
export function checkMaterialCategoryCode(categoryCode: string, excludeId?: number): Promise<ApiResponse<boolean>> {
  return request({
    url: '/material-categories/check/code',
    method: 'get',
    params: {
      categoryCode,
      excludeId
    }
  });
}

/**
 * 检查分类下是否有子分类
 * @param id 分类ID
 * @returns 是否有子分类
 */
export function hasMaterialCategoryChildren(id: number): Promise<ApiResponse<boolean>> {
  return request({
    url: `/material-categories/${id}/has-children`,
    method: 'get'
  });
}
