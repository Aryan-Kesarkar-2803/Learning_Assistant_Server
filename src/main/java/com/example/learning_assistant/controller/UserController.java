package com.example.learning_assistant.controller;

import com.example.learning_assistant.model.User;
import com.example.learning_assistant.model.io.user.UserLoginRequest;
import com.example.learning_assistant.model.io.user.UserProfileRequest;
import com.example.learning_assistant.model.io.user.UserRegisterRequest;
import com.example.learning_assistant.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

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

    @PostMapping("/update-profile")
    public ResponseEntity<Object> profileUpdation(@RequestPart(value = "file", required = false) MultipartFile file, @RequestPart("userProfile") String userProfileJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        UserProfileRequest userProfile = mapper.readValue(userProfileJson, UserProfileRequest.class);
        return userService.updateUserProfile(file, userProfile);
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(){
        return userService.getProfile();
    }

}
