package com.example.learning_assistant.model.io.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {
    private String email;
    private String fullName;
    private String phoneNo; // otp verification
    private String gender;
    private ImageData profileImageData;
}
