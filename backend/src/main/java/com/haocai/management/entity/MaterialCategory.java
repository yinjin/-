package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 耗材分类实体类
 * 
 * 遵循规范：
 * - 数据库设计规范-第1.1条（字段命名规范：下划线命名法）
 * - 数据库设计规范-第1.3条（审计字段规范：包含审计字段）
 * - 实体类设计规范-第2.1条（字段映射规范）
 * 
 * @author haocai
 * @since 2026-01-08
 */
@Data
@TableName("material_category")
public class MaterialCategory {

    /**
     * 分类ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类编码（唯一）
     */
    private String categoryCode;

    /**
     * 父分类ID，0表示顶级分类
     */
    private Long parentId;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 删除标记：0-未删除，1-已删除
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableLogic
    private Integer deleted;
}
