package org.example;

import lombok.Data;

@Data
public class ChangePathOnServerRequest implements CloudMessage{
    String name;

    public ChangePathOnServerRequest(String name) {
        this.name = name;
    }
}
