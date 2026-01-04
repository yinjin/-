package com.material.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 物料信息更新DTO
 */
@Data
public class MaterialInfoUpdateDTO {
    
    /**
     * 物料ID
     */
    @NotNull(message = "物料ID不能为空")
    private Long id;
    
    /**
     * 物料名称
     */
    @Size(max = 200, message = "物料名称长度不能超过200个字符")
    private String name;
    
    /**
     * 物料编码
     */
    @Size(max = 50, message = "物料编码长度不能超过50个字符")
    private String code;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 规格
     */
    @Size(max = 200, message = "规格长度不能超过200个字符")
    private String specification;
    
    /**
     * 单位
     */
    @Size(max = 20, message = "单位长度不能超过20个字符")
    private String unit;
    
    /**
     * 价格
     */
    @DecimalMin(value = "0.00", message = "价格不能小于0")
    private BigDecimal price;
    
    /**
     * 库存数量
     */
    private Integer stockQuantity;
    
    /**
     * 最小库存
     */
    private Integer minStock;
    
    /**
     * 最大库存
     */
    private Integer maxStock;
    
    /**
     * 描述
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;
    
    /**
     * 图片URL
     */
    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    private String imageUrl;
    
    /**
     * 状态：1正常 0禁用
     */
    private Integer status;
}
