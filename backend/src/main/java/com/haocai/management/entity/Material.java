package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 耗材实体类
 * 
 * 遵循规范：
 * - 数据库设计规范-第1.1条（字段命名规范：下划线命名法）
 * - 数据库设计规范-第1.3条（审计字段规范：包含审计字段）
 * - 实体类设计规范-第2.1条（字段映射规范）
 * 
 * @author haocai
 * @since 2026-01-09
 */
@Data
@TableName("material")
public class Material {

    /**
     * 耗材ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 耗材名称
     */
    private String materialName;

    /**
     * 耗材编码（唯一）
     */
    private String materialCode;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 生产厂家
     */
    private String manufacturer;

    /**
     * 条形码
     */
    private String barcode;

    /**
     * 二维码
     */
    private String qrCode;

    /**
     * 单价
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 技术参数
     */
    private String technicalParameters;

    /**
     * 使用说明
     */
    private String usageInstructions;

    /**
     * 存储要求
     */
    private String storageRequirements;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 最小库存量
     */
    private Integer minStock;

    /**
     * 最大库存量
     */
    private Integer maxStock;

    /**
     * 安全库存量
     */
    private Integer safetyStock;

    /**
     * 耗材描述
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
