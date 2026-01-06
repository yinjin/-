package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.dto.BatchUpdateResult;
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
