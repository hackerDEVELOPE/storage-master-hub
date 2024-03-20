package org.example;

import lombok.Data;

@Data
public class RegistrationRequest implements CloudMessage{
    private final String login;
    private final String password;
}
