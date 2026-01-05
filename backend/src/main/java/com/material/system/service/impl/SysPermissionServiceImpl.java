package com.material.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.system.common.ResultCode;
import com.material.system.dto.PermissionCreateDTO;
import com.material.system.dto.PermissionUpdateDTO;
import com.material.system.entity.SysPermission;
import com.material.system.entity.SysRolePermission;
import com.material.system.entity.SysUserRole;
import com.material.system.exception.BusinessException;
import com.material.system.mapper.SysPermissionMapper;
import com.material.system.mapper.SysRolePermissionMapper;
import com.material.system.mapper.SysUserRoleMapper;
import com.material.system.service.SysPermissionService;
import com.material.system.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {
    
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysUserRoleMapper userRoleMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(PermissionCreateDTO dto) {
        // 检查权限编码是否已存在
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getPermissionCode, dto.getPermissionCode());
        if (getOne(wrapper) != null) {
            throw new BusinessException(ResultCode.PERMISSION_CODE_ALREADY_EXIST);
        }
        
        // 创建权限
        SysPermission permission = new SysPermission();
        BeanUtils.copyProperties(dto, permission);
        
        // 设置默认值
        permission.setStatus(1);
        
        // 保存权限
        save(permission);
        
        return permission.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(PermissionUpdateDTO dto) {
        // 检查权限是否存在
        SysPermission permission = getById(dto.getId());
        if (permission == null) {
            throw new BusinessException(ResultCode.PERMISSION_NOT_EXIST);
        }
        
        // 检查权限编码是否被其他权限使用
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getPermissionCode, dto.getPermissionCode());
        wrapper.ne(SysPermission::getId, dto.getId());
        if (getOne(wrapper) != null) {
            throw new BusinessException(ResultCode.PERMISSION_CODE_ALREADY_EXIST);
        }
        
        // 更新权限信息
        BeanUtils.copyProperties(dto, permission);
        updateById(permission);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        // 检查权限是否存在
        SysPermission permission = getById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.PERMISSION_NOT_EXIST);
        }
        
        // 检查是否有子权限
        LambdaQueryWrapper<SysPermission> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(SysPermission::getParentId, id);
        if (count(childWrapper) > 0) {
            throw new BusinessException(ResultCode.PERMISSION_HAS_CHILDREN);
        }
        
        // 删除权限
        removeById(id);
        
        // 删除角色权限关联
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getPermissionId, id);
        rolePermissionMapper.delete(wrapper);
    }
    
    @Override
    public PermissionVO getPermissionById(Long id) {
        SysPermission permission = getById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.PERMISSION_NOT_EXIST);
        }
        
        PermissionVO permissionVO = new PermissionVO();
        BeanUtils.copyProperties(permission, permissionVO);
        
        return permissionVO;
    }
    
    @Override
    public Page<PermissionVO> getPermissionPage(Integer current, Integer size, String permissionName) {
        // 构建查询条件
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(permissionName)) {
            wrapper.like(SysPermission::getPermissionName, permissionName);
        }
        
        // 按排序字段升序
        wrapper.orderByAsc(SysPermission::getSortOrder);
        
        // 分页查询
        Page<SysPermission> page = page(new Page<>(current, size), wrapper);
        
        // 转换为VO
        Page<PermissionVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(permission -> {
            PermissionVO permissionVO = new PermissionVO();
            BeanUtils.copyProperties(permission, permissionVO);
            return permissionVO;
        }).collect(Collectors.toList()));
        
        return voPage;
    }
    
    @Override
    public List<PermissionVO> getAllPermissions() {
        // 查询所有启用的权限
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getStatus, 1);
        wrapper.orderByAsc(SysPermission::getSortOrder);
        
        List<SysPermission> permissions = list(wrapper);
        
        // 转换为VO
        return permissions.stream().map(permission -> {
            PermissionVO permissionVO = new PermissionVO();
            BeanUtils.copyProperties(permission, permissionVO);
            return permissionVO;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<PermissionVO> getPermissionTree() {
        // 查询所有启用的权限
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getStatus, 1);
        wrapper.orderByAsc(SysPermission::getSortOrder);
        
        List<SysPermission> permissions = list(wrapper);
        
        // 转换为VO
        List<PermissionVO> permissionVOs = permissions.stream().map(permission -> {
            PermissionVO permissionVO = new PermissionVO();
            BeanUtils.copyProperties(permission, permissionVO);
            return permissionVO;
        }).collect(Collectors.toList());
        
        // 构建树形结构
        return buildPermissionTree(permissionVOs, 0L);
    }
    
    /**
     * 递归构建权限树
     */
    private List<PermissionVO> buildPermissionTree(List<PermissionVO> permissions, Long parentId) {
        List<PermissionVO> tree = new ArrayList<>();
        
        for (PermissionVO permission : permissions) {
            if (permission.getParentId().equals(parentId)) {
                // 递归查找子权限
                List<PermissionVO> children = buildPermissionTree(permissions, permission.getId());
                permission.setChildren(children);
                tree.add(permission);
            }
        }
        
        return tree;
    }
    
    @Override
    public List<PermissionVO> getPermissionsByRoleId(Long roleId) {
        // 查询角色的权限ID列表
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        
        List<SysRolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        
        if (rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());
        
        // 查询权限详情
        List<SysPermission> permissions = listByIds(permissionIds);
        
        // 转换为VO
        return permissions.stream().map(permission -> {
            PermissionVO permissionVO = new PermissionVO();
            BeanUtils.copyProperties(permission, permissionVO);
            return permissionVO;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<PermissionVO> getPermissionsByUserId(Long userId) {
        // 查询用户的角色ID列表
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId, userId);
        
        List<SysUserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        
        // 查询角色的权限ID列表
        LambdaQueryWrapper<SysRolePermission> rolePermissionWrapper = new LambdaQueryWrapper<>();
        rolePermissionWrapper.in(SysRolePermission::getRoleId, roleIds);
        
        List<SysRolePermission> rolePermissions = rolePermissionMapper.selectList(rolePermissionWrapper);
        
        if (rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());
        
        // 查询权限详情
        List<SysPermission> permissions = listByIds(permissionIds);
        
        // 转换为VO
        return permissions.stream().map(permission -> {
            PermissionVO permissionVO = new PermissionVO();
            BeanUtils.copyProperties(permission, permissionVO);
            return permissionVO;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        List<PermissionVO> permissions = getPermissionsByUserId(userId);
        
        return permissions.stream()
                .map(PermissionVO::getPermissionCode)
                .collect(Collectors.toList());
    }
}
