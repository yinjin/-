<template>
  <div class="supplier-detail-container">
    <el-card>
      <!-- 供应商基本信息 -->
      <div class="supplier-header">
        <h2>{{ supplierInfo.supplierName }}</h2>
        <div class="supplier-actions">
          <el-button
            type="primary"
            @click="handleEdit"
          >
            <el-icon><Edit /></el-icon>
            编辑供应商
          </el-button>
          <el-button
            type="danger"
            @click="handleDelete"
          >
            <el-icon><Delete /></el-icon>
            删除供应商
          </el-button>
          <el-button
            type="success"
            @click="showEvaluationDialog = true"
          >
            <el-icon><StarFilled /></el-icon>
            评价供应商
          </el-button>
        </div>
      </div>

      <el-divider />

      <!-- 供应商信息卡片 -->
      <div class="info-cards">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
            </div>
          </template>
          <div class="info-item">
            <label>供应商编码：</label>
            <span>{{ supplierInfo.supplierCode }}</span>
          </div>
          <div class="info-item">
            <label>联系人：</label>
            <span>{{ supplierInfo.contactPerson }}</span>
          </div>
          <div class="info-item">
            <label>联系电话：</label>
            <span>{{ supplierInfo.phone }}</span>
          </div>
          <div class="info-item">
            <label>邮箱：</label>
            <span>{{ supplierInfo.email }}</span>
          </div>
          <div class="info-item">
            <label>地址：</label>
            <span>{{ supplierInfo.address }}</span>
          </div>
        </el-card>

        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>资质信息</span>
            </div>
          </template>
          <div class="info-item">
            <label>营业执照：</label>
            <span>{{ supplierInfo.businessLicense }}</span>
          </div>
          <div class="info-item">
            <label>税号：</label>
            <span>{{ supplierInfo.taxNumber }}</span>
          </div>
          <div class="info-item">
            <label>银行账号：</label>
            <span>{{ supplierInfo.bankAccount }}</span>
          </div>
          <div class="info-item">
            <label>开户行：</label>
            <span>{{ supplierInfo.bankName }}</span>
          </div>
        </el-card>

        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>状态信息</span>
            </div>
          </template>
          <div class="info-item">
            <label>信用等级：</label>
            <div class="credit-rating">
              <el-rate
                v-model="supplierInfo.creditRating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}分"
              />
            </div>
          </div>
          <div class="info-item">
            <label>合作状态：</label>
            <el-tag :type="getCooperationStatusType(supplierInfo.cooperationStatus)">
              {{ getCooperationStatusText(supplierInfo.cooperationStatus) }}
            </el-tag>
          </div>
          <div class="info-item">
            <label>供应商状态：</label>
            <el-tag :type="getStatusType(supplierInfo.status)">
              {{ getStatusText(supplierInfo.status) }}
            </el-tag>
          </div>
        </el-card>
      </div>

      <el-divider />

      <!-- 供应商描述 -->
      <el-card>
        <template #header>
          <div class="card-header">
            <span>供应商描述</span>
          </div>
        </template>
        <div class="description">
          {{ supplierInfo.description || '暂无描述' }}
        </div>
      </el-card>

      <el-divider />

      <!-- 供应商评价历史 -->
      <el-card>
        <template #header>
          <div class="card-header">
            <span>评价历史</span>
            <el-button
              size="small"
              type="primary"
              @click="showEvaluationDialog = true"
            >
              评价供应商
            </el-button>
          </div>
        </template>

        <el-empty
          v-if="evaluations.length === 0"
          description="暂无评价"
        />

        <div
          v-else
          class="evaluation-list"
        >
          <el-timeline>
            <el-timeline-item
              v-for="evaluation in evaluations"
              :key="evaluation.id"
              :timestamp="evaluation.evaluationDate"
              placement="top"
            >
              <el-card class="evaluation-card">
                <div class="evaluation-header">
                  <span class="evaluator-name">{{ evaluation.evaluatorName }}</span>
                  <span class="evaluation-date">{{ evaluation.createTime }}</span>
                </div>
                <div class="evaluation-scores">
                  <div class="score-item">
                    <label>交付评分：</label>
                    <el-rate
                      v-model="evaluation.deliveryScore"
                      disabled
                      show-score
                      score-template="{value}分"
                    />
                  </div>
                  <div class="score-item">
                    <label>质量评分：</label>
                    <el-rate
                      v-model="evaluation.qualityScore"
                      disabled
                      show-score
                      score-template="{value}分"
                    />
                  </div>
                  <div class="score-item">
                    <label>服务评分：</label>
                    <el-rate
                      v-model="evaluation.serviceScore"
                      disabled
                      show-score
                      score-template="{value}分"
                    />
                  </div>
                  <div class="score-item">
                    <label>价格评分：</label>
                    <el-rate
                      v-model="evaluation.priceScore"
                      disabled
                      show-score
                      score-template="{value}分"
                    />
                  </div>
                </div>
                <div class="evaluation-total">
                  <label>总分：</label>
                  <span class="total-score">{{ evaluation.totalScore }}</span>
                  <label>平均分：</label>
                  <span class="average-score">{{ evaluation.averageScore }}</span>
                  <label>信用等级：</label>
                  <el-tag :type="getCreditRatingType(evaluation.creditRating)">
                    {{ evaluation.creditRatingDescription }}
                  </el-tag>
                </div>
                <div
                  v-if="evaluation.remark"
                  class="evaluation-remark"
                >
                  <label>备注：</label>
                  <p>{{ evaluation.remark }}</p>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </el-card>
    </el-card>

    <!-- 评价供应商弹窗 -->
    <el-dialog
      v-model="showEvaluationDialog"
      title="评价供应商"
      width="600px"
    >
      <supplier-evaluation-form
        :supplier-id="supplierId"
        @success="handleEvaluationSuccess"
        @close="showEvaluationDialog = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, StarFilled } from '@element-plus/icons-vue'
