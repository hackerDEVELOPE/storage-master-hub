package org.example.netty.contoller;

import org.example.netty.service.AuthService;
import org.example.netty.service.AuthServiceImpl;

public class AuthControllerImpl implements AuthController{
    private final AuthService authService;

    public AuthControllerImpl() {
        this.authService = new AuthServiceImpl();
    }

    @Override
    public boolean regUser(String login, String password) {
        return authService.registration(login, password);
    }

    @Override
    public boolean authenticate(String login, String password) {
        return authService.authenticate(login, password);
    }
}
