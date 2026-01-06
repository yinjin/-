package com.haocai.management.config;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据访问层异常处理器
 * 专门处理MyBatis-Plus和数据库相关的异常
 * 
 * 功能说明：
 * 1. 处理MyBatis-Plus框架异常
 * 2. 处理数据库连接异常
 * 3. 处理SQL执行异常
 * 4. 处理数据完整性异常
 * 5. 处理唯一键冲突异常
 * 
 * @author haocai
 * @since 2026-01-06
 */
@Slf4j
@RestControllerAdvice
public class DataAccessExceptionHandler {

    /**
     * 处理MyBatis-Plus异常
     * 包括：SQL语法错误、参数绑定错误等
     */
    @ExceptionHandler(MybatisPlusException.class)
    public ResponseEntity<Map<String, Object>> handleMybatisPlusException(MybatisPlusException e) {
        log.error("MyBatis-Plus异常: {}", e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "数据库操作失败: " + e.getMessage());
        result.put("data", null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理数据访问异常
     * 包括：数据库连接失败、事务错误等
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException e) {
        log.error("数据访问异常: {}", e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "数据库访问失败，请稍后重试");
        result.put("data", null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理数据完整性违反异常
     * 包括：外键约束违反、非空约束违反等
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据完整性违反异常: {}", e.getMessage(), e);

        String message = "数据完整性错误";
        
        // 解析具体的错误信息
        String rootMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
        
        if (rootMessage != null) {
            if (rootMessage.contains("foreign key constraint")) {
                message = "无法删除：该数据被其他数据引用";
            } else if (rootMessage.contains("cannot be null")) {
                message = "必填字段不能为空";
            } else if (rootMessage.contains("Duplicate entry")) {
                message = "数据已存在，请使用其他值";
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("message", message);
        result.put("data", null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理唯一键重复异常
     * 包括：角色编码重复、权限编码重复、用户名重复等
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("唯一键重复异常: {}", e.getMessage(), e);

        String message = "数据已存在，请使用其他值";
        
        // 解析具体的错误信息
        String rootMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
        
        if (rootMessage != null) {
            if (rootMessage.contains("role_code")) {
                message = "角色编码已存在";
            } else if (rootMessage.contains("permission_code")) {
                message = "权限编码已存在";
            } else if (rootMessage.contains("username")) {
                message = "用户名已存在";
            } else if (rootMessage.contains("email")) {
                message = "邮箱已存在";
            } else if (rootMessage.contains("phone")) {
                message = "手机号已存在";
            } else if (rootMessage.contains("role_id") && rootMessage.contains("permission_id")) {
                message = "角色权限关联已存在";
            } else if (rootMessage.contains("user_id") && rootMessage.contains("role_id")) {
                message = "用户角色关联已存在";
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 409);
        result.put("message", message);
        result.put("data", null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
    }

    /**
     * 处理SQL异常
     * 包括：SQL语法错误、数据库连接错误等
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, Object>> handleSQLException(SQLException e) {
        log.error("SQL异常: {}", e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "数据库操作失败，请联系管理员");
        result.put("data", null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理数据库连接超时异常
     */
    @ExceptionHandler(org.springframework.dao.QueryTimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleQueryTimeoutException(org.springframework.dao.QueryTimeoutException e) {
        log.error("查询超时异常: {}", e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 504);
        result.put("message", "查询超时，请稍后重试");
        result.put("data", null);

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(result);
    }

    /**
     * 处理乐观锁异常
     * 当更新记录时，版本号不匹配
     */
    @ExceptionHandler(org.springframework.dao.OptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLockingFailureException(
            org.springframework.dao.OptimisticLockingFailureException e) {
        log.error("乐观锁异常: {}", e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 409);
        result.put("message", "数据已被其他用户修改，请刷新后重试");
        result.put("data", null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
    }

    /**
     * 处理悲观锁异常
     * 当获取锁失败时
     */
    @ExceptionHandler(org.springframework.dao.PessimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handlePessimisticLockingFailureException(
            org.springframework.dao.PessimisticLockingFailureException e) {
        log.error("悲观锁异常: {}", e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 409);
        result.put("message", "数据正在被其他用户操作，请稍后重试");
        result.put("data", null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
    }

    /**
     * 处理批量操作异常
     * 当批量插入或更新失败时
     */
    @ExceptionHandler(org.springframework.dao.InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDataAccessResourceUsageException(
            org.springframework.dao.InvalidDataAccessResourceUsageException e) {
        log.error("数据访问资源使用异常: {}", e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "数据库操作失败，请检查数据格式");
        result.put("data", null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
