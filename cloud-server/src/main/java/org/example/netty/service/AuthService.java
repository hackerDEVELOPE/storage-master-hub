package org.example.netty.service;

public interface AuthService {
    boolean registration(String login, String password);

    boolean authenticate(String login, String password);
}
