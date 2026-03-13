package com.example.learning_assistant.security.service;

import com.example.learning_assistant.model.User;
import com.example.learning_assistant.repository.UserRepo;
import com.example.learning_assistant.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo){
        this.userRepo = userRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not Found");
        }
        return new CustomUserDetails(user);
    }
}
