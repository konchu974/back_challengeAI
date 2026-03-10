package org.example.back_challengeai.mapper;

import org.example.back_challengeai.dto.ChallengeResponse;
import org.example.back_challengeai.entity.DailyChallenge;

import java.util.List;
import java.util.stream.Collectors;

public class ChallengeMapper {

    public static ChallengeResponse toResponse(DailyChallenge challenge) {
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .challengeDate(challenge.getChallengeDate())
                .challengeText(challenge.getChallengeText())
                .status(challenge.getStatus())
                .createdAt(challenge.getCreatedAt())
                .completedAt(challenge.getCompletedAt())
                .build();
    }

    public static List<ChallengeResponse> toResponseList(List<DailyChallenge> challenges) {
        return challenges.stream()
                .map(ChallengeMapper::toResponse)
                .collect(Collectors.toList());
    }
}