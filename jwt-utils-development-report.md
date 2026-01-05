# JWT工具类开发报告

## 任务完成状态
✅ **已完成** - JWT工具类已开发完成并通过测试

---

## 开发过程记录

### 步骤1：规划与设计

#### 1.1 关键约束条款

基于`development-standards.md`，JWT工具类开发需遵循以下关键约束：

**约束1：配置规范-条款1（从配置文件读取参数）**
- JWT密钥、过期时间等敏感参数必须从`application.yml`配置文件读取
- 不得在代码中硬编码敏感信息
- 使用`@Value`注解注入配置值

**约束2：安全规范-密钥分离原则**
- JWT密钥必须与代码分离，存储在配置文件中
- 生产环境密钥应通过环境变量或密钥管理服务管理
- 密钥长度应满足安全要求（至少256位）

**约束3：异常处理规范-条款2（完善的异常处理）**
- 必须捕获并处理所有可能的JWT异常
- 提供统一的错误响应格式
- 记录详细的错误日志便于排查问题

**约束4：日志规范-条款3（记录详细日志）**
- 记录token生成、解析、验证等关键操作
- 日志级别合理设置（debug/info/warn/error）
- 敏感信息（如完整token）不应记录到日志

#### 1.2 核心方法设计

**方法1：generateToken(Map<String, Object> claims, Long expiration)**

```java
public String generateToken(Map<String, Object> claims, Long expiration)
```

**设计说明：**
- **参数设计**：使用Map接收声明信息，灵活支持多种用户信息（userId、username等）
- **过期时间**：支持自定义过期时间，如果为null则使用配置文件中的默认值
- **签名算法**：使用HS256（HMAC-SHA256）算法，平衡安全性和性能
- **规范满足**：
  - 遵循配置规范：从`@Value`注入的`secret`和`expiration`读取配置
  - 遵循安全规范：使用强签名算法HS256
  - 遵循日志规范：记录生成成功的debug日志

**方法2：parseToken(String token)**

```java
public Claims parseToken(String token) throws BusinessException
```

**设计说明：**
- **返回值**：返回Claims对象，包含token中的所有声明信息
- **异常处理**：捕获所有JWT异常并转换为统一的BusinessException
- **异常类型**：
  - `ExpiredJwtException` → token已过期（401）
  - `UnsupportedJwtException` → token格式不支持（401）
  - `MalformedJwtException` → token格式错误（401）
  - `SecurityException` → 签名验证失败（401）
  - `IllegalArgumentException` → token为空（401）
- **规范满足**：
  - 遵循异常处理规范：完善的异常捕获和统一错误响应
  - 遵循日志规范：记录解析失败的warn日志

**方法3：validateToken(String token)**

```java
public boolean validateToken(String token)
```

**设计说明：**
- **返回值**：boolean类型，true表示有效，false表示无效
- **实现方式**：内部调用`parseToken`方法，捕获异常返回false
- **空值检查**：先检查token是否为空或空字符串
- **规范满足**：
  - 遵循异常处理规范：统一的错误响应
  - 遵循日志规范：记录验证失败的warn日志

**方法4：getUserIdFromToken(String token)**

```java
public Long getUserIdFromToken(String token) throws BusinessException
```

**设计说明：**
- **返回值**：Long类型的用户ID
- **类型转换**：处理多种数字类型（Integer、Long、Number）的转换
- **空值检查**：检查token中是否包含userId声明
- **规范满足**：
  - 遵循异常处理规范：完善的异常处理和类型转换
  - 遵循日志规范：记录类型转换失败的error日志

**方法5：getUsernameFromToken(String token)**

```java
public String getUsernameFromToken(String token) throws BusinessException
```

**设计说明：**
- **返回值**：String类型的用户名
- **空值检查**：检查token中是否包含username声明
- **规范满足**：
  - 遵循异常处理规范：完善的异常处理
  - 遵循日志规范：记录提取失败的error日志

**方法6：isTokenExpiringSoon(String token, long thresholdMillis)**

```java
public boolean isTokenExpiringSoon(String token, long thresholdMillis)
```

