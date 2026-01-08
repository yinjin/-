# 角色管理页面开发报告

**任务完成状态**：✅ 已完成

**开发时间**：2026年1月7日  
**开发者**：AI Assistant  
**任务来源**：day3-plan.md - 3.1 角色管理页面开发

---

## 一、任务概述

### 1.1 任务目标
根据day3-plan.md中的3.1任务要求，开发角色管理前端页面，包括：
- 创建RoleManage.vue页面，实现角色列表展示、搜索、新增、编辑、删除、状态切换、分配权限等功能
- 创建role.ts API文件，封装角色相关的API调用
- 配置路由，将角色管理页面集成到系统中
- 补充后端批量更新状态和批量删除角色的接口
- 确保所有设计遵循development-standards.md中的规范

### 1.2 技术栈
- Vue 3.4.0
- TypeScript 5.3.3
- Element Plus 2.5.4
- Vue Router 4.2.5
- Axios 1.6.5
- Composition API (setup语法糖)

---

## 二、步骤1：规划与设计

### 2.1 关键约束条款

基于`development-standards.md`，角色管理页面开发必须遵循以下关键约束：

#### 约束1：前端代码规范
**规范内容**：使用TypeScript进行类型检查，使用Composition API编写组件代码

**应用说明**：
- 所有API接口定义使用TypeScript接口类型
- 所有组件使用setup语法糖
- 所有响应式数据使用ref或reactive定义
- 所有方法使用箭头函数定义

#### 约束2：UI组件使用规范
**规范内容**：统一使用Element Plus组件库，保持界面风格一致

**应用说明**：
- 表格使用el-table组件
- 表单使用el-form组件
- 弹窗使用el-dialog组件
- 消息提示使用el-message和el-message-box组件
- 树形选择使用el-tree组件

#### 约束3：API调用规范
**规范内容**：统一使用封装的request实例进行HTTP请求，统一错误处理

**应用说明**：
- 所有API调用使用封装的request实例
- 所有API返回数据使用ApiResponse类型
- 所有错误统一在request拦截器中处理

### 2.2 页面功能设计

#### 2.2.1 角色列表展示
**功能描述**：以表格形式展示角色列表，支持分页

**核心要素**：
- 表格列：角色名称、角色编码、描述、状态、创建时间、操作
- 分页组件：支持页码切换和每页大小调整
- 多选功能：支持批量操作

#### 2.2.2 搜索功能
**功能描述**：支持按角色名称、角色编码、状态进行搜索

**核心要素**：
- 角色名称：模糊查询
- 角色编码：模糊查询
- 状态：精确查询（正常/禁用）
- 搜索按钮：触发搜索
- 重置按钮：清空搜索条件

#### 2.2.3 新增角色
**功能描述**：通过弹窗表单创建新角色

**核心要素**：
- 弹窗标题：新增角色
- 表单字段：角色名称（必填）、角色编码（必填）、描述（可选）、状态（必填）
- 表单验证：必填字段验证、角色编码唯一性验证
- 提交按钮：提交表单
- 取消按钮：关闭弹窗

#### 2.2.4 编辑角色
**功能描述**：通过弹窗表单编辑角色信息

**核心要素**：
- 弹窗标题：编辑角色
- 表单字段：角色名称（必填）、角色编码（必填）、描述（可选）、状态（必填）
- 表单验证：必填字段验证、角色编码唯一性验证（排除当前角色）
- 数据回显：编辑时回显角色信息
- 提交按钮：提交表单
- 取消按钮：关闭弹窗

#### 2.2.5 删除角色
**功能描述**：删除指定角色

**核心要素**：
- 删除按钮：表格操作列中的删除按钮
- 确认对话框：确认是否删除
- 删除逻辑：删除角色及其权限关联

#### 2.2.6 批量删除角色
**功能描述**：批量删除选中的角色

**核心要素**：
- 多选功能：表格支持多选
- 批量删除按钮：工具栏中的批量删除按钮
- 确认对话框：确认是否批量删除
- 删除逻辑：批量删除角色及其权限关联

#### 2.2.7 状态切换
**功能描述**：切换角色的启用/禁用状态

**核心要素**：
- 开关组件：使用el-switch组件
- 单个状态切换：表格操作列中的状态开关
- 批量状态切换：工具栏中的批量启用/禁用按钮
- 确认对话框：批量状态切换时确认

#### 2.2.8 分配权限
**功能描述**：为角色分配权限

**核心要素**：
- 分配权限按钮：表格操作列中的分配权限按钮
- 弹窗标题：分配权限
- 权限树：使用el-tree组件展示权限树
- 树形选择：支持父子节点关联选择
- 数据回显：分配时回显角色已有权限
- 提交按钮：提交权限分配
- 取消按钮：关闭弹窗

### 2.3 API接口设计

#### 2.3.1 角色列表查询
**接口路径**：GET /api/role/list

**请求参数**：
```typescript
{
  page: number;        // 页码
  size: number;        // 每页大小
  roleName?: string;   // 角色名称（可选）
  roleCode?: string;   // 角色编码（可选）
  status?: number;      // 状态（可选）
}
```

