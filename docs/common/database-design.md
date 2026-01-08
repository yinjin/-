# 高职人工智能学院实训耗材管理系统 - 数据库设计文档

## 文档信息

- **文档名称**：数据库设计文档
- **系统名称**：高职人工智能学院实训耗材管理系统
- **版本**：v1.0
- **创建日期**：2026年1月7日
- **最后更新**：2026年1月7日
- **维护人员**：开发团队

---

## 一、数据库概述

### 1.1 数据库基本信息

- **数据库名称**：haocai_management
- **数据库类型**：MySQL 8.0+
- **字符集**：utf8mb4
- **排序规则**：utf8mb4_unicode_ci
- **存储引擎**：InnoDB

### 1.2 设计原则

1. **规范化设计**：遵循第三范式（3NF），减少数据冗余
2. **性能优化**：合理设计索引，优化查询性能
3. **可扩展性**：预留扩展字段，支持业务扩展
4. **安全性**：敏感数据加密存储，防止数据泄露
5. **审计追踪**：记录关键操作日志，便于问题追溯

### 1.3 命名规范

#### 表命名规范
- 使用小写字母和下划线
- 表名使用模块前缀，如 `sys_` 表示系统模块
- 示例：`sys_user`、`material_info`、`inbound_order`

#### 字段命名规范
- 使用小写字母和下划线
- 主键统一使用 `id`
- 外键使用 `关联表_id` 格式，如 `user_id`、`role_id`
- 布尔类型字段使用 `is_` 前缀，如 `is_deleted`
- 时间字段使用 `_time` 后缀，如 `create_time`、`update_time`

#### 索引命名规范
- 普通索引：`idx_字段名`，如 `idx_username`
- 唯一索引：`uk_字段名`，如 `uk_username`
- 复合索引：`idx_字段1_字段2`，如 `idx_role_permission`

---

## 二、数据库表设计

### 2.1 用户相关表

#### 2.1.1 用户表 (sys_user)

**表说明**：存储系统用户的基本信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 用户ID（主键） |
| username | VARCHAR | 50 | 否 | - | 用户名（唯一） |
| password | VARCHAR | 255 | 否 | - | 密码（BCrypt加密） |
| name | VARCHAR | 50 | 否 | - | 真实姓名 |
| email | VARCHAR | 100 | 否 | - | 邮箱地址 |
| phone | VARCHAR | 20 | 否 | - | 手机号码 |
| avatar | VARCHAR | 255 | 是 | NULL | 头像URL |
| status | TINYINT | - | 否 | 0 | 用户状态：0-正常，1-禁用，2-锁定 |
| department_id | BIGINT | - | 是 | NULL | 部门ID（外键） |
| last_login_time | DATETIME | - | 是 | NULL | 最后登录时间 |
| create_time | DATETIME | - | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| create_by | BIGINT | - | 是 | NULL | 创建人ID |
| update_by | BIGINT | - | 是 | NULL | 更新人ID |
| remark | VARCHAR | 500 | 是 | NULL | 备注信息 |
| deleted | TINYINT | - | 否 | 0 | 逻辑删除：0-未删除，1-已删除 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `username`
- INDEX: `idx_username`, `idx_email`, `idx_phone`, `idx_department`, `idx_status`, `idx_deleted`, `idx_create_time`

**外键关系**：
- `department_id` → `sys_department.id`

**业务规则**：
- 用户名必须唯一
- 密码使用BCrypt加密存储
- 支持逻辑删除，不物理删除数据

---

#### 2.1.2 用户登录日志表 (sys_user_login_log)

**表说明**：记录用户登录日志，用于安全审计

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 日志ID（主键） |
| user_id | BIGINT | - | 否 | - | 用户ID（外键） |
| username | VARCHAR | 50 | 是 | NULL | 用户名（冗余字段，便于查询） |
| login_ip | VARCHAR | 50 | 是 | NULL | 登录IP地址 |
| login_time | DATETIME | - | 否 | CURRENT_TIMESTAMP | 登录时间 |
| login_success | TINYINT | - | 否 | - | 登录结果：1-成功，0-失败 |
| fail_reason | VARCHAR | 255 | 是 | NULL | 失败原因（登录失败时填写） |
| user_agent | VARCHAR | 500 | 是 | NULL | 用户代理信息（浏览器、设备等） |
| location | VARCHAR | 255 | 是 | NULL | 地理位置信息（可选） |
| session_id | VARCHAR | 100 | 是 | NULL | 会话ID（可选，用于关联同一登录会话的多次操作） |

