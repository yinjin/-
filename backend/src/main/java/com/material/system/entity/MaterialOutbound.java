package com.material.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出库单实体类
 */
@Data
@TableName("material_outbound")
public class MaterialOutbound {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("outbound_no")
    private String outboundNo;
    
    @TableField("warehouse_id")
    private Long warehouseId;
    
    @TableField("outbound_date")
    private LocalDateTime outboundDate;
    
    @TableField("total_quantity")
    private BigDecimal totalQuantity;
    
    @TableField("receiver")
    private String receiver;
    
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
