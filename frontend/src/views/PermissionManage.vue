<template>
  <div class="permission-manage">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="权限名称">
          <el-input v-model="searchForm.permissionName" placeholder="请输入权限名称" clearable />
        </el-form-item>
        <el-form-item label="权限类型">
          <el-select v-model="searchForm.permissionType" placeholder="请选择权限类型" clearable>
            <el-option label="菜单" value="menu" />
            <el-option label="按钮" value="button" />
            <el-option label="接口" value="api" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="handleAdd">新增权限</el-button>
        <el-button type="success" @click="handleExpandAll">展开全部</el-button>
        <el-button type="info" @click="handleCollapseAll">折叠全部</el-button>
      </div>

      <!-- 权限树形表格 -->
      <el-table
        :data="permissionTree"
        style="width: 100%; margin-top: 20px"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :expand-row-keys="expandedKeys"
        @expand-change="handleExpandChange"
      >
        <el-table-column prop="permissionName" label="权限名称" width="200" />
        <el-table-column prop="permissionCode" label="权限编码" width="200" />
        <el-table-column prop="type" label="权限类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'menu'" type="success">菜单</el-tag>
            <el-tag v-else-if="row.type === 'button'" type="warning">按钮</el-tag>
            <el-tag v-else-if="row.type === 'api'" type="info">接口</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.path || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="component" label="组件" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.component || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="{ row }">
            <el-icon v-if="row.icon">
              <component :is="row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
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
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleAddChild(row)">
              新增子权限
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑权限弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="permissionFormRef" :model="permissionForm" :rules="permissionFormRules" label-width="100px">
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="permissionForm.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="permissionForm.permissionCode" placeholder="请输入权限编码" />
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-radio-group v-model="permissionForm.type">
            <el-radio value="menu">菜单</el-radio>
            <el-radio value="button">按钮</el-radio>
            <el-radio value="api">接口</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="父级权限" prop="parentId">
          <el-tree-select
            v-model="permissionForm.parentId"
            :data="permissionTree"
            :props="treeProps"
            placeholder="请选择父级权限"
            clearable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="permissionForm.path" placeholder="请输入路径" />
        </el-form-item>
        <el-form-item label="组件" prop="component">
          <el-input v-model="permissionForm.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="permissionForm.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="permissionForm.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="permissionForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  permissionApi,
  type PermissionInfo,
  type PermissionListRequest,
  type CreatePermissionRequest,
  type UpdatePermissionRequest
} from '@/api/permission'

// 搜索表单
const searchForm = reactive<PermissionListRequest>({
  permissionName: '',
  permissionType: ''
})

// 权限树数据
const permissionTree = ref<PermissionInfo[]>([])

// 展开的行
const expandedKeys = ref<string[]>([])

// 弹窗显示状态
const dialogVisible = ref(false)
const dialogTitle = ref('新增权限')

// 权限表单
const permissionFormRef = ref<FormInstance>()
const permissionForm = reactive<CreatePermissionRequest>({
  permissionName: '',
  permissionCode: '',
  type: 'menu',
  parentId: undefined,
  path: '',
  component: '',
  icon: '',
  sortOrder: 0,
  status: 1
})

