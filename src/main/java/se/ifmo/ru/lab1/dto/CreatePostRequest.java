package se.ifmo.ru.lab1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {
    
    @NotBlank
    @Size(min = 3, max = 200)
    private String title;
    
    @NotBlank
    @Size(min = 10, max = 5000)
    private String content;
}