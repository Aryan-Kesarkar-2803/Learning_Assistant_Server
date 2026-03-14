package com.example.learning_assistant.model.io.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageData {
    private String url = "";
    private String publicId = "";
}

