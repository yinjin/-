package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.dto.BatchUpdateResult;
import com.haocai.management.dto.RoleCreateDTO;
import com.haocai.management.dto.RoleUpdateDTO;
import com.haocai.management.dto.RoleVO;
import com.haocai.management.entity.SysRole;
import com.haocai.management.entity.SysRolePermission;
import com.haocai.management.entity.SysUserRole;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.mapper.SysRoleMapper;
import com.haocai.management.mapper.SysRolePermissionMapper;
import com.haocai.management.mapper.SysUserRoleMapper;
import com.haocai.management.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 * 
 * 遵循：开发规范-第2条（分层架构）
 * 遵循：开发规范-第7条（事务控制）
 * 遵循：开发规范-第8条（异常处理）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateDTO dto, Long createBy) {
        // 遵循：开发规范-第6条（参数校验）
        if (dto == null) {
            throw new BusinessException(1009, "角色信息不能为空");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException(1009, "角色名称不能为空");
        }
        if (!StringUtils.hasText(dto.getCode())) {
            throw new BusinessException(1009, "角色编码不能为空");
        }
        
        // 检查角色编码是否已存在
        int count = roleMapper.countByRoleCode(dto.getCode(), null);
        if (count > 0) {
            throw new BusinessException(1015, "角色编码已存在：" + dto.getCode());
        }
        
        // 创建角色
        SysRole role = new SysRole();
        role.setRoleName(dto.getName());
        role.setRoleCode(dto.getCode());
        role.setDescription(dto.getDescription());
        role.setStatus(dto.getStatus());
        role.setDeleted(0);
        role.setCreateBy(createBy);
        
        roleMapper.insert(role);
        log.info("创建角色成功，角色ID：{}，角色名称：{}", role.getId(), role.getRoleName());
        
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long roleId, RoleUpdateDTO dto, Long updateBy) {
        // 遵循：开发规范-第6条（参数校验）
        if (roleId == null) {
            throw new BusinessException(1009, "角色ID不能为空");
        }
        if (dto == null) {
            throw new BusinessException(1009, "角色信息不能为空");
        }
        
        // 检查角色是否存在
        SysRole existRole = roleMapper.selectById(roleId);
        if (existRole == null) {
            throw new BusinessException(1010, "角色不存在，ID：" + roleId);
        }
        
        // 如果修改了角色编码，检查新编码是否已存在
        if (StringUtils.hasText(dto.getCode()) && !dto.getCode().equals(existRole.getRoleCode())) {
            int count = roleMapper.countByRoleCode(dto.getCode(), roleId);
            if (count > 0) {
                throw new BusinessException(1015, "角色编码已存在：" + dto.getCode());
            }
        }
        
        // 更新角色
        SysRole role = new SysRole();
        role.setId(roleId);
        role.setRoleName(dto.getName());
        role.setRoleCode(dto.getCode());
        role.setDescription(dto.getDescription());
        role.setStatus(dto.getStatus());
        role.setUpdateBy(updateBy);
        
        roleMapper.updateById(role);
        log.info("更新角色成功，角色ID：{}", roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        // 遵循：开发规范-第6条（参数校验）
        if (roleId == null) {
            throw new BusinessException(1009, "角色ID不能为空");
        }
        
        // 检查角色是否存在
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(1010, "角色不存在，ID：" + roleId);
        }
        
        // 检查是否有用户关联此角色
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getRoleId, roleId);
        long userRoleCount = userRoleMapper.selectCount(userRoleWrapper);
        if (userRoleCount > 0) {
            throw new BusinessException(1015, "该角色下还有" + userRoleCount + "个用户，无法删除");
        }
        
        // 逻辑删除角色（使用MyBatis-Plus的removeById方法，自动处理逻辑删除）
        // 遵循：逻辑删除配置（MyBatis-Plus配置）
        roleMapper.deleteById(roleId);
        
        // 删除角色权限关联
        LambdaQueryWrapper<SysRolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(SysRolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(rpWrapper);
        
        log.info("删除角色成功，角色ID：{}", roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchUpdateResult batchDeleteRoles(List<Long> roleIds) {
        // 遵循：开发规范-第6条（参数校验）
        if (CollectionUtils.isEmpty(roleIds)) {
            throw new BusinessException(1009, "角色ID列表不能为空");
        }
        
        BatchUpdateResult result = new BatchUpdateResult(0, roleIds.size());
        
        for (Long roleId : roleIds) {
            try {
                deleteRole(roleId);
                result.setSuccessCount(result.getSuccessCount() + 1);
                result.setFailureCount(result.getFailureCount() - 1);
            } catch (Exception e) {
                result.addFailure(roleId, e.getMessage());
                log.error("批量删除角色失败，角色ID：{}，错误：{}", roleId, e.getMessage());
            }
        }
        
        log.info("批量删除角色完成，成功：{}，失败：{}", result.getSuccessCount(), result.getFailureCount());
        return result;
    }

    @Override
    public IPage<RoleVO> getRolePage(Page<SysRole> page, String roleName, Integer status) {
        // 遵循：开发规范-第6条（参数校验）
        if (page == null) {
            throw new BusinessException(1009, "分页参数不能为空");
        }
        
        IPage<SysRole> rolePage = roleMapper.selectPageByCondition(page, roleName, status);
        
        // 转换为VO
        IPage<RoleVO> voPage = new Page<>(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        List<RoleVO> voList = rolePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public List<RoleVO> getRolesByUserId(Long userId) {
        // 遵循：开发规范-第6条（参数校验）
        if (userId == null) {
            throw new BusinessException(1009, "用户ID不能为空");
        }
        
        // 通过用户角色关联表查询角色
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        if (CollectionUtils.isEmpty(userRoles)) {
            return new ArrayList<>();
        }
        
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        
        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
        return roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds, Long operatorId) {
        // 遵循：开发规范-第6条（参数校验）
        if (roleId == null) {
            throw new BusinessException(1009, "角色ID不能为空");
        }
        if (CollectionUtils.isEmpty(permissionIds)) {
            throw new BusinessException(1009, "权限ID列表不能为空");
        }
        
        // 检查角色是否存在
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(1010, "角色不存在，ID：" + roleId);
        }
        
        // 删除原有的角色权限关联
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        
        // 批量插入新的角色权限关联
        List<SysRolePermission> rolePermissions = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rp.setCreateBy(operatorId);
            rolePermissions.add(rp);
        }
        
        if (!rolePermissions.isEmpty()) {
            rolePermissions.forEach(rolePermissionMapper::insert);
        }
        
        log.info("为角色分配权限成功，角色ID：{}，权限数量：{}", roleId, permissionIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        // 遵循：开发规范-第6条（参数校验）
        if (roleId == null) {
            throw new BusinessException(1009, "角色ID不能为空");
        }
        if (CollectionUtils.isEmpty(permissionIds)) {
            throw new BusinessException(1009, "权限ID列表不能为空");
        }
        
        // 删除指定的角色权限关联
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        wrapper.in(SysRolePermission::getPermissionId, permissionIds);
        rolePermissionMapper.delete(wrapper);
        
        log.info("从角色移除权限成功，角色ID：{}，权限数量：{}", roleId, permissionIds.size());
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        // 遵循：开发规范-第6条（参数校验）
        if (roleId == null) {
            throw new BusinessException(1009, "角色ID不能为空");
        }
        
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    /**
     * 转换为VO对象
     * 
     * 遵循：开发规范-第4条（DTO/VO模式）
     */
    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}
