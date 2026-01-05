package com.haocai.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haocai.management.common.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT访问拒绝处理器
 * <p>
 * 职责：
 * 1. 处理已认证用户访问无权限资源的情况
 * 2. 返回统一的JSON格式错误响应
 * 3. 记录访问拒绝日志
 * <p>
 * 使用场景：
 * - 普通用户尝试访问管理员接口
 * - 用户尝试访问其他用户的私有资源
 * - 用户权限不足访问特定功能
 * <p>
 * 设计原则：
 * - 统一错误响应格式，遵循API响应规范
 * - 不暴露敏感信息（如具体权限配置）
 * - 记录安全相关日志，便于审计
 * <p>
 * 遵循规范：
 * - 代码规范-第3条（异常处理）
 * - 代码规范-第4条（日志记录）
 * - 安全规范-第6条（错误信息不泄露）
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // 遵循：配置规范-使用Spring管理的ObjectMapper，确保序列化配置一致
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 访问被拒绝时的处理方法
     * <p>
     * 执行流程：
     * 1. 设置响应状态码为403 Forbidden
     * 2. 设置响应内容类型为JSON
     * 3. 构建统一的错误响应
     * 4. 记录访问拒绝日志
     * 5. 将错误响应写入响应体
     *
     * @param request               HTTP请求
     * @param response              HTTP响应
     * @param accessDeniedException 访问拒绝异常
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 步骤1：设置响应状态码
        // 403 Forbidden：已认证但权限不足
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 步骤2：设置响应内容类型和编码
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 步骤3：构建统一的错误响应
        // 遵循：安全规范-第6条（错误信息不泄露）
        // 不暴露具体的权限配置信息，只返回通用错误信息
        ApiResponse<Object> apiResponse = ApiResponse.error(403, "权限不足，无法访问该资源");

        // 步骤4：记录访问拒绝日志
        // 遵循：代码规范-第4条（日志记录）
        // 记录请求路径、用户信息和异常信息，便于安全审计
        log.warn("访问被拒绝 - 请求路径: {}, 用户: {}, 异常信息: {}",
                request.getRequestURI(),
                request.getRemoteUser(),
                accessDeniedException.getMessage());

        // 步骤5：将错误响应写入响应体
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
