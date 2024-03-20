package org.example;

import lombok.Data;

@Data
public class DeleteRequest implements CloudMessage{
    private final String name;
}
