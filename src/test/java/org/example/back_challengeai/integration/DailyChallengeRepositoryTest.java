package org.example.back_challengeai.integration;

import org.example.back_challengeai.entity.ChallengeStatus;
import org.example.back_challengeai.entity.DailyChallenge;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.repository.DailyChallengeRepository;
import org.example.back_challengeai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DailyChallengeRepositoryTest {

    @Autowired
    private DailyChallengeRepository dailyChallengeRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.com")
                .pseudo("TestUser")
                .password("password")
                .build();

        testUser = userRepository.save(testUser);
    }

    @Test
    void findByUser_IdAndChallengeDate_ShouldReturnChallenges() {
        // Given
        LocalDate today = LocalDate.now();
        DailyChallenge challenge = DailyChallenge.builder()
                .user(testUser)
                .challengeText("Test challenge")
                .challengeDate(today)
                .status(ChallengeStatus.PENDING)
                .build();

        dailyChallengeRepository.save(challenge);

        // When
        List<DailyChallenge> result = dailyChallengeRepository
                .findByUser_IdAndChallengeDate(testUser.getId(), today);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test challenge", result.get(0).getChallengeText());
    }

    @Test
    void countByUser_IdAndChallengeDateAndStatus_ShouldReturnCount() {
        // Given
        LocalDate today = LocalDate.now();
        DailyChallenge challenge1 = DailyChallenge.builder()
                .user(testUser)
                .challengeText("Challenge 1")
                .challengeDate(today)
                .status(ChallengeStatus.COMPLETED)
                .build();

        DailyChallenge challenge2 = DailyChallenge.builder()
                .user(testUser)
                .challengeText("Challenge 2")
                .challengeDate(today)
                .status(ChallengeStatus.PENDING)
                .build();

        dailyChallengeRepository.save(challenge1);
        dailyChallengeRepository.save(challenge2);

        // When
        long completedCount = dailyChallengeRepository
                .countByUser_IdAndChallengeDateAndStatus(testUser.getId(), today, ChallengeStatus.COMPLETED);

        long pendingCount = dailyChallengeRepository
                .countByUser_IdAndChallengeDateAndStatus(testUser.getId(), today, ChallengeStatus.PENDING);

        // Then
        assertEquals(1, completedCount);
        assertEquals(1, pendingCount);
    }

    @Test
    void findByUser_Id_ShouldReturnAllUserChallenges() {
        // Given
        DailyChallenge challenge1 = DailyChallenge.builder()
                .user(testUser)
                .challengeText("Challenge 1")
                .challengeDate(LocalDate.now())
                .status(ChallengeStatus.COMPLETED)
                .build();

        DailyChallenge challenge2 = DailyChallenge.builder()
                .user(testUser)
                .challengeText("Challenge 2")
                .challengeDate(LocalDate.now().minusDays(1))
                .status(ChallengeStatus.COMPLETED)
                .build();

        dailyChallengeRepository.save(challenge1);
        dailyChallengeRepository.save(challenge2);

        // When
        List<DailyChallenge> result = dailyChallengeRepository.findByUser_Id(testUser.getId());

        // Then
        assertEquals(2, result.size());
    }
}