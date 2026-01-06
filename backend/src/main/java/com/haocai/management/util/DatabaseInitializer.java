package com.haocai.management.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * 数据库初始化工具类
 * 用于在应用启动时自动执行SQL脚本
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private boolean isInitialized = false;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已初始化
        if (isInitialized) {
            return;
        }

        try {
            // 检查sys_user表是否存在
            Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'haocai_management' AND table_name = 'sys_user'",
                Integer.class
            );
            
            if (tableCount == null || tableCount == 0) {
                System.out.println("sys_user表不存在，开始创建表结构...");
            } else {
                // 检查admin用户是否已存在
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sys_user WHERE username = ?", 
                    Integer.class, 
                    "admin"
                );

                if (count != null && count > 0) {
                    System.out.println("数据库已初始化，admin用户已存在，数量: " + count);
                    isInitialized = true;
                    return;
                } else {
                    System.out.println("sys_user表存在，但没有admin用户，开始初始化数据...");
                }
            }

            // 执行SQL初始化脚本
            ClassPathResource resource = new ClassPathResource("init.sql");
            if (resource.exists()) {
                System.out.println("开始执行数据库初始化脚本...");
                
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                    
                    StringBuilder sqlBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 跳过注释和空行
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("--") || line.startsWith("#")) {
                            continue;
                        }
                        
                        sqlBuilder.append(line);
                        
                        // 检查是否遇到分号（语句结束）
                        if (line.endsWith(";")) {
                            String sql = sqlBuilder.toString();
                            // 移除末尾的分号
                            if (sql.endsWith(";")) {
                                sql = sql.substring(0, sql.length() - 1);
                            }
                            
                            try {
                                if (!sql.trim().isEmpty()) {
                                    jdbcTemplate.execute(sql);
                                }
                            } catch (Exception e) {
                                System.err.println("执行SQL失败: " + sql);
                                System.err.println("错误信息: " + e.getMessage());
                            }
                            
                            sqlBuilder = new StringBuilder();
                        }
                    }
                }

                System.out.println("数据库初始化完成");
                isInitialized = true;
            } else {
                System.err.println("未找到init.sql文件");
            }
        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
