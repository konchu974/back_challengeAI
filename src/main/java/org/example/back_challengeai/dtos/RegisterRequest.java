package org.example.back_challengeai.dtos;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
}