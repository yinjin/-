package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商评价实体类
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
@TableName("supplier_evaluation")
public class SupplierEvaluation {

    /**
     * 评价ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 供应商ID
     * 遵循：数据库设计规范-第1.2条（外键索引设计）
     */
    private Long supplierId;

    /**
     * 评价人ID
     */
    private Long evaluatorId;

    /**
     * 评价人名称（冗余存储，便于查询）
     */
    private String evaluatorName;

    /**
     * 评价日期
     */
    private LocalDate evaluationDate;

    /**
     * 交付评分（1-10分）
     */
    private BigDecimal deliveryScore;

    /**
     * 质量评分（1-10分）
     */
    private BigDecimal qualityScore;

    /**
     * 服务评分（1-10分）
     */
    private BigDecimal serviceScore;

    /**
     * 价格评分（1-10分）
     */
    private BigDecimal priceScore;

    /**
     * 总分
     */
    private BigDecimal totalScore;

    /**
     * 平均分
     */
    private BigDecimal averageScore;

    /**
     * 信用等级（1-10，基于平均分计算）
     */
    private Integer creditRating;

    /**
     * 评价备注
     */
    private String remark;

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
     * 删除标记：0-未删除，1-已删除
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableLogic
    private Integer deleted;
}
