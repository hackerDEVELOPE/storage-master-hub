package org.example.netty;
import java.sql.*;

public class JDBC {
    private static Connection connection;
    public static Statement stmt;
    public static PreparedStatement psRegistration;

    public static void prepareStatements() {
        try {
            psRegistration = connection.prepareStatement("INSERT INTO users (login, password) VALUES (?, ?)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:cloud-server/src/main/java/org/example/netty/cloudStorage.db");
            stmt = connection.createStatement();
            prepareStatements();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void disconnect(){
        try {
            stmt.close();
            psRegistration.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}