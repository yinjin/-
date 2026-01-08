package com.haocai.management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@SpringBootTest
@ActiveProfiles("dev")
public class UpdateAdminPasswordTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void updateAdminPassword() throws Exception {
        String password = "$2a$10$93WT6tLrIW34Mw/mtfT.FOcJQlxWN1Ou5WfY5TAAW8Ch2nZBtzCea";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "UPDATE sys_user SET password = '" + password + "' WHERE username = 'admin'";
            int rows = stmt.executeUpdate(sql);
            System.out.println("Updated " + rows + " rows");

            // Verify
            stmt.execute("SELECT id, username, password, status FROM sys_user WHERE username = 'admin'");
        }
    }
}
