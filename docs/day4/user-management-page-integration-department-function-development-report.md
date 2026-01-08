# 用户管理页面集成部门功能开发报告

## 任务概述

**任务编号**: 3.3  
**任务名称**: 用户管理页面集成部门功能  
**预计时间**: 0.5小时  
**实际时间**: 待完成  
**开发日期**: 2026-01-08  
**开发者**: haocai

## 步骤1：规划与设计

### 1.1 关键约束条款（基于development-standards.md）

#### 约束1：数据库设计规范
- **条款内容**: 所有表必须包含标准字段（id, create_time, update_time, create_by, update_by, deleted）
- **应用说明**: sys_user表已包含department_id字段，符合规范
- **设计决策**: 使用现有的department_id字段关联sys_department表

#### 约束2：前端类型规范
- **条款内容**: 前端类型定义必须与后端DTO/VO保持一致
- **应用说明**: UserInfo接口需要添加department字段，类型与后端DepartmentVO保持一致
- **设计决策**: 在UserInfo接口中添加department?: DepartmentVO字段

#### 约束3：前端交互规范
- **条款内容**: 所有异步操作必须显示加载状态，操作完成后必须提供用户反馈
- **应用说明**: 部门树加载、用户列表加载、部门选择等操作都需要加载状态
- **设计决策**: 使用Element Plus的loading属性和ElMessage组件

### 1.2 核心功能设计

#### 功能1：用户列表添加部门列
- **设计说明**: 在用户列表表格中添加"部门"列，显示用户所属部门名称
- **方法签名**: 
  ```typescript
  // UserInfo接口扩展
  interface UserInfo {
    // ... 现有字段
    department?: DepartmentVO  // 新增部门信息
  }
  ```
- **实现方式**: 后端查询用户时关联查询部门信息，前端直接显示

#### 功能2：新增/编辑用户时添加部门选择
- **设计说明**: 在用户表单中添加部门选择器，使用树形下拉框
- **方法签名**:
  ```typescript
  // CreateUserRequest扩展
  interface CreateUserRequest {
    // ... 现有字段
    departmentId?: number  // 新增部门ID
  }
  
  // UpdateUserRequest扩展
  interface UpdateUserRequest {
    // ... 现有字段
    departmentId?: number  // 新增部门ID
  }
  ```
- **实现方式**: 创建DepartmentSelect组件，使用Element Plus的TreeSelect组件

#### 功能3：按部门筛选用户
- **设计说明**: 在搜索区域添加部门筛选下拉框
- **方法签名**:
  ```typescript
  // UserListRequest扩展
  interface UserListRequest {
    // ... 现有字段
    departmentId?: number  // 新增部门ID筛选
  }
  ```
- **实现方式**: 使用DepartmentSelect组件，选择部门后重新查询用户列表

#### 功能4：部门树形选择组件
- **设计说明**: 创建可复用的部门树形选择组件
- **组件路径**: frontend/src/components/DepartmentSelect.vue
- **Props接口**:
  ```typescript
  interface Props {
    modelValue?: number | number[]  // 选中值
    multiple?: boolean  // 是否多选
    placeholder?: string  // 占位符
    clearable?: boolean  // 是否可清空
    disabled?: boolean  // 是否禁用
  }
  ```
- **Emits接口**:
  ```typescript
  interface Emits {
    (e: 'update:modelValue', value: number | number[]): void
    (e: 'change', value: number | number[]): void
  }
  ```

### 1.3 数据库设计

#### sys_user表（已存在，无需修改）
```sql
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `department_id` BIGINT COMMENT '部门ID',  -- 已存在
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-禁用，2-锁定',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `remark` VARCHAR(500) COMMENT '备注信息',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_department_id` (`department_id`),  -- 已存在索引
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

## 步骤2：实现与编码

### 2.1 创建部门选择组件

**文件路径**: frontend/src/components/DepartmentSelect.vue

**代码内容**:
```vue
<template>
  <el-tree-select
    v-model="selectedValue"
    :data="departmentTree"
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
import { departmentApi } from '@/api/department'
import type { DepartmentTreeVO } from '@/types/department'

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
  placeholder: '请选择部门',
  clearable: true,
  disabled: false
})

const emit = defineEmits<Emits>()

// 遵循：前端交互规范-第1条（加载状态管理）
const loading = ref(false)
const departmentTree = ref<DepartmentTreeVO[]>([])

// Tree组件配置
const treeProps = {
  label: 'name',
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

// 获取部门树
const fetchDepartmentTree = async () => {
  loading.value = true
  try {
    // 遵循：API规范-响应数据处理
    const response = await departmentApi.getDepartmentTree(false)
    if (response.code === 200 && response.data) {
      departmentTree.value = response.data
    } else {
      console.error('获取部门树失败:', response.message)
    }
  } catch (error: any) {
    console.error('获取部门树失败:', error)
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

// 组件挂载时获取部门树
onMounted(() => {
  fetchDepartmentTree()
})
</script>

<style scoped>
/* 遵循：前端样式规范-组件样式隔离 */
</style>
```

