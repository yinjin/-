<template>
  <div class="department-manage-container">
    <el-card>
      <!-- 搜索区域 -->
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="部门名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入部门名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            @clear="handleSearch"
          >
            <el-option label="正常" value="NORMAL" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮区域 -->
      <div class="action-buttons">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增部门
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
        <el-button @click="handleRefreshTree">
          <el-icon><Refresh /></el-icon>
          刷新树形结构
        </el-button>
      </div>

      <!-- 部门树形结构和列表 -->
      <div class="department-content">
        <!-- 左侧树形结构 -->
        <div class="department-tree">
          <div class="tree-header">
            <span>部门树形结构</span>
            <el-button
              type="link"
              size="small"
              @click="handleExpandAll"
            >
              {{ expandAll ? '收起全部' : '展开全部' }}
            </el-button>
          </div>
          <el-tree
            ref="treeRef"
            :data="departmentTree"
            :props="treeProps"
            node-key="id"
            :default-expanded-keys="expandedKeys"
            :expand-on-click-node="false"
            highlight-current
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <span class="node-label">
                  <el-icon v-if="data.status === 'DISABLED'" style="color: #909399"><Lock /></el-icon>
                  <el-icon v-else style="color: #67c23a"><OfficeBuilding /></el-icon>
                  {{ node.label }}
                </span>
                <span class="node-actions" @click.stop>
                  <el-button
                    type="primary"
                    link
                    size="small"
                    @click="handleAddChild(data)"
                  >
                    新增
                  </el-button>
                  <el-button
                    type="success"
                    link
                    size="small"
                    @click="handleEdit(data)"
                  >
                    编辑
                  </el-button>
                  <el-button
                    type="danger"
                    link
                    size="small"
                    @click="handleDelete(data)"
                  >
                    删除
                  </el-button>
                </span>
              </div>
            </template>
          </el-tree>
        </div>

        <!-- 右侧部门列表 -->
        <div class="department-list">
          <div class="list-header">
            <span>部门列表</span>
          </div>
          <el-table
            :data="departmentList"
            v-loading="loading"
            border
            stripe
            style="width: 100%"
          >
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="部门名称" width="150" />
            <el-table-column prop="code" label="部门编码" width="120" />
            <el-table-column prop="parentName" label="上级部门" width="120" />
            <el-table-column prop="level" label="层级" width="80">
              <template #default="{ row }">
                <el-tag size="small">第{{ row.level }}级</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'NORMAL' ? 'success' : 'info'">
                  {{ row.status === 'NORMAL' ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="leaderName" label="负责人" width="100" />
            <el-table-column prop="sortOrder" label="排序" width="80" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleEdit(row)">
                  编辑
                </el-button>
                <el-button
                  :type="row.status === 'NORMAL' ? 'warning' : 'success'"
                  size="small"
                  @click="handleToggleStatus(row)"
                >
                  {{ row.status === 'NORMAL' ? '禁用' : '启用' }}
                </el-button>
                <el-button type="danger" size="small" @click="handleDelete(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页组件 -->
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            style="margin-top: 20px; justify-content: flex-end"
          />
        </div>
      </div>
    </el-card>

    <!-- 新增/编辑部门弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="departmentFormRef"
        :model="departmentForm"
        :rules="departmentRules"
        label-width="100px"
      >
        <el-form-item label="部门名称" prop="name">
          <el-input
            v-model="departmentForm.name"
            placeholder="请输入部门名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="部门编码" prop="code">
          <el-input
            v-model="departmentForm.code"
            placeholder="请输入部门编码"
            maxlength="50"
            show-word-limit
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="departmentForm.parentId"
            :data="parentDepartmentTree"
            :props="treeSelectProps"
            node-key="id"
            check-strictly
            placeholder="请选择上级部门（不选则为顶级部门）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number
            v-model="departmentForm.sortOrder"
            :min="0"
            :max="9999"
            controls-position="right"
          />
          <span class="form-tip">数字越小越靠前</span>
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="isEdit">
          <el-radio-group v-model="departmentForm.status">
            <el-radio label="NORMAL">正常</el-radio>
            <el-radio label="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input
            v-model="departmentForm.leader"
            placeholder="请输入负责人用户名"
            clearable
            maxlength="50"
          />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input
            v-model="departmentForm.phone"
            placeholder="请输入联系电话"
            maxlength="20"
          />
        </el-form-item>
        <el-form-item label="联系邮箱">
          <el-input
            v-model="departmentForm.email"
            placeholder="请输入联系邮箱"
            maxlength="100"
          />
        </el-form-item>
        <el-form-item label="部门描述">
          <el-input
            v-model="departmentForm.description"
            type="textarea"
            placeholder="请输入部门描述"
            :rows="3"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="删除确认"
      width="400px"
    >
      <template v-if="deleteCheckResult">
        <el-alert
          v-if="!deleteCheckResult.canDelete"
          type="warning"
          :closable="false"
          show-icon
        >
          <template #title>
            {{ deleteCheckResult.reason }}
          </template>
        </el-alert>
        <p v-else>确定要删除部门「{{ deleteTarget?.name }}」吗？</p>
      </template>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button
          type="danger"
          :loading="deleteLoading"
          :disabled="!deleteCheckResult?.canDelete"
          @click="handleConfirmDelete"
        >
          确定删除
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Refresh, Lock, OfficeBuilding } from '@element-plus/icons-vue'
import { departmentApi } from '@/api/department'
import { userApi } from '@/api/user'
import type {
  DepartmentCreateDTO,
  DepartmentUpdateDTO,
  DepartmentVO,
  DepartmentTreeVO
} from '@/types/department'
import type { UserInfo } from '@/api/user'

// ==================== 响应式数据 ====================

// 树形结构数据
const departmentTree = ref<DepartmentTreeVO[]>([])
const parentDepartmentTree = ref<DepartmentTreeVO[]>([])
const treeRef = ref()
const expandedKeys = ref<number[]>([])

// 部门列表数据
const departmentList = ref<DepartmentVO[]>([])
const loading = ref(false)
const submitLoading = ref(false)
const deleteLoading = ref(false)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '' as 'NORMAL' | 'DISABLED' | ''
})

