package com.material.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.material.system.dto.RoleCreateDTO;
import com.material.system.dto.RoleUpdateDTO;
import com.material.system.entity.SysRole;
import com.material.system.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {
    
    /**
     * 创建角色
     */
    Long createRole(RoleCreateDTO dto);
    
    /**
     * 更新角色
     */
    void updateRole(RoleUpdateDTO dto);
    
    /**
     * 删除角色
     */
    void deleteRole(Long id);
    
    /**
     * 根据ID获取角色详情
     */
    RoleVO getRoleById(Long id);
    
    /**
     * 分页查询角色列表
     */
    Page<RoleVO> getRolePage(Integer current, Integer size, String roleName);
    
    /**
     * 获取所有角色列表
     */
    List<RoleVO> getAllRoles();
    
    /**
     * 为角色分配权限
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
    
    /**
     * 获取角色的权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
}
