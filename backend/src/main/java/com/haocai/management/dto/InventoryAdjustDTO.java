package com.haocai.management.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 库存调整请求DTO
 * 
 * <p>用于手动调整库存数量的请求参数</p>
 * 
 * @author haocai
 * @since 2026-01-13
 */
@Data
public class InventoryAdjustDTO {

    /**
     * 耗材ID
     * 遵循：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）
     */
    @NotNull(message = "耗材ID不能为空")
    private Long materialId;

    /**
     * 调整数量（正数增加，负数减少）
     * 遵循：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）
     */
    @NotNull(message = "调整数量不能为空")
    private Integer adjustQuantity;

    /**
     * 调整类型
     */
    private String adjustType;

    /**
     * 调整原因
     * 遵循：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）
     */
    @NotNull(message = "调整原因不能为空")
    private String reason;
}
