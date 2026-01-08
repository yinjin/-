<template>
  <div class="user-manage-container">
    <el-card>
      <!-- 搜索区域 -->
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入姓名"
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
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
            <el-option label="锁定" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <DepartmentSelect
            v-model="searchForm.departmentId"
            placeholder="请选择部门"
            clearable
            @clear="handleSearch"
          />
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
          新增用户
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>

      <!-- 用户列表表格 -->
      <el-table
        :data="userList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
        stripe
        style="width: 100%; margin-top: 20px"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="roles" label="角色" width="200">
          <template #default="{ row }">
            <div v-if="row.roles && row.roles.length > 0">
              <el-tag
                v-for="role in row.roles"
                :key="role.id"
                type="success"
                size="small"
                style="margin-right: 5px; margin-bottom: 5px"
              >
                {{ role.name || '未知角色' }}
              </el-tag>
            </div>
            <span v-else style="color: #999">无角色</span>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="部门" width="150">
          <template #default="{ row }">
            <span v-if="row.department">{{ row.department.name }}</span>
            <span v-else style="color: #999">未分配</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              :type="row.status === 0 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 0 ? '禁用' : '启用' }}
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
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
    </el-card>

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
          <div class="password-tip">密码必须包含大写字母、小写字母、数字和特殊字符（$ @ ! % * ? &）</div>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!isEdit">
          <el-input
            v-model="userForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="userForm.name"
            placeholder="请输入姓名"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="userForm.email"
            placeholder="请输入邮箱"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="userForm.phone"
            placeholder="请输入手机号"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="isEdit">
          <el-select v-model="userForm.status" placeholder="请选择状态">
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
            <el-option label="锁定" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="roleId" v-if="isEdit">
          <el-select
            v-model="userForm.roleId"
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in allRoles"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            />
            <div v-if="allRoles.length === 0" style="padding: 10px; color: #999">
              暂无角色数据
            </div>
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="departmentId">
          <DepartmentSelect
            v-model="userForm.departmentId"
            placeholder="请选择部门"
            clearable
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { getUserList, createUser, updateUser, deleteUser, batchDeleteUsers, updateUserStatus, batchUpdateStatus, getUserRoles, assignRoles, removeUserRole } from '@/api/user'
import { roleApi } from '@/api/role'
import type { UserInfo, UserListRequest, CreateUserRequest, UpdateUserRequest } from '@/api/user'
import type { RoleInfo } from '@/api/role'
import DepartmentSelect from '@/components/DepartmentSelect.vue'

// 用户列表数据
const userList = ref<UserInfo[]>([])
const loading = ref(false)

// 所有角色列表
const allRoles = ref<RoleInfo[]>([])

// 搜索表单
const searchForm = reactive<UserListRequest>({
  username: '',
  name: '',
  status: undefined
})

// 分页参数
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 选中的用户ID
const selectedIds = ref<number[]>([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const isEdit = ref(false)
const submitLoading = ref(false)

// 用户表单
const userFormRef = ref<FormInstance>()
const userForm = reactive<CreateUserRequest & UpdateUserRequest & { confirmPassword?: string; roleId?: number }>({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  email: '',
  phone: '',
  agreeToTerms: true,
  status: 0,
  roleId: undefined,
  departmentId: undefined
})

// 表单验证规则
const userRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_]+$/, message: '用户名只能包含中文、字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/, 
      message: '密码必须包含至少一个大写字母、小写字母、数字和特殊字符', 
      trigger: 'blur' 
    },
    { min: 8, max: 20, message: '密码长度必须在 8 到 20 个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== userForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 50, message: '姓名长度不能超过 50 个字符', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_\s]+$/, message: '姓名只能包含中文、字母、数字、下划线和空格', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
}

