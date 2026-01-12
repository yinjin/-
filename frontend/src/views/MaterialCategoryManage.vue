<template>
  <el-container style="height: 100vh">
    <!-- 顶部面包屑 + 主按钮 -->
    <el-header class="header">
      <div class="header-left">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>耗材分类</el-breadcrumb-item>
          <el-breadcrumb-item
            v-for="b in breadList"
            :key="b.id"
          >
            {{ b.categoryName }}
          </el-breadcrumb-item>
        </el-breadcrumb>
        <div class="tree-buttons">
          <el-button
            type="success"
            size="small"
            @click="handleExpandAll"
          >
            展开全部
          </el-button>
          <el-button
            type="info"
            size="small"
            @click="handleCollapseAll"
          >
            折叠全部
          </el-button>
        </div>
      </div>
      <div class="header-right">
        <el-button
          type="primary"
          :icon="Plus"
          size="small"
          @click="handleAddChild"
        >
          新增子级
        </el-button>
      </div>
    </el-header>

    <el-container>
      <!-- 左侧树 -->
      <el-aside
        width="280px"
        class="aside"
      >
        <el-input
          v-model="filterText"
          placeholder="输入关键字过滤"
          :prefix-icon="Search"
          size="small"
          clearable
        />
        <el-tree
          ref="treeRef"
          :key="treeKey"
          :data="categoryTree"
          node-key="id"
          :props="{label:'categoryName',children:'children'}"
          highlight-current
          :filter-node-method="filterNode"
          :default-expanded-keys="defaultExpandedKeys"
          @node-click="handleNodeClick"
        />
      </el-aside>

      <!-- 右侧表 -->
      <el-main>
        <el-table
          :data="tableData"
          size="small"
          stripe
        >
          <el-table-column
            prop="categoryName"
            label="分类名称"
            min-width="150"
          />
          <el-table-column
            prop="categoryCode"
            label="分类编码"
            min-width="120"
          />
          <el-table-column
            label="状态"
            min-width="80"
          >
            <template #default="{row}">
              <el-tag
                :type="row.status === 1 ? 'success' : 'info'"
                size="small"
              >
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            min-width="180"
          >
            <template #default="{row}">
              <div class="operation-buttons">
                <el-button
                  type="primary"
                  size="small"
                  link
                  @click="handleEdit(row)"
                >
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
                <el-button
                  type="danger"
                  size="small"
                  link
                  @click="handleDelete(row)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-empty
          v-if="tableData.length === 0"
          description="暂无子分类数据"
        />
      </el-main>
    </el-container>

    <!-- 新增/编辑分类弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryFormRules"
        label-width="120px"
      >
        <el-form-item
          label="分类名称"
          prop="categoryName"
        >
          <el-input
            v-model="categoryForm.categoryName"
            placeholder="请输入分类名称"
          />
        </el-form-item>
        <el-form-item
          label="分类编码"
          prop="categoryCode"
        >
          <el-input
            v-model="categoryForm.categoryCode"
            placeholder="请输入分类编码（不填则自动生成）"
            @blur="handleCodeBlur"
          />
          <div
            v-if="codeExists"
            class="error-tip"
          >
            该分类编码已存在
          </div>
        </el-form-item>
        <el-form-item
          v-if="currentCategoryId"
          label="父分类"
          prop="parentId"
        >
          <el-tree-select
            v-model="categoryForm.parentId"
            :data="parentCategoryTree"
            :props="treeProps"
            placeholder="请选择父分类"
            check-strictly
            :render-after-expand="false"
            node-key="id"
            clearable
          >
            <template #default="{ data }">
              <span>{{ data.categoryName }}</span>
              <el-tag
                v-if="data.id === 0"
                size="small"
                style="margin-left: 8px"
              >
                顶级分类
              </el-tag>
            </template>
          </el-tree-select>
        </el-form-item>
        <el-form-item
          label="排序号"
          prop="sortOrder"
        >
          <el-input-number
            v-model="categoryForm.sortOrder"
            :min="0"
            :max="9999"
          />
        </el-form-item>
        <el-form-item
          label="分类描述"
          prop="description"
        >
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item
          label="状态"
          prop="status"
        >
          <el-radio-group v-model="categoryForm.status">
            <el-radio :value="1">
              启用
            </el-radio>
            <el-radio :value="0">
              禁用
            </el-radio>
          </el-radio-group>
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
  </el-container>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import {
  getMaterialCategoryTree,
  createMaterialCategory,
  updateMaterialCategory,
  deleteMaterialCategory,
  toggleMaterialCategoryStatus,
  checkMaterialCategoryCode,
  hasMaterialCategoryChildren
} from '@/api/material-category'
import type {
  MaterialCategoryTree,
  MaterialCategoryCreateRequest,
  MaterialCategoryUpdateRequest
} from '@/types/material-category'
import type { ElTree } from 'element-plus'

