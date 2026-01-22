package com.enterprise.iam_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.Set;

@Entity
@Table(name = "users") // Good practice to use plural table names
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder // pattern for easy object creation
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    // We use a String for simplicity now, but this maps to your "ACTIVE/LOCKED" requirement
    @Builder.Default
    private String status = "PENDING"; 

    @Builder.Default
    private Boolean emailVerified = false;

    // ADD THESE MISSING FIELDS:
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER ensures roles are loaded with the user
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default // This ensures the HashSet is initialized when using the Builder
    private Set<Role> roles = new HashSet<>();
}
