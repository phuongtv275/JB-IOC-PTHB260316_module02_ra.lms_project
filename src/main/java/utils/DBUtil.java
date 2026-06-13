package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/lms";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private static Connection connection;

    public DBUtil() {
    }

    public static Connection getConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DBUtil] Kết nối database thành công.");
        } catch (SQLException e) {
            System.err.println("[DBUtil] Lỗi kết nối database: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DBUtil] Đã đóng kết nối database.");
            }
        } catch (SQLException e) {
            System.err.println("[DBUtil] Lỗi khi đóng connection: " + e.getMessage());
        }
    }
}
