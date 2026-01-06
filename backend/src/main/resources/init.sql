-- 高职人工智能学院实训耗材管理系统数据库初始化脚本
-- 数据库：haocai_management
-- 创建时间：2026年1月6日
-- 注意：此脚本由Spring Boot自动执行，假设数据库haocai_management已存在

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    phone VARCHAR(20) NOT NULL COMMENT '手机号码',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '用户状态：0-正常，1-禁用，2-锁定',
    department_id BIGINT COMMENT '部门ID',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    remark VARCHAR(500) COMMENT '备注信息',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_department (department_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted),
    INDEX idx_create_time (create_time)
) COMMENT '用户表';

-- 用户登录日志表
CREATE TABLE sys_user_login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名（冗余字段，便于查询）',
    login_ip VARCHAR(50) COMMENT '登录IP地址',
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    login_success TINYINT NOT NULL COMMENT '登录结果：1-成功，0-失败',
    fail_reason VARCHAR(255) COMMENT '失败原因（登录失败时填写）',
    user_agent VARCHAR(500) COMMENT '用户代理信息（浏览器、设备等）',
    location VARCHAR(255) COMMENT '地理位置信息（可选）',
    session_id VARCHAR(100) COMMENT '会话ID（可选，用于关联同一登录会话的多次操作）',
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_login_time (login_time),
    INDEX idx_login_success (login_success),
    INDEX idx_login_ip (login_ip)
) COMMENT '用户登录日志表';

-- 部门表
CREATE TABLE sys_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    name VARCHAR(100) NOT NULL COMMENT '部门名称',
    code VARCHAR(50) UNIQUE COMMENT '部门编码',
    parent_id BIGINT COMMENT '父部门ID',
    level INT NOT NULL DEFAULT 1 COMMENT '部门层级',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent (parent_id),
    INDEX idx_level (level),
    INDEX idx_status (status)
) COMMENT '部门表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    INDEX idx_role_code (role_code),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) COMMENT '角色表';

-- 权限表
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    type VARCHAR(20) NOT NULL COMMENT '权限类型：menu/button/api',
    parent_id BIGINT COMMENT '父权限ID',
    path VARCHAR(255) COMMENT '路由路径',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    INDEX idx_permission_code (permission_code),
    INDEX idx_parent (parent_id),
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) COMMENT '权限表';

-- 角色权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by BIGINT COMMENT '创建人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role (role_id),
    INDEX idx_permission (permission_id),
    INDEX idx_deleted (deleted)
) COMMENT '角色权限关联表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by BIGINT COMMENT '创建人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user (user_id),
    INDEX idx_role (role_id),
    INDEX idx_deleted (deleted)
) COMMENT '用户角色关联表';

-- 插入初始数据
-- 默认管理员用户
INSERT INTO sys_user (username, password, name, email, phone, status, deleted) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7jyxv5QD0yRK', '系统管理员', 'admin@haocai.com', '13800138000', 0, 0);

-- 默认角色
INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('管理员', 'admin', '系统管理员', 1),
('教师', 'teacher', '教师用户', 1),
('学生', 'student', '学生用户', 1),
('仓库管理员', 'warehouse', '仓库管理员', 1);

-- 默认权限
INSERT INTO sys_permission (permission_name, permission_code, type, path, component, icon, sort_order, status) VALUES
('系统管理', 'system', 'menu', '/system', 'Layout', 'setting', 1, 1),
('用户管理', 'system:user', 'menu', '/system/user', 'system/User', 'user', 1, 1),
('角色管理', 'system:role', 'menu', '/system/role', 'system/Role', 'role', 2, 1),
('权限管理', 'system:permission', 'menu', '/system/permission', 'system/Permission', 'lock', 3, 1),
('部门管理', 'system:department', 'menu', '/system/department', 'system/Department', 'apartment', 4, 1);

-- 关联管理员角色权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p WHERE r.role_code = 'admin';

-- 关联管理员用户角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username = 'admin' AND r.role_code = 'admin';