// 搜索表单（保留用于API调用）
const searchForm = reactive({
  categoryName: '',
  categoryCode: '',
  status: undefined as number | undefined
})

// 分类树数据
const categoryTree = ref<MaterialCategoryTree[]>([])

// 父分类树数据（用于选择父分类）
const parentCategoryTree = ref<MaterialCategoryTree[]>([])


// 默认展开的keys（用于强制更新）
const defaultExpandedKeys = ref<number[]>([])

// 树组件key（用于强制重新渲染）
const treeKey = ref(0)

// 左侧树相关
const filterText = ref('')
const treeRef = ref()

// 当前选中的节点
let currentNode: MaterialCategoryTree | null = null

// 面包屑列表
const breadList = ref<MaterialCategoryTree[]>([])

// 右侧表格数据
const tableData = ref<MaterialCategoryTree[]>([])

// 树形结构配置
const treeProps = {
  children: 'children',
  label: 'categoryName'
}

// 弹窗显示状态
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')

// 分类表单
const categoryFormRef = ref<FormInstance>()
const categoryForm = reactive<MaterialCategoryCreateRequest & { id?: number }>({
  categoryName: '',
  categoryCode: '',
  parentId: 0,
  description: '',
  sortOrder: 0,
  status: 1
})

// 分类表单验证规则
const categoryFormRules: FormRules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 当前编辑的分类ID
const currentCategoryId = ref<number>()

// 提交加载状态
const submitLoading = ref(false)

// 编码是否存在
const codeExists = ref(false)

// 防抖定时器
let codeCheckTimer: number | null = null

// 请求去重标志
let isFetchingTree = false

// 获取分类树（带请求去重）
const getCategoryTree = async () => {
  // 如果正在请求，直接返回
  if (isFetchingTree) {
    return
  }

  isFetchingTree = true
  try {
    const response = await getMaterialCategoryTree(searchForm.status)
    if (response.code === 200) {
      categoryTree.value = response.data || []
      // 同时设置父分类树
      parentCategoryTree.value = JSON.parse(JSON.stringify(response.data || []))
      // 默认展开所有节点
      defaultExpandedKeys.value = getAllNodeKeys(categoryTree.value)
      treeKey.value++

      // 默认选中第一个节点
      if (categoryTree.value.length > 0) {
        currentNode = categoryTree.value[0]
        breadList.value = [categoryTree.value[0]]
        loadTable(categoryTree.value[0].id)
      }
    } else {
      ElMessage.error(response.message || '获取分类树失败')
    }
  } catch (error) {
    ElMessage.error('获取分类树失败')
  } finally {
    isFetchingTree = false
  }
}

// 过滤节点
const filterNode = (value: string, data: any) => {
  if (!value) return true
  return data.categoryName.includes(value)
}

// 监听过滤文本变化
watch(filterText, val => {
  if (treeRef.value) {
    treeRef.value.filter(val)
  }
})

// 树节点点击
const handleNodeClick = (data: MaterialCategoryTree) => {
  currentNode = data
  breadList.value = getPath(data)
  loadTable(data.id)
}

// 获取面包屑路径
const getPath = (node: MaterialCategoryTree): MaterialCategoryTree[] => {
  const stack: MaterialCategoryTree[] = []
  let current: MaterialCategoryTree | undefined = node

  // 构建从根到当前节点的路径
  while (current) {
    stack.unshift(current)
    current = findParent(categoryTree.value, current.parentId)
  }

  return stack
}

// 查找父节点
const findParent = (nodes: MaterialCategoryTree[], parentId: number): MaterialCategoryTree | undefined => {
  for (const node of nodes) {
    if (node.id === parentId) return node
    if (node.children) {
      const found = findParent(node.children, parentId)
      if (found) return found
    }
  }
  return undefined
}

// 加载右侧表格数据
const loadTable = (parentId: number) => {
  // 从树数据中找到直接子级
  const findChildren = (nodes: MaterialCategoryTree[]): MaterialCategoryTree[] => {
    for (const node of nodes) {
      if (node.id === parentId) {
        return node.children || []
      }
      if (node.children && node.children.length > 0) {
        const result = findChildren(node.children)
        if (result.length > 0) return result
      }
    }
    return []
  }
  tableData.value = findChildren(categoryTree.value)
}

// 获取所有节点ID
const getAllNodeKeys = (nodes: MaterialCategoryTree[]): number[] => {
  const keys: number[] = []
  const traverse = (nodeList: MaterialCategoryTree[]) => {
    nodeList.forEach(node => {
      keys.push(node.id)
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    })
  }
  traverse(nodes)
  return keys
}

