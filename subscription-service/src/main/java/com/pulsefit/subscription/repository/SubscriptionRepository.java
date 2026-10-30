package com.pulsefit.subscription.repository;

import com.pulsefit.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository for Subscription entities.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * Find all subscriptions for a specific member.
     */
    List<Subscription> findByMemberId(Long memberId);

    /**
     * Find the active subscription for a member.
     */
    Optional<Subscription> findByMemberIdAndStatus(Long memberId, Subscription.Status status);

    /**
     * Find all subscriptions expiring on or before a date (for renewal reminders).
     */
    List<Subscription> findByExpiryDateBeforeAndStatus(LocalDate date, Subscription.Status status);

    /**
     * Check if member has any active subscription.
     */
    boolean existsByMemberIdAndStatus(Long memberId, Subscription.Status status);
}
