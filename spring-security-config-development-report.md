# Spring Security配置开发报告

## 任务完成状态
✅ **已完成** - Spring Security配置开发（2.2）

---

## 开发过程记录

### 步骤1：规划与设计

#### 1.1 基于`development-standards.md`的关键约束条款

根据开发规范文档，Spring Security配置需要遵循以下关键约束：

**约束1：异常处理规范（四、4.2节）**
- 必须实现全局异常处理器
- 统一异常处理和错误响应格式
- 记录详细的错误日志
- 返回友好的错误信息

**约束2：配置规范（五、5.1节）**
- 从配置文件读取JWT参数
- 开发日志记录（便于问题排查）
- 合理的默认值配置

**约束3：日志规范**
- 记录详细的操作日志
- 记录认证成功/失败事件
- 记录token验证过程

**约束4：安全规范**
- 密码使用BCrypt加密
- JWT token使用安全的签名算法（HS256）
- 密钥从配置文件读取（密钥分离）
- 无状态会话管理

#### 1.2 核心类和方法设计

**1.2.1 SecurityConfig配置类**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // 核心方法1：配置密码编码器
    // 设计说明：使用BCryptPasswordEncoder加密密码，遵循安全规范
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // 核心方法2：配置认证管理器
    // 设计说明：用于处理认证逻辑，集成自定义认证提供者
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    // 核心方法3：配置安全过滤链
    // 设计说明：配置JWT过滤器、CORS、请求授权规则
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 配置内容...
    }
}
```

**1.2.2 JwtAuthenticationFilter过滤器类**

```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    // 核心方法：doFilterInternal
    // 设计说明：拦截每个请求，验证JWT token，设置认证上下文
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 1. 提取token
        // 2. 验证token
        // 3. 设置认证上下文
        // 4. 继续过滤链
    }
}
```

**设计决策说明：**

1. **使用OncePerRequestFilter基类**
   - 满足约束：确保过滤器只执行一次，避免重复处理
   - 优势：Spring Security推荐的基础过滤器类

2. **配置无状态会话管理**
   - 满足约束：符合JWT无状态认证的设计理念
   - 实现方式：`.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)`

3. **CORS配置**
   - 满足约束：允许前后端分离架构的跨域请求
   - 配置：允许所有来源、方法和头部（开发环境）

4. **请求授权规则**
   - 公开接口：登录、注册、Swagger文档
   - 认证接口：用户管理接口、其他业务接口
   - 满足约束：合理的权限控制策略

---

### 步骤2：实现与编码

#### 2.1 SecurityConfig.java

**文件路径：** `backend/src/main/java/com/haocai/management/config/SecurityConfig.java`

```java
package com.haocai.management.config;

import com.haocai.management.filter.JwtAuthenticationFilter;
import com.haocai.management.security.JwtAuthenticationEntryPoint;
import com.haocai.management.security.JwtAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

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
                .requestMatchers("/actuator/**").permitAll()
                
                // 错误页面
                .requestMatchers("/error").permitAll()
                
                // 登录和注册接口
                .requestMatchers(
                    "/api/user/register",
                    "/api/user/login"
                ).permitAll()
                
                // ===== 需要认证的接口 =====
                
                // 其他所有API接口都需要认证
                .requestMatchers("/api/**").authenticated()
                
                // 其他所有请求允许访问（静态资源等）
                .anyRequest().permitAll()
            )
            
            // 5. 配置CORS（跨域访问）
            // 遵循：配置规范-支持前后端分离架构
            .cors(cors -> cors.configure(http))
            
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
}
```

#### 2.2 JwtAuthenticationFilter.java

**文件路径：** `backend/src/main/java/com/haocai/management/filter/JwtAuthenticationFilter.java`

```java
package com.haocai.management.filter;

