package com.material.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import com.material.system.config.JwtAuthenticationFilter;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        /**
         * 配置安全过滤器链，定义HTTP安全规则
         * 
         * @param http HttpSecurity对象，用于配置HTTP安全设置
         * @return SecurityFilterChain 安全过滤器链实例
         * @throws Exception 配置过程中可能抛出的异常
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
            // 配置HTTP安全规则，包括CSRF禁用和请求授权规则
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/actuator/**").permitAll()
                            .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                            .requestMatchers("/health", "/welcome").permitAll()
                            .requestMatchers("/api/user/login").permitAll()
                            .anyRequest().authenticated()
                    );

            // 在匿名认证过滤器之前插入 JWT 认证过滤器
            http.addFilterBefore(jwtAuthenticationFilter, AnonymousAuthenticationFilter.class);
    
            return http.build();
        }
}
