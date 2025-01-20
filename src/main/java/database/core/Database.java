package database.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String USER = System.getenv("DB_USER_NAME");
    private static final String PASSWORD = System.getenv("DB_USER_PASSWORD");

    private static final String URL = String.format("jdbc:mysql://%s:%s/%s?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
            System.getenv().getOrDefault("DB_HOST", "localhost"),
            System.getenv().getOrDefault("DB_PORT", "3306"),
            System.getenv("DB_DATABASE")
    );

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
