package com.material.system.annotation;

import java.lang.annotation.*;

/**
 * 自定义权限注解
 * 用于方法级别的权限控制
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 需要的权限标识
     */
    String value();
    
    /**
     * 是否需要所有权限（AND关系），默认为true
     * false表示只需要其中一个权限（OR关系）
     */
    boolean requireAll() default true;
}
