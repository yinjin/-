package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部门创建请求DTO
 * 
 * 遵循规范：
 * - DTO命名规范：使用业务场景名称+DTO后缀
 * - 字段验证规范：使用JSR-380验证注解
 * - 文档规范：使用@Schema注解描述字段
 * 
 * @author haocai
 * @date 2026-01-08
 */
@Data
@Schema(description = "部门创建请求")
public class DepartmentCreateDTO {

    /**
     * 部门名称
     * 遵循：验证规范-第1条（非空验证）
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    @Schema(description = "部门名称", required = true, example = "软件工程教研室")
    private String name;

    /**
     * 部门编码
     * 遵循：验证规范-第2条（格式验证）
     */
    @NotBlank(message = "部门编码不能为空")
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    @Schema(description = "部门编码（唯一）", required = true, example = "SE_GROUP")
    private String code;

    /**
     * 父部门ID
     * 遵循：验证规范-第3条（关联验证）
     */
    @Schema(description = "父部门ID（顶级部门传null）", example = "1")
    private Long parentId;

    /**
     * 排序号
     * 遵循：默认值规范-第1条（合理的默认值）
     */
    @Schema(description = "排序号（同级部门排序，数字越小越靠前）", example = "1")
    private Integer sortOrder = 0;

    /**
     * 部门负责人
     */
    @Size(max = 50, message = "部门负责人长度不能超过50个字符")
    @Schema(description = "部门负责人", example = "张三")
    private String leader;

    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    @Schema(description = "联系电话", example = "010-88888888")
    private String phone;

    /**
     * 电子邮箱
     */
    @Size(max = 100, message = "电子邮箱长度不能超过100个字符")
    @Schema(description = "电子邮箱", example = "dept@example.com")
    private String email;

    /**
     * 部门描述
     */
    @Size(max = 500, message = "部门描述长度不能超过500个字符")
    @Schema(description = "部门描述", example = "软件工程教研室负责软件工程专业教学")
    private String description;
}
