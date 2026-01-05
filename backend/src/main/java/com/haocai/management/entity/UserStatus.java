package com.haocai.management.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 * 定义用户账号的各种状态
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Getter
@AllArgsConstructor
public enum UserStatus {

    /**
     * 正常状态
     * 用户账号正常，可以正常登录和使用系统
     */
    NORMAL(0, "正常"),

    /**
     * 禁用状态
     * 用户账号被管理员禁用，无法登录系统
     * 可能是违反规定或长期未使用等原因
     */
    DISABLED(1, "禁用"),

    /**
     * 锁定状态
     * 用户账号因安全原因被临时锁定
     * 可能是密码错误次数过多或可疑登录行为
     */
    LOCKED(2, "锁定");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态码获取枚举值
     *
     * @param code 状态码
     * @return 对应的枚举值，不存在则返回null
     */
    public static UserStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
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
     * 判断是否为锁定状态
     *
     * @return true-锁定状态，false-非锁定状态
     */
    public boolean isLocked() {
        return this == LOCKED;
    }
}