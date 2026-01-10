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
        <el-table-column type="selection" width="30" />
        <el-table-column prop="materialName" label="耗材名称" min-width="120" />
        <el-table-column prop="materialCode" label="耗材编码" min-width="120" />
        <el-table-column prop="categoryName" label="分类" min-width="100" />
        <el-table-column prop="specification" label="规格型号" min-width="100" />
        <el-table-column prop="unit" label="单位" min-width="40" />
        <el-table-column prop="minStock" label="最小库存" min-width="50" align="right" />
        <el-table-column prop="maxStock" label="最大库存" min-width="50" align="right" />
        <el-table-column prop="safetyStock" label="安全库存" min-width="50" align="right" />
        <el-table-column prop="unitPrice" label="单价" min-width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.unitPrice?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="imageUrl" label="图片" min-width="80" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="row.imageUrl"
              :preview-src-list="[row.imageUrl]"
              :preview-teleported="true"
              style="width: 50px; height: 50px; border-radius: 4px;"
              fit="cover"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column label="条码" min-width="100" align="center">
          <template #default="{ row }">
            <div v-if="barcodeImages[row.id]" class="barcode-container">
              <el-image
                :src="barcodeImages[row.id]"
                style="width: 100px; height: 40px;"
                fit="contain"
              />
              <el-button size="mini" type="link" @click="downloadBarcode(row)">下载</el-button>
            </div>
            <el-button v-else size="small" @click="handleGenerateBarcode(row)">
              生成条码
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="二维码" min-width="100" align="center">
          <template #default="{ row }">
            <div v-if="qrCodeImages[row.id]" class="qrcode-container">
              <el-image
                :src="qrCodeImages[row.id]"
                style="width: 60px; height: 60px;"
                fit="contain"
              />
              <el-button size="mini" type="link" @click="downloadQRCode(row)">下载</el-button>
            </div>
            <el-button v-else size="small" @click="handleGenerateQRCode(row)">
              生成二维码
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" min-width="150" fixed="right">
          <template #default="{ row }">
            <div class="operation-buttons">
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
              <!--
              <el-dropdown>
                <el-button type="primary" size="small" link>
                  更多
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="handleGenerateBarcode(row)">
                      生成条码
                    </el-dropdown-item>
                    <el-dropdown-item @click="handleGenerateQRCode(row)">
                      生成二维码
                    </el-dropdown-item>
                    <el-dropdown-item @click="handleUploadImage(row)">
                      上传图片
                    </el-dropdown-item>
                    <el-dropdown-item @click="handleViewImages(row)" v-if="row.imageUrl">
                      查看图片
                    </el-dropdown-item>
                  
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              -->
            </div>
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
                style="width: calc(100% - 100px);"
              />
              <el-button
                type="primary"
                size="small"
                @click="handleGenerateCodeForForm"
                :disabled="!materialForm.categoryId"
                style="margin-left: 10px;"
              >
                生成编码
              </el-button>
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
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number
                v-model="materialForm.unitPrice"
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
        <el-form-item label="耗材图片">
          <div v-if="materialForm.imageUrl" class="image-preview">
            <el-image
              :src="materialForm.imageUrl"
              :preview-src-list="[materialForm.imageUrl]"
              :preview-teleported="true"
              style="width: 100px; height: 100px; border-radius: 4px;"
              fit="cover"
            />
            <el-button type="danger" size="small" @click="removeImage" style="margin-left: 10px;">删除图片</el-button>
          </div>
          <el-upload
            v-else
            class="image-uploader"
            :auto-upload="false"
            :show-file-list="false"
            accept="image/*"
            :on-change="handleImageChange"
          >
            <el-button type="primary">选择图片</el-button>
          </el-upload>
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
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
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

// 条码图片存储 (materialId -> base64Image)
const barcodeImages = ref<Record<number, string>>({})

// 二维码图片存储 (materialId -> base64Image)
const qrCodeImages = ref<Record<number, string>>({})

// 弹窗显示状态
const dialogVisible = ref(false)
const dialogTitle = ref('新增耗材')

