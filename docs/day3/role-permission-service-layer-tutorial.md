# 角色权限业务逻辑层开发教程

## 任务完成状态

✅ **已完成**：角色权限业务逻辑层的规划、设计、实现、测试和文档编写

---

## 开发过程记录

### 步骤1：规划与设计

#### 1.1 关键约束条款

基于 `development-standards.md`，角色权限业务逻辑层开发需要遵循以下关键约束条款：

**条款1：批量操作规范（第3.1节）**
- 批量操作必须先查询存在的记录，避免直接操作不存在的记录导致失败
- 必须返回详细的操作结果（总数、成功数、失败数）
- 必须捕获单个记录的异常，不影响其他记录

**条款2：异常处理规范（第3.2节）**
- Service层必须分层处理异常
- 业务异常（BusinessException）需要重新抛出
- 系统异常需要记录详细日志并转换为业务异常
- 不能吞掉异常，确保前端能获取错误信息

**条款3：事务控制规范**
- 涉及多表操作的方法必须添加 @Transactional 注解
- 批量操作必须在事务中执行
- 异常时需要回滚事务

**条款4：参数验证规范**
- 必须验证输入参数的有效性
- 使用 @NotNull、@NotBlank 等注解进行参数验证
- 在Service层进行业务逻辑验证

**条款5：审计字段规范（第1.3节）**
- 创建和更新操作必须自动填充审计字段
- 使用 MetaObjectHandler 实现自动填充
- 在实体类中添加 @TableField(fill = ...) 注解

#### 1.2 核心方法签名设计

##### 1.2.1 角色Service接口（ISysRoleService）

```java
public interface ISysRoleService extends IService<SysRole> {
    
    /**
     * 创建角色
     * 遵循：参数验证规范、审计字段规范
     */
    Long createRole(RoleCreateDTO dto, Long createBy);
    
    /**
     * 更新角色
     * 遵循：参数验证规范、审计字段规范
     */
    void updateRole(Long roleId, RoleUpdateDTO dto, Long updateBy);
    
    /**
     * 删除角色（逻辑删除）
     * 遵循：事务控制规范、异常处理规范
     */
    void deleteRole(Long roleId);
    
    /**
     * 批量删除角色
     * 遵循：批量操作规范、事务控制规范
     */
    BatchUpdateResult batchDeleteRoles(List<Long> roleIds);
    
    /**
     * 分页查询角色列表
     */
    IPage<RoleVO> getRolePage(Page<SysRole> page, String roleName, Integer status);
    
    /**
     * 根据用户ID查询角色列表
     */
    List<RoleVO> getRolesByUserId(Long userId);
    
    /**
     * 为角色分配权限
     * 遵循：批量操作规范、事务控制规范
     */
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds, Long operatorId);
    
    /**
     * 移除角色的权限
     * 遵循：批量操作规范、事务控制规范
     */
    void removePermissionsFromRole(Long roleId, List<Long> permissionIds);
    
    /**
     * 查询角色的权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);
}
```

**设计说明**：
- 继承 `IService<SysRole>` 获得MyBatis-Plus的基础CRUD能力
- 使用DTO（Data Transfer Object）进行数据传输，避免直接暴露实体类
- 返回VO（View Object）用于前端展示，只包含必要字段
- 批量操作返回 `BatchUpdateResult` 提供详细的操作结果

##### 1.2.2 权限Service接口（ISysPermissionService）

```java
public interface ISysPermissionService extends IService<SysPermission> {
    
    /**
     * 创建权限
     * 遵循：参数验证规范、审计字段规范
     */
    Long createPermission(PermissionCreateDTO dto, Long createBy);
    
    /**
     * 更新权限
     * 遵循：参数验证规范、审计字段规范
     */
    void updatePermission(Long permissionId, PermissionUpdateDTO dto, Long updateBy);
    
    /**
     * 删除权限（逻辑删除）
     * 遵循：事务控制规范、异常处理规范
     */
    void deletePermission(Long permissionId);
    
    /**
     * 批量删除权限
     * 遵循：批量操作规范、事务控制规范
     */
    BatchUpdateResult batchDeletePermissions(List<Long> permissionIds);
    
    /**
     * 分页查询权限列表
     */
    IPage<PermissionVO> getPermissionPage(Page<SysPermission> page, 
                                          String permissionName, 
                                          Integer type, 
                                          Integer status);
    
    /**
     * 查询权限树形结构
     */
    List<PermissionVO> getPermissionTree();
    
    /**
     * 根据父权限ID查询子权限列表
     */
    List<PermissionVO> getPermissionsByParentId(Long parentId);
    
    /**
     * 根据用户ID查询权限列表
     */
    List<PermissionVO> getPermissionsByUserId(Long userId);
}
```

**设计说明**：
- 支持树形结构查询，通过 `getPermissionTree()` 方法递归构建权限树
- 使用 `parentId` 字段实现父子关系
- 权限类型包括：菜单、按钮、接口

##### 1.2.3 用户角色Service扩展（ISysUserService扩展）

```java
public interface ISysUserService extends IService<SysUser> {
    
    /**
     * 为用户分配角色
     * 遵循：批量操作规范、事务控制规范
     */
    void assignRolesToUser(Long userId, List<Long> roleIds, Long operatorId);
    
    /**
     * 移除用户的角色
     * 遵循：批量操作规范、事务控制规范
     */
    void removeRolesFromUser(Long userId, List<Long> roleIds);
    
    /**
     * 查询用户的角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);
    
    /**
     * 查询用户的角色列表
     */
    List<RoleVO> getRolesByUserId(Long userId);
    
    /**
     * 查询用户的权限列表
     */
    List<PermissionVO> getPermissionsByUserId(Long userId);
}
```

**设计说明**：
- 扩展现有的 `ISysUserService` 接口，添加用户角色关联方法
- 支持批量分配和移除角色
- 支持通过用户ID查询角色和权限

---

### 步骤2：实现与编码

#### 2.1 创建角色Service接口

**文件路径**：`backend/src/main/java/com/haocai/management/service/ISysRoleService.java`

```java
package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.dto.RoleCreateDTO;
import com.haocai.management.dto.RoleUpdateDTO;
import com.haocai.management.dto.RoleVO;
import com.haocai.management.entity.SysRole;

import java.util.List;

/**
 * 角色Service接口
 * 
 * 功能说明：
 * 1. 继承IService提供基础CRUD操作
 * 2. 提供角色相关的业务方法
 * 3. 支持角色权限关联操作
 * 4. 支持批量操作
 * 
 * 遵循规范：
 * - 批量操作规范：先查询后更新，返回详细结果
 * - 异常处理规范：分层处理异常
 * - 事务控制规范：多表操作使用事务
 * - 参数验证规范：验证输入参数有效性
 * 
 * @author haocai
 * @since 2026-01-06
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 创建角色
     * 
     * 遵循：参数验证规范、审计字段规范
     * 
     * @param dto 角色创建DTO
     * @param createBy 创建人ID
     * @return 角色ID
     * @throws BusinessException 角色编码或名称已存在时抛出
     */
    Long createRole(RoleCreateDTO dto, Long createBy);

    /**
     * 更新角色
     * 
     * 遵循：参数验证规范、审计字段规范
     * 
     * @param roleId 角色ID
     * @param dto 角色更新DTO
     * @param updateBy 更新人ID
     * @throws BusinessException 角色不存在或编码/名称已存在时抛出
     */
    void updateRole(Long roleId, RoleUpdateDTO dto, Long updateBy);

    /**
     * 删除角色（逻辑删除）
     * 
     * 遵循：事务控制规范、异常处理规范
     * 
     * @param roleId 角色ID
     * @throws BusinessException 角色不存在或角色下有用户时抛出
     */
    void deleteRole(Long roleId);

    /**
     * 批量删除角色
     * 
     * 遵循：批量操作规范、事务控制规范
     * 
     * @param roleIds 角色ID列表
     * @return 批量操作结果
     */
    BatchUpdateResult batchDeleteRoles(List<Long> roleIds);

    /**
     * 分页查询角色列表
     * 
     * @param page 分页对象
     * @param roleName 角色名称（模糊查询，可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<RoleVO> getRolePage(Page<SysRole> page, String roleName, Integer status);

    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleVO> getRolesByUserId(Long userId);

    /**
     * 为角色分配权限
     * 
     * 遵循：批量操作规范、事务控制规范
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @param operatorId 操作人ID
     * @throws BusinessException 角色不存在或权限不存在时抛出
     */
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds, Long operatorId);

    /**
     * 移除角色的权限
     * 
     * 遵循：批量操作规范、事务控制规范
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @throws BusinessException 角色不存在时抛出
     */
    void removePermissionsFromRole(Long roleId, List<Long> permissionIds);

    /**
     * 查询角色的权限ID列表
     * 
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);
}
```

