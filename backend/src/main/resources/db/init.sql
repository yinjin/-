-- 高职人工智能学院实训耗材管理系统数据库初始化脚本
-- 创建时间: 2026-01-04

USE material_system;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    employee_no VARCHAR(20) COMMENT '工号/学号',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    department_id BIGINT COMMENT '部门ID',
    role_id BIGINT COMMENT '角色ID',
    avatar VARCHAR(500) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    INDEX idx_username (username),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 耗材分类表
CREATE TABLE IF NOT EXISTS material_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类编码',
    description VARCHAR(500) COMMENT '分类描述',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (code),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='耗材分类表';

-- 耗材信息表
CREATE TABLE IF NOT EXISTS material_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '耗材ID',
    name VARCHAR(200) NOT NULL COMMENT '耗材名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '耗材编码',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    specification VARCHAR(200) COMMENT '规格型号',
    unit VARCHAR(20) NOT NULL COMMENT '计量单位',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    stock_quantity INT DEFAULT 0 COMMENT '库存数量',
    min_stock INT DEFAULT 0 COMMENT '最小库存预警值',
    max_stock INT DEFAULT 0 COMMENT '最大库存预警值',
    description TEXT COMMENT '耗材描述',
    image_url VARCHAR(500) COMMENT '耗材图片URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (code),
    INDEX idx_category_id (category_id),
    INDEX idx_name (name),
    FOREIGN KEY (category_id) REFERENCES material_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='耗材信息表';

-- 供应商表
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '供应商ID',
    name VARCHAR(200) NOT NULL COMMENT '供应商名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '供应商编码',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(500) COMMENT '地址',
    email VARCHAR(100) COMMENT '邮箱',
    description TEXT COMMENT '供应商描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (code),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='供应商表';

