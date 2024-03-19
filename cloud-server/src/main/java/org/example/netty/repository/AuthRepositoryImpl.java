package org.example.netty.repository;

import org.example.netty.JDBC;

import java.sql.ResultSet;
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

    @Override
    public boolean authenticate(String login, String password) {
        try {
            JDBC.psAuthentication.setString(1, login);
            JDBC.psAuthentication.setString(2, password);
            ResultSet rs = JDBC.psAuthentication.executeQuery();
            return rs.getInt(1) != 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
