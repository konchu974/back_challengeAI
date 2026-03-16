package org.example.back_challengeai.unit;

import org.example.back_challengeai.dto.AuthResponse;
import org.example.back_challengeai.dto.UserResponse;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.mapper.UserMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toResponse_ShouldMapUserFields() {
        // Given
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .pseudo("TestUser")
                .build();

        // When
        UserResponse response = UserMapper.toResponse(user);

        // Then
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void toAuthResponse_ShouldMapUserFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .pseudo("TestUser")
                .build();

        // When
        AuthResponse response = UserMapper.toAuthResponse(user);

        // Then
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
        assertNull(response.getToken());
    }
}
