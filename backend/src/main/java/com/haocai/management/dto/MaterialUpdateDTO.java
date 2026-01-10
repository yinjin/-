package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;

/**
 * 耗材更新请求DTO
 * 
 * 遵循development-standards.md中的DTO设计规范：
 * - DTO命名规范：使用业务名称+UpdateDTO后缀
 * - 字段验证：使用Jakarta Validation注解进行参数校验
 * - 文档注解：使用Swagger注解描述字段
 * - 序列化：实现Serializable接口
 * 
 * @author haocai
 * @since 2026-01-09
 */
@Data
@Schema(description = "耗材更新请求")
public class MaterialUpdateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "耗材名称", required = true, example = "医用口罩")
    @NotBlank(message = "耗材名称不能为空")
    private String materialName;
    
    @Schema(description = "耗材编码", required = true, example = "MAT001")
    @NotBlank(message = "耗材编码不能为空")
    private String materialCode;
    
    @Schema(description = "分类ID", required = true, example = "1")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    @Schema(description = "供应商ID", example = "1")
    private Long supplierId;
    
    @Schema(description = "规格型号", example = "N95")
    private String specification;
    
    @Schema(description = "计量单位", example = "个")
    private String unit;
    
    @Schema(description = "品牌", example = "3M")
    private String brand;
    
    @Schema(description = "制造商", example = "3M中国有限公司")
    private String manufacturer;
    
    @Schema(description = "条形码", example = "6901234567890")
    private String barcode;
    
    @Schema(description = "二维码", example = "https://example.com/qr/123")
    private String qrCode;
    
    @Schema(description = "单价", example = "5.50")
    private BigDecimal unitPrice;
    
    @Schema(description = "技术参数", example = "过滤效率≥95%，呼吸阻力≤175Pa")
    private String technicalParameters;
    
    @Schema(description = "使用说明", example = "佩戴时确保口罩完全覆盖口鼻")
    private String usageInstructions;
    
    @Schema(description = "存储要求", example = "避光、干燥处保存")
    private String storageRequirements;
    
    @Schema(description = "图片URL", example = "https://example.com/images/material/123.jpg")
    private String imageUrl;

    @Schema(description = "描述", example = "医用防护口罩")
    private String description;

    @Schema(description = "最小库存量", example = "10")
    private Integer minStock;

    @Schema(description = "最大库存量", example = "100")
    private Integer maxStock;

    @Schema(description = "安全库存量", example = "20")
    private Integer safetyStock;

    @Schema(description = "状态（0-禁用，1-启用）", example = "1")
    private Integer status;
}
