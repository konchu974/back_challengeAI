package org.example.back_challengeai.controller;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.dto.*;
import org.example.back_challengeai.entity.Achievement;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.entity.UserPreferences;
import org.example.back_challengeai.service.AchievementService;
import org.example.back_challengeai.service.ChallengeService;
import org.example.back_challengeai.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AchievementService achievementService;
    private final ChallengeService challengeService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .pseudo(user.getPseudo())
                .createdAt(user.getCreatedAt())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        UUID userId = user.getId();
        int streak = challengeService.calculateStreak(userId);
        long totalCompleted = challengeService.countCompleted(userId);
        return ResponseEntity.ok(UserStatsResponse.builder()
                .currentStreak(streak)
                .totalCompleted(totalCompleted)
                .build());
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(Authentication authentication, @RequestBody ChangePasswordRequest request) {
        User user = userService.findByEmail(authentication.getName());
        userService.changePassword(user.getId(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<UserPreferences> updateUserPreferences(@PathVariable UUID id, @RequestBody UpdatePreferencesRequest request) {
        UserPreferences preferences = userService.updatePreferences(
                id,
                request.getInterests(),
                request.getGoals(),
                request.getDailyTime()
        );
        return ResponseEntity.ok(preferences);
    }

    @GetMapping("/{id}/achievements")
    public ResponseEntity<Set<Achievement>> getUserAchievements(@PathVariable UUID id) {
        Set<Achievement> achievement = achievementService.getUserAchievements(id);
        return ResponseEntity.ok(achievement);
    }
}
