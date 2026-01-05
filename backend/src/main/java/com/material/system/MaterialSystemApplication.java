package com.material.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Material Management System Backend Application
 * Main entry point for the Spring Boot application
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MaterialSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaterialSystemApplication.class, args);
        System.out.println("========================================");
        System.out.println("Material System Backend Started Successfully!");
        System.out.println("Access the application at: http://localhost:8080");
        System.out.println("========================================");
    }
}