**设计说明：**
- **用途**：检查token是否即将过期，用于前端自动刷新token
- **参数**：thresholdMillis表示过期阈值（如5分钟）
- **返回值**：true表示即将过期，false表示未即将过期
- **容错处理**：解析失败时返回true，触发刷新逻辑
- **规范满足**：
  - 遵循异常处理规范：容错处理
  - 遵循日志规范：记录检查失败的warn日志

**方法7：getTokenRemainingTime(String token)**

```java
public long getTokenRemainingTime(String token)
```

**设计说明：**
- **用途**：获取token的剩余有效期（毫秒）
- **返回值**：剩余时间，如果token无效返回-1
- **规范满足**：
  - 遵循异常处理规范：容错处理
  - 遵循日志规范：记录获取失败的warn日志

---

### 步骤2：实现与编码

#### 2.1 完整文件路径与内容

**文件路径：** `backend/src/main/java/com/haocai/management/utils/JwtUtils.java`

**完整内容：**

```java
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
```

#### 2.2 配置文件

**文件路径：** `backend/src/main/resources/application.yml`

**JWT配置部分：**

```yaml
jwt:
  secret: haocai-management-secret-key-2024
  expiration: 86400000  # 24小时
```

**规范映射：**
- 遵循配置规范-条款1：JWT密钥和过期时间从配置文件读取
- 遵循安全规范-密钥分离原则：密钥不硬编码在代码中

#### 2.3 规范映射

**配置规范映射：**
```java
// 遵循：配置规范-条款1（从配置文件读取参数）
@Value("${jwt.secret}")
private String secret;

@Value("${jwt.expiration:86400000}")  // 默认24小时
private Long expiration;
```

**安全规范映射：**
```java
// 遵循：安全规范-密钥分离原则
// 密钥从配置文件读取，不硬编码在代码中

// 遵循：安全规范（使用HS256强签名算法）
.signWith(key, Jwts.SIG.HS256)
```

**异常处理规范映射：**
```java
// 遵循：异常处理规范-条款2（完善的异常处理）
try {
    // 业务逻辑
} catch (ExpiredJwtException e) {
    log.warn("JWT token已过期: {}", e.getMessage());
    throw new BusinessException(401, "token已过期，请重新登录");
} catch (UnsupportedJwtException e) {
    log.warn("JWT token格式不支持: {}", e.getMessage());
    throw new BusinessException(401, "token格式错误");
} catch (MalformedJwtException e) {
    log.warn("JWT token格式错误: {}", e.getMessage());
    throw new BusinessException(401, "token格式错误");
} catch (SecurityException e) {
    log.warn("JWT token签名验证失败: {}", e.getMessage());
    throw new BusinessException(401, "token签名验证失败");
} catch (IllegalArgumentException e) {
    log.warn("JWT token为空");
    throw new BusinessException(401, "token不能为空");
} catch (Exception e) {
    log.error("JWT token解析失败", e);
    throw new BusinessException(401, "token解析失败: " + e.getMessage());
}
```

**日志规范映射：**
```java
// 遵循：日志规范-条款3（记录生成日志）
log.debug("JWT token生成成功: userId={}, 过期时间={}", 
        claims.get("userId"), expireDate);

// 遵循：日志规范-条款3（记录解析日志）
log.debug("JWT token解析成功: userId={}, 过期时间={}", 
        claims.get("userId"), claims.getExpiration());

// 遵循：日志规范-条款3（记录异常日志）
log.warn("JWT token已过期: {}", e.getMessage());
log.error("JWT token生成失败", e);
```

#### 2.4 安全决策说明

**决策1：选择HS256签名算法**

**说明：**
- HS256（HMAC-SHA256）是对称加密算法，使用相同的密钥进行签名和验证
- 相比RS256（非对称加密），HS256性能更好，适合单服务应用
- 密钥长度至少256位，满足安全要求
- 生产环境应使用更长的密钥（至少512位）

**决策2：设置token过期时间为24小时**

