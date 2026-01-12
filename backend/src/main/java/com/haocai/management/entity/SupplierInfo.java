package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.haocai.management.enums.CooperationStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 供应商信息实体类
 * 
 * 遵循规范：
 * - 数据库设计规范-第1.1条（字段命名规范：下划线命名法）
 * - 数据库设计规范-第1.3条（审计字段规范：包含审计字段）
 * - 实体类设计规范-第2.1条（字段映射规范）
 * - 实体类设计规范-第2.2条（枚举处理：使用@EnumValue注解）
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Data
@TableName("supplier_info")
public class SupplierInfo {

    /**
     * 供应商ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 供应商编码（唯一）
     * 遵循：数据库设计规范-第1.2条（唯一索引设计）
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 营业执照
     */
    private String businessLicense;

    /**
     * 税号
     */
    private String taxNumber;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 信用等级（1-10）
     */
    private Integer creditRating;

    /**
     * 合作状态
     * 遵循：实体类设计规范-第2.2条（枚举处理）
     */
    private CooperationStatus cooperationStatus;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 供应商描述
     */
    private String description;

    /**
     * 创建时间
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 删除标记：0-未删除，1-已删除
     * 遵循：数据库设计规范-第1.3条（审计字段规范）
     */
    @TableLogic
    private Integer deleted;
}
