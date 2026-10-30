package com.pulsefit.auth;

import com.pulsefit.auth.dto.LoginRequest;
import com.pulsefit.auth.dto.RegisterRequest;
import com.pulsefit.auth.model.User;
import com.pulsefit.auth.repository.UserRepository;
import com.pulsefit.auth.security.JwtUtil;
import com.pulsefit.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 * Tests user registration and login business logic with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("john_doe");
        registerRequest.setPassword("password123");
        registerRequest.setRole(User.Role.ROLE_MEMBER);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("john_doe");
        loginRequest.setPassword("password123");

        savedUser = new User(1L, "john_doe", "encoded_password", User.Role.ROLE_MEMBER);
    }

    // ─── Registration Tests ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Should register user successfully and return JWT token")
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("john_doe", "ROLE_MEMBER")).thenReturn("mocked.jwt.token");
        when(jwtUtil.getExpiration()).thenReturn(86400000L);

        var response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getUsername()).isEqualTo("john_doe");
        assertThat(response.getRole()).isEqualTo("ROLE_MEMBER");
        assertThat(response.getMessage()).contains("registered");

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowWhenUsernameAlreadyTaken() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already taken");

        verify(userRepository, never()).save(any());
    }

    // ─── Login Tests ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should login user successfully and return JWT token")
    void shouldLoginUserSuccessfully() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("john_doe", null));
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken("john_doe", "ROLE_MEMBER")).thenReturn("mocked.jwt.token");
        when(jwtUtil.getExpiration()).thenReturn(86400000L);

        var response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getUsername()).isEqualTo("john_doe");
        assertThat(response.getMessage()).contains("successful");
    }

    @Test
    @DisplayName("Should assign default ROLE_MEMBER when role not specified")
    void shouldAssignDefaultRoleWhenNotSpecified() {
        registerRequest.setRole(null); // no role in request

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            assertThat(u.getRole()).isEqualTo(User.Role.ROLE_MEMBER);
            return savedUser;
        });
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");
        when(jwtUtil.getExpiration()).thenReturn(86400000L);

        authService.register(registerRequest);
        verify(userRepository).save(any(User.class));
    }
}
