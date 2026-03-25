package com.example.learning_assistant.service;

import com.example.learning_assistant.model.Learning;
import com.example.learning_assistant.model.dto.CustomUserLearning;
import com.example.learning_assistant.model.io.user.ApiResponse;
import com.example.learning_assistant.repository.LearningRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.management.RuntimeMBeanException;
import java.util.List;
import java.util.Optional;

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

    public ResponseEntity<Object> getUsersLearning(String userId){

        if(userId.isEmpty()){
            throw new RuntimeException("UserId missing");
        }

        List<Learning> response;
        try{
            response = learningRepo.findByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Unable to fetch Learning");
        }

        List<CustomUserLearning> responseToSend;

        responseToSend = response.stream().map(
                item -> CustomUserLearning
                        .builder()
                        .id(item.getId())
                        .topic(item.getTopic())
                        .isCompleted(item.getIsCompleted())
                        .isStarted(item.getRoadmap().getFirst().getTopics().getFirst().getIsCompleted())
                        .build()
                ).toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .status(200)
                                .data(responseToSend)
                                .message("successful")
                                .build()
                );



    }

    public ResponseEntity<Object> getLearningById(String id) {
        if(id == null || id.isEmpty()){
            throw new RuntimeException("Learning id required");
        }

        Optional<Learning> response = learningRepo.findById(id);
        if(response.isEmpty()){
            throw new RuntimeException("Error in finding learning using id");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .data(response)
                                .message("successful")
                                .status(200)
                                .build()
                );
    }
}
