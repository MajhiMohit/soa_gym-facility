package com.pulsefit.subscription.dto;

import com.pulsefit.subscription.model.Subscription;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating a new subscription.
 */
public class SubscriptionRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Plan type is required")
    private Subscription.PlanType planType;

    // ─── Constructors ────────────────────────────────────────────────────────────

    public SubscriptionRequest() {}

    // ─── Getters & Setters ───────────────────────────────────────────────────────

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Subscription.PlanType getPlanType() { return planType; }
    public void setPlanType(Subscription.PlanType planType) { this.planType = planType; }
}
