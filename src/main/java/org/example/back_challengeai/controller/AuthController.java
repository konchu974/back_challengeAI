package org.example.back_challengeai.controller;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.dtos.LoginRequest;
import org.example.back_challengeai.dtos.RegisterRequest;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {
        // TODO: Plus tard, vérifier le password et générer un JWT
        User user = userService.findByEmail(request.getEmail());
        return ResponseEntity.ok(user);
    }
}