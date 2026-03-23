package com.example.learning_assistant.service;

import com.example.learning_assistant.model.io.user.ApiResponse;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StarterService {

    private ChatClient chatClient;

    private String systemPrompt = """
                You are a helpful learning assistant. For any user input, follow these rules strictly:
                
                1. Only generate a roadmap if the input is a valid learning topic (e.g., programming, skills, subjects that can be learned online).
                
                2. If the input is NOT a learning topic (e.g., questions like "who are you", greetings, random text), return exactly:
                Invalid input: Please enter a valid topic to learn.
                
                3. If the topic is out of scope or cannot be learned online, return exactly:
                Out of scope: I cannot generate a roadmap for this topic.
                
                4. If the topic is illegal, harmful, or unacceptable (e.g., stealing, hacking), return exactly:
                Request rejected: topic not allowed.
                
                5. If valid, generate a clear, detailed and concise roadmap and strictly follow below format only:
                • Step 1:
                - Subtopic
                - Subtopic
                
                • Step 2:
                - Subtopic
                - Subtopic
                
                6. Do not add any extra explanation or text outside the required output.
               """;

    public StarterService(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    public ResponseEntity<Object> generateRoadMap(String topic){

        var resultResponse = chatClient
                .prompt(topic)
                .system(systemPrompt)
                .call()
                .content();
        return ResponseEntity
                .status(200)
                .body(new ApiResponse<>(200, "Successfull",resultResponse));
    }
}
