package org.example.back_challengeai.repository;

import org.example.back_challengeai.entity.ChallengeStatus;
import org.example.back_challengeai.entity.DailyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DailyChallengeRepository extends JpaRepository<DailyChallenge, UUID> {
    List<DailyChallenge> findByUser_IdAndChallengeDate(UUID userId, LocalDate challengeDate);

    List<DailyChallenge> findByUser_IdAndChallengeDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);

    List<DailyChallenge> findByUser_Id(UUID userId);

    long countByUser_IdAndStatus(UUID userId, ChallengeStatus status);
    long countByUser_IdAndChallengeDateAndStatus(UUID userId, LocalDate challengeDate, ChallengeStatus status);
}
