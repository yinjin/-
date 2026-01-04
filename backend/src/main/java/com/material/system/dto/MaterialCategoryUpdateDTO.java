package com.material.system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 物料分类更新DTO
 */
@Data
public class MaterialCategoryUpdateDTO {
    
    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long id;
    
    /**
     * 分类名称
     */
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;
    
    /**
     * 分类编码
     */
    @Size(max = 50, message = "分类编码长度不能超过50个字符")
    private String code;
    
    /**
     * 描述
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态：1正常 0禁用
     */
    private Integer status;
}
