package se.ifmo.ru.lab1.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtResponse {
    private final String token;
}