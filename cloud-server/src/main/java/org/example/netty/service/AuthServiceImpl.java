package org.example.netty.service;

import org.example.netty.repository.AuthRepository;
import org.example.netty.repository.AuthRepositoryImpl;

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
