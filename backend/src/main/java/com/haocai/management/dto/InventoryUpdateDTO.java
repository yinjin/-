package com.haocai.management.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 库存更新请求DTO
 * 
 * <p>用于更新库存信息的请求参数</p>
 * 
 * @author haocai
 * @since 2026-01-13
 */
@Data
public class InventoryUpdateDTO {

    /**
     * 耗材ID
     * 遵循：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）
     */
    @NotNull(message = "耗材ID不能为空")
    private Long materialId;

    /**
     * 仓库编号
     */
    private String warehouse;

    /**
     * 库存位置
     */
    private String location;

    /**
     * 安全库存量
     * 遵循：数据规范-第2条（非空校验：阈值范围验证）
     */
    private Integer safeQuantity;

    /**
     * 最大库存量
     * 遵循：数据规范-第2条（非空校验：阈值范围验证）
     */
    private Integer maxQuantity;

    /**
     * 更新原因
     */
    private String remark;
}
