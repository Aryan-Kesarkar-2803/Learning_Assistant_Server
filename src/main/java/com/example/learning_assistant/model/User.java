package com.example.learning_assistant.model;

import com.example.learning_assistant.model.io.user.ImageData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String email; // email verification
    private String password;
    private String role;

    // Extra Properties
    private String fullName;
    private String phoneNo; // otp verification
    private String gender;
    private ImageData profileImageData;
}
