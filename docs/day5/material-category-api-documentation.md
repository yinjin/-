# 耗材分类管理API文档

## 版本信息
- **版本号**: v1.1
- **更新日期**: 2026-01-09
- **更新内容**: 权限细粒度控制、功能增强

## 概述
耗材分类管理API提供了耗材分类的完整管理功能，包括分类的增删改查、树形结构查询、状态管理等。

## 权限说明

### 权限编码
| 权限编码 | 权限名称 | 说明 |
|---------|---------|------|
| material-category:view | 查看耗材分类 | 查询分类列表、详情、树形结构等 |
| material-category:create | 创建耗材分类 | 新增分类、新增子分类 |
| material-category:edit | 编辑耗材分类 | 更新分类信息、切换状态 |
| material-category:delete | 删除耗材分类 | 删除分类、批量删除 |

### 权限使用示例
```java
// 查看权限
@PreAuthorize("hasAuthority('material-category:view')")

// 创建权限
@PreAuthorize("hasAuthority('material-category:create')")

// 编辑权限
@PreAuthorize("hasAuthority('material-category:edit')")

// 删除权限
@PreAuthorize("hasAuthority('material-category:delete')")
```

## API接口列表

### 1. 创建耗材分类

**接口地址**: `POST /api/material-categories`

**权限要求**: `material-category:create`

**请求参数**:
```json
{
  "categoryName": "计算机硬件",
  "categoryCode": "A01-01",
  "parentId": 1,
  "description": "计算机硬件耗材",
  "sortOrder": 1,
  "status": 1
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryName | String | 是 | 分类名称 |
| categoryCode | String | 否 | 分类编码（不填则自动生成） |
| parentId | Long | 否 | 父分类ID（0表示顶级分类） |
| description | String | 否 | 分类描述 |
| sortOrder | Integer | 否 | 排序号 |
| status | Integer | 否 | 状态：0-禁用，1-启用 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5
}
```

**业务规则**:
1. 分类编码不能重复
2. 父分类必须存在
3. 分类层级不能超过3级
4. 自动生成分类编码（如果未提供）
5. 自动计算分类层级

---

### 2. 更新耗材分类

**接口地址**: `PUT /api/material-categories/{id}`

**权限要求**: `material-category:edit`

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

**请求参数**:
```json
{
  "categoryName": "计算机硬件",
  "categoryCode": "A01-01",
  "parentId": 1,
  "description": "计算机硬件耗材",
  "sortOrder": 1,
  "status": 1
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryName | String | 是 | 分类名称 |
| categoryCode | String | 否 | 分类编码 |
| parentId | Long | 否 | 父分类ID（可修改） |
| description | String | 否 | 分类描述 |
| sortOrder | Integer | 否 | 排序号 |
| status | Integer | 否 | 状态：0-禁用，1-启用 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**业务规则**:
1. 分类编码不能与其他分类重复
2. 不能将分类移动到自己的子分类下（循环引用检查）
3. 父分类必须存在
4. 分类层级不能超过3级
5. 自动更新分类层级

---

### 3. 删除耗材分类

**接口地址**: `DELETE /api/material-categories/{id}`

**权限要求**: `material-category:delete`

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**业务规则**:
1. 如果分类下有子分类，不能删除
2. 使用逻辑删除，不物理删除数据

---

### 4. 批量删除耗材分类

**接口地址**: `DELETE /api/material-categories/batch`

**权限要求**: `material-category:delete`

**请求参数**:
```json
{
  "ids": [1, 2, 3]
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | Array[Long] | 是 | 分类ID列表 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**业务规则**:
1. 如果分类下有子分类，不能删除
2. 使用逻辑删除，不物理删除数据
3. 使用批量操作提高性能

---

### 5. 获取耗材分类详情

**接口地址**: `GET /api/material-categories/{id}`

**权限要求**: `material-category:view`

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "categoryName": "硬件类",
    "categoryCode": "A01",
    "parentId": 0,
    "level": 1,
    "description": "硬件耗材分类",
    "sortOrder": 1,
    "status": 1,
    "createTime": "2026-01-08T10:00:00",
    "updateTime": "2026-01-08T10:00:00",
    "createBy": "system",
    "updateBy": "system"
  }
}
```

---

### 6. 获取耗材分类树形结构

**接口地址**: `GET /api/material-categories/tree`

**权限要求**: `material-category:view`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "categoryName": "硬件类",
      "categoryCode": "A01",
      "parentId": 0,
      "level": 1,
      "description": "硬件耗材分类",
      "sortOrder": 1,
      "status": 1,
      "children": [
        {
          "id": 5,
          "categoryName": "计算机硬件",
          "categoryCode": "A01-01",
          "parentId": 1,
          "level": 2,
          "description": "计算机硬件耗材",
          "sortOrder": 1,
          "status": 1,
          "children": []
        }
      ]
    }
  ]
}
```

