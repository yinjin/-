package com.material.system.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private final DataSource dataSource;
    private final Environment env;

    public DatabaseInitializer(DataSource dataSource, Environment env) {
        this.dataSource = dataSource;
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Read SQL files
        String schemaSql = readResourceAsString("db/schema.sql");
        String initSql = readResourceAsString("db/init.sql");

        boolean isH2 = false;
        try (Connection conn = dataSource.getConnection()) {
            String product = conn.getMetaData().getDatabaseProductName();
            if (product != null && product.toLowerCase().contains("h2")) {
                isH2 = true;
            }
        }
        // Fallback: check configured datasource URL for H2 (helps in some test setups)
        try {
            String url = env.getProperty("spring.datasource.url");
            if (!isH2 && url != null && url.toLowerCase().contains("jdbc:h2")) {
                isH2 = true;
            }
        } catch (Exception ignored) {
        }

        // If H2, strip statements unsupported by H2 (e.g. CREATE DATABASE, USE)
        if (isH2) {
            schemaSql = stripUnsupportedForH2(schemaSql);
            initSql = stripUnsupportedForH2(initSql);
            // If init script also contains CREATE TABLE statements (duplicate), remove them and keep only data inserts
            initSql = initSql.replaceAll("(?is)create\\s+table.*?;", "");
            // Remove INSERTs from init.sql that are already present in schema.sql to avoid duplicate data insertion
            initSql = removeDuplicateInserts(schemaSql, initSql);
            // Ensure we don't pre-insert admin/user data in H2 tests - let tests create users via service
            schemaSql = schemaSql.replaceAll("(?is)insert\\s+into\\s+sys_user[^;]*;", "");
            initSql = initSql.replaceAll("(?is)insert\\s+into\\s+sys_user[^;]*;", "");
        }

        // For all databases (including MySQL in tests), remove pre-inserted admin user to let tests create it
        schemaSql = schemaSql.replaceAll("(?is)insert\\s+into\\s+sys_user[^;]*;", "");
        initSql = initSql.replaceAll("(?is)insert\\s+into\\s+sys_user[^;]*;", "");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        if (schemaSql != null && !schemaSql.trim().isEmpty()) {
            populator.addScript(new ByteArrayResource(schemaSql.getBytes(StandardCharsets.UTF_8)));
        }
        if (initSql != null && !initSql.trim().isEmpty()) {
            populator.addScript(new ByteArrayResource(initSql.getBytes(StandardCharsets.UTF_8)));
        }
        populator.setContinueOnError(false);
        DatabasePopulatorUtils.execute(populator, dataSource);
    }

    private String readResourceAsString(String path) throws Exception {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String stripUnsupportedForH2(String sql) {
        // Remove SQL single-line comments (--) and block comments (/* */) to simplify parsing
        String noComments = sql.replaceAll("(?m)^\\s*--.*$", "");
        noComments = noComments.replaceAll("(?s)/\\*.*?\\*/", "");

        StringBuilder result = new StringBuilder();
        // Split statements by semicolon so we preserve statement boundaries
        String[] statements = noComments.split(";\\s*\r?\n|;\\s*");
        for (String stmt : statements) {
            if (stmt == null) continue;
            String s = stmt.trim();
            if (s.isEmpty()) continue;
            String lower = s.toLowerCase();
            // Skip DB-level statements
            if (lower.startsWith("create database") || lower.startsWith("use ") || lower.startsWith("delimiter ")) {
                continue;
            }

            if (lower.startsWith("create table")) {
                // Remove inline index/unique key definitions to avoid global name conflicts in H2
                // Remove patterns like: INDEX idx_name (col),  UNIQUE KEY uk_name (col), UNIQUE INDEX ...
                s = s.replaceAll("(?i)\\s*(index|unique key|unique index)\\s+[^\\(]*\\([^\\)]*\\)\\s*,?", "");
                // Remove column/table comments: COMMENT '...'
                s = s.replaceAll("(?i)\\s*comment\\s+'[^']*'", "");
                // Remove ENGINE/CHARSET/COLLATE and everything after closing parenthesis
                int lastClose = s.lastIndexOf(')');
                if (lastClose >= 0) {
                    s = s.substring(0, lastClose + 1) + ";";
                } else {
                    s = s + ";";
                }
                // Remove trailing commas before closing parenthesis
                s = s.replaceAll(",\\s*\\)", ")");
            } else {
                // non-create-table statements (INSERT/ALTER/etc) - just append semicolon
                if (!s.endsWith(";")) s = s + ";";
            }

            result.append(s).append("\n");
        }

        return result.toString();
    }

    private String removeDuplicateInserts(String schemaSql, String initSql) {
        // collect INSERT statements from schemaSql
        java.util.Set<String> insertsInSchema = new java.util.HashSet<>();
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("(?is)insert\\s+into\\s+[^;]+;");
        java.util.regex.Matcher m = p.matcher(schemaSql);
        while (m.find()) {
            String ins = normalizeSql(m.group());
            insertsInSchema.add(ins);
        }

        // remove matching inserts from initSql
        StringBuffer sb = new StringBuffer();
        m = p.matcher(initSql);
        int lastEnd = 0;
        while (m.find()) {
            String full = m.group();
            String norm = normalizeSql(full);
            if (insertsInSchema.contains(norm)) {
                // skip this insert: append content before it
                sb.append(initSql, lastEnd, m.start());
                lastEnd = m.end();
            }
        }
        if (lastEnd == 0) {
            return initSql;
        }
        // append the tail
        sb.append(initSql.substring(lastEnd));
        return sb.toString();
    }

    private String normalizeSql(String s) {
        // collapse whitespace, remove trailing semicolon, lowercase for comparison
        String t = s.replaceAll("/\\*.*?\\*/", "").replaceAll("(?m)^\\s*--.*$", "");
        t = t.replaceAll("\\s+", " ").trim();
        if (t.endsWith(";")) t = t.substring(0, t.length() - 1);
        return t.toLowerCase();
    }
}