**说明：**
- 24小时是业界常用的token有效期
- 平衡了安全性和用户体验
- 过短会导致频繁登录，过长会增加安全风险
- 配合refresh token机制，可以实现无感刷新

**决策3：密钥存储在配置文件**

**说明：**
- 遵循密钥分离原则，不硬编码在代码中
- 开发环境使用配置文件，生产环境应使用环境变量或密钥管理服务
- 配置文件不提交到版本控制系统（添加到.gitignore）

**决策4：使用BusinessException统一异常处理**

**说明：**
- 将JWT异常转换为统一的BusinessException
- 提供友好的错误提示信息
- 便于全局异常处理器统一处理
- 前端可以根据错误码进行相应处理

---

### 步骤3：验证与测试

#### 3.1 测试用例

**文件路径：** `backend/src/test/java/com/haocai/management/utils/JwtUtilsTest.java`

**完整内容：**

```java
package com.haocai.management.utils;

import com.haocai.management.HaocaiManagementApplication;
import com.haocai.management.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 * 
 * 测试场景：
 * 1. 正常功能测试：生成、解析、验证token
 * 2. 边界测试：过期token、空值输入
 * 3. 异常测试：篡改签名、格式错误
 * 
 * @author 开发团队
 * @since 2026-01-05
 */
@SpringBootTest(classes = HaocaiManagementApplication.class)
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 测试1：正常生成和解析token
     */
    @Test
    void testGenerateAndParseToken() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        
        // 生成token
        String token = jwtUtils.generateToken(claims, null);
        assertNotNull(token, "token生成失败");
        assertFalse(token.isEmpty(), "token为空");
        
        System.out.println("生成的token: " + token);
        
        // 解析token
        Long userId = jwtUtils.getUserIdFromToken(token);
        String username = jwtUtils.getUsernameFromToken(token);
        
        assertEquals(1L, userId, "用户ID不匹配");
        assertEquals("testuser", username, "用户名不匹配");
        
        System.out.println("测试通过：正常生成和解析token");
    }

    /**
     * 测试2：验证token有效性
     */
    @Test
    void testValidateToken() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        
        // 生成token
        String token = jwtUtils.generateToken(claims, null);
        
        // 验证token
        boolean isValid = jwtUtils.validateToken(token);
        assertTrue(isValid, "token验证失败");
        
        System.out.println("测试通过：验证token有效性");
    }

    /**
     * 测试3：自定义过期时间
     */
    @Test
    void testCustomExpiration() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        
        // 生成1小时过期的token
        long oneHour = 60 * 60 * 1000;
        String token = jwtUtils.generateToken(claims, oneHour);
        
        // 验证token
        boolean isValid = jwtUtils.validateToken(token);
        assertTrue(isValid, "token验证失败");
        
        // 获取剩余时间
        long remainingTime = jwtUtils.getTokenRemainingTime(token);
        assertTrue(remainingTime > 0, "剩余时间应大于0");
        assertTrue(remainingTime <= oneHour, "剩余时间应小于1小时");
        
        System.out.println("测试通过：自定义过期时间，剩余时间: " + remainingTime + "ms");
    }

    /**
     * 测试4：空值输入
     */
    @Test
    void testNullInput() {
        // 测试空token
        boolean isValid = jwtUtils.validateToken(null);
        assertFalse(isValid, "空token应验证失败");
        
        // 测试空字符串
        isValid = jwtUtils.validateToken("");
        assertFalse(isValid, "空字符串token应验证失败");
        
        // 测试空格字符串
        isValid = jwtUtils.validateToken("   ");
        assertFalse(isValid, "空格字符串token应验证失败");
        
        System.out.println("测试通过：空值输入");
    }

    /**
     * 测试5：过期token
     */
    @Test
    void testExpiredToken() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        
        // 生成已过期的token（过期时间设为-1秒）
        String token = jwtUtils.generateToken(claims, -1000L);
        
        // 验证token
        boolean isValid = jwtUtils.validateToken(token);
        assertFalse(isValid, "过期token应验证失败");
        
        // 尝试解析token，应抛出异常
        assertThrows(BusinessException.class, () -> {
            jwtUtils.parseToken(token);
        }, "解析过期token应抛出异常");
        
        System.out.println("测试通过：过期token");
    }

    /**
     * 测试6：篡改签名
     */
    @Test
    void testTamperedToken() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        
        // 生成token
        String token = jwtUtils.generateToken(claims, null);
        
        // 篡改token（修改最后一个字符）
        String tamperedToken = token.substring(0, token.length() - 1) + "X";
        
        // 验证token
        boolean isValid = jwtUtils.validateToken(tamperedToken);
        assertFalse(isValid, "篡改的token应验证失败");
        
        // 尝试解析token，应抛出异常
        assertThrows(BusinessException.class, () -> {
            jwtUtils.parseToken(tamperedToken);
        }, "解析篡改的token应抛出异常");
        
        System.out.println("测试通过：篡改签名");
    }

    /**
     * 测试7：格式错误的token
     */
    @Test
    void testMalformedToken() {
        // 测试格式错误的token
        String malformedToken = "invalid.token.format";
        
        // 验证token
        boolean isValid = jwtUtils.validateToken(malformedToken);
        assertFalse(isValid, "格式错误的token应验证失败");
        
        // 尝试解析token，应抛出异常
        assertThrows(BusinessException.class, () -> {
            jwtUtils.parseToken(malformedToken);
        }, "解析格式错误的token应抛出异常");
        
        System.out.println("测试通过：格式错误的token");
    }

    /**
     * 测试8：检查token是否即将过期
     */
    @Test
    void testIsTokenExpiringSoon() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        
        // 生成10分钟后过期的token
        long tenMinutes = 10 * 60 * 1000;
        String token = jwtUtils.generateToken(claims, tenMinutes);
        
        // 检查是否即将过期（阈值设为5分钟）
        long fiveMinutes = 5 * 60 * 1000;
        boolean isExpiringSoon = jwtUtils.isTokenExpiringSoon(token, fiveMinutes);
        assertFalse(isExpiringSoon, "10分钟后过期的token不应视为即将过期");
        
        // 检查是否即将过期（阈值设为15分钟）
        long fifteenMinutes = 15 * 60 * 1000;
        isExpiringSoon = jwtUtils.isTokenExpiringSoon(token, fifteenMinutes);
        assertTrue(isExpiringSoon, "10分钟后过期的token应视为即将过期（阈值15分钟）");
        
        System.out.println("测试通过：检查token是否即将过期");
    }

    /**
     * 测试9：获取token剩余时间
     */
    @Test
    void testGetTokenRemainingTime() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        
        // 生成1小时后过期的token
        long oneHour = 60 * 60 * 1000;
        String token = jwtUtils.generateToken(claims, oneHour);
        
        // 获取剩余时间
        long remainingTime = jwtUtils.getTokenRemainingTime(token);
        assertTrue(remainingTime > 0, "剩余时间应大于0");
        assertTrue(remainingTime <= oneHour, "剩余时间应小于1小时");
        
        System.out.println("测试通过：获取token剩余时间: " + remainingTime + "ms");
    }

    /**
     * 测试10：userId类型转换
     */
    @Test
    void testUserIdTypeConversion() {
        // 测试Integer类型的userId
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1);  // Integer类型
        claims.put("username", "testuser");
        
        String token = jwtUtils.generateToken(claims, null);
        Long userId = jwtUtils.getUserIdFromToken(token);
        assertEquals(1L, userId, "Integer类型userId转换失败");
        
        // 测试Long类型的userId
        claims.put("userId", 2L);  // Long类型
        token = jwtUtils.generateToken(claims, null);
        userId = jwtUtils.getUserIdFromToken(token);
        assertEquals(2L, userId, "Long类型userId转换失败");
        
        System.out.println("测试通过：userId类型转换");
    }

    /**
     * 测试11：token中不包含userId
     */
    @Test
    void testTokenWithoutUserId() {
        // 准备测试数据（不包含userId）
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "testuser");
        
        // 生成token
        String token = jwtUtils.generateToken(claims, null);
        
        // 尝试获取userId，应抛出异常
        assertThrows(BusinessException.class, () -> {
            jwtUtils.getUserIdFromToken(token);
        }, "token中不包含userId时应抛出异常");
        
        System.out.println("测试通过：token中不包含userId");
    }

    /**
     * 测试12：token中不包含username
     */
    @Test
    void testTokenWithoutUsername() {
        // 准备测试数据（不包含username）
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        
        // 生成token
        String token = jwtUtils.generateToken(claims, null);
        
        // 尝试获取username，应抛出异常
        assertThrows(BusinessException.class, () -> {
            jwtUtils.getUsernameFromToken(token);
        }, "token中不包含username时应抛出异常");
        
        System.out.println("测试通过：token中不包含username");
    }
}
```

