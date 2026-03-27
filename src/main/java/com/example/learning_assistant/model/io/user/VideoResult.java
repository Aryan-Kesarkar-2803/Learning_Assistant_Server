package com.example.learning_assistant.model.io.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoResult {
    double sentimenScore=0;
    String videoId;
    String videoLink;
    double overallScore = 0;
}
