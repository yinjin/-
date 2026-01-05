package com.material.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色VO
 */
@Data
public class RoleVO {
    
    /**
     * 角色ID
     */
    private Long id;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色编码
     */
    private String roleCode;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;
    
    /**
     * 权限列表
     */
    private List<PermissionVO> permissions;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
