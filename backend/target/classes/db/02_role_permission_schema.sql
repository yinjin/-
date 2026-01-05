-- 角色和权限管理相关表结构
-- 创建时间: 2026-01-04

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    resource_type VARCHAR(20) NOT NULL COMMENT '资源类型: MENU-菜单, BUTTON-按钮, API-接口',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID，0表示顶级权限',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_permission_code (permission_code),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 插入初始角色
INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('超级管理员', 'ROLE_ADMIN', '系统超级管理员，拥有所有权限', 1),
('管理员', 'ROLE_MANAGER', '系统管理员，拥有大部分管理权限', 1),
('教师', 'ROLE_TEACHER', '教师用户，拥有教学相关权限', 1),
('学生', 'ROLE_STUDENT', '学生用户，拥有基础使用权限', 1),
('仓库管理员', 'ROLE_WAREHOUSE', '仓库管理员，负责库存管理', 1);

-- 插入初始权限
INSERT INTO sys_permission (permission_name, permission_code, resource_type, parent_id, path, component, icon, sort_order, status) VALUES
-- 用户管理
('用户管理', 'system:user', 'MENU', 0, '/system/user', 'system/user/index', 'user', 1, 1),
('用户查询', 'system:user:query', 'BUTTON', 1, NULL, NULL, NULL, 1, 1),
('用户新增', 'system:user:create', 'BUTTON', 1, NULL, NULL, NULL, 2, 1),
('用户修改', 'system:user:update', 'BUTTON', 1, NULL, NULL, NULL, 3, 1),
('用户删除', 'system:user:delete', 'BUTTON', 1, NULL, NULL, NULL, 4, 1),
('用户重置密码', 'system:user:reset', 'BUTTON', 1, NULL, NULL, NULL, 5, 1),

-- 角色管理
('角色管理', 'system:role', 'MENU', 0, '/system/role', 'system/role/index', 'role', 2, 1),
('角色查询', 'system:role:query', 'BUTTON', 7, NULL, NULL, NULL, 1, 1),
('角色新增', 'system:role:create', 'BUTTON', 7, NULL, NULL, NULL, 2, 1),
('角色修改', 'system:role:update', 'BUTTON', 7, NULL, NULL, NULL, 3, 1),
('角色删除', 'system:role:delete', 'BUTTON', 7, NULL, NULL, NULL, 4, 1),
('分配权限', 'system:role:assign', 'BUTTON', 7, NULL, NULL, NULL, 5, 1),

-- 权限管理
('权限管理', 'system:permission', 'MENU', 0, '/system/permission', 'system/permission/index', 'permission', 3, 1),
('权限查询', 'system:permission:query', 'BUTTON', 13, NULL, NULL, NULL, 1, 1),
('权限新增', 'system:permission:create', 'BUTTON', 13, NULL, NULL, NULL, 2, 1),
('权限修改', 'system:permission:update', 'BUTTON', 13, NULL, NULL, NULL, 3, 1),
('权限删除', 'system:permission:delete', 'BUTTON', 13, NULL, NULL, NULL, 4, 1),

-- 耗材分类管理
('耗材分类', 'material:category', 'MENU', 0, '/material/category', 'material/category/index', 'category', 4, 1),
('分类查询', 'material:category:query', 'BUTTON', 18, NULL, NULL, NULL, 1, 1),
('分类新增', 'material:category:create', 'BUTTON', 18, NULL, NULL, NULL, 2, 1),
('分类修改', 'material:category:update', 'BUTTON', 18, NULL, NULL, NULL, 3, 1),
('分类删除', 'material:category:delete', 'BUTTON', 18, NULL, NULL, NULL, 4, 1),

-- 耗材信息管理
('耗材信息', 'material:info', 'MENU', 0, '/material/info', 'material/info/index', 'material', 5, 1),
('耗材查询', 'material:info:query', 'BUTTON', 23, NULL, NULL, NULL, 1, 1),
('耗材新增', 'material:info:create', 'BUTTON', 23, NULL, NULL, NULL, 2, 1),
('耗材修改', 'material:info:update', 'BUTTON', 23, NULL, NULL, NULL, 3, 1),
('耗材删除', 'material:info:delete', 'BUTTON', 23, NULL, NULL, NULL, 4, 1),

-- 供应商管理
('供应商管理', 'material:supplier', 'MENU', 0, '/material/supplier', 'material/supplier/index', 'supplier', 6, 1),
('供应商查询', 'material:supplier:query', 'BUTTON', 28, NULL, NULL, NULL, 1, 1),
('供应商新增', 'material:supplier:create', 'BUTTON', 28, NULL, NULL, NULL, 2, 1),
('供应商修改', 'material:supplier:update', 'BUTTON', 28, NULL, NULL, NULL, 3, 1),
('供应商删除', 'material:supplier:delete', 'BUTTON', 28, NULL, NULL, NULL, 4, 1),

-- 仓库管理
('仓库管理', 'material:warehouse', 'MENU', 0, '/material/warehouse', 'material/warehouse/index', 'warehouse', 7, 1),
('仓库查询', 'material:warehouse:query', 'BUTTON', 33, NULL, NULL, NULL, 1, 1),
('仓库新增', 'material:warehouse:create', 'BUTTON', 33, NULL, NULL, NULL, 2, 1),
('仓库修改', 'material:warehouse:update', 'BUTTON', 33, NULL, NULL, NULL, 3, 1),
('仓库删除', 'material:warehouse:delete', 'BUTTON', 33, NULL, NULL, NULL, 4, 1),

