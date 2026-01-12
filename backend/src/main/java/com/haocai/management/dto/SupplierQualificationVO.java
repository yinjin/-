package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商资质响应VO
 * 
 * 遵循规范：
 * - 后端开发规范-第2.1条（统一响应格式）
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Data
@Schema(description = "供应商资质响应")
public class SupplierQualificationVO {

    /**
     * 资质ID
     */
    @Schema(description = "资质ID")
    private Long id;

    /**
     * 供应商ID
     */
    @Schema(description = "供应商ID")
    private Long supplierId;

    /**
     * 供应商名称（冗余字段，便于查询）
     */
    @Schema(description = "供应商名称")
    private String supplierName;

    /**
     * 资质类型
     */
    @Schema(description = "资质类型")
    private String qualificationType;

    /**
     * 资质名称
     */
    @Schema(description = "资质名称")
    private String qualificationName;

    /**
     * 资质文件URL
     */
    @Schema(description = "资质文件URL")
    private String fileUrl;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名")
    private String fileName;

    /**
     * 发证日期
     */
    @Schema(description = "发证日期")
    private LocalDate issueDate;

    /**
     * 到期日期
     */
    @Schema(description = "到期日期")
    private LocalDate expiryDate;

    /**
     * 发证机关
     */
    @Schema(description = "发证机关")
    private String issuingAuthority;

    /**
     * 状态：0-过期，1-有效
     */
    @Schema(description = "状态：0-过期，1-有效")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述")
    private String statusDesc;

    /**
     * 备注描述
     */
    @Schema(description = "备注")
    private String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 是否即将到期（30天内）
     */
    @Schema(description = "是否即将到期")
    private Boolean expiringSoon;
}
