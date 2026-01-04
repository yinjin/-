package com.material.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * Provides API endpoints to check the health status of the backend service
 */
@RestController
@RequestMapping("/")
public class HealthController {

    /**
     * Health check endpoint
     * @return health status information
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Material System Backend");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        return response;
    }

    /**
     * Welcome endpoint
     * @return welcome message
     */
    @GetMapping("/welcome")
    public Map<String, String> welcome() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Material Management System Backend API");
        response.put("status", "Backend service is running successfully");
        return response;
    }
}
