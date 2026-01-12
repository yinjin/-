## 供应商管理模块API适配修复计划

### 1. 修复前端类型定义 (`supplier.ts`)
- 更新 `SupplierInfo` 接口，添加缺失的描述字段
- 添加 `businessLicenseUrl` 和 `qualificationFiles` 字段
- 统一时间字段类型为 `string`

### 2. 添加后端评价删除接口 (`SupplierInfoController.java`)
- 添加 `DELETE /evaluation/{id}` 接口
- 复用现有 `SupplierEvaluationService.deleteEvaluation()` 方法

### 3. 验证数据一致性
- 检查所有字段名称是否一致
- 验证枚举值的映射关系