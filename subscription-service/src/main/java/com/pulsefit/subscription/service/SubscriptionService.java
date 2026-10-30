package com.pulsefit.subscription.service;

import com.pulsefit.subscription.client.MemberClient;
import com.pulsefit.subscription.dto.MemberDto;
import com.pulsefit.subscription.dto.SubscriptionRequest;
import com.pulsefit.subscription.model.Subscription;
import com.pulsefit.subscription.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for subscription lifecycle management.
 * Validates member existence via Feign client before operations.
 */
@Service
@Transactional
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private MemberClient memberClient;

    // ─── Plan Duration Mapping ────────────────────────────────────────────────────

    private int getPlanDurationDays(Subscription.PlanType planType) {
        return switch (planType) {
            case MONTHLY   -> 30;
            case QUARTERLY -> 90;
            case ANNUAL    -> 365;
        };
    }

    // ─── Create ───────────────────────────────────────────────────────────────────

    /**
     * Create a new subscription for a member.
     * Validates the member exists via Member Service (Feign).
     *
     * @param request subscription creation details
     * @return saved Subscription entity
     */
    public Subscription createSubscription(SubscriptionRequest request) {
        // Inter-service call: validate member exists in Member Service
        MemberDto member = memberClient.getMemberById(request.getMemberId());
        if (member == null) {
            throw new RuntimeException("Member not found with ID: " + request.getMemberId());
        }

        LocalDate today = LocalDate.now();
        int days = getPlanDurationDays(request.getPlanType());

        Subscription subscription = new Subscription();
        subscription.setMemberId(request.getMemberId());
        subscription.setPlanType(request.getPlanType());
        subscription.setStartDate(today);
        subscription.setExpiryDate(today.plusDays(days));
        subscription.setStatus(Subscription.Status.ACTIVE);

        return subscriptionRepository.save(subscription);
    }

    // ─── Read ─────────────────────────────────────────────────────────────────────

    /**
     * Retrieve all subscriptions.
     */
    @Transactional(readOnly = true)
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    /**
     * Retrieve a subscription by ID.
     */
    @Transactional(readOnly = true)
    public Subscription getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with ID: " + id));
    }

    /**
     * Retrieve all subscriptions for a given member.
     */
    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByMemberId(Long memberId) {
        return subscriptionRepository.findByMemberId(memberId);
    }

    /**
     * Get the active subscription for a member (used by Attendance Service).
     */
    @Transactional(readOnly = true)
    public Subscription getActiveSubscription(Long memberId) {
        return subscriptionRepository
                .findByMemberIdAndStatus(memberId, Subscription.Status.ACTIVE)
                .filter(Subscription::isActive)
                .orElseThrow(() -> new RuntimeException(
                        "No active subscription found for member ID: " + memberId));
    }

    /**
     * Check if a member has an active subscription (boolean check for Attendance Service).
     */
    @Transactional(readOnly = true)
    public boolean hasActiveSubscription(Long memberId) {
        return subscriptionRepository.findByMemberIdAndStatus(memberId, Subscription.Status.ACTIVE)
                .filter(Subscription::isActive)
                .isPresent();
    }

    // ─── Renew ────────────────────────────────────────────────────────────────────

    /**
     * Renew an existing subscription.
     * Extends expiry date based on the current plan type.
     * If expired, starts fresh from today. If still active, extends from current expiry.
     *
     * @param id subscription ID to renew
     * @return updated Subscription
     */
    public Subscription renewSubscription(Long id) {
        Subscription sub = getSubscriptionById(id);

        int days = getPlanDurationDays(sub.getPlanType());
        LocalDate baseDate = sub.getExpiryDate().isBefore(LocalDate.now())
                ? LocalDate.now()           // expired: restart from today
                : sub.getExpiryDate();      // active: extend from current expiry

        sub.setExpiryDate(baseDate.plusDays(days));
        sub.setStatus(Subscription.Status.ACTIVE);

        return subscriptionRepository.save(sub);
    }

    /**
     * Cancel a subscription by ID.
     */
    public Subscription cancelSubscription(Long id) {
        Subscription sub = getSubscriptionById(id);
        sub.setStatus(Subscription.Status.CANCELLED);
        return subscriptionRepository.save(sub);
    }

    /**
     * Find all subscriptions expiring within the next N days.
     */
    @Transactional(readOnly = true)
    public List<Subscription> getExpiringSoon(int days) {
        LocalDate cutoff = LocalDate.now().plusDays(days);
        return subscriptionRepository.findByExpiryDateBeforeAndStatus(
                cutoff, Subscription.Status.ACTIVE);
    }
}
