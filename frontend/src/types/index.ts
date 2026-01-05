// 用户相关类型
export interface User {
  id: number
  username: string
  name: string
  role: string
  department: string
  email?: string
  phone?: string
  status: 'active' | 'inactive'
  createTime: string
  updateTime: string
}

// 耗材相关类型
export interface Material {
  id: number
  name: string
  code: string
  category: string
  specification: string
  unit: string
  price: number
  stock: number
  minStock: number
  status: 'normal' | 'disabled' | 'scrapped'
  description?: string
  createTime: string
  updateTime: string
}

// 入库单相关类型
export interface InboundOrder {
  id: number
  orderNo: string
  supplier: string
  operator: string
  status: 'draft' | 'pending' | 'approved' | 'rejected' | 'completed'
  totalAmount: number
  remark?: string
  createTime: string
  updateTime: string
  items: InboundItem[]
}

export interface InboundItem {
  id: number
  materialId: number
  materialName: string
  quantity: number
  price: number
  amount: number
  batchNo?: string
  productionDate?: string
  expiryDate?: string
}

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}