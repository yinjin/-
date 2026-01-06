package com.haocai.management.controller;

import com.haocai.management.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 * 用于验证应用是否能正常启动和响应
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 简单的测试接口
     * @return 测试响应
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World! Application is running.";
    }

    /**
     * 用户管理测试接口
     * @return 用户管理模块状态
     */
    @GetMapping("/users/status")
    public String getUserStatus() {
        return "User management module is ready for testing.";
    }

    /**
     * 健康检查接口
     * 用于验证后端服务是否正常运行
     * @return 健康状态
     */
    @GetMapping("/health")
    public ApiResponse<Void> health() {
        return ApiResponse.success("Backend service is running normally");
    }
}