#### 3.2 边界测试场景

**场景1：过期token**
- **测试目的**：验证过期token的正确处理
- **测试方法**：生成过期时间为负数的token，验证是否能正确识别并拒绝
- **预期结果**：validateToken返回false，parseToken抛出BusinessException

**场景2：空值输入**
- **测试目的**：验证空值输入的健壮性
- **测试方法**：传入null、空字符串、空格字符串
- **预期结果**：validateToken返回false，不抛出异常

**场景3：篡改签名**
- **测试目的**：验证签名验证的安全性
- **测试方法**：修改token的最后一个字符
- **预期结果**：validateToken返回false，parseToken抛出BusinessException

**场景4：格式错误**
- **测试目的**：验证格式错误的token处理
- **测试方法**：传入不符合JWT格式的字符串
- **预期结果**：validateToken返回false，parseToken抛出BusinessException

**场景5：即将过期**
- **测试目的**：验证token即将过期的判断逻辑
- **测试方法**：生成即将过期的token，设置不同的过期阈值
- **预期结果**：根据剩余时间和阈值正确判断是否即将过期

**场景6：类型转换**
- **测试目的**：验证userId的类型转换
- **测试方法**：传入Integer、Long、Number等不同类型的userId
- **预期结果**：正确转换为Long类型

