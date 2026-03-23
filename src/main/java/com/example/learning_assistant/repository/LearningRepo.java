package com.example.learning_assistant.repository;

import com.example.learning_assistant.model.Learning;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningRepo extends MongoRepository<Learning, String> {

}
