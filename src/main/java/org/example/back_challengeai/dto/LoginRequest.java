package org.example.back_challengeai.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}