package com.material.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.material.system.dto.PermissionCreateDTO;
import com.material.system.dto.PermissionUpdateDTO;
import com.material.system.entity.SysPermission;
import com.material.system.vo.PermissionVO;

import java.util.List;

/**
 * 权限服务接口
 */
public interface SysPermissionService extends IService<SysPermission> {
    
    /**
     * 创建权限
     */
    Long createPermission(PermissionCreateDTO dto);
    
    /**
     * 更新权限
     */
    void updatePermission(PermissionUpdateDTO dto);
    
    /**
     * 删除权限
     */
    void deletePermission(Long id);
    
    /**
     * 根据ID获取权限详情
     */
    PermissionVO getPermissionById(Long id);
    
    /**
     * 分页查询权限列表
     */
    Page<PermissionVO> getPermissionPage(Integer current, Integer size, String permissionName);
    
    /**
     * 获取所有权限列表
     */
    List<PermissionVO> getAllPermissions();
    
    /**
     * 获取权限树形结构
     */
    List<PermissionVO> getPermissionTree();
    
    /**
     * 根据角色ID获取权限列表
     */
    List<PermissionVO> getPermissionsByRoleId(Long roleId);
    
    /**
     * 根据用户ID获取权限列表
     */
    List<PermissionVO> getPermissionsByUserId(Long userId);
    
    /**
     * 根据用户ID获取权限编码列表
     */
    List<String> getPermissionCodesByUserId(Long userId);
}
