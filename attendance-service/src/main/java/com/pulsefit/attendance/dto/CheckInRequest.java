package com.pulsefit.attendance.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for recording a member check-in.
 */
public class CheckInRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    private String facilityName;

    // ─── Constructors ────────────────────────────────────────────────────────────

    public CheckInRequest() {}

    // ─── Getters & Setters ───────────────────────────────────────────────────────

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }
}
