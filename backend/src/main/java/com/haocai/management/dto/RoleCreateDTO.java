package com.haocai.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色创建DTO
 * 
 * 遵循规范：
 * - DTO设计规范：创建DTO只包含创建时需要的字段
 * - 参数验证规范：使用Jakarta Validation注解进行参数验证
 * - 字段命名规范：驼峰命名法
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Data
public class RoleCreateDTO {

    /**
     * 角色名称
     * 遵循：参数验证规范-第1条（必填字段验证）
     * 遵循：参数验证规范-第3条（字符串长度验证）
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 100, message = "角色名称长度不能超过100个字符")
    private String name;

    /**
     * 角色编码
     * 遵循：参数验证规范-第1条（必填字段验证）
     * 遵循：参数验证规范-第3条（字符串长度验证）
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    private String code;

    /**
     * 角色描述
     * 遵循：参数验证规范-第3条（字符串长度验证）
     */
    @Size(max = 255, message = "角色描述长度不能超过255个字符")
    private String description;

    /**
     * 状态：1正常 0禁用
     * 遵循：参数验证规范-第2条（数值范围验证）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
