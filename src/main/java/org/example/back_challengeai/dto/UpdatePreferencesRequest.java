package org.example.back_challengeai.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdatePreferencesRequest {
    private List<String> interests;
    private List<String> goals;
    private Integer dailyTime;
}