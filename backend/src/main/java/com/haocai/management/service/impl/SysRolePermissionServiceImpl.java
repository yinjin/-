package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.entity.SysRolePermission;
import com.haocai.management.mapper.SysRolePermissionMapper;
import com.haocai.management.service.ISysRolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 角色权限关联Service实现类
 * 
 * 功能说明：
 * 1. 实现角色权限关联的基础CRUD操作
 * 2. 使用MyBatis-Plus提供的通用方法
 * 
 * 遵循规范：
 * - 数据访问层规范：使用MyBatis-Plus
 * - 事务控制规范：多表操作使用事务
 * 
 * @author 开发团队
 * @since 2026-01-06
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> 
        implements ISysRolePermissionService {
}
