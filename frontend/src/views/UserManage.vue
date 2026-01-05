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
            <el-option label="正常" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
            <el-option label="锁定" value="LOCKED" />
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
              :type="row.status === 'ACTIVE' ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
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
            <el-option label="正常" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
            <el-option label="锁定" value="LOCKED" />
          </el-select>
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
import { getUserList, createUser, updateUser, deleteUser, batchDeleteUsers, updateUserStatus, batchUpdateStatus } from '@/api/user'
import type { UserInfo, UserListRequest, CreateUserRequest, UpdateUserRequest } from '@/api/user'

// 用户列表数据
const userList = ref<UserInfo[]>([])
const loading = ref(false)

// 搜索表单
const searchForm = reactive<UserListRequest>({
  username: '',
  name: '',
  status: ''
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
const userForm = reactive<CreateUserRequest & UpdateUserRequest & { confirmPassword?: string }>({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  email: '',
  phone: '',
  status: 'ACTIVE'
})

// 表单验证规则
const userRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
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
      trigger: 'blur'
    }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
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

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const response = await getUserList({
      page: pagination.page,
      size: pagination.size,
      username: searchForm.username || undefined,
      name: searchForm.name || undefined,
      status: searchForm.status || undefined
    })
    
    if (response.code === 200 && response.data) {
      userList.value = response.data.records || []
      pagination.total = response.data.total || 0
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
  searchForm.status = ''
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
const handleEdit = (row: UserInfo) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
  
  // 回显用户信息
  userForm.username = row.username
  userForm.name = row.name
  userForm.email = row.email
  userForm.phone = row.phone
  userForm.status = row.status
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
  const newStatus = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  const action = newStatus === 'ACTIVE' ? '启用' : '禁用'
  
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
        status: userForm.status
      }
      
      // 获取当前编辑的用户ID
      const currentUser = userList.value.find(u => u.username === userForm.username)
      if (!currentUser) {
        ElMessage.error('用户不存在')
        return
      }
      
      const response = await updateUser(currentUser.id, updateData)
      if (response.code === 200) {
        ElMessage.success('更新成功')
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
        name: userForm.name,
        email: userForm.email,
        phone: userForm.phone
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
  userForm.status = 'ACTIVE'
  userFormRef.value?.resetFields()
}

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    ACTIVE: 'success',
    INACTIVE: 'warning',
    LOCKED: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '正常',
    INACTIVE: '禁用',
    LOCKED: '锁定'
  }
  return textMap[status] || '未知'
}

// 页面加载时获取用户列表
onMounted(() => {
  fetchUserList()
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
</style>
