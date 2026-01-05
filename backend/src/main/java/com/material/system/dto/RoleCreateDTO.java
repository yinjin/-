package com.material.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 角色创建DTO
 */
@Data
public class RoleCreateDTO {
    
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50")
    private String roleName;
    
    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
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
    @NotNull(message = "状态不能为空")
    private Integer status;
}