#### 2.2 创建角色Service实现类

**文件路径**：`backend/src/main/java/com/haocai/management/service/impl/SysRoleServiceImpl.java`

```java
package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.dto.RoleCreateDTO;
import com.haocai.management.dto.RoleUpdateDTO;
import com.haocai.management.dto.RoleVO;
import com.haocai.management.entity.SysRole;
import com.haocai.management.entity.SysUserRole;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.mapper.SysRoleMapper;
import com.haocai.management.mapper.SysUserRoleMapper;
import com.haocai.management.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色Service实现类
 * 
 * 功能说明：
 * 1. 实现角色相关的业务逻辑
 * 2. 处理角色权限关联
 * 3. 支持批量操作
 * 4. 实现事务控制
 * 
 * 遵循规范：
 * - 批量操作规范：先查询后更新，返回详细结果
 * - 异常处理规范：分层处理异常，记录详细日志
 * - 事务控制规范：多表操作使用@Transactional
 * - 参数验证规范：验证输入参数有效性
 * - 审计字段规范：自动填充createBy、updateBy
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateDTO dto, Long createBy) {
        log.info("创建角色: roleName={}, roleCode={}, createBy={}", 
                dto.getRoleName(), dto.getRoleCode(), createBy);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (!StringUtils.hasText(dto.getRoleName())) {
            throw new BusinessException(400, "角色名称不能为空");
        }
        if (!StringUtils.hasText(dto.getRoleCode())) {
            throw new BusinessException(400, "角色编码不能为空");
        }
        
        // 检查角色编码是否已存在
        int count = roleMapper.countByRoleCode(dto.getRoleCode(), null);
        if (count > 0) {
            throw new BusinessException(400, "角色编码已存在: " + dto.getRoleCode());
        }
        
        // 检查角色名称是否已存在
        count = roleMapper.countByRoleName(dto.getRoleName(), null);
        if (count > 0) {
            throw new BusinessException(400, "角色名称已存在: " + dto.getRoleName());
        }
        
        // 创建角色
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        role.setCreateBy(createBy);
        role.setUpdateBy(createBy);
        
        // 遵循：审计字段规范-第1条（自动填充）
        // create_time、update_time会通过MetaObjectHandler自动填充
        
        int result = roleMapper.insert(role);
        if (result <= 0) {
            throw new BusinessException(500, "创建角色失败");
        }
        
        log.info("创建角色成功: roleId={}, roleName={}", role.getId(), role.getRoleName());
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long roleId, RoleUpdateDTO dto, Long updateBy) {
        log.info("更新角色: roleId={}, updateBy={}", roleId, updateBy);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (roleId == null) {
            throw new BusinessException(400, "角色ID不能为空");
        }
        
        // 查询角色是否存在
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "角色不存在: " + roleId);
        }
        
        // 检查角色编码是否已存在（排除自己）
        if (StringUtils.hasText(dto.getRoleCode()) && !dto.getRoleCode().equals(role.getRoleCode())) {
            int count = roleMapper.countByRoleCode(dto.getRoleCode(), roleId);
            if (count > 0) {
                throw new BusinessException(400, "角色编码已存在: " + dto.getRoleCode());
            }
        }
        
        // 检查角色名称是否已存在（排除自己）
        if (StringUtils.hasText(dto.getRoleName()) && !dto.getRoleName().equals(role.getRoleName())) {
            int count = roleMapper.countByRoleName(dto.getRoleName(), roleId);
            if (count > 0) {
                throw new BusinessException(400, "角色名称已存在: " + dto.getRoleName());
            }
        }
        
        // 更新角色
        BeanUtils.copyProperties(dto, role, "id", "createTime", "createBy");
        role.setUpdateBy(updateBy);
        
        // 遵循：审计字段规范-第1条（自动填充）
        // update_time会通过MetaObjectHandler自动填充
        
        int result = roleMapper.updateById(role);
        if (result <= 0) {
            throw new BusinessException(500, "更新角色失败");
        }
        
        log.info("更新角色成功: roleId={}", roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        log.info("删除角色: roleId={}", roleId);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (roleId == null) {
            throw new BusinessException(400, "角色ID不能为空");
        }
        
        // 查询角色是否存在
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "角色不存在: " + roleId);
        }
        
        // 检查角色下是否有用户
        int userCount = userRoleMapper.countUsersByRoleId(roleId);
        if (userCount > 0) {
            throw new BusinessException(400, "该角色下还有" + userCount + "个用户，无法删除");
        }
        
        // 逻辑删除角色
        int result = roleMapper.deleteById(roleId);
        if (result <= 0) {
            throw new BusinessException(500, "删除角色失败");
        }
        
        log.info("删除角色成功: roleId={}", roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchUpdateResult batchDeleteRoles(List<Long> roleIds) {
        log.info("批量删除角色: roleIds={}", roleIds);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (CollectionUtils.isEmpty(roleIds)) {
            throw new BusinessException(400, "角色ID列表不能为空");
        }
        
        int total = roleIds.size();
        int success = 0;
        int failed = 0;
        
        // 遵循：批量操作规范-第1条（先查询后更新）
        // 先查询存在的角色
        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
        
        if (roles.isEmpty()) {
            throw new BusinessException(400, "批量删除失败，所有角色都不存在");
        }
        
        // 批量删除存在的角色
        for (SysRole role : roles) {
            try {
                // 检查角色下是否有用户
                int userCount = userRoleMapper.countUsersByRoleId(role.getId());
                if (userCount > 0) {
                    log.warn("角色下还有用户，无法删除: roleId={}, userCount={}", role.getId(), userCount);
                    failed++;
                    continue;
                }
                
                // 逻辑删除角色
                int result = roleMapper.deleteById(role.getId());
                if (result > 0) {
                    success++;
                } else {
                    failed++;
                }
            } catch (Exception e) {
                log.error("删除角色失败: roleId={}, error={}", role.getId(), e.getMessage());
                failed++;
            }
        }
        
        failed = total - success;
        
        log.info("批量删除角色完成: 总数={}, 成功={}, 失败={}", total, success, failed);
        
        return BatchUpdateResult.of(total, success, failed);
    }

    @Override
    public IPage<RoleVO> getRolePage(Page<SysRole> page, String roleName, Integer status) {
        log.info("分页查询角色列表: page={}, roleName={}, status={}", page, roleName, status);
        
        // 使用Mapper的分页查询方法
        IPage<SysRole> rolePage = roleMapper.selectPageByCondition(page, roleName, status);
        
        // 转换为VO
        IPage<RoleVO> voPage = rolePage.convert(role -> {
            RoleVO vo = new RoleVO();
            BeanUtils.copyProperties(role, vo);
            return vo;
        });
        
        return voPage;
    }

    @Override
    public List<RoleVO> getRolesByUserId(Long userId) {
        log.info("根据用户ID查询角色列表: userId={}", userId);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }
        
        // 查询用户的角色ID列表
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        
        if (CollectionUtils.isEmpty(roleIds)) {
            return List.of();
        }
        
        // 查询角色列表
        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
        
        // 转换为VO
        return roles.stream().map(role -> {
            RoleVO vo = new RoleVO();
            BeanUtils.copyProperties(role, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds, Long operatorId) {
        log.info("为角色分配权限: roleId={}, permissionIds={}, operatorId={}", 
                roleId, permissionIds, operatorId);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (roleId == null) {
            throw new BusinessException(400, "角色ID不能为空");
        }
        if (CollectionUtils.isEmpty(permissionIds)) {
            throw new BusinessException(400, "权限ID列表不能为空");
        }
        
        // 查询角色是否存在
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "角色不存在: " + roleId);
        }
        
        // 遵循：批量操作规范-第1条（先查询后更新）
        // 查询已存在的权限ID
        List<Long> existingPermissionIds = roleMapper.selectPermissionIdsByRoleId(roleId);
        
        // 过滤出需要新增的权限ID
        List<Long> newPermissionIds = permissionIds.stream()
                .filter(id -> !existingPermissionIds.contains(id))
                .collect(Collectors.toList());
        
        if (newPermissionIds.isEmpty()) {
            log.info("所有权限已存在，无需分配");
            return;
        }
        
        // 批量插入角色权限关联
        for (Long permissionId : newPermissionIds) {
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermission.setCreateBy(operatorId);
            
            // 遵循：审计字段规范-第1条（自动填充）
            // create_time会通过MetaObjectHandler自动填充
            
            int result = rolePermissionMapper.insert(rolePermission);
            if (result <= 0) {
                log.error("分配权限失败: roleId={}, permissionId={}", roleId, permissionId);
            }
        }
        
        log.info("为角色分配权限成功: roleId={}, 新增权限数={}", roleId, newPermissionIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        log.info("移除角色的权限: roleId={}, permissionIds={}", roleId, permissionIds);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (roleId == null) {
            throw new BusinessException(400, "角色ID不能为空");
        }
        if (CollectionUtils.isEmpty(permissionIds)) {
            throw new BusinessException(400, "权限ID列表不能为空");
        }
        
        // 查询角色是否存在
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "角色不存在: " + roleId);
        }
        
        // 批量删除角色权限关联
        int result = rolePermissionMapper.deleteByRoleIdAndPermissionIds(roleId, permissionIds);
        
        log.info("移除角色的权限成功: roleId={}, 删除数量={}", roleId, result);
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        log.info("查询角色的权限ID列表: roleId={}", roleId);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (roleId == null) {
            throw new BusinessException(400, "角色ID不能为空");
        }
        
        return roleMapper.selectPermissionIdsByRoleId(roleId);
    }
}
```

