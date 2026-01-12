<template>
  <div class="inventory-warning">
    <!-- 预警统计卡片 -->
    <el-row
      :gutter="20"
      style="margin-bottom: 20px;"
    >
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div
              class="stat-icon"
              style="background-color: #f56c6c;"
            >
              <el-icon
                :size="30"
                color="#fff"
              >
                <Warning />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">
                预警总数
              </div>
              <div class="stat-value">
                {{ warningStatistics.total }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div
              class="stat-icon"
              style="background-color: #e6a23c;"
            >
              <el-icon
                :size="30"
                color="#fff"
              >
                <Clock />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">
                未处理数
              </div>
              <div class="stat-value">
                {{ warningStatistics.unhandled }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div
              class="stat-icon"
              style="background-color: #67c23a;"
            >
              <el-icon
                :size="30"
                color="#fff"
              >
                <TrendCharts />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">
                低库存
              </div>
              <div class="stat-value">
                {{ warningStatistics.lowStock }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div
              class="stat-icon"
              style="background-color: #f56c6c;"
            >
              <el-icon
                :size="30"
                color="#fff"
              >
                <WarningFilled />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">
                超储
              </div>
              <div class="stat-value">
                {{ warningStatistics.overStock }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选表单 -->
    <el-card>
      <el-form
        :inline="true"
        :model="searchForm"
        class="search-form"
      >
        <el-form-item label="预警类型">
          <el-select
            v-model="searchForm.warningType"
            placeholder="请选择预警类型"
            clearable
            @change="handleWarningTypeChange"
          >
            <el-option
              label="全部"
              value=""
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
              label="临期"
              value="EXPIRED"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleSearch"
          >
            刷新
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预警列表 -->
    <el-card>
      <el-table
        v-loading="loading"
        :data="warningList"
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
          prop="warehouse"
          label="仓库"
          min-width="100"
        />
        <el-table-column
          prop="quantity"
          label="当前库存"
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
          prop="status"
          label="预警类型"
          min-width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="getWarningType(row.status)"
              size="small"
            >
              {{ getWarningTypeText(row.status) }}
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
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning, Clock, WarningFilled, TrendCharts } from '@element-plus/icons-vue'
import inventoryApi from '@/api/inventory'
import type { Inventory } from '@/types/inventory'

// 搜索表单
const searchForm = reactive({
  warningType: ''
})

// 预警列表数据
const warningList = ref<Inventory[]>([])

// 加载状态
const loading = ref(false)

// 预警统计数据
const warningStatistics = reactive({
  total: 0,
  unhandled: 0,
  lowStock: 0,
  overStock: 0
})

/**
 * 获取预警状态类型
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
 * 获取预警类型
 * 遵循：前端交互规范-第2条（用户反馈）
 */
const getWarningType = (status: string) => {
  switch (status) {
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
 * 获取预警类型文本
 * 遵循：前端交互规范-第2条（用户反馈）
 */
const getWarningTypeText = (status: string) => {
  switch (status) {
    case 'LOW_STOCK':
      return '低库存'
    case 'OVER_STOCK':
      return '超储'
    case 'OUT_OF_STOCK':
      return '缺货'
    default:
      return '正常'
  }
}

/**
 * 获取预警列表
 * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
 */
const getWarningList = async () => {
  loading.value = true
  try {
    let response
    if (searchForm.warningType === 'LOW_STOCK') {
      response = await inventoryApi.getLowStockList()
    } else if (searchForm.warningType === 'OVER_STOCK') {
      response = await inventoryApi.getOverStockList()
    } else if (searchForm.warningType === 'EXPIRED') {
      response = await inventoryApi.getExpiredList()
    } else {
      response = await inventoryApi.getInventoryWarning()
    }
    
    if (response.code === 200) {
      warningList.value = response.data
      updateStatistics(response.data)
    } else {
      ElMessage.error(response.message || '获取预警列表失败')
    }
  } catch (error) {
    console.error('获取预警列表失败：', error)
    ElMessage.error('获取预警列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 更新预警统计数据
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const updateStatistics = (list: Inventory[]) => {
  warningStatistics.total = list.length
  warningStatistics.lowStock = list.filter(item => item.status === 'LOW_STOCK').length
  warningStatistics.overStock = list.filter(item => item.status === 'OVER_STOCK').length
  warningStatistics.unhandled = list.length
}

/**
 * 预警类型改变
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleWarningTypeChange = () => {
  getWarningList()
}

/**
 * 刷新
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleSearch = () => {
  getWarningList()
}

// 页面加载时获取预警列表
onMounted(() => {
  getWarningList()
})
</script>

<style scoped>
.inventory-warning {
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.search-form {
  margin-bottom: 20px;
}
</style>
