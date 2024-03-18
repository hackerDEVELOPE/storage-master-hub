package org.example.netty.authService;

import org.example.netty.authRepository.AuthRepository;
import org.example.netty.authRepository.AuthRepositoryImpl;

public class AuthServiceImpl implements AuthService{
    private final AuthRepository authRepository;

    public AuthServiceImpl() {
        this.authRepository = new AuthRepositoryImpl();
    }

    @Override
    public boolean registration(String login, String password) {
        return authRepository.saveUser(login, password);
    }
}
