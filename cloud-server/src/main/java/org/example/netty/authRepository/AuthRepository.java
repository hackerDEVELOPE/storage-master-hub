package org.example.netty.authRepository;

public interface AuthRepository {
    boolean saveUser (String login, String password);
}