// 分页参数
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 选中的部门ID
const selectedIds = ref<number[]>([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const isEdit = ref(false)
const departmentFormRef = ref()

// 部门表单
const departmentForm = reactive<{
  id?: number
  name: string
  code: string
  parentId: number | null
  sortOrder: number
  status?: 'NORMAL' | 'DISABLED'
  leader: string | undefined
  phone: string
  email: string
  description: string
}>({
  id: undefined,
  name: '',
  code: '',
  parentId: null,
  sortOrder: 0,
  status: 'NORMAL',
  leader: undefined,
  phone: '',
  email: '',
  description: ''
})

// 表单验证规则
const departmentRules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { max: 100, message: '部门名称长度不能超过100个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入部门编码', trigger: 'blur' },
    { max: 50, message: '部门编码长度不能超过50个字符', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_]+$/, message: '部门编码只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  parentId: [
    { required: false, message: '请选择上级部门', trigger: 'change' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序号', trigger: 'blur' }
  ]
}

// 树形组件配置
const treeProps = {
  label: 'name',
  children: 'children',
  value: 'id'
}

const treeSelectProps = {
  label: 'name',
  children: 'children',
  value: 'id'
}

// 用户列表（用于选择负责人）
const userList = ref<UserInfo[]>([])

// 删除相关
const deleteDialogVisible = ref(false)
const deleteTarget = ref<DepartmentVO | null>(null)
const deleteCheckResult = ref<{ canDelete: boolean; reason: string } | null>(null)

// 展开全部状态
const expandAll = ref(false)

// ==================== 计算属性 ====================

// ==================== 生命周期钩子 ====================

onMounted(() => {
  fetchDepartmentTree()
  fetchDepartmentList()
  fetchUserList()
})

// ==================== 方法 ====================

/**
 * 获取部门树形结构
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const fetchDepartmentTree = async () => {
  try {
    const response = await departmentApi.getDepartmentTree(false)
    if (response.code === 200 && response.data) {
      departmentTree.value = response.data
      // 默认展开所有顶级节点
      expandedKeys.value = response.data
        .filter((d: DepartmentTreeVO) => d.children && d.children.length > 0)
        .map((d: DepartmentTreeVO) => d.id)
      // 构建用于选择的树（包含禁用部门）
      await fetchParentDepartmentTree()
    } else {
      ElMessage.error(response.message || '获取部门树形结构失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取部门树形结构失败')
  }
}

/**
 * 获取所有部门树（用于上级部门选择）
 */
const fetchParentDepartmentTree = async () => {
  try {
    const response = await departmentApi.getDepartmentTree(true)
    if (response.code === 200 && response.data) {
      parentDepartmentTree.value = response.data
    }
  } catch (error: any) {
    console.error('获取上级部门树失败:', error)
  }
}

/**
 * 获取部门列表
 */
const fetchDepartmentList = async () => {
  loading.value = true
  try {
    const response = await departmentApi.listDepartments({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status as 'NORMAL' | 'DISABLED' | undefined
    })
    
    if (response.code === 200 && response.data) {
      departmentList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取部门列表失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取部门列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取用户列表（用于选择负责人）
 */
const fetchUserList = async () => {
  try {
    const response = await userApi.getUserList({
      page: 1,
      size: 1000
    })
    if (response.code === 200 && response.data) {
      userList.value = response.data.records || []
    }
  } catch (error: any) {
    console.error('获取用户列表失败:', error)
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.page = 1
  fetchDepartmentList()
}

/**
 * 重置
 */
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  pagination.page = 1
  fetchDepartmentList()
}

/**
 * 分页大小改变
 */
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  fetchDepartmentList()
}

/**
 * 当前页改变
 */
const handleCurrentChange = (page: number) => {
  pagination.page = page
  fetchDepartmentList()
}

/**
 * 刷新树形结构
 */
const handleRefreshTree = () => {
  fetchDepartmentTree()
  ElMessage.success('刷新成功')
}

/**
 * 展开/收起全部
 */
const handleExpandAll = () => {
  expandAll.value = !expandAll.value
  if (expandAll.value) {
    // 展开所有节点
    const expandKeys = (nodes: DepartmentTreeVO[]) => {
      nodes.forEach(node => {
        expandedKeys.value.push(node.id)
        if (node.children && node.children.length > 0) {
          expandKeys(node.children)
        }
      })
    }
    expandKeys(departmentTree.value)
  } else {
    // 收起所有节点
    expandedKeys.value = []
  }
}

/**
 * 点击树节点
 */
const handleNodeClick = (data: DepartmentTreeVO) => {
  // 可以在这里实现点击节点显示详情等功能
  console.log('点击节点:', data)
}

/**
 * 新增部门
 */
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增部门'
  dialogVisible.value = true
  resetDepartmentForm()
}

/**
 * 新增子部门
 */
const handleAddChild = (data: DepartmentTreeVO) => {
  isEdit.value = false
  dialogTitle.value = '新增子部门'
  dialogVisible.value = true
  resetDepartmentForm()
  departmentForm.parentId = data.id
}

/**
 * 编辑部门
 */
const handleEdit = (data: DepartmentVO | DepartmentTreeVO) => {
  isEdit.value = true
  dialogTitle.value = '编辑部门'
  dialogVisible.value = true
  
  // 回显部门信息
  departmentForm.id = data.id
  departmentForm.name = data.name
  departmentForm.code = data.code
  departmentForm.parentId = data.parentId
  departmentForm.sortOrder = data.sortOrder
  departmentForm.status = data.status
  departmentForm.leader = data.leader ?? undefined
  departmentForm.phone = data.phone || ''
  departmentForm.email = data.email || ''
  departmentForm.description = data.description || ''
}

/**
 * 删除部门
 */
const handleDelete = async (data: DepartmentVO | DepartmentTreeVO) => {
  deleteTarget.value = data as DepartmentVO
  
  try {
    // 先检查是否可以删除
    const checkResponse = await departmentApi.checkCanDelete(data.id)
    if (checkResponse.code === 200 && checkResponse.data) {
      deleteCheckResult.value = checkResponse.data
      
      // 只有检查通过时才显示确认对话框
      if (checkResponse.data.canDelete) {
        deleteDialogVisible.value = true
      } else {
        // 检查不通过，显示警告信息
        ElMessage.warning(checkResponse.data.reason)
      }
    } else {
      ElMessage.error(checkResponse.message || '检查删除条件失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '检查删除条件失败')
  }
}

/**
 * 确认删除
 */
const handleConfirmDelete = async () => {
  if (!deleteTarget.value) return
  
  try {
    deleteLoading.value = true
    const response = await departmentApi.deleteDepartment(deleteTarget.value.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      deleteDialogVisible.value = false
      // 刷新数据
      await Promise.all([
        fetchDepartmentTree(),
        fetchDepartmentList()
      ])
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '删除失败')
  } finally {
    deleteLoading.value = false
  }
}

/**
 * 批量删除
 */
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的部门')
    return
  }
  
  // 先检查每个部门是否可删除
  const deletableIds: number[] = []
  const nonDeletableReasons: string[] = []
  
  for (const id of selectedIds.value) {
    try {
      const checkResponse = await departmentApi.checkCanDelete(id)
      if (checkResponse.code === 200 && checkResponse.data) {
        if (checkResponse.data.canDelete) {
          deletableIds.push(id)
        } else {
          // 尝试获取部门名称
          const dept = departmentList.value.find(d => d.id === id)
          const deptName = dept?.name || `ID: ${id}`
          nonDeletableReasons.push(`${deptName}: ${checkResponse.data.reason}`)
        }
      }
    } catch (error: any) {
      console.error(`检查部门 ${id} 删除条件失败:`, error)
    }
  }
  
  // 如果有不可删除的部门，显示警告
  if (nonDeletableReasons.length > 0) {
    ElMessage.warning(`以下部门无法删除：\n${nonDeletableReasons.join('\n')}`)
  }
  
  // 如果没有可删除的部门，直接返回
  if (deletableIds.length === 0) {
    return
  }
  
  // 确认删除
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${deletableIds.length} 个部门吗？${nonDeletableReasons.length > 0 ? `\n\n注意：${nonDeletableReasons.length} 个部门因存在子部门或用户无法删除` : ''}`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await departmentApi.batchDeleteDepartments(deletableIds)
    if (response.code === 200) {
      const result = response.data
      ElMessage.success(`成功删除 ${result.success} 个部门`)
      if (result.failed > 0) {
        ElMessage.warning(`${result.failed} 个部门删除失败：${result.failedDetails.join('; ')}`)
      }
      selectedIds.value = []
      // 刷新数据
      await Promise.all([
        fetchDepartmentTree(),
        fetchDepartmentList()
      ])
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

/**
 * 切换部门状态
 */
const handleToggleStatus = async (data: DepartmentVO) => {
  const newStatus = data.status === 'NORMAL' ? 'DISABLED' : 'NORMAL'
  const action = newStatus === 'NORMAL' ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}部门 "${data.name}" 吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await departmentApi.batchUpdateStatus([data.id], newStatus)
    if (response.code === 200) {
      ElMessage.success(`${action}成功`)
      fetchDepartmentList()
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || `${action}失败`)
    }
  }
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!departmentFormRef.value) return
  
  try {
    await departmentFormRef.value.validate()
    
    submitLoading.value = true
    
    if (isEdit.value) {
      // 编辑部门
      const updateData: DepartmentUpdateDTO = {
        id: departmentForm.id!,
        name: departmentForm.name,
        code: departmentForm.code,
        parentId: departmentForm.parentId,
        sortOrder: departmentForm.sortOrder,
        status: departmentForm.status,
        leader: departmentForm.leader ?? undefined,
        phone: departmentForm.phone,
        email: departmentForm.email,
        description: departmentForm.description
      }
      
      const response = await departmentApi.updateDepartment(updateData)
      if (response.code === 200) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        // 刷新数据
        await Promise.all([
          fetchDepartmentTree(),
          fetchDepartmentList()
        ])
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    } else {
      // 新增部门
      const createData: DepartmentCreateDTO = {
        name: departmentForm.name,
        code: departmentForm.code,
        parentId: departmentForm.parentId,
        sortOrder: departmentForm.sortOrder,
        leader: departmentForm.leader,
        phone: departmentForm.phone,
        email: departmentForm.email,
        description: departmentForm.description
      }
      
      const response = await departmentApi.createDepartment(createData)
      if (response.code === 200) {
        ElMessage.success('新增成功')
        dialogVisible.value = false
        // 刷新数据
        await Promise.all([
          fetchDepartmentTree(),
          fetchDepartmentList()
        ])
      } else {
        ElMessage.error(response.message || '新增失败')
      }
    }
  } catch (error: any) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    submitLoading.value = false
  }
}

/**
 * 关闭弹窗
 */
const handleDialogClose = () => {
  resetDepartmentForm()
  departmentFormRef.value?.resetFields()
}

/**
 * 重置部门表单
 */
const resetDepartmentForm = () => {
  departmentForm.id = undefined
  departmentForm.name = ''
  departmentForm.code = ''
  departmentForm.parentId = null
  departmentForm.sortOrder = 0
  departmentForm.status = 'NORMAL'
  departmentForm.leader = undefined
  departmentForm.phone = ''
  departmentForm.email = ''
  departmentForm.description = ''
}
</script>

<style scoped>
.department-manage-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.action-buttons {
  margin-bottom: 20px;
}

.action-buttons .el-button {
  margin-right: 10px;
}

.department-content {
  display: flex;
  gap: 20px;
  min-height: 500px;
}

.department-tree {
  width: 300px;
  min-width: 300px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 10px;
  font-weight: bold;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
  padding-right: 8px;
}

.node-label {
  display: flex;
  align-items: center;
  gap: 4px;
}

.node-actions {
  display: none;
}

.tree-node:hover .node-actions {
  display: inline-flex;
  gap: 4px;
}

.department-list {
  flex: 1;
}

.list-header {
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 10px;
  font-weight: bold;
}

.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
</style>
