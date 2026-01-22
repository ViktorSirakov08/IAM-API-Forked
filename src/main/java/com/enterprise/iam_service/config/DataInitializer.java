package com.enterprise.iam_service.config;
import com.enterprise.iam_service.model.Role;
import com.enterprise.iam_service.model.User;
import com.enterprise.iam_service.repository.RoleRepository;
import com.enterprise.iam_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Create ADMIN role if not exists
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);

            // 2. Create a default Admin user
            if (userRepository.findByEmail("admin@enterprise.com").isEmpty()) {
                User admin = User.builder()
                        .email("admin@enterprise.com")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .status("ACTIVE")
                        .roles(Set.of(adminRole))
                        .build();
                userRepository.save(admin);
            }
        }
    }
}