**索引设计**：
- PRIMARY KEY: `id`
- INDEX: `idx_user_id`, `idx_username`, `idx_login_time`, `idx_login_success`, `idx_login_ip`

**外键关系**：
- `user_id` → `sys_user.id`

**业务规则**：
- 记录所有登录尝试（成功和失败）
- 登录失败时记录失败原因
- 支持会话追踪

---

#### 2.1.3 部门表 (sys_department)

**表说明**：存储组织架构部门信息，支持树形结构

**遵循规范**：
- 数据库设计规范-第1.1条（字段命名规范：下划线命名法）
- 数据库设计规范-第1.2条（字段类型规范：枚举类型使用VARCHAR存储）
- 数据库设计规范-第1.3条（审计字段规范：包含审计字段）
- 数据库设计规范-第1.4条（索引规范：创建必要的索引）

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 部门ID（主键） |
| name | VARCHAR | 100 | 否 | - | 部门名称 |
| code | VARCHAR | 50 | 是 | NULL | 部门编码（唯一） |
| parent_id | BIGINT | - | 是 | NULL | 父部门ID（支持树形结构） |
| level | INT | - | 否 | 1 | 部门层级（顶级为1级） |
| sort_order | INT | - | 否 | 0 | 排序（用于同级部门排序） |
| status | VARCHAR | 20 | 否 | 'NORMAL' | 状态：NORMAL-正常，DISABLED-禁用 |
| leader_id | BIGINT | - | 是 | NULL | 部门负责人ID（关联sys_user表） |
| contact_info | VARCHAR | 200 | 是 | NULL | 联系方式 |
| description | VARCHAR | 500 | 是 | NULL | 部门描述 |
| create_time | DATETIME | - | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| create_by | BIGINT | - | 是 | NULL | 创建人ID |
| update_by | BIGINT | - | 是 | NULL | 更新人ID |
| deleted | TINYINT | - | 否 | 0 | 逻辑删除：0-未删除，1-已删除 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `code`
- INDEX: `idx_parent` (parent_id)
- INDEX: `idx_level` (level)
- INDEX: `idx_status` (status)
- INDEX: `idx_leader` (leader_id)
- INDEX: `idx_code` (code)
- INDEX: `idx_deleted` (deleted)

**外键关系**：
- `parent_id` → `sys_department.id`（自关联）
- `leader_id` → `sys_user.id`

**业务规则**：
- 支持多级部门树形结构（最多10级）
- 部门编码必须唯一
- 顶级部门的 `parent_id` 为 NULL 或 0
- 状态使用枚举值存储，便于前端展示和扩展

**初始化数据**：
```sql
INSERT INTO sys_department (name, code, parent_id, level, sort_order, status, leader_id, contact_info, description) VALUES
('高职人工智能学院', 'AI_COLLEGE', NULL, 1, 0, 'NORMAL', NULL, '010-88888888', '高职人工智能学院'),
('计算机科学系', 'CS_DEPT', 1, 2, 1, 'NORMAL', NULL, '010-88888889', '计算机科学系'),
('人工智能系', 'AI_DEPT', 1, 2, 2, 'NORMAL', NULL, '010-88888890', '人工智能系'),
('软件工程教研室', 'SE_GROUP', 2, 3, 1, 'NORMAL', NULL, '010-88888891', '软件工程教研室'),
('大数据教研室', 'BD_GROUP', 2, 3, 2, 'NORMAL', NULL, '010-88888892', '大数据教研室'),
('机器学习教研室', 'ML_GROUP', 3, 3, 1, 'NORMAL', NULL, '010-88888893', '机器学习教研室'),
('计算机视觉教研室', 'CV_GROUP', 3, 3, 2, 'NORMAL', NULL, '010-88888894', '计算机视觉教研室');
```

**枚举类型映射**：
- Java枚举：`DepartmentStatus`
- 枚举值：`NORMAL`（正常）、`DISABLED`（禁用）
- 类型转换器：`DepartmentStatusConverter`

---

#### 2.1.4 角色表 (sys_role)

**表说明**：存储系统角色信息，用于权限控制

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 角色ID（主键） |
| role_name | VARCHAR | 100 | 否 | - | 角色名称 |
| role_code | VARCHAR | 50 | 否 | - | 角色编码（唯一） |
| description | VARCHAR | 255 | 是 | NULL | 角色描述 |
| status | TINYINT | - | 否 | 1 | 状态：1-正常，0-禁用 |
| create_time | DATETIME | - | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| create_by | BIGINT | - | 是 | NULL | 创建人ID |
| update_by | BIGINT | - | 是 | NULL | 更新人ID |
| deleted | TINYINT | - | 否 | 0 | 逻辑删除：0-未删除，1-已删除 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `role_code`
- INDEX: `idx_status`, `idx_deleted`

