package com.pulsefit.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating or updating a member.
 */
public class MemberRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Contact is required")
    @Size(max = 50)
    private String contact;

    private String email;
    private String phone;
    private String homeFacility;

    // ─── Constructors ────────────────────────────────────────────────────────────

    public MemberRequest() {}

    // ─── Getters & Setters ───────────────────────────────────────────────────────

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getHomeFacility() { return homeFacility; }
    public void setHomeFacility(String homeFacility) { this.homeFacility = homeFacility; }
}
