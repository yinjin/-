package com.material.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.system.common.ResultCode;
import com.material.system.dto.RoleCreateDTO;
import com.material.system.dto.RoleUpdateDTO;
import com.material.system.entity.SysRole;
import com.material.system.entity.SysRolePermission;
import com.material.system.exception.BusinessException;
import com.material.system.mapper.SysRoleMapper;
import com.material.system.mapper.SysRolePermissionMapper;
import com.material.system.service.SysRoleService;
import com.material.system.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    
    private final SysRolePermissionMapper rolePermissionMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateDTO dto) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, dto.getRoleCode());
        if (getOne(wrapper) != null) {
            throw new BusinessException(ResultCode.ROLE_CODE_ALREADY_EXIST);
        }
        
        // 创建角色
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        
        // 设置默认值
        role.setStatus(1);
        
        // 保存角色
        save(role);
        
        return role.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateDTO dto) {
        // 检查角色是否存在
        SysRole role = getById(dto.getId());
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        
        // 检查角色编码是否被其他角色使用
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, dto.getRoleCode());
        wrapper.ne(SysRole::getId, dto.getId());
        if (getOne(wrapper) != null) {
            throw new BusinessException(ResultCode.ROLE_CODE_ALREADY_EXIST);
        }
        
        // 更新角色信息
        BeanUtils.copyProperties(dto, role);
        updateById(role);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        // 检查角色是否存在
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        
        // 删除角色
        removeById(id);
        
        // 删除角色权限关联
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, id);
        rolePermissionMapper.delete(wrapper);
    }
    
    @Override
    public RoleVO getRoleById(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        
        return roleVO;
    }
    
    @Override
    public Page<RoleVO> getRolePage(Integer current, Integer size, String roleName) {
        // 构建查询条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(roleName)) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(SysRole::getCreateTime);
        
        // 分页查询
        Page<SysRole> page = page(new Page<>(current, size), wrapper);
        
        // 转换为VO
        Page<RoleVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(role -> {
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(role, roleVO);
            return roleVO;
        }).collect(Collectors.toList()));
        
        return voPage;
    }
    
    @Override
    public List<RoleVO> getAllRoles() {
        // 查询所有启用的角色
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1);
        wrapper.orderByAsc(SysRole::getId);
        
        List<SysRole> roles = list(wrapper);
        
        // 转换为VO
        return roles.stream().map(role -> {
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(role, roleVO);
            return roleVO;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 检查角色是否存在
        SysRole role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        
        // 删除原有的角色权限关联
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        
        // 创建新的角色权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> {
                        SysRolePermission rolePermission = new SysRolePermission();
                        rolePermission.setRoleId(roleId);
                        rolePermission.setPermissionId(permissionId);
                        return rolePermission;
                    })
                    .collect(Collectors.toList());
            
            rolePermissions.forEach(rolePermissionMapper::insert);
        }
    }
    
    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        // 检查角色是否存在
        SysRole role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        
        // 查询角色的权限ID列表
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        
        List<SysRolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        
        return rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());
    }
}
