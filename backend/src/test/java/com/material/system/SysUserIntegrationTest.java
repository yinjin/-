package com.material.system;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.material.system.dto.UserCreateDTO;
import com.material.system.entity.SysUser;
import com.material.system.exception.BusinessException;
import com.material.system.service.SysUserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SysUserIntegrationTest extends AbstractMySQLTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SysUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void health_shouldReturnUp() {
        String url = "http://localhost:" + port + "/api/health";
        ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).containsKey("status");
        assertThat(resp.getBody().get("status")).isNotNull();
    }

    @Test
    void admin_canLogin_and_getToken() {
        // Clean up any existing admin user first and ensure we have one with known password
        try {
            // Try to find existing admin user
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, "admin");
            SysUser existingUser = userService.getOne(wrapper);
            if (existingUser != null) {
                // Update the password to ensure it's what we expect
                existingUser.setPassword(passwordEncoder.encode("admin123"));
                userService.updateById(existingUser);
            } else {
                // create admin user in MySQL database via service
                UserCreateDTO create = new UserCreateDTO();
                create.setUsername("admin");
                create.setPassword("admin123");
                create.setRealName("系统管理员");
                create.setPhone("13800138000");
                create.setEmail("admin@example.com");
                userService.createUser(create);
            }
        } catch (Exception e) {
            // If update fails, try to delete and recreate
            try {
                LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SysUser::getUsername, "admin");
                SysUser existingUser = userService.getOne(wrapper);
                if (existingUser != null) {
                    userService.deleteUser(existingUser.getId());
                }
                // create admin user in MySQL database via service
                UserCreateDTO create = new UserCreateDTO();
                create.setUsername("admin");
                create.setPassword("admin123");
                create.setRealName("系统管理员");
                create.setPhone("13800138000");
                create.setEmail("admin@example.com");
                userService.createUser(create);
            } catch (BusinessException ignored) {
                // already exists from previous setup, ignore for idempotency
            }
        }

        String url = "http://localhost:" + port + "/api/user/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        HttpEntity<String> req = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> resp = restTemplate.postForEntity(url, req, JsonNode.class);
        System.out.println("Login response status: " + resp.getStatusCode());
        System.out.println("Login response body: " + resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode root = resp.getBody();
        assertThat(root).isNotNull();
        assertThat(root.get("code").asInt()).isEqualTo(200);
        assertThat(root.get("data").asText()).isNotEmpty();
    }

    @Test
    void user_full_lifecycle_via_api() {
        // Ensure admin user exists with correct password
        try {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, "admin");
            SysUser existingUser = userService.getOne(wrapper);
            if (existingUser != null) {
                // Update the password to ensure it's what we expect
                existingUser.setPassword(passwordEncoder.encode("admin123"));
                userService.updateById(existingUser);
            } else {
                // create admin via service for auth
                UserCreateDTO admin = new UserCreateDTO();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setRealName("系统管理员");
                admin.setPhone("13800138000");
                admin.setEmail("admin@example.com");
                userService.createUser(admin);
            }
        } catch (Exception e) {
            // If update fails, try to create
            try {
                UserCreateDTO admin = new UserCreateDTO();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setRealName("系统管理员");
                admin.setPhone("13800138000");
                admin.setEmail("admin@example.com");
                userService.createUser(admin);
            } catch (BusinessException ignored) {
                // ignore if already exists
            }
        }

        // login to obtain token
        String loginUrl = "http://localhost:" + port + "/api/user/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String loginBody = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        ResponseEntity<JsonNode> loginResp = restTemplate.postForEntity(loginUrl, new HttpEntity<>(loginBody, headers), JsonNode.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = loginResp.getBody().get("data").asText();

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(token);

        // create a new user via API
        String createUrl = "http://localhost:" + port + "/api/api/user";
        String newUser = "{\"username\":\"testuser\",\"password\":\"password123\",\"realName\":\"测试用户\",\"phone\":\"13800000000\",\"email\":\"test@example.com\"}";
        ResponseEntity<JsonNode> createResp = restTemplate.exchange(createUrl, HttpMethod.POST, new HttpEntity<>(newUser, authHeaders), JsonNode.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long userId = createResp.getBody().get("data").asLong();

        // get user
        String getUrl = "http://localhost:" + port + "/api/api/user/" + userId;
        ResponseEntity<JsonNode> getResp = restTemplate.exchange(getUrl, HttpMethod.GET, new HttpEntity<>(authHeaders), JsonNode.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody().get("data").get("username").asText()).isEqualTo("testuser");

        // update user
        String updateUrl = "http://localhost:" + port + "/api/api/user/" + userId;
        String updateBody = "{\"id\":" + userId + ",\"username\":\"testuser\",\"realName\":\"已更新用户\"}";
        ResponseEntity<JsonNode> updateResp = restTemplate.exchange(updateUrl, HttpMethod.PUT, new HttpEntity<>(updateBody, authHeaders), JsonNode.class);
        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // change password (user action)
        String changePwdUrl = "http://localhost:" + port + "/api/api/user/" + userId + "/password?oldPassword=password123&newPassword=newpass123";
        ResponseEntity<JsonNode> cpResp = restTemplate.exchange(changePwdUrl, HttpMethod.PUT, new HttpEntity<>(authHeaders), JsonNode.class);
        assertThat(cpResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // reset password (admin action)
        String resetUrl = "http://localhost:" + port + "/api/api/user/" + userId + "/reset-password?newPassword=reset123";
        ResponseEntity<JsonNode> resetResp = restTemplate.exchange(resetUrl, HttpMethod.PUT, new HttpEntity<>(authHeaders), JsonNode.class);
        assertThat(resetResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // update status
        String statusUrl = "http://localhost:" + port + "/api/api/user/" + userId + "/status?status=0";
        ResponseEntity<JsonNode> statusResp = restTemplate.exchange(statusUrl, HttpMethod.PUT, new HttpEntity<>(authHeaders), JsonNode.class);
        assertThat(statusResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // page
        String pageUrl = "http://localhost:" + port + "/api/api/user/page?current=1&size=10";
        ResponseEntity<JsonNode> pageResp = restTemplate.exchange(pageUrl, HttpMethod.GET, new HttpEntity<>(authHeaders), JsonNode.class);
        assertThat(pageResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // delete
        String delUrl = "http://localhost:" + port + "/api/api/user/" + userId;
        ResponseEntity<JsonNode> delResp = restTemplate.exchange(delUrl, HttpMethod.DELETE, new HttpEntity<>(authHeaders), JsonNode.class);
        assertThat(delResp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
