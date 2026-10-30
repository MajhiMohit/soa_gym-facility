package com.pulsefit.subscription.controller;

import com.pulsefit.subscription.dto.SubscriptionRequest;
import com.pulsefit.subscription.model.Subscription;
import com.pulsefit.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Subscription operations.
 * All routes are protected by JWT (enforced at API Gateway).
 *
 * Base URL: /api/subscriptions
 */
@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * POST /api/subscriptions
     * Create a new membership subscription for a member.
     * Validates member exists via Member Service (inter-service call).
     */
    @PostMapping
    public ResponseEntity<Subscription> createSubscription(
            @Valid @RequestBody SubscriptionRequest request) {
        Subscription created = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/subscriptions
     * Retrieve all subscriptions in the system.
     */
    @GetMapping
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    /**
     * GET /api/subscriptions/{id}
     * Retrieve a specific subscription by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

    /**
     * GET /api/subscriptions/member/{memberId}
     * Retrieve all subscriptions for a specific member.
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Subscription>> getByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByMemberId(memberId));
    }

    /**
     * GET /api/subscriptions/member/{memberId}/active
     * Get the active subscription for a member.
     */
    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<Subscription> getActiveSubscription(@PathVariable Long memberId) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscription(memberId));
    }

    /**
     * GET /api/subscriptions/member/{memberId}/check
     * Check if a member has an active subscription (used by Attendance Service).
     */
    @GetMapping("/member/{memberId}/check")
    public ResponseEntity<Map<String, Object>> checkActiveSubscription(@PathVariable Long memberId) {
        boolean hasActive = subscriptionService.hasActiveSubscription(memberId);
        return ResponseEntity.ok(Map.of(
                "memberId", memberId,
                "hasActiveSubscription", hasActive
        ));
    }

    /**
     * PUT /api/subscriptions/{id}/renew
     * Renew an existing subscription. Extends expiry based on current plan.
     */
    @PutMapping("/{id}/renew")
    public ResponseEntity<Subscription> renewSubscription(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.renewSubscription(id));
    }

    /**
     * PUT /api/subscriptions/{id}/cancel
     * Cancel an active subscription.
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Subscription> cancelSubscription(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(id));
    }

    /**
     * GET /api/subscriptions/expiring?days=7
     * Find subscriptions expiring within the next N days.
     */
    @GetMapping("/expiring")
    public ResponseEntity<List<Subscription>> getExpiringSoon(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(subscriptionService.getExpiringSoon(days));
    }

    /**
     * GET /api/subscriptions/health
     * Service health check.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "subscription-service"
        ));
    }
}
