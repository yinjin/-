package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 * 
 * 遵循规范：
 * - 数据库字段命名规范：下划线命名法
 * - Java实体类字段命名规范：驼峰命名法
 * - 字段映射规范：使用@TableField注解明确指定映射关系
 * - 审计字段规范：create_time、create_by、deleted
 * - 字段自动填充规范：实现MetaObjectHandler
 * - 逻辑删除规范：使用@TableLogic注解
 * - 唯一约束规范：user_id + role_id
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_role")
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     * 遵循：参数验证规范-第2条（数值范围验证）
     */
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    /**
     * 角色ID
     * 遵循：参数验证规范-第2条（数值范围验证）
     */
    @NotNull(message = "角色ID不能为空")
    @TableField("role_id")
    private Long roleId;

    /**
     * 创建时间
     * 遵循：审计字段规范-第1条（创建时间自动填充）
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人
     * 遵循：审计字段规范-第1.3条（create_by字段）
     * 遵循：字段自动填充规范-第2.3条（INSERT时自动填充）
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 逻辑删除：0未删除 1已删除
     * 遵循：逻辑删除规范-第1条（使用@TableLogic注解）
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