import com.haocai.management.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 * 
 * 功能说明：
 * 1. 拦截请求，提取JWT token
 * 2. 验证token有效性
 * 3. 解析token获取用户信息
 * 4. 设置Spring Security认证上下文
 * 
 * 遵循规范：
 * - 安全规范：JWT token验证、无状态认证
 * - 日志规范：记录详细的认证日志
 * - 异常处理规范：完善的异常处理
 * 
 * @author 开发团队
 * @since 2026-01-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    /**
     * Token在请求头中的键名
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Token前缀
     */
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 执行过滤逻辑
     * 
     * 遵循：安全规范-JWT token验证流程
     * 遵循：日志规范-记录详细的认证过程
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param filterChain FilterChain
     * @throws ServletException ServletException
     * @throws IOException IOException
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 1. 提取token
            String token = extractTokenFromRequest(request);
            
            if (token != null && !token.isEmpty()) {
                log.debug("请求包含JWT token: {}", maskToken(token));
                
                // 2. 验证token有效性
                // 遵循：安全规范-验证token有效性
                if (jwtUtils.validateToken(token)) {
                    // 3. 解析token获取用户信息
                    // 遵循：安全规范-从token提取用户标识
                    Long userId = jwtUtils.getUserIdFromToken(token);
                    String username = jwtUtils.getUsernameFromToken(token);
                    
                    log.debug("JWT token验证成功: userId={}, username={}", userId, username);
                    
                    // 4. 检查当前认证上下文是否已设置
                    // 遵循：安全规范-避免重复认证
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 5. 构建用户认证信息
                        // 注意：这里使用简化的UserDetails，实际项目可能需要从数据库加载完整用户信息
                        // 遵循：安全规范-构建认证对象
                        UserDetails userDetails = User.builder()
                                .username(username)
                                .password("")  // token认证不需要密码
                                .authorities(new ArrayList<>())  // 暂时为空，后续可从token或数据库加载权限
                                .build();
                        
                        // 6. 创建认证令牌
                        // 遵循：安全规范-设置认证令牌
                        UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails,  // 主体
                                null,         // 凭证
                                userDetails.getAuthorities()  // 权限
                            );
                        
                        // 7. 设置认证详情（包含请求信息）
                        authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        
                        // 8. 设置认证上下文
                        // 遵循：安全规范-设置SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        log.debug("用户认证成功: userId={}, username={}", userId, username);
                    }
                } else {
                    log.warn("JWT token验证失败: {}", maskToken(token));
                }
            } else {
                log.debug("请求未包含JWT token，跳过认证");
            }
            
        } catch (Exception e) {
            // 遵循：异常处理规范-完善的异常处理
            // 遵循：日志规范-记录详细的错误日志
            log.error("JWT认证过滤器异常: {}", e.getMessage(), e);
            // 不抛出异常，让请求继续，后续由SecurityConfig配置决定是否放行
        }
        
        // 继续过滤链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取token
     * 
     * @param request HttpServletRequest
     * @return JWT token字符串（不包含"Bearer "前缀），如果不存在返回null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        try {
            // 从Authorization头获取token
            String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
            
            if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
                // 移除"Bearer "前缀，返回纯token
                return bearerToken.substring(TOKEN_PREFIX.length());
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("提取token失败", e);
            return null;
        }
    }

    /**
     * 遮盖token中间部分，用于日志记录
     * 只显示前6个和后4个字符，中间用...代替
     * 
     * @param token JWT token
     * @return 遮盖后的token字符串
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
```

#### 2.3 JwtAuthenticationEntryPoint.java

**文件路径：** `backend/src/main/java/com/haocai/management/security/JwtAuthenticationEntryPoint.java`

```java
package com.haocai.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haocai.management.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT认证入口点
 * 
 * 功能说明：
 * 当用户未认证（未携带token或token无效）访问需要认证的接口时，
 * 会触发此入口点，返回统一的401错误响应
 * 
 * 遵循规范：
 * - 异常处理规范：统一的错误响应格式
 * - 日志规范：记录认证失败日志
 * 
 * @author 开发团队
 * @since 2026-01-05
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 认证失败处理
     * 
     * 遵循：异常处理规范-统一的JSON错误响应
     * 遵循：日志规范-记录认证失败日志
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param authException AuthenticationException
     * @throws IOException IOException
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        
        // 遵循：日志规范-记录认证失败日志
        log.warn("用户未认证访问: URI={}, IP={}, 错误信息={}", 
                request.getRequestURI(),
                request.getRemoteAddr(),
                authException.getMessage());
        
        // 遵循：异常处理规范-统一的JSON错误响应
        // 设置响应状态码为401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // 设置响应内容类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        // 构建错误响应
        ApiResponse<Void> apiResponse = ApiResponse.error(
            401, 
            "未认证或认证已过期，请重新登录"
        );
        
        // 写入响应
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
```

#### 2.4 JwtAccessDeniedHandler.java

**文件路径：** `backend/src/main/java/com/haocai/management/security/JwtAccessDeniedHandler.java`

```java
package com.haocai.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haocai.management.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT访问拒绝处理器
 * 
 * 功能说明：
 * 当用户已认证但没有足够权限访问资源时，
 * 会触发此处理器，返回统一的403错误响应
 * 
 * 遵循规范：
 * - 异常处理规范：统一的错误响应格式
 * - 日志规范：记录权限拒绝日志
 * 
 * @author 开发团队
 * @since 2026-01-05
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 访问拒绝处理
     * 
     * 遵循：异常处理规范-统一的JSON错误响应
     * 遵循：日志规范-记录权限拒绝日志
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param accessDeniedException AccessDeniedException
     * @throws IOException IOException
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        
        // 遵循：日志规范-记录权限拒绝日志
        log.warn("用户访问被拒绝: URI={}, IP={}, 错误信息={}", 
                request.getRequestURI(),
                request.getRemoteAddr(),
                accessDeniedException.getMessage());
        
        // 遵循：异常处理规范-统一的JSON错误响应
        // 设置响应状态码为403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        // 设置响应内容类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        // 构建错误响应
        ApiResponse<Void> apiResponse = ApiResponse.error(
            403, 
            "权限不足，无法访问该资源"
        );
        
        // 写入响应
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
```

#### 2.5 规范映射和安全决策说明

**关键安全决策说明：**

1. **选择BCryptPasswordEncoder**
   - 遵循：安全规范-密码加密算法选择
   - 原因：BCrypt是当前广泛认可的密码哈希算法，内置盐值，抗彩虹表攻击
   - 配置：默认强度为10，可在生产环境根据性能需求调整

2. **无状态会话管理（STATELESS）**
   - 遵循：安全规范-JWT无状态认证设计
   - 原因：JWT本身就是无状态的，服务端不存储会话，易于横向扩展
   - 实现：`SessionCreationPolicy.STATELESS`

3. **JWT过滤器位置**
   - 遵循：安全规范-过滤器链配置
   - 位置：在`UsernamePasswordAuthenticationFilter`之前执行
   - 原因：确保JWT认证优先于传统的表单认证

4. **CORS配置**
   - 遵循：配置规范-支持前后端分离
   - 策略：允许所有来源（开发环境）
   - 生产环境建议：限制具体域名

5. **异常处理策略**
   - 遵循：异常处理规范-统一的JSON错误响应
   - 实现：自定义`AuthenticationEntryPoint`和`AccessDeniedHandler`
   - 返回：标准的`ApiResponse`格式

---

### 步骤3：验证与测试

#### 3.1 测试用例

**文件路径：** `backend/src/test/java/com/haocai/management/config/SecurityConfigTest.java`

```java
package com.haocai.management.config;

import com.haocai.management.filter.JwtAuthenticationFilter;
import com.haocai.management.security.JwtAccessDeniedHandler;
import com.haocai.management.security.JwtAuthenticationEntryPoint;
import com.haocai.management.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring Security配置测试类
 * 
 * 功能说明：
 * 1. 测试密码编码器
 * 2. 测试JWT认证过滤器
 * 3. 测试认证管理器
 * 
 * @author 开发团队
 * @since 2026-01-05
 */
