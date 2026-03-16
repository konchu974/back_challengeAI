package org.example.back_challengeai.unit;

import org.example.back_challengeai.entity.Achievement;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.repository.AchievementRepository;
import org.example.back_challengeai.repository.UserRepository;
import org.example.back_challengeai.service.AchievementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AchievementService achievementService;

    private User testUser;
    private Achievement testAchievement;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .pseudo("TestUser")
                .achievements(new HashSet<>())
                .build();

        testAchievement = Achievement.builder()
                .id(UUID.randomUUID())
                .name("First Challenge")
                .description("Complete your first challenge")
                .build();
    }

    @Test
    void getAllAchievements_ShouldReturnAllAchievements() {
        // Given
        List<Achievement> achievements = Arrays.asList(testAchievement);
        when(achievementRepository.findAll()).thenReturn(achievements);

        // When
        List<Achievement> result = achievementService.getAllAchievements();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(achievementRepository, times(1)).findAll();
    }

    @Test
    void getUserAchievements_ShouldReturnUserAchievements() {
        // Given
        testUser.getAchievements().add(testAchievement);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // When
        Set<Achievement> result = achievementService.getUserAchievements(testUser.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(testAchievement));
    }

    @Test
    void unlockAchievement_ShouldUnlockAchievement_WhenNotAlreadyUnlocked() {
        // Given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(achievementRepository.findById(testAchievement.getId())).thenReturn(Optional.of(testAchievement));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        achievementService.unlockAchievement(testUser.getId(), testAchievement.getId());

        // Then
        verify(userRepository, times(1)).save(testUser);
        assertTrue(testUser.getAchievements().contains(testAchievement));
    }

    @Test
    void unlockAchievement_ShouldThrowException_WhenAlreadyUnlocked() {
        // Given
        testUser.getAchievements().add(testAchievement);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(achievementRepository.findById(testAchievement.getId())).thenReturn(Optional.of(testAchievement));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            achievementService.unlockAchievement(testUser.getId(), testAchievement.getId());
        });

        verify(userRepository, never()).save(any(User.class));
    }
}