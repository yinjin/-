package com.haocai.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户登录请求DTO
 * 用于接收用户登录时的请求参数
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Data
public class UserLoginDTO {

    /**
     * 用户名或邮箱或手机号
     * 支持多种登录方式：用户名、邮箱、手机号
     */
    @NotBlank(message = "用户名/邮箱/手机号不能为空")
    @Size(min = 3, max = 100, message = "用户名/邮箱/手机号长度必须在3-100个字符之间")
    private String username;

    /**
     * 登录密码
     * 用户输入的明文密码，后端会进行加密验证
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    /**
     * 验证码
     * 用于防止暴力破解的图形验证码
     * 可选字段，在高安全要求场景下启用
     */
    private String captcha;

    /**
     * 记住我
     * 是否记住登录状态，影响token过期时间
     */
    private Boolean rememberMe = false;

    /**
     * 登录IP地址
     * 客户端IP地址，用于安全审计和登录日志
     */
    private String ipAddress;

    /**
     * 登录设备信息
     * 浏览器User-Agent等设备信息
     */
    private String userAgent;
}