package com.example.learning_assistant.controller;

import com.example.learning_assistant.model.User;
import com.example.learning_assistant.model.io.user.UserLoginRequest;
import com.example.learning_assistant.model.io.user.UserRegisterRequest;
import com.example.learning_assistant.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;

    }

    @PostMapping(value = "/register")
    public ResponseEntity<Object> registerUser(@RequestBody UserRegisterRequest user){
        return userService.registerUser(user.getEmail(), user.getPassword());
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody UserLoginRequest user){
        return userService.loginUser(user.getEmail(), user.getPassword());
    }


    @GetMapping("/hello")
    public String helloController(){
        return "Hello World - Authentication passed";
    }

}
