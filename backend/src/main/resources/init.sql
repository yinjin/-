-- ========================================
-- 好才耗材管理系统 - 数据库初始化脚本
-- ========================================
-- 创建时间: 2026-01-08
-- 数据库版本: MySQL 8.0+
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ========================================

-- 设置字符集
SET NAMES utf8mb4;

-- ========================================
-- 创建数据库
-- ========================================
-- 如果数据库存在则删除
DROP DATABASE IF EXISTS `haocai_management`;

-- 创建数据库
CREATE DATABASE `haocai_management` 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `haocai_management`;

-- 设置外键检查为0，避免删除表时的外键约束错误
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 用户管理相关表
-- ========================================

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `department_id` BIGINT COMMENT '部门ID',
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
  KEY `idx_department_id` (`department_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) COMMENT '角色描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
  `permission_type` VARCHAR(20) NOT NULL COMMENT '权限类型：menu-菜单，button-按钮',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID',
  `path` VARCHAR(200) COMMENT '路由路径',
  `component` VARCHAR(200) COMMENT '组件路径',
  `icon` VARCHAR(50) COMMENT '图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 部门表
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `department_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
  `department_code` VARCHAR(50) NOT NULL COMMENT '部门编码',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID，0表示顶级部门',
  `leader` VARCHAR(50) COMMENT '部门负责人',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `description` VARCHAR(500) COMMENT '部门描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_department_code` (`department_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 操作日志表
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `module` VARCHAR(50) NOT NULL COMMENT '模块名称',
  `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型：CREATE-创建，UPDATE-更新，DELETE-删除，QUERY-查询',
  `operation_desc` VARCHAR(200) COMMENT '操作描述',
  `request_method` VARCHAR(10) COMMENT '请求方法',
  `request_url` VARCHAR(500) COMMENT '请求URL',
  `request_params` TEXT COMMENT '请求参数',
  `response_result` TEXT COMMENT '响应结果',
  `execute_time` BIGINT COMMENT '执行时间（毫秒）',
  `operator` VARCHAR(50) COMMENT '操作人',
  `operator_id` BIGINT COMMENT '操作人ID',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-失败，1-成功',
  `error_message` TEXT COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_module` (`module`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ========================================
-- 耗材管理相关表
-- ========================================

-- 耗材分类表
DROP TABLE IF EXISTS `material_category`;
CREATE TABLE `material_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `category_code` VARCHAR(50) NOT NULL COMMENT '分类编码',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
  `description` VARCHAR(500) COMMENT '分类描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_code` (`category_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='耗材分类表';

-- 耗材表
DROP TABLE IF EXISTS `material`;
CREATE TABLE `material` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '耗材ID',
  `material_name` VARCHAR(100) NOT NULL COMMENT '耗材名称',
  `material_code` VARCHAR(50) NOT NULL COMMENT '耗材编码',
  `category_id` BIGINT COMMENT '分类ID',
  `specification` VARCHAR(200) COMMENT '规格型号',
  `unit` VARCHAR(20) NOT NULL COMMENT '计量单位',
  `brand` VARCHAR(50) COMMENT '品牌',
  `manufacturer` VARCHAR(100) COMMENT '生产厂家',
  `min_stock` INT DEFAULT 0 COMMENT '最小库存量',
  `max_stock` INT DEFAULT 0 COMMENT '最大库存量',
  `safety_stock` INT DEFAULT 0 COMMENT '安全库存量',
  `price` DECIMAL(10,2) COMMENT '单价',
  `description` VARCHAR(500) COMMENT '耗材描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_code` (`material_code`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='耗材表';

-- ========================================
-- 入库管理相关表
-- ========================================

-- 入库单表
DROP TABLE IF EXISTS `inbound_order`;
CREATE TABLE `inbound_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '入库单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '入库单号',
  `order_type` VARCHAR(20) NOT NULL COMMENT '入库类型：PURCHASE-采购，RETURN-退货，OTHER-其他',
  `supplier_id` BIGINT COMMENT '供应商ID',
  `warehouse_id` BIGINT COMMENT '仓库ID',
  `total_quantity` INT DEFAULT 0 COMMENT '总数量',
  `total_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '总金额',
  `operator_id` BIGINT COMMENT '操作人ID',
  `operator_name` VARCHAR(50) COMMENT '操作人姓名',
  `order_date` DATE COMMENT '入库日期',
  `remark` VARCHAR(500) COMMENT '备注',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已审核，REJECTED-已驳回',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_order_type` (`order_type`),
  KEY `idx_status` (`status`),
  KEY `idx_order_date` (`order_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入库单表';

-- 入库单明细表
DROP TABLE IF EXISTS `inbound_order_detail`;
CREATE TABLE `inbound_order_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` BIGINT NOT NULL COMMENT '入库单ID',
  `material_id` BIGINT NOT NULL COMMENT '耗材ID',
  `material_name` VARCHAR(100) COMMENT '耗材名称',
  `material_code` VARCHAR(50) COMMENT '耗材编码',
  `quantity` INT NOT NULL COMMENT '数量',
  `unit_price` DECIMAL(10,2) COMMENT '单价',
  `total_price` DECIMAL(12,2) COMMENT '总价',
  `batch_no` VARCHAR(50) COMMENT '批次号',
  `production_date` DATE COMMENT '生产日期',
  `expiry_date` DATE COMMENT '有效期',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入库单明细表';

-- ========================================
-- 出库管理相关表
-- ========================================

-- 出库单表
DROP TABLE IF EXISTS `outbound_order`;
CREATE TABLE `outbound_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '出库单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '出库单号',
  `order_type` VARCHAR(20) NOT NULL COMMENT '出库类型：SALE-销售，USAGE-领用，TRANSFER-调拨，OTHER-其他',
  `department_id` BIGINT COMMENT '部门ID',
  `warehouse_id` BIGINT COMMENT '仓库ID',
  `total_quantity` INT DEFAULT 0 COMMENT '总数量',
  `total_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '总金额',
  `operator_id` BIGINT COMMENT '操作人ID',
  `operator_name` VARCHAR(50) COMMENT '操作人姓名',
  `order_date` DATE COMMENT '出库日期',
  `remark` VARCHAR(500) COMMENT '备注',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已审核，REJECTED-已驳回',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_order_type` (`order_type`),
  KEY `idx_status` (`status`),
  KEY `idx_order_date` (`order_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出库单表';

-- 出库单明细表
DROP TABLE IF EXISTS `outbound_order_detail`;
CREATE TABLE `outbound_order_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` BIGINT NOT NULL COMMENT '出库单ID',
  `material_id` BIGINT NOT NULL COMMENT '耗材ID',
  `material_name` VARCHAR(100) COMMENT '耗材名称',
  `material_code` VARCHAR(50) COMMENT '耗材编码',
  `quantity` INT NOT NULL COMMENT '数量',
  `unit_price` DECIMAL(10,2) COMMENT '单价',
  `total_price` DECIMAL(12,2) COMMENT '总价',
  `batch_no` VARCHAR(50) COMMENT '批次号',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出库单明细表';

-- ========================================
-- 盘点管理相关表
-- ========================================

-- 盘点单表
DROP TABLE IF EXISTS `inventory_check`;
CREATE TABLE `inventory_check` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '盘点单ID',
  `check_no` VARCHAR(50) NOT NULL COMMENT '盘点单号',
  `check_type` VARCHAR(20) NOT NULL COMMENT '盘点类型：FULL-全盘，PARTIAL-部分盘点',
  `warehouse_id` BIGINT COMMENT '仓库ID',
  `check_date` DATE COMMENT '盘点日期',
  `operator_id` BIGINT COMMENT '操作人ID',
  `operator_name` VARCHAR(50) COMMENT '操作人姓名',
  `total_items` INT DEFAULT 0 COMMENT '盘点总项数',
  `difference_items` INT DEFAULT 0 COMMENT '差异项数',
  `remark` VARCHAR(500) COMMENT '备注',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已审核，REJECTED-已驳回',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_no` (`check_no`),
  KEY `idx_check_type` (`check_type`),
  KEY `idx_status` (`status`),
  KEY `idx_check_date` (`check_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='盘点单表';

-- 盘点单明细表
DROP TABLE IF EXISTS `inventory_check_detail`;
CREATE TABLE `inventory_check_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `check_id` BIGINT NOT NULL COMMENT '盘点单ID',
  `material_id` BIGINT NOT NULL COMMENT '耗材ID',
  `material_name` VARCHAR(100) COMMENT '耗材名称',
  `material_code` VARCHAR(50) COMMENT '耗材编码',
  `book_quantity` INT COMMENT '账面数量',
  `actual_quantity` INT COMMENT '实际数量',
  `difference_quantity` INT COMMENT '差异数量',
  `unit_price` DECIMAL(10,2) COMMENT '单价',
  `difference_amount` DECIMAL(12,2) COMMENT '差异金额',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_check_id` (`check_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='盘点单明细表';

-- ========================================
-- 初始化数据
-- ========================================

-- 初始化部门数据
INSERT INTO `sys_department` (`department_name`, `department_code`, `parent_id`, `leader`, `phone`, `email`, `sort_order`, `status`, `description`, `create_by`) VALUES
('总公司', 'HQ', 0, '张三', '010-12345678', 'hq@haocai.com', 1, 1, '总公司', 'system'),
('技术部', 'TECH', 1, '李四', '010-12345679', 'tech@haocai.com', 1, 1, '技术研发部门', 'system'),
('市场部', 'MARKET', 1, '王五', '010-12345680', 'market@haocai.com', 2, 1, '市场营销部门', 'system'),
('财务部', 'FINANCE', 1, '赵六', '010-12345681', 'finance@haocai.com', 3, 1, '财务管理部门', 'system'),
('人力资源部', 'HR', 1, '钱七', '010-12345682', 'hr@haocai.com', 4, 1, '人力资源管理部门', 'system'),
('研发一组', 'DEV1', 2, '孙八', '010-12345683', 'dev1@haocai.com', 1, 1, '研发一组', 'system'),
('研发二组', 'DEV2', 2, '周九', '010-12345684', 'dev2@haocai.com', 2, 1, '研发二组', 'system');

-- 初始化角色数据
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `status`, `create_by`) VALUES
('超级管理员', 'ROLE_ADMIN', '系统超级管理员，拥有所有权限', 1, 'system'),
('管理员', 'ROLE_MANAGER', '系统管理员，拥有大部分管理权限', 1, 'system'),
('普通用户', 'ROLE_USER', '普通用户，拥有基本操作权限', 1, 'system');

-- 初始化权限数据
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `permission_type`, `parent_id`, `path`, `component`, `icon`, `sort_order`, `status`, `create_by`) VALUES
('系统管理', 'system', 'menu', 0, '/system', NULL, 'Setting', 1, 1, 'system'),
('用户管理', 'system:user', 'menu', 1, '/system/user', 'UserManage', 'User', 1, 1, 'system'),
('角色管理', 'system:role', 'menu', 1, '/system/role', 'RoleManage', 'UserGroup', 2, 1, 'system'),
('权限管理', 'system:permission', 'menu', 1, '/system/permission', 'PermissionManage', 'Lock', 3, 1, 'system'),
('部门管理', 'system:department', 'menu', 1, '/system/department', 'DepartmentManage', 'OfficeBuilding', 4, 1, 'system'),
('耗材管理', 'material', 'menu', 0, '/material', NULL, 'Box', 2, 1, 'system'),
('入库管理', 'inbound', 'menu', 0, '/inbound', NULL, 'Download', 3, 1, 'system'),
('出库管理', 'outbound', 'menu', 0, '/outbound', NULL, 'Upload', 4, 1, 'system'),
('盘点管理', 'inventory', 'menu', 0, '/inventory', NULL, 'Document', 5, 1, 'system');

-- 初始化用户数据（密码为 admin123，使用BCrypt加密）
-- BCrypt哈希值生成方式：new BCryptPasswordEncoder().encode("admin123")
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `email`, `phone`, `department_id`, `status`, `create_by`) VALUES
('admin', '$2a$10$93WT6tLrIW34Mw/mtfT.FOcJQlxWN1Ou5WfY5TAAW8Ch2nZBtzCea', '管理员', 'admin@haocai.com', '13800138000', 1, 0, 'system'),
('manager', '$2a$10$93WT6tLrIW34Mw/mtfT.FOcJQlxWN1Ou5WfY5TAAW8Ch2nZBtzCea', '经理', 'manager@haocai.com', '13800138001', 2, 0, 'system'),
('user', '$2a$10$93WT6tLrIW34Mw/mtfT.FOcJQlxWN1Ou5WfY5TAAW8Ch2nZBtzCea', '用户', 'user@haocai.com', '13800138002', 6, 0, 'system');

-- 初始化用户角色关联数据
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(2, 2),
(3, 3);

-- 初始化角色权限关联数据（超级管理员拥有所有权限）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9);

SET FOREIGN_KEY_CHECKS = 1;