**业务规则**：
- 角色编码必须唯一
- 支持逻辑删除
- 一个用户可以拥有多个角色

---

#### 2.1.5 权限表 (sys_permission)

**表说明**：存储系统权限信息，支持树形结构

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 权限ID（主键） |
| permission_name | VARCHAR | 100 | 否 | - | 权限名称 |
| permission_code | VARCHAR | 100 | 否 | - | 权限编码（唯一） |
| type | VARCHAR | 20 | 否 | - | 权限类型：menu/button/api |
| parent_id | BIGINT | - | 是 | NULL | 父权限ID（外键） |
| path | VARCHAR | 255 | 是 | NULL | 路由路径 |
| component | VARCHAR | 255 | 是 | NULL | 组件路径 |
| icon | VARCHAR | 100 | 是 | NULL | 图标 |
| sort_order | INT | - | 否 | 0 | 排序 |
| status | TINYINT | - | 否 | 1 | 状态：1-正常，0-禁用 |
| create_time | DATETIME | - | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| create_by | BIGINT | - | 是 | NULL | 创建人ID |
| update_by | BIGINT | - | 是 | NULL | 更新人ID |
| deleted | TINYINT | - | 否 | 0 | 逻辑删除：0-未删除，1-已删除 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `permission_code`
- INDEX: `idx_parent`, `idx_type`, `idx_status`, `idx_deleted`

**外键关系**：
- `parent_id` → `sys_permission.id`（自关联）

**业务规则**：
- 权限编码必须唯一
- 支持树形结构（菜单权限）
- 权限类型：menu（菜单）、button（按钮）、api（接口）
- 支持逻辑删除

---

#### 2.1.6 角色权限关联表 (sys_role_permission)

**表说明**：角色与权限的多对多关联关系

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 关联ID（主键） |
| role_id | BIGINT | - | 否 | - | 角色ID（外键） |
| permission_id | BIGINT | - | 否 | - | 权限ID（外键） |
| create_time | DATETIME | - | 否 | CURRENT_TIMESTAMP | 创建时间 |
| create_by | BIGINT | - | 是 | NULL | 创建人ID |
| deleted | TINYINT | - | 否 | 0 | 逻辑删除：0-未删除，1-已删除 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_role_permission` (role_id, permission_id)
- INDEX: `idx_role`, `idx_permission`, `idx_deleted`

**外键关系**：
- `role_id` → `sys_role.id`
- `permission_id` → `sys_permission.id`

**业务规则**：
- 同一角色不能重复关联同一权限
- 支持逻辑删除

---

#### 2.1.7 用户角色关联表 (sys_user_role)

**表说明**：用户与角色的多对多关联关系

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 关联ID（主键） |
| user_id | BIGINT | - | 否 | - | 用户ID（外键） |
| role_id | BIGINT | - | 否 | - | 角色ID（外键） |
| create_time | DATETIME | - | 否 | CURRENT_TIMESTAMP | 创建时间 |
| create_by | BIGINT | - | 是 | NULL | 创建人ID |
| deleted | TINYINT | - | 否 | 0 | 逻辑删除：0-未删除，1-已删除 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_user_role` (user_id, role_id)
- INDEX: `idx_user`, `idx_role`, `idx_deleted`

**外键关系**：
- `user_id` → `sys_user.id`
- `role_id` → `sys_role.id`

**业务规则**：
- 同一用户不能重复关联同一角色
- 一个用户可以拥有多个角色
- 支持逻辑删除

---

### 2.2 耗材相关表

#### 2.2.1 耗材分类表 (material_category)

**表说明**：存储耗材分类信息，支持多级分类

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 分类ID（主键） |
| category_name | VARCHAR | 50 | 否 | - | 分类名称 |
| category_code | VARCHAR | 50 | 否 | - | 分类编码 |
| parent_id | BIGINT | - | 是 | 0 | 父分类ID，0表示顶级分类 |
| level | INT | - | 是 | 1 | 分类层级 |
| sort_order | INT | - | 是 | 0 | 排序 |
| description | VARCHAR | 200 | 是 | NULL | 分类描述 |
| status | TINYINT | - | 是 | 1 | 状态：1-正常，0-禁用 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
- PRIMARY KEY: `id`
- INDEX: `idx_parent_id`, `idx_category_code`, `idx_sort_order`

**外键关系**：
- `parent_id` → `material_category.id`（自关联）

