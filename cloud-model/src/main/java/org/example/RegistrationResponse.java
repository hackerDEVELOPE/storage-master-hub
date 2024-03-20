package org.example;

import lombok.Data;

@Data
public class RegistrationResponse implements CloudMessage{
    public RegistrationResponse(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    boolean isRegistered;
}
