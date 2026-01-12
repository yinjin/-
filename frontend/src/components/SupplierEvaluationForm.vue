<template>
  <div class="evaluation-form-container">
    <el-form
      ref="evaluationFormRef"
      :model="evaluationForm"
      :rules="evaluationRules"
      label-width="120px"
    >
      <el-form-item
        label="交付评分"
        prop="deliveryScore"
      >
        <div class="score-container">
          <el-slider
            v-model="evaluationForm.deliveryScore"
            :min="1"
            :max="10"
            :step="0.5"
            :format-tooltip="formatScore"
          />
          <el-rate
            v-model="evaluationForm.deliveryScore"
            :max="10"
            :show-score="true"
            score-template="{value}分"
            style="margin-left: 20px"
          />
        </div>
      </el-form-item>

      <el-form-item
        label="质量评分"
        prop="qualityScore"
      >
        <div class="score-container">
          <el-slider
            v-model="evaluationForm.qualityScore"
            :min="1"
            :max="10"
            :step="0.5"
            :format-tooltip="formatScore"
          />
          <el-rate
            v-model="evaluationForm.qualityScore"
            :max="10"
            :show-score="true"
            score-template="{value}分"
            style="margin-left: 20px"
          />
        </div>
      </el-form-item>

      <el-form-item
        label="服务评分"
        prop="serviceScore"
      >
        <div class="score-container">
          <el-slider
            v-model="evaluationForm.serviceScore"
            :min="1"
            :max="10"
            :step="0.5"
            :format-tooltip="formatScore"
          />
          <el-rate
            v-model="evaluationForm.serviceScore"
            :max="10"
            :show-score="true"
            score-template="{value}分"
            style="margin-left: 20px"
          />
        </div>
      </el-form-item>

      <el-form-item
        label="价格评分"
        prop="priceScore"
      >
        <div class="score-container">
          <el-slider
            v-model="evaluationForm.priceScore"
            :min="1"
            :max="10"
            :step="0.5"
            :format-tooltip="formatScore"
          />
          <el-rate
            v-model="evaluationForm.priceScore"
            :max="10"
            :show-score="true"
            score-template="{value}分"
            style="margin-left: 20px"
          />
        </div>
      </el-form-item>

      <el-form-item
        label="评价备注"
        prop="remark"
      >
        <el-input
          v-model="evaluationForm.remark"
          type="textarea"
          :rows="4"
          placeholder="请输入评价备注（可选）"
        />
      </el-form-item>

      <el-form-item>
        <div class="form-actions">
          <el-button @click="$emit('close')">
            取消
          </el-button>
          <el-button
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
          >
            提交评价
          </el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, defineProps, defineEmits } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { createEvaluation } from '@/api/supplier'
import type { SupplierEvaluationCreateRequest } from '@/api/supplier'

// Props
const props = defineProps<{
  supplierId: number
}>()

// Emits
const emit = defineEmits<{
  (e: 'success'): void
  (e: 'close'): void
}>()

// 表单引用
const evaluationFormRef = ref<FormInstance>()

// 提交状态
const submitting = ref(false)

// 评价表单
const evaluationForm = reactive<SupplierEvaluationCreateRequest>({
  supplierId: props.supplierId,
  deliveryScore: 5,
  qualityScore: 5,
  serviceScore: 5,
  priceScore: 5,
  remark: ''
})

// 表单验证规则
const evaluationRules: FormRules = {
  deliveryScore: [
    { required: true, message: '请输入交付评分', trigger: 'blur' },
    { type: 'number', min: 1, max: 10, message: '评分必须在1-10之间', trigger: 'blur' }
  ],
  qualityScore: [
    { required: true, message: '请输入质量评分', trigger: 'blur' },
    { type: 'number', min: 1, max: 10, message: '评分必须在1-10之间', trigger: 'blur' }
  ],
  serviceScore: [
    { required: true, message: '请输入服务评分', trigger: 'blur' },
    { type: 'number', min: 1, max: 10, message: '评分必须在1-10之间', trigger: 'blur' }
  ],
  priceScore: [
    { required: true, message: '请输入价格评分', trigger: 'blur' },
    { type: 'number', min: 1, max: 10, message: '评分必须在1-10之间', trigger: 'blur' }
  ],
  remark: [
    { max: 500, message: '评价备注不能超过500个字符', trigger: 'blur' }
  ]
}

// 格式化评分显示
const formatScore = (value: number) => {
  return `${value}分`
}

// 提交评价
const handleSubmit = async () => {
  if (!evaluationFormRef.value) return
  
  try {
    await evaluationFormRef.value.validate()
    
    submitting.value = true
    
    const response = await createEvaluation(props.supplierId, {
      ...evaluationForm,
      supplierId: props.supplierId
    })
    
    if (response.code === 200) {
      emit('success')
    } else {
      ElMessage.error(response.message || '评价提交失败')
    }
  } catch (error) {
    if ((error as Error).message) {
      ElMessage.error((error as Error).message)
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.evaluation-form-container {
  padding: 20px 0;
}

.score-container {
  display: flex;
  align-items: center;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>