package org.example.back_challengeai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.back_challengeai.entity.ChallengeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class ChallengeResponse {
    private UUID id;
    private String challengeText;
    private LocalDate challengeDate;
    private ChallengeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}