package com.haocai.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置类
 * 配置Spring Security的密码编码器，用于用户密码的加密和验证
 */
@Configuration
public class PasswordConfig {

    /**
     * 配置BCrypt密码编码器
     * BCrypt是一种安全的单向哈希算法，专门为密码哈希设计
     *
     * 为什么选择BCrypt？
     * 1. 自适应复杂度：可以调整计算强度，随着计算能力提升而增加安全性
     * 2. 包含盐值：自动生成随机盐值，防止彩虹表攻击
     * 3. 抗暴力破解：计算复杂度高，难以通过暴力破解
     * 4. 标准实现：被广泛使用和验证的安全算法
     *
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 默认强度为10，意味着2^10=1024次哈希计算
        // 强度范围：4-31，强度越高安全性越好但性能越低
        return new BCryptPasswordEncoder();
    }
}