// 耗材表单
const materialFormRef = ref<FormInstance>()
const materialForm = reactive<MaterialCreateRequest & { id?: number; status?: number; imageUrl?: string }>({
  materialName: '',
  materialCode: '',
  categoryId: 0,
  specification: '',
  unit: '',
  minStock: 0,
  maxStock: 0,
  safetyStock: 0,
  unitPrice: 0,
  description: '',
  status: 1,
  imageUrl: undefined
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
    minStock: row.minStock,
    maxStock: row.maxStock,
    safetyStock: row.safetyStock,
    unitPrice: row.unitPrice,
    description: row.description,
    status: row.status,
    imageUrl: row.imageUrl || undefined
  })
  codeExists.value = false
  dialogVisible.value = true

  // 如果编辑时没有耗材编码，自动生成一个
  if (!materialForm.materialCode && materialForm.categoryId) {
    setTimeout(() => {
      handleGenerateCodeForForm()
    }, 100) // 延迟执行，确保对话框已打开
  }
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
            minStock: materialForm.minStock,
            maxStock: materialForm.maxStock,
            safetyStock: materialForm.safetyStock,
            unitPrice: materialForm.unitPrice,
            description: materialForm.description,
            status: materialForm.status,
            imageUrl: materialForm.imageUrl
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
            minStock: materialForm.minStock,
            maxStock: materialForm.maxStock,
            safetyStock: materialForm.safetyStock,
            unitPrice: materialForm.unitPrice,
            description: materialForm.description,
            imageUrl: materialForm.imageUrl
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

// 生成条码
const handleGenerateBarcode = async (row: Material) => {
  try {
    const response = await materialApi.generateBarcode(row.id)
    if (response.code === 200) {
      ElMessage.success('条码生成成功')
      // 存储生成的条码图片
      barcodeImages.value[row.id] = response.data
      // 更新列表以显示新生成的条码
      getMaterialList()
    } else {
      ElMessage.error(response.message || '条码生成失败')
    }
  } catch (error) {
    ElMessage.error('条码生成失败')
  }
}

// 生成二维码
const handleGenerateQRCode = async (row: Material) => {
  try {
    const response = await materialApi.generateQRCode(row.id)
    if (response.code === 200) {
      ElMessage.success('二维码生成成功')
      // 存储生成的二维码图片
      qrCodeImages.value[row.id] = response.data
      // 更新列表以显示新生成的二维码
      getMaterialList()
    } else {
      ElMessage.error(response.message || '二维码生成失败')
    }
  } catch (error) {
    ElMessage.error('二维码生成失败')
  }
}

