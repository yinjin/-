package com.haocai.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 耗材视图对象VO
 * 
 * 遵循development-standards.md中的VO设计规范：
 * - VO命名规范：使用业务名称+VO后缀
 * - 文档注解：使用Swagger注解描述字段
 * - 序列化：实现Serializable接口
 * - 包含关联信息：如分类名称
 * 
 * @author haocai
 * @since 2026-01-09
 */
@Data
@Schema(description = "耗材信息")
public class MaterialVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "耗材ID", example = "1")
    private Long id;
    
    @Schema(description = "耗材名称", example = "医用口罩")
    private String materialName;
    
    @Schema(description = "耗材编码", example = "MAT001")
    private String materialCode;
    
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;
    
    @Schema(description = "分类名称", example = "防护用品")
    private String categoryName;
    
    @Schema(description = "供应商ID", example = "1")
    private Long supplierId;
    
    @Schema(description = "供应商名称", example = "医疗用品供应商")
    private String supplierName;
    
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
    
    @Schema(description = "状态（0-禁用，1-启用）", example = "1")
    private Integer status;
    
    @Schema(description = "创建时间", example = "2026-01-09T10:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2026-01-09T10:00:00")
    private LocalDateTime updateTime;
    
    @Schema(description = "创建人", example = "admin")
    private String createBy;
    
    @Schema(description = "更新人", example = "admin")
    private String updateBy;

    @Schema(description = "最小库存量", example = "10")
    private Integer minStock;

    @Schema(description = "最大库存量", example = "100")
    private Integer maxStock;

    @Schema(description = "安全库存量", example = "20")
    private Integer safetyStock;
}
