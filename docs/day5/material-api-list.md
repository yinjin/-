# 耗材管理后端API接口列表

## 概述
本文档列出了耗材管理模块的所有后端API接口，供前端开发使用。

**基础路径**: `/api/material`

**权限要求**: 所有接口都需要 `material` 权限

---

## 1. 创建耗材

### 接口信息
- **接口路径**: `/api/material`
- **HTTP方法**: `POST`
- **功能描述**: 创建新的耗材
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| materialName | String | 是 | 耗材名称 |
| materialCode | String | 否 | 耗材编码（不传则自动生成） |
| categoryId | Long | 否 | 分类ID |
| specification | String | 否 | 规格型号 |
| unit | String | 是 | 计量单位 |
| brand | String | 否 | 品牌 |
| manufacturer | String | 否 | 生产厂家 |
| minStock | Integer | 否 | 最小库存量（默认0） |
| maxStock | Integer | 否 | 最大库存量（默认0） |
| safetyStock | Integer | 否 | 安全库存量（默认0） |
| price | BigDecimal | 否 | 单价 |
| description | String | 否 | 耗材描述 |
| status | Integer | 否 | 状态（0-禁用，1-启用，默认1） |

### 请求示例
```json
{
  "materialName": "A4打印纸",
  "materialCode": "MAT_001",
  "categoryId": 2,
  "specification": "70g/m² 500张/包",
  "unit": "包",
  "brand": "得力",
  "manufacturer": "得力集团有限公司",
  "minStock": 10,
  "maxStock": 100,
  "safetyStock": 20,
  "price": 25.50,
  "description": "A4打印纸，适用于日常办公打印",
  "status": 1
}
```

### 返回数据

