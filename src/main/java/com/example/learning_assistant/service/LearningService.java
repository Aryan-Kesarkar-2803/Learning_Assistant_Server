package com.example.learning_assistant.service;

import com.example.learning_assistant.model.Learning;
import com.example.learning_assistant.model.io.user.ApiResponse;
import com.example.learning_assistant.repository.LearningRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LearningService {

    private LearningRepo learningRepo;

    public LearningService(LearningRepo learningRepo){
        this.learningRepo = learningRepo;
    }

    public ResponseEntity<Object> saveRoadmap(Learning learning){
        Learning result = null;
        try{
            result = learningRepo.save(learning);
        }catch (Exception e){
            throw new RuntimeException("Unable to save roadmap");
        }

       return ResponseEntity
               .status(HttpStatus.OK)
               .body(
                       ApiResponse.builder()
                               .status(200)
                               .message("Roadmap Saved successfully")
                               .build()
               );


    }



}
