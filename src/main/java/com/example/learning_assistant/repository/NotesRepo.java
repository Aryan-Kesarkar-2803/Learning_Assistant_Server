package com.example.learning_assistant.repository;

import com.example.learning_assistant.model.Notes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotesRepo extends MongoRepository<Notes, String> {
    Optional<Notes> findById(String id);

}
