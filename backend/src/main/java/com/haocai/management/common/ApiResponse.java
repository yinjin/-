package com.haocai.management.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一API响应格式
 * 为所有API接口提供统一的响应格式，便于前端处理和错误处理
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 响应状态码
     * 200-成功，400-客户端错误，500-服务器错误
     */
    private Integer code;

    /**
     * 响应消息
     * 成功时为"success"，失败时为具体的错误信息
     */
    private String message;

    /**
     * 响应数据
     * 实际的业务数据，泛型支持不同类型的数据
     */
    private T data;

    /**
     * 响应时间戳
     * 记录响应的时间，用于调试和日志记录
     */
    private LocalDateTime timestamp;

    /**
     * 请求ID（可选）
     * 用于跟踪单个请求的完整生命周期
     */
    private String requestId;

    /**
     * 创建成功的响应
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data, LocalDateTime.now(), null);
    }

    /**
     * 创建成功的响应（带消息）
     *
     * @param data 响应数据
     * @param message 成功消息
     * @param <T> 数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data, LocalDateTime.now(), null);
    }

    /**
     * 创建成功的响应（无数据）
     *
     * @return ApiResponse实例
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(200, "success", null, LocalDateTime.now(), null);
    }

    /**
     * 创建成功的响应（无数据，带消息）
     *
     * @param message 成功消息
     * @return ApiResponse实例
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(200, message, null, LocalDateTime.now(), null);
    }

    /**
     * 创建失败的响应
     *
     * @param code 错误码
     * @param message 错误信息
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null, LocalDateTime.now(), null);
    }

    /**
     * 创建失败的响应（使用默认错误码）
     *
     * @param message 错误信息
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null, LocalDateTime.now(), null);
    }

    /**
     * 创建失败的响应（带数据）
     *
     * @param code 错误码
     * @param message 错误信息
     * @param data 错误详情数据
     * @param <T> 数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data, LocalDateTime.now(), null);
    }

    /**
     * 判断响应是否成功
     *
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return code != null && code.equals(200);
    }

    /**
     * 判断响应是否失败
     *
     * @return true-失败，false-成功
     */
    public boolean isError() {
        return !isSuccess();
    }
}
