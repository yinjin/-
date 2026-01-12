package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 供应商资质创建DTO
 * 
 * 遵循规范：
 * - 后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Data
@Schema(description = "供应商资质创建请求")
public class SupplierQualificationCreateDTO {

    /**
     * 供应商ID
     * 遵循：后端开发规范-第2.3条（参数校验：非空校验）
     */
    @NotNull(message = "供应商ID不能为空")
    @Schema(description = "供应商ID", required = true)
    private Long supplierId;

    /**
     * 资质类型
     * 遵循：后端开发规范-第2.3条（参数校验：非空校验、长度限制）
     */
    @NotBlank(message = "资质类型不能为空")
    @Size(max = 50, message = "资质类型长度不能超过50个字符")
    @Schema(description = "资质类型", required = true, example = "BUSINESS_LICENSE")
    private String qualificationType;

    /**
     * 资质名称
     * 遵循：后端开发规范-第2.3条（参数校验：非空校验、长度限制）
     */
    @NotBlank(message = "资质名称不能为空")
    @Size(max = 100, message = "资质名称长度不能超过100个字符")
    @Schema(description = "资质名称", required = true, example = "营业执照")
    private String qualificationName;

    /**
     * 资质文件URL
     * 遵循：后端开发规范-第2.3条（参数校验：长度限制）
     */
    @Size(max = 500, message = "文件URL长度不能超过500个字符")
    @Schema(description = "资质文件URL")
    private String fileUrl;

    /**
     * 原始文件名
     * 遵循：后端开发规范-第2.3条（参数校验：长度限制）
     */
    @Size(max = 200, message = "文件名长度不能超过200个字符")
    @Schema(description = "原始文件名")
    private String fileName;

    /**
     * 发证日期
     * 遵循：后端开发规范-第2.3条（参数校验：日期格式）
     */
    @Schema(description = "发证日期")
    private LocalDate issueDate;

    /**
     * 到期日期
     * 遵循：后端开发规范-第2.3条（参数校验：日期格式）
     */
    @Schema(description = "到期日期")
    private LocalDate expiryDate;

    /**
     * 发证机关
     * 遵循：后端开发规范-第2.3条（参数校验：长度限制）
     */
    @Size(max = 100, message = "发证机关名称长度不能超过100个字符")
    @Schema(description = "发证机关")
    private String issuingAuthority;

    /**
     * 备注描述
     * 遵循：后端开发规范-第2.3条（参数校验：长度限制）
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Schema(description = "备注")
    private String description;
}
