package com.haocai.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息更新请求DTO
 * 用于接收用户更新个人信息时的请求参数
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Data
public class UserUpdateDTO {

    /**
     * 用户ID
     * 要更新的用户ID，必填字段
     */
    private Long id;

    /**
     * 真实姓名
     * 用户的真实姓名，可选更新字段
     */
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z\\s]+$", message = "真实姓名只能包含中文、英文和空格")
    private String name;

    /**
     * 邮箱地址
     * 用户的邮箱地址，可选更新字段
     * 更新时需要验证邮箱唯一性（排除当前用户）
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 手机号码
     * 用户的手机号码，可选更新字段
     * 更新时需要验证手机号唯一性（排除当前用户）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 头像URL
     * 用户头像的存储路径或URL地址
     */
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String avatar;

    /**
     * 部门ID
     * 用户所属部门的ID，可选更新字段
     */
    private Long departmentId;

    /**
     * 备注信息
     * 用户的其他备注信息，可选更新字段
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 版本号
     * 用于乐观锁控制，防止并发更新冲突
     * 前端需要传递当前数据的版本号
     */
    private Integer version;

    /**
     * 更新者ID
     * 执行更新操作的用户ID
     * 通常从当前登录用户的token中获取
     */
    private Long updateBy;
}
