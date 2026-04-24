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

    @GetMapping("get-notes")
    public ResponseEntity<Object> getNotesById(@RequestParam String id){
        return learningService.getNotesById(id);
    }

    @GetMapping("generate-notes")
    public ResponseEntity<Object> getNotesForTopic(@RequestParam String topic){
        return learningService.generateNotes(topic);
    }

    @GetMapping("generate-quiz")
    public ResponseEntity<Object> generateQuizForSubtopic(@RequestParam String topic){
        return learningService.generateQuiz(topic);
    }

    @GetMapping("get-youtube-video")
    public ResponseEntity<Object> getYoutubeVideoforTopic(@RequestParam String topic){
        return learningService.getYoutubeVideoforTopic(topic);
    }

    @GetMapping("solve-doubt")
    public ResponseEntity<Object> solveDoubtForTopic(@RequestParam String doubt){
        return learningService.solveDoubtForTopic(doubt);
    }



}
