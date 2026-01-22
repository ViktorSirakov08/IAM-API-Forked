package com.enterprise.iam_service.service;

import com.enterprise.iam_service.dto.AuthResponse;
import com.enterprise.iam_service.dto.LoginRequest;
import com.enterprise.iam_service.dto.RegisterRequest;
import com.enterprise.iam_service.model.User;
import com.enterprise.iam_service.repository.UserRepository;
import com.enterprise.iam_service.security.JwtUtils;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Automatically creates constructor for dependencies
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final SecurityService securityService;

    public AuthResponse register(RegisterRequest request) {
        // 1. Check if user exists
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use");
        }
        validatePasswordStrength(request.password());
        // 2. Create User object using Builder pattern
        var user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password())) // Hash immediately
                .status("ACTIVE") // Default to ACTIVE for the MVP
                .build();

        // 3. Save to DB
        userRepository.save(user);
        String token = jwtUtils.generateToken(user.getEmail());

        return new AuthResponse(token);
    }

    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 1. Check if account is locked
        if ("LOCKED".equals(user.getStatus())) {
            throw new RuntimeException("Account is locked due to multiple failed attempts.");
        }

        // 2. Check password
        if (passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            // SUCCESS: Reset attempts and return token
            user.setFailedLoginAttempts(0);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            String token = jwtUtils.generateToken(user.getEmail());
            return new AuthResponse(token);
        } else {
            // FAILURE: Increment attempts
            securityService.handleFailedLogin(user);
            throw new RuntimeException("Invalid credentials");
        }
    }

    public void validatePasswordStrength(String password) {
    // Logic: At least 8 characters, 1 uppercase, 1 lowercase, and 1 number.
    // No special symbols required.
    String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    
    if (password == null || !password.matches(regex)) {
        throw new RuntimeException("Password too weak! Must be at least 8 characters and include uppercase, lowercase, and a number.");
    }
}
}