**业务规则**：
- 支持多级分类树形结构
- 分类编码必须唯一
- 顶级分类的 `parent_id` 为 0

---

#### 2.2.2 耗材信息表 (material_info)

**表说明**：存储耗材的基本信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 耗材ID（主键） |
| material_code | VARCHAR | 50 | 否 | - | 耗材编码（唯一） |
| material_name | VARCHAR | 100 | 否 | - | 耗材名称 |
| category_id | BIGINT | - | 否 | - | 分类ID（外键） |
| specification | VARCHAR | 200 | 是 | NULL | 规格型号 |
| unit | VARCHAR | 20 | 否 | - | 单位（个、台、套等） |
| unit_price | DECIMAL | 10,2 | 是 | NULL | 单价 |
| supplier_id | BIGINT | - | 是 | NULL | 供应商ID（外键） |
| shelf_life | INT | - | 是 | NULL | 保质期（天） |
| barcode | VARCHAR | 100 | 是 | NULL | 条形码 |
| qr_code | VARCHAR | 200 | 是 | NULL | 二维码 |
| image_url | VARCHAR | 500 | 是 | NULL | 图片URL |
| description | TEXT | - | 是 | NULL | 描述 |
| technical_parameters | TEXT | - | 是 | NULL | 技术参数 |
| usage_instructions | TEXT | - | 是 | NULL | 使用说明 |
| storage_requirements | VARCHAR | 500 | 是 | NULL | 存储要求 |
| status | TINYINT | - | 是 | 1 | 状态：1-正常，0-停用，2-报废 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `material_code`
- INDEX: `idx_category_id`, `idx_supplier_id`, `idx_barcode`, `idx_status`

**外键关系**：
- `category_id` → `material_category.id`
- `supplier_id` → `supplier_info.id`

**业务规则**：
- 耗材编码必须唯一
- 必须关联到有效的分类
- 支持关联多个供应商

---

#### 2.2.3 供应商信息表 (supplier_info)

**表说明**：存储供应商的基本信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 供应商ID（主键） |
| supplier_code | VARCHAR | 50 | 否 | - | 供应商编码（唯一） |
| supplier_name | VARCHAR | 100 | 否 | - | 供应商名称 |
| contact_person | VARCHAR | 50 | 是 | NULL | 联系人 |
| phone | VARCHAR | 20 | 是 | NULL | 联系电话 |
| email | VARCHAR | 100 | 是 | NULL | 邮箱 |
| address | VARCHAR | 200 | 是 | NULL | 地址 |
| business_license | VARCHAR | 200 | 是 | NULL | 营业执照 |
| tax_number | VARCHAR | 50 | 是 | NULL | 税号 |
| bank_account | VARCHAR | 100 | 是 | NULL | 银行账号 |
| bank_name | VARCHAR | 100 | 是 | NULL | 开户行 |
| credit_rating | TINYINT | - | 是 | 5 | 信用等级（1-10） |
| cooperation_status | TINYINT | - | 是 | 1 | 合作状态：1-合作中，0-已终止 |
| description | TEXT | - | 是 | NULL | 供应商描述 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `supplier_code`
- INDEX: `idx_supplier_name`, `idx_credit_rating`

**业务规则**：
- 供应商编码必须唯一
- 信用等级范围：1-10

---

#### 2.2.4 库存表 (material_inventory)

**表说明**：存储耗材的库存信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 库存ID（主键） |
| material_id | BIGINT | - | 否 | - | 耗材ID（外键，唯一） |
| quantity | INT | - | 否 | 0 | 当前库存数量 |
| available_quantity | INT | - | 否 | 0 | 可用库存数量（扣除已申请未出库数量） |
| safe_quantity | INT | - | 是 | 10 | 安全库存阈值 |
| max_quantity | INT | - | 是 | 1000 | 最大库存阈值 |
| warehouse | VARCHAR | 50 | 是 | '主仓库' | 仓库名称 |
| location | VARCHAR | 100 | 是 | NULL | 存放位置 |
| last_in_time | DATETIME | - | 是 | NULL | 最后入库时间 |
| last_out_time | DATETIME | - | 是 | NULL | 最后出库时间 |
| total_in_quantity | INT | - | 是 | 0 | 累计入库数量 |
| total_out_quantity | INT | - | 是 | 0 | 累计出库数量 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `material_id`
- INDEX: `idx_warehouse`, `idx_location`, `idx_available_quantity`

**外键关系**：
- `material_id` → `material_info.id` ON DELETE CASCADE

**业务规则**：
- 每个耗材只能有一条库存记录
- 可用库存 ≤ 当前库存
- 库存数量不能为负数
- 删除耗材时级联删除库存记录

