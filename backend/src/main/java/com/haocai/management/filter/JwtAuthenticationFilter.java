package com.haocai.management.filter;

import com.haocai.management.exception.BusinessException;
import com.haocai.management.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * <p>
 * 职责：
 * 1. 从请求头中提取JWT token
 * 2. 验证token有效性和完整性
 * 3. 将认证信息设置到SecurityContext中
 * <p>
 * 设计原则：
 * - 继承OncePerRequestFilter确保每个请求只执行一次
 * - 无状态设计，不依赖Session
 * - 统一异常处理，遵循全局异常规范
 * <p>
 * 遵循规范：
 * - 安全规范-第5条（无状态认证）
 * - 代码规范-第3条（异常处理）
 * - 代码规范-第4条（日志记录）
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * JWT token在请求头中的字段名
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * JWT token的前缀
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 过滤器核心方法
     * <p>
     * 执行流程：
     * 1. 从请求头提取token
     * 2. 验证token有效性
     * 3. 构建认证对象并设置到安全上下文
     *
     * @param request     HTTP请求
     * @param response    HTTP响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 步骤1：从请求头提取JWT token
            String jwt = extractJwtFromRequest(request);

            // 步骤2：验证token存在且有效
            if (StringUtils.hasText(jwt) && jwtUtils.validateToken(jwt)) {
                // 步骤2.1：从token中解析用户名
                // 使用JwtUtils.getUsernameFromToken方法，从自定义claim中获取username
                String username = jwtUtils.getUsernameFromToken(jwt);

                // 步骤2.2：从数据库加载用户详情
                // 遵循：代码规范-第2条（参数校验）
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 步骤2.3：构建认证对象
                // 使用userDetails中的权限列表，确保权限验证正常工作
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // 步骤2.4：设置认证详情（包含IP地址等）
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 步骤2.5：将认证信息设置到安全上下文
                // 遵循：安全规范-第5条（无状态认证）
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // 步骤3：继续执行过滤器链
            // 注意：即使没有token或token无效，也继续执行过滤器链
            // 这样可以让未认证请求继续处理，由授权规则决定是否允许访问
            filterChain.doFilter(request, response);

        } catch (BusinessException e) {
            // 业务异常：token验证失败等
            // 不抛出异常，让请求继续，由后续的授权规则处理
            // 遵循：代码规范-第3条（异常处理）
            logger.warn("JWT认证失败：" + e.getMessage());
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 其他异常：统一记录日志后继续
            // 保守处理策略：认证失败不影响请求继续，由后续授权规则决定
            // 遵循：代码规范-第4条（日志记录）
            logger.error("JWT认证过程发生异常", e);
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 从请求头中提取JWT token
     * <p>
     * 预期格式：Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     *
     * @param request HTTP请求
     * @return JWT token字符串（不含"Bearer "前缀），如果不存在则返回null
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        // 遵循：代码规范-第2条（参数校验）
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // 验证token存在且以"Bearer "开头
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // 去除"Bearer "前缀，返回纯token
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