@SpringBootTest
@ActiveProfiles("dev")
@DisplayName("Spring Security配置测试")
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * 测试1：密码编码器基本功能
     * 验证BCryptPasswordEncoder能正确加密和验证密码
     */
    @Test
    @DisplayName("测试1：密码编码器基本功能")
    void testPasswordEncoder() {
        // 准备测试数据
        String rawPassword = "test123";
        
        // 执行：加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // 验证1：加密后的密码不为空
        assertNotNull(encodedPassword, "加密后的密码不能为空");
        
        // 验证2：加密后的密码与原密码不同
        assertNotEquals(rawPassword, encodedPassword, "加密后的密码应该与原密码不同");
        
        // 验证3：加密后的密码应该包含$符号（BCrypt特征）
        assertTrue(encodedPassword.contains("$"), "BCrypt加密后的密码应该包含$符号");
        
        // 验证4：验证密码正确
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword), "密码验证应该成功");
        
        // 验证5：验证错误密码失败
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword), "错误密码验证应该失败");
        
        System.out.println("✅ 测试1通过：密码编码器基本功能正常");
    }

    /**
     * 测试2：密码编码器同一密码多次加密结果不同
     * 验证BCryptPasswordEncoder每次加密都使用不同的盐值
     */
    @Test
    @DisplayName("测试2：密码编码器同一密码多次加密结果不同")
    void testPasswordEncoderDifferentSalts() {
        // 准备测试数据
        String rawPassword = "test123";
        
        // 执行：同一密码加密两次
        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);
        
        // 验证：两次加密结果不同（因为盐值不同）
        assertNotEquals(encoded1, encoded2, "同一密码多次加密结果应该不同");
        
        // 验证：但都能通过验证
        assertTrue(passwordEncoder.matches(rawPassword, encoded1), "第一次加密的密码应该能验证");
        assertTrue(passwordEncoder.matches(rawPassword, encoded2), "第二次加密的密码应该能验证");
        
        System.out.println("✅ 测试2通过：密码编码器使用不同盐值");
    }

    /**
     * 测试3：JWT认证过滤器依赖注入
     * 验证JWT认证过滤器能正确注入Spring容器
     */
    @Test
    @DisplayName("测试3：JWT认证过滤器依赖注入")
    void testJwtAuthenticationFilterInjection() {
        // 验证：过滤器不是null
        assertNotNull(jwtAuthenticationFilter, "JWT认证过滤器应该成功注入");
        
        System.out.println("✅ 测试3通过：JWT认证过滤器依赖注入成功");
    }

    /**
     * 测试4：认证入口点依赖注入
     * 验证认证入口点能正确注入Spring容器
     */
    @Test
    @DisplayName("测试4：认证入口点依赖注入")
    void testJwtAuthenticationEntryPointInjection() {
        // 验证：认证入口点不是null
        assertNotNull(jwtAuthenticationEntryPoint, "认证入口点应该成功注入");
        
        System.out.println("✅ 测试4通过：认证入口点依赖注入成功");
    }

    /**
     * 测试5：访问拒绝处理器依赖注入
     * 验证访问拒绝处理器能正确注入Spring容器
     */
    @Test
    @DisplayName("测试5：访问拒绝处理器依赖注入")
    void testJwtAccessDeniedHandlerInjection() {
        // 验证：访问拒绝处理器不是null
        assertNotNull(jwtAccessDeniedHandler, "访问拒绝处理器应该成功注入");
        
        System.out.println("✅ 测试5通过：访问拒绝处理器依赖注入成功");
    }

    /**
     * 测试6：JWT工具类与Security集成
     * 验证JWT工具类能正确与Security配置集成
     */
    @Test
    @DisplayName("测试6：JWT工具类与Security集成")
    void testJwtUtilsIntegration() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        
        // 执行：生成token
        String token = jwtUtils.generateToken(claims, null);
        
        // 验证：token不为空
        assertNotNull(token, "生成的token不能为空");
        
        // 验证：token能正确解析
        Long userId = jwtUtils.getUserIdFromToken(token);
        String username = jwtUtils.getUsernameFromToken(token);
        
        assertEquals(1L, userId, "用户ID应该正确");
        assertEquals("testuser", username, "用户名应该正确");
        
        System.out.println("✅ 测试6通过：JWT工具类与Security集成正常");
    }

    /**
     * 测试7：SecurityConfig Bean创建
     * 验证SecurityConfig中的Bean能正确创建
     */
    @Test
    @DisplayName("测试7：SecurityConfig Bean创建")
    void testSecurityConfigBeans() {
        // 验证：密码编码器已创建
        assertNotNull(passwordEncoder, "密码编码器应该已创建");
        
        // 验证：JWT工具类已创建
        assertNotNull(jwtUtils, "JWT工具类应该已创建");
        
        // 验证：过滤器已创建
        assertNotNull(jwtAuthenticationFilter, "JWT认证过滤器应该已创建");
        
        // 验证：异常处理器已创建
        assertNotNull(jwtAuthenticationEntryPoint, "认证入口点应该已创建");
        assertNotNull(jwtAccessDeniedHandler, "访问拒绝处理器应该已创建");
        
        System.out.println("✅ 测试7通过：SecurityConfig所有Bean创建成功");
    }
}
```

#### 3.2 边界测试和异常测试场景

**边界测试场景：**

1. **Token为null或空字符串**
   - 场景：请求头中不包含Authorization字段，或Authorization字段为空
   - 预期：认证失败，返回401错误

2. **Token格式错误**
   - 场景：Authorization头不包含"Bearer "前缀，或token格式不正确
   - 预期：认证失败，返回401错误

3. **Token已过期**
   - 场景：使用已过期的token访问接口
   - 预期：认证失败，返回401错误，提示"token已过期"

4. **Token被篡改**
   - 场景：修改token的签名部分
   - 预期：签名验证失败，返回401错误

5. **Token中没有必需的声明**
   - 场景：token中不包含userId或username
   - 预期：解析失败，返回401错误

**异常测试场景：**

1. **认证失败异常**
   - 场景：未携带token访问需要认证的接口
   - 预期：触发JwtAuthenticationEntryPoint，返回401响应

2. **权限不足异常**
   - 场景：用户已认证但没有足够的权限访问资源
   - 预期：触发JwtAccessDeniedHandler，返回403响应

3. **JWT解析异常**
   - 场景：token格式错误或签名无效
   - 预期：捕获ExpiredJwtException、MalformedJwtException等异常

4. **用户不存在**
   - 场景：token中包含的用户ID在数据库中不存在
   - 预期：认证失败，返回401错误

5. **密码验证失败**
   - 场景：登录时密码错误
   - 预期：认证失败，返回401错误

---

### 步骤4：文档与知识固化

#### 4.1 对`development-standards.md`的更新建议

**建议1：新增"安全配置规范"章节**

建议在文档中新增专门的"安全配置规范"章节，包含以下内容：

```markdown
## 十、安全配置规范

