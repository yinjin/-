package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haocai.management.entity.SysUserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 * 
 * 功能说明：
 * 1. 继承BaseMapper提供基础CRUD操作
 * 2. 提供用户角色关联的自定义查询方法
 * 3. 支持批量操作
 * 4. 支持唯一性检查
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID查询角色ID列表
     * 
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId} AND deleted = 0")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户ID列表
     * 
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @Select("SELECT user_id FROM sys_user_role WHERE role_id = #{roleId} AND deleted = 0")
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询用户角色关联列表
     * 
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    @Select("SELECT * FROM sys_user_role WHERE user_id = #{userId} AND deleted = 0")
    List<SysUserRole> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户角色关联列表
     * 
     * @param roleId 角色ID
     * @return 用户角色关联列表
     */
    @Select("SELECT * FROM sys_user_role WHERE role_id = #{roleId} AND deleted = 0")
    List<SysUserRole> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户角色关联是否存在
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 存在的数量
     */
    @Select("SELECT COUNT(*) FROM sys_user_role WHERE user_id = #{userId} AND role_id = #{roleId} AND deleted = 0")
    int countByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 根据用户ID物理删除所有用户角色关联
     * 
     * @param userId 用户ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    int physicalDeleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID删除所有用户角色关联（逻辑删除）
     * 
     * @param roleId 角色ID
     * @return 删除的记录数
     */
    @Delete("UPDATE sys_user_role SET deleted = 1 WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID和角色ID列表删除关联（逻辑删除）
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 删除的记录数
     */
    @Delete("<script>" +
            "UPDATE sys_user_role SET deleted = 1 " +
            "WHERE user_id = #{userId} " +
            "AND role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    int deleteByUserIdAndRoleIds(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 统计用户的角色数量
     * 
     * @param userId 用户ID
     * @return 角色数量
     */
    @Select("SELECT COUNT(*) FROM sys_user_role WHERE user_id = #{userId} AND deleted = 0")
    int countRolesByUserId(@Param("userId") Long userId);

    /**
     * 统计角色拥有多少个用户
     * 
     * @param roleId 角色ID
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM sys_user_role WHERE role_id = #{roleId} AND deleted = 0")
    int countUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有指定角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有角色
     */
    @Select("SELECT COUNT(*) > 0 FROM sys_user_role WHERE user_id = #{userId} AND role_id = #{roleId} AND deleted = 0")
    boolean hasRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 根据用户ID列表查询所有角色ID（去重）
     * 
     * @param userIds 用户ID列表
     * @return 角色ID列表
     */
    @Select("<script>" +
            "SELECT DISTINCT role_id FROM sys_user_role " +
            "WHERE deleted = 0 " +
            "AND user_id IN " +
            "<foreach collection='userIds' item='userId' open='(' separator=',' close=')'>" +
            "#{userId}" +
            "</foreach>" +
            "</script>")
    List<Long> selectRoleIdsByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 批量检查用户角色关联是否存在
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 已存在的角色ID列表
     */
    @Select("<script>" +
            "SELECT role_id FROM sys_user_role " +
            "WHERE user_id = #{userId} AND deleted = 0 " +
            "AND role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    List<Long> selectExistingRoleIds(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 根据用户ID查询用户角色关联列表（包含角色信息）
     * 
     * @param userId 用户ID
     * @return 用户角色关联列表（包含角色信息）
     */
    @Select("SELECT ur.*, r.role_name, r.role_code, r.status " +
            "FROM sys_user_role ur " +
            "INNER JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND ur.deleted = 0 AND r.deleted = 0 " +
            "ORDER BY ur.id ASC")
    List<SysUserRole> selectWithRoleInfoByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户角色关联列表（包含用户信息）
     * 
     * @param roleId 角色ID
     * @return 用户角色关联列表（包含用户信息）
     */
    @Select("SELECT ur.*, u.username, u.nickname, u.status " +
            "FROM sys_user_role ur " +
            "INNER JOIN sys_user u ON ur.user_id = u.id " +
            "WHERE ur.role_id = #{roleId} AND ur.deleted = 0 AND u.deleted = 0 " +
            "ORDER BY ur.id ASC")
    List<SysUserRole> selectWithUserInfoByRoleId(@Param("roleId") Long roleId);
}