// 权限表单验证规则
const permissionFormRules: FormRules = {
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 当前编辑的权限ID
const currentPermissionId = ref<number>()

// 树形结构配置
const treeProps = {
  children: 'children',
  label: 'permissionName',
  value: 'id'
}

// 获取权限树
const getPermissionTree = async () => {
  try {
    const response = await permissionApi.getPermissionTree()
    if (response.code === 200) {
      permissionTree.value = response.data
    } else {
      ElMessage.error(response.message || '获取权限树失败')
    }
  } catch (error) {
    ElMessage.error('获取权限树失败')
  }
}

// 搜索
const handleSearch = () => {
  // 树形结构暂不支持搜索过滤，这里可以后续扩展
  ElMessage.info('树形结构暂不支持搜索过滤')
}

// 重置
const handleReset = () => {
  searchForm.permissionName = ''
  searchForm.permissionType = ''
  getPermissionTree()
}

// 展开全部
const handleExpandAll = () => {
  const allKeys: string[] = []
  const collectKeys = (nodes: PermissionInfo[]) => {
    nodes.forEach(node => {
      allKeys.push(String(node.id!))
      if (node.children && node.children.length > 0) {
        collectKeys(node.children)
      }
    })
  }
  collectKeys(permissionTree.value)
  expandedKeys.value = allKeys
}

// 折叠全部
const handleCollapseAll = () => {
  expandedKeys.value = []
}

// 展开变化
const handleExpandChange = (row: PermissionInfo, expanded: boolean) => {
  const rowId = String(row.id!)
  if (expanded) {
    if (!expandedKeys.value.includes(rowId)) {
      expandedKeys.value.push(rowId)
    }
  } else {
    const index = expandedKeys.value.indexOf(rowId)
    if (index > -1) {
      expandedKeys.value.splice(index, 1)
    }
  }
}

// 新增权限
const handleAdd = () => {
  dialogTitle.value = '新增权限'
  currentPermissionId.value = undefined
  Object.assign(permissionForm, {
    permissionName: '',
    permissionCode: '',
    type: 'menu',
    parentId: undefined,
    path: '',
    component: '',
    icon: '',
    sortOrder: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 新增子权限
const handleAddChild = (row: PermissionInfo) => {
  dialogTitle.value = '新增子权限'
  currentPermissionId.value = undefined
  Object.assign(permissionForm, {
    permissionName: '',
    permissionCode: '',
    type: 'menu',
    parentId: row.id,
    path: '',
    component: '',
    icon: '',
    sortOrder: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑权限
const handleEdit = (row: PermissionInfo) => {
  dialogTitle.value = '编辑权限'
  currentPermissionId.value = row.id
  Object.assign(permissionForm, {
    permissionName: row.permissionName,
    permissionCode: row.permissionCode,
    type: row.type,
    parentId: row.parentId,
    path: row.path,
    component: row.component,
    icon: row.icon,
    sortOrder: row.sortOrder || 0,
    status: row.status
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!permissionFormRef.value) return

  await permissionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (currentPermissionId.value) {
          // 更新权限
          const updateData: UpdatePermissionRequest = {
            id: currentPermissionId.value,
            ...permissionForm
          }
          const response = await permissionApi.updatePermission(updateData)
          if (response.code === 200) {
            ElMessage.success('更新权限成功')
            dialogVisible.value = false
            getPermissionTree()
          } else {
            ElMessage.error(response.message || '更新权限失败')
          }
        } else {
          // 创建权限
          const response = await permissionApi.createPermission(permissionForm)
          if (response.code === 200) {
            ElMessage.success('创建权限成功')
            dialogVisible.value = false
            getPermissionTree()
          } else {
            ElMessage.error(response.message || '创建权限失败')
          }
        }
      } catch (error) {
        ElMessage.error(currentPermissionId.value ? '更新权限失败' : '创建权限失败')
      }
    }
  })
}

// 关闭弹窗
const handleDialogClose = () => {
  permissionFormRef.value?.resetFields()
}

// 状态切换
const handleStatusChange = async (row: PermissionInfo) => {
  try {
    const updateData: UpdatePermissionRequest = {
      id: row.id!,
      permissionName: row.permissionName,
      permissionCode: row.permissionCode,
      type: row.type,
      parentId: row.parentId,
      path: row.path,
      component: row.component,
      icon: row.icon,
      sortOrder: row.sortOrder,
      status: row.status
    }
    const response = await permissionApi.updatePermission(updateData)
    if (response.code === 200) {
      ElMessage.success('状态更新成功')
    } else {
      ElMessage.error(response.message || '状态更新失败')
      // 恢复状态
      row.status = row.status === 1 ? 0 : 1
    }
  } catch (error) {
    ElMessage.error('状态更新失败')
    // 恢复状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 删除权限
const handleDelete = (row: PermissionInfo) => {
  ElMessageBox.confirm('确定要删除该权限吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await permissionApi.deletePermission(row.id!)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          getPermissionTree()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 页面加载时获取权限树
onMounted(() => {
  getPermissionTree()
})
</script>

<style scoped>
.permission-manage {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.action-buttons {
  margin-bottom: 20px;
}
</style>
