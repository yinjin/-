package com.haocai.management.config;

import com.haocai.management.filter.JwtAuthenticationFilter;
import com.haocai.management.security.JwtAuthenticationEntryPoint;
import com.haocai.management.security.JwtAccessDeniedHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置类
 * 
 * 功能说明：
 * 1. 配置密码编码器（BCrypt）
 * 2. 配置认证管理器
 * 3. 配置JWT认证过滤器
 * 4. 配置无状态会话管理
 * 5. 配置请求授权规则
 * 6. 配置跨域访问（CORS）
 * 
 * 遵循规范：
 * - 安全规范：使用BCrypt加密密码、JWT无状态认证
 * - 配置规范：合理的请求授权规则
 * - 异常处理规范：自定义认证入口点和访问拒绝处理器
 * - 日志规范：记录详细的认证日志
 * 
 * @author 开发团队
 * @since 2026-01-05
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // 启用方法级别的安全注解（@PreAuthorize等）
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            @Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    /**
     * 配置密码编码器
     * 
     * 遵循：安全规范（使用BCrypt强加密算法）
     * 遵循：配置规范（提供密码加密服务）
     * 
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("初始化BCrypt密码编码器");
        // 遵循：安全规范-密码加密算法选择
        // BCrypt优势：
        // 1. 内置盐值，无需额外存储
        // 2. 可调整计算强度（默认10轮）
        // 3. 自动处理加盐和哈希
        // 4. 抗彩虹表攻击
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器
     * 
     * 遵循：安全规范（集中管理认证逻辑）
     * 
     * @param authConfig 认证配置
     * @return AuthenticationManager实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        log.info("初始化认证管理器");
        // 遵循：安全规范-认证管理器配置
        // 认证管理器负责：
        // 1. 协调多个认证提供者
        // 2. 处理认证流程
        // 3. 返回认证结果
        return authConfig.getAuthenticationManager();
    }

    /**
     * 配置安全过滤链
     * 
     * 遵循：安全规范（无状态会话管理）
     * 遵循：配置规范（请求授权规则）
     * 遵循：异常处理规范（自定义异常处理器）
     * 
     * @param http HttpSecurity配置对象
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("配置Spring Security过滤链");
        
        http
            // 1. 禁用CSRF保护
            // 遵循：安全规范-无状态认证不需要CSRF
            // JWT token本身就是防CSRF机制
            .csrf(csrf -> csrf.disable())
            
            // 2. 配置会话管理为无状态
            // 遵循：安全规范-JWT无状态认证
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 3. 配置异常处理
            // 遵循：异常处理规范-统一的错误响应
            .exceptionHandling(exception -> exception
                // 认证失败处理器（未认证）
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                // 访问拒绝处理器（无权限）
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )
            
            // 4. 配置请求授权规则
            // 遵循：配置规范-合理的权限控制策略
            .authorizeHttpRequests(authz -> authz
                // ===== 公开访问的接口（无需认证）=====
                
                // Swagger UI文档
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                
                // 健康检查接口
                .requestMatchers("/actuator/**", "/api/test/**").permitAll()
                
                // 调试接口（仅开发环境使用）
                .requestMatchers("/api/debug/**").permitAll()
                
                // 错误页面
                .requestMatchers("/error").permitAll()
                
                // 登录和注册接口
                // 遵循：配置规范-路径与Controller保持一致
                // 修正：使用复数形式/api/users，与SysUserController的@RequestMapping一致
                .requestMatchers(
                    "/api/users/register",
                    "/api/users/login",
                    "/api/users/check/**"
                ).permitAll()
                
                // 文件访问接口（图片等静态资源）
                .requestMatchers("/api/files/**").permitAll()
                
                // ===== 需要认证的接口 =====
                
                // 其他所有API接口都需要认证
                .requestMatchers("/api/**").authenticated()
                
                // 其他所有请求允许访问（静态资源等）
                .anyRequest().permitAll()
            )
            
            // 5. 配置CORS（跨域访问）
            // 遵循：配置规范-支持前后端分离架构
            // 遵循：安全规范-合理的跨域策略
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 6. 添加JWT认证过滤器
            // 遵循：安全规范-JWT token验证
            // 在UsernamePasswordAuthenticationFilter之前执行
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );
        
        log.info("Spring Security过滤链配置完成");
        return http.build();
    }

    /**
     * 配置CORS（跨域资源共享）
     * 
     * 遵循：配置规范-支持前后端分离架构
     * 遵循：安全规范-合理的跨域策略
     * 
     * CORS配置说明：
     * 1. 允许前端应用（http://localhost:5173）跨域访问
     * 2. 允许所有HTTP方法（GET, POST, PUT, DELETE等）
     * 3. 允许携带认证信息（credentials）
     * 4. 允许所有请求头
     * 5. 预检请求缓存时间为1小时
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("配置CORS跨域支持");
        
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源（前端应用地址）
        // 遵循：安全规范-限制允许的源，避免使用通配符
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",  // Vite开发服务器
            "http://localhost:5174",  // Vite开发服务器
            "http://localhost:5175",  // Vite开发服务器（当前使用）
            "http://localhost:3000",  // 其他可能的开发服务器
            "http://127.0.0.1:5173",
            "http://127.0.0.1:5174",
            "http://127.0.0.1:5175"
        ));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 允许携带认证信息（如JWT token）
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);
        
        // 暴露的响应头（允许前端访问的响应头）
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type"
        ));
        
        // 应用到所有路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("CORS配置完成，允许的源: {}", configuration.getAllowedOrigins());
        return source;
    }
}
