package com.haocai.management.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存信息响应VO
 * 
 * <p>用于返回库存信息的响应对象</p>
 * 
 * @author haocai
 * @since 2026-01-13
 */
@Data
public class InventoryVO {

    /**
     * 库存ID
     */
    private Long id;

    /**
     * 耗材ID
     */
    private Long materialId;

    /**
     * 耗材名称
     */
    private String materialName;

    /**
     * 耗材编码
     */
    private String materialCode;

    /**
     * 耗材规格
     */
    private String specification;

    /**
     * 耗材单位
     */
    private String unit;

    /**
     * 耗材品牌
     */
    private String brand;

    /**
     * 耗材单价
     */
    private BigDecimal unitPrice;

    /**
     * 库存总数量
     */
    private Integer quantity;

    /**
     * 可用库存数量
     */
    private Integer availableQuantity;

    /**
     * 安全库存量
     */
    private Integer safeQuantity;

    /**
     * 最大库存量
     */
    private Integer maxQuantity;

    /**
     * 仓库编号
     */
    private String warehouse;

    /**
     * 库存位置
     */
    private String location;

    /**
     * 最后入库时间
     */
    private LocalDate lastInTime;

    /**
     * 最后出库时间
     */
    private LocalDate lastOutTime;

    /**
     * 总入库数量
     */
    private Integer totalInQuantity;

    /**
     * 总出库数量
     */
    private Integer totalOutQuantity;

    /**
     * 库存状态
     */
    private String status;

    /**
     * 库存状态描述
     */
    private String statusDescription;

    /**
     * 库存价值（数量 * 单价）
     */
    private BigDecimal inventoryValue;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
