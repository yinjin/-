package com.haocai.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haocai.management.common.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT认证入口点
 * <p>
 * 职责：
 * 1. 处理认证失败的情况（未提供token、token无效等）
 * 2. 返回统一的JSON格式错误响应
 * 3. 记录认证失败日志
 * <p>
 * 使用场景：
 * - 用户未提供JWT token访问受保护资源
 * - JWT token已过期
 * - JWT token签名验证失败
 * - JWT token格式错误
 * <p>
 * 设计原则：
 * - 统一错误响应格式，遵循API响应规范
 * - 不暴露敏感信息（如具体错误原因）
 * - 记录安全相关日志
 * <p>
 * 遵循规范：
 * - 代码规范-第3条（异常处理）
 * - 代码规范-第4条（日志记录）
 * - 安全规范-第6条（错误信息不泄露）
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 遵循：配置规范-使用Spring管理的ObjectMapper，确保序列化配置一致
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 认证失败时的处理方法
     * <p>
     * 执行流程：
     * 1. 设置响应状态码为401 Unauthorized
     * 2. 设置响应内容类型为JSON
     * 3. 构建统一的错误响应
     * 4. 记录认证失败日志
     * 5. 将错误响应写入响应体
     *
     * @param request       HTTP请求
     * @param response      HTTP响应
     * @param authException 认证异常
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 步骤1：设置响应状态码
        // 401 Unauthorized：未认证，需要提供有效的认证信息
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 步骤2：设置响应内容类型和编码
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 步骤3：构建统一的错误响应
        // 遵循：安全规范-第6条（错误信息不泄露）
        // 不暴露具体的认证失败原因，只返回通用错误信息
        ApiResponse<Object> apiResponse = ApiResponse.error(401, "认证失败，请重新登录");

        // 步骤4：记录认证失败日志
        // 遵循：代码规范-第4条（日志记录）
        // 记录请求路径和异常信息，便于安全审计
        log.warn("认证失败 - 请求路径: {}, 异常信息: {}",
                request.getRequestURI(),
                authException.getMessage());

        // 步骤5：将错误响应写入响应体
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