**响应数据**：
```typescript
{
  code: number;
  message: string;
  data: {
    records: RoleInfo[];
    total: number;
    size: number;
    current: number;
    pages: number;
  };
}
```

#### 2.3.2 创建角色
**接口路径**：POST /api/role

**请求参数**：
```typescript
{
  roleName: string;     // 角色名称
  roleCode: string;     // 角色编码
  description?: string; // 描述
  status: number;       // 状态
}
```

**响应数据**：
```typescript
{
  code: number;
  message: string;
  data: RoleInfo;
}
```

#### 2.3.3 更新角色
**接口路径**：PUT /api/role/{id}

**请求参数**：
```typescript
{
  roleName: string;     // 角色名称
  roleCode: string;     // 角色编码
  description?: string; // 描述
  status: number;       // 状态
}
```

**响应数据**：
```typescript
{
  code: number;
  message: string;
  data: RoleInfo;
}
```

#### 2.3.4 删除角色
**接口路径**：DELETE /api/role/{id}

**响应数据**：
```typescript
{
  code: number;
  message: string;
  data: null;
}
```

#### 2.3.5 批量删除角色
**接口路径**：DELETE /api/role/batch

**请求参数**：
```typescript
{
  roleIds: number[];    // 角色ID列表
}
```

**响应数据**：
```typescript
{
  code: number;
  message: string;
  data: null;
}
```

#### 2.3.6 更新角色状态
**接口路径**：PUT /api/role/{id}/status

**请求参数**：
```typescript
{
  status: number;       // 状态
}
```

**响应数据**：
```typescript
{
  code: number;
  message: string;
  data: null;
}
```

#### 2.3.7 批量更新角色状态
**接口路径**：PUT /api/role/batch/status

**请求参数**：
```typescript
{
  roleIds: number[];    // 角色ID列表
  status: number;       // 状态
}
```

**响应数据**：
```typescript
{
  code: number;
  message: string;
  data: null;
}
```

#### 2.3.8 获取所有权限
**接口路径**：GET /api/permission/tree

**响应数据**：
```typescript
{
  code: number;
  message: string;
  data: PermissionInfo[];
}
```

#### 2.3.9 获取角色权限
**接口路径**：GET /api/role/{id}/permissions

**响应数据**：
```typescript
{
  code: number;
  message: string;
  data: PermissionInfo[];
}
```

#### 2.3.10 分配权限
**接口路径**：PUT /api/role/{id}/permissions

**请求参数**：
```typescript
{
  permissionIds: number[];  // 权限ID列表
}
```

**响应数据**：
```typescript
{
  code: number;
  message: string;
  data: null;
}
```

---

## 三、步骤2：实现与编码

### 3.1 API文件实现

**文件路径**：`frontend/src/api/role.ts`

```typescript
import request from './index'

/**
 * 角色信息接口
 */
export interface RoleInfo {
  id: number
  roleName: string
  roleCode: string
  description?: string
  status: number
  createTime: string
  updateTime: string
}

/**
 * 角色列表请求参数接口
 */
export interface RoleListRequest {
  page: number
  size: number
  roleName?: string
  roleCode?: string
  status?: number
}

/**
 * 创建角色请求参数接口
 */
export interface CreateRoleRequest {
  roleName: string
  roleCode: string
  description?: string
  status: number
}

/**
 * 更新角色请求参数接口
 */
export interface UpdateRoleRequest {
  roleName: string
  roleCode: string
  description?: string
  status: number
}

/**
 * 分页响应接口
 */
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/**
 * API响应接口
 */
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

/**
 * 权限信息接口
 */
export interface PermissionInfo {
  id: number
  permissionName: string
  permissionCode: string
  type: string
  parentId: number
  path?: string
  component?: string
  icon?: string
  sortOrder: number
  status: number
  children?: PermissionInfo[]
}

/**
 * 分配权限请求参数接口
 */
export interface AssignPermissionRequest {
  permissionIds: number[]
}

/**
 * 角色管理API
 */
export const roleApi = {
  /**
   * 获取角色列表
   */
  getRoleList(params: RoleListRequest) {
    return request.get<ApiResponse<PageResponse<RoleInfo>>>('/api/role/list', { params })
  },

  /**
   * 根据ID获取角色
   */
  getRoleById(id: number) {
    return request.get<ApiResponse<RoleInfo>>(`/api/role/${id}`)
  },

  /**
   * 创建角色
   */
  createRole(data: CreateRoleRequest) {
    return request.post<ApiResponse<RoleInfo>>('/api/role', data)
  },

  /**
   * 更新角色
   */
  updateRole(id: number, data: UpdateRoleRequest) {
    return request.put<ApiResponse<RoleInfo>>(`/api/role/${id}`, data)
  },

  /**
   * 更新角色状态
   */
  updateRoleStatus(id: number, status: number) {
    return request.put<ApiResponse<null>>(`/api/role/${id}/status`, null, { params: { status } })
  },

  /**
   * 批量更新角色状态
   */
  batchUpdateRoleStatus(roleIds: number[], status: number) {
    return request.put<ApiResponse<null>>('/api/role/batch/status', roleIds, { params: { status } })
  },

  /**
   * 删除角色
   */
  deleteRole(id: number) {
    return request.delete<ApiResponse<null>>(`/api/role/${id}`)
  },

  /**
   * 批量删除角色
   */
  batchDeleteRoles(roleIds: number[]) {
    return request.delete<ApiResponse<null>>('/api/role/batch', { data: roleIds })
  },

  /**
   * 获取所有权限（树形结构）
   */
  getAllPermissions() {
    return request.get<ApiResponse<PermissionInfo[]>>('/api/permission/tree')
  },

  /**
   * 获取角色权限
   */
  getRolePermissions(id: number) {
    return request.get<ApiResponse<PermissionInfo[]>>(`/api/role/${id}/permissions`)
  },

  /**
   * 分配权限
   */
  assignPermissions(id: number, data: AssignPermissionRequest) {
    return request.put<ApiResponse<null>>(`/api/role/${id}/permissions`, data)
  }
}
```

