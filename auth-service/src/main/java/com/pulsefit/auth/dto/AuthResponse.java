package com.pulsefit.auth.dto;

/**
 * Response DTO returned after successful authentication.
 * Contains the JWT token and basic user information.
 */
public class AuthResponse {

    private String token;
    private String username;
    private String role;
    private String message;
    private long expiresIn;

    // ─── Constructors ────────────────────────────────────────────────────────────

    public AuthResponse() {}

    public AuthResponse(String token, String username, String role, String message, long expiresIn) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.message = message;
        this.expiresIn = expiresIn;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────────

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}