-- 库存管理
('库存管理', 'material:stock', 'MENU', 0, '/material/stock', 'material/stock/index', 'stock', 8, 1),
('库存查询', 'material:stock:query', 'BUTTON', 38, NULL, NULL, NULL, 1, 1),
('库存调整', 'material:stock:adjust', 'BUTTON', 38, NULL, NULL, NULL, 2, 1),

-- 入库管理
('入库管理', 'material:inbound', 'MENU', 0, '/material/inbound', 'material/inbound/index', 'inbound', 9, 1),
('入库查询', 'material:inbound:query', 'BUTTON', 42, NULL, NULL, NULL, 1, 1),
('入库新增', 'material:inbound:create', 'BUTTON', 42, NULL, NULL, NULL, 2, 1),
('入库审核', 'material:inbound:audit', 'BUTTON', 42, NULL, NULL, NULL, 3, 1),
('入库删除', 'material:inbound:delete', 'BUTTON', 42, NULL, NULL, NULL, 4, 1),

-- 出库管理
('出库管理', 'material:outbound', 'MENU', 0, '/material/outbound', 'material/outbound/index', 'outbound', 10, 1),
('出库查询', 'material:outbound:query', 'BUTTON', 47, NULL, NULL, NULL, 1, 1),
('出库申请', 'material:outbound:apply', 'BUTTON', 47, NULL, NULL, NULL, 2, 1),
('出库审批', 'material:outbound:approve', 'BUTTON', 47, NULL, NULL, NULL, 3, 1),
('出库执行', 'material:outbound:execute', 'BUTTON', 47, NULL, NULL, NULL, 4, 1),
('出库删除', 'material:outbound:delete', 'BUTTON', 47, NULL, NULL, NULL, 5, 1),

-- 采购管理
('采购管理', 'material:purchase', 'MENU', 0, '/material/purchase', 'material/purchase/index', 'purchase', 11, 1),
('采购查询', 'material:purchase:query', 'BUTTON', 53, NULL, NULL, NULL, 1, 1),
('采购新增', 'material:purchase:create', 'BUTTON', 53, NULL, NULL, NULL, 2, 1),
('采购审核', 'material:purchase:audit', 'BUTTON', 53, NULL, NULL, NULL, 3, 1),
('采购删除', 'material:purchase:delete', 'BUTTON', 53, NULL, NULL, NULL, 4, 1),

-- 领用管理
('领用管理', 'material:requisition', 'MENU', 0, '/material/requisition', 'material/requisition/index', 'requisition', 12, 1),
('领用查询', 'material:requisition:query', 'BUTTON', 58, NULL, NULL, NULL, 1, 1),
('领用申请', 'material:requisition:apply', 'BUTTON', 58, NULL, NULL, NULL, 2, 1),
('领用审批', 'material:requisition:approve', 'BUTTON', 58, NULL, NULL, NULL, 3, 1),
('领用删除', 'material:requisition:delete', 'BUTTON', 58, NULL, NULL, NULL, 4, 1),

-- 统计报表
('统计报表', 'system:statistics', 'MENU', 0, '/system/statistics', 'system/statistics/index', 'chart', 13, 1),
('入库统计', 'system:statistics:inbound', 'BUTTON', 63, NULL, NULL, NULL, 1, 1),
('出库统计', 'system:statistics:outbound', 'BUTTON', 63, NULL, NULL, NULL, 2, 1),
('库存统计', 'system:statistics:stock', 'BUTTON', 63, NULL, NULL, NULL, 3, 1),

-- 系统日志
('系统日志', 'system:log', 'MENU', 0, '/system/log', 'system/log/index', 'log', 14, 1),
('日志查询', 'system:log:query', 'BUTTON', 67, NULL, NULL, NULL, 1, 1),
('日志导出', 'system:log:export', 'BUTTON', 67, NULL, NULL, NULL, 2, 1);

-- 为超级管理员分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 为管理员分配大部分权限（除了系统日志）
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE id NOT IN (67, 68);

-- 为教师分配教学相关权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 23), (3, 24), (3, 25), (3, 26), (3, 27),  -- 耗材信息查询
(3, 38), (3, 39),  -- 库存查询
(3, 47), (3, 48), (3, 49),  -- 出库查询和申请
(3, 58), (3, 59), (3, 60),  -- 领用查询和申请
(3, 63), (3, 64), (3, 65), (3, 66);  -- 统计报表

-- 为学生分配基础权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(4, 23), (4, 24),  -- 耗材信息查询
(4, 38),  -- 库存查询
(4, 47), (4, 48), (4, 49),  -- 出库查询和申请
(4, 58), (4, 59), (4, 60);  -- 领用查询和申请

-- 为仓库管理员分配仓库相关权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(5, 23), (5, 24), (5, 25), (5, 26), (5, 27),  -- 耗材信息管理
(5, 28), (5, 29), (5, 30), (5, 31), (5, 32),  -- 供应商管理
(5, 33), (5, 34), (5, 35), (5, 36), (5, 37),  -- 仓库管理
(5, 38), (5, 39),  -- 库存管理
(5, 42), (5, 43), (5, 44), (5, 45), (5, 46),  -- 入库管理
(5, 47), (5, 48), (5, 50), (5, 51), (5, 52),  -- 出库管理（审批和执行）
(5, 53), (5, 54), (5, 55), (5, 56), (5, 57),  -- 采购管理
(5, 58), (5, 59), (5, 61), (5, 62),  -- 领用管理（审批）
(5, 63), (5, 64), (5, 65), (5, 66);  -- 统计报表

-- 更新管理员用户的角色
UPDATE sys_user SET role_id = 1 WHERE username = 'admin';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
