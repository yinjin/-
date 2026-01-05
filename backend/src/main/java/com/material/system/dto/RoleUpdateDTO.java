package com.material.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 角色更新DTO
 */
@Data
public class RoleUpdateDTO {
    
    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long id;
    
    /**
     * 角色名称
     */
    @Size(max = 50, message = "角色名称长度不能超过50")
    private String roleName;
    
    /**
     * 角色编码
     */
    @Size(max = 50, message = "角色编码长度不能超过50")
    private String roleCode;
    
    /**
     * 角色描述
     */
    @Size(max = 200, message = "角色描述长度不能超过200")
    private String description;
    
    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;
}
