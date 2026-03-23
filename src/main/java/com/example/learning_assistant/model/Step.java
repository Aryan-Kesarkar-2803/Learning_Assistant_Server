package com.example.learning_assistant.model;

import com.example.learning_assistant.model.io.user.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Step {
    private String stepName;
    private ArrayList<Topic> topics;
    private Boolean isCompleted;
}
