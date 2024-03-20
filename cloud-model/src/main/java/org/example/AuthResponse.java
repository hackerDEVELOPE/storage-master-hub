package org.example;

import lombok.Data;

@Data
public class AuthResponse implements CloudMessage{
    boolean isAuthenticated;

    public AuthResponse(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }
}
