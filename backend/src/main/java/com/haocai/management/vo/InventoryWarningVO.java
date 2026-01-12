package com.haocai.management.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库存预警响应VO
 * 
 * <p>用于返回库存预警信息的响应对象</p>
 * 
 * @author haocai
 * @since 2026-01-13
 */
@Data
public class InventoryWarningVO {

    /**
     * 预警ID
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
     * 预警类型
     */
    private String warningType;

    /**
     * 预警类型描述
     */
    private String warningTypeDescription;

    /**
     * 当前数量
     */
    private Integer currentQuantity;

    /**
     * 阈值（安全库存或最大库存）
     */
    private Integer thresholdValue;

    /**
     * 预警时间
     */
    private LocalDateTime warningTime;

    /**
     * 处理状态
     */
    private String handleStatus;

    /**
     * 处理状态描述
     */
    private String handleStatusDescription;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 备注信息
     */
    private String remark;
}
