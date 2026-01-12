package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商资质实体类
 * 
 * 遵循规范：
 * - 数据库设计规范-第1.1条（字段命名规范：下划线命名法）
 * - 数据库设计规范-第1.3条（审计字段规范：包含审计字段）
 * - 实体类设计规范-第2.1条（字段映射规范）
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Data
@TableName("supplier_qualification")
public class SupplierQualification {

    /**
     * 资质ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 供应商ID
     * 遵循：数据库设计规范-第1.2条（外键索引设计）
     */
    private Long supplierId;

    /**
     * 资质类型
     * 遵循：数据库设计规范-第1.2条（唯一索引设计：与supplier_id组合唯一）
     */
    private String qualificationType;

    /**
     * 资质名称
     */
    private String qualificationName;

    /**
     * 资质文件URL
     */
    private String fileUrl;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 发证日期
     */
    private LocalDate issueDate;

    /**
     * 到期日期
     */
    private LocalDate expiryDate;

    /**
     * 发证机关
     */
    private String issuingAuthority;

    /**
     * 状态：0-过期，1-有效
     */
    private Integer status;

    /**
     * 备注描述
     */
    private String description;

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
    private Long createBy;

    /**
     * 更新人
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 删除标记：0-未删除，1-已删除
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableLogic
    private Integer deleted;
}
