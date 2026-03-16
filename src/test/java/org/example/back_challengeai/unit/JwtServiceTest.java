package org.example.back_challengeai.unit;

import org.example.back_challengeai.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String testSecret = "test-secret-key-for-testing-only-must-be-at-least-256-bits-long-so-adding-more-characters";
    private final long testExpiration = 3600000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Given
        String email = "test@test.com";
        String userId = UUID.randomUUID().toString();

        // When
        String token = jwtService.generateToken(email, userId);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT a 3 parties
    }

    @Test
    void getUserIdFromToken_ShouldExtractUserId() {
        // Given
        String email = "test@test.com";
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(email, userId.toString());

        // When
        UUID extractedUserId = jwtService.getUserIdFromToken(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    void getUserIdFromToken_ShouldThrowException_WhenTokenInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            jwtService.getUserIdFromToken(invalidToken);
        });
    }
}