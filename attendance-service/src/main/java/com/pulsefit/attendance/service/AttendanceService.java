package com.pulsefit.attendance.service;

import com.pulsefit.attendance.client.MemberClient;
import com.pulsefit.attendance.client.SubscriptionClient;
import com.pulsefit.attendance.dto.CheckInRequest;
import com.pulsefit.attendance.dto.MemberDto;
import com.pulsefit.attendance.model.Attendance;
import com.pulsefit.attendance.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Business logic for attendance check-in operations.
 *
 * Check-in validation chain (inter-service):
 *   1. Verify member exists → Member Service
 *   2. Verify active subscription → Subscription Service
 *   3. Record check-in → local H2 DB
 */
@Service
@Transactional
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private MemberClient memberClient;

    @Autowired
    private SubscriptionClient subscriptionClient;

    /**
     * Process a member check-in at a PulseFit facility.
     *
     * Performs inter-service validation:
     *   - Calls Member Service to verify member exists
     *   - Calls Subscription Service to verify active membership
     *
     * @param request check-in details (memberId, facilityName)
     * @return saved Attendance record
     */
    public Attendance checkIn(CheckInRequest request) {
        // Step 1: Validate member exists (calls Member Service via Feign)
        MemberDto member = memberClient.getMemberById(request.getMemberId());
        if (member == null) {
            throw new RuntimeException("Member not found with ID: " + request.getMemberId());
        }

        // Step 2: Validate active subscription (calls Subscription Service via Feign)
        Map<String, Object> subscriptionCheck =
                subscriptionClient.checkActiveSubscription(request.getMemberId());

        boolean hasActiveSubscription = Boolean.TRUE.equals(
                subscriptionCheck.get("hasActiveSubscription"));

        if (!hasActiveSubscription) {
            throw new RuntimeException(
                    "Member " + member.getName() +
                    " does not have an active subscription. Please renew before entry.");
        }

        // Step 3: Record check-in
        Attendance attendance = new Attendance();
        attendance.setMemberId(request.getMemberId());
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setFacilityName(request.getFacilityName() != null
                ? request.getFacilityName()
                : member.getHomeFacility());
        attendance.setMemberName(member.getName());

        return attendanceRepository.save(attendance);
    }

    /**
     * Get all attendance records.
     */
    @Transactional(readOnly = true)
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    /**
     * Get attendance history for a specific member (most recent first).
     */
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByMemberId(Long memberId) {
        return attendanceRepository.findByMemberIdOrderByCheckInTimeDesc(memberId);
    }

    /**
     * Get a specific attendance record by ID.
     */
    @Transactional(readOnly = true)
    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance record not found with ID: " + id));
    }

    /**
     * Get all check-ins at a specific facility.
     */
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByFacility(String facilityName) {
        return attendanceRepository.findByFacilityName(facilityName);
    }

    /**
     * Count total visits for a member.
     */
    @Transactional(readOnly = true)
    public long getTotalVisits(Long memberId) {
        return attendanceRepository.countByMemberId(memberId);
    }
}
