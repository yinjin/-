

这份文档经过精简与重构，去除了冗余解释，强化了“前后端分离”架构下的协作规范，并更新了 Vue 3 为现代的 `<script setup>` 语法。

---

# 高职人工智能学院实训耗材管理系统 - 开发规范

**文档版本**：v2.0  
**最后更新**：2026年1月7日  
**适用范围**：项目全栈开发团队  
**技术栈**：Spring Boot 3.1.6 + MyBatis-Plus + MySQL 8.0 + Vue 3 (Composition API) + Element Plus + Playwright

---

## 一、数据库设计规范

### 1.1 基础命名与类型
*   **命名规范**：表名/字段名使用 `snake_case`（如 `department_id`），Java 实体类使用 `camelCase`（如 `departmentId`）。
*   **枚举存储**：数据库使用 `VARCHAR` 存储枚举名称（如 `ACTIVE`），Java 使用对应枚举类，**必须**配置 `TypeHandler`。
*   **审计字段**：所有业务表必须包含以下字段：
    ```sql
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除'
    ```

### 1.2 索引与约束
*   **查询索引**：外键字段及高频查询条件字段（如 `status`）必须建立普通索引。
*   **唯一索引**：需要唯一约束的业务字段（如 `username`）必须建立唯一索引。

### 1.3 ⚠️ 关键规范：唯一索引与逻辑删除冲突
**问题**：若表存在唯一索引（如 `uk_code`），将数据逻辑删除（`deleted=1`）后再次插入相同 `code` 的新数据，会报 "Duplicate key" 错误，因为旧数据仍占用索引。

**解决方案**：在插入“唯一键可能重复”的数据前，必须**物理删除**该键值对应的旧记录（无论其逻辑删除状态如何）。

```java
// ✅ 正确：物理删除旧数据以释放唯一索引
lambdaQueryWrapper.eq(Entity::getUniqueCode, code);
// 不要加 .eq(Entity::getDeleted, 0);
mapper.delete(lambdaQueryWrapper); 
```

---

## 二、后端开发规范

### 2.1 实体类
*   **字段映射**：非标准驼峰映射必须使用 `@TableField`。
*   **自动填充**：审计字段需配合 `@TableField(fill = FieldFill.INSERT)` 及 `MetaObjectHandler` 实现类。
*   **枚举处理**：
    ```java
    @TableField(value = "status", typeHandler = UserStatusHandler.class)
    private UserStatus status;
    ```

### 2.2 Service 层
*   **事务管理**：涉及多表操作或数据一致性要求的业务方法，必须添加 `@Transactional(rollbackFor = Exception.class)`。
*   **批量操作**：禁止直接批量更新不存在的 ID。必须先 `selectBatchIds` 过滤有效 ID，再执行更新，并返回详细的成功/失败统计。
*   **异常处理**：Service 层捕获异常后，应抛出统一的 `BusinessException`，禁止吞掉异常或直接打印堆栈后继续执行。

### 2.3 Controller 层
*   **统一响应**：所有接口返回 `ApiResponse` 包装对象。
*   **参数校验**：使用 `@Validated` 和 JSR-303 注解（`@NotNull`, `@NotEmpty`）进行参数校验。
*   **全局异常**：使用 `@RestControllerAdvice` 捕获异常，统一返回错误码和错误信息。

### 2.4 配置规范
**application.yml**：
```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 开启驼峰转换
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl # 开发环境开启SQL日志
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

---

## 三、前端开发规范

### 3.1 代码风格 (Vue 3)
*   **语法要求**：统一使用 **Vue 3 Composition API** (`<script setup>`)。
*   **组件库**：UI 组件必须使用 **Element Plus**，禁止手写原生样式（除特殊布局外）。
*   **目录结构**：按功能模块划分目录（`/views/consumable/...`），组件按复用程度存放在 `/components`。

### 3.2 交互逻辑
*   **加载状态**：所有异步请求必须显示 Loading 状态（表格用 `v-loading`，按钮用 `:loading`）。
*   **用户反馈**：
    *   成功：`ElMessage.success('操作成功')`
    *   失败：`ElMessage.error(error.response?.data?.message || '操作失败')`
*   **数据一致性**：增删改操作成功后，**必须**重新调用列表查询接口刷新数据 (`await fetchData()`)。

### 3.3 代码示例

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { apiGetList, apiDeleteItem } from '@/api/consumable'

const loading = ref(false)
const tableData = ref([])

// 获取列表
const fetchData = async () => {
  try {
    loading.value = true
    const { data } = await apiGetList()
    tableData.value = data
  } catch (error) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

// 删除操作
const handleDelete = async (id) => {
  try {
    await apiDeleteItem(id)
    ElMessage.success('删除成功')
    await fetchData() // 核心规范：操作后刷新
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>
```

---

## 四、E2E 测试规范

### 4.1 工具与原则
*   **框架**：Playwright。
*   **原则**：测试核心业务流程（CRUD），而非UI细节。

### 4.2 用例设计
1.  **正常流**：登录 -> 导航 -> 新增 -> 列表验证 -> 编辑 -> 验证 -> 删除 -> 验证。
2.  **异常流**：提交必填项为空的表单，验证是否出现错误提示。

### 4.3 执行命令
```bash
# 运行测试
npx playwright test

# 查看报告
npx playwright show-report
```

---

## 五、开发检查清单

在提交代码或请求 Code Review 前，请对照以下清单自查：

### 数据库 & 后端
- [ ] 数据库表包含 `create_time`, `update_time`, `deleted` 字段。
- [ ] 唯一索引与逻辑删除冲突问题已处理（如涉及）。
- [ ] 枚举字段已实现并注册 `TypeHandler`。
- [ ] Service 层涉及多表操作已添加 `@Transactional`。
- [ ] 接口返回统一使用 `ApiResponse` 包装。
- [ ] 全局异常处理器已配置，能正确捕获业务异常。

### 前端
- [ ] 使用 `<script setup>` 语法编写 Vue 组件。
- [ ] 所有异步请求都有 Loading 状态和 `try-catch` 错误处理。
- [ ] 增删改操作成功后已刷新列表数据。
- [ ] UI 组件均来自 Element Plus，样式统一。

### 测试
- [ ] 核心接口已通过 Postman/Apifox 自测。
- [ ] 关键业务流程已编写 Playwright 测试用例且通过。

---

## 六、常见问题速查

| 问题现象 | 可能原因 | 解决方案 |
| :--- | :--- | :--- |
| 查询结果字段为 null | 命名不匹配 | 数据库下划线，Java驼峰，或加 `@TableField` |
| 插入枚举报错 | 类型转换失败 | 检查 `typeHandler` 是否在 `@TableField` 指定且已注册 |
| 逻辑删除后插入报重复键 | 唯一索引冲突 | 插入前物理删除旧记录（不带 `deleted=0` 条件） |
| 前端不报错也不刷新 | 异常未捕获 | 检查 `axios` 拦截器或 API 调用是否缺少 `await/try-catch` |