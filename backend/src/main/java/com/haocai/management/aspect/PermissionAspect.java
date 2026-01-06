package com.haocai.management.aspect;

import com.haocai.management.annotation.RequirePermission;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限切面
 * 用于拦截带有@RequirePermission注解的方法，进行权限验证
 * 
 * 遵循开发规范：
 * - AOP切面规范-使用@Aspect和@Component注解
 * - 权限控制规范-使用切面进行权限验证
 * - 日志记录规范-使用@Slf4j记录权限检查日志
 * - 异常处理规范-使用BusinessException抛出业务异常
 * 
 * @author haocai
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * 权限检查前置通知
     * 在方法执行前检查用户是否拥有所需权限
     * 
     * @param joinPoint 连接点
     */
    @Before("@annotation(com.haocai.management.annotation.RequirePermission)")
    public void checkPermission(JoinPoint joinPoint) {
        // 获取当前请求
        HttpServletRequest request = getRequest();
        if (request == null) {
            log.warn("无法获取当前请求，跳过权限检查");
            return;
        }
        
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取@RequirePermission注解
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            // 如果方法上没有注解，检查类上是否有注解
            requirePermission = method.getDeclaringClass().getAnnotation(RequirePermission.class);
        }
        
        if (requirePermission == null) {
            log.debug("方法或类上没有@RequirePermission注解，跳过权限检查");
            return;
        }
        
        // 获取所需权限
        String[] requiredPermissions = requirePermission.value();
        RequirePermission.Logical logical = requirePermission.logical();
        
        // 获取当前用户权限
        Set<String> userPermissions = getUserPermissions();
        
        // 记录权限检查日志
        log.debug("权限检查 - 方法: {}, 所需权限: {}, 逻辑: {}, 用户权限: {}", 
                method.getName(), Arrays.toString(requiredPermissions), logical, userPermissions);
        
        // 验证权限
        boolean hasPermission = validatePermissions(userPermissions, requiredPermissions, logical);
        
        if (!hasPermission) {
            String errorMsg = String.format("权限不足，需要权限: %s", Arrays.toString(requiredPermissions));
            log.warn("权限检查失败 - 方法: {}, 用户权限: {}, 所需权限: {}", 
                    method.getName(), userPermissions, Arrays.toString(requiredPermissions));
            throw new BusinessException(403, errorMsg);
        }
        
        log.debug("权限检查通过 - 方法: {}", method.getName());
    }
    
    /**
     * 获取当前请求
     * 
     * @return HttpServletRequest
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
    
    /**
     * 获取当前用户权限
     * 从SecurityContext中获取当前用户的权限列表
     * 
     * @return 用户权限集合
     */
    private Set<String> getUserPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("用户未认证，无法获取权限");
            return Set.of();
        }
        
        // 从认证信息中获取权限
        return authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
    
    /**
     * 验证用户是否拥有所需权限
     * 
     * @param userPermissions 用户权限集合
     * @param requiredPermissions 所需权限数组
     * @param logical 逻辑关系（AND/OR）
     * @return 是否拥有权限
     */
    private boolean validatePermissions(Set<String> userPermissions, String[] requiredPermissions, 
                                       RequirePermission.Logical logical) {
        if (requiredPermissions == null || requiredPermissions.length == 0) {
            return true;
        }
        
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }
        
        List<String> requiredList = Arrays.asList(requiredPermissions);
        
        if (logical == RequirePermission.Logical.AND) {
            // AND逻辑：需要拥有所有权限
            return userPermissions.containsAll(requiredList);
        } else {
            // OR逻辑：只需要拥有其中一个权限
            return requiredList.stream().anyMatch(userPermissions::contains);
        }
    }
}
