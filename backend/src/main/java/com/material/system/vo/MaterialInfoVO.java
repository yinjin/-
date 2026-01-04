package com.material.system.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 物料信息VO
 */
@Data
public class MaterialInfoVO {
    
    /**
     * 物料ID
     */
    private Long id;
    
    /**
     * 物料名称
     */
    private String name;
    
    /**
     * 物料编码
     */
    private String code;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 规格
     */
    private String specification;
    
    /**
     * 单位
     */
    private String unit;
    
    /**
     * 价格
     */
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
     * 库存状态：1正常 2低于最小库存 3高于最大库存
     */
    private Integer stockStatus;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 状态：1正常 0禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
