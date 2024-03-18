package org.example.netty.authContoller;

import org.example.netty.authService.AuthService;
import org.example.netty.authService.AuthServiceImpl;

public class AuthControllerImpl implements AuthController{
    private final AuthService authService;

    public AuthControllerImpl() {
        this.authService = new AuthServiceImpl();
    }

    @Override
    public boolean regUser(String login, String password) {
        return authService.registration(login, password);
    }
}
