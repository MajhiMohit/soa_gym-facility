package com.pulsefit.attendance.client;

import com.pulsefit.attendance.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for inter-service communication with Member Service.
 * Uses Eureka service registry for load-balanced discovery.
 *
 * Communication flow: Attendance Service → Member Service
 */
@FeignClient(name = "member-service", path = "/api/members")
public interface MemberClient {

    /**
     * Fetch member details by ID from Member Service.
     * Used to validate member exists and get member name for attendance record.
     */
    @GetMapping("/{id}")
    MemberDto getMemberById(@PathVariable("id") Long id);
}
