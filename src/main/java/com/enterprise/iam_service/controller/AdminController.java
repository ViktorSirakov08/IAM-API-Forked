package com.enterprise.iam_service.controller;

import com.enterprise.iam_service.model.User;
import com.enterprise.iam_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    // Requirement: GET /api/admin/users – List all users
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')") // Only users with ROLE_ADMIN can enter
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/locked")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getLockedUsers() {
        // This assumes you add a custom finder in your UserRepository
        return ResponseEntity.ok(userRepository.findAllByStatus("LOCKED"));
}
}