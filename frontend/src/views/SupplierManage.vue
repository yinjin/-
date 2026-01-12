<template>
  <div class="supplier-manage-container">
    <el-card>
      <!-- 搜索区域 -->
      <el-form
        :model="searchForm"
        :inline="true"
        class="search-form"
      >
        <el-form-item label="供应商名称">
          <el-input
            v-model="searchForm.supplierName"
            placeholder="请输入供应商名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="供应商编码">
          <el-input
            v-model="searchForm.supplierCode"
            placeholder="请输入供应商编码"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input
            v-model="searchForm.contactPerson"
            placeholder="请输入联系人"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入联系电话"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="信用等级">
          <el-select
            v-model="searchForm.creditRating"
            placeholder="请选择信用等级"
            clearable
            @clear="handleSearch"
          >
            <el-option
              label="10（优秀）"
              :value="10"
            />
            <el-option
              label="9（良好）"
              :value="9"
            />
            <el-option
              label="8（较好）"
              :value="8"
            />
            <el-option
              label="7（一般）"
              :value="7"
            />
            <el-option
              label="6（及格）"
              :value="6"
            />
            <el-option
              label="5（较差）"
              :value="5"
            />
            <el-option
              label="4（差）"
              :value="4"
            />
            <el-option
              label="3（很差）"
              :value="3"
            />
            <el-option
              label="2（极差）"
              :value="2"
            />
            <el-option
              label="1（不合格）"
              :value="1"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="合作状态">
          <el-select
            v-model="searchForm.cooperationStatus"
            placeholder="请选择合作状态"
            clearable
            @clear="handleSearch"
          >
            <el-option
              label="合作中"
              :value="1"
            />
            <el-option
              label="已终止"
              :value="0"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            @clear="handleSearch"
          >
            <el-option
              label="启用"
              :value="1"
            />
            <el-option
              label="禁用"
              :value="0"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleSearch"
          >
            搜索
          </el-button>
          <el-button @click="handleReset">
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮区域 -->
      <div class="action-buttons">
        <el-button
          type="primary"
          @click="handleAdd"
        >
          <el-icon><Plus /></el-icon>
          新增供应商
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

      <!-- 供应商列表表格 -->
      <el-table
        v-loading="loading"
        :data="supplierList"
        border
        stripe
        style="width: 100%; margin-top: 20px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="55"
        />
        <el-table-column
          prop="id"
          label="ID"
          width="80"
        />
        <el-table-column
          prop="supplierCode"
          label="供应商编码"
          width="150"
        />
        <el-table-column
          prop="supplierName"
          label="供应商名称"
          width="180"
        />
        <el-table-column
          prop="contactPerson"
          label="联系人"
          width="120"
        />
        <el-table-column
          prop="phone"
          label="联系电话"
          width="130"
        />
        <el-table-column
          prop="email"
          label="邮箱"
          width="180"
        />
        <el-table-column
          prop="creditRating"
          label="信用等级"
          width="120"
        >
          <template #default="{ row }">
            <el-rate
              v-model="row.creditRating"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}分"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="cooperationStatus"
          label="合作状态"
          width="100"
        >
          <template #default="{ row }">
            <el-tag :type="getCooperationStatusType(row.cooperationStatus)">
              {{ getCooperationStatusText(row.cooperationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态"
          width="100"
        >
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
        />
        <el-table-column
          label="操作"
          width="300"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button
              type="info"
              size="small"
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
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
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑供应商弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="supplierFormRef"
        :model="supplierForm"
        :rules="supplierRules"
        label-width="120px"
      >
        <el-form-item
          label="供应商名称"
          prop="supplierName"
        >
          <el-input
            v-model="supplierForm.supplierName"
            placeholder="请输入供应商名称"
          />
        </el-form-item>
        <el-form-item
          label="供应商编码"
          prop="supplierCode"
        >
          <el-input
            v-model="supplierForm.supplierCode"
            placeholder="请输入供应商编码（不填则自动生成）"
            :disabled="isEdit"
          >
            <template #append>
              <el-button
                :disabled="isEdit"
                @click="handleGenerateCode"
              >
                生成编码
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item
          label="联系人"
          prop="contactPerson"
        >
          <el-input
            v-model="supplierForm.contactPerson"
            placeholder="请输入联系人"
          />
        </el-form-item>
        <el-form-item
          label="联系电话"
          prop="phone"
        >
          <el-input
            v-model="supplierForm.phone"
            placeholder="请输入联系电话"
          />
        </el-form-item>
        <el-form-item
          label="邮箱"
          prop="email"
        >
          <el-input
            v-model="supplierForm.email"
            placeholder="请输入邮箱"
          />
        </el-form-item>
        <el-form-item
          label="地址"
          prop="address"
        >
          <el-input
            v-model="supplierForm.address"
            placeholder="请输入地址"
          />
        </el-form-item>
        <el-form-item
          label="营业执照号"
          prop="businessLicense"
        >
          <el-input
            v-model="supplierForm.businessLicense"
            placeholder="请输入营业执照号"
          />
        </el-form-item>
        <el-form-item label="营业执照文件">
          <el-upload
            ref="businessLicenseUploadRef"
            class="upload-demo"
            :action="uploadUrl"
            :headers="{ Authorization: `Bearer ${localStorage.getItem('token')}` }"
            :on-success="handleBusinessLicenseUploadSuccess"
            :on-error="handleUploadError"
            :on-preview="handleFilePreview"
            :on-remove="handleBusinessLicenseRemove"
            :file-list="businessLicenseFileList"
            :before-upload="beforeUpload"
            :on-change="() => uploadLoading = true"
            :auto-upload="true"
            accept=".jpg,.jpeg,.png,.pdf,.doc,.docx"
          >
            <el-button
              type="primary"
              :loading="uploadLoading"
            >
              <el-icon><Upload /></el-icon>
              上传营业执照
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                请上传jpg/png/pdf/doc/docx格式文件，大小不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="资质文件">
          <el-upload
            ref="qualificationUploadRef"
            class="upload-demo"
            :action="uploadUrl"
            :headers="{ Authorization: `Bearer ${localStorage.getItem('token')}` }"
            :on-success="handleQualificationUploadSuccess"
            :on-error="handleUploadError"
            :on-preview="handleFilePreview"
            :on-remove="handleQualificationRemove"
            :file-list="qualificationFileList"
            :before-upload="beforeUpload"
            :on-change="() => uploadLoading = true"
            :auto-upload="true"
            multiple
            accept=".jpg,.jpeg,.png,.pdf,.doc,.docx"
          >
            <el-button
              type="primary"
              :loading="uploadLoading"
            >
              <el-icon><Upload /></el-icon>
              上传资质文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                请上传jpg/png/pdf/doc/docx格式文件，大小不超过10MB，支持多文件上传
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item
          label="税号"
          prop="taxNumber"
        >
          <el-input
            v-model="supplierForm.taxNumber"
            placeholder="请输入税号"
          />
        </el-form-item>
        <el-form-item
          label="银行账号"
          prop="bankAccount"
        >
          <el-input
            v-model="supplierForm.bankAccount"
            placeholder="请输入银行账号"
          />
        </el-form-item>
        <el-form-item
          label="开户行"
          prop="bankName"
        >
          <el-input
            v-model="supplierForm.bankName"
            placeholder="请输入开户行"
          />
        </el-form-item>
        <el-form-item
          label="信用等级"
          prop="creditRating"
        >
          <el-rate
            v-model="supplierForm.creditRating"
            show-score
            text-color="#ff9900"
            score-template="{value}分"
          />
        </el-form-item>
        <el-form-item
          label="合作状态"
          prop="cooperationStatus"
        >
          <el-select
            v-model="supplierForm.cooperationStatus"
            placeholder="请选择合作状态"
            style="width: 100%"
          >
            <el-option
              label="合作中"
              :value="1"
            />
            <el-option
              label="已终止"
              :value="0"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="isEdit"
          label="状态"
          prop="status"
        >
          <el-select
            v-model="supplierForm.status"
            placeholder="请选择状态"
            style="width: 100%"
          >
            <el-option
              label="启用"
              :value="1"
            />
            <el-option
              label="禁用"
              :value="0"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="供应商描述"
          prop="description"
        >
          <el-input
            v-model="supplierForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入供应商描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">
          取消
        </el-button>
        <el-button
          type="primary"
          :loading="submitLoading"
          @click="handleSubmit"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadInstance } from 'element-plus'
import { Plus, Delete, Upload } from '@element-plus/icons-vue'
import {
  getSupplierPage,
  createSupplier,
  updateSupplier,
  deleteSupplier,
  batchDeleteSuppliers,
  toggleStatus,
  generateSupplierCode
} from '@/api/supplier'
import type {
  SupplierInfo,
  SupplierCreateRequest,
  SupplierUpdateRequest,
  SupplierQueryRequest
} from '@/api/supplier'

// 路由实例
const router = useRouter()

// 供应商列表数据
const supplierList = ref<SupplierInfo[]>([])
const loading = ref(false)

// 搜索表单
const searchForm = reactive<SupplierQueryRequest>({
  supplierName: '',
  supplierCode: '',
  contactPerson: '',
  phone: '',
  creditRating: undefined,
  cooperationStatus: undefined,
  status: undefined
})

// 分页参数
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 选中的供应商ID
const selectedIds = ref<number[]>([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增供应商')
const isEdit = ref(false)
const submitLoading = ref(false)

// 供应商表单
const supplierFormRef = ref<FormInstance>()
const supplierForm = reactive<SupplierCreateRequest & SupplierUpdateRequest>({
  supplierName: '',
  supplierCode: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: '',
  businessLicense: '',
  taxNumber: '',
  bankAccount: '',
  bankName: '',
  creditRating: 5,
  cooperationStatus: 1,
  status: 1,
  description: ''
})

// 文件上传相关
const uploadUrl = '/api/files/upload' // 文件上传API地址
const businessLicenseUploadRef = ref<UploadInstance>()
const qualificationUploadRef = ref<UploadInstance>()

// 文件列表
const businessLicenseFileList = ref<Array<{
  name: string
  url: string
}>>([])
const qualificationFileList = ref<Array<{
  name: string
  url: string
}>>([])

// 上传文件ID列表，用于保存到数据库
const uploadedBusinessLicenseIds = ref<Array<string>>([])
const uploadedQualificationIds = ref<Array<string>>([])

// 文件上传加载状态
const uploadLoading = ref(false)

// 表单验证规则
const supplierRules: FormRules = {
  supplierName: [
    { required: true, message: '请输入供应商名称', trigger: 'blur' },
    { max: 100, message: '供应商名称最大长度为100个字符', trigger: 'blur' }
  ],
  supplierCode: [
    { max: 50, message: '供应商编码最大长度为50个字符', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' },
    { max: 50, message: '联系人最大长度为50个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { max: 20, message: '联系电话最大长度为20个字符', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码格式', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { max: 100, message: '邮箱最大长度为100个字符', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' },
    { max: 200, message: '地址最大长度为200个字符', trigger: 'blur' }
  ],
  businessLicense: [
    { required: true, message: '请输入营业执照号', trigger: 'blur' },
    { max: 200, message: '营业执照最大长度为200个字符', trigger: 'blur' }
  ],
  taxNumber: [
    { required: true, message: '请输入税号', trigger: 'blur' },
    { max: 50, message: '税号最大长度为50个字符', trigger: 'blur' }
  ],
  bankAccount: [
    { required: true, message: '请输入银行账号', trigger: 'blur' },
    { max: 100, message: '银行账号最大长度为100个字符', trigger: 'blur' },
    { pattern: /^[0-9]+$/, message: '请输入正确的银行账号格式（仅数字）', trigger: 'blur' }
  ],
  bankName: [
    { required: true, message: '请输入开户行', trigger: 'blur' },
    { max: 100, message: '开户行最大长度为100个字符', trigger: 'blur' }
  ],
  creditRating: [
    { required: true, message: '请选择信用等级', trigger: 'change' }
  ],
  cooperationStatus: [
    { required: true, message: '请选择合作状态', trigger: 'change' }
  ]
}

// 获取供应商列表
const fetchSupplierList = async () => {
  loading.value = true
  try {
    const response = await getSupplierPage({
      current: pagination.page,
      size: pagination.size,
      supplierName: searchForm.supplierName || undefined,
      supplierCode: searchForm.supplierCode || undefined,
      contactPerson: searchForm.contactPerson || undefined,
      phone: searchForm.phone || undefined,
      creditRatingMin: searchForm.creditRating,
      creditRatingMax: searchForm.creditRating,
      cooperationStatus: searchForm.cooperationStatus || undefined,
      status: searchForm.status || undefined,
      orderBy: 'createTime',
      orderDirection: 'desc'
    })
    
    if (response.code === 200 && response.data) {
      supplierList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取供应商列表失败')
    }
  } catch (error) {
    ElMessage.error((error as Error).message || '获取供应商列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchSupplierList()
}

// 重置
const handleReset = () => {
  searchForm.supplierName = ''
  searchForm.supplierCode = ''
  searchForm.contactPerson = ''
  searchForm.phone = ''
  searchForm.creditRating = undefined
  searchForm.cooperationStatus = undefined
  searchForm.status = undefined
  pagination.page = 1
  fetchSupplierList()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  fetchSupplierList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.page = page
  fetchSupplierList()
}

// 选择改变
const handleSelectionChange = (selection: SupplierInfo[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增供应商
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增供应商'
  dialogVisible.value = true
  resetSupplierForm()
}

// 编辑供应商
const handleEdit = (row: SupplierInfo) => {
  isEdit.value = true
  dialogTitle.value = '编辑供应商'
  dialogVisible.value = true
  
  // 回显供应商信息
  supplierForm.id = row.id
  supplierForm.supplierName = row.supplierName
  supplierForm.supplierCode = row.supplierCode
  supplierForm.contactPerson = row.contactPerson
  supplierForm.phone = row.phone
  supplierForm.email = row.email
  supplierForm.address = row.address
  supplierForm.businessLicense = row.businessLicense
  supplierForm.taxNumber = row.taxNumber
  supplierForm.bankAccount = row.bankAccount
  supplierForm.bankName = row.bankName
  supplierForm.creditRating = row.creditRating
  supplierForm.cooperationStatus = row.cooperationStatus
  supplierForm.status = row.status
  supplierForm.description = row.description
  
  // TODO: 回显已上传的文件信息（需要从后端获取）
  // businessLicenseFileList.value = [...]
  // qualificationFileList.value = [...]
}

// 删除供应商
const handleDelete = async (row: SupplierInfo) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除供应商 "${row.supplierName}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteSupplier(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      fetchSupplierList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error((error as Error).message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 个供应商吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await batchDeleteSuppliers(selectedIds.value)
    if (response.code === 200) {
      ElMessage.success(`成功删除 ${selectedIds.value.length} 个供应商`)
      selectedIds.value = []
      fetchSupplierList()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error((error as Error).message || '批量删除失败')
    }
  }
}

// 切换供应商状态
const handleToggleStatus = async (row: SupplierInfo) => {
  const action = row.status === 1 ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}供应商 "${row.supplierName}" 吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await toggleStatus(row.id)
    if (response.code === 200) {
      ElMessage.success(`${action}成功`)
      fetchSupplierList()
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error((error as Error).message || `${action}失败`)
    }
  }
}

// 生成供应商编码
const handleGenerateCode = async () => {
  try {
    const response = await generateSupplierCode()
    if (response.code === 200 && response.data) {
      supplierForm.supplierCode = response.data
      ElMessage.success('供应商编码生成成功')
    } else {
      ElMessage.error(response.message || '生成供应商编码失败')
    }
  } catch (error) {
    ElMessage.error((error as Error).message || '生成供应商编码失败')
  }
}

// 查看供应商详情
const handleViewDetail = (row: SupplierInfo) => {
  router.push(`/suppliers/${row.id}`)
}

// 提交表单
const handleSubmit = async () => {
  if (!supplierFormRef.value) return
  
  try {
    await supplierFormRef.value.validate()
    
    submitLoading.value = true
    
    // 供应商数据
    const supplierData = {
      supplierName: supplierForm.supplierName,
      supplierCode: supplierForm.supplierCode,
      contactPerson: supplierForm.contactPerson,
      phone: supplierForm.phone,
      email: supplierForm.email,
      address: supplierForm.address,
      businessLicense: supplierForm.businessLicense,
      taxNumber: supplierForm.taxNumber,
      bankAccount: supplierForm.bankAccount,
      bankName: supplierForm.bankName,
      creditRating: supplierForm.creditRating,
      cooperationStatus: supplierForm.cooperationStatus,
      status: supplierForm.status,
      description: supplierForm.description,
      // 添加上传的文件信息
      businessLicenseUrl: businessLicenseFileList.value.length > 0 ? businessLicenseFileList.value[0].url : undefined,
      qualificationFiles: qualificationFileList.value.map(file => file.url)
    }
    
    let response
    if (isEdit.value) {
      // 编辑供应商
      const updateData: SupplierUpdateRequest = {
        ...supplierData,
        id: supplierForm.id!
      }
      
      response = await updateSupplier(supplierForm.id!, updateData)
    } else {
      // 新增供应商
      const createData: SupplierCreateRequest = supplierData
      
      response = await createSupplier(createData)
    }
    
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
      dialogVisible.value = false
      fetchSupplierList()
    } else {
      ElMessage.error(response.message || (isEdit.value ? '更新失败' : '新增失败'))
    }
  } catch (error) {
    if ((error as Error).message) {
      ElMessage.error((error as Error).message)
    }
  } finally {
    submitLoading.value = false
  }
}

// 关闭弹窗
const handleDialogClose = () => {
  resetSupplierForm()
}

// 文件上传前验证
const beforeUpload = (file: File) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('上传文件大小不能超过 10MB')
  }
  return isLt10M || false
}

// 营业执照上传成功回调
const handleBusinessLicenseUploadSuccess = (response: { code: number; data: string; message?: string }, uploadFile: { name: string }) => {
  uploadLoading.value = false
  if (response.code === 200 && response.data) {
    businessLicenseFileList.value.push({
      name: uploadFile.name,
      url: response.data
    })
    uploadedBusinessLicenseIds.value.push(response.data)
    ElMessage.success('营业执照文件上传成功')
  } else {
    ElMessage.error(response.message || '营业执照文件上传失败')
  }
}

// 资质文件上传成功回调
const handleQualificationUploadSuccess = (response: { code: number; data: string; message?: string }, uploadFile: { name: string }) => {
  uploadLoading.value = false
  if (response.code === 200 && response.data) {
    qualificationFileList.value.push({
      name: uploadFile.name,
      url: response.data
    })
    uploadedQualificationIds.value.push(response.data)
    ElMessage.success('资质文件上传成功')
  } else {
    ElMessage.error(response.message || '资质文件上传失败')
  }
}

// 上传失败回调
const handleUploadError = () => {
  uploadLoading.value = false
  ElMessage.error('文件上传失败')
}

// 文件预览
const handleFilePreview = (file: { url: string }) => {
  // 对于图片，直接预览
  if (file.url) {
    // 如果是图片，直接在浏览器中打开预览
    if (file.url.match(/\.(jpg|jpeg|png|gif)$/)) {
      window.open(file.url, '_blank')
    } else {
      // 对于其他类型文件，提示下载
      ElMessage.info('该文件类型不支持预览，请下载查看')
      window.open(file.url, '_blank')
    }
  }
}

// 删除营业执照文件
const handleBusinessLicenseRemove = (_file: { url: string }, fileList: Array<{ name: string; url: string }>) => {
  // 更新文件列表
  businessLicenseFileList.value = fileList
  
  // 从已上传文件ID列表中移除
  const index = uploadedBusinessLicenseIds.value.indexOf(_file.url)
  if (index > -1) {
    uploadedBusinessLicenseIds.value.splice(index, 1)
  }
}

// 删除资质文件
const handleQualificationRemove = (_file: { url: string }, fileList: Array<{ name: string; url: string }>) => {
  // 更新文件列表
  qualificationFileList.value = fileList
  
  // 从已上传文件ID列表中移除
  const index = uploadedQualificationIds.value.indexOf(_file.url)
  if (index > -1) {
    uploadedQualificationIds.value.splice(index, 1)
  }
}

// 重置供应商表单
const resetSupplierForm = () => {
  supplierForm.supplierName = ''
  supplierForm.supplierCode = ''
  supplierForm.contactPerson = ''
  supplierForm.phone = ''
  supplierForm.email = ''
  supplierForm.address = ''
  supplierForm.businessLicense = ''
  supplierForm.taxNumber = ''
  supplierForm.bankAccount = ''
  supplierForm.bankName = ''
  supplierForm.creditRating = 5
  supplierForm.cooperationStatus = 1
  supplierForm.status = 1
  supplierForm.description = ''
  
  // 重置文件上传相关
  businessLicenseFileList.value = []
  qualificationFileList.value = []
  uploadedBusinessLicenseIds.value = []
  uploadedQualificationIds.value = []
  
  supplierFormRef.value?.resetFields()
}

// 获取状态类型
const getStatusType = (status: number): 'success' | 'danger' | 'info' => {
  const typeMap: Record<number, 'success' | 'danger' | 'info'> = {
    0: 'danger',
    1: 'success'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const textMap: Record<number, string> = {
    0: '禁用',
    1: '启用'
  }
  return textMap[status] || '未知'
}

// 获取合作状态类型
const getCooperationStatusType = (status: number): 'success' | 'danger' | 'info' => {
  const typeMap: Record<number, 'success' | 'danger' | 'info'> = {
    0: 'danger',
    1: 'success'
  }
  return typeMap[status] || 'info'
}

// 获取合作状态文本
const getCooperationStatusText = (status: number) => {
  const textMap: Record<number, string> = {
    0: '已终止',
    1: '合作中'
  }
  return textMap[status] || '未知'
}

// 页面加载时获取供应商列表
onMounted(() => {
  fetchSupplierList()
})
</script>

<style scoped>
.supplier-manage-container {
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
