package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户数据访问层接口
 * 基于MyBatis-Plus的BaseMapper，提供基础CRUD操作
 * 并扩展自定义的用户查询方法
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     * 用于登录验证和用户名唯一性检查
     *
     * @param username 用户名
     * @return 用户对象，不存在则返回null
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     * 用于邮箱登录和邮箱唯一性检查
     *
     * @param email 邮箱地址
     * @return 用户对象，不存在则返回null
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND deleted = 0")
    SysUser selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     * 用于手机号登录和手机号唯一性检查
     *
     * @param phone 手机号
     * @return 用户对象，不存在则返回null
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND deleted = 0")
    SysUser selectByPhone(@Param("phone") String phone);

    /**
     * 根据部门ID查询用户列表
     * 用于部门用户管理
     *
     * @param departmentId 部门ID
     * @return 用户列表
     */
    @Select("SELECT * FROM sys_user WHERE department_id = #{departmentId} AND deleted = 0 ORDER BY create_time DESC")
    List<SysUser> selectByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 分页查询用户列表
     * 支持多条件组合查询
     *
     * @param page 分页对象
     * @param username 用户名关键词（可选）
     * @param name 真实姓名关键词（可选）
     * @param status 用户状态（可选）
     * @param departmentId 部门ID（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM sys_user WHERE deleted = 0" +
            "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%')</if>" +
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='departmentId != null'> AND department_id = #{departmentId}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    IPage<SysUser> selectUserPage(Page<SysUser> page,
                                  @Param("username") String username,
                                  @Param("name") String name,
                                  @Param("status") Integer status,
                                  @Param("departmentId") Long departmentId);

    /**
     * 统计各部门用户数量
     * 用于部门用户统计报表
     *
     * @return 部门用户统计列表
     */
    @Select("SELECT department_id, COUNT(*) as user_count FROM sys_user WHERE deleted = 0 AND department_id IS NOT NULL GROUP BY department_id")
    List<UserDepartmentStats> selectUserCountByDepartment();

    /**
     * 统计用户状态分布
     * 用于用户状态统计
     *
     * @return 用户状态统计列表
     */
    @Select("SELECT status, COUNT(*) as count FROM sys_user WHERE deleted = 0 GROUP BY status")
    List<UserStatusStats> selectUserCountByStatus();

    /**
     * 更新用户最后登录时间
     * 在用户成功登录后调用
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Select("UPDATE sys_user SET last_login_time = NOW() WHERE id = #{userId} AND deleted = 0")
    int updateLastLoginTime(@Param("userId") Long userId);

    /**
     * 检查用户名是否存在（排除指定用户ID）
     * 用于更新时的唯一性验证
     *
     * @param username 用户名
     * @param excludeUserId 排除的用户ID（更新时使用）
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username} AND id != #{excludeUserId} AND deleted = 0")
    int countByUsernameExcludeId(@Param("username") String username, @Param("excludeUserId") Long excludeUserId);

    /**
     * 检查邮箱是否存在（排除指定用户ID）
     * 用于更新时的唯一性验证
     *
     * @param email 邮箱
     * @param excludeUserId 排除的用户ID
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE email = #{email} AND id != #{excludeUserId} AND deleted = 0")
    int countByEmailExcludeId(@Param("email") String email, @Param("excludeUserId") Long excludeUserId);

    /**
     * 检查手机号是否存在（排除指定用户ID）
     * 用于更新时的唯一性验证
     *
     * @param phone 手机号
     * @param excludeUserId 排除的用户ID
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE phone = #{phone} AND id != #{excludeUserId} AND deleted = 0")
    int countByPhoneExcludeId(@Param("phone") String phone, @Param("excludeUserId") Long excludeUserId);

    /**
     * 批量更新用户状态
     * 用于批量禁用或启用用户
     *
     * @param userIds 用户ID列表
     * @param status 目标状态
     * @param updateBy 操作者ID
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE sys_user SET status = #{status}, update_time = NOW(), update_by = #{updateBy} " +
            "WHERE id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND deleted = 0" +
            "</script>")
    int updateStatusBatch(@Param("userIds") List<Long> userIds,
                         @Param("status") Integer status,
                         @Param("updateBy") Long updateBy);

    /**
     * 部门用户统计结果类
     */
    class UserDepartmentStats {
        private Long departmentId;
        private Integer userCount;

        // getters and setters
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public Integer getUserCount() { return userCount; }
        public void setUserCount(Integer userCount) { this.userCount = userCount; }
    }

    /**
     * 用户状态统计结果类
     */
    class UserStatusStats {
        private Integer status;
        private Integer count;

        // getters and setters
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
}