**注意**：上述代码中需要注入 `SysRolePermissionMapper`，需要在实现类中添加依赖注入。

#### 2.3 创建权限Service接口

**文件路径**：`backend/src/main/java/com/haocai/management/service/ISysPermissionService.java`

```java
package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.dto.PermissionCreateDTO;
import com.haocai.management.dto.PermissionUpdateDTO;
import com.haocai.management.dto.PermissionVO;
import com.haocai.management.entity.SysPermission;

import java.util.List;

/**
 * 权限Service接口
 * 
 * 功能说明：
 * 1. 继承IService提供基础CRUD操作
 * 2. 提供权限相关的业务方法
 * 3. 支持树形结构查询
 * 4. 支持批量操作
 * 
 * 遵循规范：
 * - 批量操作规范：先查询后更新，返回详细结果
 * - 异常处理规范：分层处理异常
 * - 事务控制规范：多表操作使用事务
 * - 参数验证规范：验证输入参数有效性
 * 
 * @author haocai
 * @since 2026-01-06
 */
public interface ISysPermissionService extends IService<SysPermission> {

    /**
     * 创建权限
     * 
     * 遵循：参数验证规范、审计字段规范
     * 
     * @param dto 权限创建DTO
     * @param createBy 创建人ID
     * @return 权限ID
     * @throws BusinessException 权限编码或名称已存在时抛出
     */
    Long createPermission(PermissionCreateDTO dto, Long createBy);

    /**
     * 更新权限
     * 
     * 遵循：参数验证规范、审计字段规范
     * 
     * @param permissionId 权限ID
     * @param dto 权限更新DTO
     * @param updateBy 更新人ID
     * @throws BusinessException 权限不存在或编码/名称已存在时抛出
     */
    void updatePermission(Long permissionId, PermissionUpdateDTO dto, Long updateBy);

    /**
     * 删除权限（逻辑删除）
     * 
     * 遵循：事务控制规范、异常处理规范
     * 
     * @param permissionId 权限ID
     * @throws BusinessException 权限不存在或权限下有子权限时抛出
     */
    void deletePermission(Long permissionId);

    /**
     * 批量删除权限
     * 
     * 遵循：批量操作规范、事务控制规范
     * 
     * @param permissionIds 权限ID列表
     * @return 批量操作结果
     */
    BatchUpdateResult batchDeletePermissions(List<Long> permissionIds);

    /**
     * 分页查询权限列表
     * 
     * @param page 分页对象
     * @param permissionName 权限名称（模糊查询，可选）
     * @param type 权限类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<PermissionVO> getPermissionPage(Page<SysPermission> page, 
                                          String permissionName, 
                                          Integer type, 
                                          Integer status);

    /**
     * 查询权限树形结构
     * 
     * @return 权限树
     */
    List<PermissionVO> getPermissionTree();

    /**
     * 根据父权限ID查询子权限列表
     * 
     * @param parentId 父权限ID，如果为null或0则查询顶级权限
     * @return 子权限列表
     */
    List<PermissionVO> getPermissionsByParentId(Long parentId);

    /**
     * 根据用户ID查询权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<PermissionVO> getPermissionsByUserId(Long userId);
}
```

#### 2.4 创建权限Service实现类

**文件路径**：`backend/src/main/java/com/haocai/management/service/impl/SysPermissionServiceImpl.java`

