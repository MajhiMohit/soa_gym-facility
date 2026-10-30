package com.pulsefit.subscription.client;

import com.pulsefit.subscription.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for inter-service communication with Member Service.
 * Uses Eureka service name for load-balanced discovery.
 *
 * Communication flow: Subscription Service → Member Service
 */
@FeignClient(name = "member-service", path = "/api/members")
public interface MemberClient {

    /**
     * Fetch a member by ID from the Member Service.
     * Used to validate member existence before creating a subscription.
     */
    @GetMapping("/{id}")
    MemberDto getMemberById(@PathVariable("id") Long id);
}
