package se.ifmo.ru.lab1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
}