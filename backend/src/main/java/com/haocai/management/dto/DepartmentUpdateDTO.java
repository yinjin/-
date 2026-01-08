package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部门更新请求DTO
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
@Schema(description = "部门更新请求")
public class DepartmentUpdateDTO {

    /**
     * 部门ID
     * 遵循：验证规范-第4条（ID验证）
     */
    @NotNull(message = "部门ID不能为空")
    @Schema(description = "部门ID", required = true, example = "1")
    private Long id;

    /**
     * 部门名称
     * 遵循：验证规范-第1条（非空验证）
     */
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    @Schema(description = "部门名称", example = "软件工程教研室")
    private String name;

    /**
     * 部门编码
     * 遵循：验证规范-第2条（格式验证）
     */
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    @Schema(description = "部门编码（唯一）", example = "SE_GROUP")
    private String code;

    /**
     * 父部门ID
     * 遵循：验证规范-第3条（关联验证）
     */
    @Schema(description = "父部门ID（顶级部门传null）", example = "1")
    private Long parentId;

    /**
     * 排序号
     */
    @Schema(description = "排序号（同级部门排序，数字越小越靠前）", example = "1")
    private Integer sortOrder;

    /**
     * 部门状态
     * 遵循：数据库设计规范-第1.2条（枚举类型字段使用VARCHAR存储）
     */
    @Schema(description = "状态：NORMAL-正常，DISABLED-禁用", example = "NORMAL")
    private String status;

    /**
     * 部门负责人ID
     */
    @Schema(description = "部门负责人ID（关联sys_user表）", example = "1")
    private Long leaderId;

    /**
     * 联系方式
     */
    @Size(max = 200, message = "联系方式长度不能超过200个字符")
    @Schema(description = "联系方式", example = "010-88888888")
    private String contactInfo;

    /**
     * 部门描述
     */
    @Size(max = 500, message = "部门描述长度不能超过500个字符")
    @Schema(description = "部门描述", example = "软件工程教研室负责软件工程专业教学")
    private String description;
}
