package com.haocai.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * 数据库字符集配置
 * 确保每次数据库连接都使用utf8mb4字符集
 */
@Configuration
public class DatabaseCharsetConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        // 在连接池初始化时设置字符集
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            
            // 设置所有字符集相关的变量为utf8mb4
            statement.execute("SET NAMES utf8mb4");
            statement.execute("SET CHARACTER SET utf8mb4");
            statement.execute("SET character_set_client=utf8mb4");
            statement.execute("SET character_set_connection=utf8mb4");
            statement.execute("SET character_set_results=utf8mb4");
            statement.execute("SET collation_connection=utf8mb4_unicode_ci");
            
            statement.close();
            connection.close();
            
            System.out.println("数据库字符集已设置为utf8mb4");
        } catch (Exception e) {
            System.err.println("设置数据库字符集失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return jdbcTemplate;
    }
}