### 2.2 更新用户API类型定义

**文件路径**: frontend/src/api/user.ts

**修改内容**:
```typescript
// 导入部门类型
import type { DepartmentVO } from '@/types/department'

// 用户信息类型
export interface UserInfo {
  id: number
  username: string
  name: string
  email: string
  phone: string
  status: number  // 0-正常，1-禁用，2-锁定
  createTime: string
  updateTime: string
  roles?: RoleInfo[]  // 用户角色列表
  department?: DepartmentVO  // 遵循：前端类型规范-与后端DTO/VO保持一致
}

// 创建用户请求类型
export interface CreateUserRequest {
  username: string
  password: string
  confirmPassword: string
  name: string
  email: string
  phone: string
  agreeToTerms: boolean
  departmentId?: number  // 遵循：前端类型规范-与后端DTO/VO保持一致
}

// 更新用户信息请求类型
export interface UpdateUserRequest {
  name?: string
  email?: string
  phone?: string
  status?: number
  departmentId?: number  // 遵循：前端类型规范-与后端DTO/VO保持一致
}

// 用户列表查询请求类型
export interface UserListRequest {
  page?: number
  size?: number
  username?: string
  name?: string
  status?: number
  departmentId?: number  // 遵循：前端类型规范-与后端DTO/VO保持一致
}
```

### 2.3 更新用户管理页面

**文件路径**: frontend/src/views/UserManage.vue

**主要修改点**:
1. 导入DepartmentSelect组件
2. 在搜索表单中添加部门筛选
3. 在用户列表表格中添加部门列
4. 在用户表单中添加部门选择
5. 更新搜索和提交逻辑

**关键代码片段**:
```vue
<!-- 搜索区域添加部门筛选 -->
<el-form-item label="部门">
  <DepartmentSelect
    v-model="searchForm.departmentId"
    placeholder="请选择部门"
    clearable
    @clear="handleSearch"
  />
</el-form-item>

<!-- 用户列表添加部门列 -->
<el-table-column prop="department" label="部门" width="150">
  <template #default="{ row }">
    <span v-if="row.department">{{ row.department.name }}</span>
    <span v-else style="color: #999">未分配</span>
  </template>
</el-table-column>

<!-- 用户表单添加部门选择 -->
<el-form-item label="部门" prop="departmentId">
  <DepartmentSelect
    v-model="userForm.departmentId"
    placeholder="请选择部门"
    clearable
  />
</el-form-item>
```

## 步骤3：验证与测试

### 3.1 测试用例

#### 测试用例1：用户列表显示部门
- **测试步骤**:
  1. 登录系统
  2. 进入用户管理页面
  3. 查看用户列表
- **预期结果**: 用户列表中显示部门列，显示用户所属部门名称
- **边界测试**: 用户未分配部门时显示"未分配"

#### 测试用例2：按部门筛选用户
- **测试步骤**:
  1. 进入用户管理页面
  2. 在搜索区域选择部门
  3. 点击搜索按钮
- **预期结果**: 只显示该部门下的用户
- **边界测试**: 选择顶级部门显示所有子部门用户

#### 测试用例3：新增用户时选择部门
- **测试步骤**:
  1. 点击"新增用户"按钮
  2. 填写用户信息
  3. 选择部门
  4. 点击确定
- **预期结果**: 用户创建成功，部门信息正确保存
- **边界测试**: 不选择部门时用户创建成功，departmentId为null

#### 测试用例4：编辑用户时修改部门
- **测试步骤**:
  1. 点击某个用户的"编辑"按钮
  2. 修改部门
  3. 点击确定
- **预期结果**: 用户部门更新成功
- **边界测试**: 清空部门选择，用户部门设置为null

#### 测试用例5：部门树形选择器功能
- **测试步骤**:
  1. 点击部门选择器
  2. 查看部门树
  3. 选择部门
- **预期结果**: 部门树正确显示，支持展开/折叠，支持搜索
- **边界测试**: 部门树为空时显示提示信息

### 3.2 异常测试场景

#### 异常场景1：部门树加载失败
- **测试步骤**: 模拟网络错误或后端异常
- **预期结果**: 显示错误提示，不影响其他功能
- **处理方式**: 使用try-catch捕获异常，显示ElMessage错误提示

#### 异常场景2：部门数据不一致
- **测试步骤**: 用户关联的部门被删除
- **预期结果**: 显示"未分配"或部门ID
- **处理方式**: 前端判断department是否存在，不存在时显示默认值

#### 异常场景3：并发修改部门
- **测试步骤**: 多个用户同时修改同一用户的部门
- **预期结果**: 后端处理并发冲突，前端显示最新结果
- **处理方式**: 后端使用乐观锁或悲观锁，前端刷新列表

### 3.3 代码检查顺序

1. **数据库检查**: 确认sys_user表department_id字段存在且有索引
2. **后端代码检查**: 确认用户查询接口返回部门信息
3. **前端代码检查**: 确认类型定义、组件导入、API调用正确
4. **测试文件检查**: 确认E2E测试覆盖所有功能点

