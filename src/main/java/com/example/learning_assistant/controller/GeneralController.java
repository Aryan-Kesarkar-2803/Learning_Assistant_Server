package com.example.learning_assistant.controller;

import com.example.learning_assistant.model.io.user.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class GeneralController {
    @GetMapping("/verify-token")
    public ResponseEntity<ApiResponse> home(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK.value(),"SuccessFull", null));
    }
}
