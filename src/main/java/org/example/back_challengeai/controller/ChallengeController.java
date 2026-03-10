package org.example.back_challengeai.controller;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.dto.ChallengeResponse;
import org.example.back_challengeai.dto.StreakResponse;
import org.example.back_challengeai.entity.DailyChallenge;
import org.example.back_challengeai.mapper.ChallengeMapper;
import org.example.back_challengeai.service.ChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;


    @GetMapping("/today")
    public ResponseEntity<List<ChallengeResponse>> getTodayChallenges(@RequestParam UUID userId) {
        List<DailyChallenge> challenges = challengeService.getTodayChallenges(userId);
        List<ChallengeResponse> response = ChallengeMapper.toResponseList(challenges);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate")
    public ResponseEntity<List<ChallengeResponse>> generateChallenges(@RequestParam UUID userId) {
        List<DailyChallenge> challenges = challengeService.generateDailyChallenges(userId);
        List<ChallengeResponse> response = ChallengeMapper.toResponseList(challenges);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ChallengeResponse> completeChallenge(@PathVariable UUID id) {
        DailyChallenge challenge = challengeService.completeChallenge(id);
        ChallengeResponse response = ChallengeMapper.toResponse(challenge);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/skip")
    public ResponseEntity<ChallengeResponse> skipChallenge(@PathVariable UUID id) {
        DailyChallenge challenge = challengeService.skipChallenge(id);
        ChallengeResponse response = ChallengeMapper.toResponse(challenge);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/streak")
    public ResponseEntity<StreakResponse> getStreak(@RequestParam UUID userId) {
        int streak = challengeService.calculateStreak(userId);
        StreakResponse response = new StreakResponse(streak);
        return ResponseEntity.ok(response);
    }
}