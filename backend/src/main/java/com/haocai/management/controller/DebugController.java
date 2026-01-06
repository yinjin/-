package com.haocai.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 调试控制器
 * 用于排查数据库问题
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 检查数据库连接和用户数据
     */
    /**
     * 重置admin用户状态（恢复被逻辑删除的admin用户）
     */
    @PostMapping("/reset-admin")
    public String resetAdminUser() {
        StringBuilder result = new StringBuilder();
        
        try {
            // 检查admin用户是否存在（包括已删除的）
            Integer allAdminCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user WHERE username = 'admin'",
                Integer.class
            );
            
            if (allAdminCount == 0) {
                // 不存在，插入新的admin用户
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode("admin123");
                
                int inserted = jdbcTemplate.update(
                    "INSERT INTO sys_user (username, password, name, email, phone, status, deleted, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                    "admin", encodedPassword, "系统管理员", "admin@haocai.com", "13800138000", 0, 0
                );
                
                result.append("成功创建admin用户，影响行数: ").append(inserted).append("\n");
            } else {
                // 存在，更新deleted字段为0，status字段为0（正常状态）
                int updated = jdbcTemplate.update(
                    "UPDATE sys_user SET deleted = 0, status = 0 WHERE username = 'admin'"
                );
                
                result.append("成功重置admin用户状态，影响行数: ").append(updated).append("\n");
            }
            
            // 验证结果
            Integer adminCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user WHERE username = 'admin' AND deleted = 0",
                Integer.class
            );
            result.append("当前admin用户数量（未删除）: ").append(adminCount).append("\n");
            
        } catch (Exception e) {
            result.append("错误: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }
        
        return result.toString();
    }

    /**
     * 重置admin用户密码为admin123
     */
    @PostMapping("/reset-admin-password")
    public String resetAdminPassword() {
        StringBuilder result = new StringBuilder();
        
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode("admin123");
            
            int updated = jdbcTemplate.update(
                "UPDATE sys_user SET password = ? WHERE username = 'admin'",
                encodedPassword
            );
            
            result.append("成功重置admin用户密码，影响行数: ").append(updated).append("\n");
            result.append("新密码哈希: ").append(encodedPassword).append("\n");
            
        } catch (Exception e) {
            result.append("错误: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }
        
        return result.toString();
    }

    /**
     * 检查数据库连接和用户数据
     */
    @GetMapping("/check-db")
    public String checkDatabase() {
        StringBuilder result = new StringBuilder();
        
        try {
            // 检查当前数据库
            String currentDb = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            result.append("当前数据库: ").append(currentDb).append("\n");
            
            // 检查sys_user表是否存在
            Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = 'sys_user'",
                Integer.class,
                currentDb
            );
            result.append("sys_user表存在: ").append(tableCount > 0).append("\n");
            
            if (tableCount > 0) {
                // 检查用户总数
                Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user", Integer.class);
                result.append("sys_user表用户总数: ").append(userCount).append("\n");
                
                // 检查admin用户
                Integer adminCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sys_user WHERE username = 'admin' AND deleted = 0",
                    Integer.class
                );
                result.append("admin用户存在(未删除): ").append(adminCount).append("\n");
                
                // 检查包含deleted的所有admin用户
                Integer allAdminCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sys_user WHERE username = 'admin'",
                    Integer.class
                );
                result.append("admin用户总数(含已删除): ").append(allAdminCount).append("\n");
                
                // 如果有用户，显示详细信息
                if (adminCount > 0) {
                    String userInfo = jdbcTemplate.queryForObject(
                        "SELECT id, username, name, email, status, deleted FROM sys_user WHERE username = 'admin' LIMIT 1",
                        (rs, rowNum) -> {
                            return String.format("ID=%d, username=%s, name=%s, email=%s, status=%s, deleted=%d",
                                rs.getLong("id"),
                                rs.getString("username"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("status"),
                                rs.getInt("deleted")
                            );
                        }
                    );
                    result.append("admin用户信息: ").append(userInfo).append("\n");
                }
            } else {
                result.append("sys_user表不存在\n");
            }
        } catch (Exception e) {
            result.append("错误: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }
        
        return result.toString();
    }
}
