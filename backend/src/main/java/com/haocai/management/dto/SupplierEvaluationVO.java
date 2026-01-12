package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商评价响应VO
 * 
 * 遵循development-standards.md中的VO设计规范：
 * - VO命名规范：使用业务名称+VO后缀
 * - 字段描述：使用Swagger注解描述字段
 * - 序列化：实现Serializable接口
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Data
@Schema(description = "供应商评价响应")
public class SupplierEvaluationVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "评价ID")
    private Long id;
    
    @Schema(description = "供应商ID")
    private Long supplierId;
    
    @Schema(description = "供应商名称")
    private String supplierName;
    
    @Schema(description = "评价人ID")
    private Long evaluatorId;
    
    @Schema(description = "评价人名称")
    private String evaluatorName;
    
    @Schema(description = "评价日期")
    private LocalDate evaluationDate;
    
    @Schema(description = "交付评分（1-10分）")
    private BigDecimal deliveryScore;
    
    @Schema(description = "质量评分（1-10分）")
    private BigDecimal qualityScore;
    
    @Schema(description = "服务评分（1-10分）")
    private BigDecimal serviceScore;
    
    @Schema(description = "价格评分（1-10分）")
    private BigDecimal priceScore;
    
    @Schema(description = "总分")
    private BigDecimal totalScore;
    
    @Schema(description = "平均分")
    private BigDecimal averageScore;
    
    @Schema(description = "信用等级（1-10）")
    private Integer creditRating;
    
    @Schema(description = "信用等级描述")
    private String creditRatingDescription;
    
    @Schema(description = "评价备注")
    private String remark;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
