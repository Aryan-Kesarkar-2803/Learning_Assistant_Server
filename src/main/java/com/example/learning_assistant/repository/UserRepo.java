package com.example.learning_assistant.repository;

import com.example.learning_assistant.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<User, String> {
    User findByEmail(String email);
}
