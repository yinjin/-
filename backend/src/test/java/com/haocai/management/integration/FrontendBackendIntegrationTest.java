package com.haocai.management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haocai.management.HaocaiManagementApplication;
import com.haocai.management.dto.UserLoginDTO;
import com.haocai.management.dto.UserRegisterDTO;
import com.haocai.management.dto.UserUpdateDTO;
import com.haocai.management.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 前后端联调集成测试
 * 
 * 测试目标：
 * 1. 验证前后端API接口的完整性和正确性
 * 2. 验证JWT认证机制
 * 3. 验证业务逻辑的正确性
 * 4. 验证异常处理机制
 * 
 * 遵循：测试规范-集成测试要求
 * - 使用@SpringBootTest启动完整应用上下文
 * - 使用@AutoConfigureMockMvc模拟HTTP请求
 * - 测试真实的数据库交互
 * - 验证完整的请求-响应流程
 * 
 * @author 系统开发团队
 * @since 2026-01-06
 */
@SpringBootTest(classes = HaocaiManagementApplication.class)
@AutoConfigureMockMvc
public class FrontendBackendIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String adminToken;
    private Long testUserId;

    /**
     * 测试前准备：重置admin用户状态并登录获取token
     * 
     * 遵循：测试规范-测试数据准备
     * 在每个测试方法执行前准备必要的测试数据
     * 
     * 遵循：测试规范-测试隔离
     * 确保每个测试在干净的状态下运行
     */
    @BeforeEach
    public void setUp() throws Exception {
        // 重置admin用户状态为NORMAL，确保测试能够正常登录
        // 遵循：测试规范-测试数据清理
        jdbcTemplate.update("UPDATE sys_user SET status = 0, deleted = 0, name = '系统管理员', email = 'admin@example.com', phone = '13800138000' WHERE username = 'admin'");
        
        // 登录获取token
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        adminToken = (String) data.get("token");
        
        System.out.println("? 登录测试通过，获取到token");
    }

    /**
     * 测试1：用户登录成功
     * 
     * 遵循：测试规范-正常流程测试
     * 验证用户能够成功登录并获取JWT token
     */
    @Test
    public void testLoginSuccess() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.user.username").value("admin"))
                .andExpect(jsonPath("$.data.user.name").value("系统管理员"))
                .andExpect(jsonPath("$.data.user.status").value(0));

        System.out.println("? 登录成功测试通过");
    }

    /**
     * 测试2：用户注册成功
     * 
     * 遵循：测试规范-正常流程测试
     * 验证新用户能够成功注册
     */
    @Test
    public void testRegisterSuccess() throws Exception {
        long timestamp = System.currentTimeMillis();
        // 使用时间戳的后5位生成用户名，确保长度不超过20个字符
        String username = "user" + (timestamp % 100000);
        // 使用时间戳生成唯一手机号，避免冲突
        String phone = "138" + String.format("%08d", timestamp % 100000000);
        
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername(username);
        registerDTO.setPassword("Test@123456");
        registerDTO.setConfirmPassword("Test@123456");
        registerDTO.setName("测试用户");
        registerDTO.setEmail("test" + timestamp + "@example.com");
        registerDTO.setPhone(phone);
        registerDTO.setVerificationCode("123456");
        registerDTO.setAgreeToTerms(true);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").exists())
                .andExpect(jsonPath("$.data.name").value("测试用户"));

        System.out.println("? 注册成功测试通过");
    }

    /**
     * 测试3：获取当前用户信息
     * 
     * 遵循：测试规范-认证测试
     * 验证使用JWT token能够获取当前用户信息
     */
    @Test
    public void testGetCurrentUser() throws Exception {
        mockMvc.perform(get("/api/users/current")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.name").value("系统管理员"));

        System.out.println("? 获取当前用户信息测试通过");
    }

    /**
     * 测试4：分页查询用户列表
     * 
     * 遵循：测试规范-正常流程测试
     * 验证能够分页查询用户列表
     */
    @Test
    public void testGetUserList() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").exists())
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(10));

        System.out.println("? 分页查询用户列表测试通过");
    }

    /**
     * 测试5：根据ID获取用户信息
     * 
     * 遵循：测试规范-正常流程测试
     * 验证能够根据ID获取指定用户信息
     */
    @Test
    public void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"));

        System.out.println("? 根据ID获取用户信息测试通过");
    }

    /**
     * 测试6：更新用户信息
     * 
     * 遵循：测试规范-正常流程测试
     * 验证能够更新用户信息
     * 注意：使用时间戳生成唯一手机号，避免与数据库中其他用户冲突
     */
    @Test
    public void testUpdateUser() throws Exception {
        long timestamp = System.currentTimeMillis();
        // 使用时间戳生成唯一手机号，避免与数据库中其他用户冲突
        String phone = "139" + String.format("%08d", timestamp % 100000000);
        
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("系统管理员更新");
        updateDTO.setEmail("admin-updated@example.com");
        updateDTO.setPhone(phone);

        mockMvc.perform(put("/api/users/1")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("系统管理员更新"))
                .andExpect(jsonPath("$.data.email").value("admin-updated@example.com"));

        System.out.println("? 更新用户信息测试通过");
    }

    /**
     * 测试7：更新用户状态
     * 
     * 遵循：测试规范-正常流程测试
     * 验证能够更新用户状态
     * 注意：此测试创建独立的测试用户，避免影响admin用户和后续测试
     */
    @Test
    public void testUpdateUserStatus() throws Exception {
        long timestamp = System.currentTimeMillis();
        String username = "status" + (timestamp % 100000);
        String phone = "138" + String.format("%08d", (timestamp + 1) % 100000000);
        
        // 先创建一个测试用户
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername(username);
        registerDTO.setPassword("Test@123456");
        registerDTO.setConfirmPassword("Test@123456");
        registerDTO.setName("状态测试用户");
        registerDTO.setEmail(username + "@example.com");
        registerDTO.setPhone(phone);
        registerDTO.setVerificationCode("123456");
        registerDTO.setAgreeToTerms(true);

        MvcResult result = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        Long testUserId = Long.valueOf(data.get("id").toString());
        
        // 更新测试用户状态
        mockMvc.perform(patch("/api/users/" + testUserId + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "DISABLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证状态已更新
        mockMvc.perform(get("/api/users/" + testUserId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(1));

        // 恢复状态
        mockMvc.perform(patch("/api/users/" + testUserId + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "NORMAL"))
                .andExpect(status().isOk());

        System.out.println("? 更新用户状态测试通过");
    }

    /**
     * 测试8：批量更新用户状态
     * 
     * 遵循：测试规范-正常流程测试
     * 验证能够批量更新用户状态
     * 注意：此测试先创建测试用户，然后测试批量更新，避免影响admin用户
     */
    @Test
    public void testBatchUpdateStatus() throws Exception {
        long timestamp = System.currentTimeMillis();
        String username = "batch" + (timestamp % 100000);
        String phone = "138" + String.format("%08d", (timestamp + 2) % 100000000);
        
        // 先创建一个测试用户
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername(username);
        registerDTO.setPassword("Test@123456");
        registerDTO.setConfirmPassword("Test@123456");
        registerDTO.setName("批量测试用户");
        registerDTO.setEmail(username + "@example.com");
        registerDTO.setPhone(phone);
        registerDTO.setVerificationCode("123456");
        registerDTO.setAgreeToTerms(true);

        MvcResult result = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        Long testUserId = Long.valueOf(data.get("id").toString());
        
        // 批量更新测试用户状态
        List<Long> userIds = Arrays.asList(testUserId);
        
        mockMvc.perform(patch("/api/users/batch/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "DISABLED")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.count").value(1));

        // 验证状态已更新
        mockMvc.perform(get("/api/users/" + testUserId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(1));

        // 恢复状态
        mockMvc.perform(patch("/api/users/batch/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "NORMAL")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk());

        System.out.println("? 批量更新用户状态测试通过");
    }

    /**
     * 测试9：删除用户（逻辑删除）
     * 
     * 遵循：测试规范-正常流程测试
     * 验证能够逻辑删除用户
     */
    @Test
    public void testDeleteUser() throws Exception {
        long timestamp = System.currentTimeMillis();
        String username = "del" + (timestamp % 100000);
        String phone = "138" + String.format("%08d", (timestamp + 3) % 100000000);
        
        // 先创建一个测试用户
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername(username);
        registerDTO.setPassword("Test@123456");
        registerDTO.setConfirmPassword("Test@123456");
        registerDTO.setName("待删除用户");
        registerDTO.setEmail(username + "@example.com");
        registerDTO.setPhone(phone);
        registerDTO.setVerificationCode("123456");
        registerDTO.setAgreeToTerms(true);

        MvcResult result = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        testUserId = Long.valueOf(data.get("id").toString());

        // 删除用户
        mockMvc.perform(delete("/api/users/" + testUserId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        System.out.println("? 删除用户测试通过");
    }

    /**
     * 测试10：批量删除用户
     * 
     * 遵循：测试规范-正常流程测试
     * 验证能够批量删除用户
     */
    @Test
    public void testBatchDeleteUsers() throws Exception {
        long timestamp = System.currentTimeMillis();
        String phone1 = "138" + String.format("%08d", (timestamp + 4) % 100000000);
        String phone2 = "138" + String.format("%08d", (timestamp + 5) % 100000000);
        
        // 创建两个测试用户
        UserRegisterDTO registerDTO1 = new UserRegisterDTO();
        registerDTO1.setUsername("del1" + (timestamp % 100000));
        registerDTO1.setPassword("Test@123456");
        registerDTO1.setConfirmPassword("Test@123456");
        registerDTO1.setName("待删除用户A");
        registerDTO1.setEmail("del1" + timestamp + "@example.com");
        registerDTO1.setPhone(phone1);
        registerDTO1.setVerificationCode("123456");
        registerDTO1.setAgreeToTerms(true);

        UserRegisterDTO registerDTO2 = new UserRegisterDTO();
        registerDTO2.setUsername("del2" + ((timestamp + 1) % 100000));
        registerDTO2.setPassword("Test@123456");
        registerDTO2.setConfirmPassword("Test@123456");
        registerDTO2.setName("待删除用户B");
        registerDTO2.setEmail("del2" + timestamp + "@example.com");
        registerDTO2.setPhone(phone2);
        registerDTO2.setVerificationCode("123456");
        registerDTO2.setAgreeToTerms(true);

        MvcResult result1 = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO1)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result2 = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO2)))
                .andExpect(status().isOk())
                .andReturn();

        String response1 = result1.getResponse().getContentAsString();
        String response2 = result2.getResponse().getContentAsString();
        Map<String, Object> responseMap1 = objectMapper.readValue(response1, Map.class);
        Map<String, Object> responseMap2 = objectMapper.readValue(response2, Map.class);
        Map<String, Object> data1 = (Map<String, Object>) responseMap1.get("data");
        Map<String, Object> data2 = (Map<String, Object>) responseMap2.get("data");
        
        Long userId1 = Long.valueOf(data1.get("id").toString());
        Long userId2 = Long.valueOf(data2.get("id").toString());

        List<Long> userIds = Arrays.asList(userId1, userId2);

        // 批量删除
        mockMvc.perform(delete("/api/users/batch")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.count").value(2));

        System.out.println("? 批量删除用户测试通过");
    }

    /**
     * 测试11：未授权访问
     * 
     * 遵循：测试规范-安全测试
     * 验证未提供token时无法访问需要认证的接口
     */
    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        System.out.println("? 未授权访问测试通过");
    }

    /**
     * 测试12：无效token访问
     * 
     * 遵循：测试规范-安全测试
     * 验证使用无效token时无法访问需要认证的接口
     */
    @Test
    public void testInvalidToken() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());

        System.out.println("? 无效token访问测试通过");
    }

    /**
     * 测试13：用户状态枚举转换
     * 
     * 遵循：测试规范-数据类型测试
     * 验证用户状态枚举能够正确转换
     * 注意：此测试创建独立的测试用户，避免影响admin用户和后续测试
     */
    @Test
    public void testUserStatusConversion() throws Exception {
        long timestamp = System.currentTimeMillis();
        String username = "enum" + (timestamp % 100000);
        String phone = "138" + String.format("%08d", (timestamp + 6) % 100000000);
        
        // 先创建一个测试用户
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername(username);
        registerDTO.setPassword("Test@123456");
        registerDTO.setConfirmPassword("Test@123456");
        registerDTO.setName("枚举测试用户");
        registerDTO.setEmail(username + "@example.com");
        registerDTO.setPhone(phone);
        registerDTO.setVerificationCode("123456");
        registerDTO.setAgreeToTerms(true);

        MvcResult result = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        Long testUserId = Long.valueOf(data.get("id").toString());
        
        // 更新状态为DISABLED
        mockMvc.perform(patch("/api/users/" + testUserId + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "DISABLED"))
                .andExpect(status().isOk());

        // 验证状态值
        mockMvc.perform(get("/api/users/" + testUserId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(1));

        // 恢复状态
        mockMvc.perform(patch("/api/users/" + testUserId + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "NORMAL"))
                .andExpect(status().isOk());

        System.out.println("? 用户状态枚举转换测试通过");
    }

    /**
     * 测试14：参数验证
     * 
     * 遵循：测试规范-边界测试
     * 验证参数验证机制
     */
    @Test
    public void testParameterValidation() throws Exception {
        // 测试缺少必填参数
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername("admin");
        // 缺少password

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest());

        System.out.println("? 参数验证测试通过");
    }

    /**
     * 测试15：异常处理
     * 
     * 遵循：测试规范-异常测试
     * 验证异常处理机制
     */
    @Test
    public void testExceptionHandling() throws Exception {
        // 测试查询不存在的用户
        mockMvc.perform(get("/api/users/99999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("用户不存在")));

        System.out.println("? 异常处理测试通过");
    }

    /**
     * 测试16：检查用户名是否存在
     * 
     * 遵循：测试规范-正常流程测试
     * 验证能够检查用户名是否存在
     */
    @Test
    public void testCheckUsername() throws Exception {
        mockMvc.perform(get("/api/users/check/username")
                .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(true));

        mockMvc.perform(get("/api/users/check/username")
                .param("username", "nonexistentuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(false));

        System.out.println("? 检查用户名是否存在测试通过");
    }

    /**
     * 测试17：检查邮箱是否存在
     * 
     * 遵循：测试规范-正常流程测试
     * 验证能够检查邮箱是否存在
     */
    @Test
    public void testCheckEmail() throws Exception {
        // 检查存在的邮箱
        mockMvc.perform(get("/api/users/check/email")
                .param("email", "admin@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(true));

        // 检查不存在的邮箱
        long timestamp = System.currentTimeMillis();
        mockMvc.perform(get("/api/users/check/email")
                .param("email", "nonexistent" + timestamp + "@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(false));

        System.out.println("? 检查邮箱是否存在测试通过");
    }

    /**
     * 测试18：检查手机号是否存在
     * 
     * 遵循：测试规范-正常流程测试
     * 验证能够检查手机号是否存在
     */
    @Test
    public void testCheckPhone() throws Exception {
        long timestamp = System.currentTimeMillis();
        String phone = "138" + String.format("%08d", (timestamp + 7) % 100000000);
        
        // 检查一个不存在的手机号
        mockMvc.perform(get("/api/users/check/phone")
                .param("phone", phone))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(false));

        System.out.println("? 检查手机号是否存在测试通过");
    }
}
