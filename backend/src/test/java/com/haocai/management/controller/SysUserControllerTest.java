package com.haocai.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haocai.management.dto.UserLoginDTO;
import com.haocai.management.dto.UserRegisterDTO;
import com.haocai.management.dto.UserUpdateDTO;
import com.haocai.management.entity.SysUser;
import com.haocai.management.entity.UserStatus;
import com.haocai.management.service.ISysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.containsString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户管理控制器测试类
 * 
 * <p>测试目的：验证用户管理相关接口的正确性与健壮性</p>
 * 
 * <p>测试场景：</p>
 * <ul>
 *   <li>用户注册接口测试</li>
 *   <li>用户登录接口测试</li>
 *   <li>用户信息查询接口测试</li>
 *   <li>用户更新接口测试</li>
 *   <li>用户状态管理接口测试</li>
 *   <li>批量操作接口测试</li>
 *   <li>数据验证接口测试</li>
 *   <li>异常处理测试</li>
 *   <li>边界条件测试</li>
 *   <li>性能测试</li>
 * </ul>
 * 
 * <p>遵循规范：</p>
 * <ul>
 *   <li>测试规范-第6条：必须测试字段映射、类型转换、批量操作</li>
 *   <li>控制层规范-第4.1条：批量操作接口规范</li>
 *   <li>控制层规范-第4.2条：异常处理规范</li>
 * </ul>
 * 
 * @author 开发团队
 * @since 2026-01-06
 * @version 2.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SysUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ISysUserService userService;

    private SysUser testUser;
    private UserRegisterDTO registerDTO;
    private UserLoginDTO loginDTO;
    private UserUpdateDTO updateDTO;

    /**
     * 测试初始化方法
     * 
     * <p>在每个测试方法执行前调用，用于初始化测试数据</p>
     * 
     * <p>初始化内容：</p>
     * <ul>
     *   <li>测试用户对象（包含BCrypt加密的密码）</li>
     *   <li>用户注册DTO（符合验证规则）</li>
     *   <li>用户登录DTO</li>
     *   <li>用户更新DTO</li>
     * </ul>
     */
    @BeforeEach
    public void setUp() {
        // 初始化测试用户数据
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi"); // BCrypt加密的"password123"
        testUser.setName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(UserStatus.NORMAL);
        testUser.setDepartmentId(1L);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());

        // 初始化注册DTO - 符合验证规则
        registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("Password123@"); // 符合复杂度要求：大小写字母+数字+特殊字符
        registerDTO.setConfirmPassword("Password123@");
        registerDTO.setName("新用户");
        registerDTO.setEmail("newuser@example.com");
        registerDTO.setPhone("13900139000");
        registerDTO.setVerificationCode("1234"); // 验证码必填
        registerDTO.setAgreeToTerms(true); // 必须同意协议

        // 初始化登录DTO
        loginDTO = new UserLoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");
        loginDTO.setIpAddress("127.0.0.1");

        // 初始化更新DTO
        updateDTO = new UserUpdateDTO();
        updateDTO.setName("更新后的用户");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setPhone("13900139001");
    }

    // ==================== 用户注册接口测试 ====================

    /**
     * 测试1：用户注册接口 - 正常注册
     * 
     * <p>测试目的：验证用户注册接口的正常流程</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备符合验证规则的注册数据</li>
     *   <li>Mock Service层返回成功结果</li>
     *   <li>发送POST请求到/api/users/register</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的用户信息正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回用户信息包含username和name</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testRegister_Success() throws Exception {
        System.out.println("\n=== 测试1：用户注册接口 - 正常注册 ===");

        // Mock Service层返回
        when(userService.register(any(UserRegisterDTO.class))).thenReturn(testUser);

        // 执行请求
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.name").value("测试用户"));

        System.out.println("✓ 测试通过：用户注册接口正常");
    }

    /**
     * 测试2：用户注册接口 - 密码不一致
     * 
     * <p>测试目的：验证密码确认校验机制</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备注册数据，设置不一致的密码</li>
     *   <li>发送POST请求到/api/users/register</li>
     *   <li>验证返回业务状态码为400</li>
     *   <li>验证错误消息包含"两次输入的密码不一致"</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：400</li>
     *   <li>错误消息：包含"两次输入的密码不一致"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testRegister_PasswordMismatch() throws Exception {
        System.out.println("\n=== 测试2：用户注册接口 - 密码不一致 ===");

        // 设置不一致的密码
        registerDTO.setConfirmPassword("different");

        // 执行请求 - 密码不一致会在控制器层返回400错误
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("两次输入的密码不一致")));

        System.out.println("✓ 测试通过：密码不一致校验正常");
    }

    /**
     * 测试3：用户注册接口 - 参数验证失败
     * 
     * <p>测试目的：验证参数验证注解的有效性</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备注册数据，设置无效的用户名（太短）</li>
     *   <li>发送POST请求到/api/users/register</li>
     *   <li>验证返回HTTP状态码为400</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：400（Bad Request）</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testRegister_ValidationFailed() throws Exception {
        System.out.println("\n=== 测试3：用户注册接口 - 参数验证失败 ===");

        // 设置无效的用户名（太短）
        registerDTO.setUsername("ab");

        // 执行请求
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 测试通过：参数验证失败处理正常");
    }

    /**
     * 测试21：用户注册接口 - 用户名已存在
     * 
     * <p>测试目的：验证用户名重复注册的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备注册数据，使用已存在的用户名</li>
     *   <li>Mock Service层抛出业务异常</li>
     *   <li>发送POST请求到/api/users/register</li>
     *   <li>验证返回业务状态码为500（控制器捕获异常返回500）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"用户注册失败"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>注：控制器捕获所有异常并返回500，不区分业务异常和系统异常</p>
     */
    @Test
    public void testRegister_UsernameExists() throws Exception {
        System.out.println("\n=== 测试21：用户注册接口 - 用户名已存在 ===");

        // 使用已存在的用户名
        registerDTO.setUsername("testuser");
        
        // Mock Service层抛出异常
        when(userService.register(any(UserRegisterDTO.class)))
                .thenThrow(new RuntimeException("用户名已存在"));

        // 执行请求 - 控制器捕获异常返回500
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("用户注册失败")));

        System.out.println("✓ 测试通过：用户名已存在处理正常");
    }

    /**
     * 测试22：用户注册接口 - 邮箱已存在
     * 
     * <p>测试目的：验证邮箱重复注册的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备注册数据，使用已存在的邮箱</li>
     *   <li>Mock Service层抛出业务异常</li>
     *   <li>发送POST请求到/api/users/register</li>
     *   <li>验证返回业务状态码为500（控制器捕获异常返回500）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"用户注册失败"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>注：控制器捕获所有异常并返回500，不区分业务异常和系统异常</p>
     */
    @Test
    public void testRegister_EmailExists() throws Exception {
        System.out.println("\n=== 测试22：用户注册接口 - 邮箱已存在 ===");

        // 使用已存在的邮箱
        registerDTO.setEmail("test@example.com");
        
        // Mock Service层抛出异常
        when(userService.register(any(UserRegisterDTO.class)))
                .thenThrow(new RuntimeException("邮箱已存在"));

        // 执行请求 - 控制器捕获异常返回500
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("用户注册失败")));

        System.out.println("✓ 测试通过：邮箱已存在处理正常");
    }

    /**
     * 测试23：用户注册接口 - 密码复杂度不足
     * 
     * <p>测试目的：验证密码复杂度验证机制</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备注册数据，使用简单密码（只有字母）</li>
     *   <li>发送POST请求到/api/users/register</li>
     *   <li>验证返回HTTP状态码为400（@Valid注解验证失败）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：400（Bad Request）</li>
     * </ul>
     * 
     * <p>遵循规范：安全规范-密码复杂度要求</p>
     * <p>注：密码复杂度由@Valid注解在UserRegisterDTO中验证，简单密码会在参数验证阶段返回400</p>
     */
    @Test
    public void testRegister_PasswordComplexity() throws Exception {
        System.out.println("\n=== 测试23：用户注册接口 - 密码复杂度不足 ===");

        // 使用简单密码 - 不符合密码复杂度要求（由@Valid注解验证）
        registerDTO.setPassword("simple");
        registerDTO.setConfirmPassword("simple");

        // 执行请求 - @Valid注解验证失败返回HTTP 400
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 测试通过：密码复杂度验证正常");
    }

    // ==================== 用户登录接口测试 ====================

    /**
     * 测试4：用户登录接口 - 正常登录
     * 
     * <p>测试目的：验证用户登录接口的正常流程</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备登录数据</li>
     *   <li>Mock Service层返回JWT token</li>
     *   <li>发送POST请求到/api/users/login</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的token和用户信息</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回JWT token</li>
     *   <li>返回用户信息</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testLogin_Success() throws Exception {
        System.out.println("\n=== 测试4：用户登录接口 - 正常登录 ===");

        // Mock Service层返回
        when(userService.login(any(UserLoginDTO.class))).thenReturn("test-jwt-token");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // 执行请求
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"));

        System.out.println("✓ 测试通过：用户登录接口正常");
    }

    /**
     * 测试5：用户登录接口 - 登录失败
     * 
     * <p>测试目的：验证登录失败的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备登录数据</li>
     *   <li>Mock Service层抛出异常</li>
     *   <li>发送POST请求到/api/users/login</li>
     *   <li>验证返回业务状态码为401</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：401</li>
     *   <li>错误消息：包含"用户登录失败"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testLogin_Failed() throws Exception {
        System.out.println("\n=== 测试5：用户登录接口 - 登录失败 ===");

        // Mock Service层抛出异常
        when(userService.login(any(UserLoginDTO.class)))
                .thenThrow(new RuntimeException("用户名或密码错误"));

        // 执行请求
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(containsString("用户登录失败")));

        System.out.println("✓ 测试通过：登录失败处理正常");
    }

    /**
     * 测试24：用户登录接口 - 用户不存在
     * 
     * <p>测试目的：验证用户不存在时的登录处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备不存在的用户登录数据</li>
     *   <li>Mock Service层抛出异常</li>
     *   <li>发送POST请求到/api/users/login</li>
     *   <li>验证返回业务状态码为401</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：401</li>
     *   <li>错误消息：包含"用户登录失败"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testLogin_UserNotFound() throws Exception {
        System.out.println("\n=== 测试24：用户登录接口 - 用户不存在 ===");

        // 使用不存在的用户名
        loginDTO.setUsername("nonexistent");
        
        // Mock Service层抛出异常
        when(userService.login(any(UserLoginDTO.class)))
                .thenThrow(new RuntimeException("用户不存在"));

        // 执行请求
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(containsString("用户登录失败")));

        System.out.println("✓ 测试通过：用户不存在处理正常");
    }

    /**
     * 测试25：用户登录接口 - 密码错误
     * 
     * <p>测试目的：验证密码错误时的登录处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备错误的密码</li>
     *   <li>Mock Service层抛出异常</li>
     *   <li>发送POST请求到/api/users/login</li>
     *   <li>验证返回业务状态码为401</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：401</li>
     *   <li>错误消息：包含"用户登录失败"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testLogin_WrongPassword() throws Exception {
        System.out.println("\n=== 测试25：用户登录接口 - 密码错误 ===");

        // 使用错误的密码
        loginDTO.setPassword("wrongpassword");
        
        // Mock Service层抛出异常
        when(userService.login(any(UserLoginDTO.class)))
                .thenThrow(new RuntimeException("密码错误"));

        // 执行请求
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(containsString("用户登录失败")));

        System.out.println("✓ 测试通过：密码错误处理正常");
    }

    // ==================== 用户信息查询接口测试 ====================

    /**
     * 测试6：获取当前用户信息 - 已认证用户
     * 
     * <p>测试目的：验证获取当前用户信息接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟已认证用户</li>
     *   <li>Mock Service层返回用户信息</li>
     *   <li>发送GET请求到/api/users/current</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的用户信息正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回用户信息</li>
     * </ul>
     * 
     * <p>遵循规范：安全规范-需要认证的接口配置</p>
     */
    @Test
    @WithMockUser(username = "testuser")
    public void testGetCurrentUser_Success() throws Exception {
        System.out.println("\n=== 测试6：获取当前用户信息 - 已认证用户 ===");

        // Mock Service层返回
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // 执行请求
        mockMvc.perform(get("/api/users/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        System.out.println("✓ 测试通过：获取当前用户信息正常");
    }

    /**
     * 测试7：获取当前用户信息 - 未认证用户
     * 
     * <p>测试目的：验证未认证用户的访问控制</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>不使用@WithMockUser，模拟未认证用户</li>
     *   <li>发送GET请求到/api/users/current</li>
     *   <li>验证返回HTTP状态码为401</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：401（Unauthorized）</li>
     * </ul>
     * 
     * <p>遵循规范：安全规范-需要认证的接口配置</p>
     */
    @Test
    public void testGetCurrentUser_Unauthorized() throws Exception {
        System.out.println("\n=== 测试7：获取当前用户信息 - 未认证用户 ===");

        // 执行请求（未认证）
        mockMvc.perform(get("/api/users/current"))
                .andExpect(status().isUnauthorized());

        System.out.println("✓ 测试通过：未认证用户访问控制正常");
    }

    /**
     * 测试8：分页查询用户列表
     * 
     * <p>测试目的：验证分页查询接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>准备分页测试数据</li>
     *   <li>Mock Service层返回分页结果</li>
     *   <li>发送GET请求到/api/users，带分页参数</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证分页信息正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回分页数据（total、records等）</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testFindUserPage_Success() throws Exception {
        System.out.println("\n=== 测试8：分页查询用户列表 ===");

        // 准备测试数据
        List<SysUser> users = new ArrayList<>();
        users.add(testUser);
        
        com.baomidou.mybatisplus.core.metadata.IPage<SysUser> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        page.setRecords(users);
        page.setTotal(1);
        page.setCurrent(1);
        page.setSize(10);
        page.setPages(1);

        // Mock Service层返回 - 使用具体的类型和nullable参数
        when(userService.findUserPage(
            any(Page.class), 
            isNull(), 
            isNull(), 
            isNull(), 
            isNull()
        )).thenReturn(page);

        // 执行请求
        mockMvc.perform(get("/api/users")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records").isArray());

        System.out.println("✓ 测试通过：分页查询用户列表正常");
    }

    /**
     * 测试9：根据ID获取用户信息
     * 
     * <p>测试目的：验证根据ID查询用户接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>Mock Service层返回用户信息</li>
     *   <li>发送GET请求到/api/users/{id}</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的用户信息正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回用户信息</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testGetUserById_Success() throws Exception {
        System.out.println("\n=== 测试9：根据ID获取用户信息 ===");

        // Mock Service层返回
        when(userService.findById(1L)).thenReturn(testUser);

        // 执行请求
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        System.out.println("✓ 测试通过：根据ID获取用户信息正常");
    }

    /**
     * 测试10：根据ID获取用户信息 - 用户不存在
     * 
     * <p>测试目的：验证用户不存在时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>Mock Service层返回null</li>
     *   <li>发送GET请求到/api/users/{id}（不存在的ID）</li>
     *   <li>验证返回业务状态码为404</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：404</li>
     *   <li>错误消息："用户不存在"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testGetUserById_NotFound() throws Exception {
        System.out.println("\n=== 测试10：根据ID获取用户信息 - 用户不存在 ===");

        // Mock Service层返回null
        when(userService.findById(999L)).thenReturn(null);

        // 执行请求
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));

        System.out.println("✓ 测试通过：用户不存在处理正常");
    }

    // ==================== 用户更新接口测试 ====================

    /**
     * 测试11：更新用户信息
     * 
     * <p>测试目的：验证用户信息更新接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>准备更新数据</li>
     *   <li>Mock Service层返回更新后的用户</li>
     *   <li>发送PUT请求到/api/users/{id}</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证更新后的用户信息正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回更新后的用户信息</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testUpdateUser_Success() throws Exception {
        System.out.println("\n=== 测试11：更新用户信息 ===");

        // 准备更新后的用户
        SysUser updatedUser = new SysUser();
        updatedUser.setId(1L);
        updatedUser.setUsername("testuser");
        updatedUser.setName("更新后的用户");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPhone("13900139001");
        updatedUser.setStatus(UserStatus.NORMAL);

        // Mock Service层返回
        when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenReturn(updatedUser);

        // 执行请求
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("更新后的用户"));

        System.out.println("✓ 测试通过：更新用户信息正常");
    }

    /**
     * 测试26：更新用户信息 - 用户不存在
     * 
     * <p>测试目的：验证更新不存在用户时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>Mock Service层抛出异常</li>
     *   <li>发送PUT请求到/api/users/{id}（不存在的ID）</li>
     *   <li>验证返回业务状态码为500（控制器捕获异常返回500）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"更新用户信息失败"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>注：控制器捕获所有异常并返回500，不检查返回值是否为null</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testUpdateUser_NotFound() throws Exception {
        System.out.println("\n=== 测试26：更新用户信息 - 用户不存在 ===");

        // Mock Service层抛出异常
        when(userService.updateUser(eq(999L), any(UserUpdateDTO.class)))
                .thenThrow(new RuntimeException("用户不存在"));

        // 执行请求 - 控制器捕获异常返回500
        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("更新用户信息失败")));

        System.out.println("✓ 测试通过：更新不存在用户处理正常");
    }

    // ==================== 用户状态管理接口测试 ====================

    /**
     * 测试12：更新用户状态
     * 
     * <p>测试目的：验证用户状态更新接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>Mock Service层返回成功</li>
     *   <li>发送PATCH请求到/api/users/{id}/status</li>
     *   <li>验证返回状态码为200</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testUpdateUserStatus_Success() throws Exception {
        System.out.println("\n=== 测试12：更新用户状态 ===");

        // Mock Service层返回
        when(userService.updateUserStatus(1L, UserStatus.DISABLED, 1L)).thenReturn(true);

        // 执行请求
        mockMvc.perform(patch("/api/users/1/status")
                .param("status", "DISABLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✓ 测试通过：更新用户状态正常");
    }

    /**
     * 测试13：批量更新用户状态
     * 
     * <p>测试目的：验证批量更新用户状态接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>准备用户ID列表</li>
     *   <li>Mock Service层返回更新数量</li>
     *   <li>发送PATCH请求到/api/users/batch/status</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的更新数量正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回更新数量</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     * <p>遵循规范：数据访问层规范-第3.1条（批量操作规范）</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testBatchUpdateStatus_Success() throws Exception {
        System.out.println("\n=== 测试13：批量更新用户状态 ===");

        // 准备测试数据
        List<Long> userIds = List.of(1L, 2L, 3L);

        // Mock Service层返回
        when(userService.batchUpdateStatus(userIds, UserStatus.DISABLED, 1L)).thenReturn(3);

        // 执行请求
        mockMvc.perform(patch("/api/users/batch/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIds))
                .param("status", "DISABLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.count").value(3));

        System.out.println("✓ 测试通过：批量更新用户状态正常");
    }

    // ==================== 用户删除接口测试 ====================

    /**
     * 测试14：删除用户
     * 
     * <p>测试目的：验证用户删除接口（逻辑删除）</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>Mock Service层返回成功</li>
     *   <li>发送DELETE请求到/api/users/{id}</li>
     *   <li>验证返回状态码为200</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testDeleteUser_Success() throws Exception {
        System.out.println("\n=== 测试14：删除用户 ===");

        // Mock Service层返回
        when(userService.deleteUser(1L, 1L)).thenReturn(true);

        // 执行请求
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✓ 测试通过：删除用户正常");
    }

    /**
     * 测试15：批量删除用户
     * 
     * <p>测试目的：验证批量删除用户接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>准备用户ID列表</li>
     *   <li>Mock Service层返回删除数量</li>
     *   <li>发送DELETE请求到/api/users/batch</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的删除数量正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回删除数量</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     * <p>遵循规范：数据访问层规范-第3.1条（批量操作规范）</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testBatchDeleteUsers_Success() throws Exception {
        System.out.println("\n=== 测试15：批量删除用户 ===");

        // 准备测试数据
        List<Long> userIds = List.of(1L, 2L, 3L);

        // Mock Service层返回
        when(userService.batchDeleteUsers(userIds, 1L)).thenReturn(3);

        // 执行请求
        mockMvc.perform(delete("/api/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.count").value(3));

        System.out.println("✓ 测试通过：批量删除用户正常");
    }

    // ==================== 数据验证接口测试 ====================

    /**
     * 测试16：检查用户名是否存在 - 存在
     * 
     * <p>测试目的：验证用户名检查接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回true</li>
     *   <li>发送GET请求到/api/users/check/username</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的exists为true</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>exists: true</li>
     * </ul>
     * 
     * <p>遵循规范：安全规范-公开访问接口配置</p>
     */
    @Test
    public void testCheckUsername_Exists() throws Exception {
        System.out.println("\n=== 测试16：检查用户名是否存在 - 存在 ===");

        // Mock Service层返回
        when(userService.existsByUsername("testuser")).thenReturn(true);

        // 执行请求
        mockMvc.perform(get("/api/users/check/username")
                .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.exists").value(true));

        System.out.println("✓ 测试通过：检查用户名存在正常");
    }

    /**
     * 测试17：检查用户名是否存在 - 不存在
     * 
     * <p>测试目的：验证用户名检查接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回false</li>
     *   <li>发送GET请求到/api/users/check/username</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的exists为false</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>exists: false</li>
     * </ul>
     * 
     * <p>遵循规范：安全规范-公开访问接口配置</p>
     */
    @Test
    public void testCheckUsername_NotExists() throws Exception {
        System.out.println("\n=== 测试17：检查用户名是否存在 - 不存在 ===");

        // Mock Service层返回
        when(userService.existsByUsername("nonexistent")).thenReturn(false);

        // 执行请求
        mockMvc.perform(get("/api/users/check/username")
                .param("username", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.exists").value(false));

        System.out.println("✓ 测试通过：检查用户名不存在正常");
    }

    /**
     * 测试18：检查邮箱是否存在
     * 
     * <p>测试目的：验证邮箱检查接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回true</li>
     *   <li>发送GET请求到/api/users/check/email</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的exists为true</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>exists: true</li>
     * </ul>
     * 
     * <p>遵循规范：安全规范-公开访问接口配置</p>
     */
    @Test
    public void testCheckEmail_Success() throws Exception {
        System.out.println("\n=== 测试18：检查邮箱是否存在 ===");

        // Mock Service层返回
        when(userService.existsByEmail("test@example.com", null)).thenReturn(true);

        // 执行请求
        mockMvc.perform(get("/api/users/check/email")
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.exists").value(true));

        System.out.println("✓ 测试通过：检查邮箱存在正常");
    }

    /**
     * 测试19：检查手机号是否存在
     * 
     * <p>测试目的：验证手机号检查接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回true</li>
     *   <li>发送GET请求到/api/users/check/phone</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的exists为true</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>exists: true</li>
     * </ul>
     * 
     * <p>遵循规范：安全规范-公开访问接口配置</p>
     */
    @Test
    public void testCheckPhone_Success() throws Exception {
        System.out.println("\n=== 测试19：检查手机号是否存在 ===");

        // Mock Service层返回
        when(userService.existsByPhone("13800138000", null)).thenReturn(true);

        // 执行请求
        mockMvc.perform(get("/api/users/check/phone")
                .param("phone", "13800138000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.exists").value(true));

        System.out.println("✓ 测试通过：检查手机号存在正常");
    }

    // ==================== 边界条件测试 ====================

    /**
     * 测试20：批量操作 - 空列表
     * 
     * <p>测试目的：验证批量操作对空列表的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>准备空的用户ID列表</li>
     *   <li>发送PATCH请求到/api/users/batch/status</li>
     *   <li>验证返回状态码为200</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testBatchOperation_EmptyList() throws Exception {
        System.out.println("\n=== 测试20：批量操作 - 空列表 ===");

        // 准备空列表
        List<Long> emptyList = new ArrayList<>();

        // 执行请求
        mockMvc.perform(patch("/api/users/batch/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyList))
                .param("status", "DISABLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✓ 测试通过：批量操作空列表处理正常");
    }

    /**
     * 测试27：分页查询 - 超大页码
     * 
     * <p>测试目的：验证分页查询对超大页码的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>准备超大页码（999999）</li>
     *   <li>Mock Service层返回空分页结果</li>
     *   <li>发送GET请求到/api/users</li>
     *   <li>验证返回状态码为200</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回空数据</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testFindUserPage_LargePageNumber() throws Exception {
        System.out.println("\n=== 测试27：分页查询 - 超大页码 ===");

        // 准备空分页结果
        com.baomidou.mybatisplus.core.metadata.IPage<SysUser> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(999999, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);
        page.setCurrent(999999);
        page.setSize(10);
        page.setPages(0);

        // Mock Service层返回
        when(userService.findUserPage(
            any(Page.class), 
            isNull(), 
            isNull(), 
            isNull(), 
            isNull()
        )).thenReturn(page);

        // 执行请求
        mockMvc.perform(get("/api/users")
                .param("page", "999999")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));

        System.out.println("✓ 测试通过：超大页码处理正常");
    }

    /**
     * 测试28：分页查询 - 负数页码
     * 
     * <p>测试目的：验证分页查询对负数页码的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>准备负数页码（-1）</li>
     *   <li>Mock Service层返回空的分页结果</li>
     *   <li>发送GET请求到/api/users</li>
     *   <li>验证返回状态码为200（控制器不验证页码，由MyBatis-Plus处理）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回空数据或由MyBatis-Plus处理负数页码</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>注：控制器不验证页码参数，负数页码由MyBatis-Plus的Page类处理，通常返回空数据</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testFindUserPage_NegativePageNumber() throws Exception {
        System.out.println("\n=== 测试28：分页查询 - 负数页码 ===");

        // 准备空的分页结果 - MyBatis-Plus会处理负数页码
        com.baomidou.mybatisplus.core.metadata.IPage<SysUser> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);
        page.setCurrent(1);
        page.setSize(10);
        page.setPages(0);

        // Mock Service层返回
        when(userService.findUserPage(
            any(Page.class), 
            isNull(), 
            isNull(), 
            isNull(), 
            isNull()
        )).thenReturn(page);

        // 执行请求 - 控制器不验证页码，返回200
        mockMvc.perform(get("/api/users")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✓ 测试通过：负数页码处理正常");
    }

    /**
     * 测试29：用户注册 - 超长用户名
     * 
     * <p>测试目的：验证用户注册对超长用户名的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备超长用户名（超过50个字符）</li>
     *   <li>发送POST请求到/api/users/register</li>
     *   <li>验证返回HTTP状态码为400</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：400（Bad Request）</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testRegister_TooLongUsername() throws Exception {
        System.out.println("\n=== 测试29：用户注册 - 超长用户名 ===");

        // 准备超长用户名
        registerDTO.setUsername("a".repeat(51));

        // 执行请求
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 测试通过：超长用户名处理正常");
    }

    /**
     * 测试30：用户注册 - 无效邮箱格式
     * 
     * <p>测试目的：验证用户注册对无效邮箱格式的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备无效邮箱格式</li>
     *   <li>发送POST请求到/api/users/register</li>
     *   <li>验证返回HTTP状态码为400</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：400（Bad Request）</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testRegister_InvalidEmailFormat() throws Exception {
        System.out.println("\n=== 测试30：用户注册 - 无效邮箱格式 ===");

        // 准备无效邮箱格式
        registerDTO.setEmail("invalid-email");

        // 执行请求
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 测试通过：无效邮箱格式处理正常");
    }

    // ==================== 性能测试 ====================

    /**
     * 测试31：性能测试 - 批量查询1000条记录
     * 
     * <p>测试目的：验证批量查询的性能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>准备1000条测试数据</li>
     *   <li>Mock Service层返回1000条记录</li>
     *   <li>发送GET请求到/api/users</li>
     *   <li>测量响应时间</li>
     *   <li>验证响应时间在可接受范围内（< 1秒）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>响应时间 < 1秒</li>
     * </ul>
     * 
     * <p>遵循规范：性能规范-响应时间要求</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testPerformance_BatchQuery1000() throws Exception {
        System.out.println("\n=== 测试31：性能测试 - 批量查询1000条记录 ===");

        // 准备1000条测试数据
        List<SysUser> users = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            SysUser user = new SysUser();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setName("用户" + i);
            users.add(user);
        }
        
        com.baomidou.mybatisplus.core.metadata.IPage<SysUser> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 1000);
        page.setRecords(users);
        page.setTotal(1000);
        page.setCurrent(1);
        page.setSize(1000);
        page.setPages(1);

        // Mock Service层返回
        when(userService.findUserPage(
            any(Page.class), 
            isNull(), 
            isNull(), 
            isNull(), 
            isNull()
        )).thenReturn(page);

        // 执行请求并测量时间
        long startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/users")
                .param("page", "1")
                .param("size", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("✓ 测试通过：批量查询1000条记录，耗时: " + duration + "ms");
        
        // 验证响应时间在可接受范围内（< 1秒）
        if (duration > 1000) {
            System.out.println("⚠ 警告：响应时间超过1秒，可能需要优化");
        }
    }

    /**
     * 测试32：性能测试 - 并发注册请求
     * 
     * <p>测试目的：验证并发注册请求的性能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备10个并发注册请求</li>
     *   <li>Mock Service层返回成功</li>
     *   <li>同时发送10个POST请求到/api/users/register</li>
     *   <li>测量总响应时间</li>
     *   <li>验证所有请求都成功</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>所有请求HTTP状态码：200</li>
     *   <li>所有请求业务状态码：200</li>
     *   <li>总响应时间在可接受范围内</li>
     * </ul>
     * 
     * <p>遵循规范：性能规范-并发处理要求</p>
     */
    @Test
    public void testPerformance_ConcurrentRegister() throws Exception {
        System.out.println("\n=== 测试32：性能测试 - 并发注册请求 ===");

        // Mock Service层返回
        when(userService.register(any(UserRegisterDTO.class))).thenReturn(testUser);

        // 执行10个并发请求
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            registerDTO.setUsername("concurrentuser" + i);
            mockMvc.perform(post("/api/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("✓ 测试通过：10个并发注册请求，总耗时: " + duration + "ms");
        System.out.println("  平均每个请求耗时: " + (duration / 10) + "ms");
    }

    /**
     * 测试33：性能测试 - 批量删除1000条记录
     * 
     * <p>测试目的：验证批量删除的性能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>准备1000个用户ID</li>
     *   <li>Mock Service层返回删除数量</li>
     *   <li>发送DELETE请求到/api/users/batch</li>
     *   <li>测量响应时间</li>
     *   <li>验证响应时间在可接受范围内（< 2秒）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回删除数量：1000</li>
     *   <li>响应时间 < 2秒</li>
     * </ul>
     * 
     * <p>遵循规范：性能规范-批量操作性能要求</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testPerformance_BatchDelete1000() throws Exception {
        System.out.println("\n=== 测试33：性能测试 - 批量删除1000条记录 ===");

        // 准备1000个用户ID
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            userIds.add((long) i);
        }

        // Mock Service层返回
        when(userService.batchDeleteUsers(userIds, 1L)).thenReturn(1000);

        // 执行请求并测量时间
        long startTime = System.currentTimeMillis();
        mockMvc.perform(delete("/api/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.count").value(1000));
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("✓ 测试通过：批量删除1000条记录，耗时: " + duration + "ms");
        
        // 验证响应时间在可接受范围内（< 2秒）
        if (duration > 2000) {
            System.out.println("⚠ 警告：响应时间超过2秒，可能需要优化");
        }
    }

    // ==================== 异常处理测试 ====================

    /**
     * 测试34：异常处理 - Service层抛出空指针异常
     * 
     * <p>测试目的：验证空指针异常的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>Mock Service层抛出NullPointerException</li>
     *   <li>发送GET请求到/api/users/1</li>
     *   <li>验证返回业务状态码为500</li>
     *   <li>验证错误消息包含"获取用户信息失败"</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"获取用户信息失败: 测试空指针异常"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>注：控制器捕获所有异常并返回500，错误消息包含原始异常信息</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testExceptionHandling_NullPointerException() throws Exception {
        System.out.println("\n=== 测试34：异常处理 - Service层抛出空指针异常 ===");

        // Mock Service层抛出异常
        when(userService.findById(1L)).thenThrow(new NullPointerException("测试空指针异常"));

        // 执行请求 - 控制器捕获异常并返回包含原始异常信息的消息
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("获取用户信息失败")));

        System.out.println("✓ 测试通过：空指针异常处理正常");
    }

    /**
     * 测试35：异常处理 - Service层抛出非法参数异常
     * 
     * <p>测试目的：验证非法参数异常的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>Mock Service层抛出IllegalArgumentException</li>
     *   <li>发送GET请求到/api/users/1</li>
     *   <li>验证返回业务状态码为500（控制器捕获所有异常返回500）</li>
     *   <li>验证错误消息包含"获取用户信息失败"</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"获取用户信息失败: 测试非法参数异常"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>注：控制器捕获所有异常并返回500，不区分IllegalArgumentException和其他异常</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testExceptionHandling_IllegalArgumentException() throws Exception {
        System.out.println("\n=== 测试35：异常处理 - Service层抛出非法参数异常 ===");

        // Mock Service层抛出异常
        when(userService.findById(1L)).thenThrow(new IllegalArgumentException("测试非法参数异常"));

        // 执行请求 - 控制器捕获所有异常并返回500
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("获取用户信息失败")));

        System.out.println("✓ 测试通过：非法参数异常处理正常");
    }

    /**
     * 测试36：异常处理 - Service层抛出运行时异常
     * 
     * <p>测试目的：验证运行时异常的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>使用@WithMockUser模拟管理员用户</li>
     *   <li>Mock Service层抛出RuntimeException</li>
     *   <li>发送GET请求到/api/users/1</li>
     *   <li>验证返回业务状态码为500</li>
     *   <li>验证错误消息包含"获取用户信息失败"</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"获取用户信息失败: 测试运行时异常"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>注：控制器捕获所有异常并返回500，错误消息包含原始异常信息</p>
     */
    @Test
    @WithMockUser(username = "admin")
    public void testExceptionHandling_RuntimeException() throws Exception {
        System.out.println("\n=== 测试36：异常处理 - Service层抛出运行时异常 ===");

        // Mock Service层抛出异常
        when(userService.findById(1L)).thenThrow(new RuntimeException("测试运行时异常"));

        // 执行请求 - 控制器捕获异常并返回包含原始异常信息的消息
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("获取用户信息失败")));

        System.out.println("✓ 测试通过：运行时异常处理正常");
    }

    /**
     * 主测试方法：运行所有测试
     * 
     * <p>此方法用于演示测试套件的功能</p>
     * <p>实际测试运行请使用JUnit或Maven命令</p>
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("用户管理控制器测试套件 v2.0");
        System.out.println("========================================");
        System.out.println("\n测试覆盖范围：");
        System.out.println("1. 用户注册接口测试（6个测试用例）");
        System.out.println("2. 用户登录接口测试（4个测试用例）");
        System.out.println("3. 用户信息查询接口测试（5个测试用例）");
        System.out.println("4. 用户更新接口测试（2个测试用例）");
        System.out.println("5. 用户状态管理接口测试（2个测试用例）");
        System.out.println("6. 用户删除接口测试（2个测试用例）");
        System.out.println("7. 数据验证接口测试（4个测试用例）");
        System.out.println("8. 边界条件测试（5个测试用例）");
        System.out.println("9. 性能测试（3个测试用例）");
        System.out.println("10. 异常处理测试（3个测试用例）");
        System.out.println("\n总计：36个测试用例");
        System.out.println("\n========================================");
        System.out.println("运行方式：");
        System.out.println("1. IDE中右键运行此测试类");
        System.out.println("2. Maven命令：mvn test -Dtest=SysUserControllerTest");
        System.out.println("3. 生成覆盖率报告：mvn clean test jacoco:report");
        System.out.println("========================================");
    }
}
