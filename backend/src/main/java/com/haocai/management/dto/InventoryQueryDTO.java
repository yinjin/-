package com.haocai.management.dto;

import lombok.Data;

/**
 * 库存查询请求DTO
 * 
 * <p>用于库存列表查询的请求参数</p>
 * 
 * @author haocai
 * @since 2026-01-13
 */
@Data
public class InventoryQueryDTO {

    /**
     * 耗材ID
     */
    private Long materialId;

    /**
     * 耗材名称（模糊查询）
     */
    private String materialName;

    /**
     * 耗材编码
     */
    private String materialCode;

    /**
     * 仓库编号
     */
    private String warehouse;

    /**
     * 库存状态
     */
    private String status;

    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方向
     */
    private String orderDirection;
}
