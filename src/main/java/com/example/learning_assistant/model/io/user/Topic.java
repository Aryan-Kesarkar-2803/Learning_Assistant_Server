package com.example.learning_assistant.model.io.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic {
    private String topicName;
    private String videoLink;
    private String docLink;
    private Boolean isCompleted;
}