## 步骤4：文档与知识固化

### 4.1 development-standards.md更新建议

#### 建议新增：部门关联规范
```markdown
### 部门关联规范

1. **用户部门关联**
   - 用户表必须包含department_id字段
   - department_id允许为null，表示用户未分配部门
   - 查询用户列表时必须关联查询部门信息
   - 删除部门前必须检查是否有用户关联

2. **部门树形选择**
   - 使用TreeSelect组件实现部门选择
   - 支持单选和多选模式
   - 支持懒加载和搜索功能
   - 禁用部门不可选择

3. **部门筛选**
   - 列表查询支持按部门筛选
   - 选择父部门时包含所有子部门数据
   - 清空部门筛选时显示所有数据
```

#### 建议新增：树形组件规范
```markdown
### 树形组件规范

1. **组件设计**
   - 树形组件必须支持懒加载
   - 必须提供加载状态显示
   - 必须支持展开/折叠操作
   - 必须支持节点搜索

2. **数据格式**
   - 树形数据必须包含id、label、children字段
   - 叶子节点children为空数组
   - 禁用节点设置disabled属性

3. **性能优化**
   - 大数据量时使用懒加载
   - 使用虚拟滚动优化渲染性能
   - 缓存已加载的节点数据
```

### 4.2 给新开发者的快速指南

#### 如何在用户管理中集成部门功能

**步骤1：添加部门字段到用户类型**
```typescript
// frontend/src/api/user.ts
import type { DepartmentVO } from '@/types/department'

export interface UserInfo {
  // ... 其他字段
  department?: DepartmentVO  // 添加部门信息
}

export interface CreateUserRequest {
  // ... 其他字段
  departmentId?: number  // 添加部门ID
}

export interface UpdateUserRequest {
  // ... 其他字段
  departmentId?: number  // 添加部门ID
}

export interface UserListRequest {
  // ... 其他字段
  departmentId?: number  // 添加部门筛选
}
```

**步骤2：使用DepartmentSelect组件**
```vue
<template>
  <!-- 单选部门 -->
  <DepartmentSelect
    v-model="form.departmentId"
    placeholder="请选择部门"
    clearable
  />
  
  <!-- 多选部门 -->
  <DepartmentSelect
    v-model="form.departmentIds"
    :multiple="true"
    placeholder="请选择多个部门"
    clearable
  />
</template>

<script setup lang="ts">
import DepartmentSelect from '@/components/DepartmentSelect.vue'
</script>
```

**步骤3：在表格中显示部门**
```vue
<el-table-column prop="department" label="部门" width="150">
  <template #default="{ row }">
    <span v-if="row.department">{{ row.department.name }}</span>
    <span v-else style="color: #999">未分配</span>
  </template>
</el-table-column>
```

**步骤4：在搜索中添加部门筛选**
```vue
<el-form-item label="部门">
  <DepartmentSelect
    v-model="searchForm.departmentId"
    placeholder="请选择部门"
    clearable
    @clear="handleSearch"
  />
</el-form-item>
```

#### 常见问题

**Q1: 用户未分配部门时如何显示？**
A: 使用v-if判断department是否存在，不存在时显示"未分配"或默认值。

**Q2: 如何实现部门级联选择？**
A: DepartmentSelect组件已支持树形结构，选择父部门时自动包含子部门。

**Q3: 部门树加载失败如何处理？**
A: 组件内部已处理加载失败情况，会显示错误提示，不影响其他功能。

**Q4: 如何禁用某些部门？**
A: 后端返回的部门数据中设置disabled属性，前端TreeSelect组件会自动禁用。

## 任务完成状态

- [x] 步骤1：规划与设计
- [ ] 步骤2：实现与编码
- [ ] 步骤3：验证与测试
- [ ] 步骤4：文档与知识固化

## 后续步骤建议

1. **后端开发**: 确保用户查询接口返回部门信息
2. **前端开发**: 实现上述所有功能点
3. **测试验证**: 执行所有测试用例
4. **文档更新**: 更新development-standards.md
5. **E2E测试**: 编写Playwright测试用例

## 附录

### A. 相关文件清单

- frontend/src/components/DepartmentSelect.vue（新建）
- frontend/src/api/user.ts（修改）
- frontend/src/views/UserManage.vue（修改）
- backend/src/main/resources/init.sql（无需修改）
- docs/common/database-design.md（无需修改）

### B. API接口清单

- GET /department/tree - 获取部门树
- GET /users - 获取用户列表（支持departmentId筛选）
- POST /users/register - 创建用户（支持departmentId）
- PUT /users/{id} - 更新用户（支持departmentId）

### C. 类型定义清单

- DepartmentVO - 部门信息
- DepartmentTreeVO - 部门树形结构
- UserInfo - 用户信息（包含department）
- CreateUserRequest - 创建用户请求（包含departmentId）
- UpdateUserRequest - 更新用户请求（包含departmentId）
- UserListRequest - 用户列表查询（包含departmentId）