// 展开全部
const handleExpandAll = () => {
  const allKeys = getAllNodeKeys(categoryTree.value)
  defaultExpandedKeys.value = [...allKeys]
  treeKey.value++
}

// 折叠全部
const handleCollapseAll = () => {
  defaultExpandedKeys.value = []
  treeKey.value++
}

// 新增子分类
const handleAddChild = () => {
  if (!currentNode) {
    ElMessage.warning('请先选择左侧分类')
    return
  }

  // 检查是否已达到3级
  if (currentNode.level >= 3) {
    ElMessage.warning('最多支持3级分类')
    return
  }

  dialogTitle.value = '新增子分类'
  currentCategoryId.value = undefined
  Object.assign(categoryForm, {
    categoryName: '',
    categoryCode: '',
    parentId: currentNode.id,
    description: '',
    sortOrder: 0,
    status: 1
  })
  codeExists.value = false
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (data: MaterialCategoryTree) => {
  dialogTitle.value = '编辑分类'
  currentCategoryId.value = data.id
  Object.assign(categoryForm, {
    id: data.id,
    categoryName: data.categoryName,
    categoryCode: data.categoryCode,
    parentId: data.parentId,
    description: data.description,
    sortOrder: data.sortOrder,
    status: data.status
  })
  codeExists.value = false
  dialogVisible.value = true
}

// 编码失焦检查（带防抖）
const handleCodeBlur = async () => {
  if (!categoryForm.categoryCode) {
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
      const response = await checkMaterialCategoryCode(
        categoryForm.categoryCode,
        currentCategoryId.value
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
  if (!categoryFormRef.value) return

  // 检查编码是否存在
  if (categoryForm.categoryCode && codeExists.value) {
    ElMessage.error('该分类编码已存在')
    return
  }

  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (currentCategoryId.value) {
          // 更新分类
          const updateData: MaterialCategoryUpdateRequest = {
            categoryName: categoryForm.categoryName,
            categoryCode: categoryForm.categoryCode,
            parentId: categoryForm.parentId,
            description: categoryForm.description,
            sortOrder: categoryForm.sortOrder,
            status: categoryForm.status
          }
          const response = await updateMaterialCategory(currentCategoryId.value, updateData)
          if (response.code === 200) {
            ElMessage.success('更新分类成功')
            dialogVisible.value = false
            getCategoryTree()
          } else {
            ElMessage.error(response.message || '更新分类失败')
          }
        } else {
          // 创建分类
          const createData: MaterialCategoryCreateRequest = {
            categoryName: categoryForm.categoryName,
            categoryCode: categoryForm.categoryCode,
            parentId: categoryForm.parentId,
            description: categoryForm.description,
            sortOrder: categoryForm.sortOrder,
            status: categoryForm.status
          }
          const response = await createMaterialCategory(createData)
          if (response.code === 200) {
            ElMessage.success('创建分类成功')
            dialogVisible.value = false
            getCategoryTree()
          } else {
            ElMessage.error(response.message || '创建分类失败')
          }
        }
      } catch (error) {
        ElMessage.error(currentCategoryId.value ? '更新分类失败' : '创建分类失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 关闭弹窗
const handleDialogClose = () => {
  categoryFormRef.value?.resetFields()
  codeExists.value = false
}

// 切换状态
const handleToggleStatus = async (data: MaterialCategoryTree) => {
  const newStatus = data.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'
  
  try {
    const response = await toggleMaterialCategoryStatus(data.id)
    if (response.code === 200) {
      ElMessage.success(`已${statusText}分类`)
      getCategoryTree()
    } else {
      ElMessage.error(response.message || '状态切换失败')
    }
  } catch (error) {
    ElMessage.error('状态切换失败')
  }
}

// 删除分类
const handleDelete = async (data: MaterialCategoryTree) => {
  // 检查是否有子分类
  try {
    const hasChildrenResponse = await hasMaterialCategoryChildren(data.id)
    if (hasChildrenResponse.code === 200 && hasChildrenResponse.data) {
      ElMessage.warning('该分类下存在子分类，请先删除子分类')
      return
    }
  } catch (error) {
    console.error('检查子分类失败:', error)
  }

  ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await deleteMaterialCategory(data.id)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          getCategoryTree()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 页面加载时获取分类树
onMounted(() => {
  getCategoryTree()
})
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
  padding: 0 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.tree-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-right {
  display: flex;
  align-items: center;
}

.aside {
  border-right: 1px solid #eee;
  padding: 12px;
}

.operation-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dot {
  cursor: pointer;
  color: #909399;
}

.dot:hover {
  color: #409eff;
}
</style>
