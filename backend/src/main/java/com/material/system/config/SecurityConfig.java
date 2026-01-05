package com.material.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import com.material.system.config.JwtAuthenticationFilter;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * 配置Web安全，排除不需要认证的路径
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/api/user/login");
    }

    /**
     * 配置安全过滤器链，定义HTTP安全规则
     * 
     * @param http HttpSecurity对象，用于配置HTTP安全设置
     * @return SecurityFilterChain 安全过滤器链实例
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        System.out.println("SecurityConfig: Configuring security filter chain");
        // 配置HTTP安全规则，包括CSRF禁用和请求授权规则
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    System.out.println("SecurityConfig: Configuring authorization rules");
                    auth
                        .requestMatchers("/api/user/login").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/health", "/welcome").permitAll()
                        .anyRequest().authenticated();
                    System.out.println("SecurityConfig: Authorization rules configured");
                })
                .addFilterBefore(jwtAuthenticationFilter, AnonymousAuthenticationFilter.class);

        System.out.println("SecurityConfig: Security filter chain configured successfully");
        return http.build();
    }

    /**
     * 配置密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置用户详情服务（为了避免Spring Boot默认安全配置）
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails dummyUser = User.builder()
                .username("dummy")
                .password(passwordEncoder.encode("dummy"))
                .roles("DUMMY")
                .build();
        return new InMemoryUserDetailsManager(dummyUser);
    }
}
