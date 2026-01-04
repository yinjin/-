package com.material.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 耗材库存实体类
 */
@Data
@TableName("material_stock")
public class MaterialStock {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("material_id")
    private Long materialId;
    
    @TableField("warehouse_id")
    private Long warehouseId;
    
    @TableField("quantity")
    private BigDecimal quantity;
    
    @TableField("unit")
    private String unit;
    
    @TableField("min_stock")
    private BigDecimal minStock;
    
    @TableField("max_stock")
    private BigDecimal maxStock;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
