package org.example.back_challengeai.integration;

import org.example.back_challengeai.entity.Achievement;
import org.example.back_challengeai.repository.AchievementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AchievementRepositoryTest {

    @Autowired
    private AchievementRepository achievementRepository;

    @Test
    void findAll_ShouldReturnAllAchievements() {
        // Given
        Achievement achievement1 = Achievement.builder()
                .name("First Challenge")
                .description("Complete your first challenge")
                .build();

        Achievement achievement2 = Achievement.builder()
                .name("Week Streak")
                .description("Complete challenges for 7 days")
                .build();

        achievementRepository.save(achievement1);
        achievementRepository.save(achievement2);

        // When
        List<Achievement> result = achievementRepository.findAll();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void save_ShouldPersistAchievement() {
        // Given
        Achievement achievement = Achievement.builder()
                .name("Test Achievement")
                .description("Test description")
                .build();

        // When
        Achievement saved = achievementRepository.save(achievement);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Test Achievement", saved.getName());
    }
}