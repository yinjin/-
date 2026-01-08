/**
 * 部门相关TypeScript类型定义
 * 
 * 遵循规范：
 * - 前端类型规范：与后端DTO/VO保持一致
 * - 使用TypeScript确保类型安全
 * 
 * @author haocai
 * @date 2026-01-08
 */

/**
 * 部门创建请求
 */
export interface DepartmentCreateDTO {
  /** 部门名称 */
  name: string
  /** 部门编码（唯一） */
  code: string
  /** 父部门ID（顶级部门传null） */
  parentId?: number | null
  /** 排序号（同级部门排序，数字越小越靠前） */
  sortOrder?: number
  /** 部门负责人ID */
  leaderId?: number
  /** 联系方式 */
  contactInfo?: string
  /** 部门描述 */
  description?: string
}

/**
 * 部门更新请求
 */
export interface DepartmentUpdateDTO {
  /** 部门ID */
  id: number
  /** 部门名称 */
  name?: string
  /** 部门编码（唯一） */
  code?: string
  /** 父部门ID（顶级部门传null） */
  parentId?: number | null
  /** 排序号 */
  sortOrder?: number
  /** 状态：NORMAL-正常，DISABLED-禁用 */
  status?: 'NORMAL' | 'DISABLED'
  /** 部门负责人ID */
  leaderId?: number
  /** 联系方式 */
  contactInfo?: string
  /** 部门描述 */
  description?: string
}

/**
 * 部门查询参数
 */
export interface DepartmentQueryParams {
  /** 页码 */
  page: number
  /** 每页大小 */
  size: number
  /** 关键词搜索（名称或编码） */
  keyword?: string
  /** 部门状态 */
  status?: 'NORMAL' | 'DISABLED'
  /** 父部门ID */
  parentId?: number
  /** 排序字段 */
  orderBy?: string
  /** 排序方向：asc/desc */
  orderDir?: 'asc' | 'desc'
}

/**
 * 部门信息响应VO
 */
export interface DepartmentVO {
  /** 部门ID */
  id: number
  /** 部门名称 */
  name: string
  /** 部门编码 */
  code: string
  /** 父部门ID */
  parentId: number | null
  /** 父部门名称 */
  parentName: string
  /** 部门层级（顶级为1级） */
  level: number
  /** 排序号 */
  sortOrder: number
  /** 状态：NORMAL-正常，DISABLED-禁用 */
  status: 'NORMAL' | 'DISABLED'
  /** 部门负责人ID */
  leaderId: number | null
  /** 部门负责人名称 */
  leaderName: string
  /** 联系方式 */
  contactInfo: string
  /** 部门描述 */
  description: string
  /** 子部门数量 */
  childrenCount: number
  /** 部门下用户数量 */
  usersCount: number
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
}

/**
 * 部门树形结构响应VO
 */
export interface DepartmentTreeVO extends DepartmentVO {
  /** 子部门列表 */
  children: DepartmentTreeVO[]
  /** 是否展开（前端使用） */
  expanded: boolean
  /** 是否叶子节点 */
  leaf: boolean
  /** 懒加载模式下子节点是否已加载 */
  loaded: boolean
}

/**
 * 批量删除结果
 */
export interface BatchDeleteResult {
  /** 总数 */
  total: number
  /** 成功数 */
  success: number
  /** 失败数 */
  failed: number
  /** 失败详情 */
  failedDetails: string[]
}

/**
 * 删除检查结果
 */
export interface CanDeleteResult {
  /** 是否可删除 */
  canDelete: boolean
  /** 不可删除原因 */
  reason: string
}

/**
 * 部门树节点（用于前端Tree组件）
 */
export interface DepartmentTreeNode {
  /** 节点ID */
  id: number
  /** 节点标签（显示名称） */
  label: string
  /** 父节点ID */
  parentId: number | null
  /** 节点数据 */
  data: DepartmentTreeVO
  /** 子节点 */
  children: DepartmentTreeNode[]
  /** 是否禁用 */
  disabled: boolean
  /** 是否展开 */
  expanded: boolean
  /** 是否叶子节点 */
  isLeaf: boolean
}

/**
 * 部门选择器配置
 */
export interface DepartmentSelectConfig {
  /** 是否多选 */
  multiple?: boolean
  /** 是否显示根节点 */
  showRoot?: boolean
  /** 根节点标签 */
  rootLabel?: string
  /** 根节点值 */
  rootValue?: number | null
  /** 是否禁用部门 */
  disabled?: boolean
  /** 占位符 */
  placeholder?: string
  /** 是否可清空 */
  clearable?: boolean
  /** 是否懒加载 */
  lazy?: boolean
  /** 加载子节点的方法 */
  load?: (node: DepartmentTreeNode, resolve: (data: DepartmentTreeNode[]) => void) => void
}
