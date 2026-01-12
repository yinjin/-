package com.haocai.management.exception;

import lombok.Getter;

/**
 * 供应商业务异常类
 * 
 * 遵循规范：
 * - 后端开发规范-第2.3条（异常处理：Service层捕获异常后，应抛出统一的BusinessException）
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Getter
public class SupplierException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final String errorCode;
    
    /**
     * 错误信息
     */
    private final String message;
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public SupplierException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 错误信息
     * @param cause 原始异常
     */
    public SupplierException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
    }
    
    /**
     * 供应商不存在异常
     * 
     * @param supplierId 供应商ID
     * @return SupplierException
     */
    public static SupplierException notFound(Long supplierId) {
        return new SupplierException(
            "SUPPLIER_NOT_FOUND",
            String.format("供应商不存在，ID: %d", supplierId)
        );
    }
    
    /**
     * 供应商不存在异常（根据编码）
     * 
     * @param supplierCode 供应商编码
     * @return SupplierException
     */
    public static SupplierException notFoundByCode(String supplierCode) {
        return new SupplierException(
            "SUPPLIER_NOT_FOUND",
            String.format("供应商不存在，编码: %s", supplierCode)
        );
    }
    
    /**
     * 供应商编码重复异常
     * 
     * @param supplierCode 供应商编码
     * @return SupplierException
     */
    public static SupplierException codeDuplicate(String supplierCode) {
        return new SupplierException(
            "SUPPLIER_CODE_DUPLICATE",
            String.format("供应商编码已存在: %s", supplierCode)
        );
    }
    
    /**
     * 供应商名称重复异常
     * 
     * @param supplierName 供应商名称
     * @return SupplierException
     */
    public static SupplierException nameDuplicate(String supplierName) {
        return new SupplierException(
            "SUPPLIER_NAME_DUPLICATE",
            String.format("供应商名称已存在: %s", supplierName)
        );
    }
    
    /**
     * 供应商有关联耗材异常
     * 
     * @param supplierId 供应商ID
     * @param materialCount 关联的耗材数量
     * @return SupplierException
     */
    public static SupplierException hasRelatedMaterials(Long supplierId, int materialCount) {
        return new SupplierException(
            "SUPPLIER_HAS_RELATED_MATERIALS",
            String.format("供应商有关联耗材，无法删除（关联耗材数: %d）", materialCount)
        );
    }
    
    /**
     * 供应商有关联入库记录异常
     * 
     * @param supplierId 供应商ID
     * @param orderCount 关联的入库单数量
     * @return SupplierException
     */
    public static SupplierException hasRelatedInboundOrders(Long supplierId, int orderCount) {
        return new SupplierException(
            "SUPPLIER_HAS_RELATED_INBOUND_ORDERS",
            String.format("供应商有关联入库记录，无法删除（关联入库单数: %d）", orderCount)
        );
    }
    
    /**
     * 供应商状态不允许操作异常
     * 
     * @param supplierId 供应商ID
     * @param currentStatus 当前状态
     * @param operation 操作名称
     * @return SupplierException
     */
    public static SupplierException statusNotAllowed(Long supplierId, Integer currentStatus, String operation) {
        return new SupplierException(
            "SUPPLIER_STATUS_NOT_ALLOWED",
            String.format("供应商ID: %d 当前状态: %d，不允许执行 %s 操作", supplierId, currentStatus, operation)
        );
    }
    
    /**
     * 信用等级超出范围异常
     * 
     * @param creditRating 信用等级
     * @param min 最小值
     * @param max 最大值
     * @return SupplierException
     */
    public static SupplierException creditRatingOutOfRange(Integer creditRating, int min, int max) {
        return new SupplierException(
            "SUPPLIER_CREDIT_RATING_OUT_OF_RANGE",
            String.format("信用等级 %d 超出有效范围 [%d, %d]", creditRating, min, max)
        );
    }
    
    /**
     * 供应商编码生成失败异常
     * 
     * @param reason 失败原因
     * @return SupplierException
     */
    public static SupplierException codeGenerationFailed(String reason) {
        return new SupplierException(
            "SUPPLIER_CODE_GENERATION_FAILED",
            String.format("供应商编码生成失败: %s", reason)
        );
    }
}
