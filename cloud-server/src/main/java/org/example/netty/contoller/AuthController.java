package org.example.netty.contoller;


public interface AuthController {
    boolean regUser(String login, String password);
    boolean authenticate(String login, String password);
}
