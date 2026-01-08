package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haocai.management.entity.SysRolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联Mapper接口
 * 
 * 功能说明：
 * 1. 继承BaseMapper提供基础CRUD操作
 * 2. 提供角色权限关联的自定义查询方法
 * 3. 支持批量操作
 * 4. 支持唯一性检查
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 根据角色ID查询权限ID列表
     * 
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @Select("SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId} AND deleted = 0")
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID查询角色ID列表
     * 
     * @param permissionId 权限ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM sys_role_permission WHERE permission_id = #{permissionId} AND deleted = 0")
    List<Long> selectRoleIdsByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 根据角色ID查询角色权限关联列表
     * 
     * @param roleId 角色ID
     * @return 角色权限关联列表
     */
    @Select("SELECT * FROM sys_role_permission WHERE role_id = #{roleId} AND deleted = 0")
    List<SysRolePermission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID查询角色权限关联列表
     * 
     * @param permissionId 权限ID
     * @return 角色权限关联列表
     */
    @Select("SELECT * FROM sys_role_permission WHERE permission_id = #{permissionId} AND deleted = 0")
    List<SysRolePermission> selectByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 检查角色权限关联是否存在
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 存在的数量
     */
    @Select("SELECT COUNT(*) FROM sys_role_permission WHERE role_id = #{roleId} AND permission_id = #{permissionId} AND deleted = 0")
    int countByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 根据角色ID删除所有角色权限关联（逻辑删除）
     * 
     * @param roleId 角色ID
     * @return 删除的记录数
     */
    @Delete("UPDATE sys_role_permission SET deleted = 1 WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID删除所有角色权限关联（逻辑删除）
     * 
     * @param permissionId 权限ID
     * @return 删除的记录数
     */
    @Delete("UPDATE sys_role_permission SET deleted = 1 WHERE permission_id = #{permissionId}")
    int deleteByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 根据角色ID和权限ID列表删除关联（逻辑删除）
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 删除的记录数
     */
    @Delete("<script>" +
            "UPDATE sys_role_permission SET deleted = 1 " +
            "WHERE role_id = #{roleId} " +
            "AND permission_id IN " +
            "<foreach collection='permissionIds' item='permissionId' open='(' separator=',' close=')'>" +
            "#{permissionId}" +
            "</foreach>" +
            "</script>")
    int deleteByRoleIdAndPermissionIds(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 统计角色的权限数量
     * 
     * @param roleId 角色ID
     * @return 权限数量
     */
    @Select("SELECT COUNT(*) FROM sys_role_permission WHERE role_id = #{roleId} AND deleted = 0")
    int countPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 统计权限被分配给多少个角色
     * 
     * @param permissionId 权限ID
     * @return 角色数量
     */
    @Select("SELECT COUNT(*) FROM sys_role_permission WHERE permission_id = #{permissionId} AND deleted = 0")
    int countRolesByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 检查角色是否拥有指定权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 是否拥有权限
     */
    @Select("SELECT COUNT(*) > 0 FROM sys_role_permission WHERE role_id = #{roleId} AND permission_id = #{permissionId} AND deleted = 0")
    boolean hasPermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 根据角色ID列表查询所有权限ID（去重）
     * 
     * @param roleIds 角色ID列表
     * @return 权限ID列表
     */
    @Select("<script>" +
            "SELECT DISTINCT permission_id FROM sys_role_permission " +
            "WHERE deleted = 0 " +
            "AND role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    List<Long> selectPermissionIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 批量检查角色权限关联是否存在
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 已存在的权限ID列表
     */
    @Select("<script>" +
            "SELECT permission_id FROM sys_role_permission " +
            "WHERE role_id = #{roleId} AND deleted = 0 " +
            "AND permission_id IN " +
            "<foreach collection='permissionIds' item='permissionId' open='(' separator=',' close=')'>" +
            "#{permissionId}" +
            "</foreach>" +
            "</script>")
    List<Long> selectExistingPermissionIds(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 根据角色ID物理删除所有角色权限关联
     * 
     * @param roleId 角色ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    int physicalDeleteByRoleId(@Param("roleId") Long roleId);
}
