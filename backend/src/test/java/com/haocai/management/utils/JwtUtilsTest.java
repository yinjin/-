package com.haocai.management.utils;

import com.haocai.management.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 * 
 * 测试目的：验证JWT工具类的正确性与健壮性
 * 
 * 测试场景：
 * 1. 正常token生成和解析
 * 2. token过期处理
 * 3. token签名篡改检测
 * 4. 空值和异常输入处理
 * 5. 用户信息提取
 * 
 * @author 开发团队
 * @since 2026-01-05
 */
@SpringBootTest
public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        jwtUtils = new JwtUtils();
        // 通过反射设置测试用的密钥和过期时间
        try {
            java.lang.reflect.Field secretField = JwtUtils.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(jwtUtils, "test-secret-key-for-jwt-testing-2024");
            
            java.lang.reflect.Field expirationField = JwtUtils.class.getDeclaredField("expiration");
            expirationField.setAccessible(true);
            expirationField.set(jwtUtils, 86400000L);  // 24小时
        } catch (Exception e) {
            throw new RuntimeException("测试初始化失败", e);
        }
    }

    /**
     * 测试1：正常token生成和解析
     * 
     * 目的：验证基本的token生成和解析功能
     */
    @Test
    public void testGenerateAndParseToken() {
        System.out.println("\n=== 测试1：正常token生成和解析 ===");
        
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        claims.put("role", "ADMIN");
        
        // 生成token
        String token = jwtUtils.generateToken(claims, null);
        System.out.println("生成的token: " + token);
        assertNotNull(token, "token不应为null");
        assertFalse(token.isEmpty(), "token不应为空");
        
        // 解析token
        try {
            var parsedClaims = jwtUtils.parseToken(token);
            System.out.println("解析成功:");
            System.out.println("  userId: " + parsedClaims.get("userId"));
            System.out.println("  username: " + parsedClaims.get("username"));
            System.out.println("  role: " + parsedClaims.get("role"));
            
            // 使用getUserIdFromToken方法获取userId，确保类型正确
            Long userId = jwtUtils.getUserIdFromToken(token);
            assertEquals(1L, userId, "userId应该匹配");
            assertEquals("testuser", parsedClaims.get("username"), "username应该匹配");
            assertEquals("ADMIN", parsedClaims.get("role"), "role应该匹配");
            
            System.out.println("✓ 测试通过：token生成和解析正常");
        } catch (BusinessException e) {
            fail("token解析失败: " + e.getMessage());
        }
    }

    /**
     * 测试2：token验证功能
     * 
     * 目的：验证token有效性验证功能
     */
    @Test
    public void testValidateToken() {
        System.out.println("\n=== 测试2：token验证功能 ===");
        
        // 生成有效token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        String validToken = jwtUtils.generateToken(claims, null);
        
        // 验证有效token
        boolean isValid = jwtUtils.validateToken(validToken);
        System.out.println("有效token验证结果: " + isValid);
        assertTrue(isValid, "有效token应该验证通过");
        
        // 验证空token
        boolean isNullValid = jwtUtils.validateToken(null);
        System.out.println("空token验证结果: " + isNullValid);
        assertFalse(isNullValid, "空token应该验证失败");
        
        // 验证空字符串token
        boolean isEmptyValid = jwtUtils.validateToken("");
        System.out.println("空字符串token验证结果: " + isEmptyValid);
        assertFalse(isEmptyValid, "空字符串token应该验证失败");
        
        // 验证无效token
        boolean isInvalidValid = jwtUtils.validateToken("invalid.token.here");
        System.out.println("无效token验证结果: " + isInvalidValid);
        assertFalse(isInvalidValid, "无效token应该验证失败");
        
        System.out.println("✓ 测试通过：token验证功能正常");
    }

    /**
     * 测试3：从token中提取用户ID
     * 
     * 目的：验证用户ID提取功能
     */
    @Test
    public void testGetUserIdFromToken() {
        System.out.println("\n=== 测试3：从token中提取用户ID ===");
        
        // 生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123L);
        claims.put("username", "testuser");
        String token = jwtUtils.generateToken(claims, null);
        
        // 提取用户ID
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            System.out.println("提取的用户ID: " + userId);
            assertEquals(123L, userId, "用户ID应该匹配");
            System.out.println("✓ 测试通过：用户ID提取正常");
        } catch (BusinessException e) {
            fail("提取用户ID失败: " + e.getMessage());
        }
    }

    /**
     * 测试4：从token中提取用户名
     * 
     * 目的：验证用户名提取功能
     */
    @Test
    public void testGetUsernameFromToken() {
        System.out.println("\n=== 测试4：从token中提取用户名 ===");
        
        // 生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        String token = jwtUtils.generateToken(claims, null);
        
        // 提取用户名
        try {
            String username = jwtUtils.getUsernameFromToken(token);
            System.out.println("提取的用户名: " + username);
            assertEquals("testuser", username, "用户名应该匹配");
            System.out.println("✓ 测试通过：用户名提取正常");
        } catch (BusinessException e) {
            fail("提取用户名失败: " + e.getMessage());
        }
    }

    /**
     * 测试5：token过期处理
     * 
     * 目的：验证过期token的处理
     */
    @Test
    public void testExpiredToken() {
        System.out.println("\n=== 测试5：token过期处理 ===");
        
        // 生成一个立即过期的token（过期时间设为-1毫秒）
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        String expiredToken = jwtUtils.generateToken(claims, -1L);
        
        System.out.println("生成的过期token: " + expiredToken);
        
        // 验证过期token
        boolean isValid = jwtUtils.validateToken(expiredToken);
        System.out.println("过期token验证结果: " + isValid);
        assertFalse(isValid, "过期token应该验证失败");
        
        // 尝试解析过期token
        try {
            jwtUtils.parseToken(expiredToken);
            fail("过期token应该抛出异常");
        } catch (BusinessException e) {
            System.out.println("捕获到预期异常: " + e.getMessage());
            assertEquals(401, e.getCode(), "异常码应该是401");
            assertTrue(e.getMessage().contains("过期"), "异常信息应该包含'过期'");
            System.out.println("✓ 测试通过：过期token处理正常");
        }
    }

    /**
     * 测试6：token签名篡改检测
     * 
     * 目的：验证签名篡改的检测能力
     */
    @Test
    public void testTamperedToken() {
        System.out.println("\n=== 测试6：token签名篡改检测 ===");
        
        // 生成正常token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        String token = jwtUtils.generateToken(claims, null);
        
        // 篡改token（修改最后一个字符）
        String tamperedToken = token.substring(0, token.length() - 1) + "X";
        System.out.println("篡改后的token: " + tamperedToken);
        
        // 验证篡改后的token
        boolean isValid = jwtUtils.validateToken(tamperedToken);
        System.out.println("篡改token验证结果: " + isValid);
        assertFalse(isValid, "篡改的token应该验证失败");
        
        // 尝试解析篡改后的token
        try {
            jwtUtils.parseToken(tamperedToken);
            fail("篡改的token应该抛出异常");
        } catch (BusinessException e) {
            System.out.println("捕获到预期异常: " + e.getMessage());
            assertEquals(401, e.getCode(), "异常码应该是401");
            System.out.println("✓ 测试通过：签名篡改检测正常");
        }
    }

    /**
     * 测试7：空值和异常输入处理
     * 
     * 目的：验证边界情况的处理
     */
    @Test
    public void testNullAndInvalidInputs() {
        System.out.println("\n=== 测试7：空值和异常输入处理 ===");
        
        // 测试null token
        try {
            jwtUtils.parseToken(null);
            fail("null token应该抛出异常");
        } catch (BusinessException e) {
            System.out.println("null token异常: " + e.getMessage());
            assertEquals(401, e.getCode(), "异常码应该是401");
        }
        
        // 测试空字符串token
        try {
            jwtUtils.parseToken("");
            fail("空字符串token应该抛出异常");
        } catch (BusinessException e) {
            System.out.println("空字符串token异常: " + e.getMessage());
            assertEquals(401, e.getCode(), "异常码应该是401");
        }
        
        // 测试格式错误的token
        try {
            jwtUtils.parseToken("not.a.valid.jwt");
            fail("格式错误的token应该抛出异常");
        } catch (BusinessException e) {
            System.out.println("格式错误token异常: " + e.getMessage());
            assertEquals(401, e.getCode(), "异常码应该是401");
        }
        
        System.out.println("✓ 测试通过：空值和异常输入处理正常");
    }

    /**
     * 测试8：token剩余时间检查
     * 
     * 目的：验证token剩余时间计算功能
     */
    @Test
    public void testTokenRemainingTime() {
        System.out.println("\n=== 测试8：token剩余时间检查 ===");
        
        // 生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        String token = jwtUtils.generateToken(claims, null);
        
        // 获取剩余时间
        long remainingTime = jwtUtils.getTokenRemainingTime(token);
        System.out.println("token剩余时间（毫秒）: " + remainingTime);
        assertTrue(remainingTime > 0, "剩余时间应该大于0");
        assertTrue(remainingTime <= 86400000L, "剩余时间应该不超过24小时");
        
        // 检查是否即将过期（阈值设为25小时）
        // 注意：24小时过期的token，剩余时间接近24小时，小于25小时阈值，所以应该返回true
        boolean isExpiringSoon = jwtUtils.isTokenExpiringSoon(token, 90000000L);  // 25小时
        System.out.println("是否即将过期（25小时阈值）: " + isExpiringSoon);
        assertTrue(isExpiringSoon, "24小时过期的token在25小时阈值下应被视为即将过期");
        
        // 检查是否即将过期（阈值设为1小时）
        boolean isExpiringSoon1Hour = jwtUtils.isTokenExpiringSoon(token, 3600000L);  // 1小时
        System.out.println("是否即将过期（1小时阈值）: " + isExpiringSoon1Hour);
        assertFalse(isExpiringSoon1Hour, "新token在1小时阈值下不应被视为即将过期");
        
        System.out.println("✓ 测试通过：token剩余时间检查正常");
    }

    /**
     * 测试9：自定义过期时间
     * 
     * 目的：验证自定义过期时间功能
     */
    @Test
    public void testCustomExpiration() {
        System.out.println("\n=== 测试9：自定义过期时间 ===");
        
        // 生成1小时过期的token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        String token1Hour = jwtUtils.generateToken(claims, 3600000L);  // 1小时
        
        // 检查剩余时间
        long remainingTime = jwtUtils.getTokenRemainingTime(token1Hour);
        System.out.println("1小时token剩余时间（毫秒）: " + remainingTime);
        assertTrue(remainingTime > 0, "剩余时间应该大于0");
        assertTrue(remainingTime <= 3600000L, "剩余时间应该不超过1小时");
        
        System.out.println("✓ 测试通过：自定义过期时间功能正常");
    }

    /**
     * 测试10：token中不包含必要字段
     * 
     * 目的：验证缺少必要字段时的处理
     */
    @Test
    public void testMissingRequiredFields() {
        System.out.println("\n=== 测试10：token中不包含必要字段 ===");
        
        // 生成不包含userId的token
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "testuser");
        String tokenWithoutUserId = jwtUtils.generateToken(claims, null);
        
        // 尝试提取userId
        try {
            jwtUtils.getUserIdFromToken(tokenWithoutUserId);
            fail("缺少userId的token应该抛出异常");
        } catch (BusinessException e) {
            System.out.println("缺少userId异常: " + e.getMessage());
            assertEquals(401, e.getCode(), "异常码应该是401");
            assertTrue(e.getMessage().contains("用户ID"), "异常信息应该包含'用户ID'");
        }
        
        // 生成不包含username的token
        Map<String, Object> claims2 = new HashMap<>();
        claims2.put("userId", 1L);
        String tokenWithoutUsername = jwtUtils.generateToken(claims2, null);
        
        // 尝试提取username
        try {
            jwtUtils.getUsernameFromToken(tokenWithoutUsername);
            fail("缺少username的token应该抛出异常");
        } catch (BusinessException e) {
            System.out.println("缺少username异常: " + e.getMessage());
            assertEquals(401, e.getCode(), "异常码应该是401");
            assertTrue(e.getMessage().contains("用户名"), "异常信息应该包含'用户名'");
        }
        
        System.out.println("✓ 测试通过：缺少必要字段处理正常");
    }

    /**
     * 主测试方法：运行所有测试
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("JWT工具类测试套件");
        System.out.println("========================================");
        
        JwtUtilsTest test = new JwtUtilsTest();
        
        try {
            test.setUp();
            test.testGenerateAndParseToken();
            
            test.setUp();
            test.testValidateToken();
            
            test.setUp();
            test.testGetUserIdFromToken();
            
            test.setUp();
            test.testGetUsernameFromToken();
            
            test.setUp();
            test.testExpiredToken();
            
            test.setUp();
            test.testTamperedToken();
            
            test.setUp();
            test.testNullAndInvalidInputs();
            
            test.setUp();
            test.testTokenRemainingTime();
            
            test.setUp();
            test.testCustomExpiration();
            
            test.setUp();
            test.testMissingRequiredFields();
            
            System.out.println("\n========================================");
            System.out.println("✓ 所有测试通过！");
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("\n========================================");
            System.err.println("✗ 测试失败: " + e.getMessage());
            System.err.println("========================================");
            e.printStackTrace();
        }
    }
}
