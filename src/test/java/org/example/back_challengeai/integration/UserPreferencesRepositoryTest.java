package org.example.back_challengeai.integration;

import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.entity.UserPreferences;
import org.example.back_challengeai.repository.UserPreferencesRepository;
import org.example.back_challengeai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserPreferencesRepositoryTest {

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

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
    void findByUser_Id_ShouldReturnPreferences() {
        // Given
        UserPreferences preferences = UserPreferences.builder()
                .user(testUser)
                .dailyTime(10)
                .interests(Arrays.asList("sport", "reading"))
                .goals(Arrays.asList("health", "learning"))
                .build();

        userPreferencesRepository.save(preferences);

        // When
        Optional<UserPreferences> result = userPreferencesRepository.findByUser_Id(testUser.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(10, result.get().getDailyTime());
        assertEquals(2, result.get().getInterests().size());
        assertTrue(result.get().getInterests().contains("sport"));
    }

    @Test
    void save_ShouldPersistPreferences() {
        // Given
        UserPreferences preferences = UserPreferences.builder()
                .user(testUser)
                .dailyTime(15)
                .interests(new ArrayList<>())
                .goals(new ArrayList<>())
                .build();

        // When
        UserPreferences saved = userPreferencesRepository.save(preferences);

        // Then
        assertNotNull(saved.getId());
        assertEquals(15, saved.getDailyTime());
    }
}