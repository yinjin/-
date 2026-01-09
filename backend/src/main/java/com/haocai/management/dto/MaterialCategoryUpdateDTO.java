package com.haocai.management.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 耗材分类更新请求DTO
 * 
 * 遵循规范：
 * - 实体类设计规范-第2.1条（字段映射规范）
 * - 参数验证规范
 * 
 * @author haocai
 * @since 2026-01-08
 */
@Data
public class MaterialCategoryUpdateDTO {

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 父分类ID（0表示顶级分类）
     */
    private Long parentId;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}