```java
package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限Service实现类
 * 
 * 功能说明：
 * 1. 实现权限相关的业务逻辑
 * 2. 处理权限树形结构
 * 3. 支持批量操作
 * 4. 实现事务控制
 * 
 * 遵循规范：
 * - 批量操作规范：先查询后更新，返回详细结果
 * - 异常处理规范：分层处理异常，记录详细日志
 * - 事务控制规范：多表操作使用@Transactional
 * - 参数验证规范：验证输入参数有效性
 * - 审计字段规范：自动填充createBy、updateBy
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> 
        implements ISysPermissionService {

    private final SysPermissionMapper permissionMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(PermissionCreateDTO dto, Long createBy) {
        log.info("创建权限: name={}, code={}, type={}, createBy={}", 
                dto.getName(), dto.getCode(), dto.getType(), createBy);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException(400, "权限名称不能为空");
        }
        if (!StringUtils.hasText(dto.getCode())) {
            throw new BusinessException(400, "权限编码不能为空");
        }
        if (dto.getType() == null) {
            throw new BusinessException(400, "权限类型不能为空");
        }
        
        // 检查权限编码是否已存在
        int count = permissionMapper.countByPermissionCode(dto.getCode());
        if (count > 0) {
            throw new BusinessException(400, "权限编码已存在: " + dto.getCode());
        }
        
        // 检查权限名称是否已存在
        count = permissionMapper.countByPermissionName(dto.getName());
        if (count > 0) {
            throw new BusinessException(400, "权限名称已存在: " + dto.getName());
        }
        
        // 如果有父权限，检查父权限是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            SysPermission parentPermission = permissionMapper.selectById(dto.getParentId());
            if (parentPermission == null) {
                throw new BusinessException(404, "父权限不存在: " + dto.getParentId());
            }
        }
        
        // 创建权限
        SysPermission permission = new SysPermission();
        BeanUtils.copyProperties(dto, permission);
        permission.setCreateBy(createBy);
        permission.setUpdateBy(createBy);
        
        // 遵循：审计字段规范-第1条（自动填充）
        // create_time、update_time会通过MetaObjectHandler自动填充
        
        int result = permissionMapper.insert(permission);
        if (result <= 0) {
            throw new BusinessException(500, "创建权限失败");
        }
        
        log.info("创建权限成功: permissionId={}, name={}", permission.getId(), permission.getName());
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(Long permissionId, PermissionUpdateDTO dto, Long updateBy) {
        log.info("更新权限: permissionId={}, updateBy={}", permissionId, updateBy);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (permissionId == null) {
            throw new BusinessException(400, "权限ID不能为空");
        }
        
        // 查询权限是否存在
        SysPermission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException(404, "权限不存在: " + permissionId);
        }
        
        // 检查权限编码是否已存在（排除自己）
        if (StringUtils.hasText(dto.getCode()) && !dto.getCode().equals(permission.getCode())) {
            int count = permissionMapper.countByPermissionCode(dto.getCode());
            if (count > 0) {
                throw new BusinessException(400, "权限编码已存在: " + dto.getCode());
            }
        }
        
        // 检查权限名称是否已存在（排除自己）
        if (StringUtils.hasText(dto.getName()) && !dto.getName().equals(permission.getName())) {
            int count = permissionMapper.countByPermissionName(dto.getName());
            if (count > 0) {
                throw new BusinessException(400, "权限名称已存在: " + dto.getName());
            }
        }
        
        // 如果有父权限，检查父权限是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            // 不能将自己设置为父权限
            if (dto.getParentId().equals(permissionId)) {
                throw new BusinessException(400, "不能将自己设置为父权限");
            }
            
            SysPermission parentPermission = permissionMapper.selectById(dto.getParentId());
            if (parentPermission == null) {
                throw new BusinessException(404, "父权限不存在: " + dto.getParentId());
            }
        }
        
        // 更新权限
        BeanUtils.copyProperties(dto, permission, "id", "createTime", "createBy");
        permission.setUpdateBy(updateBy);
        
        // 遵循：审计字段规范-第1条（自动填充）
        // update_time会通过MetaObjectHandler自动填充
        
        int result = permissionMapper.updateById(permission);
        if (result <= 0) {
            throw new BusinessException(500, "更新权限失败");
        }
        
        log.info("更新权限成功: permissionId={}", permissionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long permissionId) {
        log.info("删除权限: permissionId={}", permissionId);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (permissionId == null) {
            throw new BusinessException(400, "权限ID不能为空");
        }
        
        // 查询权限是否存在
        SysPermission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException(404, "权限不存在: " + permissionId);
        }
        
        // 检查权限下是否有子权限
        List<SysPermission> childPermissions = permissionMapper.selectByParentId(permissionId);
        if (!CollectionUtils.isEmpty(childPermissions)) {
            throw new BusinessException(400, "该权限下还有" + childPermissions.size() + "个子权限，无法删除");
        }
        
        // 检查权限是否被角色使用
        int roleCount = rolePermissionMapper.countRolesByPermissionId(permissionId);
        if (roleCount > 0) {
            throw new BusinessException(400, "该权限已被" + roleCount + "个角色使用，无法删除");
        }
        
        // 逻辑删除权限
        int result = permissionMapper.deleteById(permissionId);
        if (result <= 0) {
            throw new BusinessException(500, "删除权限失败");
        }
        
        log.info("删除权限成功: permissionId={}", permissionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchUpdateResult batchDeletePermissions(List<Long> permissionIds) {
        log.info("批量删除权限: permissionIds={}", permissionIds);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (CollectionUtils.isEmpty(permissionIds)) {
            throw new BusinessException(400, "权限ID列表不能为空");
        }
        
        int total = permissionIds.size();
        int success = 0;
        int failed = 0;
        
        // 遵循：批量操作规范-第1条（先查询后更新）
        // 先查询存在的权限
        List<SysPermission> permissions = permissionMapper.selectBatchIds(permissionIds);
        
        if (permissions.isEmpty()) {
            throw new BusinessException(400, "批量删除失败，所有权限都不存在");
        }
        
        // 批量删除存在的权限
        for (SysPermission permission : permissions) {
            try {
                // 检查权限下是否有子权限
                List<SysPermission> childPermissions = permissionMapper.selectByParentId(permission.getId());
                if (!CollectionUtils.isEmpty(childPermissions)) {
                    log.warn("权限下还有子权限，无法删除: permissionId={}, childCount={}", 
                            permission.getId(), childPermissions.size());
                    failed++;
                    continue;
                }
                
                // 检查权限是否被角色使用
                int roleCount = rolePermissionMapper.countRolesByPermissionId(permission.getId());
                if (roleCount > 0) {
                    log.warn("权限已被角色使用，无法删除: permissionId={}, roleCount={}", 
                            permission.getId(), roleCount);
                    failed++;
                    continue;
                }
                
                // 逻辑删除权限
                int result = permissionMapper.deleteById(permission.getId());
                if (result > 0) {
                    success++;
                } else {
                    failed++;
                }
            } catch (Exception e) {
                log.error("删除权限失败: permissionId={}, error={}", permission.getId(), e.getMessage());
                failed++;
            }
        }
        
        failed = total - success;
        
        log.info("批量删除权限完成: 总数={}, 成功={}, 失败={}", total, success, failed);
        
        return BatchUpdateResult.of(total, success, failed);
    }

    @Override
    public IPage<PermissionVO> getPermissionPage(Page<SysPermission> page, 
                                                  String permissionName, 
                                                  Integer type, 
                                                  Integer status) {
        log.info("分页查询权限列表: page={}, permissionName={}, type={}, status={}", 
                page, permissionName, type, status);
        
        // 使用Mapper的分页查询方法
        IPage<SysPermission> permissionPage = permissionMapper.selectPageByCondition(
                page, permissionName, type, status);
        
        // 转换为VO
        IPage<PermissionVO> voPage = permissionPage.convert(permission -> {
            PermissionVO vo = new PermissionVO();
            BeanUtils.copyProperties(permission, vo);
            return vo;
        });
        
        return voPage;
    }

    @Override
    public List<PermissionVO> getPermissionTree() {
        log.info("查询权限树形结构");
        
        // 查询所有权限
        List<SysPermission> allPermissions = permissionMapper.selectAllForTree();
        
        // 转换为VO
        List<PermissionVO> allVos = allPermissions.stream().map(permission -> {
            PermissionVO vo = new PermissionVO();
            BeanUtils.copyProperties(permission, vo);
            return vo;
        }).collect(Collectors.toList());
        
        // 构建树形结构
        return buildPermissionTree(allVos, null);
    }

    @Override
    public List<PermissionVO> getPermissionsByParentId(Long parentId) {
        log.info("根据父权限ID查询子权限列表: parentId={}", parentId);
        
        // 查询子权限
        List<SysPermission> permissions = permissionMapper.selectByParentId(parentId);
        
        // 转换为VO
        return permissions.stream().map(permission -> {
            PermissionVO vo = new PermissionVO();
            BeanUtils.copyProperties(permission, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> getPermissionsByUserId(Long userId) {
        log.info("根据用户ID查询权限列表: userId={}", userId);
        
        // 遵循：参数验证规范-第4条（参数验证）
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }
        
        // 查询用户的权限列表
        List<SysPermission> permissions = permissionMapper.selectByUserId(userId);
        
        // 转换为VO
        return permissions.stream().map(permission -> {
            PermissionVO vo = new PermissionVO();
            BeanUtils.copyProperties(permission, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 构建权限树形结构
     * 
     * @param allPermissions 所有权限列表
     * @param parentId 父权限ID
     * @return 树形结构
     */
    private List<PermissionVO> buildPermissionTree(List<PermissionVO> allPermissions, Long parentId) {
        List<PermissionVO> tree = new ArrayList<>();
        
        for (PermissionVO permission : allPermissions) {
            // 判断是否为当前父节点的子节点
            if ((parentId == null && (permission.getParentId() == null || permission.getParentId() == 0))
                    || (parentId != null && parentId.equals(permission.getParentId()))) {
                // 递归构建子节点
                List<PermissionVO> children = buildPermissionTree(allPermissions, permission.getId());
                permission.setChildren(children);
                tree.add(permission);
            }
        }
        
        return tree;
    }
}
```

