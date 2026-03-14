package com.example.learning_assistant.model.io.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String fullName;
    private String phoneNo; // otp verification
    private String gender;
    private ImageData profileImageData;
    private String email;
    private String id;
}