### 10.1 密码加密规范

**必须使用BCryptPasswordEncoder**：
- 配置：`@Bean public PasswordEncoder passwordEncoder()`
- 优势：内置盐值、可调整强度、抗彩虹表攻击
- 示例代码：
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**⚠️ 常见错误**：
- ❌ 使用MD5或SHA1等弱加密算法
- ❌ 密码明文存储
- ❌ 未加盐的哈希

### 10.2 JWT认证配置规范

**必须配置无状态会话管理**：
```java
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

**必须配置JWT认证过滤器**：
```java
.addFilterBefore(
    jwtAuthenticationFilter,
    UsernamePasswordAuthenticationFilter.class
)
```

**⚠️ 常见错误**：
- ❌ 未配置无状态会话管理
- ❌ JWT过滤器位置配置错误
- ❌ 未禁用CSRF保护

### 10.3 异常处理规范

**必须自定义认证入口点**：
- 实现`AuthenticationEntryPoint`接口
- 返回统一的401 JSON响应

**必须自定义访问拒绝处理器**：
- 实现`AccessDeniedHandler`接口
- 返回统一的403 JSON响应

**⚠️ 常见错误**：
- ❌ 未自定义异常处理器，返回HTML错误页面
- ❌ 错误响应格式不统一
```

**建议2：完善"配置规范"章节**