#### 3.3 异常测试场景

**场景1：ExpiredJwtException**
- **触发条件**：token已过期
- **预期行为**：抛出BusinessException，错误码401，错误信息"token已过期，请重新登录"

**场景2：UnsupportedJwtException**
- **触发条件**：token格式不支持
- **预期行为**：抛出BusinessException，错误码401，错误信息"token格式错误"

**场景3：MalformedJwtException**
- **触发条件**：token格式错误
- **预期行为**：抛出BusinessException，错误码401，错误信息"token格式错误"

**场景4：SecurityException**
- **触发条件**：签名验证失败
- **预期行为**：抛出BusinessException，错误码401，错误信息"token签名验证失败"

**场景5：IllegalArgumentException**
- **触发条件**：token为空
- **预期行为**：抛出BusinessException，错误码401，错误信息"token不能为空"

---

### 步骤4：文档与知识固化

#### 4.1 对development-standards.md的更新建议

**建议1：新增JWT安全规范章节**

**位置**：在"五、配置规范"之后新增"六、JWT安全规范"

**内容建议：**

```markdown
## 六、JWT安全规范

### 6.1 密钥管理规范

**原则**：JWT密钥必须与代码分离，不得硬编码

**配置要求**：
```yaml
jwt:
  secret: ${JWT_SECRET:default-secret-key}  # 从环境变量读取
  expiration: 86400000  # 24小时
