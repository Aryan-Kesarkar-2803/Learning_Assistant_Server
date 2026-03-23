package com.example.learning_assistant.repository;

import com.example.learning_assistant.model.Learning;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningRepo extends MongoRepository<Learning, String> {
    public List<Learning> findByUserId(String userId) ;
}