// 下载条码
const downloadBarcode = (row: Material) => {
  const imageSrc = barcodeImages.value[row.id]
  if (imageSrc) {
    const link = document.createElement('a')
    link.href = imageSrc
    link.download = `barcode_${row.materialCode}.png`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

// 下载二维码
const downloadQRCode = (row: Material) => {
  const imageSrc = qrCodeImages.value[row.id]
  if (imageSrc) {
    const link = document.createElement('a')
    link.href = imageSrc
    link.download = `qrcode_${row.materialCode}.png`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

// 上传图片
const handleUploadImage = async (row: Material) => {
  // 创建一个隐藏的文件输入框
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (event: any) => {
    const file = event.target.files[0]
    if (!file) return

    try {
      const response = await (materialApi as any).uploadMaterialImage(row.id, file)
      if (response.code === 200) {
        ElMessage.success('图片上传成功')
        // 更新列表以显示新上传的图片
        getMaterialList()
      } else {
        ElMessage.error(response.message || '图片上传失败')
      }
    } catch (error) {
      ElMessage.error('图片上传失败')
    }
  }
  input.click()
}

// 查看图片
const handleViewImages = (row: Material) => {
  if (row.imageUrl) {
    window.open(row.imageUrl, '_blank')
  } else {
    ElMessage.info('该耗材暂无图片')
  }
}

// 生成编码
const handleGenerateCode = async (row: Material) => {
  try {
    const response = await (materialApi as any).generateMaterialCode(row.categoryId)
    if (response.code === 200) {
      ElMessage.success(`编码生成成功: ${response.data}`)
      // 可以选择自动填充到表单中
      ElMessageBox.confirm(
        `生成的编码为: ${response.data}\n是否将此编码填入当前表单？`,
        '确认操作',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        }
      ).then(() => {
        materialForm.materialCode = response.data
        ElMessage.success('编码已填入表单')
      }).catch(() => {
        // 用户取消操作
      })
    } else {
      ElMessage.error(response.message || '编码生成失败')
    }
  } catch (error) {
    ElMessage.error('编码生成失败')
  }
}

// 为编辑表单生成编码
const handleGenerateCodeForForm = async () => {
  if (!materialForm.categoryId) {
    ElMessage.warning('请先选择分类')
    return
  }

  try {
    const response = await (materialApi as any).generateMaterialCode(materialForm.categoryId)
    if (response.code === 200) {
      materialForm.materialCode = response.data
      ElMessage.success('编码生成成功，已自动填入')
      // 触发编码验证
      await handleCodeBlur()
    } else {
      ElMessage.error(response.message || '编码生成失败')
    }
  } catch (error) {
    ElMessage.error('编码生成失败')
  }
}

// 处理图片选择
const handleImageChange = (uploadFile: UploadFile) => {
  const file = uploadFile.raw
  if (!file) return

  // 如果是编辑模式，需要上传图片到服务器
  if (currentMaterialId.value) {
    (materialApi as any).uploadMaterialImage(currentMaterialId.value, file)
      .then((response: any) => {
        if (response.code === 200) {
          materialForm.imageUrl = response.data
          ElMessage.success('图片上传成功')
        } else {
          ElMessage.error(response.message || '图片上传失败')
        }
      })
      .catch(() => {
        ElMessage.error('图片上传失败')
      })
  } else {
    // 如果是新建模式，先保存文件引用，创建耗材后再上传
    // 使用 FileReader 预览图片
    const reader = new FileReader()
    reader.onload = (e) => {
      materialForm.imageUrl = e.target?.result as string
    }
    reader.readAsDataURL(file)
  }
}

// 删除图片
const removeImage = async () => {
  if (!materialForm.imageUrl) {
    return
  }
  
  // 如果是新增模式（没有 currentMaterialId），直接清除前端预览
  if (!currentMaterialId.value) {
    materialForm.imageUrl = undefined
    ElMessage.success('图片已移除')
    return
  }
  
  // 编辑模式：调用后端接口删除图片
  try {
    // 从imageUrl中提取文件名作为imageId
    // imageUrl格式: /api/files/uploads/material_1_1767966351242.jpg
    const fileName = materialForm.imageUrl.split('/').pop()
    const imageId = fileName ? fileName.split('.')[0] : ''
    
    if (imageId) {
      const response = await materialApi.deleteMaterialImage(currentMaterialId.value, imageId)
      if (response.code === 200) {
        ElMessage.success('图片删除成功')
        // 清除前端显示
        materialForm.imageUrl = undefined
        // 刷新列表数据
        getMaterialList()
      } else {
        ElMessage.error(response.message || '图片删除失败')
      }
    }
  } catch (error) {
    ElMessage.error('图片删除失败')
  }
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

.image-preview {
  display: flex;
  align-items: center;
}

.image-uploader {
  display: inline-block;
}

.barcode-container,
.qrcode-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.barcode-container .el-image,
.qrcode-container .el-image {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.operation-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.operation-buttons .el-button {
  white-space: nowrap;
}

/* 修复更多按钮的边框样式 */
.operation-buttons .el-dropdown {
  border: none !important;
  background: transparent !important;
  box-shadow: none !important;
  margin: 0 !important;
}

.operation-buttons .el-button {
  white-space: nowrap;
}

/* 确保更多按钮与其他按钮样式一致 */
.operation-buttons .el-dropdown .el-button {
  border: none !important;
  background: transparent !important;
  box-shadow: none !important;
}
</style>
