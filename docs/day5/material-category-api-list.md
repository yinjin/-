# 耗材分类管理后端API接口列表

## 概述
本文档列出了耗材分类管理模块的所有后端API接口，供前端开发使用。

**基础路径**: `/api/material-category`

**权限要求**: 所有接口都需要 `material` 权限

---

## 1. 创建耗材分类

### 接口信息
- **接口路径**: `/api/material-category`
- **HTTP方法**: `POST`
- **功能描述**: 创建新的耗材分类
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryName | String | 是 | 分类名称 |
| categoryCode | String | 否 | 分类编码（不传则自动生成） |
| parentId | Long | 是 | 父分类ID（0表示顶级分类） |
| description | String | 否 | 分类描述 |
| sortOrder | Integer | 否 | 排序号 |
| status | Integer | 否 | 状态（0-禁用，1-启用，默认1） |

### 请求示例
```json
{
  "categoryName": "办公用品",
  "categoryCode": "OFFICE",
  "parentId": 0,
  "description": "办公用品分类",
  "sortOrder": 1,
  "status": 1
}
```

### 返回数据

**类型**: `MaterialCategoryVO`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 分类ID |
| categoryName | String | 分类名称 |
| categoryCode | String | 分类编码 |
| parentId | Long | 父分类ID |
| level | Integer | 分类层级（1-一级，2-二级，3-三级） |
| sortOrder | Integer | 排序号 |
| description | String | 分类描述 |
| status | Integer | 状态（0-禁用，1-启用） |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| createBy | String | 创建人 |
| updateBy | String | 更新人 |

### 返回示例
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "categoryName": "办公用品",
    "categoryCode": "OFFICE",
    "parentId": 0,
    "level": 1,
    "sortOrder": 1,
    "description": "办公用品分类",
    "status": 1,
    "createTime": "2026-01-09T10:00:00",
    "updateTime": "2026-01-09T10:00:00",
    "createBy": "admin",
    "updateBy": "admin"
  }
}
```

---

## 2. 更新耗材分类

### 接口信息
- **接口路径**: `/api/material-category/{id}`
- **HTTP方法**: `PUT`
- **功能描述**: 更新耗材分类信息
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryName | String | 是 | 分类名称 |
| categoryCode | String | 否 | 分类编码 |
| description | String | 否 | 分类描述 |
| sortOrder | Integer | 否 | 排序号 |
| status | Integer | 否 | 状态（0-禁用，1-启用） |

### 请求示例
```json
{
  "categoryName": "办公用品（更新）",
  "categoryCode": "OFFICE",
  "description": "办公用品分类描述",
  "sortOrder": 2,
  "status": 1
}
```

### 返回数据

**类型**: `MaterialCategoryVO`

同创建接口的返回数据结构。

---

## 3. 删除耗材分类

### 接口信息
- **接口路径**: `/api/material-category/{id}`
- **HTTP方法**: `DELETE`
- **功能描述**: 删除耗材分类（逻辑删除）
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

### 返回数据

**类型**: `Boolean`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| - | Boolean | true表示删除成功，false表示删除失败 |

### 返回示例
```json
{
  "code": 200,
  "message": "删除成功",
  "data": true
}
```

---

## 4. 获取耗材分类详情

### 接口信息
- **接口路径**: `/api/material-category/{id}`
- **HTTP方法**: `GET`
- **功能描述**: 根据ID获取耗材分类详情
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

### 返回数据

**类型**: `MaterialCategoryVO`

同创建接口的返回数据结构。

---

## 5. 批量删除耗材分类

### 接口信息
- **接口路径**: `/api/material-category/batch`
- **HTTP方法**: `DELETE`
- **功能描述**: 批量删除耗材分类（逻辑删除）
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | List<Long> | 是 | 分类ID列表 |

### 请求示例
```json
{
  "ids": [1, 2, 3]
}
```

### 返回数据

**类型**: `Boolean`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| - | Boolean | true表示删除成功，false表示删除失败 |

---

## 6. 获取耗材分类树形结构

### 接口信息
- **接口路径**: `/api/material-category/tree`
- **HTTP方法**: `GET`
- **功能描述**: 获取耗材分类的完整树形结构
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 查询参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 否 | 状态过滤（0-禁用，1-启用，不传则查询所有） |

### 返回数据

**类型**: `List<MaterialCategoryTreeVO>`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 分类ID |
| categoryName | String | 分类名称 |
| categoryCode | String | 分类编码 |
| parentId | Long | 父分类ID |
| level | Integer | 分类层级 |
| sortOrder | Integer | 排序号 |
| description | String | 分类描述 |
| status | Integer | 状态 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| createBy | String | 创建人 |
| updateBy | String | 更新人 |
| children | List<MaterialCategoryTreeVO> | 子分类列表（递归结构） |

### 返回示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "categoryName": "办公用品",
      "categoryCode": "OFFICE",
      "parentId": 0,
      "level": 1,
      "sortOrder": 1,
      "description": "办公用品分类",
      "status": 1,
      "createTime": "2026-01-09T10:00:00",
      "updateTime": "2026-01-09T10:00:00",
      "createBy": "admin",
      "updateBy": "admin",
      "children": [
        {
          "id": 2,
          "categoryName": "纸张",
          "categoryCode": "OFFICE_PAPER",
          "parentId": 1,
          "level": 2,
          "sortOrder": 1,
          "description": "纸张类办公用品",
          "status": 1,
          "createTime": "2026-01-09T10:00:00",
          "updateTime": "2026-01-09T10:00:00",
          "createBy": "admin",
          "updateBy": "admin",
          "children": []
        }
      ]
    }
  ]
}
```

