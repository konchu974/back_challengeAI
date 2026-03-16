package org.example.back_challengeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    private int currentStreak;
    private int weekCompleted;
    private int weekTotal;
    private int monthCompleted;
    private int monthTotal;
    private int totalCompleted;
    private double successRate;
    private int longestStreak;
    private int totalSkipped;
}