import { getSupplierById, deleteSupplier } from '@/api/supplier'
import SupplierEvaluationForm from '@/components/SupplierEvaluationForm.vue'
import type { SupplierInfo } from '@/api/supplier'
import { getEvaluations } from '@/api/supplier'
import type { SupplierEvaluation } from '@/api/supplier'

// 路由相关
const route = useRoute()
const router = useRouter()
const supplierId = computed(() => Number(route.params.id))

// 供应商信息
const supplierInfo = ref<SupplierInfo>({
  id: 0,
  supplierCode: '',
  supplierName: '',
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
  description: '',
  createTime: '',
  updateTime: '',
  createBy: '',
  updateBy: ''
})

// 评价列表
const evaluations = ref<SupplierEvaluation[]>([])

// 评价弹窗
const showEvaluationDialog = ref(false)

// 加载状态
const loading = ref(true)

// 获取供应商详情
const fetchSupplierDetail = async () => {
  loading.value = true
  try {
    const response = await getSupplierById(supplierId.value)
    if (response.code === 200) {
      supplierInfo.value = response.data
    } else {
      ElMessage.error(response.message || '获取供应商详情失败')
    }
  } catch (error) {
    ElMessage.error((error as Error).message || '获取供应商详情失败')
  } finally {
    loading.value = false
  }
}

// 获取评价历史
const fetchEvaluations = async () => {
  try {
    const response = await getEvaluations(supplierId.value)
    if (response.code === 200) {
      evaluations.value = response.data
    } else {
      ElMessage.error(response.message || '获取评价历史失败')
    }
  } catch (error) {
    ElMessage.error((error as Error).message || '获取评价历史失败')
  }
}

// 编辑供应商
const handleEdit = () => {
  router.push(`/supplier/manage?edit=${supplierId.value}`)
}

// 删除供应商
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除供应商 "${supplierInfo.value.supplierName}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteSupplier(supplierId.value)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      router.push('/supplier/manage')
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error((error as Error).message || '删除失败')
    }
  }
}

// 评价成功回调
const handleEvaluationSuccess = () => {
  showEvaluationDialog.value = false
  ElMessage.success('评价成功')
  fetchEvaluations() // 重新获取评价历史
  fetchSupplierDetail() // 重新获取供应商信息（信用等级可能已更新）
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

// 获取信用等级类型
const getCreditRatingType = (rating: number): 'success' | 'warning' | 'danger' | 'info' => {
  if (rating >= 8) return 'success'
  if (rating >= 6) return 'warning'
  return 'danger'
}

// 页面加载时获取数据
onMounted(() => {
  fetchSupplierDetail()
  fetchEvaluations()
})
</script>

<style scoped>
.supplier-detail-container {
  padding: 20px;
}

.supplier-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.supplier-header h2 {
  margin: 0;
  font-size: 24px;
}

.supplier-actions {
  display: flex;
  gap: 10px;
}

.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.info-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  align-items: center;
}

.info-item label {
  width: 120px;
  font-weight: bold;
  color: #606266;
}

.info-item span {
  flex: 1;
  color: #303133;
}

.description {
  padding: 10px 0;
  line-height: 1.6;
  color: #303133;
}

.evaluation-list {
  margin-top: 20px;
}

.evaluation-card {
  margin-bottom: 20px;
}

.evaluation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.evaluator-name {
  font-weight: bold;
  color: #303133;
}

.evaluation-date {
  font-size: 12px;
  color: #909399;
}

.evaluation-scores {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  margin-bottom: 15px;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.score-item label {
  width: 80px;
  font-weight: bold;
  color: #606266;
}

.evaluation-total {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.total-score, .average-score {
  font-weight: bold;
  color: #409eff;
}

.evaluation-remark {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #ebeef5;
}

.evaluation-remark label {
  display: block;
  font-weight: bold;
  color: #606266;
  margin-bottom: 10px;
}

.credit-rating {
  margin-left: 10px;
}
</style>