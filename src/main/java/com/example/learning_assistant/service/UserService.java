package com.example.learning_assistant.service;

import com.example.learning_assistant.model.User;
import com.example.learning_assistant.model.io.user.ApiResponse;
import com.example.learning_assistant.repository.UserRepo;
import com.example.learning_assistant.security.config.jwt.JwtService;
import com.example.learning_assistant.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserService {

    private UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    public UserService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

    }


    public ResponseEntity<Object> registerUser(String email, String password) {

        if(email == null || email.isEmpty() || !Utils.isValidEmail(email)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please enter valid email");
        }
        if(!Utils.isStrongPassword(password)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please enter strong password");
        }

        email = email.toLowerCase();

        if (userRepo.findByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User Already Exist");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepo.save(user);

        if(savedUser == null){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error Saving User");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201,"User registered successfully",null));
    }

    public ResponseEntity<Object> loginUser(String email, String password) {

        if(email == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide email");
        }
        if(password == null || password == ""){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide password");
        }

        email = email.toLowerCase();

        User savedUser = userRepo.findByEmail(email);
        if(savedUser == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        Authentication authentication = null;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password");
        }

        Map<String,Object> responseData = new HashMap<>();
        responseData.put("role","user");
        responseData.put("token", jwtService.generateToken(email, "user"));
        responseData.put("id", savedUser.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(HttpStatus.OK.value(),"Login Successful",responseData));

    }
}
