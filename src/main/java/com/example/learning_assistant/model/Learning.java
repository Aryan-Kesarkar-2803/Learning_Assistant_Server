package com.example.learning_assistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "learnings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Learning {
    @Id
    private String id;
    private String userId;
    private String topic;
    private ArrayList<Step> roadmap;
}
