package org.example.back_challengeai.controller;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.dto.ChallengeResponse;
import org.example.back_challengeai.dto.StreakResponse;
import org.example.back_challengeai.entity.DailyChallenge;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.mapper.ChallengeMapper;
import org.example.back_challengeai.service.ChallengeService;
import org.example.back_challengeai.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserService userService;

    @GetMapping("/today")
    public ResponseEntity<List<ChallengeResponse>> getTodayChallenges(Authentication authentication) {
        UUID userId = getUserId(authentication);
        List<DailyChallenge> challenges = challengeService.getTodayChallenges(userId);
        if (challenges.isEmpty()) {
            challenges = challengeService.generateDailyChallenges(userId);
        }
        return ResponseEntity.ok(ChallengeMapper.toResponseList(challenges));
    }

    @PostMapping("/generate")
    public ResponseEntity<List<ChallengeResponse>> generateChallenges(Authentication authentication) {
        UUID userId = getUserId(authentication);
        List<DailyChallenge> challenges = challengeService.generateDailyChallenges(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ChallengeMapper.toResponseList(challenges));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ChallengeResponse> completeChallenge(@PathVariable UUID id) {
        DailyChallenge challenge = challengeService.completeChallenge(id);
        return ResponseEntity.ok(ChallengeMapper.toResponse(challenge));
    }

    @PostMapping("/{id}/skip")
    public ResponseEntity<ChallengeResponse> skipChallenge(@PathVariable UUID id) {
        DailyChallenge challenge = challengeService.skipChallenge(id);
        return ResponseEntity.ok(ChallengeMapper.toResponse(challenge));
    }

    @GetMapping("/streak")
    public ResponseEntity<StreakResponse> getStreak(Authentication authentication) {
        UUID userId = getUserId(authentication);
        int streak = challengeService.calculateStreak(userId);
        return ResponseEntity.ok(new StreakResponse(streak));
    }

    private UUID getUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        return user.getId();
    }
}
