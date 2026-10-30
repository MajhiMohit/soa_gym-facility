package com.pulsefit.subscription.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Subscription entity representing a member's membership plan.
 * Stored in the subscription-service H2 database.
 */
@Entity
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, length = 50)
    private PlanType planType;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    public enum PlanType {
        MONTHLY,    // 30 days
        QUARTERLY,  // 90 days
        ANNUAL      // 365 days
    }

    public enum Status {
        ACTIVE,
        EXPIRED,
        CANCELLED
    }

    // ─── Constructors ────────────────────────────────────────────────────────────

    public Subscription() {}

    public Subscription(Long subscriptionId, Long memberId, PlanType planType,
                        LocalDate expiryDate, LocalDate startDate, Status status) {
        this.subscriptionId = subscriptionId;
        this.memberId = memberId;
        this.planType = planType;
        this.expiryDate = expiryDate;
        this.startDate = startDate;
        this.status = status;
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────

    public Long getSubscriptionId() { return subscriptionId; }
    public Long getMemberId() { return memberId; }
    public PlanType getPlanType() { return planType; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public LocalDate getStartDate() { return startDate; }
    public Status getStatus() { return status; }

    // ─── Setters ─────────────────────────────────────────────────────────────────

    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public void setPlanType(PlanType planType) { this.planType = planType; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setStatus(Status status) { this.status = status; }

    /**
     * Convenience method: check if subscription is currently active.
     */
    public boolean isActive() {
        return Status.ACTIVE.equals(this.status) &&
               !this.expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Subscription{id=" + subscriptionId + ", memberId=" + memberId
                + ", plan=" + planType + ", expiry=" + expiryDate + ", status=" + status + "}";
    }
}