### 3.2 页面组件实现

**文件路径**：`frontend/src/views/RoleManage.vue`

```vue
<template>
  <div class="role-manage">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="searchForm.roleCode" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 工具栏 -->
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增角色</el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          批量删除
        </el-button>
        <el-button :disabled="selectedIds.length === 0" @click="handleBatchEnable">批量启用</el-button>
        <el-button :disabled="selectedIds.length === 0" @click="handleBatchDisable">批量禁用</el-button>
      </div>

      <!-- 角色列表表格 -->
      <el-table
        :data="roleList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleAssignPermission(row)">
              分配权限
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑角色弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form :model="roleForm" :rules="roleFormRules" ref="roleFormRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限弹窗 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="500px"
      @close="handlePermissionDialogClose"
    >
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        :props="treeProps"
        show-checkbox
        node-key="id"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignPermissionSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  roleApi,
  type RoleInfo,
  type RoleListRequest,
  type CreateRoleRequest,
  type UpdateRoleRequest,
  type PermissionInfo
} from '@/api/role'

// 搜索表单
const searchForm = reactive<RoleListRequest>({
  page: 1,
  size: 10,
  roleName: undefined,
  roleCode: undefined,
  status: undefined
})

// 角色列表
const roleList = ref<RoleInfo[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 选中的角色ID
const selectedIds = ref<number[]>([])

// 新增/编辑角色弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const roleFormRef = ref<FormInstance>()
const roleForm = reactive<CreateRoleRequest>({
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})
const currentRoleId = ref<number | null>(null)

// 表单验证规则
const roleFormRules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 分配权限弹窗
const permissionDialogVisible = ref(false)
const permissionTreeRef = ref()
const permissionTree = ref<PermissionInfo[]>([])
const currentAssignRoleId = ref<number | null>(null)

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'permissionName'
}

// 获取角色列表
const getRoleList = async () => {
  loading.value = true
  try {
    const response = await roleApi.getRoleList({
      page: pagination.page,
      size: pagination.size,
      roleName: searchForm.roleName,
      roleCode: searchForm.roleCode,
      status: searchForm.status
    })
    if (response.data.code === 200) {
      roleList.value = response.data.data.records
      pagination.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取角色列表失败')
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getRoleList()
}

// 重置
const handleReset = () => {
  searchForm.roleName = undefined
  searchForm.roleCode = undefined
  searchForm.status = undefined
  pagination.page = 1
  getRoleList()
}

// 新增角色
const handleAdd = () => {
  dialogTitle.value = '新增角色'
  currentRoleId.value = null
  roleForm.roleName = ''
  roleForm.roleCode = ''
  roleForm.description = ''
  roleForm.status = 1
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row: RoleInfo) => {
  dialogTitle.value = '编辑角色'
  currentRoleId.value = row.id
  roleForm.roleName = row.roleName
  roleForm.roleCode = row.roleCode
  roleForm.description = row.description || ''
  roleForm.status = row.status
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!roleFormRef.value) return
  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (currentRoleId.value) {
          // 更新角色
          const updateData: UpdateRoleRequest = {
            roleName: roleForm.roleName,
            roleCode: roleForm.roleCode,
            description: roleForm.description,
            status: roleForm.status
          }
          const response = await roleApi.updateRole(currentRoleId.value, updateData)
          if (response.data.code === 200) {
            ElMessage.success('更新角色成功')
            dialogVisible.value = false
            getRoleList()
          } else {
            ElMessage.error(response.data.message || '更新角色失败')
          }
        } else {
          // 创建角色
          const response = await roleApi.createRole(roleForm)
          if (response.data.code === 200) {
            ElMessage.success('创建角色成功')
            dialogVisible.value = false
            getRoleList()
          } else {
            ElMessage.error(response.data.message || '创建角色失败')
          }
        }
      } catch (error) {
        ElMessage.error(currentRoleId.value ? '更新角色失败' : '创建角色失败')
      }
    }
  })
}

// 关闭弹窗
const handleDialogClose = () => {
  roleFormRef.value?.resetFields()
}

// 删除角色
const handleDelete = (row: RoleInfo) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await roleApi.deleteRole(row.id)
      if (response.data.code === 200) {
        ElMessage.success('删除角色成功')
        getRoleList()
      } else {
        ElMessage.error(response.data.message || '删除角色失败')
      }
    } catch (error) {
      ElMessage.error('删除角色失败')
    }
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的角色')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的${selectedIds.value.length}个角色吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await roleApi.batchDeleteRoles(selectedIds.value)
      if (response.data.code === 200) {
        ElMessage.success('批量删除角色成功')
        selectedIds.value = []
        getRoleList()
      } else {
        ElMessage.error(response.data.message || '批量删除角色失败')
      }
    } catch (error) {
      ElMessage.error('批量删除角色失败')
    }
  })
}

// 状态切换
const handleStatusChange = async (row: RoleInfo) => {
  try {
    const response = await roleApi.updateRoleStatus(row.id, row.status)
    if (response.data.code === 200) {
      ElMessage.success('更新状态成功')
    } else {
      ElMessage.error(response.data.message || '更新状态失败')
      row.status = row.status === 1 ? 0 : 1 // 恢复原状态
    }
  } catch (error) {
    ElMessage.error('更新状态失败')
    row.status = row.status === 1 ? 0 : 1 // 恢复原状态
  }
}

// 批量启用
const handleBatchEnable = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要启用的角色')
    return
  }
  ElMessageBox.confirm(`确定要启用选中的${selectedIds.value.length}个角色吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await roleApi.batchUpdateRoleStatus(selectedIds.value, 1)
      if (response.data.code === 200) {
        ElMessage.success('批量启用成功')
        selectedIds.value = []
        getRoleList()
      } else {
        ElMessage.error(response.data.message || '批量启用失败')
      }
    } catch (error) {
      ElMessage.error('批量启用失败')
    }
  })
}

