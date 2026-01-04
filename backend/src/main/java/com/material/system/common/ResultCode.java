package com.material.system.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统错误"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    /**
     * 用户名或密码错误
     */
    USERNAME_PASSWORD_ERROR(10001, "用户名或密码错误"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(10002, "用户不存在"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXIST(10003, "用户已存在"),

    /**
     * token无效
     */
    TOKEN_INVALID(10004, "token无效"),

    /**
     * token已过期
     */
    TOKEN_EXPIRED(10005, "token已过期"),

    /**
     * 权限不足
     */
    PERMISSION_DENIED(10006, "权限不足"),

    /**
     * 数据已存在
     */
    DATA_EXIST(10007, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(10008, "数据不存在"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(10009, "文件上传失败"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_NOT_SUPPORTED(10010, "文件类型不支持"),

    /**
     * 文件大小超限
     */
    FILE_SIZE_EXCEEDED(10011, "文件大小超限"),

    /**
     * 导出失败
     */
    EXPORT_ERROR(10012, "导出失败"),

    /**
     * 导入失败
     */
    IMPORT_ERROR(10013, "导入失败"),

    /**
     * 密码错误
     */
    PASSWORD_ERROR(10014, "密码错误"),

    /**
     * 用户已禁用
     */
    USER_DISABLED(10015, "用户已禁用"),

    /**
     * 用户名已存在
     */
    USERNAME_ALREADY_EXIST(10016, "用户名已存在");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;
}
