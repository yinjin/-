package com.haocai.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 权限更新DTO
 * 用于接收前端传递的权限更新请求参数
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
    @Size(max = 50, message = "权限名称长度不能超过50个字符")
    private String name;
    
    /**
     * 权限编码
     */
    @NotBlank(message = "权限编码不能为空")
    @Size(max = 100, message = "权限编码长度不能超过100个字符")
    private String code;

    /**
     * 权限类型（menu-菜单，button-按钮，api-接口）
     */
    @NotBlank(message = "权限类型不能为空")
    @Size(max = 20, message = "权限类型长度不能超过20个字符")
    private String type;

    /**
     * 父权限ID（顶级权限为0）
     */
    @NotNull(message = "父权限ID不能为空")
    private Long parentId;
    
    /**
     * 路由路径
     */
    @Size(max = 200, message = "路由路径长度不能超过200个字符")
    private String path;
    
    /**
     * 组件路径
     */
    @Size(max = 200, message = "组件路径长度不能超过200个字符")
    private String component;
    
    /**
     * 图标
     */
    @Size(max = 50, message = "图标长度不能超过50个字符")
    private String icon;
    
    /**
     * 排序号
     */
    private Integer sortOrder;
    
    /**
     * 状态（0-禁用，1-启用）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
