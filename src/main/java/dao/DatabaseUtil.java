package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    public static Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:C:\\Code\\Pets\\idea-related\\currency-exchanger\\" +
                            "exchanger-database");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
