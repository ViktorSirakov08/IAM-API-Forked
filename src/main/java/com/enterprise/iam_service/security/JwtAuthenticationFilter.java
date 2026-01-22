package com.enterprise.iam_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.enterprise.iam_service.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.stream.Collectors;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // 1. Get the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        // 2. Check if it starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            // 3. Extract the email (subject) from the token
            String email = jwtUtils.extractEmail(token);

            // 4. If email is valid and user is not already authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                userRepository.findByEmail(email).ifPresent(user -> {
                    // Convert Roles to Spring Security Authorities
                    var authorities = user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                });
            }
        }

        // Continue to the next filter
        filterChain.doFilter(request, response);
    }
}