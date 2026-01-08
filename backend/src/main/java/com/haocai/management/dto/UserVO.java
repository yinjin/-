package com.haocai.management.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应VO
 * 用于返回用户信息给前端的响应对象
 * 不包含敏感信息如密码
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Data
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 用户状态
     * 0-正常，1-禁用，2-锁定
     */
    private Integer status;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 部门名称
     * 从关联的部门表中获取，便于前端显示
     */
    private String departmentName;

    /**
     * 部门对象
     * 完整的部门信息，包含部门ID、名称、编码等
     * 遵循：前端类型规范-与后端DTO/VO保持一致
     */
    private DepartmentVO department;

    /**
     * 用户角色列表
     * 用户拥有的所有角色名称列表
     */
    private String[] roles;

    /**
     * 用户权限列表
     * 用户拥有的所有权限代码列表
     */
    private String[] permissions;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 备注信息
     */
    private String remark;
}
