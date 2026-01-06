package com.haocai.management.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 
 * 功能说明：
 * 1. 标记需要记录操作日志的方法
 * 2. 记录操作模块和操作类型
 * 3. 通过AOP切面实现日志记录
 * 
 * 遵循规范：
 * - 日志规范：记录关键操作日志
 * - AOP规范：使用切面实现横切关注点
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @PostMapping
 * @Log(module = "用户管理", operation = "创建用户")
 * public ApiResponse<User> createUser(@RequestBody User user) {
 *     // 业务逻辑
 * }
 * }
 * </pre>
 * 
 * @author 开发团队
 * @since 2026-01-06
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    
    /**
     * 操作模块
     * 
     * @return 模块名称
     */
    String module() default "";
    
    /**
     * 操作类型
     * 
     * @return 操作类型
     */
    String operation() default "";
    
    /**
     * 操作描述
     * 
     * @return 描述信息
     */
    String description() default "";
}
