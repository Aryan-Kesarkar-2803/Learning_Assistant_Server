package com.example.learning_assistant.controller;

import com.example.learning_assistant.model.Learning;
import com.example.learning_assistant.service.LearningService;
import com.example.learning_assistant.service.StarterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/get-started")
public class StarterController {

    private StarterService starterService;
    private LearningService learningService;

    public StarterController(StarterService starterService, LearningService learningService){
        this.starterService = starterService;
        this.learningService = learningService;
    }

    @GetMapping("/generateRoadmap")
    public ResponseEntity<Object> generateRoadMap(@RequestParam String topic){
        if(topic == null || topic.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please provide topic to learn");
        }
        return starterService.generateRoadMap(topic);
    }

    @PostMapping("/save-roadmap")
    public ResponseEntity<Object> saveRoadmap(@RequestBody Learning learning){
        return learningService.saveRoadmap(learning);
    }
}
