package com.material.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 耗材信息实体类
 */
@Data
@TableName("material_info")
public class MaterialInfo {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("name")
    private String name;
    
    @TableField("code")
    private String code;
    
    @TableField("category_id")
    private Long categoryId;
    
    @TableField("specification")
    private String specification;
    
    @TableField("unit")
    private String unit;
    
    @TableField("price")
    private BigDecimal price;
    
    @TableField("stock_quantity")
    private Integer stockQuantity;
    
    @TableField("min_stock")
    private Integer minStock;
    
    @TableField("max_stock")
    private Integer maxStock;
    
    @TableField("description")
    private String description;
    
    @TableField("image_url")
    private String imageUrl;
    
    @TableField("status")
    private Integer status;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
