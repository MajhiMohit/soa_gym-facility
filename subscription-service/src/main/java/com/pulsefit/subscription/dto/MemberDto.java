package com.pulsefit.subscription.dto;

/**
 * DTO representing a Member response from the Member Service.
 * Used for Feign client deserialization during inter-service calls.
 */
public class MemberDto {

    private Long memberId;
    private String name;
    private String contact;
    private String email;
    private String phone;
    private String homeFacility;

    // ─── Constructors ────────────────────────────────────────────────────────────

    public MemberDto() {}

    public MemberDto(Long memberId, String name, String contact,
                     String email, String phone, String homeFacility) {
        this.memberId = memberId;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.phone = phone;
        this.homeFacility = homeFacility;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────────

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

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
