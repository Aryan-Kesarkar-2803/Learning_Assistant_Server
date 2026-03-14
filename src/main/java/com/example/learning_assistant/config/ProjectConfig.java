package com.example.learning_assistant.config;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProjectConfig {

    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    @Bean
    public Cloudinary getCloudinary(){
        String cloudName = dotenv.get("CLOUD_NAME");
        String apiKey = dotenv.get("CLOUD_API_KEY");
        String apiSecret = dotenv.get("CLOUD_API_SECRET");
        if(cloudName == null){
            cloudName = System.getenv("CLOUD_NAME");
        }
        if(apiKey == null){
            apiKey = System.getenv("CLOUD_API_KEY");
        }
        if(apiSecret == null){
            apiSecret = System.getenv("CLOUD_API_SECRET");
        }

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name",cloudName);
        config.put("api_key",apiKey);
        config.put("api_secret",apiSecret);
        config.put("secret",true);
        return new Cloudinary(config);
    }
}