**业务规则**:
1. 从顶级分类开始构建树形结构
2. 递归加载子分类
3. 按排序号排序

---

### 7. 根据父分类ID查询子分类列表

**接口地址**: `GET /api/material-categories/children/{parentId}`

**权限要求**: `material-category:view`

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| parentId | Long | 是 | 父分类ID（0表示顶级分类） |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 5,
      "categoryName": "计算机硬件",
      "categoryCode": "A01-01",
      "parentId": 1,
      "level": 2,
      "description": "计算机硬件耗材",
      "sortOrder": 1,
      "status": 1
    }
  ]
}
```

---

### 8. 查询所有顶级分类

**接口地址**: `GET /api/material-categories/top-level`

**权限要求**: `material-category:view`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "categoryName": "硬件类",
      "categoryCode": "A01",
      "parentId": 0,
      "level": 1,
      "description": "硬件耗材分类",
      "sortOrder": 1,
      "status": 1
    }
  ]
}
```

---

### 9. 切换分类状态

**接口地址**: `PUT /api/material-categories/{id}/toggle-status`

**权限要求**: `material-category:edit`

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**业务规则**:
1. 切换状态：0-禁用，1-启用
2. 自动更新状态

---

### 10. 检查分类编码是否存在

**接口地址**: `GET /api/material-categories/check/code`

**权限要求**: 无需登录（用于表单验证）

**查询参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryCode | String | 是 | 分类编码 |
| excludeId | Long | 否 | 排除的分类ID（更新时使用） |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": false
}
```

**说明**:
- 返回true表示编码已存在
- 返回false表示编码不存在
- excludeId用于更新时排除当前分类的编码

---

### 11. 检查分类下是否有子分类

**接口地址**: `GET /api/material-categories/{id}/has-children`

**权限要求**: `material-category:view`

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**说明**:
- 返回true表示有子分类
- 返回false表示没有子分类

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 业务异常说明

| 异常信息 | 说明 |
|---------|------|
| 分类编码已存在 | 创建或更新时编码重复 |
| 父分类不存在 | 指定的父分类ID不存在 |
| 分类层级不能超过3级 | 尝试创建超过3级的分类 |
| 不能将分类移动到自己的子分类下 | 更新父分类时产生循环引用 |
| 耗材分类不存在 | 查询的分类不存在 |
| 分类下存在子分类，不能删除 | 删除时分类下有子分类 |
| 部分耗材分类不存在 | 批量删除时部分分类不存在 |

## 更新日志

### v1.1 (2026-01-09)
- ✅ 权限细粒度控制：将权限从`material`细化为`material-category:view/create/edit/delete`
- ✅ 功能增强：支持更新分类的父分类ID
- ✅ 功能增强：添加循环引用检查
- ✅ 功能增强：添加分类层级自动更新
- ✅ 异常处理：统一使用BusinessException
- ✅ 参数更新：MaterialCategoryUpdateDTO新增parentId字段

### v1.0 (2026-01-08)
- ✅ 初始版本发布
- ✅ 基础CRUD功能
- ✅ 树形结构查询
- ✅ 状态管理
- ✅ 编码验证

---

**文档维护**: 开发团队  
**最后更新**: 2026-01-09