---

### 2.3 入库相关表

#### 2.3.1 入库单表 (inbound_order)

**表说明**：存储入库单的基本信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 入库单ID（主键） |
| order_no | VARCHAR | 50 | 否 | - | 入库单号（唯一） |
| inbound_date | DATE | - | 否 | - | 入库日期 |
| supplier_id | BIGINT | - | 是 | NULL | 供应商ID（外键） |
| handler_id | BIGINT | - | 否 | - | 经办人ID（外键） |
| checker_id | BIGINT | - | 是 | NULL | 验收人ID（外键） |
| total_quantity | INT | - | 是 | 0 | 总数量 |
| total_amount | DECIMAL | 12,2 | 是 | 0 | 总金额 |
| status | TINYINT | - | 是 | 0 | 状态：0-待审核，1-已审核，2-已拒绝，3-已完成 |
| audit_time | DATETIME | - | 是 | NULL | 审核时间 |
| auditor_id | BIGINT | - | 是 | NULL | 审核人ID（外键） |
| audit_opinion | TEXT | - | 是 | NULL | 审核意见 |
| remark | TEXT | - | 是 | NULL | 备注 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `order_no`
- INDEX: `idx_inbound_date`, `idx_status`, `idx_handler_id`, `idx_supplier_id`

**外键关系**：
- `supplier_id` → `supplier_info.id`
- `handler_id` → `sys_user.id`
- `checker_id` → `sys_user.id`
- `auditor_id` → `sys_user.id`

**业务规则**：
- 入库单号必须唯一
- 审核通过后才能更新库存
- 已审核的入库单不能修改

---

#### 2.3.2 入库明细表 (inbound_detail)

**表说明**：存储入库单的明细信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 明细ID（主键） |
| inbound_order_id | BIGINT | - | 否 | - | 入库单ID（外键） |
| material_id | BIGINT | - | 否 | - | 耗材ID（外键） |
| quantity | INT | - | 否 | - | 数量 |
| unit_price | DECIMAL | 10,2 | 是 | NULL | 单价 |
| total_price | DECIMAL | 12,2 | 是 | NULL | 总价 |
| batch_no | VARCHAR | 50 | 是 | NULL | 批次号 |
| production_date | DATE | - | 是 | NULL | 生产日期 |
| expiry_date | DATE | - | 是 | NULL | 过期日期 |
| supplier_batch_no | VARCHAR | 100 | 是 | NULL | 供应商批次号 |
| quality_certificate | VARCHAR | 200 | 是 | NULL | 质检证书 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引设计**：
- PRIMARY KEY: `id`
- INDEX: `idx_inbound_order_id`, `idx_material_id`, `idx_batch_no`

**外键关系**：
- `inbound_order_id` → `inbound_order.id` ON DELETE CASCADE
- `material_id` → `material_info.id`

**业务规则**：
- 删除入库单时级联删除明细
- 数量必须大于0

---

### 2.4 出库相关表

#### 2.4.1 出库单表 (outbound_order)

**表说明**：存储出库单的基本信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 出库单ID（主键） |
| order_no | VARCHAR | 50 | 否 | - | 出库单号（唯一） |
| outbound_date | DATE | - | 是 | NULL | 出库日期 |
| applicant_id | BIGINT | - | 否 | - | 申请人ID（外键） |
| applicant_dept_id | BIGINT | - | 是 | NULL | 申请人部门ID（外键） |
| handler_id | BIGINT | - | 是 | NULL | 处理人ID（外键） |
| approver_id | BIGINT | - | 是 | NULL | 审批人ID（外键） |
| total_quantity | INT | - | 是 | 0 | 总数量 |
| total_amount | DECIMAL | 12,2 | 是 | 0 | 总金额 |
| usage_type | TINYINT | - | 否 | - | 用途类型：1-教学用，2-科研用，3-竞赛用，4-其他 |
| usage_detail | TEXT | - | 否 | - | 用途详情（JSON格式） |
| expected_return_date | DATE | - | 是 | NULL | 预计归还日期 |
| actual_return_date | DATE | - | 是 | NULL | 实际归还日期 |
| status | TINYINT | - | 是 | 0 | 状态：0-待审批，1-已批准，2-已拒绝，3-待出库，4-已出库，5-已归还，6-已损坏，7-已丢失 |
| approval_time | DATETIME | - | 是 | NULL | 审批时间 |
| approval_opinion | TEXT | - | 是 | NULL | 审批意见 |
| outbound_time | DATETIME | - | 是 | NULL | 出库时间 |
| remark | TEXT | - | 是 | NULL | 备注 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `order_no`
- INDEX: `idx_outbound_date`, `idx_applicant_id`, `idx_status`, `idx_usage_type`, `idx_expected_return_date`

