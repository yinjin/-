package com.material.system.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 物料分类VO
 */
@Data
public class MaterialCategoryVO {
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类编码
     */
    private String code;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
    /**
     * 父分类名称
     */
    private String parentName;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态：1正常 0禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 子分类列表
     */
    private List<MaterialCategoryVO> children;
}
