import request, { type ApiResponse } from './index'
import type {
  SupplierInfo,
  SupplierCreateRequest,
  SupplierUpdateRequest,
  SupplierQueryRequest,
  PageResponse
} from '../types/supplier'

// 供应商评价类型（与后端SupplierEvaluationVO对应）
export interface SupplierEvaluation {
  id: number
  supplierId: number
  supplierName?: string
  evaluatorId: number
  evaluatorName: string
  evaluationDate: string
  deliveryScore: number
  qualityScore: number
  serviceScore: number
  priceScore: number
  totalScore: number
  averageScore: number
  creditRating: number
  creditRatingDescription?: string
  remark?: string
  createTime: string
  updateTime?: string
}

// 供应商评价创建请求类型（与后端SupplierEvaluationCreateDTO对应）
export interface SupplierEvaluationCreateRequest {
  supplierId: number
  deliveryScore: number
  qualityScore: number
  serviceScore: number
  priceScore: number
  remark?: string
}

// 供应商相关API
export const supplierApi = {
  // 创建供应商
  createSupplier: (data: SupplierCreateRequest): Promise<ApiResponse<number>> => {
    return request.post('/supplier', data)
  },

  // 更新供应商
  updateSupplier: (id: number, data: SupplierUpdateRequest): Promise<ApiResponse<boolean>> => {
    return request.put(`/supplier/${id}`, data)
  },

  // 删除供应商
  deleteSupplier: (id: number): Promise<ApiResponse<boolean>> => {
    return request.delete(`/supplier/${id}`)
  },

  // 批量删除供应商
  batchDeleteSuppliers: (ids: number[]): Promise<ApiResponse<boolean>> => {
    return request.delete('/supplier/batch', { data: ids })
  },

  // 获取供应商详情
  getSupplierById: (id: number): Promise<ApiResponse<SupplierInfo>> => {
    return request.get(`/supplier/${id}`)
  },

  // 分页查询供应商
  getSupplierPage: (params: SupplierQueryRequest): Promise<ApiResponse<PageResponse<SupplierInfo>>> => {
    return request.get('/supplier/page', { params })
  },

  // 获取供应商列表（不分页）
  getSupplierList: (params: SupplierQueryRequest): Promise<ApiResponse<SupplierInfo[]>> => {
    return request.get('/supplier/list', { params })
  },

  // 切换供应商状态
  toggleStatus: (id: number): Promise<ApiResponse<boolean>> => {
    return request.put(`/supplier/${id}/status`)
  },

  // 更新供应商状态
  updateStatus: (id: number, status: number): Promise<ApiResponse<boolean>> => {
    return request.put(`/supplier/${id}/status/${status}`)
  },

  // 批量更新供应商状态
  batchUpdateStatus: (ids: number[], status: number): Promise<ApiResponse<number>> => {
    return request.put(`/supplier/batch/status/${status}`, ids)
  },

  // 生成供应商编码
  generateSupplierCode: (): Promise<ApiResponse<string>> => {
    return request.get('/supplier/generate-code')
  },

  // 检查供应商编码是否存在
  checkSupplierCode: (supplierCode: string): Promise<ApiResponse<boolean>> => {
    return request.get('/supplier/check-code', { params: { supplierCode } })
  },

  // 搜索供应商
  searchSuppliers: (keyword: string): Promise<ApiResponse<SupplierInfo[]>> => {
    return request.get('/supplier/search', { params: { keyword } })
  },

  // 根据合作状态获取供应商
  getSuppliersByCooperationStatus: (cooperationStatus: number): Promise<ApiResponse<SupplierInfo[]>> => {
    return request.get('/supplier/by-cooperation-status', { params: { cooperationStatus } })
  },

  // 根据信用等级范围获取供应商
  getSuppliersByCreditRatingRange: (minRating: number, maxRating: number): Promise<ApiResponse<SupplierInfo[]>> => {
    return request.get('/supplier/by-credit-rating', { params: { minRating, maxRating } })
  },

  // 以下是评价相关API
  
  // 创建供应商评价
  createEvaluation: (id: number, data: SupplierEvaluationCreateRequest): Promise<ApiResponse<number>> => {
    return request.post(`/supplier/${id}/evaluate`, data)
  },

  // 获取供应商评价历史
  getEvaluations: (id: number): Promise<ApiResponse<SupplierEvaluation[]>> => {
    return request.get(`/supplier/${id}/evaluations`)
  },

  // 获取我的评价
  getMyEvaluations: (id: number): Promise<ApiResponse<SupplierEvaluation[]>> => {
    return request.get(`/supplier/${id}/my-evaluations`)
  },

  // 删除评价
  deleteEvaluation: (id: number): Promise<ApiResponse<boolean>> => {
    return request.delete(`/supplier/evaluation/${id}`)
  }
}

// 导出各个API方法
export const createSupplier = supplierApi.createSupplier
export const updateSupplier = supplierApi.updateSupplier
export const deleteSupplier = supplierApi.deleteSupplier
export const batchDeleteSuppliers = supplierApi.batchDeleteSuppliers
export const getSupplierById = supplierApi.getSupplierById
export const getSupplierPage = supplierApi.getSupplierPage
export const getSupplierList = supplierApi.getSupplierList
export const toggleStatus = supplierApi.toggleStatus
export const updateStatus = supplierApi.updateStatus
export const batchUpdateStatus = supplierApi.batchUpdateStatus
export const generateSupplierCode = supplierApi.generateSupplierCode
export const checkSupplierCode = supplierApi.checkSupplierCode
export const searchSuppliers = supplierApi.searchSuppliers
export const getSuppliersByCooperationStatus = supplierApi.getSuppliersByCooperationStatus
export const getSuppliersByCreditRatingRange = supplierApi.getSuppliersByCreditRatingRange

// 导出评价相关API
export const createEvaluation = supplierApi.createEvaluation
export const getEvaluations = supplierApi.getEvaluations
export const getMyEvaluations = supplierApi.getMyEvaluations
export const deleteEvaluation = supplierApi.deleteEvaluation
