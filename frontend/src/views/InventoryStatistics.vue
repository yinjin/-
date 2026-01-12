<template>
  <div class="inventory-statistics">
    <!-- 统计卡片 -->
    <el-row
      :gutter="20"
      style="margin-bottom: 20px;"
    >
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div
              class="stat-icon"
              style="background-color: #409eff;"
            >
              <el-icon
                :size="40"
                color="#fff"
              >
                <Money />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">
                库存总价值
              </div>
              <div class="stat-value">
                ¥{{ statistics.totalInventoryValue?.toFixed(2) || '0.00' }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div
              class="stat-icon"
              style="background-color: #67c23a;"
            >
              <el-icon
                :size="40"
                color="#fff"
              >
                <TrendCharts />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">
                库存周转率
              </div>
              <div class="stat-value">
                {{ statistics.inventoryTurnoverRate?.toFixed(2) || '0.00' }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row
      :gutter="20"
      style="margin-bottom: 20px;"
    >
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>库存分类占比</span>
            </div>
          </template>
          <div
            ref="categoryChartRef"
            style="width: 100%; height: 400px;"
          />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>库存预警统计</span>
            </div>
          </template>
          <div
            ref="warningChartRef"
            style="width: 100%; height: 400px;"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 刷新按钮 -->
    <div class="action-buttons">
      <el-button
        type="primary"
        :loading="loading"
        @click="handleRefresh"
      >
        刷新数据
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { Money, TrendCharts } from '@element-plus/icons-vue'
import inventoryApi from '@/api/inventory'
import type { InventoryStatistics } from '@/types/inventory'

// 加载状态
const loading = ref(false)

// 统计数据
const statistics = reactive<InventoryStatistics>({
  totalInventoryValue: 0,
  inventoryTurnoverRate: 0
})

// 图表引用
const categoryChartRef = ref<HTMLElement | null>(null)
const warningChartRef = ref<HTMLElement | null>(null)

// 图表实例
let categoryChart: echarts.ECharts | null = null
let warningChart: echarts.ECharts | null = null

/**
 * 获取统计数据
 * 遵循：前端开发规范-第2.1条（API 调用规范：统一使用 axios）
 */
const getStatistics = async () => {
  loading.value = true
  try {
    const response = await inventoryApi.getInventoryStatistics()
    if (response.code === 200) {
      statistics.totalInventoryValue = response.data.totalInventoryValue
      statistics.inventoryTurnoverRate = response.data.inventoryTurnoverRate
      updateCharts()
    } else {
      ElMessage.error(response.message || '获取统计数据失败')
    }
  } catch (error) {
    console.error('获取统计数据失败：', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

/**
 * 更新图表
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const updateCharts = () => {
  if (categoryChart) {
    updateCategoryChart()
  }
  if (warningChart) {
    updateWarningChart()
  }
}

/**
 * 更新库存分类占比饼图
 * 遵循：前端交互规范-第2条（用户反馈）
 */
const updateCategoryChart = () => {
  if (!categoryChart || !categoryChartRef.value) return

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const data = params as any[]
        return `${data[0].name}: ${data[0].value} (${data[0].percent}%)`
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '库存分类占比',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {d} ({c}%)'
        },
        data: [
          { value: 1048, name: '电子元器件', percent: 35 },
          { value: 735, name: '五金配件', percent: 25 },
          { value: 580, name: '办公用品', percent: 20 },
          { value: 484, name: '其他耗材', percent: 17 },
          { value: 300, name: '劳保用品', percent: 10 }
        ]
      }
    ]
  }

  categoryChart.setOption(option)
}

/**
 * 更新库存预警统计柱状图
 * 遵循：前端交互规范-第2条（用户反馈）
 */
const updateWarningChart = () => {
  if (!warningChart || !warningChartRef.value) return

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['低库存', '超储', '缺货']
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '低库存',
        type: 'bar',
        data: [12, 15, 10, 8, 14, 11],
        itemStyle: {
          color: '#e6a23c'
        }
      },
      {
        name: '超储',
        type: 'bar',
        data: [5, 8, 6, 9, 7, 10],
        itemStyle: {
          color: '#f56c6c'
        }
      },
      {
        name: '缺货',
        type: 'bar',
        data: [3, 5, 4, 6, 3, 5],
        itemStyle: {
          color: '#909399'
        }
      }
    ]
  }

  warningChart.setOption(option)
}

/**
 * 刷新数据
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleRefresh = () => {
  getStatistics()
}

// 页面加载时初始化图表
onMounted(() => {
  getStatistics()

  if (categoryChartRef.value) {
    categoryChart = echarts.init(categoryChartRef.value)
  }

  if (warningChartRef.value) {
    warningChart = echarts.init(warningChartRef.value)
  }

  window.addEventListener('resize', handleResize)
})

/**
 * 处理窗口大小变化
 * 遵循：前端交互规范-第1条（加载状态管理）
 */
const handleResize = () => {
  if (categoryChart) {
    categoryChart.resize()
  }
  if (warningChart) {
    warningChart.resize()
  }
}

// 页面卸载时销毁图表
onUnmounted(() => {
  if (categoryChart) {
    categoryChart.dispose()
    categoryChart = null
  }
  if (warningChart) {
    warningChart.dispose()
    warningChart = null
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.inventory-statistics {
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
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
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.action-buttons {
  text-align: center;
  margin-top: 20px;
}
</style>
