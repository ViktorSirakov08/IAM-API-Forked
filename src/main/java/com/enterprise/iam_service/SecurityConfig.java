package com.enterprise.iam_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.enterprise.iam_service.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ? @Configuration: Marks this class as a source of bean definitions for the application context.
// ? @EnableWebSecurity: Switches on Spring Security's web security support.
// ? @EnableMethodSecurity: Enables the use of @PreAuthorize annotations in your controllers.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; // * Injecting the custom JWT logic

    // * Bean: Defines the encryption algorithm used across the entire system.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // ! SECURITY: BCrypt automatically handles salting to prevent rainbow table attacks.
        return new BCryptPasswordEncoder();
    }

    // * Security Pipeline: Defines how HTTP requests are secured and filtered.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ! SECURITY: CSRF is disabled because JWTs are stored on the client side (typically not in cookies).
            .csrf(csrf -> csrf.disable())
            
            // ! SECURITY: Configures the app to be STATELESS. No HTTP sessions are created or used.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // * Request Authorization: Defines "Who can go where".
            .authorizeHttpRequests(auth -> auth
                // ? PermitAll: Authentication and Registration endpoints must be public.
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/error").permitAll()
                
                // ! Shield: Every other request in the system requires a valid JWT token.
                .anyRequest().authenticated() 
            )
            
            // ! Filter Ordering: Inserts the JWT filter BEFORE the standard Username/Password filter.
            // ! This ensures the token is validated first on every incoming request.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}