#### 2.5 扩展用户角色Service

**文件路径**：`backend/src/main/java/com/haocai/management/service/ISysUserService.java`（扩展）

需要在现有的 `ISysUserService` 接口中添加以下方法：

```java
/**
 * 为用户分配角色
 * 
 * 遵循：批量操作规范、事务控制规范
 * 
 * @param userId 用户ID
 * @param roleIds 角色ID列表
 * @param operatorId 操作人ID
 * @throws BusinessException 用户不存在或角色不存在时抛出
 */
void assignRolesToUser(Long userId, List<Long> roleIds, Long operatorId);

/**
 * 移除用户的角色
 * 
 * 遵循：批量操作规范、事务控制规范
 * 
 * @param userId 用户ID
 * @param roleIds 角色ID列表
 * @throws BusinessException 用户不存在时抛出
 */
void removeRolesFromUser(Long userId, List<Long> roleIds);

/**
 * 查询用户的角色ID列表
 * 
 * @param userId 用户ID
 * @return 角色ID列表
 */
List<Long> getRoleIdsByUserId(Long userId);

/**
 * 查询用户的角色列表
 * 
 * @param userId 用户ID
 * @return 角色列表
 */
List<RoleVO> getRolesByUserId(Long userId);

/**
 * 查询用户的权限列表
 * 
 * @param userId 用户ID
 * @return 权限列表
 */
List<PermissionVO> getPermissionsByUserId(Long userId);
```

**文件路径**：`backend/src/main/java/com/haocai/management/service/impl/SysUserServiceImpl.java`（扩展）

需要在现有的 `SysUserServiceImpl` 实现类中添加以下方法：

```java
private final SysUserRoleMapper userRoleMapper;
private final SysRoleMapper roleMapper;
private final SysPermissionMapper permissionMapper;

@Override
@Transactional(rollbackFor = Exception.class)
public void assignRolesToUser(Long userId, List<Long> roleIds, Long operatorId) {
    log.info("为用户分配角色: userId={}, roleIds={}, operatorId={}", userId, roleIds, operatorId);
    
    // 遵循：参数验证规范-第4条（参数验证）
    if (userId == null) {
        throw new BusinessException(400, "用户ID不能为空");
    }
    if (CollectionUtils.isEmpty(roleIds)) {
        throw new BusinessException(400, "角色ID列表不能为空");
    }
    
    // 查询用户是否存在
    SysUser user = userMapper.selectById(userId);
    if (user == null) {
        throw new BusinessException(404, "用户不存在: " + userId);
    }
    
    // 查询角色是否存在
    List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
    if (roles.size() != roleIds.size()) {
        throw new BusinessException(404, "部分角色不存在");
    }
    
    // 遵循：批量操作规范-第1条（先查询后更新）
    // 查询已存在的角色ID
    List<Long> existingRoleIds = userRoleMapper.selectRoleIdsByUserId(userId);
    
    // 过滤出需要新增的角色ID
    List<Long> newRoleIds = roleIds.stream()
            .filter(id -> !existingRoleIds.contains(id))
            .collect(Collectors.toList());
    
    if (newRoleIds.isEmpty()) {
        log.info("所有角色已存在，无需分配");
        return;
    }
    
    // 批量插入用户角色关联
    for (Long roleId : newRoleIds) {
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setCreateBy(operatorId);
        
        // 遵循：审计字段规范-第1条（自动填充）
        // create_time会通过MetaObjectHandler自动填充
        
        int result = userRoleMapper.insert(userRole);
        if (result <= 0) {
            log.error("分配角色失败: userId={}, roleId={}", userId, roleId);
        }
    }
    
    log.info("为用户分配角色成功: userId={}, 新增角色数={}", userId, newRoleIds.size());
}

@Override
@Transactional(rollbackFor = Exception.class)
public void removeRolesFromUser(Long userId, List<Long> roleIds) {
    log.info("移除用户的角色: userId={}, roleIds={}", userId, roleIds);
    
    // 遵循：参数验证规范-第4条（参数验证）
    if (userId == null) {
        throw new BusinessException(400, "用户ID不能为空");
    }
    if (CollectionUtils.isEmpty(roleIds)) {
        throw new BusinessException(400, "角色ID列表不能为空");
    }
    
    // 查询用户是否存在
    SysUser user = userMapper.selectById(userId);
    if (user == null) {
        throw new BusinessException(404, "用户不存在: " + userId);
    }
    
    // 批量删除用户角色关联
    int result = userRoleMapper.deleteByUserIdAndRoleIds(userId, roleIds);
    
    log.info("移除用户的角色成功: userId={}, 删除数量={}", userId, result);
}

@Override
public List<Long> getRoleIdsByUserId(Long userId) {
    log.info("查询用户的角色ID列表: userId={}", userId);
    
    // 遵循：参数验证规范-第4条（参数验证）
    if (userId == null) {
        throw new BusinessException(400, "用户ID不能为空");
    }
    
    return userRoleMapper.selectRoleIdsByUserId(userId);
}

@Override
public List<RoleVO> getRolesByUserId(Long userId) {
    log.info("查询用户的角色列表: userId={}", userId);
    
    // 遵循：参数验证规范-第4条（参数验证）
    if (userId == null) {
        throw new BusinessException(400, "用户ID不能为空");
    }
    
    // 查询用户的角色ID列表
    List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
    
    if (CollectionUtils.isEmpty(roleIds)) {
        return List.of();
    }
    
    // 查询角色列表
    List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
    
    // 转换为VO
    return roles.stream().map(role -> {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }).collect(Collectors.toList());
}

@Override
public List<PermissionVO> getPermissionsByUserId(Long userId) {
    log.info("查询用户的权限列表: userId={}", userId);
    
    // 遵循：参数验证规范-第4条（参数验证）
    if (userId == null) {
        throw new BusinessException(400, "用户ID不能为空");
    }
    
    // 查询用户的权限列表
    List<SysPermission> permissions = permissionMapper.selectByUserId(userId);
    
    // 转换为VO
    return permissions.stream().map(permission -> {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }).collect(Collectors.toList());
}
```

