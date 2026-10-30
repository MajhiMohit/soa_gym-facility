package com.pulsefit.attendance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign client for inter-service communication with Subscription Service.
 * Uses Eureka service registry for load-balanced discovery.
 *
 * Communication flow: Attendance Service → Subscription Service
 */
@FeignClient(name = "subscription-service", path = "/api/subscriptions")
public interface SubscriptionClient {

    /**
     * Check if a member has an active subscription.
     * Returns a map with 'hasActiveSubscription' boolean.
     *
     * Used before allowing a check-in to ensure the member has a valid plan.
     */
    @GetMapping("/member/{memberId}/check")
    Map<String, Object> checkActiveSubscription(@PathVariable("memberId") Long memberId);
}
