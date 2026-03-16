package org.example.back_challengeai.controller;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.dto.AuthResponse;
import org.example.back_challengeai.dto.LoginRequest;
import org.example.back_challengeai.dto.RegisterRequest;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.security.JwtService;
import org.example.back_challengeai.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getEmail(), request.getPassword(), request.getPseudo());

        String token = jwtService.generateToken(user.getEmail(), user.getId().toString());

        AuthResponse response = AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .pseudo(user.getPseudo())
                .createdAt(user.getCreatedAt())
                .token(token)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getEmail(), request.getPassword());


        String token = jwtService.generateToken(user.getEmail(), user.getId().toString());

        AuthResponse response = AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .pseudo(user.getPseudo())
                .createdAt(user.getCreatedAt())
                .token(token)
                .build();

        return ResponseEntity.ok(response);
    }
}