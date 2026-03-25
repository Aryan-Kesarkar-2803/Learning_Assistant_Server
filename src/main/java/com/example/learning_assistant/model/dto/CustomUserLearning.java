package com.example.learning_assistant.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserLearning {
    private String id;
    private String topic;
    private Boolean isCompleted;
    private Boolean isStarted;
    private String progress;
}
