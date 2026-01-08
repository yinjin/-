package com.haocai.management.utils;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    
    @Test
    public void testPasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("=== 密码哈希结果 ===");
        System.out.println("原始密码: " + rawPassword);
        System.out.println("BCrypt哈希: " + encodedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("验证结果: " + matches);
        
        // 测试不同的哈希值
        String[] testHashes = {
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            "$2a$10$SlYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6"
        };
        
        for (String hash : testHashes) {
            boolean result = encoder.matches(rawPassword, hash);
            System.out.println("哈希 " + hash.substring(0, 20) + "... 验证结果: " + result);
        }
    }
}
