package com.example.learning_assistant.model.io.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoStats{
    long viewCount;
    long likeCount;
}