在"配置规范"章节中添加Spring Security相关配置：

```markdown
### 5.3 Spring Security配置

**必须配置的Bean**：
1. PasswordEncoder - 密码编码器
2. AuthenticationManager - 认证管理器
3. SecurityFilterChain - 安全过滤链
4. JwtAuthenticationFilter - JWT认证过滤器
5. JwtAuthenticationEntryPoint - 认证入口点
6. JwtAccessDeniedHandler - 访问拒绝处理器

**请求授权规则配置**：
```java
.authorizeHttpRequests(authz -> authz
    // 公开接口
    .requestMatchers("/api/user/register", "/api/user/login").permitAll()
    // 认证接口
    .requestMatchers("/api/**").authenticated()
    // 其他
    .anyRequest().permitAll()
)
```

**⚠️ 常见错误**：
- ❌ 未配置认证管理器
- ❌ 请求授权规则配置过于宽松
- ❌ 未配置异常处理器
```

**建议3：新增"测试规范"章节**

添加安全配置相关的测试规范：

```markdown
### 6.4 安全配置测试

**必须测试的场景**：
1. 密码编码器功能测试
2. JWT token生成和验证测试
3. 认证失败场景测试
4. 权限不足场景测试
5. Bean依赖注入测试

**测试示例**：
```java
@Test
void testPasswordEncoder() {
    String rawPassword = "test123";
    String encodedPassword = passwordEncoder.encode(rawPassword);
    
    assertNotNull(encodedPassword);
    assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
}
```
```

