package com.material.system.aspect;

import com.material.system.annotation.RequirePermission;
import com.material.system.common.ResultCode;
import com.material.system.exception.BusinessException;
import com.material.system.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 权限切面
 * 处理@RequirePermission注解的权限验证
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PermissionAspect {
    
    private final JwtUtil jwtUtil;
    
    @Before("@annotation(com.material.system.annotation.RequirePermission)")
    public void checkPermission(JoinPoint joinPoint) {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        
        if (requirePermission == null) {
            return;
        }
        
        // 获取需要的权限
        String requiredPermission = requirePermission.value();
        boolean requireAll = requirePermission.requireAll();
        
        // 从SecurityContext获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("用户未认证，访问被拒绝: {}", request.getRequestURI());
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        // 获取用户的权限列表
        List<String> userPermissions = (List<String>) authentication.getCredentials();
        if (userPermissions == null || userPermissions.isEmpty()) {
            log.warn("用户无任何权限，访问被拒绝: {}", request.getRequestURI());
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        
        // 检查权限
        boolean hasPermission = userPermissions.contains(requiredPermission);
        
        if (!hasPermission) {
            log.warn("用户权限不足，需要权限: {}, 用户权限: {}", requiredPermission, userPermissions);
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        
        log.debug("权限验证通过，用户权限: {}, 需要权限: {}", userPermissions, requiredPermission);
    }
}
