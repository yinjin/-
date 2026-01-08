package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体类
 * 
 * @author Haocai Management Team
 * @since 2026-01-06
 */
@Data
@TableName("sys_role")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     * 遵循：数据库字段命名规范-第1.1条（下划线命名法）
     * 使用 @JsonProperty 统一返回字段名为 name，与前端约定一致
     */
    @TableField("role_name")
    @JsonProperty("name")
    private String roleName;

    /**
     * 角色编码
     * 遵循：数据库字段命名规范-第1.1条（下划线命名法）
     * 使用 @JsonProperty 统一返回字段名为 code，与前端约定一致
     */
    @TableField("role_code")
    @JsonProperty("code")
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：1正常 0禁用
     */
    private Integer status;

    /**
     * 创建时间
     * 遵循：审计字段规范-第1.3条（create_time字段）
     * 遵循：字段自动填充规范-第2.3条（INSERT时自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 遵循：审计字段规范-第1.3条（update_time字段）
     * 遵循：字段自动填充规范-第2.3条（INSERT_UPDATE时自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     * 遵循：审计字段规范-第1.3条（create_by字段）
     * 遵循：字段自动填充规范-第2.3条（INSERT时自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     * 遵循：审计字段规范-第1.3条（update_by字段）
     * 遵循：字段自动填充规范-第2.3条（INSERT_UPDATE时自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 逻辑删除：0未删除 1已删除
     * 遵循：审计字段规范-第1.3条（deleted字段）
     * 遵循：逻辑删除配置（MyBatis-Plus配置）
     */
    @TableLogic
    private Integer deleted;
}
