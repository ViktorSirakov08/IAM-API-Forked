package com.enterprise.iam_service.service;

import com.enterprise.iam_service.model.User;
import com.enterprise.iam_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public String hashPassword(String password) {
        return passwordEncoder.encode(password); // [cite: 60]
    }

    public boolean verifyPassword(String password, String hash) {
        return passwordEncoder.matches(password, hash); // [cite: 61]
    }

    public void handleFailedLogin(User user) {
        // [cite: 15, 63, 85] Requirement: Lock user after multiple attempts
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= 5) {
            user.setStatus("LOCKED");
        }
        userRepository.save(user);
    }
}