package com.haocai.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色视图对象
 * 用于返回角色信息给前端
 * 
 * 遵循：开发规范-第2条（DTO设计规范）
 * 遵循：开发规范-第5条（API响应规范）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO {
    
    /** 角色ID */
    private Long id;
    
    /** 角色名称 */
    private String name;
    
    /** 角色编码 */
    private String code;
    
    /** 角色描述 */
    private String description;
    
    /** 角色状态（0-禁用，1-启用） */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 创建人 */
    private String createBy;
    
    /** 更新人 */
    private String updateBy;
}