**外键关系**：
- `applicant_id` → `sys_user.id`
- `applicant_dept_id` → `sys_department.id`
- `handler_id` → `sys_user.id`
- `approver_id` → `sys_user.id`

**业务规则**：
- 出库单号必须唯一
- 审批通过后才能出库
- 出库时检查库存是否充足
- 用途详情存储JSON格式数据

---

#### 2.4.2 出库明细表 (outbound_detail)

**表说明**：存储出库单的明细信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 明细ID（主键） |
| outbound_order_id | BIGINT | - | 否 | - | 出库单ID（外键） |
| material_id | BIGINT | - | 否 | - | 耗材ID（外键） |
| quantity | INT | - | 否 | - | 数量 |
| unit_price | DECIMAL | 10,2 | 是 | NULL | 单价 |
| total_price | DECIMAL | 12,2 | 是 | NULL | 总价 |
| return_quantity | INT | - | 是 | 0 | 已归还数量 |
| return_status | TINYINT | - | 是 | 0 | 归还状态：0-未归还，1-部分归还，2-已归还，3-已损坏，4-已丢失 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
- PRIMARY KEY: `id`
- INDEX: `idx_outbound_order_id`, `idx_material_id`, `idx_return_status`

**外键关系**：
- `outbound_order_id` → `outbound_order.id` ON DELETE CASCADE
- `material_id` → `material_info.id`

**业务规则**：
- 删除出库单时级联删除明细
- 已归还数量 ≤ 出库数量
- 归还数量等于出库数量时，归还状态为"已归还"

---

#### 2.4.3 归还记录表 (return_record)

**表说明**：存储耗材归还记录

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 归还记录ID（主键） |
| outbound_detail_id | BIGINT | - | 否 | - | 出库明细ID（外键） |
| return_date | DATE | - | 否 | - | 归还日期 |
| return_quantity | INT | - | 否 | - | 归还数量 |
| return_status | TINYINT | - | 否 | - | 归还状态：1-完好，2-损坏，3-丢失 |
| damage_description | TEXT | - | 是 | NULL | 损坏描述 |
| handler_id | BIGINT | - | 是 | NULL | 处理人ID（外键） |
| remark | TEXT | - | 是 | NULL | 备注 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引设计**：
- PRIMARY KEY: `id`
- INDEX: `idx_outbound_detail_id`, `idx_return_date`, `idx_return_status`, `idx_handler_id`

**外键关系**：
- `outbound_detail_id` → `outbound_detail.id` ON DELETE CASCADE
- `handler_id` → `sys_user.id`

**业务规则**：
- 删除出库明细时级联删除归还记录
- 归还数量必须大于0
- 损坏或丢失时必须填写损坏描述

---

### 2.5 盘点相关表

#### 2.5.1 盘点单表 (inventory_check)

**表说明**：存储盘点单的基本信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 盘点单ID（主键） |
| check_no | VARCHAR | 50 | 否 | - | 盘点单号（唯一） |
| check_date | DATE | - | 否 | - | 盘点日期 |
| checker_id | BIGINT | - | 否 | - | 盘点人ID（外键） |
| checker_dept_id | BIGINT | - | 是 | NULL | 盘点人部门ID（外键） |
| check_type | TINYINT | - | 是 | 1 | 盘点类型：1-全盘，2-抽盘，3-专项盘 |
| check_scope | TEXT | - | 是 | NULL | 盘点范围（JSON格式） |
| status | TINYINT | - | 是 | 0 | 状态：0-进行中，1-已完成，2-已作废 |
| total_items | INT | - | 是 | 0 | 盘点总项数 |
| discrepancy_items | INT | - | 是 | 0 | 差异项数 |
| remark | TEXT | - | 是 | NULL | 备注 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| complete_time | DATETIME | - | 是 | NULL | 完成时间 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `check_no`
- INDEX: `idx_check_date`, `idx_checker_id`, `idx_status`

**外键关系**：
- `checker_id` → `sys_user.id`
- `checker_dept_id` → `sys_department.id`

**业务规则**：
- 盘点单号必须唯一
- 盘点范围存储JSON格式数据
- 完成盘点后才能调整库存

---

#### 2.5.2 盘点明细表 (inventory_check_detail)

