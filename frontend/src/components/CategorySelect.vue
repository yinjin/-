<template>
  <el-tree-select
    v-model="selectedValue"
    :data="categoryTree"
    :props="treeProps"
    :multiple="multiple"
    :placeholder="placeholder"
    :clearable="clearable"
    :disabled="disabled"
    :loading="loading"
    :render-after-expand="false"
    check-strictly
    node-key="id"
    @change="handleChange"
  />
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { getMaterialCategoryTree } from '@/api/material-category'
import type { MaterialCategoryTree } from '@/types/material-category'

// 遵循：前端组件规范-组件Props定义
interface Props {
  modelValue?: number | number[]
  multiple?: boolean
  placeholder?: string
  clearable?: boolean
  disabled?: boolean
}

// 遵循：前端组件规范-组件Emits定义
interface Emits {
  (e: 'update:modelValue', value: number | number[]): void
  (e: 'change', value: number | number[]): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: undefined,
  multiple: false,
  placeholder: '请选择分类',
  clearable: true,
  disabled: false
})

const emit = defineEmits<Emits>()

// 遵循：前端交互规范-第1条（加载状态管理）
const loading = ref(false)
const categoryTree = ref<MaterialCategoryTree[]>([])

// Tree组件配置
const treeProps = {
  label: 'categoryName',
  children: 'children',
  value: 'id',
  disabled: 'disabled'
}

// 选中值
const selectedValue = ref<number | number[] | undefined>(props.modelValue)

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  selectedValue.value = newVal
})

// 获取分类树
const fetchCategoryTree = async () => {
  loading.value = true
  try {
    // 遵循：API规范-响应数据处理
    const response = await getMaterialCategoryTree()
    if (response.code === 200 && response.data) {
      categoryTree.value = response.data
    } else {
      console.error('获取分类树失败:', response.message)
    }
  } catch (error: any) {
    console.error('获取分类树失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理选择变化
const handleChange = (value: number | number[]) => {
  selectedValue.value = value
  emit('update:modelValue', value)
  emit('change', value)
}

// 组件挂载时获取分类树
onMounted(() => {
  fetchCategoryTree()
})
</script>

<style scoped>
/* 遵循：前端样式规范-组件样式隔离 */
</style>
