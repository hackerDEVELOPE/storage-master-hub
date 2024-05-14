package org.example;

import lombok.Data;

@Data
public class AuthRequest implements CloudMessage{
    private final String login;
    private final String password;
}
