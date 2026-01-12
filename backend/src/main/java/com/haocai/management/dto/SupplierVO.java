package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 供应商信息响应VO
 * 
 * 遵循development-standards.md中的DTO设计规范：
 * - DTO命名规范：使用业务名称+VO后缀
 * - 文档注解：使用Swagger注解描述字段
 * - 序列化：实现Serializable接口
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Data
@Schema(description = "供应商信息响应")
public class SupplierVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "供应商ID", example = "1")
    private Long id;
    
    @Schema(description = "供应商编码", example = "SUP001")
    private String supplierCode;
    
    @Schema(description = "供应商名称", example = "北京科技有限公司")
    private String supplierName;
    
    @Schema(description = "联系人", example = "张三")
    private String contactPerson;
    
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;
    
    @Schema(description = "邮箱", example = "contact@company.com")
    private String email;
    
    @Schema(description = "地址", example = "北京市朝阳区xxx")
    private String address;
    
    @Schema(description = "营业执照", example = "https://example.com/license/123.jpg")
    private String businessLicense;
    
    @Schema(description = "税号", example = "91110000xxxxxxxx")
    private String taxNumber;
    
    @Schema(description = "银行账号", example = "622202xxxxxxxxxxxx")
    private String bankAccount;
    
    @Schema(description = "开户行", example = "中国银行北京支行")
    private String bankName;
    
    @Schema(description = "信用等级（1-10）", example = "8")
    private Integer creditRating;
    
    @Schema(description = "信用等级描述", example = "良好")
    private String creditRatingDescription;
    
    @Schema(description = "合作状态（1-合作中，0-已终止）", example = "1")
    private Integer cooperationStatus;
    
    @Schema(description = "合作状态描述", example = "合作中")
    private String cooperationStatusDescription;
    
    @Schema(description = "状态（0-禁用，1-启用）", example = "1")
    private Integer status;
    
    @Schema(description = "状态描述", example = "启用")
    private String statusDescription;
    
    @Schema(description = "供应商描述", example = "主要供应商，提供办公设备")
    private String description;
    
    @Schema(description = "创建时间", example = "2026-01-12 10:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2026-01-12 10:00:00")
    private LocalDateTime updateTime;
    
    @Schema(description = "创建人", example = "admin")
    private String createBy;
    
    @Schema(description = "更新人", example = "admin")
    private String updateBy;
}
