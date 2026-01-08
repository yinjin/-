package com.haocai.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限视图对象
 * 用于返回给前端的权限信息
 */
@Data
public class PermissionVO {
    
    /**
     * 权限ID
     */
    private Long id;
    
    /**
     * 权限名称
     * 使用@JsonProperty确保前端能正确获取permissionName字段
     */
    @JsonProperty("permissionName")
    private String name;
    
    /**
     * 权限编码
     * 使用@JsonProperty确保前端能正确获取permissionCode字段
     */
    @JsonProperty("permissionCode")
    private String code;
    
    /**
     * 权限类型（menu-菜单，button-按钮，api-接口）
     */
    private String type;
    
    /**
     * 父权限ID
     */
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
     * 排序号
     */
    private Integer sortOrder;
    
    /**
     * 状态（0-禁用，1-启用）
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
     * 创建人
     */
    private String createBy;
    
    /**
     * 更新人
     */
    private String updateBy;
    
    /**
     * 子权限列表（用于树形结构）
     */
    private List<PermissionVO> children;
}