---

## 7. 根据父分类ID查询子分类列表

### 接口信息
- **接口路径**: `/api/material-category/children/{parentId}`
- **HTTP方法**: `GET`
- **功能描述**: 根据父分类ID查询子分类列表
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| parentId | Long | 是 | 父分类ID |

### 查询参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 否 | 状态过滤（0-禁用，1-启用，不传则查询所有） |

### 返回数据

**类型**: `List<MaterialCategoryVO>`

同创建接口的返回数据结构（列表形式）。

---

## 8. 查询所有顶级分类

### 接口信息
- **接口路径**: `/api/material-category/top-level`
- **HTTP方法**: `GET`
- **功能描述**: 查询所有顶级分类（parentId=0）
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 查询参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 否 | 状态过滤（0-禁用，1-启用，不传则查询所有） |

### 返回数据

**类型**: `List<MaterialCategoryVO>`

同创建接口的返回数据结构（列表形式）。

---

## 9. 切换分类状态

### 接口信息
- **接口路径**: `/api/material-category/{id}/toggle-status`
- **HTTP方法**: `PUT`
- **功能描述**: 切换耗材分类的启用/禁用状态
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

### 返回数据

**类型**: `Boolean`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| - | Boolean | true表示切换成功，false表示切换失败 |

---

## 10. 检查分类编码是否存在

### 接口信息
- **接口路径**: `/api/material-category/check/code`
- **HTTP方法**: `GET`
- **功能描述**: 检查分类编码是否已存在
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 查询参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryCode | String | 是 | 分类编码 |
| excludeId | Long | 否 | 排除的分类ID（用于更新时检查） |

### 返回数据

**类型**: `Boolean`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| - | Boolean | true表示编码已存在，false表示编码不存在 |

---

## 11. 检查分类下是否有子分类

### 接口信息
- **接口路径**: `/api/material-category/{id}/has-children`
- **HTTP方法**: `GET`
- **功能描述**: 检查分类下是否有子分类
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

### 返回数据

**类型**: `Boolean`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| - | Boolean | true表示有子分类，false表示没有子分类 |

---

## 通用响应格式

所有接口的响应都遵循以下统一格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码（200-成功，其他-失败） |
| message | String | 响应消息 |
| data | Object/Array/Boolean | 响应数据 |

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 注意事项

1. **权限控制**: 所有接口都需要 `material` 权限，前端需要在请求头中携带有效的JWT token
2. **分类编码**: 创建分类时如果不传 `categoryCode`，系统会自动生成（格式：CAT_时间戳）
3. **分类层级**: 系统支持最多3级分类
4. **逻辑删除**: 删除操作为逻辑删除，不会物理删除数据
5. **状态切换**: 禁用父分类不会自动禁用子分类，需要单独处理
6. **排序**: 同级分类按 `sortOrder` 字段升序排列
7. **时间格式**: 所有时间字段使用 ISO 8601 格式（如：2026-01-09T10:00:00）

---

## 前端开发建议

1. **树形展示**: 使用 Element Plus 的 `el-tree` 组件展示分类树
2. **表单验证**: 创建和更新时需要验证必填字段
3. **编码唯一性**: 在表单提交前调用检查编码接口
4. **删除确认**: 删除前检查是否有子分类，如有则提示用户
5. **状态切换**: 使用开关组件（Switch）进行状态切换
6. **分页加载**: 如果分类数量很多，建议使用懒加载方式加载子分类
7. **缓存策略**: 分类树数据变化不频繁，可以适当缓存
