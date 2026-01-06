package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.dto.BatchUpdateResult;
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
 * - 树形结构规范：支持递归查询子节点
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
     * 遵循：事务控制规范、异常处理规范、树形结构规范
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
     * @param permissionType 权限类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<PermissionVO> getPermissionPage(Page<SysPermission> page, 
                                          String permissionName, 
                                          String permissionType, 
                                          Integer status);

    /**
     * 查询权限树形结构
     * 
     * 遵循：树形结构规范（递归查询）
     * 
     * @return 权限树
     */
    List<PermissionVO> getPermissionTree();

    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<PermissionVO> getPermissionsByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限列表（通过角色）
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<PermissionVO> getPermissionsByUserId(Long userId);

    /**
     * 查询所有启用的权限
     * 
     * @return 启用的权限列表
     */
    List<PermissionVO> getAllEnabledPermissions();
}
