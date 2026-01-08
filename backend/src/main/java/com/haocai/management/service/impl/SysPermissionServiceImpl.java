package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.dto.BatchUpdateResult;
import com.haocai.management.dto.PermissionCreateDTO;
import com.haocai.management.dto.PermissionUpdateDTO;
import com.haocai.management.dto.PermissionVO;
import com.haocai.management.entity.SysPermission;
import com.haocai.management.entity.SysRolePermission;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.mapper.SysPermissionMapper;
import com.haocai.management.mapper.SysRolePermissionMapper;
import com.haocai.management.service.ISysPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 * 
 * 遵循：开发规范-第2条（分层架构）
 * 遵循：开发规范-第7条（事务控制）
 * 遵循：开发规范-第8条（异常处理）
 * 遵循：开发规范-第9条（树形结构处理）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

    private final SysPermissionMapper permissionMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(PermissionCreateDTO dto, Long createBy) {
        // 遵循：开发规范-第6条（参数校验）
        if (dto == null) {
            throw new BusinessException(1009, "权限信息不能为空");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException(1009, "权限名称不能为空");
        }
        if (!StringUtils.hasText(dto.getCode())) {
            throw new BusinessException(1009, "权限编码不能为空");
        }
        // parentId可以为null或0（顶级权限），不需要校验
        if (dto.getParentId() == null) {
            dto.setParentId(0L); // 默认设置为顶级权限
        }
        
        // 检查父权限是否存在（如果不是顶级权限）
        if (dto.getParentId() != 0L) {
            SysPermission parentPermission = permissionMapper.selectById(dto.getParentId());
            if (parentPermission == null) {
                throw new BusinessException(1010, "父权限不存在，ID：" + dto.getParentId());
            }
        }
        
        // 检查权限编码是否已存在
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getCode, dto.getCode());
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(1015, "权限编码已存在：" + dto.getCode());
        }
        
        // 创建权限
        SysPermission permission = new SysPermission();
        permission.setName(dto.getName());
        permission.setCode(dto.getCode());
        permission.setType(dto.getType());
        permission.setParentId(dto.getParentId());
        permission.setPath(dto.getPath());
        permission.setComponent(dto.getComponent());
        permission.setIcon(dto.getIcon());
        permission.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        permission.setStatus(dto.getStatus());
        permission.setDeleted(0);
        permission.setCreateBy(createBy != null ? String.valueOf(createBy) : null);
        
        permissionMapper.insert(permission);
        log.info("创建权限成功，权限ID：{}，权限名称：{}", permission.getId(), permission.getName());
        
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(Long permissionId, PermissionUpdateDTO dto, Long updateBy) {
        // 遵循：开发规范-第6条（参数校验）
        if (permissionId == null) {
            throw new BusinessException(1009, "权限ID不能为空");
        }
        if (dto == null) {
            throw new BusinessException(1009, "权限信息不能为空");
        }
        
        // 检查权限是否存在
        SysPermission existPermission = permissionMapper.selectById(permissionId);
        if (existPermission == null) {
            throw new BusinessException(1010, "权限不存在，ID：" + permissionId);
        }
        
        // 检查是否将父权限设置为自己或自己的子权限
        if (dto.getParentId() != null && dto.getParentId().equals(permissionId)) {
            throw new BusinessException(1015, "不能将父权限设置为自己");
        }
        if (dto.getParentId() != null && dto.getParentId() != 0L) {
            // 检查是否是自己的子权限
            if (isChildPermission(permissionId, dto.getParentId())) {
                throw new BusinessException(1015, "不能将父权限设置为自己的子权限");
            }
        }
        
        // 如果修改了权限编码，检查新编码是否已存在
        if (StringUtils.hasText(dto.getCode()) && !dto.getCode().equals(existPermission.getCode())) {
            LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysPermission::getCode, dto.getCode());
            wrapper.ne(SysPermission::getId, permissionId);
            if (permissionMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(1015, "权限编码已存在：" + dto.getCode());
            }
        }
        
        // 更新权限
        SysPermission permission = new SysPermission();
        permission.setId(permissionId);
        permission.setName(dto.getName());
        permission.setCode(dto.getCode());
        permission.setType(dto.getType());
        permission.setParentId(dto.getParentId());
        permission.setPath(dto.getPath());
        permission.setComponent(dto.getComponent());
        permission.setIcon(dto.getIcon());
        permission.setSortOrder(dto.getSortOrder());
        permission.setStatus(dto.getStatus());
        permission.setUpdateBy(updateBy != null ? String.valueOf(updateBy) : null);
        
        permissionMapper.updateById(permission);
        log.info("更新权限成功，权限ID：{}", permissionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long permissionId) {
        // 遵循：开发规范-第6条（参数校验）
        if (permissionId == null) {
            throw new BusinessException(1009, "权限ID不能为空");
        }
        
        // 检查权限是否存在
        SysPermission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException(1010, "权限不存在，ID：" + permissionId);
        }
        
        // 检查是否有子权限
        LambdaQueryWrapper<SysPermission> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(SysPermission::getParentId, permissionId);
        childWrapper.eq(SysPermission::getDeleted, 0);
        long childCount = permissionMapper.selectCount(childWrapper);
        if (childCount > 0) {
            throw new BusinessException(1015, "该权限下还有" + childCount + "个子权限，无法删除");
        }
        
        // 检查是否有角色关联此权限
        LambdaQueryWrapper<SysRolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(SysRolePermission::getPermissionId, permissionId);
        long rpCount = rolePermissionMapper.selectCount(rpWrapper);
        if (rpCount > 0) {
            throw new BusinessException(1015, "该权限还被" + rpCount + "个角色使用，无法删除");
        }
        
        // 使用MyBatis-Plus的逻辑删除功能
        permissionMapper.deleteById(permissionId);
        
        log.info("删除权限成功，权限ID：{}", permissionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchUpdateResult batchDeletePermissions(List<Long> permissionIds) {
        // 遵循：开发规范-第6条（参数校验）
        if (CollectionUtils.isEmpty(permissionIds)) {
            throw new BusinessException(1009, "权限ID列表不能为空");
        }
        
        BatchUpdateResult result = new BatchUpdateResult(0, permissionIds.size());
        
        for (Long permissionId : permissionIds) {
            try {
                deletePermission(permissionId);
                result.setSuccessCount(result.getSuccessCount() + 1);
                result.setFailureCount(result.getFailureCount() - 1);
            } catch (Exception e) {
                result.addFailure(permissionId, e.getMessage());
                log.error("批量删除权限失败，权限ID：{}，错误：{}", permissionId, e.getMessage());
            }
        }
        
        log.info("批量删除权限完成，成功：{}，失败：{}", result.getSuccessCount(), result.getFailureCount());
        return result;
    }

    @Override
    public IPage<PermissionVO> getPermissionPage(Page<SysPermission> page, 
                                                 String permissionName, 
                                                 String permissionType, 
                                                 Integer status) {
        // 遵循：开发规范-第6条（参数校验）
        if (page == null) {
            throw new BusinessException(1009, "分页参数不能为空");
        }
        
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        
        // 权限名称模糊查询
        if (StringUtils.hasText(permissionName)) {
            wrapper.like(SysPermission::getName, permissionName);
        }
        
        // 权限类型查询
        if (StringUtils.hasText(permissionType)) {
            wrapper.eq(SysPermission::getType, permissionType);
        }
        
        // 状态查询
        if (status != null) {
            wrapper.eq(SysPermission::getStatus, status);
        }
        
        // 按排序号和创建时间排序
        wrapper.orderByAsc(SysPermission::getSortOrder)
               .orderByDesc(SysPermission::getCreateTime);
        
        IPage<SysPermission> permissionPage = permissionMapper.selectPage(page, wrapper);
        
        // 转换为VO
        IPage<PermissionVO> voPage = new Page<>(permissionPage.getCurrent(), permissionPage.getSize(), permissionPage.getTotal());
        List<PermissionVO> voList = permissionPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public List<PermissionVO> getPermissionTree() {
        // 查询所有启用的权限
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getStatus, 1);
        wrapper.eq(SysPermission::getDeleted, 0);
        wrapper.orderByAsc(SysPermission::getSortOrder)
               .orderByDesc(SysPermission::getCreateTime);
        
        List<SysPermission> allPermissions = permissionMapper.selectList(wrapper);
        
        // 转换为VO
        List<PermissionVO> voList = allPermissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建树形结构
        return buildPermissionTree(voList, 0L);
    }

    @Override
    public List<PermissionVO> getPermissionsByRoleId(Long roleId) {
        // 遵循：开发规范-第6条（参数校验）
        if (roleId == null) {
            throw new BusinessException(1009, "角色ID不能为空");
        }
        
        // 通过角色权限关联表查询权限
        LambdaQueryWrapper<SysRolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> rolePermissions = rolePermissionMapper.selectList(rpWrapper);
        
        if (CollectionUtils.isEmpty(rolePermissions)) {
            return new ArrayList<>();
        }
        
        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());
        
        List<SysPermission> permissions = permissionMapper.selectBatchIds(permissionIds);
        return permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> getPermissionsByUserId(Long userId) {
        // 遵循：开发规范-第6条（参数校验）
        if (userId == null) {
            throw new BusinessException(1009, "用户ID不能为空");
        }
        
        // 通过用户ID查询权限（通过角色）
        List<SysPermission> permissions = permissionMapper.selectByUserId(userId);
        
        return permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> getAllEnabledPermissions() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getStatus, 1);
        wrapper.eq(SysPermission::getDeleted, 0);
        wrapper.orderByAsc(SysPermission::getSortOrder)
               .orderByDesc(SysPermission::getCreateTime);
        
        List<SysPermission> permissions = permissionMapper.selectList(wrapper);
        
        return permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 构建权限树
     * 
     * 遵循：开发规范-第9条（树形结构处理）
     * 
     * @param permissions 所有权限列表
     * @param parentId 父权限ID
     * @return 树形结构
     */
    private List<PermissionVO> buildPermissionTree(List<PermissionVO> permissions, Long parentId) {
        List<PermissionVO> tree = new ArrayList<>();
        
        for (PermissionVO permission : permissions) {
            // 处理parentId为null的情况，将其视为顶级权限
            Long currentParentId = permission.getParentId();
            if (currentParentId == null) {
                currentParentId = 0L;
            }
            
            if (currentParentId.equals(parentId)) {
                // 递归查找子权限
                List<PermissionVO> children = buildPermissionTree(permissions, permission.getId());
                permission.setChildren(children);
                tree.add(permission);
            }
        }
        
        return tree;
    }

    /**
     * 检查是否是子权限
     * 
     * 遵循：开发规范-第9条（树形结构处理）
     * 
     * @param parentId 父权限ID
     * @param childId 子权限ID
     * @return true-是子权限，false-不是子权限
     */
    private boolean isChildPermission(Long parentId, Long childId) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getId, childId);
        wrapper.eq(SysPermission::getParentId, parentId);
        wrapper.eq(SysPermission::getDeleted, 0);
        
        return permissionMapper.selectCount(wrapper) > 0;
    }

    /**
     * 转换为VO对象
     * 
     * 遵循：开发规范-第4条（DTO/VO模式）
     */
    private PermissionVO convertToVO(SysPermission permission) {
        PermissionVO vo = new PermissionVO();
        vo.setId(permission.getId());
        vo.setName(permission.getName());
        vo.setCode(permission.getCode());
        vo.setType(permission.getType());
        vo.setParentId(permission.getParentId());
        vo.setPath(permission.getPath());
        vo.setComponent(permission.getComponent());
        vo.setIcon(permission.getIcon());
        vo.setSortOrder(permission.getSortOrder());
        vo.setStatus(permission.getStatus());
        vo.setCreateTime(permission.getCreateTime());
        vo.setUpdateTime(permission.getUpdateTime());
        return vo;
    }
}
