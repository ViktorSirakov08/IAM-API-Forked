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

// ? @Service: Marks this as a service-layer bean where business logic and transaction management reside.
// ? @RequiredArgsConstructor: Injects all 'final' fields (repositories/security utils) via the constructor.
@Service
@RequiredArgsConstructor 
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final SecurityService securityService;

    // * Business Logic: Handles the end-to-end registration flow for new users.
    public AuthResponse register(RegisterRequest request) {
        // * Step 1: Pre-persistence check to ensure the email is unique.
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use");
        }
        
        // ! SECURITY: Enforces the organization's password complexity rules before proceeding.
        validatePasswordStrength(request.password());
        
        // * Step 2: Assemble the User object. 
        // ! SECURITY: The password is encrypted using BCrypt immediately via passwordEncoder.encode().
        var user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password())) 
                .status("ACTIVE") 
                .build();

        // * Step 3: Persistence and Token Issuance.
        userRepository.save(user);
        String token = jwtUtils.generateToken(user.getEmail());

        return new AuthResponse(token);
    }

    // * Business Logic: Handles credential verification and session initiation.
    public AuthResponse authenticate(LoginRequest request) {
        // * Step 1: Look up the identity in the database.
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // ! SECURITY: Brute-Force Check. Block authentication if the account status is 'LOCKED'.
        if ("LOCKED".equals(user.getStatus())) {
            throw new RuntimeException("Account is locked due to multiple failed attempts.");
        }

        // ! SECURITY: Compare the raw password from the request with the salted hash in the database.
        if (passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            // * SUCCESS: Clean up state and update audit metadata.
            user.setFailedLoginAttempts(0);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            // * Issue the session token.
            String token = jwtUtils.generateToken(user.getEmail());
            return new AuthResponse(token);
        } else {
            // ! FAILURE: Trigger the security increment logic to eventually lock the account.
            securityService.handleFailedLogin(user);
            throw new RuntimeException("Invalid credentials");
        }
    }

    // ! SECURITY: Regex-based validation for identity assurance.
    public void validatePasswordStrength(String password) {
    // ? Regex breakdown: 8+ characters, contains digits, lowercase, and uppercase letters.
    String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    
    if (password == null || !password.matches(regex)) {
        throw new RuntimeException("Password too weak! Must be at least 8 characters and include uppercase, lowercase, and a number.");
    }
}
}