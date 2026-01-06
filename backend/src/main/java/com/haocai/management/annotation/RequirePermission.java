package com.haocai.management.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义权限注解
 * 用于标记需要特定权限才能访问的方法或类
 * 
 * 遵循开发规范：
 * - 注解设计规范-使用清晰的注解名称和属性
 * - 权限控制规范-使用注解进行方法级权限控制
 * 
 * @author haocai
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    /**
     * 权限编码数组
     * 支持多个权限编码，根据logical属性决定是AND还是OR关系
     * 
     * 示例：
     * - @RequirePermission("user:create") - 需要user:create权限
     * - @RequirePermission({"user:create", "user:update"}) - 根据logical属性决定
     * 
     * @return 权限编码数组
     */
    String[] value();
    
    /**
     * 逻辑关系
     * AND: 需要拥有所有权限
     * OR: 只需要拥有其中一个权限即可
     * 
     * 默认为OR，即只需要拥有其中一个权限
     * 
     * @return 逻辑关系
     */
    Logical logical() default Logical.OR;
    
    /**
     * 权限逻辑枚举
     */
    enum Logical {
        /**
         * AND逻辑：需要拥有所有权限
         */
        AND,
        
        /**
         * OR逻辑：只需要拥有其中一个权限
         */
        OR
    }
}
