<template>
  <div class="material-manage">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="耗材名称">
          <el-input v-model="searchForm.materialName" placeholder="请输入耗材名称" clearable />
        </el-form-item>
        <el-form-item label="耗材编码">
          <el-input v-model="searchForm.materialCode" placeholder="请输入耗材编码" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <CategorySelect v-model="searchForm.categoryId" placeholder="请选择分类" clearable />
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
        <el-button type="primary" @click="handleAdd">新增耗材</el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          批量删除
        </el-button>
      </div>

      <!-- 耗材列表 -->
      <el-table
        :data="materialList"
        style="width: 100%; margin-top: 20px"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="materialName" label="耗材名称" min-width="150" />
        <el-table-column prop="materialCode" label="耗材编码" width="120" />
        <el-table-column prop="categoryName" label="分类" width="150" />
        <el-table-column prop="specification" label="规格型号" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="manufacturer" label="制造商" width="150" />
        <el-table-column prop="minStock" label="最小库存" width="100" align="right" />
        <el-table-column prop="maxStock" label="最大库存" width="100" align="right" />
        <el-table-column prop="safetyStock" label="安全库存" width="100" align="right" />
        <el-table-column prop="price" label="单价" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.price?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑耗材弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form ref="materialFormRef" :model="materialForm" :rules="materialFormRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="耗材名称" prop="materialName">
              <el-input v-model="materialForm.materialName" placeholder="请输入耗材名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="耗材编码" prop="materialCode">
              <el-input
                v-model="materialForm.materialCode"
                placeholder="请输入耗材编码"
                @blur="handleCodeBlur"
              />
              <div v-if="codeExists" class="error-tip">该耗材编码已存在</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <CategorySelect v-model="materialForm.categoryId" placeholder="请选择分类" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="specification">
              <el-input v-model="materialForm.specification" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="materialForm.unit" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="materialForm.brand" placeholder="请输入品牌" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="制造商" prop="manufacturer">
              <el-input v-model="materialForm.manufacturer" placeholder="请输入制造商" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价" prop="price">
              <el-input-number
                v-model="materialForm.price"
                :min="0"
                :precision="2"
                :step="0.01"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="最小库存" prop="minStock">
              <el-input-number
                v-model="materialForm.minStock"
                :min="0"
                :max="999999"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大库存" prop="maxStock">
              <el-input-number
                v-model="materialForm.maxStock"
                :min="0"
                :max="999999"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="安全库存" prop="safetyStock">
              <el-input-number
                v-model="materialForm.safetyStock"
                :min="0"
                :max="999999"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="耗材描述" prop="description">
          <el-input
            v-model="materialForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入耗材描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="materialForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import CategorySelect from '@/components/CategorySelect.vue'
import materialApi from '@/api/material'
import type {
  Material,
  MaterialCreateRequest,
  MaterialUpdateRequest,
  MaterialQueryParams
} from '@/types/material'

// 搜索表单
const searchForm = reactive<MaterialQueryParams>({
  materialName: '',
  materialCode: '',
  categoryId: undefined,
  status: undefined,
  current: 1,
  size: 10
})

// 耗材列表数据
const materialList = ref<Material[]>([])

// 加载状态
const loading = ref(false)

// 分页信息
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 选中的耗材ID
const selectedIds = ref<number[]>([])

// 弹窗显示状态
const dialogVisible = ref(false)
const dialogTitle = ref('新增耗材')

// 耗材表单
const materialFormRef = ref<FormInstance>()
const materialForm = reactive<MaterialCreateRequest & { id?: number; status?: number }>({
  materialName: '',
  materialCode: '',
  categoryId: 0,
  specification: '',
  unit: '',
  brand: '',
  manufacturer: '',
  minStock: 0,
  maxStock: 0,
  safetyStock: 0,
  price: 0,
  description: '',
  status: 1
})

