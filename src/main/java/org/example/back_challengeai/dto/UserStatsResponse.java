package org.example.back_challengeai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserStatsResponse {
    private int currentStreak;
    private long totalCompleted;
}
