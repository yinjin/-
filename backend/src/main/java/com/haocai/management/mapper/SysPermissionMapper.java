package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 * 
 * 功能说明：
 * 1. 继承BaseMapper提供基础CRUD操作
 * 2. 提供权限相关的自定义查询方法
 * 3. 支持树形结构查询
 * 4. 支持条件查询和分页查询
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据权限编码查询权限
     * 
     * @param permissionCode 权限编码
     * @return 权限对象，如果不存在返回null
     */
    @Select("SELECT * FROM sys_permission WHERE permission_code = #{permissionCode} AND deleted = 0")
    SysPermission selectByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 根据父权限ID查询子权限列表
     * 
     * @param parentId 父权限ID，如果为0或null则查询顶级权限
     * @return 子权限列表
     */
    @Select("<script>" +
            "SELECT * FROM sys_permission " +
            "WHERE deleted = 0 " +
            "<if test='parentId != null'>" +
            "AND parent_id = #{parentId} " +
            "</if>" +
            "<if test='parentId == null'>" +
            "AND (parent_id IS NULL OR parent_id = 0) " +
            "</if>" +
            "ORDER BY sort_order ASC, id ASC" +
            "</script>")
    List<SysPermission> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有顶级权限（parent_id为null或0）
     * 
     * @return 顶级权限列表
     */
    @Select("SELECT * FROM sys_permission WHERE (parent_id IS NULL OR parent_id = 0) AND deleted = 0 ORDER BY sort_order ASC, id ASC")
    List<SysPermission> selectTopLevelPermissions();


    /**
     * 根据状态查询权限列表
     * 
     * @param status 状态（1:启用 0:禁用）
     * @return 权限列表
     */
    @Select("SELECT * FROM sys_permission WHERE status = #{status} AND deleted = 0 ORDER BY sort_order ASC, id ASC")
    List<SysPermission> selectByStatus(@Param("status") Integer status);

    /**
     * 查询所有启用的权限
     * 
     * @return 启用的权限列表
     */
    @Select("SELECT * FROM sys_permission WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC, id ASC")
    List<SysPermission> selectAllEnabled();

    /**
     * 根据权限名称模糊查询
     * 
     * @param permissionName 权限名称（支持模糊匹配）
     * @return 权限列表
     */
    @Select("SELECT * FROM sys_permission WHERE permission_name LIKE CONCAT('%', #{permissionName}, '%') AND deleted = 0 ORDER BY sort_order ASC, id ASC")
    List<SysPermission> selectByPermissionName(@Param("permissionName") String permissionName);

    /**
     * 分页条件查询权限
     * 
     * @param page 分页对象
     * @param permissionName 权限名称（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM sys_permission " +
            "WHERE deleted = 0 " +
            "<if test='permissionName != null and permissionName != \"\"'>" +
            "AND permission_name LIKE CONCAT('%', #{permissionName}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND status = #{status} " +
            "</if>" +
            "ORDER BY sort_order ASC, id ASC" +
            "</script>")
    IPage<SysPermission> selectPageByCondition(Page<SysPermission> page,
                                               @Param("permissionName") String permissionName,
                                               @Param("status") Integer status);

    /**
     * 检查权限编码是否存在
     * 
     * @param permissionCode 权限编码
     * @return 存在的数量
     */
    @Select("SELECT COUNT(*) FROM sys_permission WHERE permission_code = #{permissionCode} AND deleted = 0")
    int countByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 检查权限名称是否存在
     * 
     * @param permissionName 权限名称
     * @return 存在的数量
     */
    @Select("SELECT COUNT(*) FROM sys_permission WHERE permission_name = #{permissionName} AND deleted = 0")
    int countByPermissionName(@Param("permissionName") String permissionName);

    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.deleted = 0 AND rp.deleted = 0 " +
            "ORDER BY p.sort_order ASC, p.id ASC")
    List<SysPermission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限列表（通过角色关联）
     * 直接使用数据库字段名，依赖MyBatis Plus的map-underscore-to-camel-case配置自动映射
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("SELECT DISTINCT p.id, p.permission_name AS name, p.permission_code AS code, " +
            "p.parent_id, p.path, p.component, p.icon, p.sort_order, p.status, " +
            "p.create_time, p.update_time, p.create_by, p.update_by, p.deleted " +
            "FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.deleted = 0 AND rp.deleted = 0 AND ur.deleted = 0 " +
            "ORDER BY p.sort_order ASC, p.id ASC")
    List<SysPermission> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询权限树形结构（递归查询所有权限）
     * 
     * @return 所有权限列表（按层级排序）
     */
    @Select("SELECT * FROM sys_permission WHERE deleted = 0 ORDER BY parent_id ASC, sort_order ASC, id ASC")
    List<SysPermission> selectAllForTree();

    /**
     * 查询权限树形结构（使用正确的JSON字段别名）
     * 这个方法确保返回的JSON使用permissionName和permissionCode字段名
     * 
     * @return 所有权限列表（按层级排序）
     */
    @Select("SELECT id, permission_name AS name, permission_code AS code, " +
            "permission_type AS type, parent_id, path, component, icon, sort_order, status, " +
            "create_time, update_time, create_by, " +
            "update_by, deleted " +
            "FROM sys_permission " +
            "WHERE deleted = 0 " +
            "ORDER BY parent_id ASC, sort_order ASC, id ASC")
    List<SysPermission> selectAllForTreeWithCorrectAliases();
}
