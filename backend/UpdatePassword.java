import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class UpdatePassword {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/haocai_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String password = "root";

        String newPassword = "$2a$10$93WT6tLrIW34Mw/mtfT.FOcJQlxWN1Ou5WfY5TAAW8Ch2nZBtzCea";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            String sql = "UPDATE sys_user SET password = '" + newPassword + "' WHERE username = 'admin'";
            int rows = stmt.executeUpdate(sql);
            System.out.println("Updated " + rows + " rows");
        }
    }
}
