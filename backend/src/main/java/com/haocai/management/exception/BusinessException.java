package com.haocai.management.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于封装业务逻辑层的异常信息
 * 提供统一的异常处理机制
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param message 错误信息
     * @param cause 原始异常
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 用户不存在异常
     */
    public static BusinessException userNotFound() {
        return new BusinessException(1001, "用户不存在");
    }

    /**
     * 用户已被禁用异常
     */
    public static BusinessException userDisabled() {
        return new BusinessException(1002, "用户已被禁用");
    }

    /**
     * 用户名已存在异常
     */
    public static BusinessException usernameExists() {
        return new BusinessException(1003, "用户名已存在");
    }

    /**
     * 邮箱已存在异常
     */
    public static BusinessException emailExists() {
        return new BusinessException(1004, "邮箱已存在");
    }

    /**
     * 手机号已存在异常
     */
    public static BusinessException phoneExists() {
        return new BusinessException(1005, "手机号已存在");
    }

    /**
     * 密码错误异常
     */
    public static BusinessException passwordError() {
        return new BusinessException(1006, "密码错误");
    }

    /**
     * 原密码错误异常
     */
    public static BusinessException oldPasswordError() {
        return new BusinessException(1007, "原密码错误");
    }

    /**
     * 验证码错误异常
     */
    public static BusinessException captchaError() {
        return new BusinessException(1008, "验证码错误");
    }

    /**
     * 参数错误异常
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(1009, message);
    }

    /**
     * 数据不存在异常
     */
    public static BusinessException dataNotFound(String message) {
        return new BusinessException(1010, message);
    }

    /**
     * 操作无权限异常
     */
    public static BusinessException noPermission() {
        return new BusinessException(1011, "操作无权限");
    }

    /**
     * 数据已被删除异常
     */
    public static BusinessException dataDeleted() {
        return new BusinessException(1012, "数据已被删除");
    }

    /**
     * 用户已被锁定异常
     */
    public static BusinessException userLocked() {
        return new BusinessException(1014, "用户已被锁定");
    }

    /**
     * 操作失败异常
     */
    public static BusinessException operationFailed(String message) {
        return new BusinessException(1015, message);
    }
}