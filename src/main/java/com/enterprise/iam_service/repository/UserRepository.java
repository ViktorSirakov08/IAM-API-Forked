package com.enterprise.iam_service.repository;

import com.enterprise.iam_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Spring Data automatically implements this based on the name
    Optional<User> findByEmail(String email);
    List<User> findAllByStatus(String status);
    boolean existsByEmail(String email);
}