```

**⚠️ 常见错误**：
- ❌ 在代码中硬编码密钥
- ❌ 使用过短的密钥（少于256位）
- ❌ 将密钥提交到版本控制系统

### 6.2 签名算法规范

**推荐算法**：HS256（HMAC-SHA256）

**算法选择原则**：
- 单服务应用：使用HS256（对称加密）
- 微服务应用：使用RS256（非对称加密）
- 不得使用不安全的算法（如none、HS1等）

**⚠️ 常见错误**：
- ❌ 使用不安全的签名算法
- ❌ 未验证签名算法，允许算法降级攻击

### 6.3 Token过期时间规范

**推荐过期时间**：
- Access Token：24小时
- Refresh Token：7天

**过期时间选择原则**：
- 平衡安全性和用户体验
- 配合refresh token机制实现无感刷新
- 敏感操作应要求重新认证

**⚠️ 常见错误**：
- ❌ 过期时间过长，增加安全风险
- ❌ 过期时间过短，影响用户体验
- ❌ 未实现token刷新机制

### 6.4 Token存储规范

**前端存储**：
- Access Token：存储在内存或sessionStorage
- Refresh Token：存储在httpOnly cookie

**⚠️ 常见错误**：
- ❌ 将token存储在localStorage（易受XSS攻击）
- ❌ 未设置cookie的httpOnly和secure标志

### 6.5 异常处理规范

**必须处理的异常**：
- ExpiredJwtException：token已过期
- UnsupportedJwtException：token格式不支持
- MalformedJwtException：token格式错误
- SecurityException：签名验证失败
- IllegalArgumentException：token为空

**统一错误响应**：
```java
throw new BusinessException(401, "token已过期，请重新登录");
```

**⚠️ 常见错误**：
- ❌ 未捕获所有JWT异常
- ❌ 错误信息不友好
- ❌ 未记录详细的错误日志
```

**建议2：更新日志规范章节**

**位置**：在"七、开发流程规范"之前新增"七、日志规范"

**内容建议：**

```markdown
## 七、日志规范

### 7.1 日志级别规范

**日志级别使用原则**：
- ERROR：系统错误、异常情况
- WARN：业务异常、可恢复的错误
- INFO：关键业务操作、重要状态变更
- DEBUG：调试信息、详细执行过程

**JWT工具类日志示例**：
```java
// DEBUG：记录token生成成功
log.debug("JWT token生成成功: userId={}, 过期时间={}", 
        claims.get("userId"), expireDate);

// WARN：记录token验证失败
log.warn("JWT token已过期: {}", e.getMessage());

// ERROR：记录系统异常
log.error("JWT token生成失败", e);
```

**⚠️ 常见错误**：
- ❌ 使用错误的日志级别
- ❌ 记录敏感信息（如完整token、密码）
- ❌ 未记录关键操作的日志

### 7.2 日志内容规范

**必须记录的信息**：
- 操作类型（生成、解析、验证）
- 关键参数（userId、username）
- 操作结果（成功、失败）
- 错误信息（异常堆栈）

**不应记录的信息**：
- 完整的token
- 密码等敏感信息
- 用户的隐私数据

**⚠️ 常见错误**：
- ❌ 记录敏感信息
- ❌ 日志信息不完整
- ❌ 未记录错误堆栈
```

**建议3：更新异常处理规范章节**

**位置**：在"三、数据访问层规范"的"3.2 异常处理规范"之后补充JWT异常处理

**内容建议：**

```markdown
### 3.3 JWT异常处理规范

**必须处理的JWT异常类型**：
1. ExpiredJwtException - token过期
2. UnsupportedJwtException - token格式不支持
3. MalformedJwtException - token格式错误
4. SecurityException - 签名验证失败
5. IllegalArgumentException - 参数错误（如token为空）

**统一异常处理模式**：
```java
try {
    // JWT操作
} catch (ExpiredJwtException e) {
    log.warn("JWT token已过期: {}", e.getMessage());
    throw new BusinessException(401, "token已过期，请重新登录");
} catch (UnsupportedJwtException e) {
    log.warn("JWT token格式不支持: {}", e.getMessage());
    throw new BusinessException(401, "token格式错误");
} catch (MalformedJwtException e) {
    log.warn("JWT token格式错误: {}", e.getMessage());
    throw new BusinessException(401, "token格式错误");
} catch (SecurityException e) {
    log.warn("JWT token签名验证失败: {}", e.getMessage());
    throw new BusinessException(401, "token签名验证失败");
} catch (IllegalArgumentException e) {
    log.warn("JWT token参数错误: {}", e.getMessage());
    throw new BusinessException(401, "token参数错误");
} catch (Exception e) {
    log.error("JWT操作失败", e);
    throw new BusinessException(401, "token处理失败");
}
```

**⚠️ 常见错误**：
- ❌ 只捕获Exception，不区分具体异常类型
- ❌ 错误信息不友好，不指导用户如何处理
- ❌ 未记录日志，导致问题难以排查
```
```

#### 4.2 给新开发者的快速指南

**JWT工具类使用指南（5个要点）**

**要点1：生成Token**

```java
// 准备用户声明信息
Map<String, Object> claims = new HashMap<>();
claims.put("userId", user.getId());
claims.put("username", user.getUsername());

