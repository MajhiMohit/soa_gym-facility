package com.pulsefit.attendance.controller;

import com.pulsefit.attendance.dto.CheckInRequest;
import com.pulsefit.attendance.model.Attendance;
import com.pulsefit.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Attendance check-in operations.
 * All routes are protected by JWT (enforced at API Gateway).
 *
 * Base URL: /api/attendance
 */
@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * POST /api/attendance/checkin
     * Record a member check-in at a PulseFit facility.
     * Validates member existence and active subscription via inter-service calls.
     */
    @PostMapping("/checkin")
    public ResponseEntity<Attendance> checkIn(@Valid @RequestBody CheckInRequest request) {
        Attendance attendance = attendanceService.checkIn(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
    }

    /**
     * GET /api/attendance
     * Retrieve all attendance records in the system.
     */
    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    /**
     * GET /api/attendance/{id}
     * Retrieve a specific attendance record by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    /**
     * GET /api/attendance/member/{memberId}
     * Retrieve attendance history for a specific member (most recent first).
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Attendance>> getAttendanceByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByMemberId(memberId));
    }

    /**
     * GET /api/attendance/member/{memberId}/count
     * Get total visit count for a member.
     */
    @GetMapping("/member/{memberId}/count")
    public ResponseEntity<Map<String, Object>> getTotalVisits(@PathVariable Long memberId) {
        long count = attendanceService.getTotalVisits(memberId);
        return ResponseEntity.ok(Map.of(
                "memberId", memberId,
                "totalVisits", count
        ));
    }

    /**
     * GET /api/attendance/facility/{facilityName}
     * Get all check-ins at a specific facility.
     */
    @GetMapping("/facility/{facilityName}")
    public ResponseEntity<List<Attendance>> getByFacility(@PathVariable String facilityName) {
        return ResponseEntity.ok(attendanceService.getAttendanceByFacility(facilityName));
    }

    /**
     * GET /api/attendance/health
     * Service health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "attendance-service"
        ));
    }
}