**类型**: `MaterialVO`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 耗材ID |
| materialName | String | 耗材名称 |
| materialCode | String | 耗材编码 |
| categoryId | Long | 分类ID |
| categoryName | String | 分类名称（关联查询） |
| specification | String | 规格型号 |
| unit | String | 计量单位 |
| brand | String | 品牌 |
| manufacturer | String | 生产厂家 |
| minStock | Integer | 最小库存量 |
| maxStock | Integer | 最大库存量 |
| safetyStock | Integer | 安全库存量 |
| price | BigDecimal | 单价 |
| description | String | 耗材描述 |
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
    "materialName": "A4打印纸",
    "materialCode": "MAT_001",
    "categoryId": 2,
    "categoryName": "纸张",
    "specification": "70g/m² 500张/包",
    "unit": "包",
    "brand": "得力",
    "manufacturer": "得力集团有限公司",
    "minStock": 10,
    "maxStock": 100,
    "safetyStock": 20,
    "price": 25.50,
    "description": "A4打印纸，适用于日常办公打印",
    "status": 1,
    "createTime": "2026-01-09T10:00:00",
    "updateTime": "2026-01-09T10:00:00",
    "createBy": "admin",
    "updateBy": "admin"
  }
}
```

---

## 2. 更新耗材

### 接口信息
- **接口路径**: `/api/material/{id}`
- **HTTP方法**: `PUT`
- **功能描述**: 更新耗材信息
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 耗材ID |

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| materialName | String | 是 | 耗材名称 |
| materialCode | String | 否 | 耗材编码 |
| categoryId | Long | 否 | 分类ID |
| specification | String | 否 | 规格型号 |
| unit | String | 是 | 计量单位 |
| brand | String | 否 | 品牌 |
| manufacturer | String | 否 | 生产厂家 |
| minStock | Integer | 否 | 最小库存量 |
| maxStock | Integer | 否 | 最大库存量 |
| safetyStock | Integer | 否 | 安全库存量 |
| price | BigDecimal | 否 | 单价 |
| description | String | 否 | 耗材描述 |
| status | Integer | 否 | 状态（0-禁用，1-启用） |

### 请求示例
```json
{
  "materialName": "A4打印纸（更新）",
  "materialCode": "MAT_001",
  "categoryId": 2,
  "specification": "80g/m² 500张/包",
  "unit": "包",
  "brand": "得力",
  "manufacturer": "得力集团有限公司",
  "minStock": 15,
  "maxStock": 120,
  "safetyStock": 25,
  "price": 28.00,
  "description": "A4打印纸，适用于日常办公打印",
  "status": 1
}
```

### 返回数据

**类型**: `MaterialVO`

同创建接口的返回数据结构。

---

## 3. 删除耗材

### 接口信息
- **接口路径**: `/api/material/{id}`
- **HTTP方法**: `DELETE`
- **功能描述**: 删除耗材（逻辑删除）
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 耗材ID |

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

## 4. 获取耗材详情

### 接口信息
- **接口路径**: `/api/material/{id}`
- **HTTP方法**: `GET`
- **功能描述**: 根据ID获取耗材详情
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 耗材ID |

### 返回数据

**类型**: `MaterialVO`

同创建接口的返回数据结构。

---

## 5. 分页查询耗材列表

### 接口信息
- **接口路径**: `/api/material/page`
- **HTTP方法**: `GET`
- **功能描述**: 分页查询耗材列表，支持多条件筛选
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 查询参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 是 | 当前页码（从1开始） |
| size | Integer | 是 | 每页大小 |
| materialName | String | 否 | 耗材名称（模糊查询） |
| materialCode | String | 否 | 耗材编码（模糊查询） |
| categoryId | Long | 否 | 分类ID（精确查询） |
| brand | String | 否 | 品牌（模糊查询） |
| manufacturer | String | 否 | 生产厂家（模糊查询） |
| status | Integer | 否 | 状态（0-禁用，1-启用） |
| startTime | String | 否 | 创建时间开始（格式：yyyy-MM-dd HH:mm:ss） |
| endTime | String | 否 | 创建时间结束（格式：yyyy-MM-dd HH:mm:ss） |

### 请求示例
```
GET /api/material/page?current=1&size=10&materialName=纸&categoryId=2&status=1
```

### 返回数据

**类型**: `Page<MaterialVO>`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | List<MaterialVO> | 耗材列表 |
| total | Long | 总记录数 |
| size | Integer | 每页大小 |
| current | Integer | 当前页码 |
| pages | Integer | 总页数 |

### 返回示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 1,
        "materialName": "A4打印纸",
        "materialCode": "MAT_001",
        "categoryId": 2,
        "categoryName": "纸张",
        "specification": "70g/m² 500张/包",
        "unit": "包",
        "brand": "得力",
        "manufacturer": "得力集团有限公司",
        "minStock": 10,
        "maxStock": 100,
        "safetyStock": 20,
        "price": 25.50,
        "description": "A4打印纸，适用于日常办公打印",
        "status": 1,
        "createTime": "2026-01-09T10:00:00",
        "updateTime": "2026-01-09T10:00:00",
        "createBy": "admin",
        "updateBy": "admin"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

## 6. 批量删除耗材

### 接口信息
- **接口路径**: `/api/material/batch`
- **HTTP方法**: `DELETE`
- **功能描述**: 批量删除耗材（逻辑删除）
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | List<Long> | 是 | 耗材ID列表 |

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

## 7. 切换耗材状态

### 接口信息
- **接口路径**: `/api/material/{id}/toggle-status`
- **HTTP方法**: `PUT`
- **功能描述**: 切换耗材的启用/禁用状态
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 耗材ID |

### 返回数据

**类型**: `Boolean`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| - | Boolean | true表示切换成功，false表示切换失败 |

---

## 8. 检查耗材编码是否存在

### 接口信息
- **接口路径**: `/api/material/check/code`
- **HTTP方法**: `GET`
- **功能描述**: 检查耗材编码是否已存在
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 查询参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| materialCode | String | 是 | 耗材编码 |
| excludeId | Long | 否 | 排除的耗材ID（用于更新时检查） |

### 返回数据

**类型**: `Boolean`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| - | Boolean | true表示编码已存在，false表示编码不存在 |

---

## 9. 根据分类ID查询耗材列表

### 接口信息
- **接口路径**: `/api/material/by-category/{categoryId}`
- **HTTP方法**: `GET`
- **功能描述**: 根据分类ID查询耗材列表（不分页）
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | Long | 是 | 分类ID |

### 查询参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 否 | 状态过滤（0-禁用，1-启用，不传则查询所有） |

### 返回数据

**类型**: `List<MaterialVO>`

同创建接口的返回数据结构（列表形式）。

---

## 10. 搜索耗材（支持多条件）

### 接口信息
- **接口路径**: `/api/material/search`
- **HTTP方法**: `GET`
- **功能描述**: 搜索耗材，支持多条件组合查询
- **权限要求**: `@PreAuthorize("hasAuthority('material')")`

### 查询参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | String | 否 | 关键词（搜索名称、编码、规格、品牌、厂家） |
| categoryId | Long | 否 | 分类ID |
| status | Integer | 否 | 状态（0-禁用，1-启用） |
| current | Integer | 否 | 当前页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |

### 请求示例
```
GET /api/material/search?keyword=纸&categoryId=2&status=1&current=1&size=10
```

### 返回数据

**类型**: `Page<MaterialVO>`

同分页查询接口的返回数据结构。

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
2. **耗材编码**: 创建耗材时如果不传 `materialCode`，系统会自动生成（格式：MAT_时间戳）
3. **分类关联**: 耗材可以关联到耗材分类，也可以不关联（categoryId为null）
4. **逻辑删除**: 删除操作为逻辑删除，不会物理删除数据
5. **状态切换**: 禁用耗材后，该耗材将不能被使用
6. **库存管理**: minStock、maxStock、safetyStock用于库存预警，实际库存需要单独管理
7. **价格精度**: price字段使用DECIMAL(10,2)类型，支持最多2位小数
8. **时间格式**: 所有时间字段使用 ISO 8601 格式（如：2026-01-09T10:00:00）
9. **分页查询**: 分页查询从第1页开始，不是第0页
10. **模糊查询**: materialName、materialCode、brand、manufacturer等字段支持模糊查询

---

## 前端开发建议

1. **表单验证**: 创建和更新时需要验证必填字段（materialName、unit）
2. **编码唯一性**: 在表单提交前调用检查编码接口
3. **分类选择**: 使用分类选择组件（CategorySelect）选择耗材分类
4. **删除确认**: 删除前需要用户确认
5. **状态切换**: 使用开关组件（Switch）进行状态切换
6. **分页加载**: 使用分页组件加载耗材列表
7. **搜索功能**: 提供多条件搜索功能，支持关键词搜索
8. **批量操作**: 支持批量删除功能
9. **数据缓存**: 耗材列表数据变化不频繁，可以适当缓存
10. **库存预警**: 可以根据minStock、maxStock、safetyStock字段实现库存预警功能
