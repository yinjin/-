package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
// 添加UserStatus枚举的import
import com.haocai.management.entity.UserStatus;

/**
 * 用户实体类
 * 用于映射数据库中的sys_user表，包含用户的基本信息和管理字段
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "sys_user")
public class SysUser {

    /**
     * 用户ID
     * 主键，自动递增，使用雪花算法生成唯一ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     * 用户登录系统的唯一标识符
     * 长度限制：3-20个字符，只能包含字母、数字、下划线
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    /**
     * 密码
     * 用户登录密码，经过BCrypt加密存储
     * 实际存储的是加密后的哈希值
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * 真实姓名
     * 用户的真实姓名，用于显示和身份验证
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;

    /**
     * 邮箱地址
     * 用户的邮箱地址，用于找回密码、通知等功能
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    /**
     * 手机号码
     * 用户的手机号码，用于短信验证、紧急联系等
     */
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    @Column(name = "phone", unique = true, nullable = false, length = 20)
    private String phone;

    /**
     * 头像URL
     * 用户头像的存储路径或URL地址
     */
    @Column(name = "avatar", length = 500)
    private String avatar;

    /**
     * 用户状态
     * 0-正常，1-禁用，2-锁定
     * 使用枚举类型管理状态值，提供更好的类型安全
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.NORMAL;

    /**
     * 部门ID
     * 用户所属部门的ID，用于数据权限控制
     */
    @Column(name = "department_id")
    private Long departmentId;

    /**
     * 创建时间
     * 记录用户账号的创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 记录用户信息的最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    /**
     * 最后登录时间
     * 记录用户最后一次成功登录的时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 创建者ID
     * 记录创建该用户账号的管理员ID
     */
    @TableField(fill = FieldFill.INSERT)
    @Column(name = "create_by")
    private Long createBy;

    /**
     * 更新者ID
     * 记录最后更新该用户账号的管理员ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * 备注信息
     * 用户的其他备注信息
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * 逻辑删除标志
     * 0-未删除，1-已删除
     * 使用逻辑删除而非物理删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Column(name = "deleted", nullable = false)
    private Integer deleted = 0;
}