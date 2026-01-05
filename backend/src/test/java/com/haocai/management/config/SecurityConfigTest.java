package com.haocai.management.config;

import com.haocai.management.HaocaiManagementApplication;
import com.haocai.management.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring Security配置测试类
 * <p>
 * 测试目标：
 * 1. 密码编码器配置正确性
 * 2. JWT工具类可用性
 * 3. Security配置基本功能验证
 * <p>
 * 测试场景：
 * - 密码加密和验证
 * - JWT token生成和验证
 * - Security配置加载
 * <p>
 * 注意：这是基础的配置测试，完整的认证授权测试需要集成测试
 */
@SpringBootTest(classes = HaocaiManagementApplication.class)
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired(required = false)
    private SecurityConfig securityConfig;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private JwtUtils jwtUtils;

    /**
     * 测试1：验证Security配置加载成功
     */
    @Test
    public void testSecurityConfigLoaded() {
        assertNotNull(securityConfig, "SecurityConfig应该被成功加载");
    }

    /**
     * 测试2：验证密码编码器配置
     */
    @Test
    public void testPasswordEncoder() {
        assertNotNull(passwordEncoder, "PasswordEncoder应该被成功配置");

        String rawPassword = "test123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 验证加密后的密码不为空
        assertNotNull(encodedPassword);
        assertFalse(encodedPassword.isEmpty());

        // 验证加密后的密码与原密码不同
        assertNotEquals(rawPassword, encodedPassword);

        // 验证加密后的密码可以验证原密码
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));

        // 验证错误密码无法通过验证
        assertFalse(passwordEncoder.matches("wrongpassword", encodedPassword));

        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
    }

    /**
     * 测试3：验证JWT工具类可用
     */
    @Test
    public void testJwtUtilsAvailability() {
        assertNotNull(jwtUtils, "JwtUtils应该被成功注入");
    }

    /**
     * 测试4：密码编码器多次加密同一密码结果不同
     * 验证BCrypt的盐值机制
     */
    @Test
    public void testPasswordEncoderDifferentResults() {
        String password = "samePassword";
        String encoded1 = passwordEncoder.encode(password);
        String encoded2 = passwordEncoder.encode(password);

        // 同一密码多次加密结果应该不同（因为盐值不同）
        assertNotEquals(encoded1, encoded2, "相同密码的加密结果应该不同");

        // 但都能验证原密码
        assertTrue(passwordEncoder.matches(password, encoded1));
        assertTrue(passwordEncoder.matches(password, encoded2));
    }

    /**
     * 测试5：验证空密码加密
     */
    @Test
    public void testPasswordEncoderEmptyPassword() {
        String emptyPassword = "";
        String encodedPassword = passwordEncoder.encode(emptyPassword);

        assertNotNull(encodedPassword);
        assertTrue(passwordEncoder.matches(emptyPassword, encodedPassword));
    }

    /**
     * 测试6：验证特殊字符密码
     */
    @Test
    public void testPasswordEncoderSpecialCharacters() {
        String specialPassword = "P@$$w0rd!#$%^&*()_+-=[]{}|;':\",./<>?";
        String encodedPassword = passwordEncoder.encode(specialPassword);

        assertTrue(passwordEncoder.matches(specialPassword, encodedPassword));
    }
}
