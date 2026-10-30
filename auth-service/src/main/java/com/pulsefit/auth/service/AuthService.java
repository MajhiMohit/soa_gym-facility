package com.pulsefit.auth.service;

import com.pulsefit.auth.dto.AuthResponse;
import com.pulsefit.auth.dto.LoginRequest;
import com.pulsefit.auth.dto.RegisterRequest;
import com.pulsefit.auth.model.User;
import com.pulsefit.auth.repository.UserRepository;
import com.pulsefit.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Business logic for user registration and JWT authentication.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Register a new user with BCrypt-encoded password.
     *
     * @param request registration details (username, password, role)
     * @return AuthResponse with JWT token for immediate access
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username '" + request.getUsername() + "' is already taken.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.ROLE_MEMBER);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                "User registered successfully",
                jwtUtil.getExpiration()
        );
    }

    /**
     * Authenticate a user and return a signed JWT token.
     *
     * @param request login credentials
     * @return AuthResponse with JWT token and user info
     */
    public AuthResponse login(LoginRequest request) {
        // Spring Security will throw BadCredentialsException if invalid
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                "Login successful",
                jwtUtil.getExpiration()
        );
    }
}
