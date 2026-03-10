package org.example.back_challengeai.controller;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.dto.UpdatePreferencesRequest;
import org.example.back_challengeai.entity.Achievement;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.entity.UserPreferences;
import org.example.back_challengeai.service.AchievementService;
import org.example.back_challengeai.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AchievementService achievementService;

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
