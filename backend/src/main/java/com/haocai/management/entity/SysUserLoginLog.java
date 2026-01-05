package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用户登录日志实体类
 * 记录用户的登录行为，用于安全审计和统计分析
 */
@Data
@TableName("sys_user_login_log")
@Entity
@Table(name = "sys_user_login_log")
public class SysUserLoginLog {

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 用户名（冗余字段，便于查询）
     */
    @TableField("username")
    @Column(name = "username", length = 50)
    private String username;

    /**
     * 登录IP地址
     */
    @TableField("login_ip")
    @Column(name = "login_ip", length = 45)
    private String loginIp;

    /**
     * 登录时间
     */
    @TableField("login_time")
    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    /**
     * 登录结果：true-成功，false-失败
     */
    @NotNull(message = "登录结果不能为空")
    @TableField("login_success")
    @Column(name = "login_success", nullable = false)
    private Boolean loginSuccess;

    /**
     * 失败原因（登录失败时填写）
     */
    @TableField("fail_reason")
    @Column(name = "fail_reason", length = 200)
    private String failReason;

    /**
     * 用户代理信息（浏览器、设备等）
     */
    @TableField("user_agent")
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 地理位置信息（可选）
     */
    @TableField("location")
    @Column(name = "location", length = 100)
    private String location;

    /**
     * 会话ID（可选，用于关联同一登录会话的多次操作）
     */
    @TableField("session_id")
    @Column(name = "session_id", length = 100)
    private String sessionId;
}