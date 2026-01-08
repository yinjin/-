package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门分页查询DTO
 * 
 * 遵循规范：
 * - DTO命名规范：使用业务场景名称+DTO后缀
 * - 分页参数规范：使用page和size参数
 * - 查询条件规范：支持按状态、关键词搜索
 * 
 * @author haocai
 * @date 2026-01-08
 */
@Data
@Schema(description = "部门分页查询请求")
public class DepartmentQueryDTO {

    /**
     * 当前页码
     * 遵循：分页参数规范-第1条（从1开始计数）
     */
    @Schema(description = "当前页码（从1开始）", example = "1")
    private Integer page = 1;

    /**
     * 每页大小
     * 遵循：分页参数规范-第2条（合理的默认值）
     */
    @Schema(description = "每页记录数", example = "10")
    private Integer size = 10;

    /**
     * 部门状态
     */
    @Schema(description = "状态：NORMAL-正常，DISABLED-禁用", example = "NORMAL")
    private String status;

    /**
     * 关键词搜索（搜索名称或编码）
     */
    @Schema(description = "关键词搜索（搜索名称或编码）", example = "软件")
    private String keyword;

    /**
     * 父部门ID（查询指定部门的子部门）
     */
    @Schema(description = "父部门ID（查询指定部门的子部门）", example = "1")
    private Long parentId;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "sortOrder")
    private String orderBy = "sortOrder";

    /**
     * 排序方向
     * 遵循：排序规范-第1条（asc升序，desc降序）
     */
    @Schema(description = "排序方向：asc-升序，desc-降序", example = "asc")
    private String orderDir = "asc";
}