// 获取所有角色列表
// 遵循：API规范-响应数据处理
// axios拦截器已返回response.data，所以response直接是ApiResponse<PageResponse<RoleInfo>>
const fetchRoleList = async () => {
  try {
    const response = await roleApi.getRoleList({ pageNum: 1, pageSize: 100 })
    console.log('角色列表响应:', response)
    if (response.code === 200 && response.data) {
      allRoles.value = response.data.records || []
      console.log('角色列表数据:', allRoles.value)
    } else {
      console.error('获取角色列表失败:', response.message)
    }
  } catch (error: any) {
    console.error('获取角色列表失败:', error)
  }
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const response = await getUserList({
      page: pagination.page,
      size: pagination.size,
      username: searchForm.username || undefined,
      name: searchForm.name || undefined,
      status: searchForm.status || undefined,
      departmentId: searchForm.departmentId || undefined  // 遵循：前端交互规范-数据查询
    })
    
    if (response.code === 200 && response.data) {
      userList.value = response.data.records || []
      pagination.total = response.data.total || 0
      
      // 为每个用户获取角色信息（并行获取以提高性能）
      const rolePromises = userList.value.map(async (user) => {
        try {
          const rolesResponse = await getUserRoles(user.id)
          console.log(`用户 ${user.username} 的角色响应:`, rolesResponse)
          return {
            userId: user.id,
            roles: rolesResponse.code === 200 && rolesResponse.data ? rolesResponse.data : []
          }
        } catch (error) {
          console.error(`获取用户 ${user.username} 的角色失败:`, error)
          return {
            userId: user.id,
            roles: []
          }
        }
      })

      const roleResults = await Promise.all(rolePromises)
      console.log('所有角色结果:', roleResults)

      // 更新用户列表中的角色信息
      roleResults.forEach(result => {
        const userIndex = userList.value.findIndex(user => user.id === result.userId)
        if (userIndex !== -1) {
          // 使用Vue的响应式更新
          userList.value[userIndex].roles = result.roles
        }
      })
    } else {
      ElMessage.error(response.message || '获取用户列表失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchUserList()
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.name = ''
  searchForm.status = undefined
  searchForm.departmentId = undefined  // 遵循：前端交互规范-搜索重置
  pagination.page = 1
  fetchUserList()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  fetchUserList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.page = page
  fetchUserList()
}

// 选择改变
const handleSelectionChange = (selection: UserInfo[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增用户
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
  resetUserForm()
}

// 编辑用户
const handleEdit = async (row: UserInfo) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
  
  // 回显用户信息
  userForm.username = row.username
  userForm.name = row.name
  userForm.email = row.email
  userForm.phone = row.phone
  userForm.status = row.status
  userForm.departmentId = row.department?.id  // 遵循：前端交互规范-数据回显
  
  // 加载用户的角色
  try {
    const response = await getUserRoles(row.id)
    if (response.code === 200 && response.data) {
      userForm.roleId = response.data[0]?.id || undefined
    } else {
      userForm.roleId = undefined
    }
  } catch (error: any) {
    console.error('获取用户角色失败:', error)
    userForm.roleId = undefined
  }
}

// 删除用户
const handleDelete = async (row: UserInfo) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.username}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteUser(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      fetchUserList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 个用户吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await batchDeleteUsers(selectedIds.value)
    if (response.code === 200) {
      ElMessage.success(`成功删除 ${selectedIds.value.length} 个用户`)
      selectedIds.value = []
      fetchUserList()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 切换用户状态
const handleToggleStatus = async (row: UserInfo) => {
  const newStatus = row.status === 0 ? 1 : 0
  const action = newStatus === 0 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${row.username}" 吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await updateUserStatus(row.id, newStatus)
    if (response.code === 200) {
      ElMessage.success(`${action}成功`)
      fetchUserList()
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || `${action}失败`)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    
    submitLoading.value = true
    
    if (isEdit.value) {
      // 编辑用户
      const updateData: UpdateUserRequest = {
        name: userForm.name,
        email: userForm.email,
        phone: userForm.phone,
        status: userForm.status,
        departmentId: userForm.departmentId  // 遵循：前端交互规范-数据提交
      }
      
      // 获取当前编辑的用户ID
      const currentUser = userList.value.find(u => u.username === userForm.username)
      if (!currentUser) {
        ElMessage.error('用户不存在')
        return
      }
      
      const response = await updateUser(currentUser.id, updateData)
      if (response.code === 200) {
        // 更新角色
        let roleUpdateSuccess = true
        let roleErrorMessage = ''
        try {
          const currentRolesResponse = await getUserRoles(currentUser.id)
          if (currentRolesResponse.code === 200 && currentRolesResponse.data) {
            const currentRoleId = currentRolesResponse.data[0]?.id
            if (currentRoleId !== userForm.roleId) {
              // 移除旧角色
              if (currentRoleId) {
                const removeResponse = await removeUserRole(currentUser.id, currentRoleId)
                if (removeResponse.code !== 200) {
                  roleUpdateSuccess = false
                  roleErrorMessage = removeResponse.message || '移除角色失败'
                  console.error('移除角色失败:', roleErrorMessage)
                } else {
                  // 只有移除成功，才添加新角色
                  if (userForm.roleId) {
                    const assignResponse = await assignRoles(currentUser.id, [userForm.roleId])
                    if (assignResponse.code !== 200) {
                      roleUpdateSuccess = false
                      roleErrorMessage = assignResponse.message || '分配角色失败'
                      console.error('分配角色失败:', roleErrorMessage)
                    }
                  }
                }
              } else {
                // 没有旧角色，直接添加新角色
                if (userForm.roleId) {
                  const assignResponse = await assignRoles(currentUser.id, [userForm.roleId])
                  if (assignResponse.code !== 200) {
                    roleUpdateSuccess = false
                    roleErrorMessage = assignResponse.message || '分配角色失败'
                    console.error('分配角色失败:', roleErrorMessage)
                  }
                }
              }
            }
          } else {
            roleUpdateSuccess = false
            roleErrorMessage = '获取当前角色失败'
          }
        } catch (error: any) {
          roleUpdateSuccess = false
          roleErrorMessage = error.message || '更新角色失败'
          console.error('更新角色失败:', error)
        }
        
        if (roleUpdateSuccess) {
          ElMessage.success('更新成功')
        } else {
          ElMessage.warning('用户信息更新成功，但角色更新失败: ' + roleErrorMessage)
        }
        dialogVisible.value = false
        fetchUserList()
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    } else {
      // 新增用户
      const createData: CreateUserRequest = {
        username: userForm.username,
        password: userForm.password,
        confirmPassword: userForm.confirmPassword,
        name: userForm.name,
        email: userForm.email,
        phone: userForm.phone,
        agreeToTerms: true
      }
      
      const response = await createUser(createData)
      if (response.code === 200) {
        ElMessage.success('新增成功')
        dialogVisible.value = false
        fetchUserList()
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

// 关闭弹窗
const handleDialogClose = () => {
  resetUserForm()
}

// 重置用户表单
const resetUserForm = () => {
  userForm.username = ''
  userForm.password = ''
  userForm.confirmPassword = ''
  userForm.name = ''
  userForm.email = ''
  userForm.phone = ''
  userForm.status = 0
  userForm.roleId = undefined
  userForm.departmentId = undefined  // 遵循：前端交互规范-表单重置
  userFormRef.value?.resetFields()
}

// 获取状态类型
const getStatusType = (status: number) => {
  const typeMap: Record<number, any> = {
    0: 'success',
    1: 'warning',
    2: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const textMap: Record<number, string> = {
    0: '正常',
    1: '禁用',
    2: '锁定'
  }
  return textMap[status] || '未知'
}

// 页面加载时获取用户列表和角色列表
onMounted(() => {
  fetchUserList()
  fetchRoleList()
})
</script>

<style scoped>
.user-manage-container {
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

.password-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
  margin-top: 4px;
}
</style>
