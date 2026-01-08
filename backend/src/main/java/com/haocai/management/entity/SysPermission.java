package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体类
 * 
 * 遵循规范：
 * - 数据库字段命名规范：下划线命名法
 * - Java实体类字段命名规范：驼峰命名法
 * - 字段映射规范：使用@TableField注解明确指定映射关系
 * - 审计字段规范：create_time、update_time、create_by、update_by、deleted
 * - 字段自动填充规范：实现MetaObjectHandler
 * - 逻辑删除规范：使用@TableLogic注解
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_permission")
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     * 遵循：参数验证规范-第1条（必填字段验证）
     */
    @NotBlank(message = "权限名称不能为空")
    @TableField("permission_name")
    @JsonProperty("name")
    private String name;

    /**
     * 权限编码
     * 遵循：参数验证规范-第1条（必填字段验证）
     */
    @NotBlank(message = "权限编码不能为空")
    @TableField("permission_code")
    @JsonProperty("code")
    private String code;

    /**
     * 权限类型：menu/button/api
     * 遵循：参数验证规范-第1条（必填字段验证）
     */
    @NotBlank(message = "权限类型不能为空")
    @TableField("type")
    private String type;

    /**
     * 父权限ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 路由路径
     */
    @TableField("path")
    private String path;

    /**
     * 组件路径
     */
    @TableField("component")
    private String component;

    /**
     * 图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 排序
     * 遵循：参数验证规范-第2条（数值范围验证）
     */
    @NotNull(message = "排序不能为空")
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态：1正常 0禁用
     * 遵循：参数验证规范-第2条（数值范围验证）
     */
    @NotNull(message = "状态不能为空")
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     * 遵循：审计字段规范-第1条（创建时间自动填充）
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 遵循：审计字段规范-第2条（更新时间自动填充）
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     * 遵循：审计字段规范-第3条（创建人自动填充）
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人ID
     * 遵循：审计字段规范-第4条（更新人自动填充）
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 逻辑删除：0未删除 1已删除
     * 遵循：逻辑删除规范-第1条（使用@TableLogic注解）
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 子权限列表（非数据库字段，用于树形结构）
     * 遵循：树形结构规范-使用transient标记非持久化字段
     */
    @TableField(exist = false)
    private java.util.List<SysPermission> children;
}
