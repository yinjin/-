<template>
  <div class="inventory-manage">
    <el-card>
      <!-- 搜索表单 -->
      <el-form
        :inline="true"
        :model="searchForm"
        class="search-form"
      >
        <el-form-item label="耗材名称">
          <el-input
            v-model="searchForm.materialName"
            placeholder="请输入耗材名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="耗材编码">
          <el-input
            v-model="searchForm.materialCode"
            placeholder="请输入耗材编码"
            clearable
          />
        </el-form-item>
        <el-form-item label="仓库">
          <el-input
            v-model="searchForm.warehouse"
            placeholder="请输入仓库"
            clearable
          />
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option
              label="正常"
              value="NORMAL"
            />
            <el-option
              label="低库存"
              value="LOW_STOCK"
            />
            <el-option
              label="超储"
              value="OVER_STOCK"
            />
            <el-option
              label="缺货"
              value="OUT_OF_STOCK"
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

      <!-- 库存列表 -->
      <el-table
        v-loading="loading"
        :data="inventoryList"
        style="width: 100%; margin-top: 20px"
      >
        <el-table-column
          prop="materialName"
          label="耗材名称"
          min-width="120"
        />
        <el-table-column
          prop="materialCode"
          label="耗材编码"
          min-width="120"
        />
        <el-table-column
          prop="specification"
          label="规格型号"
          min-width="100"
        />
        <el-table-column
          prop="unit"
          label="单位"
          min-width="40"
        />
        <el-table-column
          prop="warehouse"
          label="仓库"
          min-width="100"
        />
        <el-table-column
          prop="location"
          label="库存位置"
          min-width="100"
        />
        <el-table-column
          prop="quantity"
          label="库存数量"
          min-width="80"
          align="right"
        />
        <el-table-column
          prop="availableQuantity"
          label="可用数量"
          min-width="80"
          align="right"
        />
        <el-table-column
          prop="safeQuantity"
          label="安全库存"
          min-width="80"
          align="right"
        />
        <el-table-column
          prop="maxQuantity"
          label="最大库存"
          min-width="80"
          align="right"
        />
        <el-table-column
          prop="unitPrice"
          label="单价"
          min-width="100"
          align="right"
        >
          <template #default="{ row }">
            ¥{{ row.unitPrice?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="inventoryValue"
          label="库存价值"
          min-width="100"
          align="right"
        >
          <template #default="{ row }">
            ¥{{ row.inventoryValue?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="库存状态"
          min-width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              size="small"
            >
              {{ row.statusDescription }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="lastInTime"
          label="最后入库时间"
          min-width="120"
        />
        <el-table-column
          prop="lastOutTime"
          label="最后出库时间"
          min-width="120"
        />
        <el-table-column
          label="操作"
          min-width="150"
          align="center"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button
              size="small"
              type="primary"
              link
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
            <el-button
              size="small"
              type="warning"
              link
              @click="handleAdjust(row)"
            >
              调整
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

    <!-- 库存详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="库存详情"
      width="800px"
    >
      <el-descriptions
        :column="2"
        border
      >
        <el-descriptions-item label="耗材名称">
          {{ currentInventory?.materialName }}
        </el-descriptions-item>
        <el-descriptions-item label="耗材编码">
          {{ currentInventory?.materialCode }}
        </el-descriptions-item>
        <el-descriptions-item label="规格型号">
          {{ currentInventory?.specification }}
        </el-descriptions-item>
        <el-descriptions-item label="单位">
          {{ currentInventory?.unit }}
        </el-descriptions-item>
        <el-descriptions-item label="品牌">
          {{ currentInventory?.brand }}
        </el-descriptions-item>
        <el-descriptions-item label="单价">
          ¥{{ currentInventory?.unitPrice?.toFixed(2) || '0.00' }}
        </el-descriptions-item>
        <el-descriptions-item label="库存数量">
          {{ currentInventory?.quantity }}
        </el-descriptions-item>
        <el-descriptions-item label="可用数量">
          {{ currentInventory?.availableQuantity }}
        </el-descriptions-item>
        <el-descriptions-item label="安全库存">
          {{ currentInventory?.safeQuantity }}
        </el-descriptions-item>
        <el-descriptions-item label="最大库存">
          {{ currentInventory?.maxQuantity }}
        </el-descriptions-item>
        <el-descriptions-item label="仓库">
          {{ currentInventory?.warehouse }}
        </el-descriptions-item>
        <el-descriptions-item label="库存位置">
          {{ currentInventory?.location }}
        </el-descriptions-item>
        <el-descriptions-item label="最后入库时间">
          {{ currentInventory?.lastInTime }}
        </el-descriptions-item>
        <el-descriptions-item label="最后出库时间">
          {{ currentInventory?.lastOutTime }}
        </el-descriptions-item>
        <el-descriptions-item label="总入库量">
          {{ currentInventory?.totalInQuantity }}
        </el-descriptions-item>
        <el-descriptions-item label="总出库量">
          {{ currentInventory?.totalOutQuantity }}
        </el-descriptions-item>
        <el-descriptions-item label="库存状态">
          <el-tag
            :type="getStatusType(currentInventory?.status)"
            size="small"
          >
            {{ currentInventory?.statusDescription }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="库存价值">
          ¥{{ currentInventory?.inventoryValue?.toFixed(2) || '0.00' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">
          关闭
        </el-button>
      </template>
    </el-dialog>

    <!-- 库存调整弹窗 -->
    <el-dialog
      v-model="adjustDialogVisible"
      title="库存调整"
      width="600px"
      @close="handleAdjustDialogClose"
    >
      <el-form
        ref="adjustFormRef"
        :model="adjustForm"
        :rules="adjustFormRules"
        label-width="120px"
      >
        <el-form-item
          label="耗材名称"
        >
          <el-input
            v-model="adjustForm.materialName"
            disabled
          />
        </el-form-item>
        <el-form-item
          label="当前库存"
        >
          <el-input
            v-model="adjustForm.currentQuantity"
            disabled
          />
        </el-form-item>
        <el-form-item
          label="调整数量"
          prop="adjustQuantity"
        >
          <el-input-number
            v-model="adjustForm.adjustQuantity"
            :min="-999999"
            :max="999999"
            placeholder="正数增加，负数减少"
          />
        </el-form-item>
        <el-form-item
          label="调整类型"
        >
          <el-select
            v-model="adjustForm.adjustType"
            placeholder="请选择调整类型"
          >
            <el-option
              label="手动调整"
              value="MANUAL"
            />
            <el-option
              label="盘点调整"
              value="CHECK"
            />
            <el-option
              label="损耗调整"
              value="LOSS"
            />
            <el-option
              label="其他调整"
              value="OTHER"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="调整原因"
          prop="reason"
        >
          <el-input
            v-model="adjustForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustDialogVisible = false">
          取消
        </el-button>
        <el-button
          type="primary"
          :loading="adjustSubmitLoading"
          @click="handleAdjustSubmit"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import inventoryApi from '@/api/inventory'
import type {
  Inventory,
  InventoryAdjustRequest,
  InventoryQueryParams
} from '@/types/inventory'

// 搜索表单
const searchForm = reactive<InventoryQueryParams>({
  materialName: '',
  materialCode: '',
  warehouse: '',
  status: '',
  current: 1,
  size: 10,
  orderBy: 'id',
  orderDirection: 'desc'
})

// 库存列表数据
const inventoryList = ref<Inventory[]>([])

// 加载状态
const loading = ref(false)

// 分页信息
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 库存详情弹窗
const detailDialogVisible = ref(false)
const currentInventory = ref<Inventory | null>(null)

// 库存调整弹窗
const adjustDialogVisible = ref(false)
const adjustFormRef = ref<FormInstance>()
const adjustSubmitLoading = ref(false)
const adjustForm = reactive({
  materialId: 0,
  materialName: '',
  currentQuantity: 0,
  adjustQuantity: 0,
  adjustType: 'MANUAL' as string | undefined,
  reason: ''
})

// 库存调整表单验证规则
const adjustFormRules: FormRules = {
  adjustQuantity: [
    { required: true, message: '请输入调整数量', trigger: 'blur' },
    { type: 'number', message: '请输入有效的数字', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请输入调整原因', trigger: 'blur' }
  ]
}

/**
 * 获取库存状态类型
 * 遵循：前端交互规范-第2条（用户反馈）
 */
const getStatusType = (status: string) => {
  switch (status) {
    case 'NORMAL':
      return 'success'
    case 'LOW_STOCK':
      return 'warning'
    case 'OVER_STOCK':
      return 'danger'
    case 'OUT_OF_STOCK':
      return 'info'
    default:
      return ''
  }
}

/**
 * 获取库存列表
 * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
 */
const getInventoryList = async () => {
  loading.value = true
  try {
    const response = await inventoryApi.getInventoryPage(searchForm)
    if (response.code === 200) {
      inventoryList.value = response.data.records
      pagination.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取库存列表失败')
    }
  } catch (error) {
    console.error('获取库存列表失败：', error)
    ElMessage.error('获取库存列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleSearch = () => {
  pagination.current = 1
  getInventoryList()
}

/**
 * 重置
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleReset = () => {
  searchForm.materialName = ''
  searchForm.materialCode = ''
  searchForm.warehouse = ''
  searchForm.status = ''
  pagination.current = 1
  getInventoryList()
}

/**
 * 分页大小改变
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleSizeChange = (size: number) => {
  pagination.size = size
  getInventoryList()
}

/**
 * 当前页改变
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleCurrentChange = (current: number) => {
  pagination.current = current
  getInventoryList()
}

/**
 * 查看库存详情
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleViewDetail = async (row: Inventory) => {
  try {
    const response = await inventoryApi.getInventoryById(row.id)
    if (response.code === 200) {
      currentInventory.value = response.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取库存详情失败')
    }
  } catch (error) {
    console.error('获取库存详情失败：', error)
    ElMessage.error('获取库存详情失败')
  }
}

/**
 * 调整库存
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleAdjust = (row: Inventory) => {
  adjustForm.materialId = row.materialId
  adjustForm.materialName = row.materialName
  adjustForm.currentQuantity = row.quantity
  adjustForm.adjustQuantity = 0
  adjustForm.adjustType = 'MANUAL'
  adjustForm.reason = ''
  adjustDialogVisible.value = true
}

/**
 * 关闭调整弹窗
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleAdjustDialogClose = () => {
  adjustDialogVisible.value = false
  adjustFormRef.value?.resetFields()
}

/**
 * 提交库存调整
 * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
 */
const handleAdjustSubmit = async () => {
  if (!adjustFormRef.value) return

  await adjustFormRef.value.validate(async (valid) => {
    if (valid) {
      adjustSubmitLoading.value = true
      try {
        const adjustData: InventoryAdjustRequest = {
          materialId: adjustForm.materialId,
          adjustQuantity: adjustForm.adjustQuantity,
          reason: adjustForm.reason
        }
        if (adjustForm.adjustType) {
          adjustData.adjustType = adjustForm.adjustType
        }
        const response = await inventoryApi.adjustInventory(adjustData)
        if (response.code === 200) {
          ElMessage.success('库存调整成功')
          adjustDialogVisible.value = false
          getInventoryList()
        } else {
          ElMessage.error(response.message || '库存调整失败')
        }
      } catch (error) {
        console.error('库存调整失败：', error)
        ElMessage.error('库存调整失败')
      } finally {
        adjustSubmitLoading.value = false
      }
    }
  })
}

// 页面加载时获取库存列表
onMounted(() => {
  getInventoryList()
})
</script>

<style scoped>
.inventory-manage {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
