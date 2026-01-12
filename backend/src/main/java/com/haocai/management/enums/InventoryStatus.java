package com.haocai.management.enums;

import lombok.Getter;

/**
 * 库存状态枚举
 * 
 * <p>定义库存的各种状态</p>
 * 
 * <p>状态说明：</p>
 * <ul>
 *   <li>NORMAL: 正常库存，数量在安全库存和最大库存之间</li>
 *   <li>LOW_STOCK: 低库存，可用数量低于安全库存</li>
 *   <li>OVER_STOCK: 超储，总数量超过最大库存</li>
 *   <li>OUT_OF_STOCK: 缺货，可用数量为0</li>
 * </ul>
 * 
 * @author haocai
 * @since 2026-01-13
 */
@Getter
public enum InventoryStatus {

    /**
     * 正常库存
     */
    NORMAL("正常"),

    /**
     * 低库存
     */
    LOW_STOCK("低库存"),

    /**
     * 超储
     */
    OVER_STOCK("超储"),

    /**
     * 缺货
     */
    OUT_OF_STOCK("缺货");

    /**
     * 状态描述
     */
    private final String description;

    InventoryStatus(String description) {
        this.description = description;
    }

    /**
     * 根据库存数量判断状态
     * 
     * <p>判断逻辑：</p>
     * <ul>
     *   <li>如果可用数量为0，返回缺货</li>
     *   <li>如果可用数量小于安全库存，返回低库存</li>
     *   <li>如果总数量大于最大库存，返回超储</li>
     *   <li>否则返回正常</li>
     * </ul>
     * 
     * @param availableQuantity 可用库存数量
     * @param totalQuantity 总库存数量
     * @param safeQuantity 安全库存数量
     * @param maxQuantity 最大库存数量
     * @return 库存状态
     */
    public static InventoryStatus judgeStatus(Integer availableQuantity, Integer totalQuantity, Integer safeQuantity, Integer maxQuantity) {
        if (availableQuantity == null || availableQuantity == 0) {
            return OUT_OF_STOCK;
        }
        if (safeQuantity != null && availableQuantity < safeQuantity) {
            return LOW_STOCK;
        }
        if (maxQuantity != null && totalQuantity != null && totalQuantity > maxQuantity) {
            return OVER_STOCK;
        }
        return NORMAL;
    }
}
