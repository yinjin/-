package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 供应商评价创建请求DTO
 * 
 * 遵循development-standards.md中的DTO设计规范：
 * - DTO命名规范：使用业务名称+CreateDTO后缀
 * - 字段验证：使用Jakarta Validation注解进行参数校验
 * - 文档注解：使用Swagger注解描述字段
 * - 序列化：实现Serializable接口
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Data
@Schema(description = "供应商评价创建请求")
public class SupplierEvaluationCreateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "供应商ID", required = true, example = "1")
    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;
    
    @Schema(description = "交付评分（1-10分）", required = true, example = "8.5")
    @NotNull(message = "交付评分不能为空")
    @DecimalMin(value = "1.0", message = "交付评分最小为1.0")
    @DecimalMax(value = "10.0", message = "交付评分最大为10.0")
    private BigDecimal deliveryScore;
    
    @Schema(description = "质量评分（1-10分）", required = true, example = "9.0")
    @NotNull(message = "质量评分不能为空")
    @DecimalMin(value = "1.0", message = "质量评分最小为1.0")
    @DecimalMax(value = "10.0", message = "质量评分最大为10.0")
    private BigDecimal qualityScore;
    
    @Schema(description = "服务评分（1-10分）", required = true, example = "8.0")
    @NotNull(message = "服务评分不能为空")
    @DecimalMin(value = "1.0", message = "服务评分最小为1.0")
    @DecimalMax(value = "10.0", message = "服务评分最大为10.0")
    private BigDecimal serviceScore;
    
    @Schema(description = "价格评分（1-10分）", required = true, example = "7.5")
    @NotNull(message = "价格评分不能为空")
    @DecimalMin(value = "1.0", message = "价格评分最小为1.0")
    @DecimalMax(value = "10.0", message = "价格评分最大为10.0")
    private BigDecimal priceScore;
    
    @Schema(description = "评价备注", example = "交货及时，产品质量稳定")
    private String remark;
}