// 耗材表单验证规则
const materialFormRules: FormRules = {
  materialName: [{ required: true, message: '请输入耗材名称', trigger: 'blur' }],
  materialCode: [{ required: true, message: '请输入耗材编码', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 当前编辑的耗材ID
const currentMaterialId = ref<number>()

// 提交加载状态
const submitLoading = ref(false)

// 编码是否存在
const codeExists = ref(false)

// 防抖定时器
let codeCheckTimer: number | null = null

// 获取耗材列表
const getMaterialList = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      current: pagination.current,
      size: pagination.size
    }
    const response = await materialApi.getMaterialPage(params)
    if (response.code === 200) {
      materialList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取耗材列表失败')
    }
  } catch (error) {
    ElMessage.error('获取耗材列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  getMaterialList()
}

// 重置
const handleReset = () => {
  searchForm.materialName = ''
  searchForm.materialCode = ''
  searchForm.categoryId = undefined
  searchForm.status = undefined
  pagination.current = 1
  getMaterialList()
}

// 选择变化
const handleSelectionChange = (selection: Material[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  getMaterialList()
}

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.current = current
  getMaterialList()
}

// 新增耗材
const handleAdd = () => {
  dialogTitle.value = '新增耗材'
  currentMaterialId.value = undefined
  Object.assign(materialForm, {
    materialName: '',
    materialCode: '',
    categoryId: undefined,
    specification: '',
    unit: '',
    brand: '',
    manufacturer: '',
    minStock: 0,
    maxStock: 0,
    safetyStock: 0,
    price: 0,
    description: '',
    status: 1
  })
  codeExists.value = false
  dialogVisible.value = true
}

// 编辑耗材
const handleEdit = (row: Material) => {
  dialogTitle.value = '编辑耗材'
  currentMaterialId.value = row.id
  Object.assign(materialForm, {
    id: row.id,
    materialName: row.materialName,
    materialCode: row.materialCode,
    categoryId: row.categoryId,
    specification: row.specification,
    unit: row.unit,
    brand: row.brand,
    manufacturer: row.manufacturer,
    minStock: row.minStock,
    maxStock: row.maxStock,
    safetyStock: row.safetyStock,
    price: row.price,
    description: row.description,
    status: row.status
  })
  codeExists.value = false
  dialogVisible.value = true
}

// 编码失焦检查（带防抖）
const handleCodeBlur = async () => {
  if (!materialForm.materialCode) {
    codeExists.value = false
    return
  }

  // 清除之前的定时器
  if (codeCheckTimer) {
    clearTimeout(codeCheckTimer)
  }

  // 设置新的定时器，500ms后执行检查
  codeCheckTimer = window.setTimeout(async () => {
    try {
      const response = await materialApi.checkMaterialCode(
        materialForm.materialCode!,
        currentMaterialId.value
      )
      if (response.code === 200) {
        codeExists.value = response.data
      }
    } catch (error) {
      console.error('检查编码失败:', error)
    }
  }, 500)
}

// 提交表单
const handleSubmit = async () => {
  if (!materialFormRef.value) return

  // 检查编码是否存在
  if (materialForm.materialCode && codeExists.value) {
    ElMessage.error('该耗材编码已存在')
    return
  }

  await materialFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (currentMaterialId.value) {
          // 更新耗材
          const updateData: MaterialUpdateRequest = {
            materialName: materialForm.materialName || '',
            materialCode: materialForm.materialCode || '',
            categoryId: materialForm.categoryId,
            specification: materialForm.specification,
            unit: materialForm.unit || '',
            brand: materialForm.brand,
            manufacturer: materialForm.manufacturer,
            minStock: materialForm.minStock,
            maxStock: materialForm.maxStock,
            safetyStock: materialForm.safetyStock,
            price: materialForm.price,
            description: materialForm.description,
            status: materialForm.status
          }
          const response = await materialApi.updateMaterial(currentMaterialId.value, updateData)
          if (response.code === 200) {
            ElMessage.success('更新耗材成功')
            dialogVisible.value = false
            getMaterialList()
          } else {
            ElMessage.error(response.message || '更新耗材失败')
          }
        } else {
          // 创建耗材
          const createData: MaterialCreateRequest = {
            materialName: materialForm.materialName,
            materialCode: materialForm.materialCode,
            categoryId: materialForm.categoryId,
            specification: materialForm.specification,
            unit: materialForm.unit,
            brand: materialForm.brand,
            manufacturer: materialForm.manufacturer,
            minStock: materialForm.minStock,
            maxStock: materialForm.maxStock,
            safetyStock: materialForm.safetyStock,
            price: materialForm.price,
            description: materialForm.description
          }
          const response = await materialApi.createMaterial(createData)
          if (response.code === 200) {
            ElMessage.success('创建耗材成功')
            dialogVisible.value = false
            getMaterialList()
          } else {
            ElMessage.error(response.message || '创建耗材失败')
          }
        }
      } catch (error) {
        ElMessage.error(currentMaterialId.value ? '更新耗材失败' : '创建耗材失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 关闭弹窗
const handleDialogClose = () => {
  materialFormRef.value?.resetFields()
  codeExists.value = false
}

// 切换状态
const handleToggleStatus = async (row: Material) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'
  
  try {
    const response = await materialApi.toggleMaterialStatus(row.id)
    if (response.code === 200) {
      ElMessage.success(`已${statusText}耗材`)
      getMaterialList()
    } else {
      ElMessage.error(response.message || '状态切换失败')
    }
  } catch (error) {
    ElMessage.error('状态切换失败')
  }
}

// 删除耗材
const handleDelete = async (row: Material) => {
  ElMessageBox.confirm('确定要删除该耗材吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await materialApi.deleteMaterial(row.id)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          getMaterialList()
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
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的耗材')
    return
  }

  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个耗材吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await materialApi.batchDeleteMaterials({ ids: selectedIds.value })
        if (response.code === 200) {
          ElMessage.success('批量删除成功')
          selectedIds.value = []
          getMaterialList()
        } else {
          ElMessage.error(response.message || '批量删除失败')
        }
      } catch (error) {
        ElMessage.error('批量删除失败')
      }
    })
    .catch(() => {})
}

// 页面加载时获取耗材列表
onMounted(() => {
  getMaterialList()
})
</script>

<style scoped>
.material-manage {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.action-buttons {
  margin-bottom: 20px;
}

.error-tip {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}
</style>
