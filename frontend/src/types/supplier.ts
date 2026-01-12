// 供应商信息类型（与后端SupplierVO对应）
export interface SupplierInfo {
  id: number
  supplierCode: string
  supplierName: string
  contactPerson?: string
  phone?: string
  email?: string
  address?: string
  businessLicense?: string
  taxNumber?: string
  bankAccount?: string
  bankName?: string
  creditRating: number
  creditRatingDescription?: string
  cooperationStatus: number
  cooperationStatusDescription?: string
  status: number
  statusDescription?: string
  description?: string
  createTime: string
  updateTime: string
  createBy?: string
  updateBy?: string
  businessLicenseUrl?: string
}

// 供应商创建请求类型（与后端SupplierCreateDTO对应）
export interface SupplierCreateRequest {
  supplierName: string
  supplierCode?: string
  contactPerson?: string
  phone?: string
  email?: string
  address?: string
  businessLicense?: string
  taxNumber?: string
  bankAccount?: string
  bankName?: string
  creditRating: number
  cooperationStatus: number
  status?: number
  description?: string
  businessLicenseUrl?: string
  qualificationFiles?: string[]
}

// 供应商更新请求类型（与后端SupplierUpdateDTO对应）
export interface SupplierUpdateRequest {
  id: number
  supplierName?: string
  contactPerson?: string
  phone?: string
  email?: string
  address?: string
  businessLicense?: string
  taxNumber?: string
  bankAccount?: string
  bankName?: string
  creditRating?: number
  cooperationStatus?: number
  status?: number
  description?: string
  businessLicenseUrl?: string
  qualificationFiles?: string[]
}

// 供应商查询请求类型
export interface SupplierQueryRequest {
  supplierName?: string
  supplierCode?: string
  contactPerson?: string
  phone?: string
  creditRatingMin?: number
  creditRatingMax?: number
  cooperationStatus?: number
  status?: number
  current?: number
  size?: number
  orderBy?: string
  orderDirection?: string
}

// 分页响应类型
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}