**表说明**：存储盘点单的明细信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 明细ID（主键） |
| inventory_check_id | BIGINT | - | 否 | - | 盘点单ID（外键） |
| material_id | BIGINT | - | 否 | - | 耗材ID（外键） |
| system_quantity | INT | - | 否 | - | 系统库存数量 |
| actual_quantity | INT | - | 否 | - | 实际盘点数量 |
| diff_quantity | INT | - | 否 | - | 差异数量 |
| diff_amount | DECIMAL | 12,2 | 是 | NULL | 差异金额 |
| check_status | TINYINT | - | 是 | 0 | 盘点状态：0-待确认，1-已确认，2-已调整 |
| adjust_quantity | INT | - | 是 | 0 | 调整数量 |
| adjust_reason | TEXT | - | 是 | NULL | 调整原因 |
| adjust_time | DATETIME | - | 是 | NULL | 调整时间 |
| adjuster_id | BIGINT | - | 是 | NULL | 调整人ID（外键） |
| remark | TEXT | - | 是 | NULL | 备注 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
- PRIMARY KEY: `id`
- INDEX: `idx_inventory_check_id`, `idx_material_id`, `idx_check_status`, `idx_adjuster_id`

**外键关系**：
- `inventory_check_id` → `inventory_check.id` ON DELETE CASCADE
- `material_id` → `material_info.id`
- `adjuster_id` → `sys_user.id`

**业务规则**：
- 删除盘点单时级联删除明细
- 差异数量 = 实际盘点数量 - 系统库存数量
- 调整数量必须等于差异数量

---

### 2.6 系统日志表

#### 2.6.1 操作日志表 (sys_log)

**表说明**：记录系统操作日志，用于审计追踪

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 日志ID（主键） |
| user_id | BIGINT | - | 是 | NULL | 用户ID（外键） |
| username | VARCHAR | 50 | 是 | NULL | 用户名 |
| real_name | VARCHAR | 50 | 是 | NULL | 真实姓名 |
| operation | VARCHAR | 100 | 是 | NULL | 操作内容 |
| method | VARCHAR | 200 | 是 | NULL | 请求方法 |
| params | TEXT | - | 是 | NULL | 请求参数 |
| result | TEXT | - | 是 | NULL | 返回结果 |
| ip | VARCHAR | 50 | 是 | NULL | IP地址 |
| user_agent | TEXT | - | 是 | NULL | 用户代理 |
| status | TINYINT | - | 是 | NULL | 状态：1-成功，0-失败 |
| error_msg | TEXT | - | 是 | NULL | 错误信息 |
| execute_time | BIGINT | - | 是 | NULL | 执行时长（毫秒） |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引设计**：
- PRIMARY KEY: `id`
- INDEX: `idx_user_id`, `idx_create_time`, `idx_operation`, `idx_status`, `idx_ip`

**外键关系**：
- `user_id` → `sys_user.id`

**业务规则**：
- 记录所有关键操作
- 记录操作结果和执行时间
- 支持按用户、时间、操作类型查询

---

#### 2.6.2 系统配置表 (sys_config)

**表说明**：存储系统配置信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | 否 | AUTO_INCREMENT | 配置ID（主键） |
| config_key | VARCHAR | 100 | 否 | - | 配置键（唯一） |
| config_name | VARCHAR | 100 | 否 | - | 配置名称 |
| config_value | TEXT | - | 是 | NULL | 配置值 |
| config_type | VARCHAR | 50 | 是 | 'text' | 配置类型 |
| description | VARCHAR | 200 | 是 | NULL | 配置描述 |
| create_time | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
- PRIMARY KEY: `id`
- UNIQUE KEY: `config_key`

**业务规则**：
- 配置键必须唯一
- 支持多种配置类型（text、number、boolean、json等）

---

## 三、数据库索引设计

### 3.1 索引设计原则

1. **主键索引**：所有表都有自增主键 `id`
2. **唯一索引**：业务唯一字段（如用户名、编码）创建唯一索引
3. **普通索引**：常用查询字段创建索引
4. **复合索引**：多条件查询字段创建复合索引
5. **外键索引**：所有外键字段创建索引

### 3.2 索引使用建议

1. **避免过度索引**：索引会占用存储空间，影响写入性能
2. **定期维护索引**：使用 `ANALYZE TABLE` 更新索引统计信息
3. **监控慢查询**：使用 `EXPLAIN` 分析查询计划，优化索引
4. **索引覆盖查询**：尽量使用覆盖索引，减少回表查询

---

## 四、数据库优化建议

### 4.1 查询优化

1. **使用分页查询**：避免一次性查询大量数据
2. **避免 SELECT ***：只查询需要的字段
3. **使用 JOIN 代替子查询**：提高查询效率
4. **合理使用索引**：确保查询使用到索引

