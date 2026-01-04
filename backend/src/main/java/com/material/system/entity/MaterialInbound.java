package com.material.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 入库单实体类
 */
@Data
@TableName("material_inbound")
public class MaterialInbound {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("inbound_no")
    private String inboundNo;
    
    @TableField("supplier_id")
    private Long supplierId;
    
    @TableField("warehouse_id")
    private Long warehouseId;
    
    @TableField("inbound_date")
    private LocalDateTime inboundDate;
    
    @TableField("total_quantity")
    private BigDecimal totalQuantity;
    
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    @TableField("operator")
    private String operator;
    
    @TableField("remark")
    private String remark;
    
    @TableField("status")
    private Integer status;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
