<template>
  <div class="role-manage">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.name" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="searchForm.code" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="handleAdd">新增角色</el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          批量删除
        </el-button>
        <el-button
          type="warning"
          :disabled="selectedIds.length === 0"
          @click="handleBatchEnable"
        >
          批量启用
        </el-button>
        <el-button
          type="info"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDisable"
        >
          批量禁用
        </el-button>
      </div>

      <!-- 角色列表栅格布局 -->
      <div class="role-grid" style="margin-top: 20px">
        <el-row :gutter="20">
          <el-col
            v-for="role in roleList"
            :key="role.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="8"
            :xl="6"
            style="margin-bottom: 20px"
          >
            <el-card class="role-card" shadow="hover">
              <template #header>
                <div class="role-card-header">
                  <el-checkbox
                    :model-value="selectedIds.includes(role.id!)"
                    @change="() => handleCheckboxChange(role)"
                    style="margin-right: 10px"
                  />
                  <span class="role-name">{{ role.name }}</span>
                  <el-tag
                    :type="role.status === 1 ? 'success' : 'danger'"
                    size="small"
                    style="margin-left: auto"
                  >
                    {{ role.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </div>
              </template>
              <div class="role-card-body">
                <div class="role-info">
                  <span class="label">角色编码：</span>
                  <span class="value">{{ role.code }}</span>
                </div>
                <div class="role-info">
                  <span class="label">描述：</span>
                  <span class="value" :title="role.description">{{ role.description || '暂无描述' }}</span>
                </div>
                <div class="role-info">
                  <span class="label">权限：</span>
                  <div v-if="role.permissions && role.permissions.length > 0" class="permission-tags">
                    <el-tag
                      v-for="permission in role.permissions"
                      :key="permission.id"
                      size="small"
                      style="margin-right: 4px; margin-bottom: 4px"
                    >
                      {{ permission.permissionName }}
                    </el-tag>
                  </div>
                  <span v-else class="no-permissions">暂无权限</span>
                </div>
                <div class="role-info">
                  <span class="label">创建时间：</span>
                  <span class="value">{{ role.createTime }}</span>
                </div>
              </div>
              <template #footer>
                <div class="role-card-footer">
                  <el-button type="primary" size="small" @click="handleEdit(role)">编辑</el-button>
                  <el-button type="success" size="small" @click="handleAssignPermission(role)">
                    分配权限
                  </el-button>
                  <el-button type="danger" size="small" @click="handleDelete(role)">删除</el-button>
                </div>
              </template>
            </el-card>
          </el-col>
        </el-row>
        <el-empty v-if="roleList.length === 0" description="暂无角色数据" />
      </div>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑角色弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form ref="roleFormRef" :model="roleForm" :rules="roleFormRules" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="roleForm.code" placeholder="请输入角色编码" />
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
        <el-button type="primary" @click="handleAssignPermissions">确定</el-button>
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
  pageNum: 1,
  pageSize: 10,
  name: '',
  code: '',
  status: undefined
})

// 角色列表
const roleList = ref<RoleInfo[]>([])

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 选中的角色ID
const selectedIds = ref<number[]>([])

// 弹窗显示状态
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')

// 角色表单
const roleFormRef = ref<FormInstance>()
const roleForm = reactive<CreateRoleRequest>({
  name: '',
  code: '',
  description: '',
  status: 1
})

// 角色表单验证规则
const roleFormRules: FormRules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 当前编辑的角色ID
const currentRoleId = ref<number>()

// 权限弹窗显示状态
const permissionDialogVisible = ref(false)

// 权限树数据
const permissionTree = ref<PermissionInfo[]>([])

// 权限树引用
const permissionTreeRef = ref()

// 树形结构配置
const treeProps = {
  children: 'children',
  label: 'permissionName'
}

// 当前分配权限的角色ID
const currentAssignRoleId = ref<number>()

// 所有权限的引用
const allPermissions = ref<PermissionInfo[]>([])

// 扁平化权限树
const flattenPermissions = (permissions: PermissionInfo[]): PermissionInfo[] => {
  const result: PermissionInfo[] = []
  const traverse = (nodes: PermissionInfo[]) => {
    nodes.forEach(node => {
      result.push(node)
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    })
  }
  traverse(permissions)
  return result
}

// 获取角色列表
const getRoleList = async () => {
  try {
    const response = await roleApi.getRoleList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      name: searchForm.name || undefined,
      code: searchForm.code || undefined,
      status: searchForm.status
    })
    if (response.code === 200) {
      roleList.value = response.data.records
      pagination.total = response.data.total
      
      // 先获取所有权限列表
      const allPermsResponse = await roleApi.getAllPermissions()
      if (allPermsResponse.code === 200) {
        allPermissions.value = allPermsResponse.data
      }
      
      // 扁平化权限树
      const flatPermissions = flattenPermissions(allPermissions.value)
      
      // 为每个角色获取权限信息
      await Promise.all(
        roleList.value.map(async (role) => {
          try {
            const permissionResponse = await roleApi.getRolePermissions(role.id!)
            if (permissionResponse.code === 200) {
              // 后端直接返回权限对象列表，无需再匹配
              role.permissions = permissionResponse.data || []
            }
          } catch (error) {
            console.error(`获取角色 ${role.id} 的权限失败:`, error)
            role.permissions = []
          }
        })
      )
    } else {
      ElMessage.error(response.message || '获取角色列表失败')
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getRoleList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.code = ''
  searchForm.status = undefined
  pagination.pageNum = 1
  getRoleList()
}

// 选择变化
const handleSelectionChange = (selection: RoleInfo[]) => {
  selectedIds.value = selection.map((item) => item.id!)
}

