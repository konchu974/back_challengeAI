package org.example.back_challengeai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private UUID id;
    private String email;
    private LocalDateTime createdAt;
    private String token;  // Pour JWT plus tard
}