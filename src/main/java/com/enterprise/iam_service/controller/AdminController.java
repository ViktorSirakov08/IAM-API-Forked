package com.enterprise.iam_service.controller;

import com.enterprise.iam_service.model.User;
import com.enterprise.iam_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ? @RestController marks this class as a request handler for RESTful web services.
// ? @RequestMapping("/api/admin") sets the base path for all endpoints in this controller.
// ? @RequiredArgsConstructor (Lombok) generates a constructor for the 'final' UserRepository field.
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    // ! SECURITY: @PreAuthorize("hasRole('ADMIN')") ensures only users with 'ROLE_ADMIN' can access this.
    // * Fetches a complete list of all registered users from the database.
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ! SECURITY: Requires Administrative privileges to monitor security statuses.
    // * Retrieves all accounts that have been deactivated or locked due to failed login attempts.
    @GetMapping("/users/locked")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getLockedUsers() {
        return ResponseEntity.ok(userRepository.findAllByStatus("LOCKED"));
    }
    
}