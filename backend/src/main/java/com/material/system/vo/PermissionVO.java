package com.material.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限VO
 */
@Data
public class PermissionVO {
    
    /**
     * 权限ID
     */
    private Long id;
    
    /**
     * 权限名称
     */
    private String permissionName;
    
    /**
     * 权限编码
     */
    private String permissionCode;
    
    /**
     * 资源类型: MENU-菜单, BUTTON-按钮, API-接口
     */
    private String resourceType;
    
    /**
     * 父权限ID，0表示顶级权限
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
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;
    
    /**
     * 子权限列表
     */
    private List<PermissionVO> children;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
