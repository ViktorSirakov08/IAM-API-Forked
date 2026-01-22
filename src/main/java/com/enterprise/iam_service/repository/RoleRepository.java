package com.enterprise.iam_service.repository;

import com.enterprise.iam_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // This allows us to find "ADMIN" or "USER" roles by their name string
    Optional<Role> findByName(String name);
}