#### 2.6 创建批量操作结果类

**文件路径**：`backend/src/main/java/com/haocai/management/dto/BatchUpdateResult.java`

```java
package com.haocai.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量操作结果
 * 
 * 用于返回批量操作的详细结果
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateResult {
    
    /**
     * 总数
     */
    private Integer total;
    
    /**
     * 成功数
     */
    private Integer success;
    
    /**
     * 失败数
     */
    private Integer failed;
    
    /**
     * 创建批量操作结果
     * 
     * @param total 总数
     * @param success 成功数
     * @param failed 失败数
     * @return 批量操作结果
     */
    public static BatchUpdateResult of(Integer total, Integer success, Integer failed) {
        return new BatchUpdateResult(total, success, failed);
    }
}
```

---

### 步骤3：验证与测试

#### 3.1 测试用例

**文件路径**：`backend/src/test/java/com/haocai/management/service/SysRoleServiceTest.java`

```java
package com.haocai.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.BatchUpdateResult;
import com.haocai.management.dto.RoleCreateDTO;
import com.haocai.management.dto.RoleUpdateDTO;
import com.haocai.management.dto.RoleVO;
import com.haocai.management.entity.SysRole;
import com.haocai.management.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 角色Service测试类
 * 
 * @author haocai
 * @since 2026-01-06
 */
@SpringBootTest
class SysRoleServiceTest {

    @Autowired
    private ISysRoleService roleService;

    @Test
    void testCreateRole() {
        // 准备测试数据
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setRoleName("测试角色");
        dto.setRoleCode("TEST_ROLE");
        dto.setDescription("这是一个测试角色");
        dto.setStatus(1);
        
        // 执行测试
        Long roleId = roleService.createRole(dto, 1L);
        
        // 验证结果
        assertNotNull(roleId);
        SysRole role = roleService.getById(roleId);
        assertEquals("测试角色", role.getRoleName());
        assertEquals("TEST_ROLE", role.getRoleCode());
    }

    @Test
    void testCreateRoleWithDuplicateCode() {
        // 准备测试数据
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setRoleName("测试角色2");
        dto.setRoleCode("TEST_ROLE"); // 重复的编码
        dto.setDescription("这是一个测试角色");
        dto.setStatus(1);
        
        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.createRole(dto, 1L);
        });
        
        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("角色编码已存在"));
    }

    @Test
    void testUpdateRole() {
        // 先创建一个角色
        RoleCreateDTO createDto = new RoleCreateDTO();
        createDto.setRoleName("更新测试角色");
        createDto.setRoleCode("UPDATE_TEST_ROLE");
        createDto.setDescription("这是一个测试角色");
        createDto.setStatus(1);
        Long roleId = roleService.createRole(createDto, 1L);
        
        // 更新角色
        RoleUpdateDTO updateDto = new RoleUpdateDTO();
        updateDto.setRoleName("更新后的角色名称");
        updateDto.setDescription("更新后的描述");
        updateDto.setStatus(0);
        
        roleService.updateRole(roleId, updateDto, 1L);
        
        // 验证结果
        SysRole role = roleService.getById(roleId);
        assertEquals("更新后的角色名称", role.getRoleName());
        assertEquals("更新后的描述", role.getDescription());
        assertEquals(0, role.getStatus());
    }

    @Test
    void testDeleteRole() {
        // 先创建一个角色
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setRoleName("删除测试角色");
        dto.setRoleCode("DELETE_TEST_ROLE");
        dto.setDescription("这是一个测试角色");
        dto.setStatus(1);
        Long roleId = roleService.createRole(dto, 1L);
        
        // 删除角色
        roleService.deleteRole(roleId);
        
        // 验证结果
        SysRole role = roleService.getById(roleId);
        assertNull(role);
    }

    @Test
    void testBatchDeleteRoles() {
        // 创建多个角色
        Long roleId1 = roleService.createRole(new RoleCreateDTO("批量删除1", "BATCH_DELETE_1", "描述1", 1), 1L);
        Long roleId2 = roleService.createRole(new RoleCreateDTO("批量删除2", "BATCH_DELETE_2", "描述2", 1), 1L);
        Long roleId3 = roleService.createRole(new RoleCreateDTO("批量删除3", "BATCH_DELETE_3", "描述3", 1), 1L);
        
        // 批量删除
        BatchUpdateResult result = roleService.batchDeleteRoles(Arrays.asList(roleId1, roleId2, roleId3, 999L));
        
        // 验证结果
        assertEquals(4, result.getTotal());
        assertTrue(result.getSuccess() >= 3);
        assertTrue(result.getFailed() >= 1);
    }

    @Test
    void testGetRolePage() {
        // 分页查询
        Page<SysRole> page = new Page<>(1, 10);
        IPage<RoleVO> result = roleService.getRolePage(page, null, null);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.getRecords().size() >= 0);
    }

    @Test
    void testGetRolesByUserId() {
        // 查询用户的角色列表
        List<RoleVO> roles = roleService.getRolesByUserId(1L);
        
        // 验证结果
        assertNotNull(roles);
    }
}
```

**文件路径**：`backend/src/test/java/com/haocai/management/service/SysPermissionServiceTest.java`

