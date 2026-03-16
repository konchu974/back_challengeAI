package org.example.back_challengeai.integration;

import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.com")
                .pseudo("TestUser")
                .password("hashedPassword")
                .build();

        userRepository.save(testUser);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // When
        Optional<User> result = userRepository.findByEmail("test@test.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("test@test.com", result.get().getEmail());
        assertEquals("TestUser", result.get().getPseudo());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailNotExists() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@test.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // When
        boolean exists = userRepository.existsByEmail("test@test.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailNotExists() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@test.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void save_ShouldPersistUser() {
        // Given
        User newUser = User.builder()
                .email("new@test.com")
                .pseudo("NewUser")
                .password("password")
                .build();

        // When
        User saved = userRepository.save(newUser);

        // Then
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("new@test.com", saved.getEmail());
    }
}