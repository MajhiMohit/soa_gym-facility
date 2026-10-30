package com.pulsefit.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * User entity for authentication.
 * Stores credentials and role for JWT-based authorization.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    public enum Role {
        ROLE_ADMIN,
        ROLE_MEMBER,
        ROLE_STAFF
    }

    // ─── Constructors ────────────────────────────────────────────────────────────

    public User() {}

    public User(Long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    // ─── Setters ─────────────────────────────────────────────────────────────────

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role=" + role + "}";
    }
}
