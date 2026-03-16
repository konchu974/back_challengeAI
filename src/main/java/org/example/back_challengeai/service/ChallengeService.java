package org.example.back_challengeai.service;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.entity.ChallengeStatus;
import org.example.back_challengeai.entity.DailyChallenge;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.entity.UserPreferences;
import org.example.back_challengeai.repository.DailyChallengeRepository;
import org.example.back_challengeai.repository.UserPreferencesRepository;
import org.example.back_challengeai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeService {

    private final DailyChallengeRepository dailyChallengeRepository;
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final AIService aIService;

    public List<DailyChallenge> getTodayChallenges(UUID userId) {
        return dailyChallengeRepository.findByUser_IdAndChallengeDate(userId, LocalDate.now());
    }

    public DailyChallenge completeChallenge(UUID challengeId) {
        DailyChallenge dChallenge = dailyChallengeRepository.findById(challengeId).orElseThrow(() -> new RuntimeException("challenge not found"));

        if (dChallenge.isCompleted()) {
            throw new RuntimeException("challenge already completed");
        }
        dChallenge.markAsCompleted();

        return dailyChallengeRepository.save(dChallenge);
    }

    public DailyChallenge skipChallenge(UUID challengeId) {
        DailyChallenge dChallenge = dailyChallengeRepository.findById(challengeId).orElseThrow(() -> new RuntimeException("challenge not found"));
        if (dChallenge.isSkipped()) {
            throw new RuntimeException("challenge already skipped");
        }
        dChallenge.markAsSkipped();

        return dailyChallengeRepository.save(dChallenge);
    }

    public int calculateStreak(UUID userId) {
        LocalDate currentDate = LocalDate.now();
        int streak = 0;

        while (true) {

            long count = dailyChallengeRepository.countByUser_IdAndChallengeDateAndStatus(userId, currentDate, ChallengeStatus.COMPLETED);

            if (count == 0) {
                break;
            }

            streak++;
            currentDate = currentDate.minusDays(1);
        }
        return streak;
    }

    public List<DailyChallenge> generateDailyChallenges(UUID userId) {
        // Vérifier si des challenges existent déjà
        List<DailyChallenge> existingChallenges = dailyChallengeRepository
                .findByUser_IdAndChallengeDate(userId, LocalDate.now());

        if (!existingChallenges.isEmpty()) {
            throw new RuntimeException("Challenges already generated for today");
        }

        // 1. Récupérer le user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Récupérer les préférences
        UserPreferences prefs = userPreferencesRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Preferences not found"));

        // 3. Générer avec l'IA
        List<String> aiChallenges = aIService.generateChallenges(
                prefs.getInterests(),
                prefs.getGoals(),
                prefs.getDailyTime()
        );

        // 4. Créer les entités DailyChallenge
        List<DailyChallenge> challenges = new ArrayList<>();

        for (String challengeText : aiChallenges) {
            DailyChallenge challenge = DailyChallenge.builder()
                    .user(user)
                    .challengeText(challengeText)
                    .challengeDate(LocalDate.now())
                    .status(ChallengeStatus.PENDING)
                    .build();

            challenges.add(challenge);
        }

        return dailyChallengeRepository.saveAll(challenges);
    }

    public long countCompleted(UUID userId) {
        return dailyChallengeRepository.countByUser_IdAndStatus(userId, ChallengeStatus.COMPLETED);
    }

}