### 4.2 性能优化

1. **使用连接池**：配置合理的连接池大小（HikariCP）
2. **启用查询缓存**：对热点数据启用查询缓存
3. **使用 Redis 缓存**：缓存热点数据，减少数据库查询
4. **定期归档历史数据**：减少表数据量，提高查询性能

### 4.3 存储优化

1. **选择合适的数据类型**：使用最小的数据类型
2. **使用 NOT NULL**：减少 NULL 值的存储空间
3. **使用 TEXT 类型**：大文本字段使用 TEXT 类型
4. **定期清理日志**：定期清理操作日志和登录日志

### 4.4 安全优化

1. **使用参数化查询**：防止 SQL 注入
2. **敏感数据加密**：对敏感字段进行加密存储
3. **最小权限原则**：数据库用户只授予必要的权限
4. **定期备份**：每日自动备份数据库

---

## 五、数据库初始化数据

### 5.1 初始用户数据

```sql
-- 默认管理员用户
INSERT INTO sys_user (username, password, name, email, phone, status, deleted) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7jyxv5QD0yRK', '系统管理员', 'admin@haocai.com', '13800138000', 0, 0);
```

**说明**：
- 用户名：admin
- 密码：admin123（BCrypt加密）
- 状态：正常
- 未删除

### 5.2 初始角色数据

```sql
-- 默认角色
INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('管理员', 'admin', '系统管理员', 1),
('教师', 'teacher', '教师用户', 1),
('学生', 'student', '学生用户', 1),
('仓库管理员', 'warehouse', '仓库管理员', 1);
```

**说明**：
- 管理员：拥有系统所有权限
- 教师：可查询和申请耗材
- 学生：可查询和申请耗材
- 仓库管理员：管理耗材出入库

### 5.3 初始权限数据

```sql
-- 默认权限
INSERT INTO sys_permission (permission_name, permission_code, type, path, component, icon, sort_order, status) VALUES
('系统管理', 'system', 'menu', '/system', 'Layout', 'setting', 1, 1),
('用户管理', 'system:user', 'menu', '/system/user', 'system/User', 'user', 1, 1),
('角色管理', 'system:role', 'menu', '/system/role', 'system/Role', 'role', 2, 1),
('权限管理', 'system:permission', 'menu', '/system/permission', 'system/Permission', 'lock', 3, 1),
('部门管理', 'system:department', 'menu', '/system/department', 'system/Department', 'apartment', 4, 1);
```

**说明**：
- 系统管理：父菜单
- 用户管理：用户管理菜单
- 角色管理：角色管理菜单
- 权限管理：权限管理菜单
- 部门管理：部门管理菜单

### 5.4 初始角色权限关联数据

```sql
-- 关联管理员角色权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p WHERE r.role_code = 'admin';
```

**说明**：
- 管理员角色拥有所有权限

### 5.5 初始用户角色关联数据

```sql
-- 关联管理员用户角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username = 'admin' AND r.role_code = 'admin';
```

**说明**：
- admin 用户拥有管理员角色

---

## 六、数据库维护

### 6.1 备份策略

1. **全量备份**：每日凌晨执行全量备份
2. **增量备份**：每小时执行增量备份
3. **备份保留**：保留最近7天的备份
4. **异地备份**：备份文件存储到异地服务器

### 6.2 监控指标

1. **连接数监控**：监控数据库连接数
2. **慢查询监控**：监控执行时间超过1秒的查询
3. **磁盘空间监控**：监控磁盘空间使用率
4. **锁等待监控**：监控锁等待情况

### 6.3 定期维护

1. **每周执行**：`ANALYZE TABLE` 更新索引统计信息
2. **每月执行**：`OPTIMIZE TABLE` 优化表结构
3. **每季度执行**：清理历史数据
4. **每年执行**：数据库性能评估和优化

---

## 七、附录

### 7.1 数据库版本信息

- **MySQL版本**：8.0.33+
- **字符集**：utf8mb4
- **排序规则**：utf8mb4_unicode_ci
- **存储引擎**：InnoDB

### 7.2 相关文档

- [系统设计文档](./plan.md)
- [开发规范文档](./development-standards.md)
- [API接口文档](../api-docs.md)

### 7.3 变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|----------|--------|
| v1.0 | 2026-01-07 | 初始版本，从plan.md抽取数据库设计内容 | 开发团队 |

---

## 八、联系方式

如有数据库相关问题，请联系：

- **技术支持**：tech-support@haocai.com
- **数据库管理员**：dba@haocai.com
- **项目负责人**：pm@haocai.com
