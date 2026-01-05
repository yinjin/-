package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.entity.SysUserLoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户登录日志数据访问层接口
 */
@Mapper
public interface SysUserLoginLogMapper extends BaseMapper<SysUserLoginLog> {

    /**
     * 分页查询用户登录日志
     * @param page 分页参数
     * @param userId 用户ID（可选）
     * @param username 用户名（可选）
     * @param loginSuccess 登录结果（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM sys_user_login_log WHERE 1=1" +
            "<if test='userId != null'> AND user_id = #{userId}</if>" +
            "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%')</if>" +
            "<if test='loginSuccess != null'> AND login_success = #{loginSuccess}</if>" +
            "<if test='startTime != null'> AND login_time >= #{startTime}</if>" +
            "<if test='endTime != null'> AND login_time &lt;= #{endTime}</if>" +
            " ORDER BY login_time DESC" +
            "</script>")
    IPage<SysUserLoginLog> selectLoginLogPage(Page<SysUserLoginLog> page,
                                             @Param("userId") Long userId,
                                             @Param("username") String username,
                                             @Param("loginSuccess") Boolean loginSuccess,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户登录次数
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 登录次数
     */
    @Select("SELECT COUNT(*) FROM sys_user_login_log " +
            "WHERE user_id = #{userId} AND login_success = true" +
            "<if test='startTime != null'> AND login_time >= #{startTime}</if>" +
            "<if test='endTime != null'> AND login_time &lt;= #{endTime}</if>")
    int countUserLoginTimes(@Param("userId") Long userId,
                           @Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);

    /**
     * 获取用户最后一次登录记录
     * @param userId 用户ID
     * @return 最后一次登录记录
     */
    @Select("SELECT * FROM sys_user_login_log " +
            "WHERE user_id = #{userId} AND login_success = true " +
            "ORDER BY login_time DESC LIMIT 1")
    SysUserLoginLog selectLastLoginLog(@Param("userId") Long userId);

    /**
     * 统计登录失败次数（用于安全监控）
     * @param userId 用户ID
     * @param startTime 开始时间
     * @return 失败次数
     */
    @Select("SELECT COUNT(*) FROM sys_user_login_log " +
            "WHERE user_id = #{userId} AND login_success = false" +
            "<if test='startTime != null'> AND login_time >= #{startTime}</if>")
    int countLoginFailTimes(@Param("userId") Long userId,
                           @Param("startTime") LocalDateTime startTime);

    /**
     * 清理过期登录日志
     * @param beforeTime 指定时间之前的日志
     * @return 删除的记录数
     */
    int deleteExpiredLogs(@Param("beforeTime") LocalDateTime beforeTime);
}