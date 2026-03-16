package org.example.back_challengeai.unit;

import org.example.back_challengeai.entity.ChallengeStatus;
import org.example.back_challengeai.entity.DailyChallenge;
import org.example.back_challengeai.repository.DailyChallengeRepository;
import org.example.back_challengeai.service.ChallengeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private DailyChallengeRepository dailyChallengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    private UUID userId;
    private DailyChallenge testChallenge;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testChallenge = DailyChallenge.builder()
                .id(UUID.randomUUID())
                .challengeText("Test challenge")
                .status(ChallengeStatus.PENDING)
                .challengeDate(LocalDate.now())
                .build();
    }

    @Test
    void completeChallenge_ShouldMarkAsCompleted() {
        // Given
        when(dailyChallengeRepository.findById(testChallenge.getId()))
                .thenReturn(Optional.of(testChallenge));
        when(dailyChallengeRepository.save(any(DailyChallenge.class)))
                .thenReturn(testChallenge);

        // When
        DailyChallenge result = challengeService.completeChallenge(testChallenge.getId());

        // Then
        verify(dailyChallengeRepository).save(result);
        assertEquals(ChallengeStatus.COMPLETED, testChallenge.getStatus());
    }

    @Test
    void calculateStreak_ShouldReturnCorrectStreak() {
        // Given - 3 jours consécutifs complétés
        LocalDate today = LocalDate.now();

        when(dailyChallengeRepository.countByUser_IdAndChallengeDateAndStatus(
                userId, today, ChallengeStatus.COMPLETED
        )).thenReturn(1L);

        when(dailyChallengeRepository.countByUser_IdAndChallengeDateAndStatus(
                userId, today.minusDays(1), ChallengeStatus.COMPLETED
        )).thenReturn(1L);

        when(dailyChallengeRepository.countByUser_IdAndChallengeDateAndStatus(
                userId, today.minusDays(2), ChallengeStatus.COMPLETED
        )).thenReturn(1L);

        when(dailyChallengeRepository.countByUser_IdAndChallengeDateAndStatus(
                userId, today.minusDays(3), ChallengeStatus.COMPLETED
        )).thenReturn(0L);

        // When
        int streak = challengeService.calculateStreak(userId);

        // Then
        assertEquals(3, streak);
    }
}