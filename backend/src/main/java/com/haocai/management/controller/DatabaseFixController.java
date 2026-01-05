package com.haocai.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;

@RestController
@RequestMapping("/api/fix")
public class DatabaseFixController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/table")
    public String fixTable() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 先删除real_name字段
            String dropSql = "ALTER TABLE sys_user DROP COLUMN real_name";
            stmt.execute(dropSql);
            
            return "数据库表结构修复成功！real_name字段已删除";
        } catch (Exception e) {
            return "修复失败: " + e.getMessage();
        }
    }
}
