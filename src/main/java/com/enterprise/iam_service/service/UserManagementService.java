package com.enterprise.iam_service.service;

import com.enterprise.iam_service.model.User;
import com.enterprise.iam_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.enterprise.iam_service.dto.UserProfileResponse;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public void changePasswordByEmail(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Existing password does not match");
        }

        // Add password strength check (Requirement)
        

        authService.validatePasswordStrength(newPassword);

        // Encode and save
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Convert Set<Role> to Set<String> for the response
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(java.util.stream.Collectors.toSet());

        return new UserProfileResponse(
            user.getEmail(),
            user.getStatus(),
            roles,
            user.getLastLoginAt(),
            user.getCreatedAt()
        );
    }
}