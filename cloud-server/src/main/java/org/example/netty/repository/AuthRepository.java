package org.example.netty.repository;

public interface AuthRepository {
    boolean saveUser (String login, String password);

    boolean authenticate(String login, String password);
}
