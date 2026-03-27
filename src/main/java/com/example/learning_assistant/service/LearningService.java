package com.example.learning_assistant.service;

import com.example.learning_assistant.model.Learning;
import com.example.learning_assistant.model.dto.CustomUserLearning;
import com.example.learning_assistant.model.io.user.ApiResponse;
import com.example.learning_assistant.model.io.user.VideoResult;
import com.example.learning_assistant.model.io.user.VideoStats;
import com.example.learning_assistant.repository.LearningRepo;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class LearningService {

    private final Dotenv dotenv =Dotenv.configure().ignoreIfMissing().load();
    private LearningRepo learningRepo;
    private final RestClient restClient = RestClient.builder().build();
    private String youtubeApiKey;

    public LearningService(LearningRepo learningRepo){
        this.learningRepo = learningRepo;

        String apiKey = dotenv.get("YOUTUBE_API_KEY");
        if(apiKey == null){
            apiKey = System.getenv("YOUTUBE_API_KEY");
        }
        this.youtubeApiKey = apiKey;
    }

    public ResponseEntity<Object> saveRoadmap(Learning learning){
        Learning result = null;
        try{
            result = learningRepo.save(learning);
        }catch (Exception e){
            throw new RuntimeException("Unable to save roadmap");
        }

       return ResponseEntity
               .status(HttpStatus.OK)
               .body(
                       ApiResponse.builder()
                               .status(200)
                               .message("Roadmap Saved successfully")
                               .build()
               );


    }

    public ResponseEntity<Object> getUsersLearning(String userId){

        if(userId.isEmpty()){
            throw new RuntimeException("UserId missing");
        }

        List<Learning> response;
        try{
            response = learningRepo.findByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Unable to fetch Learning");
        }

        List<CustomUserLearning> responseToSend;

        responseToSend = response.stream().map(
                item -> CustomUserLearning
                        .builder()
                        .id(item.getId())
                        .topic(item.getTopic())
                        .isCompleted(item.getIsCompleted())
                        .isStarted(item.getRoadmap().getFirst().getTopics().getFirst().getIsCompleted())
                        .build()
                ).toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .status(200)
                                .data(responseToSend)
                                .message("successful")
                                .build()
                );



    }

    public ResponseEntity<Object> getLearningById(String id) {
        if(id == null || id.isEmpty()){
            throw new RuntimeException("Learning id required");
        }

        Optional<Learning> response = learningRepo.findById(id);
        if(response.isEmpty()){
            throw new RuntimeException("Error in finding learning using id");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .data(response)
                                .message("successful")
                                .status(200)
                                .build()
                );
    }

    public ResponseEntity<Object> getYoutubeVideoforTopic(String topic) {
        if(topic == null || topic.isEmpty()){
            throw new RuntimeException("Topic not received");
        }
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("www.googleapis.com")
                        .path("/youtube/v3/search")
                        .queryParam("part", "snippet")
                        .queryParam("q", topic)
                        .queryParam("type", "video")
                        .queryParam("maxResults", 10)
                        .queryParam("key", youtubeApiKey)
                        .build())
                .retrieve()
                .body(String.class);

        List<String> videoIds = extractVideoIds(response);

        ArrayList<VideoResult> results = new ArrayList<>();

        for(String videoId: videoIds){
            double score = getGenericCommentScoreForVideo(videoId);
            if(score <= 0){
                continue;
            }
            results.add(
                    VideoResult.builder()
                            .sentimenScore(score)
                            .videoId(videoId)
                            .videoLink("https://www.youtube.com/watch?v="+videoId)
                            .build());
        }

        VideoResult res = results.getFirst();

        for(VideoResult r: results){
            VideoStats temp = extractMetadataVideo(r.getVideoId());

            double likesRatio = (double) temp.getLikeCount()/temp.getViewCount();
            double overallScore = (0.6 * r.getSentimenScore()) + (0.4 * likesRatio);

            if(overallScore > res.getOverallScore()){
                res = r;
                res.setOverallScore(overallScore);
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse
                                .builder()
                                .data(res)
                                .message("successful")
                                .status(200)
                                .build()
                );
    }

    private List<String> extractVideoIds(String json) {
        List<String> ids = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode items = root.get("items");

            for (JsonNode item : items) {
                String videoId = item.get("id").get("videoId").asString();
                ids.add(videoId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    private VideoStats extractMetadataVideo(String videoId){
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("www.googleapis.com")
                        .path("/youtube/v3/videos")
                        .queryParam("part", "snippet,statistics")
                        .queryParam("id", videoId)
                        .queryParam("key", youtubeApiKey)
                        .build())
                .retrieve()
                .body(String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response);
        JsonNode item = node.get("items").get(0);
        JsonNode stats = item.get("statistics");

        return VideoStats.builder()
                .viewCount(stats.get("viewCount").asLong())
                .likeCount(stats.get("likeCount").asLong())
                .build();

    }

    private List<String> extractVideoComments(String videoId){

        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("www.googleapis.com")
                        .path("/youtube/v3/commentThreads")
                        .queryParam("part", "snippet")
                        .queryParam("videoId", videoId)
                        .queryParam("maxResults", 200)
                        .queryParam("key", youtubeApiKey)
                        .build())
                .retrieve()
                .body(String.class);

        List<String> comments = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode items = root.get("items");

            for (JsonNode item : items) {
                String comment = item
                        .get("snippet")
                        .get("topLevelComment")
                        .get("snippet")
                        .get("textDisplay")
                        .asText();

                comments.add(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

    private double getGenericCommentScoreForVideo(String videoId) {
        String sentimentModelBaseUrl = dotenv.get("SENTIMENT_MODEL_BASE_URL");

        if(sentimentModelBaseUrl == null){
            sentimentModelBaseUrl = System.getenv("SENTIMENT_MODEL_BASE_URL");
        }

        List<String> comments;
        try{
           comments = extractVideoComments(videoId);
        }catch (Exception e){
            return 0;
        }

        if(comments.size() <= 0 || comments.size() < 40){
            return 0;
        }

        Map<String,Object> body = new HashMap<>();
        body.put("text", comments);
        JsonNode res =  restClient
                .post()
                .uri(sentimentModelBaseUrl+"/analyze")
                .body(body)
                        .retrieve()
                .body(JsonNode.class);
        return res.get("result").asDouble();
    }





}
