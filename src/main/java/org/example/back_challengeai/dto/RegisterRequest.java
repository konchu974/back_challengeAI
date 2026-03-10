package org.example.back_challengeai.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
}