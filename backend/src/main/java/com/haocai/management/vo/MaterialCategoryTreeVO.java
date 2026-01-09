package com.haocai.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 耗材分类树形结构响应VO
 * 用于返回分类树形结构数据
 * 
 * @author haocai
 * @since 2026-01-08
 */
@Data
@Schema(description = "耗材分类树形结构响应VO")
public class MaterialCategoryTreeVO {
    
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
    
    @Schema(description = "子分类列表")
    private List<MaterialCategoryTreeVO> children;
}
