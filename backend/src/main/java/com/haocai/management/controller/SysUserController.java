package com.haocai.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.common.ApiResponse;
import com.haocai.management.dto.UserLoginDTO;
import com.haocai.management.dto.UserRegisterDTO;
import com.haocai.management.dto.UserUpdateDTO;
import com.haocai.management.dto.UserVO;
import com.haocai.management.entity.SysUser;
import com.haocai.management.entity.UserStatus;
import com.haocai.management.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * 提供用户管理相关的RESTful API接口
 * 包括用户注册、登录、信息查询、更新、删除等功能
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 用户注册接口
     * POST /api/users/register
     * 
     * 遵循：安全规范-公开访问接口配置
     * 使用@PermitAll注解允许匿名访问
     *
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ApiResponse<UserVO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        log.info("用户注册请求: username={}", registerDTO.getUsername());
        
        // 密码确认校验
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            return ApiResponse.error(400, "两次输入的密码不一致");
        }
        
        try {
            SysUser user = userService.register(registerDTO);
            UserVO userVO = convertToUserVO(user);
            return ApiResponse.success(userVO);
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return ApiResponse.error(500, "用户注册失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录接口
     * POST /api/users/login
     * 
     * 遵循：安全规范-公开访问接口配置
     * 使用@PermitAll注解允许匿名访问
     *
     * @param loginDTO 登录信息
     * @param request  HTTP请求
     * @return 登录结果，包含JWT token
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO loginDTO,
                                                   HttpServletRequest request) {
        log.info("用户登录请求: username={}", loginDTO.getUsername());
        
        // 获取客户端IP地址
        String ipAddress = getClientIpAddress(request);
        loginDTO.setIpAddress(ipAddress);
        
        try {
            String token = userService.login(loginDTO);
            
            // 获取用户信息
            SysUser user = userService.findByUsername(loginDTO.getUsername());
            UserVO userVO = convertToUserVO(user);
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", userVO);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return ApiResponse.error(401, "用户登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     * GET /api/users/current
     * 需要 JWT token 认证
     * 
     * 遵循：安全规范-需要认证的接口配置
     * 使用@PreAuthorize("isAuthenticated()")确保用户已认证
     *
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        try {
            // 从JWT token中获取用户ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 从SecurityContext中获取已认证的用户信息
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ApiResponse.error(401, "未认证");
            }
            
            // 从认证信息中获取用户名
            String username = authentication.getName();
            
            // 根据用户名查询用户信息
            SysUser user = userService.findByUsername(username);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            UserVO userVO = convertToUserVO(user);
            return ApiResponse.success(userVO);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ApiResponse.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询用户列表
     * GET /api/users
     * 
     * 遵循：安全规范-需要认证的接口配置
     * 使用@PreAuthorize("isAuthenticated()")确保用户已认证
     *
     * @param page         页码（默认1）
     * @param size         每页大小（默认10）
     * @param username     用户名关键词（可选）
     * @param realName     真实姓名关键词（可选）
     * @param status       用户状态（可选）
     * @param departmentId 部门ID（可选）
     * @return 分页结果
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Map<String, Object>> findUserPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) Long departmentId) {
        
        try {
            Page<SysUser> pageParam = new Page<>(page, size);
            IPage<SysUser> userPage = userService.findUserPage(
                pageParam, username, name, status, departmentId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", userPage.getRecords().stream().map(this::convertToUserVO).toList());
            result.put("total", userPage.getTotal());
            result.put("current", userPage.getCurrent());
            result.put("size", userPage.getSize());
            result.put("pages", userPage.getPages());
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return ApiResponse.error(500, "查询用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取用户信息
     * GET /api/users/{id}
     * 
     * 遵循：安全规范-需要认证的接口配置
     * 使用@PreAuthorize("isAuthenticated()")确保用户已认证
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserVO> getUserById(@PathVariable Long id) {
        try {
            SysUser user = userService.findById(id);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            UserVO userVO = convertToUserVO(user);
            return ApiResponse.success(userVO);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ApiResponse.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * PUT /api/users/{id}
     * 
     * 遵循：安全规范-需要认证的接口配置
     * 使用@PreAuthorize("isAuthenticated()")确保用户已认证
     *
     * @param id        用户ID
     * @param updateDTO 更新信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserVO> updateUser(@PathVariable Long id,
                                          @Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("更新用户信息: userId={}", id);
        
        try {
            SysUser user = userService.updateUser(id, updateDTO);
            UserVO userVO = convertToUserVO(user);
            return ApiResponse.success(userVO);
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return ApiResponse.error(500, "更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     * PATCH /api/users/{id}/status
     * 
     * 遵循：安全规范-需要认证的接口配置
     * 使用@PreAuthorize("isAuthenticated()")确保用户已认证
     *
     * @param id     用户ID
     * @param status 新状态
     * @return 更新结果
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status,
            HttpServletRequest request) {
        log.info("更新用户状态: userId={}, status={}", id, status);
        
        try {
            // TODO: 从JWT token中获取操作人ID
            Long updateBy = 1L;
            
            boolean success = userService.updateUserStatus(id, status, updateBy);
            if (success) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error(500, "更新用户状态失败");
            }
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return ApiResponse.error(500, "更新用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新用户状态
     * PATCH /api/users/batch/status
     * 
     * 遵循：安全规范-需要认证的接口配置
     * 使用@PreAuthorize("isAuthenticated()")确保用户已认证
     *
     * @param userIds 用户ID列表
     * @param status  新状态
     * @return 更新结果
     */
    @PatchMapping("/batch/status")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Map<String, Integer>> batchUpdateStatus(
            @RequestBody List<Long> userIds,
            @RequestParam UserStatus status,
            HttpServletRequest request) {
        log.info("批量更新用户状态: userIds={}, status={}", userIds, status);
        
        try {
            // TODO: 从JWT token中获取操作人ID
            Long updateBy = 1L;
            
            int count = userService.batchUpdateStatus(userIds, status, updateBy);
            
            Map<String, Integer> result = new HashMap<>();
            result.put("count", count);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("批量更新用户状态失败", e);
            return ApiResponse.error(500, "批量更新用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户（逻辑删除）
     * DELETE /api/users/{id}
     * 
     * 遵循：安全规范-需要认证的接口配置
     * 使用@PreAuthorize("isAuthenticated()")确保用户已认证
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        log.info("删除用户: userId={}", id);
        
        try {
            // TODO: 从JWT token中获取操作人ID
            Long deleteBy = 1L;
            
            boolean success = userService.deleteUser(id, deleteBy);
            if (success) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error(500, "删除用户失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ApiResponse.error(500, "删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除用户（逻辑删除）
     * DELETE /api/users/batch
     * 
     * 遵循：安全规范-需要认证的接口配置
     * 使用@PreAuthorize("isAuthenticated()")确保用户已认证
     *
     * @param userIds 用户ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Map<String, Integer>> batchDeleteUsers(
            @RequestBody List<Long> userIds,
            HttpServletRequest request) {
        log.info("批量删除用户: userIds={}", userIds);
        
        try {
            // TODO: 从JWT token中获取操作人ID
            Long deleteBy = 1L;
            
            int count = userService.batchDeleteUsers(userIds, deleteBy);
            
            Map<String, Integer> result = new HashMap<>();
            result.put("count", count);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("批量删除用户失败", e);
            return ApiResponse.error(500, "批量删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户名是否存在
     * GET /api/users/check/username
     * 
     * 遵循：安全规范-公开访问接口配置
     * 使用@PermitAll注解允许匿名访问（用于注册时检查用户名）
     *
     * @param username 用户名
     * @return 检查结果
     */
    @GetMapping("/check/username")
    public ApiResponse<Map<String, Boolean>> checkUsername(
            @RequestParam String username) {
        
        boolean exists = userService.existsByUsername(username);
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        
        return ApiResponse.success(result);
    }

    /**
     * 检查邮箱是否存在
     * GET /api/users/check/email
     * 
     * 遵循：安全规范-公开访问接口配置
     * 使用@PermitAll注解允许匿名访问（用于注册时检查邮箱）
     *
     * @param email        邮箱
     * @param excludeUserId 排除的用户ID（可选）
     * @return 检查结果
     */
    @GetMapping("/check/email")
    public ApiResponse<Map<String, Boolean>> checkEmail(
            @RequestParam String email,
            @RequestParam(required = false) Long excludeUserId) {
        
        boolean exists = userService.existsByEmail(email, excludeUserId);
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        
        return ApiResponse.success(result);
    }

    /**
     * 检查手机号是否存在
     * GET /api/users/check/phone
     * 
     * 遵循：安全规范-公开访问接口配置
     * 使用@PermitAll注解允许匿名访问（用于注册时检查手机号）
     *
     * @param phone        手机号
     * @param excludeUserId 排除的用户ID（可选）
     * @return 检查结果
     */
    @GetMapping("/check/phone")
    public ApiResponse<Map<String, Boolean>> checkPhone(
            @RequestParam String phone,
            @RequestParam(required = false) Long excludeUserId) {
        
        boolean exists = userService.existsByPhone(phone, excludeUserId);
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        
        return ApiResponse.success(result);
    }

    /**
     * 将SysUser实体转换为UserVO
     * 隐藏敏感信息，转换状态枚举
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToUserVO(SysUser user) {
        if (user == null) {
            return null;
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        // 转换状态枚举
        if (user.getStatus() != null) {
            userVO.setStatus(user.getStatus().ordinal());
        }
        
        return userVO;
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果是多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
