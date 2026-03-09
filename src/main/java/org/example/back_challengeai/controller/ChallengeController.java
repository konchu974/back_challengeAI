package org.example.back_challengeai.controller;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.dtos.StreakResponse;
import org.example.back_challengeai.entity.DailyChallenge;
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
    public ResponseEntity<List<DailyChallenge>> getTodayChallenges(@RequestParam UUID userId) {
        List<DailyChallenge> challenges = challengeService.getTodayChallenges(userId);
        return ResponseEntity.ok(challenges);
    }

    @PostMapping("/generate")
    public ResponseEntity<List<DailyChallenge>> generateChallenges(@RequestParam UUID userId) {
        List<DailyChallenge> challenges = challengeService.generateDailyChallenges(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(challenges);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<DailyChallenge> completeChallenge(@PathVariable UUID id) {
        DailyChallenge challenge = challengeService.completeChallenge(id);
        return ResponseEntity.ok(challenge);  // ✅ OK, pas CREATED
    }

    @PostMapping("/{id}/skip")
    public ResponseEntity<DailyChallenge> skipChallenge(@PathVariable UUID id) {
        DailyChallenge challenge = challengeService.skipChallenge(id);
        return ResponseEntity.ok(challenge);
    }

    @GetMapping("/streak")
    public ResponseEntity<StreakResponse> getStreak(@RequestParam UUID userId) {
        int streak = challengeService.calculateStreak(userId);
        StreakResponse response = new StreakResponse(streak);
        return ResponseEntity.ok(response);
    }
}