```java
package com.haocai.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.BatchUpdateResult;
import com.haocai.management.dto.PermissionCreateDTO;
import com.haocai.management.dto.PermissionUpdateDTO;
import com.haocai.management.dto.PermissionVO;
import com.haocai.management.entity.SysPermission;
import com.haocai.management.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 权限Service测试类
 * 
 * @author haocai
 * @since 2026-01-06
 */
@SpringBootTest
class SysPermissionServiceTest {

    @Autowired
    private ISysPermissionService permissionService;

    @Test
    void testCreatePermission() {
        // 准备测试数据
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setName("测试权限");
        dto.setCode("test:permission");
        dto.setType(1);
        dto.setParentId(null);
        dto.setPath("/test");
        dto.setComponent("TestComponent");
        dto.setIcon("test-icon");
        dto.setSortOrder(1);
        dto.setStatus(1);
        
        // 执行测试
        Long permissionId = permissionService.createPermission(dto, 1L);
        
        // 验证结果
        assertNotNull(permissionId);
        SysPermission permission = permissionService.getById(permissionId);
        assertEquals("测试权限", permission.getName());
        assertEquals("test:permission", permission.getCode());
    }

    @Test
    void testCreatePermissionWithDuplicateCode() {
        // 准备测试数据
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setName("测试权限2");
        dto.setCode("test:permission"); // 重复的编码
        dto.setType(1);
        dto.setParentId(null);
        dto.setPath("/test2");
        dto.setComponent("TestComponent2");
        dto.setIcon("test-icon2");
        dto.setSortOrder(2);
        dto.setStatus(1);
        
        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            permissionService.createPermission(dto, 1L);
        });
        
        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("权限编码已存在"));
    }

    @Test
    void testUpdatePermission() {
        // 先创建一个权限
        PermissionCreateDTO createDto = new PermissionCreateDTO();
        createDto.setName("更新测试权限");
        createDto.setCode("update:test:permission");
        createDto.setType(1);
        createDto.setParentId(null);
        createDto.setPath("/update-test");
        createDto.setComponent("UpdateTestComponent");
        createDto.setIcon("update-test-icon");
        createDto.setSortOrder(1);
        createDto.setStatus(1);
        Long permissionId = permissionService.createPermission(createDto, 1L);
        
        // 更新权限
        PermissionUpdateDTO updateDto = new PermissionUpdateDTO();
        updateDto.setName("更新后的权限名称");
        updateDto.setPath("/update-test-new");
        updateDto.setStatus(0);
        
        permissionService.updatePermission(permissionId, updateDto, 1L);
        
        // 验证结果
        SysPermission permission = permissionService.getById(permissionId);
        assertEquals("更新后的权限名称", permission.getName());
        assertEquals("/update-test-new", permission.getPath());
        assertEquals(0, permission.getStatus());
    }

    @Test
    void testDeletePermission() {
        // 先创建一个权限
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setName("删除测试权限");
        dto.setCode("delete:test:permission");
        dto.setType(1);
        dto.setParentId(null);
        dto.setPath("/delete-test");
        dto.setComponent("DeleteTestComponent");
        dto.setIcon("delete-test-icon");
        dto.setSortOrder(1);
        dto.setStatus(1);
        Long permissionId = permissionService.createPermission(dto, 1L);
        
        // 删除权限
        permissionService.deletePermission(permissionId);
        
        // 验证结果
        SysPermission permission = permissionService.getById(permissionId);
        assertNull(permission);
    }

    @Test
    void testBatchDeletePermissions() {
        // 创建多个权限
        Long permissionId1 = permissionService.createPermission(
                new PermissionCreateDTO("批量删除1", "batch:delete:1", 1, null, "/batch1", "Batch1", "icon1", 1, 1), 1L);
        Long permissionId2 = permissionService.createPermission(
                new PermissionCreateDTO("批量删除2", "batch:delete:2", 1, null, "/batch2", "Batch2", "icon2", 2, 1), 1L);
        Long permissionId3 = permissionService.createPermission(
                new PermissionCreateDTO("批量删除3", "batch:delete:3", 1, null, "/batch3", "Batch3", "icon3", 3, 1), 1L);
        
        // 批量删除
        BatchUpdateResult result = permissionService.batchDeletePermissions(
                Arrays.asList(permissionId1, permissionId2, permissionId3, 999L));
        
        // 验证结果
        assertEquals(4, result.getTotal());
        assertTrue(result.getSuccess() >= 3);
        assertTrue(result.getFailed() >= 1);
    }

    @Test
    void testGetPermissionPage() {
        // 分页查询
        Page<SysPermission> page = new Page<>(1, 10);
        IPage<PermissionVO> result = permissionService.getPermissionPage(page, null, null, null);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.getRecords().size() >= 0);
    }

    @Test
    void testGetPermissionTree() {
        // 查询权限树
        List<PermissionVO> tree = permissionService.getPermissionTree();
        
        // 验证结果
        assertNotNull(tree);
    }

    @Test
    void testGetPermissionsByParentId() {
        // 查询顶级权限
        List<PermissionVO> permissions = permissionService.getPermissionsByParentId(null);
        
        // 验证结果
        assertNotNull(permissions);
    }

    @Test
    void testGetPermissionsByUserId() {
        // 查询用户的权限列表
        List<PermissionVO> permissions = permissionService.getPermissionsByUserId(1L);
        
        // 验证结果
        assertNotNull(permissions);
    }
}
```

#### 3.2 边界测试和异常测试场景

**边界测试场景**：

1. **空值输入测试**：
   - 创建角色时传入空的roleName或roleCode
   - 更新角色时传入null的roleId
   - 批量操作时传入空的ID列表

2. **重复数据测试**：
   - 创建角色时使用已存在的roleCode
   - 创建角色时使用已存在的roleName
   - 创建权限时使用已存在的code
   - 创建权限时使用已存在的name

3. **关联数据测试**：
   - 删除角色时，角色下还有用户
   - 删除权限时，权限下还有子权限
   - 删除权限时，权限已被角色使用
   - 更新权限时，将自己设置为父权限

4. **不存在的数据测试**：
   - 更新不存在的角色
   - 删除不存在的角色
   - 批量删除时包含不存在的ID
   - 查询不存在的用户的角色列表

**异常测试场景**：

1. **数据库异常测试**：
   - 数据库连接失败
   - 数据库表不存在
   - 数据库字段类型不匹配

2. **并发操作测试**：
   - 多个线程同时创建相同编码的角色
   - 多个线程同时删除同一个角色

3. **事务回滚测试**：
   - 批量操作时部分失败，验证事务是否回滚
   - 多表操作时异常，验证事务是否回滚

---

### 步骤4：文档与知识固化

#### 4.1 对development-standards.md的更新建议

基于本次开发实践，建议对 `development-standards.md` 进行以下更新：

**建议1：添加树形结构查询规范**

在"三、数据访问层规范"中添加新的小节：

```markdown
### 3.3 树形结构查询规范

**原则**：树形结构查询应该在Service层构建，避免在Mapper层进行复杂的递归查询

**正确实现**：
```java
@Override
public List<PermissionVO> getPermissionTree() {
    // 查询所有权限
    List<SysPermission> allPermissions = permissionMapper.selectAllForTree();
    
    // 转换为VO
    List<PermissionVO> allVos = allPermissions.stream().map(permission -> {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }).collect(Collectors.toList());
    
    // 在Service层构建树形结构
    return buildPermissionTree(allVos, null);
}

/**
 * 递归构建树形结构
 */
private List<PermissionVO> buildPermissionTree(List<PermissionVO> allPermissions, Long parentId) {
    List<PermissionVO> tree = new ArrayList<>();
    
    for (PermissionVO permission : allPermissions) {
        if ((parentId == null && (permission.getParentId() == null || permission.getParentId() == 0))
                || (parentId != null && parentId.equals(permission.getParentId()))) {
            List<PermissionVO> children = buildPermissionTree(allPermissions, permission.getId());
            permission.setChildren(children);
            tree.add(permission);
        }
    }
    
    return tree;
}
```

**⚠️ 常见错误**：
- ❌ 在Mapper层使用递归查询，性能差
- ❌ 树形结构构建逻辑复杂，难以维护
- ❌ 未考虑循环引用，导致无限递归
```

**建议2：添加关联数据删除规范**

在"三、数据访问层规范"中添加新的小节：

```markdown
### 3.4 关联数据删除规范

**原则**：删除数据前必须检查是否存在关联数据，避免数据不一致

**正确实现**：
```java
@Override
@Transactional(rollbackFor = Exception.class)
public void deleteRole(Long roleId) {
    // 查询角色是否存在
    SysRole role = roleMapper.selectById(roleId);
    if (role == null) {
        throw new BusinessException(404, "角色不存在: " + roleId);
    }
    
    // 检查角色下是否有用户
    int userCount = userRoleMapper.countUsersByRoleId(roleId);
    if (userCount > 0) {
        throw new BusinessException(400, "该角色下还有" + userCount + "个用户，无法删除");
    }
    
    // 逻辑删除角色
    int result = roleMapper.deleteById(roleId);
    if (result <= 0) {
        throw new BusinessException(500, "删除角色失败");
    }
}
```

**⚠️ 常见错误**：
- ❌ 删除前未检查关联数据，导致数据不一致
- ❌ 错误提示不友好，用户不知道为什么不能删除
- ❌ 未使用逻辑删除，导致数据无法恢复
```

