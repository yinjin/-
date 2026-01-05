package com.haocai.management.utils;

import com.haocai.management.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 * 
 * 功能说明：
 * 1. 生成JWT token
 * 2. 解析JWT token
 * 3. 验证token有效性
 * 4. 从token中提取用户信息
 * 
 * 遵循规范：
 * - 配置规范：从application.yml读取JWT配置参数
 * - 异常处理规范：完善的异常处理和统一错误响应
 * - 日志规范：记录详细的操作日志
 * - 安全规范：使用安全的密钥存储和签名算法
 * 
 * @author 开发团队
 * @since 2026-01-05
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * JWT密钥
     * 遵循：安全规范-密钥分离原则
     * 遵循：配置规范-从配置文件读取
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * token默认过期时间（毫秒）
     * 遵循：配置规范-从配置文件读取
     */
    @Value("${jwt.expiration:86400000}")  // 默认24小时
    private Long expiration;

    /**
     * 生成JWT token
     * 
     * 遵循：配置规范-条款1（从配置文件读取参数）
     * 遵循：安全规范（使用HS256强签名算法）
     * 遵循：日志规范-条款3（记录生成日志）
     * 
     * @param claims 用户声明信息（如userId, username等）
     * @param expiration 过期时间（毫秒），如果为null则使用默认过期时间
     * @return JWT token字符串
     */
    public String generateToken(Map<String, Object> claims, Long expiration) {
        try {
            // 如果未指定过期时间，使用默认值
            long exp = expiration != null ? expiration : this.expiration;
            
            // 计算过期时间点
            Date expireDate = new Date(System.currentTimeMillis() + exp);
            
            // 生成密钥
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            
            // 构建JWT
            String token = Jwts.builder()
                    .claims(claims)                    // 设置声明信息
                    .issuedAt(new Date())               // 签发时间
                    .expiration(expireDate)             // 过期时间
                    .signWith(key, Jwts.SIG.HS256)      // 签名算法
                    .compact();
            
            log.debug("JWT token生成成功: userId={}, 过期时间={}", 
                    claims.get("userId"), expireDate);
            
            return token;
            
        } catch (Exception e) {
            // 遵循：异常处理规范-条款2（完善的异常处理）
            log.error("JWT token生成失败", e);
            throw new RuntimeException("JWT token生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析JWT token
     * 
     * 遵循：异常处理规范-条款2（完善的异常处理）
     * 遵循：日志规范-条款3（记录详细日志）
     * 
     * @param token JWT token字符串
     * @return Claims对象（包含token中的所有声明）
     * @throws BusinessException token无效或过期时抛出
     */
    public Claims parseToken(String token) throws BusinessException {
        try {
            // 生成密钥
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            
            // 解析JWT
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            log.debug("JWT token解析成功: userId={}, 过期时间={}", 
                    claims.get("userId"), claims.getExpiration());
            
            return claims;
            
        } catch (ExpiredJwtException e) {
            // token已过期
            log.warn("JWT token已过期: {}", e.getMessage());
            throw new BusinessException(401, "token已过期，请重新登录");
            
        } catch (UnsupportedJwtException e) {
            // 不支持的token
            log.warn("JWT token格式不支持: {}", e.getMessage());
            throw new BusinessException(401, "token格式错误");
            
        } catch (MalformedJwtException e) {
            // token格式错误
            log.warn("JWT token格式错误: {}", e.getMessage());
            throw new BusinessException(401, "token格式错误");
            
        } catch (SecurityException e) {
            // 签名验证失败
            log.warn("JWT token签名验证失败: {}", e.getMessage());
            throw new BusinessException(401, "token签名验证失败");
            
        } catch (IllegalArgumentException e) {
            // token为空
            log.warn("JWT token为空");
            throw new BusinessException(401, "token不能为空");
            
        } catch (Exception e) {
            // 其他异常
            log.error("JWT token解析失败", e);
            throw new BusinessException(401, "token解析失败: " + e.getMessage());
        }
    }

    /**
     * 验证token有效性
     * 
     * 遵循：异常处理规范-条款2（统一的错误响应）
     * 
     * @param token JWT token字符串
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                log.warn("token为空，验证失败");
                return false;
            }
            
            // 尝试解析token，如果成功则说明token有效
            parseToken(token);
            return true;
            
        } catch (BusinessException e) {
            log.warn("token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从token中提取用户ID
     * 
     * @param token JWT token字符串
     * @return 用户ID
     * @throws BusinessException token无效或过期时抛出
     */
    public Long getUserIdFromToken(String token) throws BusinessException {
        try {
            Claims claims = parseToken(token);
            Object userId = claims.get("userId");
            
            if (userId == null) {
                log.warn("token中不包含userId");
                throw new BusinessException(401, "token中不包含用户ID");
            }
            
            // 遵循：异常处理规范-条款2（完善的异常处理）
            // 处理各种数字类型转换
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId instanceof Long) {
                return (Long) userId;
            } else if (userId instanceof Number) {
                return ((Number) userId).longValue();
            } else {
                log.error("userId类型错误: {}", userId.getClass().getName());
                throw new BusinessException(401, "token中用户ID格式错误");
            }
            
        } catch (ClassCastException e) {
            log.error("userId类型转换失败", e);
            throw new BusinessException(401, "token中用户ID格式错误");
        }
    }

    /**
     * 从token中提取用户名
     * 
     * @param token JWT token字符串
     * @return 用户名
     * @throws BusinessException token无效或过期时抛出
     */
    public String getUsernameFromToken(String token) throws BusinessException {
        try {
            Claims claims = parseToken(token);
            String username = claims.get("username", String.class);
            
            if (username == null) {
                log.warn("token中不包含username");
                throw new BusinessException(401, "token中不包含用户名");
            }
            
            return username;
            
        } catch (Exception e) {
            log.error("从token中提取用户名失败", e);
            throw new BusinessException(401, "从token中提取用户名失败");
        }
    }

    /**
     * 检查token是否即将过期（剩余时间小于指定阈值）
     * 
     * @param token JWT token字符串
     * @param thresholdMillis 过期阈值（毫秒）
     * @return true-即将过期，false-未即将过期
     */
    public boolean isTokenExpiringSoon(String token, long thresholdMillis) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            return remainingTime <= thresholdMillis;
        } catch (Exception e) {
            log.warn("检查token过期状态失败", e);
            return true;  // 解析失败视为即将过期
        }
    }

    /**
     * 获取token的剩余有效期（毫秒）
     * 
     * @param token JWT token字符串
     * @return 剩余有效期（毫秒），如果token无效返回-1
     */
    public long getTokenRemainingTime(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return Math.max(0, expiration.getTime() - System.currentTimeMillis());
        } catch (Exception e) {
            log.warn("获取token剩余时间失败", e);
            return -1;
        }
    }
}
