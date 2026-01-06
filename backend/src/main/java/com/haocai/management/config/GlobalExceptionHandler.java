package com.haocai.management.config;

import com.haocai.management.common.ApiResponse;
import com.haocai.management.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局异常处理器
 * 统一处理应用中的各种异常，提供友好的错误响应格式
 * 
 * 遵循开发规范：
 * - 异常处理规范-使用@RestControllerAdvice统一处理异常
 * - 响应格式规范-使用ApiResponse统一响应格式
 * - 日志记录规范-记录异常日志
 * 
 * @author haocai
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 自定义业务逻辑异常
     * 
     * @param e 业务异常
     * @return 统一响应格式
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getCode(), e.getMessage()));
    }

    /**
     * 处理访问拒绝异常
     * 权限不足时抛出
     * 
     * @param e 访问拒绝异常
     * @return 统一响应格式
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("访问拒绝异常: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, "权限不足，无法访问该资源"));
    }

    /**
     * 处理认证失败异常
     * 用户名或密码错误
     * 
     * @param e 认证失败异常
     * @return 统一响应格式
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("认证失败异常: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "用户名或密码错误"));
    }

    /**
     * 处理数据完整性违反异常
     * 如：数据库连接异常、SQL语法错误等
     * 
     * @param e 数据完整性违反异常
     * @return 统一响应格式
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据完整性违反异常", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "数据完整性错误，请检查输入数据"));
    }

    /**
     * 处理唯一键重复异常
     * 如：用户名、邮箱、手机号重复
     * 
     * @param e 唯一键重复异常
     * @return 统一响应格式
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("唯一键重复异常", e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(409, "数据已存在，请使用其他值"));
    }

    /**
     * 处理参数验证异常
     * Controller层@Valid验证失败
     * 
     * @param e 参数验证异常
     * @return 统一响应格式
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.error("参数验证异常", e);
        
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "参数验证失败", errors));
    }

    /**
     * 处理绑定异常
     * 表单数据绑定失败
     * 
     * @param e 绑定异常
     * @return 统一响应格式
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBindException(BindException e) {
        log.error("数据绑定异常", e);
        
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "数据绑定失败", errors));
    }

    /**
     * 处理约束违反异常
     * JPA验证约束失败
     * 
     * @param e 约束违反异常
     * @return 统一响应格式
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException e) {
        log.error("约束违反异常", e);
        
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "数据验证失败", errors));
    }

    /**
     * 处理非法参数异常
     * 
     * @param e 非法参数异常
     * @return 统一响应格式
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, e.getMessage()));
    }

    /**
     * 处理运行时异常
     * 兜底异常处理器
     * 
     * @param e 运行时异常
     * @return 统一响应格式
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "系统内部错误"));
    }

    /**
     * 处理通用异常
     * 最终兜底处理器
     * 
     * @param e 通用异常
     * @return 统一响应格式
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "系统异常，请联系管理员"));
    }
}
