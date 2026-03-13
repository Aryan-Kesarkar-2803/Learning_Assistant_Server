package com.example.learning_assistant.model.io.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private String message;
    private int status;
    private T data;

    public ApiResponse(int status, String message, T data){
        this.message = message;
        this.status = status;
        this.data =  data;
    }
}
