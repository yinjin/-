package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 供应商查询请求DTO
 * 
 * 遵循development-standards.md中的DTO设计规范：
 * - DTO命名规范：使用业务名称+QueryDTO后缀
 * - 文档注解：使用Swagger注解描述字段
 * - 序列化：实现Serializable接口
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Data
@Schema(description = "供应商查询请求")
public class SupplierQueryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "供应商名称（模糊搜索）", example = "科技")
    private String supplierName;
    
    @Schema(description = "供应商编码（精确匹配）", example = "SUP001")
    private String supplierCode;
    
    @Schema(description = "联系人（模糊搜索）", example = "张")
    private String contactPerson;
    
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;
    
    @Schema(description = "信用等级最小值", example = "5")
    private Integer creditRatingMin;
    
    @Schema(description = "信用等级最大值", example = "10")
    private Integer creditRatingMax;
    
    @Schema(description = "合作状态（1-合作中，0-已终止）", example = "1")
    private Integer cooperationStatus;
    
    @Schema(description = "状态（0-禁用，1-启用）", example = "1")
    private Integer status;
    
    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "排序字段", example = "createTime")
    private String orderBy = "createTime";
    
    @Schema(description = "排序方向（asc/desc）", example = "desc")
    private String orderDirection = "desc";
}
