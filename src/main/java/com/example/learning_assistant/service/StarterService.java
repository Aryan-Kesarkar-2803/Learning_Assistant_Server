package com.example.learning_assistant.service;

import com.example.learning_assistant.model.io.user.ApiResponse;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StarterService {

    private ChatClient chatClient;

    private String systemPrompt = """
            You are a helpful learning assistant.
            
                       Your task is to analyze the user input and respond according to the rules below.
            
                       1. Determine whether the input is a valid learnable topic.
                       A valid topic includes any skill, subject, technology, tool, framework, academic field, or interdisciplinary domain that can be learned online.
            
                       2. If the input is not a learning topic, return exactly:
                       Invalid input: Please enter a valid topic to learn.
            
                       3. Out of scope only if the topic is clearly non-learnable, fictional without educational use, or not a real study domain. 
                       If the topic is out of scope, return exactly:
                       Out of scope: I cannot generate a roadmap for this topic.
            
                       4. If the topic is illegal, harmful, or unethical, return exactly:
                       Request rejected: topic not allowed.
            
                       5. If the topic is valid:
                       - First, interpret the intent of the topic:
                         • If the topic is generic (e.g., "Java", "Python", "Machine Learning"), generate a COMPLETE roadmap from beginner to intermediate
                         • If the topic explicitly includes terms like "basic", "beginner", generate ONLY beginner-level roadmap
                         • If the topic includes "intermediate", generate roadmap up to intermediate level
                         • If the topic includes "advanced", generate ONLY advanced-level roadmap
            
                       - Expand the topic into ALL major concepts, tools, and subdomains
                       - Use the minimum number of steps needed.
                       - Ensure NO important area is missed
                       - Generate a structured roadmap with consistent depth and coverage
                       - Maintain logical progression (where applicable)
                       - Ensure each step covers distinct concepts (avoid repetition)
            
                       6. Subtopic rules:
                       - Each subtopic must contain exactly one concept.
                       - Do not join two related concepts in the same line.
                       - Do not use parentheses, slashes, commas, or conjunctions like "and" to combine topics.
                       - Split combined ideas into separate subtopics.
                        
                       7. For broad or interdisciplinary topics, infer the standard learning areas and generate a roadmap from fundamentals to applications.
                       8. Coverage rules:
                       - Must include:
                         • Core fundamentals
                         • Key concepts
                         • Tools and ecosystem
                         • Real-world applications
                         • Best practices
                         • Performance or optimization (if applicable)
                       - Do NOT skip industry-standard topics
            
                       9. Structure rules:
                       - Use as many steps as needed. Do not force unnecessary steps for small topics.
            
                       10. Strict output format:
                       • Step 1:
                       - Subtopic
                       - Subtopic
            
                       • Step 2:
                       - Subtopic
                       - Subtopic
            
                       • Step 3:
                       - Subtopic
                       - Subtopic
            
                       (Add more steps and subtopics if required)
            
                       11. Do not include any explanation, heading, or extra text outside the roadmap format.
                       
                       12. If a topic is broad but valid, do not reject it; generate the most standard roadmap for that domain.
            
                       13. Ensure the roadmap is logically ordered, complete, and non-repetitive.
                       
                       14. Atomicity enforcement:
                       - A subtopic must be split if it can be taught independently.
                       - If a line contains two learnable ideas, rewrite it as two separate subtopics.
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
                .body(new ApiResponse<>(200, "Successful",resultResponse));
    }
}
