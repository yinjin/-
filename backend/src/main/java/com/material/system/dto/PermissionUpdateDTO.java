package com.material.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 权限更新DTO
 */
@Data
public class PermissionUpdateDTO {
    
    /**
     * 权限ID
     */
    @NotNull(message = "权限ID不能为空")
    private Long id;
    
    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;
    
    /**
     * 权限编码
     */
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;
    
    /**
     * 资源类型: MENU-菜单, BUTTON-按钮, API-接口
     */
    @NotBlank(message = "资源类型不能为空")
    private String resourceType;
    
    /**
     * 父权限ID，0表示顶级权限
     */
    @NotNull(message = "父权限ID不能为空")
    private Long parentId;
    
    /**
     * 路由路径
     */
    private String path;
    
    /**
     * 组件路径
     */
    private String component;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;
}
