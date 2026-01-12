package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 供应商创建请求DTO
 * 
 * 遵循development-standards.md中的DTO设计规范：
 * - DTO命名规范：使用业务名称+CreateDTO后缀
 * - 字段验证：使用Jakarta Validation注解进行参数校验
 * - 文档注解：使用Swagger注解描述字段
 * - 序列化：实现Serializable接口
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Data
@Schema(description = "供应商创建请求")
public class SupplierCreateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "供应商名称", required = true, example = "北京科技有限公司")
    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 100, message = "供应商名称最大长度为100个字符")
    private String supplierName;
    
    @Schema(description = "供应商编码（可选，不传则自动生成）", example = "SUP001")
    private String supplierCode;
    
    @Schema(description = "联系人", example = "张三")
    @Size(max = 50, message = "联系人最大长度为50个字符")
    private String contactPerson;
    
    @Schema(description = "联系电话", example = "13800138000")
    @Size(max = 20, message = "联系电话最大长度为20个字符")
    private String phone;
    
    @Schema(description = "邮箱", example = "contact@company.com")
    @Size(max = 100, message = "邮箱最大长度为100个字符")
    private String email;
    
    @Schema(description = "地址", example = "北京市朝阳区xxx")
    @Size(max = 200, message = "地址最大长度为200个字符")
    private String address;
    
    @Schema(description = "营业执照", example = "https://example.com/license/123.jpg")
    @Size(max = 200, message = "营业执照URL最大长度为200个字符")
    private String businessLicense;
    
    @Schema(description = "税号", example = "91110000xxxxxxxx")
    @Size(max = 50, message = "税号最大长度为50个字符")
    private String taxNumber;
    
    @Schema(description = "银行账号", example = "622202xxxxxxxxxxxx")
    @Size(max = 100, message = "银行账号最大长度为100个字符")
    private String bankAccount;
    
    @Schema(description = "开户行", example = "中国银行北京支行")
    @Size(max = 100, message = "开户行最大长度为100个字符")
    private String bankName;
    
    @Schema(description = "信用等级（1-10）", example = "8")
    @NotNull(message = "信用等级不能为空")
    private Integer creditRating;
    
    @Schema(description = "合作状态（1-合作中，0-已终止）", example = "1")
    @NotNull(message = "合作状态不能为空")
    private Integer cooperationStatus;
    
    @Schema(description = "状态（0-禁用，1-启用）", example = "1")
    private Integer status;
    
    @Schema(description = "供应商描述", example = "主要供应商，提供办公设备")
    private String description;
    
    @Schema(description = "营业执照文件URL", example = "https://example.com/uploads/license.jpg")
    private String businessLicenseUrl;
    
    @Schema(description = "资质文件URL列表", example = "[\"https://example.com/uploads/qual1.pdf\", \"https://example.com/uploads/qual2.jpg\"]")
    private java.util.List<String> qualificationFiles;
}
