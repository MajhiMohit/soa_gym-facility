package com.pulsefit.member.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Member entity representing a PulseFit fitness club member.
 * Stored in the member-service H2 database.
 */
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @NotBlank(message = "Member name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Contact information is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String contact;

    @Column(length = 200)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "home_facility", length = 100)
    private String homeFacility;

    // ─── Constructors ────────────────────────────────────────────────────────────

    public Member() {}

    public Member(Long memberId, String name, String contact,
                  String email, String phone, String homeFacility) {
        this.memberId = memberId;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.phone = phone;
        this.homeFacility = homeFacility;
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────

    public Long getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getHomeFacility() { return homeFacility; }

    // ─── Setters ─────────────────────────────────────────────────────────────────

    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setHomeFacility(String homeFacility) { this.homeFacility = homeFacility; }

    @Override
    public String toString() {
        return "Member{memberId=" + memberId + ", name='" + name + "', contact='" + contact + "'}";
    }
}
