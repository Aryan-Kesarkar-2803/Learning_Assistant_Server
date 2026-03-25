package com.example.learning_assistant.controller;

import com.example.learning_assistant.model.Learning;
import com.example.learning_assistant.service.LearningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningController {
    private LearningService learningService;

    public LearningController(LearningService learningService){
        this.learningService = learningService;
    }

    @GetMapping("get-users-learning")
    public ResponseEntity<Object> getUsersLearnings(@RequestParam String userId){
        return learningService.getUsersLearning(userId);
    }

    @GetMapping("get-learning")
    public ResponseEntity<Object> getLearningById(@RequestParam String id){
        return learningService.getLearningById(id);
    }

}