-- 采购订单表
CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
    order_date DATE NOT NULL COMMENT '订单日期',
    expected_date DATE COMMENT '预计到货日期',
    actual_date DATE COMMENT '实际到货日期',
    status VARCHAR(20) NOT NULL COMMENT '状态: PENDING-待审核, APPROVED-已审核, REJECTED-已拒绝, COMPLETED-已完成, CANCELLED-已取消',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date),
    FOREIGN KEY (supplier_id) REFERENCES supplier(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采购订单表';

-- 采购订单明细表
CREATE TABLE IF NOT EXISTS purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    material_id BIGINT NOT NULL COMMENT '耗材ID',
    quantity INT NOT NULL COMMENT '采购数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price DECIMAL(12,2) NOT NULL COMMENT '小计金额',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_material_id (material_id),
    FOREIGN KEY (order_id) REFERENCES purchase_order(id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES material_info(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采购订单明细表';

-- 领用申请表
CREATE TABLE IF NOT EXISTS requisition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    requisition_no VARCHAR(50) NOT NULL UNIQUE COMMENT '申请编号',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    department VARCHAR(100) COMMENT '申请部门',
    purpose TEXT COMMENT '用途说明',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '申请总金额',
    apply_date DATE NOT NULL COMMENT '申请日期',
    status VARCHAR(20) NOT NULL COMMENT '状态: PENDING-待审核, APPROVED-已批准, REJECTED-已拒绝, COMPLETED-已完成, CANCELLED-已取消',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_requisition_no (requisition_no),
    INDEX idx_applicant_id (applicant_id),
    INDEX idx_status (status),
    INDEX idx_apply_date (apply_date),
    FOREIGN KEY (applicant_id) REFERENCES sys_user(id),
    FOREIGN KEY (approver_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领用申请表';

-- 领用申请明细表
CREATE TABLE IF NOT EXISTS requisition_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    requisition_id BIGINT NOT NULL COMMENT '申请ID',
    material_id BIGINT NOT NULL COMMENT '耗材ID',
    quantity INT NOT NULL COMMENT '领用数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price DECIMAL(12,2) NOT NULL COMMENT '小计金额',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_requisition_id (requisition_id),
    INDEX idx_material_id (material_id),
    FOREIGN KEY (requisition_id) REFERENCES requisition(id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES material_info(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领用申请明细表';

-- 库存变动记录表
CREATE TABLE IF NOT EXISTS stock_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    material_id BIGINT NOT NULL COMMENT '耗材ID',
    change_type VARCHAR(20) NOT NULL COMMENT '变动类型: IN-入库, OUT-出库, ADJUST-调整',
    change_quantity INT NOT NULL COMMENT '变动数量（正数表示增加，负数表示减少）',
    before_quantity INT NOT NULL COMMENT '变动前数量',
    after_quantity INT NOT NULL COMMENT '变动后数量',
    reference_type VARCHAR(50) COMMENT '关联类型: PURCHASE-采购, REQUISITION-领用, ADJUST-调整',
    reference_id BIGINT COMMENT '关联ID',
    remark VARCHAR(500) COMMENT '备注',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_material_id (material_id),
    INDEX idx_change_type (change_type),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (material_id) REFERENCES material_info(id),
    FOREIGN KEY (operator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存变动记录表';

-- 系统日志表
CREATE TABLE IF NOT EXISTS sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作内容',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    time BIGINT COMMENT '执行时长(毫秒)',
    ip VARCHAR(50) COMMENT 'IP地址',
    status TINYINT COMMENT '状态: 0-失败, 1-成功',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- 仓库表
CREATE TABLE IF NOT EXISTS warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '仓库ID',
    name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '仓库编码',
    address VARCHAR(500) COMMENT '仓库地址',
    manager VARCHAR(50) COMMENT '负责人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    description VARCHAR(500) COMMENT '仓库描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (code),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓库表';

-- 库存表
CREATE TABLE IF NOT EXISTS material_stock (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    material_id BIGINT NOT NULL COMMENT '耗材ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    quantity INT DEFAULT 0 COMMENT '库存数量',
    min_stock INT DEFAULT 0 COMMENT '最小库存预警值',
    max_stock INT DEFAULT 0 COMMENT '最大库存预警值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_material_warehouse (material_id, warehouse_id),
    INDEX idx_material_id (material_id),
    INDEX idx_warehouse_id (warehouse_id),
    FOREIGN KEY (material_id) REFERENCES material_info(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存表';

-- 入库单表
CREATE TABLE IF NOT EXISTS material_inbound (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '入库单ID',
    inbound_no VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    inbound_date DATETIME NOT NULL COMMENT '入库日期',
    total_quantity DECIMAL(12,2) NOT NULL COMMENT '入库总数量',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '入库总金额',
    operator VARCHAR(50) COMMENT '操作人',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待入库, 1-已入库, 2-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_inbound_no (inbound_no),
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_inbound_date (inbound_date),
    INDEX idx_status (status),
    FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入库单表';

-- 出库单表
CREATE TABLE IF NOT EXISTS material_outbound (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '出库单ID',
    outbound_no VARCHAR(50) NOT NULL UNIQUE COMMENT '出库单号',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    outbound_date DATETIME NOT NULL COMMENT '出库日期',
    total_quantity DECIMAL(12,2) NOT NULL COMMENT '出库总数量',
    receiver VARCHAR(100) COMMENT '接收人',
    operator VARCHAR(50) COMMENT '操作人',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待出库, 1-已出库, 2-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_outbound_no (outbound_no),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_outbound_date (outbound_date),
    INDEX idx_status (status),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出库单表';

-- 插入初始管理员用户
INSERT INTO sys_user (username, password, real_name, employee_no, phone, email, department_id, role_id, avatar, status, last_login_time, login_count) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', NULL, '13800138000', 'admin@example.com', NULL, NULL, NULL, 1, NULL, 0);

-- 插入初始耗材分类
INSERT INTO material_category (name, code, description, parent_id, sort_order, status) VALUES
('电子元器件', 'ELEC', '各类电子元器件', 0, 1, 1),
('机械零件', 'MECH', '各类机械零件', 0, 2, 1),
('工具耗材', 'TOOL', '各类工具和耗材', 0, 3, 1),
('实验材料', 'LAB', '各类实验材料', 0, 4, 1);

-- 插入初始供应商
INSERT INTO supplier (name, code, contact_person, contact_phone, address, email, description, status) VALUES
('电子科技有限公司', 'SUP001', '张三', '13800138001', '北京市海淀区', 'zhangsan@elec.com', '专业电子元器件供应商', 1),
('机械制造有限公司', 'SUP002', '李四', '13800138002', '上海市浦东新区', 'lisi@mech.com', '专业机械零件供应商', 1);

-- 插入初始仓库
INSERT INTO warehouse (name, code, address, manager, contact_phone, description, status) VALUES
('主仓库', 'WH001', '北京市海淀区学院路100号', '王五', '13800138003', '学院主仓库，存放各类耗材', 1),
('实训楼仓库', 'WH002', '北京市海淀区学院路100号实训楼', '赵六', '13800138004', '实训楼专用仓库', 1);
