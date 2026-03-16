package org.example.back_challengeai.unit;

import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.entity.UserPreferences;
import org.example.back_challengeai.repository.UserRepository;
import org.example.back_challengeai.repository.UserPreferencesRepository;
import org.example.back_challengeai.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPreferencesRepository userPreferencesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .pseudo("TestUser")
                .password("hashedPassword")
                .build();
    }

    @Test
    void registerUser_ShouldCreateUser_WhenEmailNotExists() {
        // Given
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.registerUser("new@test.com", "password123", "NewUser");

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(userPreferencesRepository).save(any());
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("existing@test.com", "password", "User"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsValid() {
        // Given
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        // When
        User result = userService.login("test@test.com", "password123");

        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void login_ShouldThrow_WhenInvalidPassword() {
        // Given
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () ->
                userService.login("test@test.com", "wrongpassword"));
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        // Given
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () ->
                userService.login("unknown@test.com", "password"));
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        // Given
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByEmail("test@test.com");

        // Then
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void findByEmail_ShouldThrow_WhenNotFound() {
        // Given
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () ->
                userService.findByEmail("unknown@test.com"));
    }

    @Test
    void updatePreferences_ShouldUpdateFields() {
        // Given
        UserPreferences prefs = UserPreferences.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .interests(List.of("sport"))
                .goals(List.of("fitness"))
                .dailyTime(10)
                .build();

        when(userPreferencesRepository.findByUser_Id(testUser.getId()))
                .thenReturn(Optional.of(prefs));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(prefs);

        // When
        UserPreferences result = userService.updatePreferences(
                testUser.getId(), List.of("musique"), List.of("apprendre"), 20);

        // Then
        assertEquals(List.of("musique"), result.getInterests());
        assertEquals(List.of("apprendre"), result.getGoals());
        assertEquals(20, result.getDailyTime());
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenOldPasswordCorrect() {
        // Given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");

        // When
        assertDoesNotThrow(() ->
                userService.changePassword(testUser.getId(), "oldPassword", "newPassword"));

        // Then
        verify(userRepository).save(testUser);
    }

    @Test
    void changePassword_ShouldThrow_WhenOldPasswordIncorrect() {
        // Given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongOldPassword", testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () ->
                userService.changePassword(testUser.getId(), "wrongOldPassword", "newPassword"));
        verify(userRepository, never()).save(any());
    }
}
