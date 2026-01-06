package com.haocai.management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI配置类
 * 用于生成API文档
 * 
 * 遵循开发规范：
 * - 配置类规范-使用@Configuration注解
 * - API文档规范-使用OpenAPI 3规范
 * - 安全配置规范-配置JWT认证
 * 
 * @author haocai
 * @since 1.0.0
 */
@Configuration
public class SwaggerConfig {
    
    /**
     * 配置OpenAPI文档
     * 
     * @return OpenAPI配置对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 安全方案名称
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                // 添加JWT认证安全方案
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("请输入JWT Token，格式：Bearer {token}")))
                // 配置API基本信息
                .info(new Info()
                        .title("高职人工智能学院实训耗材管理系统API")
                        .description("提供用户管理、角色管理、权限管理等功能的RESTful API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("haocai")
                                .email("haocai@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
