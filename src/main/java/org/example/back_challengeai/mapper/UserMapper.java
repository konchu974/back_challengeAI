package org.example.back_challengeai.mapper;

import org.example.back_challengeai.dto.AuthResponse;
import org.example.back_challengeai.dto.UserResponse;
import org.example.back_challengeai.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static AuthResponse toAuthResponse(User user) {
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .token(null)  // ← Pour l'instant null, JWT plus tard
                .build();
    }
}