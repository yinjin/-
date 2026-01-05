package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用户登录日志实体类
 * 记录用户的登录行为，用于安全审计和统计分析
 */
@Data
@TableName("sys_user_login_log")
public class SysUserLoginLog {

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名（冗余字段，便于查询）
     */
    @TableField("username")
    private String username;

    /**
     * 登录IP地址
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 登录时间
     */
    @TableField("login_time")
    private LocalDateTime loginTime;

    /**
     * 登录结果：true-成功，false-失败
     */
    @NotNull(message = "登录结果不能为空")
    @TableField("login_success")
    private Boolean loginSuccess;

    /**
     * 失败原因（登录失败时填写）
     */
    @TableField("fail_reason")
    private String failReason;

    /**
     * 用户代理信息（浏览器、设备等）
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 地理位置信息（可选）
     */
    @TableField("location")
    private String location;

    /**
     * 会话ID（可选，用于关联同一登录会话的多次操作）
     */
    @TableField("session_id")
    private String sessionId;
}