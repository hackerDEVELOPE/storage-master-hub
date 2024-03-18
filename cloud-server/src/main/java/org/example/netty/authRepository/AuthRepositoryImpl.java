package org.example.netty.authRepository;

import org.example.netty.JDBC;

import java.sql.SQLException;

public class AuthRepositoryImpl implements AuthRepository{
    @Override
    public boolean saveUser(String login, String password) {
        try {
            JDBC.psRegistration.setString(1, login);
            JDBC.psRegistration.setString(2, password);
            JDBC.psRegistration.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