#### 4.2 给新开发者的快速指南

**Spring Security配置快速指南**

1. **核心组件理解**
   - SecurityConfig：主配置类，配置密码编码器、认证管理器、过滤链
   - JwtAuthenticationFilter：JWT认证过滤器，拦截请求验证token
   - JwtAuthenticationEntryPoint：认证失败处理器（401）
   - JwtAccessDeniedHandler：权限不足处理器（403）

2. **配置步骤**
   - 步骤1：配置密码编码器（BCrypt）
   - 步骤2：配置认证管理器
   - 步骤3：配置安全过滤链（会话管理、请求授权、CORS、JWT过滤器）
   - 步骤4：自定义异常处理器

3. **使用方式**
   - 登录接口：返回JWT token
   - 访问认证接口：在请求头中添加`Authorization: Bearer <token>`
   - Token过期：返回401错误，前端需重新登录

4. **注意事项**
   - ✅ 使用BCrypt加密密码
   - ✅ 配置无状态会话管理
   - ✅ JWT过滤器在UsernamePasswordAuthenticationFilter之前
   - ✅ 自定义异常处理器返回JSON响应
   - ❌ 不要禁用CsrfFilter（除非使用JWT）
   - ❌ 不要将所有接口都设置为permitAll

5. **调试技巧**
   - 开启Spring Security调试日志：`logging.level.org.springframework.security=debug`
   - 查看SecurityContextHolder中的认证信息
   - 使用Postman测试不同场景（无token、过期token、错误token等）

---

## 生成的完整代码清单

### 1. SecurityConfig.java
**路径：** `backend/src/main/java/com/haocai/management/config/SecurityConfig.java`
**说明：** Spring Security主配置类

### 2. JwtAuthenticationFilter.java
**路径：** `backend/src/main/java/com/haocai/management/filter/JwtAuthenticationFilter.java`
**说明：** JWT认证过滤器

### 3. JwtAuthenticationEntryPoint.java
**路径：** `backend/src/main/java/com/haocai/management/security