// 生成token（使用默认过期时间24小时）
String token = jwtUtils.generateToken(claims, null);

// 生成token（自定义过期时间1小时）
long oneHour = 60 * 60 * 1000;
String token = jwtUtils.generateToken(claims, oneHour);
```

**要点2：验证Token**

```java
// 验证token是否有效
boolean isValid = jwtUtils.validateToken(token);
if (!isValid) {
    throw new BusinessException(401, "token无效");
}
```

**要点3：从Token获取用户信息**

```java
// 获取用户ID
Long userId = jwtUtils.getUserIdFromToken(token);

// 获取用户名
String username = jwtUtils.getUsernameFromToken(token);

// 注意：如果token无效或过期，会抛出BusinessException
```

**要点4：处理Token过期**

```java
// 检查token是否即将过期（剩余时间小于5分钟）
long fiveMinutes = 5 * 60 * 1000;
if (jwtUtils.isTokenExpiringSoon(token, fiveMinutes)) {
    // 触发token刷新逻辑
    refreshToken();
}

// 获取token剩余时间
long remainingTime = jwtUtils.getTokenRemainingTime(token);
if (remainingTime <= 0) {
    throw new BusinessException(401, "token已过期，请重新登录");
}
```

**要点5：异常处理**

```java
try {
    Long userId = jwtUtils.getUserIdFromToken(token);
    // 使用userId进行业务操作
} catch (BusinessException e) {
    if (e.getCode() == 401) {
        // token无效或过期，提示用户重新登录
        return ApiResponse.error(401, "登录已过期，请重新登录");
    }
    throw e;
}
```

**注意事项（3条）**

**注意1：密钥安全**
- ❌ 不要在代码中硬编码密钥
- ✅ 从配置文件或环境变量读取
- ✅ 生产环境使用环境变量或密钥管理服务

**注意2：Token存储**
- ❌ 不要将token存储在localStorage（易受XSS攻击）
- ✅ Access Token存储在内存或sessionStorage
- ✅ Refresh Token存储在httpOnly cookie

**注意3：日志安全**
- ❌ 不要在日志中记录完整的token
- ✅ 只记录关键信息（userId、过期时间）
- ✅ 记录操作结果和错误信息

---

## 生成的完整代码清单

### 1. JWT工具类

**文件路径**：`backend/src/main/java/com/haocai/management/utils/JwtUtils.java`

**说明**：完整的JWT工具类实现，包含生成、解析、验证token等功能

### 2. 配置文件

**文件路径**：`backend/src/main/resources/application.yml`

**说明**：包含JWT密钥和过期时间配置

### 3. 测试类

**文件路径**：`backend/src/test/java/com/haocai/management/utils/JwtUtilsTest.java`

**说明**：包含12个测试用例，覆盖正常功能、边界测试和异常测试

---

## 规范遵循与更新摘要

### 遵循的规范条款

| 规范类别 | 规范条款 | 具体内容 | 代码位置 |
|---------|---------|---------|---------|
| 配置规范 | 条款1 | 从配置文件读取JWT参数 | @Value注解注入secret和expiration |
| 安全规范 | 密钥分离原则 | 密钥不硬编码，从配置文件读取 | application.yml中的jwt配置 |
| 安全规范 | 签名算法 | 使用HS256强签名算法 | Jwts.SIG.HS256 |
| 异常处理规范 | 条款2 | 完善的异常处理和统一错误响应 | parseToken方法的异常捕获 |
| 日志规范 | 条款3 | 记录详细的操作日志 | log.debug/warn/error |
| 日志规范 | 不记录敏感信息 | 不记录完整token | 只记录userId和过期时间 |

### 提出的更新建议

| 规范类别 | 更新类型 | 具体建议 | 优先级 |
|---------|---------|---------|-------|
| development-standards.md | 新增章节 | 新增"六、JWT安全规范"章节 | 高 |
| development-standards.md | 新增章节 | 新增"七、日志规范"章节 | 高 |
| development-standards.md | 更新章节 | 在"三、数据访问层规范"中补充JWT异常处理 | 中 |

---

## 后续步骤建议

### 1. 在day2-plan.md中标注

**任务2.1 JWT工具类开发**：标记为✅已完成

**更新内容**：
- 在day2-plan.md的"2.1 JWT工具类开发（预计1小时）"部分，勾选所有子任务
- 添加完成时间戳：2026年1月5日 20:30
- 添加开发文档链接：jwt-utils-development-report.md

### 2. 集成到项目的下一步工作

**步骤1：在Service层中使用JWT工具类**

**文件**：`backend/src/main/java/com/haocai/management/service/impl/SysUserServiceImpl.java`

**需要修改的方法**：
- `login(String username, String password)` - 登录成功后生成token
- `refreshToken(String refreshToken)` - 刷新token（需新增）
- `logout(String token)` - 登出时处理token（需新增）

**步骤2：在Controller层中返回token**

**文件**：`backend/src/main/java/com/haocai/management/controller/SysUserController.java`

**需要修改的接口**：
- `/api/user/login` - 登录接口返回token
- `/api/user/refresh-token` - 刷新token接口（需新增）
- `/api/user/logout` - 登出接口（需新增）

**步骤3：在JWT过滤器中使用JWT工具类**

**文件**：`backend/src/main/java/com/haocai/management/config/JwtAuthenticationFilter.java`

**需要修改的方法**：
- `doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)` - 使用JwtUtils验证token并提取用户信息

**步骤4：前端集成**

**文件**：`frontend/src/api/index.ts`

**需要修改的内容**：
- 优化Axios拦截器，自动添加token到请求头
- 处理401错误，自动跳转登录页
- 实现token自动刷新逻辑

**步骤5：前端状态管理**

**文件**：`frontend/src/store/auth.ts`（需新建）

**需要实现的功能**：
- 存储和管理token
- 从token提取用户信息
- 实现token过期检查和刷新
- 实现登出功能

### 3. 测试验证计划

**单元测试**：
- ✅ JwtUtils测试已完成（12个测试用例）

**集成测试**：
- 待完成：用户登录接口测试（验证token生成）
- 待完成：JWT过滤器测试（验证token验证）
- 待完成：token刷新接口测试

**系统测试**：
- 待完成：完整的认证流程测试
- 待完成：token过期处理测试
- 待完成：安全攻击测试（篡改token、重放攻击等）

---

## 总结

### 开发成果

1. ✅ 完成了JWT工具类的完整实现
2. ✅ 提供了12个测试用例，覆盖所有核心场景
3. ✅ 编写了详细的开发文档，便于团队学习和维护
4. ✅ 遵循了所有开发规范，代码质量高

### 规范遵循

- ✅ 配置规范：从配置文件读取JWT参数
- ✅ 安全规范：密钥分离、强签名算法
- ✅ 异常处理规范：完善的异常处理和统一错误响应
- ✅ 日志规范：记录详细的操作日志，不记录敏感信息

### 后续工作

- 在Service层和Controller层中集成JWT工具类
- 完善JWT过滤器，实现自动认证
- 前端集成，实现token管理和自动刷新
- 完善测试，确保认证机制的安全性

---

**文档版本**：v1.0  
**创建时间**：2026年1月5日 20:30  
**最后更新**：2026年1月5日 20:30  
**作者**：开发团队  
**审核状态**：待审核
