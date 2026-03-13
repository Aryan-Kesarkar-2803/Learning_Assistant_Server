package com.example.learning_assistant.security.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {
    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    @Bean
    public MongoClient mongoClients() {
        String mongoDbUri = dotenv.get("MONGO_DB_URI");
        if(mongoDbUri == null){
            mongoDbUri = System.getenv("MONGO_DB_URI");
        }
        return MongoClients.create(mongoDbUri);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        String dbName = dotenv.get("DB_NAME");
        if(dbName == null){
            dbName = System.getenv("DB_NAME");
        }
        return new MongoTemplate(mongoClients(), dbName);
    }
}
