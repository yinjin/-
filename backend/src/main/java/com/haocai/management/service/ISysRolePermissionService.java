package com.haocai.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.entity.SysRolePermission;

/**
 * 角色权限关联Service接口
 * 
 * 功能说明：
 * 1. 继承IService提供基础CRUD操作
 * 2. 提供角色权限关联相关的业务方法
 * 
 * 遵循规范：
 * - 数据访问层规范：使用MyBatis-Plus
 * - 事务控制规范：多表操作使用事务
 * 
 * @author 开发团队
 * @since 2026-01-06
 */
public interface ISysRolePermissionService extends IService<SysRolePermission> {
}
