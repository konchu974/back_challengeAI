package org.example.back_challengeai.service;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.entity.Achievement;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.repository.AchievementRepository;
import org.example.back_challengeai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;

    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    public Set<Achievement> getUserAchievements(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));
        return user.getAchievements();
    }

    public void unlockAchievement(UUID userId, UUID achievementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new RuntimeException("Achievement non trouvé"));


        if (user.getAchievements().contains(achievement)) {
            throw new IllegalStateException("Achievement déjà débloqué");
        }
        user.unlockAchievement(achievement);

        userRepository.save(user);
    }
}
