package com.enterprise.iam_service.controller;

import com.enterprise.iam_service.dto.PasswordChangeRequest;
import com.enterprise.iam_service.dto.UserProfileResponse;
import com.enterprise.iam_service.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManagementService userManagementService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // We'll create a quick DTO to return clean data
        return ResponseEntity.ok(userManagementService.getUserProfile(email));
}

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userManagementService.changePasswordByEmail(email, request.oldPassword(), request.newPassword());
        return ResponseEntity.ok("Password updated successfully");
    }
}