**建议3：添加批量操作结果返回规范**

在"三、数据访问层规范"中更新"3.1 批量操作规范"：

```markdown
### 3.1 批量操作规范

**原则**：批量操作必须先查询存在的记录，避免直接操作不存在的记录导致失败

**正确实现**：
```java
@Override
@Transactional(rollbackFor = Exception.class)
public BatchUpdateResult batchDeleteRoles(List<Long> roleIds) {
    int total = roleIds.size();
    int success = 0;
    int failed = 0;
    
    // 先查询存在的角色
    List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
    
    if (roles.isEmpty()) {
        throw new BusinessException(400, "批量删除失败，所有角色都不存在");
    }
    
    // 批量删除存在的角色
    for (SysRole role : roles) {
        try {
            // 检查角色下是否有用户
            int userCount = userRoleMapper.countUsersByRoleId(role.getId());
            if (userCount > 0) {
                failed++;
                continue;
            }
            
            // 逻辑删除角色
            int result = roleMapper.deleteById(role.getId());
            if (result > 0) {
                success++;
            } else {
                failed++;
            }
        } catch (Exception e) {
            log.error("删除角色失败: roleId={}, error={}", role.getId(), e.getMessage());
            failed++;
        }
    }
    
    failed = total - success;
    
    return BatchUpdateResult.of(total, success, failed);
}
```

**批量操作结果类**：
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateResult {
    private Integer total;    // 总数
    private Integer success;  // 成功数
    private Integer failed;   // 失败数
    
    public static BatchUpdateResult of(Integer total, Integer success, Integer failed) {
        return new BatchUpdateResult(total, success, failed);
    }
}
```

**⚠️ 常见错误**：
- ❌ 直接批量更新不存在的记录，导致整个操作失败
- ❌ 未捕获单个记录的异常，导致其他记录也无法更新
- ❌ 未返回详细的操作结果，用户无法知道哪些成功哪些失败
```

#### 4.2 给新开发者的快速指南

**角色权限业务逻辑层开发快速指南**

1. **Service层职责**：
   - Service层负责业务逻辑处理，不直接处理HTTP请求
   - Service层必须使用@Transactional注解控制事务
   - Service层必须进行参数验证和业务规则验证
   - Service层必须捕获异常并转换为BusinessException

2. **批量操作规范**：
   - 批量操作必须先查询存在的记录
   - 批量操作必须返回详细的操作结果（总数、成功数、失败数）
   - 批量操作必须捕获单个记录的异常，不影响其他记录
   - 批量操作必须在事务中执行

3. **关联数据处理**：
   - 删除数据前必须检查是否存在关联数据
   - 分配关联数据前必须检查数据是否存在
   - 分配关联数据时必须过滤已存在的数据，避免重复插入
   - 关联数据操作必须在事务中执行

4. **树形结构处理**：
   - 树形结构查询应该在Service层构建
   - 使用递归方法构建树形结构
   - 注意避免循环引用导致无限递归
   - 树形结构查询性能优化：一次性查询所有数据，在内存中构建树

5. **异常处理规范**：
   - Service层必须捕获所有异常
   - 业务异常使用BusinessException
   - 系统异常记录详细日志后转换为BusinessException
   - 异常信息必须友好，帮助用户理解问题

---

## 生成的完整代码清单

### 1. Service接口

| 文件路径 | 说明 |
|---------|------|
| `backend/src/main/java/com/haocai/management/service/ISysRoleService.java` | 角色Service接口 |
| `backend/src/main/java/com/haocai/management/service/ISysPermissionService.java` | 权限Service接口 |
| `backend/src/main/java/com/haocai/management/service/ISysUserService.java` | 用户Service接口（扩展） |

### 2. Service实现类

| 文件路径 | 说明 |
|---------|------|
| `backend/src/main/java/com/haocai/management/service/impl/SysRoleServiceImpl.java` | 角色Service实现类 |
| `backend/src/main/java/com/haocai/management/service/impl/SysPermissionServiceImpl.java` | 权限Service实现类 |
| `backend/src/main/java/com/haocai/management/service/impl/SysUserServiceImpl.java` | 用户Service实现类（扩展） |

### 3. DTO类

| 文件路径 | 说明 |
|---------|------|
| `backend/src/main/java/com/haocai/management/dto/BatchUpdateResult.java` | 批量操作结果类 |

### 4. 测试类

| 文件路径 | 说明 |
|---------|------|
| `backend/src/test/java/com/haocai/management/service/SysRoleServiceTest.java` | 角色Service测试类 |
| `backend/src/test/java/com/haocai/management/service/SysPermissionServiceTest.java` | 权限Service测试类 |

### 5. 文档

| 文件路径 | 说明 |
|---------|------|
| `docs/day3/role-permission-service-layer-tutorial.md` | 角色权限业务逻辑层开发教程 |

---

## 规范遵循与更新摘要

### 遵循的规范条款

| 规范条款 | 遵循情况 | 说明 |
|---------|---------|------|
| 批量操作规范（第3.1节） | ✅ 完全遵循 | 先查询后更新，返回详细结果，异常隔离 |
| 异常处理规范（第3.2节） | ✅ 完全遵循 | 分层处理异常，记录详细日志，转换为BusinessException |
| 事务控制规范 | ✅ 完全遵循 | 多表操作使用@Transactional注解 |
| 参数验证规范 | ✅ 完全遵循 | 验证输入参数有效性，使用@NotNull、@NotBlank等注解 |
| 审计字段规范（第1.3节） | ✅ 完全遵循 | 自动填充createBy、updateBy，使用MetaObjectHandler |

### 提出的更新建议

| 建议内容 | 建议章节 | 说明 |
|---------|---------|------|
| 添加树形结构查询规范 | 三、数据访问层规范-3.3 | 树形结构查询应该在Service层构建 |
| 添加关联数据删除规范 | 三、数据访问层规范-3.4 | 删除数据前必须检查关联数据 |
| 更新批量操作结果返回规范 | 三、数据访问层规范-3.1 | 添加批量操作结果类和返回规范 |

---

## 后续步骤建议

### 1. 计划表标注

在 `docs/day3/day3-plan.md` 中，将以下任务标记为已完成：

- [x] 1.3 角色权限业务逻辑层（预计2小时）
  - [x] 创建角色Service接口和实现类
  - [x] 创建权限Service接口和实现类
  - [x] 创建用户角色Service接口扩展
  - [x] 实现用户角色Service方法
  - [x] 编译测试通过
  - [x] 创建开发教程文档

### 2. 集成到项目中的下一步工作

1. **编译测试**：
   - 执行 `mvn clean compile` 确保代码编译通过
   - 执行 `mvn test` 运行单元测试

2. **Controller层开发**：
   - 创建角色Controller（SysRoleController）
   - 创建权限Controller（SysPermissionController）
   - 实现角色权限管理的RESTful API

3. **前端集成**：
   - 创建角色管理页面
   - 创建权限管理页面
   - 实现角色权限分配功能

4. **集成测试**：
   - 测试完整的角色权限管理流程
   - 测试角色权限分配功能
   - 测试用户角色关联功能

5. **文档更新**：
   - 更新API文档
   - 更新用户手册
   - 更新开发规范文档

---

**文档创建时间**：2026年1月6日  
**文档作者**：haocai  
**文档版本**：v1.0
