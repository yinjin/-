package com.haocai.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 耗材分类信息响应VO
 * 
 * 设计说明：
 * 1. 用于返回单个分类的详细信息
 * 2. 包含所有业务字段和审计字段
 * 3. 使用Swagger注解提供API文档
 * 
 * @author haocai
 * @since 2026-01-08
 */
@Data
@Schema(description = "耗材分类信息响应VO")
public class MaterialCategoryVO {
    
    @Schema(description = "分类ID")
    private Long id;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "分类编码")
    private String categoryCode;
    
    @Schema(description = "父分类ID（0表示顶级分类）")
    private Long parentId;
    
    @Schema(description = "分类层级（1-一级，2-二级，3-三级）")
    private Integer level;
    
    @Schema(description = "排序号")
    private Integer sortOrder;
    
    @Schema(description = "分类描述")
    private String description;
    
    @Schema(description = "状态（0-禁用，1-启用）")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "创建人")
    private String createBy;
    
    @Schema(description = "更新人")
    private String updateBy;
}
