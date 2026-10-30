package com.pulsefit.auth.dto;

import com.pulsefit.auth.model.User;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for user registration.
 */
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private User.Role role = User.Role.ROLE_MEMBER;

    // ─── Constructors ────────────────────────────────────────────────────────────

    public RegisterRequest() {}

    // ─── Getters & Setters ───────────────────────────────────────────────────────

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }
}
