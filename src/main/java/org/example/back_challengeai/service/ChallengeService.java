package org.example.back_challengeai.service;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.entity.ChallengeStatus;
import org.example.back_challengeai.entity.DailyChallenge;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.repository.DailyChallengeRepository;
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

        while (true){

            long count = dailyChallengeRepository.countByUser_IdAndChallengeDateAndStatus(userId, currentDate, ChallengeStatus.COMPLETED);

            if (count == 0){
                break;
            }

            streak ++;
            currentDate = currentDate.minusDays(1);
        }
        return streak;
    }

    public List<DailyChallenge> generateDailyChallenges(UUID userId) {
        List<DailyChallenge> existing = getTodayChallenges(userId);
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("challenge already exists for today");
        }

        User user  = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

        String[] simulatedChallenges = {
                "Marche 5000 pas aujourd'hui",
                "Bois 8 verres d'eau",
                "Lis 10 pages d'un livre"
        };

        List<DailyChallenge> challenges = new ArrayList<>();

        for (String text : simulatedChallenges) {
            DailyChallenge dChallenge = DailyChallenge.builder()
                    .user(user)
                    .challengeDate(LocalDate.now())
                    .challengeText(text)
                    .build();

            challenges.add(dailyChallengeRepository.save(dChallenge));
        }
        return challenges;
    }
}
