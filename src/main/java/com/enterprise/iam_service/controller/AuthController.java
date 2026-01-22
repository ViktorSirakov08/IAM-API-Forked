package com.enterprise.iam_service.controller;

import com.enterprise.iam_service.dto.RegisterRequest;
import com.enterprise.iam_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.enterprise.iam_service.dto.LoginRequest;
import com.enterprise.iam_service.dto.AuthResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request)); // Returns the JWT [cite: 65]
    }
}