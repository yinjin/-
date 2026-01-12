package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存实体类
 * 
 * <p>库存信息管理实体，记录每个耗材在不同仓库的库存情况</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>库存数量管理（总数量、可用数量）</li>
 *   <li>库存预警管理（安全库存、最大库存）</li>
 *   <li>库存位置管理（仓库、货位）</li>
 *   <li>库存时间管理（最后入库时间、最后出库时间）</li>
 *   <li>库存统计管理（总入库量、总出库量）</li>
 * </ul>
 * 
 * <p>遵循规范：</p>
 * <ul>
 *   <li>数据库设计规范-第1.1条（字段命名规范：下划线命名法）</li>
 *   <li>数据库设计规范-第1.3条（审计字段规范：包含审计字段）</li>
 *   <li>实体类设计规范-第2.1条（字段映射规范）</li>
 * </ul>
 * 
 * @author haocai
 * @since 2026-01-13
 */
@Data
@TableName("material_inventory")
public class MaterialInventory {

    /**
     * 库存ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 耗材ID
     * 遵循：数据库设计规范-第1.2条（查询索引：外键字段必须建立索引）
     */
    private Long materialId;

    /**
     * 耗材名称（冗余字段，便于查询）
     */
    private String materialName;

    /**
     * 耗材编码（冗余字段，便于查询）
     */
    private String materialCode;

    /**
     * 库存总数量
     * 遵循：数据规范-第2条（非空校验：数量必须非负）
     */
    private Integer quantity;

    /**
     * 可用库存数量（未冻结的库存）
     * 遵循：数据规范-第2条（非空校验：数量必须非负）
     */
    private Integer availableQuantity;

    /**
     * 安全库存量（低于此值触发低库存预警）
     * 遵循：数据规范-第2条（非空校验：阈值范围验证）
     */
    private Integer safeQuantity;

    /**
     * 最大库存量（超过此值触发超储预警）
     * 遵循：数据规范-第2条（非空校验：阈值范围验证）
     */
    private Integer maxQuantity;

    /**
     * 仓库编号
     */
    private String warehouse;

    /**
     * 库存位置（货位号）
     */
    private String location;

    /**
     * 最后入库时间
     */
    private LocalDate lastInTime;

    /**
     * 最后出库时间
     */
    private LocalDate lastOutTime;

    /**
     * 总入库数量（累计）
     */
    private Integer totalInQuantity;

    /**
     * 总出库数量（累计）
     */
    private Integer totalOutQuantity;

    /**
     * 库存状态
     * 遵循：数据库设计规范-第1.1条（枚举存储：使用VARCHAR存储枚举名称）
     */
    private String status;

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