// 处理卡片checkbox选择变化
const handleCheckboxChange = (role: RoleInfo) => {
  const index = selectedIds.value.indexOf(role.id!)
  if (index > -1) {
    selectedIds.value.splice(index, 1)
  } else {
    selectedIds.value.push(role.id!)
  }
}

// 新增角色
const handleAdd = () => {
  dialogTitle.value = '新增角色'
  currentRoleId.value = undefined
  Object.assign(roleForm, {
    name: '',
    code: '',
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row: RoleInfo) => {
  dialogTitle.value = '编辑角色'
  currentRoleId.value = row.id
  Object.assign(roleForm, {
    name: row.name,
    code: row.code,
    description: row.description,
    status: row.status
  })
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
            id: currentRoleId.value,
            ...roleForm
          }
          const response = await roleApi.updateRole(updateData)
          if (response.code === 200) {
            ElMessage.success('更新角色成功')
            dialogVisible.value = false
            getRoleList()
          } else {
            ElMessage.error(response.message || '更新角色失败')
          }
        } else {
          // 创建角色
          const response = await roleApi.createRole(roleForm)
          if (response.code === 200) {
            ElMessage.success('创建角色成功')
            dialogVisible.value = false
            getRoleList()
          } else {
            ElMessage.error(response.message || '创建角色失败')
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

// 状态切换
const handleStatusChange = async (row: RoleInfo) => {
  try {
    const response = await roleApi.updateRoleStatus(row.id!, row.status)
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

// 删除角色
const handleDelete = (row: RoleInfo) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await roleApi.deleteRole(row.id!)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          getRoleList()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个角色吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await roleApi.batchDeleteRoles(selectedIds.value)
        if (response.code === 200) {
          ElMessage.success('批量删除成功')
          selectedIds.value = []
          getRoleList()
        } else {
          ElMessage.error(response.message || '批量删除失败')
        }
      } catch (error) {
        ElMessage.error('批量删除失败')
      }
    })
    .catch(() => {})
}

// 批量启用
const handleBatchEnable = () => {
  ElMessageBox.confirm(`确定要启用选中的 ${selectedIds.value.length} 个角色吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await roleApi.batchUpdateRoleStatus(selectedIds.value, 1)
        if (response.code === 200) {
          ElMessage.success('批量启用成功')
          selectedIds.value = []
          getRoleList()
        } else {
          ElMessage.error(response.message || '批量启用失败')
        }
      } catch (error) {
        ElMessage.error('批量启用失败')
      }
    })
    .catch(() => {})
}

// 批量禁用
const handleBatchDisable = () => {
  ElMessageBox.confirm(`确定要禁用选中的 ${selectedIds.value.length} 个角色吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await roleApi.batchUpdateRoleStatus(selectedIds.value, 0)
        if (response.code === 200) {
          ElMessage.success('批量禁用成功')
          selectedIds.value = []
          getRoleList()
        } else {
          ElMessage.error(response.message || '批量禁用失败')
        }
      } catch (error) {
        ElMessage.error('批量禁用失败')
      }
    })
    .catch(() => {})
}

// 分配权限
const handleAssignPermission = async (row: RoleInfo) => {
  currentAssignRoleId.value = row.id
  permissionDialogVisible.value = true

  try {
    // 获取所有权限树
    const treeResponse = await roleApi.getAllPermissions()
    if (treeResponse.code === 200) {
      permissionTree.value = treeResponse.data
    } else {
      ElMessage.error(treeResponse.message || '获取权限树失败')
      return
    }

    // 获取角色已有的权限
    const rolePermissionResponse = await roleApi.getRolePermissions(row.id!)
    if (rolePermissionResponse.code === 200) {
      // 后端返回的是权限对象列表，提取ID用于设置选中状态
      const permissionIds = rolePermissionResponse.data?.map(p => p.id) || []
      // 设置选中的权限
      permissionTreeRef.value?.setCheckedKeys(permissionIds)
    } else {
      ElMessage.error(rolePermissionResponse.message || '获取角色权限失败')
    }
  } catch (error) {
    ElMessage.error('获取权限数据失败')
  }
}

// 提交分配权限
const handleAssignPermissions = async () => {
  if (!currentAssignRoleId.value) return

  try {
    const checkedKeys = permissionTreeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = permissionTreeRef.value?.getHalfCheckedKeys() || []
    const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]

    const response = await roleApi.assignPermissions({
      roleId: currentAssignRoleId.value,
      permissionIds: allCheckedKeys as number[]
    })

    if (response.code === 200) {
      ElMessage.success('分配权限成功')
      permissionDialogVisible.value = false
      // 刷新角色列表以更新权限显示
      getRoleList()
    } else {
      ElMessage.error(response.message || '分配权限失败')
    }
  } catch (error) {
    ElMessage.error('分配权限失败')
  }
}

// 关闭权限弹窗
const handlePermissionDialogClose = () => {
  permissionTreeRef.value?.setCheckedKeys([])
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getRoleList()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  getRoleList()
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

.action-buttons {
  margin-bottom: 20px;
}

.permission-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.no-permissions {
  color: #909399;
  font-size: 13px;
}

.role-card {
  height: 100%;
  transition: all 0.3s;
}

.role-card:hover {
  transform: translateY(-2px);
}

.role-card-header {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.role-name {
  font-weight: 600;
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-card-body {
  padding: 10px 0;
}

.role-info {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  font-size: 13px;
}

.role-info:last-child {
  margin-bottom: 0;
}

.role-info .label {
  color: #909399;
  flex-shrink: 0;
  margin-right: 8px;
}

.role-info .value {
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-card-footer {
  display: flex;
  justify-content: center;
  gap: 8px;
}
</style>
