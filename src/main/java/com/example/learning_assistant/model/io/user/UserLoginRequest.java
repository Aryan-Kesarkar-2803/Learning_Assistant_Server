package com.example.learning_assistant.model.io.user;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String email;
    private String password;
}
