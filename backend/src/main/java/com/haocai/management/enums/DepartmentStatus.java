package com.haocai.management.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 部门状态枚举
 * 定义部门实体的各种状态
 *
 * <p>遵循规范：</p>
 * <ul>
 *   <li>数据库设计规范-第1.2条：字段类型规范（枚举类型使用VARCHAR存储）</li>
 *   <li>实体类设计规范-第2.2条：类型转换器规范（实现BaseTypeHandler）</li>
 * </ul>
 *
 * @author 开发团队
 * @since 2026-01-08
 * @version 1.0
 */
@Getter
public enum DepartmentStatus {

    /**
     * 正常状态
     * 部门正常运营，可以进行业务操作
     */
    NORMAL(1, "正常"),

    /**
     * 禁用状态
     * 部门已被禁用，无法进行新增业务操作，但不影响历史数据查询
     */
    DISABLED(0, "禁用");

    /**
     * 状态码
     * 使用@EnumValue注解标记，MyBatis-Plus会将此值存入数据库
     * 使用@JsonValue注解标记，Jackson序列化时会使用此值
     *
     * 遵循：实体类设计规范-第2.2条（@EnumValue注解标记）
     */
    @EnumValue
    @JsonValue
    private final Integer code;

    /**
     * 状态描述
     * 用于前端展示和日志记录
     */
    private final String description;

    /**
     * 构造函数
     */
    DepartmentStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取枚举值
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>数据库查询结果转换为枚举</li>
     *   <li>API参数接收后转换为枚举</li>
     * </ul>
     *
     * @param code 状态码
     * @return 对应的枚举值，不存在则返回null
     */
    public static DepartmentStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DepartmentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据状态名称获取枚举值
     *
     * @param name 状态名称（枚举常量名）
     * @return 对应的枚举值，不存在则返回null
     */
    public static DepartmentStatus getByName(String name) {
        if (name == null) {
            return null;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 判断是否为正常状态
     *
     * @return true-正常状态，false-非正常状态
     */
    public boolean isNormal() {
        return this == NORMAL;
    }

    /**
     * 判断是否为禁用状态
     *
     * @return true-禁用状态，false-非禁用状态
     */
    public boolean isDisabled() {
        return this == DISABLED;
    }

    /**
     * 获取状态的数据库存储值
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>直接获取数据库存储值进行SQL操作</li>
     *   <li>日志记录状态值</li>
     * </ul>
     *
     * @return 状态码
     */
    public int getStorageValue() {
        return this.code;
    }
}
