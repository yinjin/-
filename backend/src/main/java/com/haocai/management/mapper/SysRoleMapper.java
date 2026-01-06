package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 * 
 * 功能说明：
 * 1. 继承BaseMapper提供基础CRUD操作
 * 2. 提供角色相关的自定义查询方法
 * 3. 支持角色编码、状态等条件查询
 * 4. 支持分页查询
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色编码查询角色
     * 
     * @param roleCode 角色编码
     * @return 角色实体，如果不存在返回null
     */
    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode} AND deleted = 0")
    SysRole selectByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根据状态查询角色列表
     * 
     * @param status 状态（0-禁用，1-启用）
     * @return 角色列表
     */
    @Select("SELECT * FROM sys_role WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<SysRole> selectByStatus(@Param("status") Integer status);

    /**
     * 分页查询角色列表
     * 
     * @param page 分页对象
     * @param roleName 角色名称（模糊查询，可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM sys_role " +
            "WHERE deleted = 0 " +
            "<if test='roleName != null and roleName != \"\"'>" +
            "AND role_name LIKE CONCAT('%', #{roleName}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND status = #{status} " +
            "</if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<SysRole> selectPageByCondition(Page<SysRole> page,
                                         @Param("roleName") String roleName,
                                         @Param("status") Integer status);

    /**
     * 查询所有启用的角色
     * 
     * @return 启用的角色列表
     */
    @Select("SELECT * FROM sys_role WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<SysRole> selectAllEnabled();

    /**
     * 根据角色名称模糊查询
     * 
     * @param roleName 角色名称
     * @return 角色列表
     */
    @Select("SELECT * FROM sys_role WHERE role_name LIKE CONCAT('%', #{roleName}, '%') AND deleted = 0 ORDER BY create_time DESC")
    List<SysRole> selectByRoleName(@Param("roleName") String roleName);

    /**
     * 检查角色编码是否存在
     * 
     * @param roleCode 角色编码
     * @param excludeId 排除的角色ID（用于更新时检查）
     * @return 存在的数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM sys_role " +
            "WHERE role_code = #{roleCode} AND deleted = 0 " +
            "<if test='excludeId != null'>" +
            "AND id != #{excludeId} " +
            "</if>" +
            "</script>")
    int countByRoleCode(@Param("roleCode") String roleCode, @Param("excludeId") Long excludeId);

    /**
     * 检查角色名称是否存在
     * 
     * @param roleName 角色名称
     * @param excludeId 排除的角色ID（用于更新时检查）
     * @return 存在的数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM sys_role " +
            "WHERE role_name = #{roleName} AND deleted = 0 " +
            "<if test='excludeId != null'>" +
            "AND id != #{excludeId} " +
            "</if>" +
            "</script>")
    int countByRoleName(@Param("roleName") String roleName, @Param("excludeId") Long excludeId);
}
