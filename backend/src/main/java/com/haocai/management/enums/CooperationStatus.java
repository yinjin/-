package com.haocai.management.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 供应商合作状态枚举
 * 
 * 遵循规范：
 * - 数据库设计规范-第1.2条（枚举存储：数据库使用VARCHAR存储枚举名称）
 * - 实体类设计规范-第2.1条（枚举处理：配置TypeHandler）
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Getter
@AllArgsConstructor
public enum CooperationStatus {
    
    /**
     * 合作中
     */
    COOPERATING(1, "合作中"),
    
    /**
     * 已终止
     */
    TERMINATED(0, "已终止");
    
    /**
     * 枚举值（存储到数据库的值）
     * 遵循：@EnumValue注解标记，MyBatis-Plus会将此值存入数据库
     */
    @EnumValue
    private final Integer value;
    
    /**
     * 枚举描述（用于前端展示）
     */
    @JsonValue
    private final String description;
    
    /**
     * 根据值获取枚举实例
     * 
     * @param value 枚举值
     * @return 枚举实例
     */
    public static CooperationStatus fromValue(Integer value) {
        if (value == null) {
            return COOPERATING; // 默认值
        }
        for (CooperationStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return COOPERATING; // 默认值
    }
    
    /**
     * 获取枚举描述
     * 
     * @return 枚举描述
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * 判断是否为合作中状态
     * 
     * @return true-合作中，false-已终止
     */
    public boolean isCooperating() {
        return this == COOPERATING;
    }
    
    /**
     * 判断是否为已终止状态
     * 
     * @return true-已终止，false-合作中
     */
    public boolean isTerminated() {
        return this == TERMINATED;
    }
}
