package com.haocai.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求DTO
 * 用于接收用户注册时的请求参数
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Data
public class UserRegisterDTO {

    /**
     * 用户名
     * 用户登录系统的唯一标识符
     * 注册时需要验证用户名唯一性
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 登录密码
     * 用户设置的登录密码
     * 需要符合密码复杂度要求
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
             message = "密码必须包含至少一个大写字母、小写字母、数字和特殊字符")
    private String password;

    /**
     * 确认密码
     * 用于验证用户两次输入的密码是否一致
     * 前端验证，后端也需要再次确认
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 真实姓名
     * 用户的真实姓名，用于身份验证
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s]+$", message = "真实姓名只能包含中文、英文、数字和空格")
    private String name;

    /**
     * 邮箱地址
     * 用户的邮箱地址，用于激活账号、找回密码等
     * 注册时需要验证邮箱唯一性
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 手机号码
     * 用户的手机号码，用于短信验证
     * 注册时需要验证手机号唯一性
     */
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 部门ID
     * 用户所属部门的ID
     * 可选字段，新注册用户可以不指定部门
     */
    private Long departmentId;

    /**
     * 用户头像URL
     * 用户头像的网络地址
     * 可选字段，新注册用户可以使用默认头像
     */
    private String avatar;

    /**
     * 验证码
     * 邮箱或短信验证码，用于验证邮箱或手机号真实性
     * 可选字段，管理员创建用户时不需要验证码
     */
    @Size(min = 4, max = 6, message = "验证码长度必须在4-6个字符之间")
    private String verificationCode;

    /**
     * 注册来源
     * 记录用户是通过什么方式注册的
     * 如：web端注册、管理员创建等
     */
    private String registerSource = "web";

    /**
     * 同意用户协议
     * 用户是否同意平台用户协议和服务条款
     * 必须为true才能完成注册
     */
    private Boolean agreeToTerms = false;

    /**
     * 注册IP地址
     * 用户注册时的IP地址，用于安全审计
     */
    private String registerIp;
}