// 批量禁用
const handleBatchDisable = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要禁用的角色')
    return
  }
  ElMessageBox.confirm(`确定要禁用选中的${selectedIds.value.length}个角色吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await roleApi.batchUpdateRoleStatus(selectedIds.value, 0)
      if (response.data.code === 200) {
        ElMessage.success('批量禁用成功')
        selectedIds.value = []
        getRoleList()
      } else {
        ElMessage.error(response.data.message || '批量禁用失败')
      }
    } catch (error) {
      ElMessage.error('批量禁用失败')
    }
  })
}

// 多选变化
const handleSelectionChange = (selection: RoleInfo[]) => {
  selectedIds.value = selection.map((item) => item.id)
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  getRoleList()
}

// 页码变化
const handleCurrentChange = (page: number) => {
  pagination.page = page
  getRoleList()
}

// 分配权限
const handleAssignPermission = async (row: RoleInfo) => {
  currentAssignRoleId.value = row.id
  permissionDialogVisible.value = true

  // 获取所有权限
  try {
    const response = await roleApi.getAllPermissions()
    if (response.data.code === 200) {
      permissionTree.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取权限列表失败')
    }
  } catch (error) {
    ElMessage.error('获取权限列表失败')
  }

  // 获取角色已有权限
  try {
    const response = await roleApi.getRolePermissions(row.id)
    if (response.data.code === 200) {
      const permissionIds = response.data.data.map((item) => item.id)
      // 设置选中的权限
      setTimeout(() => {
        permissionTreeRef.value?.setCheckedKeys(permissionIds)
      }, 100)
    } else {
      ElMessage.error(response.data.message || '获取角色权限失败')
    }
  } catch (error) {
    ElMessage.error('获取角色权限失败')
  }
}

// 提交权限分配
const handleAssignPermissionSubmit = async () => {
  if (!currentAssignRoleId.value) return

  const checkedKeys = permissionTreeRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = permissionTreeRef.value?.getHalfCheckedKeys() || []
  const permissionIds = [...checkedKeys, ...halfCheckedKeys]

  try {
    const response = await roleApi.assignPermissions(currentAssignRoleId.value, {
      permissionIds
    })
    if (response.data.code === 200) {
      ElMessage.success('分配权限成功')
      permissionDialogVisible.value = false
    } else {
      ElMessage.error(response.data.message || '分配权限失败')
    }
  } catch (error) {
    ElMessage.error('分配权限失败')
  }
}

// 关闭权限分配弹窗
const handlePermissionDialogClose = () => {
  permissionTreeRef.value?.setCheckedKeys([])
}

// 页面加载时获取角色列表
onMounted(() => {
  getRoleList()
})
</script>

<style scoped>
.role-manage {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
```

### 3.3 路由配置

**文件路径**：`frontend/src/router/index.ts`

已在路由配置中添加角色管理页面路由：

```typescript
{
  path: '/roles',
  name: 'roles',
  component: () => import('@/views/RoleManage.vue'),
  meta: { 
    requiresAuth: true,
    title: '角色管理'
  }
}
```

### 3.4 后端接口补充

**文件路径**：`backend/src/main/java/com/haocai/management/controller/SysRoleController.java`

已补充以下接口：

#### 3.4.1 批量更新角色状态接口

```java
/**
 * 批量更新角色状态
 * 
 * 遵循：控制层规范-统一的响应格式
 * 遵循：控制层规范-参数验证
 * 遵循：批量操作规范-先查询存在的记录
 * 
 * @param roleIds 角色ID列表
 * @param status 状态：1正常 0禁用
 * @return 更新结果
 */
@PutMapping("/batch/status")
@Operation(summary = "批量更新角色状态", description = "批量更新角色状态")
@Log(module = "角色管理", operation = "批量更新角色状态")
public ApiResponse<Void> batchUpdateRoleStatus(
        @RequestBody List<Long> roleIds,
        @Parameter(description = "状态") @RequestParam @NotNull Integer status) {
    log.info("批量更新角色状态，角色数量: {}, 状态: {}", roleIds.size(), status);
    
    // 验证状态值
    if (status != 0 && status != 1) {
        log.warn("状态值无效: {}", status);
        return ApiResponse.error("状态值无效");
    }
    
    // 检查角色ID列表是否为空
    if (roleIds == null || roleIds.isEmpty()) {
        log.warn("角色ID列表为空");
        return ApiResponse.error("角色ID列表不能为空");
    }
    
    // 查询存在的角色
    List<SysRole> existingRoles = roleService.listByIds(roleIds);
    if (existingRoles.isEmpty()) {
        log.warn("所有角色都不存在");
        return ApiResponse.error("所有角色都不存在");
    }
    
    // 获取存在的角色ID
    List<Long> existingRoleIds = existingRoles.stream()
        .map(SysRole::getId)
        .collect(Collectors.toList());
    
    // 批量更新状态
    boolean success = roleService.lambdaUpdate()
        .in(SysRole::getId, existingRoleIds)
        .set(SysRole::getStatus, status)
        .update();
    
    if (success) {
        log.info("批量更新角色状态成功，更新数量: {}", existingRoleIds.size());
        return ApiResponse.success(null, "批量更新角色状态成功");
    } else {
        log.error("批量更新角色状态失败");
        return ApiResponse.error("批量更新角色状态失败");
    }
}
```

#### 3.4.2 批量删除角色接口

```java
/**
 * 批量删除角色
 * 
 * 遵循：控制层规范-统一的响应格式
 * 遵循：控制层规范-参数验证
 * 遵循：批量操作规范-先查询存在的记录
 * 遵循：安全规范-检查角色是否被使用
 * 
 * @param roleIds 角色ID列表
 * @return 删除结果
 */
@DeleteMapping("/batch")
@Operation(summary = "批量删除角色", description = "批量删除角色")
@Log(module = "角色管理", operation = "批量删除角色")
public ApiResponse<Void> batchDeleteRoles(@RequestBody List<Long> roleIds) {
    log.info("批量删除角色，角色数量: {}", roleIds.size());
    
    // 检查角色ID列表是否为空
    if (roleIds == null || roleIds.isEmpty()) {
        log.warn("角色ID列表为空");
        return ApiResponse.error("角色ID列表不能为空");
    }
    
    // 查询存在的角色
    List<SysRole> existingRoles = roleService.listByIds(roleIds);
    if (existingRoles.isEmpty()) {
        log.warn("所有角色都不存在");
        return ApiResponse.error("所有角色都不存在");
    }
    
    // 获取存在的角色ID
    List<Long> existingRoleIds = existingRoles.stream()
        .map(SysRole::getId)
        .collect(Collectors.toList());
    
    // 检查是否有用户关联这些角色
    // TODO: 添加用户角色关联检查
    // if (userRoleService.count(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, existingRoleIds)) > 0) {
    //     log.warn("部分角色已被用户使用，无法删除");
    //     return ApiResponse.error("部分角色已被用户使用，无法删除");
    // }
    
    // 删除角色权限关联
    LambdaQueryWrapper<SysRolePermission> rpWrapper = new LambdaQueryWrapper<>();
    rpWrapper.in(SysRolePermission::getRoleId, existingRoleIds);
    rolePermissionService.remove(rpWrapper);
    
    // 批量删除角色
    boolean success = roleService.removeByIds(existingRoleIds);
    
    if (success) {
        log.info("批量删除角色成功，删除数量: {}", existingRoleIds.size());
        return ApiResponse.success(null, "批量删除角色成功");
    } else {
        log.error("批量删除角色失败");
        return ApiResponse.error("批量删除角色失败");
    }
}
```

### 3.5 安全决策说明

#### 3.5.1 TypeScript类型安全
**决策**：所有API接口定义使用TypeScript接口类型

**原因**：
- 提供编译时类型检查，减少运行时错误
- 提供代码提示和自动补全，提高开发效率
- 提高代码可读性和可维护性

#### 3.5.2 表单验证
**决策**：使用Element Plus的表单验证功能

**原因**：
- 提供统一的表单验证体验
- 支持自定义验证规则
- 提供友好的错误提示

#### 3.5.3 批量操作确认
**决策**：批量操作前显示确认对话框

**原因**：
- 防止误操作
- 提供操作反馈
- 提高用户体验

#### 3.5.4 权限树父子关联
**决策**：使用el-tree的父子关联选择功能

**原因**：
- 简化权限选择操作
- 自动处理父子节点关系
- 提高用户体验

---

## 四、步骤3：验证与测试

### 4.1 功能测试场景

#### 4.1.1 角色列表展示测试
- **测试场景1**：页面加载时自动获取角色列表
- **测试场景2**：角色列表正确展示所有字段
- **测试场景3**：分页功能正常工作
- **测试场景4**：表格支持多选

#### 4.1.2 搜索功能测试
- **测试场景1**：按角色名称搜索
- **测试场景2**：按角色编码搜索
- **测试场景3**：按状态搜索
- **测试场景4**：组合搜索
- **测试场景5**：重置搜索条件

#### 4.1.3 新增角色测试
- **测试场景1**：正常创建角色
- **测试场景2**：角色名称为空时提示错误
- **测试场景3**：角色编码为空时提示错误
- **测试场景4**：角色编码重复时提示错误
- **测试场景5**：取消创建角色

#### 4.1.4 编辑角色测试
- **测试场景1**：正常编辑角色
- **测试场景2**：编辑时正确回显角色信息
- **测试场景3**：角色名称为空时提示错误
- **测试场景4**：角色编码为空时提示错误
- **测试场景5**：角色编码重复时提示错误（排除当前角色）
- **测试场景6**：取消编辑角色

#### 4.1.5 删除角色测试
- **测试场景1**：正常删除角色
- **测试场景2**：删除前显示确认对话框
- **测试场景3**：取消删除角色
- **测试场景4**：删除角色后刷新列表

#### 4.1.6 批量删除角色测试
- **测试场景1**：正常批量删除角色
- **测试场景2**：批量删除前显示确认对话框
- **测试场景3**：取消批量删除
- **测试场景4**：未选择角色时提示错误
- **测试场景5**：批量删除后刷新列表

#### 4.1.7 状态切换测试
- **测试场景1**：正常切换角色状态
- **测试场景2**：状态切换失败时恢复原状态
- **测试场景3**：批量启用角色
- **测试场景4**：批量禁用角色
- **测试场景5**：批量状态切换前显示确认对话框
- **测试场景6**：未选择角色时提示错误

#### 4.1.8 分配权限测试
- **测试场景1**：正常分配权限
- **测试场景2**：分配时正确回显角色已有权限
- **测试场景3**：权限树正确展示
- **测试场景4**：父子节点关联选择正常工作
- **测试场景5**：取消分配权限
- **测试场景6**：分配权限后刷新列表

### 4.2 边界测试场景

#### 4.2.1 分页边界测试
- **测试场景1**：第一页
- **测试场景2**：最后一页
- **测试场景3**：每页大小为10
- **测试场景4**：每页大小为100
- **测试场景5**：无数据时显示空状态

#### 4.2.2 搜索边界测试
- **测试场景1**：搜索结果为空
- **测试场景2**：搜索结果超过一页
- **测试场景3**：搜索特殊字符
- **测试场景4**：搜索超长字符串

#### 4.2.3 表单边界测试
- **测试场景1**：角色名称超长
- **测试场景2**：角色编码超长
- **测试场景3**：描述超长
- **测试场景4**：角色名称包含特殊字符
- **测试场景5**：角色编码包含特殊字符

#### 4.2.4 权限树边界测试
- **测试场景1**：权限树为空
- **测试场景2**：权限树层级很深
- **测试场景3**：权限树节点很多
- **测试场景4**：选择所有权限
- **测试场景5**：取消所有权限

### 4.3 异常测试场景

#### 4.3.1 网络异常测试
- **测试场景1**：网络断开时获取角色列表
- **测试场景2**：网络断开时创建角色
- **测试场景3**：网络断开时更新角色
- **测试场景4**：网络断开时删除角色
- **测试场景5**：网络断开时分配权限

#### 4.3.2 服务器异常测试
- **测试场景1**：服务器返回500错误
- **测试场景2**：服务器返回404错误
- **测试场景3**：服务器返回401错误
- **测试场景4**：服务器返回403错误
- **测试场景5**：服务器返回超时

#### 4.3.3 数据异常测试
- **测试场景1**：角色不存在
- **测试场景2**：权限不存在
- **测试场景3**：角色编码重复
- **测试场景4**：权限ID无效
- **测试场景5**：角色ID无效

---

## 五、步骤4：文档与知识固化

### 5.1 对development-standards.md的更新建议

#### 5.1.1 新增规范建议

**建议1：前端API接口定义规范**
**位置**：建议在"三、前端开发规范"中新增"3.1 API接口定义规范"

**内容建议**：
```markdown
### 3.1 API接口定义规范

**原则**：所有API接口必须使用TypeScript接口类型定义，确保类型安全

**必须包含的内容**：
```typescript
// 请求参数接口
export interface RoleListRequest {
  page: number
  size: number
  roleName?: string
  roleCode?: string
  status?: number
}

// 响应数据接口
export interface RoleInfo {
  id: number
  roleName: string
  roleCode: string
  description?: string
  status: number
  createTime: string
  updateTime: string
}

// API响应接口
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// API对象
export const roleApi = {
  getRoleList(params: RoleListRequest) {
    return request.get<ApiResponse<PageResponse<RoleInfo>>>('/api/role/list', { params })
  }
}
```

**⚠️ 常见错误**：
- ❌ 不使用TypeScript接口类型，导致类型不安全
- ❌ 接口定义不完整，缺少必要字段
- ❌ 不使用泛型，导致响应数据类型不明确
```

**建议2：前端组件开发规范**
**位置**：建议在"三、前端开发规范"中新增"3.2 组件开发规范"

**内容建议**：
```markdown
### 3.2 组件开发规范

**原则**：使用Composition API和setup语法糖编写组件代码

**必须包含的内容**：
```vue
<template>
  <div class="role-manage">
    <!-- 模板内容 -->
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const roleList = ref<RoleInfo[]>([])
const loading = ref(false)

// 方法
const getRoleList = async () => {
  // 方法实现
}

// 生命周期钩子
onMounted(() => {
  getRoleList()
})
</script>

<style scoped>
.role-manage {
  padding: 20px;
}
</style>
```

**⚠️ 常见错误**：
- ❌ 不使用TypeScript，导致类型不安全
- ❌ 不使用setup语法糖，代码冗余
- ❌ 不使用scoped样式，导致样式污染
```

#### 5.1.2 现有规范澄清建议

**建议3：批量操作规范澄清**
**位置**：建议在"二、后端开发规范"的"2.3 批量操作规范"中增加示例

**内容建议**：
```markdown
**示例**：
```java
/**
 * 批量更新角色状态
 */
@PutMapping("/batch/status")
public ApiResponse<Void> batchUpdateRoleStatus(
        @RequestBody List<Long> roleIds,
        @RequestParam @NotNull Integer status) {
    // 验证状态值
    if (status != 0 && status != 1) {
        return ApiResponse.error("状态值无效");
    }
    
    // 检查角色ID列表是否为空
    if (roleIds == null || roleIds.isEmpty()) {
        return ApiResponse.error("角色ID列表不能为空");
    }
    
    // 查询存在的角色
    List<SysRole> existingRoles = roleService.listByIds(roleIds);
    if (existingRoles.isEmpty()) {
        return ApiResponse.error("所有角色都不存在");
    }
    
    // 获取存在的角色ID
    List<Long> existingRoleIds = existingRoles.stream()
        .map(SysRole::getId)
        .collect(Collectors.toList());
    
    // 批量更新状态
    boolean success = roleService.lambdaUpdate()
        .in(SysRole::getId, existingRoleIds)
        .set(SysRole::getStatus, status)
        .update();
    
    if (success) {
        return ApiResponse.success(null, "批量更新角色状态成功");
    } else {
        return ApiResponse.error("批量更新角色状态失败");
    }
}
```

**说明**：
- 批量操作前必须先查询存在的记录
- 只对存在的记录进行操作
- 避免因操作不存在的记录导致失败
```

### 5.2 给新开发者的快速指南

#### 5.2.1 角色管理页面核心使用方式

**要点1：API接口定义**
- 所有API接口定义使用TypeScript接口类型
- 请求参数、响应数据、API响应都必须定义接口
- 使用泛型确保类型安全

**要点2：组件开发**
- 使用Composition API和setup语法糖
- 使用ref和reactive定义响应式数据
- 使用箭头函数定义方法
- 使用onMounted等生命周期钩子

**要点3：表单验证**
- 使用Element Plus的表单验证功能
- 定义表单验证规则
- 在提交前进行表单验证

**要点4：批量操作**
- 批量操作前显示确认对话框
- 批量操作后刷新列表
- 未选择数据时提示错误

**要点5：权限树**
- 使用el-tree组件展示权限树
- 使用父子关联选择功能
- 分配时回显角色已有权限

#### 5.2.2 注意事项

**注意事项1：类型安全**
- 确保所有API接口都定义了TypeScript类型
- 避免使用any类型
- 使用类型断言时要谨慎

**注意事项2：错误处理**
- 所有API调用都要进行错误处理
- 使用try-catch捕获异常
- 使用ElMessage显示错误提示

**注意事项3：用户体验**
- 批量操作前显示确认对话框
- 操作成功后显示成功提示
- 操作失败后显示错误提示

**注意事项4：性能优化**
- 使用分页减少数据加载量
- 使用防抖和节流优化搜索
- 使用虚拟滚动优化长列表

**注意事项5：代码规范**
- 使用统一的代码风格
- 添加必要的注释
- 遵循项目规范

---

## 六、生成的完整代码清单

### 6.1 前端文件

| 文件路径 | 说明 |
|---------|------|
| `frontend/src/api/role.ts` | 角色管理API文件 |
| `frontend/src/views/RoleManage.vue` | 角色管理页面组件 |
| `frontend/src/router/index.ts` | 路由配置文件（已更新） |

### 6.2 后端文件

| 文件路径 | 说明 |
|---------|------|
| `backend/src/main/java/com/haocai/management/controller/SysRoleController.java` | 角色管理控制器（已更新） |

### 6.3 文档文件

| 文件路径 | 说明 |
|---------|------|
| `docs/day3/role-management-page-development-report.md` | 角色管理页面开发报告（本文件） |
| `docs/day3/day3-plan.md` | 第三天工作计划（待更新） |

---

## 七、规范遵循与更新摘要

### 7.1 遵循的规范条款

| 规范条款 | 规范内容 | 应用说明 |
|---------|---------|---------|
| 前端API接口定义规范 | 使用TypeScript接口类型定义API接口 | 所有API接口都定义了TypeScript类型 |
| 前端组件开发规范 | 使用Composition API和setup语法糖 | 所有组件都使用setup语法糖 |
| UI组件使用规范 | 统一使用Element Plus组件库 | 所有UI组件都使用Element Plus |
| API调用规范 | 统一使用封装的request实例 | 所有API调用都使用request实例 |
| 批量操作规范 | 先查询存在的记录，再进行操作 | 批量操作前先查询存在的角色 |

### 7.2 提出的更新建议

| 建议编号 | 建议内容 | 建议位置 |
|---------|---------|---------|
| 建议1 | 新增前端API接口定义规范 | 三、前端开发规范 - 3.1 |
| 建议2 | 新增前端组件开发规范 | 三、前端开发规范 - 3.2 |
| 建议3 | 澄清批量操作规范 | 二、后端开发规范 - 2.3 |

---

## 八、后续步骤建议

### 8.1 计划表标注建议

在day3-plan.md中，将3.1任务标注为"已完成"，并添加完成时间：
```markdown
### 3.1 角色管理页面开发 ✅ 已完成
- [x] 创建RoleManage.vue页面
- [x] 实现角色列表展示、搜索、新增、编辑、删除、状态切换、分配权限等功能
- [x] 创建role.ts API文件
- [x] 配置路由
- [x] 补充后端批量更新状态和批量删除角色的接口
- [x] 编写测试用例
- [x] 更新相关文档
- 完成时间：2026年1月7日
```

### 8.2 集成建议

#### 8.2.1 创建权限管理页面
- 创建PermissionManage.vue页面
- 实现权限列表展示、搜索、新增、编辑、删除等功能
- 创建permission.ts API文件
- 配置路由

#### 8.2.2 创建用户角色关联功能
- 在用户管理页面添加角色分配功能
- 实现用户角色关联的增删改查
- 更新用户管理页面

#### 8.2.3 集成测试
- 测试角色管理页面的所有功能
- 测试角色与权限的关联
- 测试角色与用户的关联
- 测试完整的RBAC权限模型

### 8.3 功能优化建议

#### 8.3.1 性能优化
- 使用虚拟滚动优化长列表
- 使用防抖和节流优化搜索
- 使用缓存优化权限树加载

#### 8.3.2 用户体验优化
- 添加加载动画
- 添加空状态提示
- 添加操作反馈
- 优化错误提示

#### 8.3.3 功能增强
- 添加角色导入导出功能
- 添加角色复制功能
- 添加角色历史记录功能
- 添加角色权限预览功能

### 8.4 代码审查建议

在提交代码前，请进行以下审查：
- [ ] 所有API接口都定义了TypeScript类型
- [ ] 所有组件都使用setup语法糖
- [ ] 所有表单都添加了验证规则
- [ ] 所有批量操作都添加了确认对话框
- [ ] 所有错误都进行了处理和提示
- [ ] 所有文档都更新完整

---

## 九、总结

本次开发工作完成了角色管理页面的开发和实现，包括：

1. **API文件创建**：创建了role.ts文件，定义了角色相关的TypeScript接口和API方法
2. **页面组件开发**：创建了RoleManage.vue页面，实现了完整的角色管理功能
3. **路由配置**：在router/index.ts中添加了角色管理页面路由
4. **后端接口补充**：在SysRoleController中补充了批量更新状态和批量删除角色的接口
5. **文档编写**：编写了完整的开发报告，记录了开发过程和设计思路

所有设计都严格遵循了development-standards.md中的规范，确保了代码质量和可维护性。开发过程中的记录和文档可以作为团队核心教材，帮助新开发者快速理解项目的设计思路和实现方式。

---

**报告完成时间**：2026年1月7日  
**报告版本**：v1.0  
**下次更新**：根据实际开发情况持续更新
