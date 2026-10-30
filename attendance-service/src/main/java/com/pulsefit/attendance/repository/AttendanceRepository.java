package com.pulsefit.attendance.repository;

import com.pulsefit.attendance.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA repository for Attendance entities.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * Find all check-ins for a specific member, ordered by most recent.
     */
    List<Attendance> findByMemberIdOrderByCheckInTimeDesc(Long memberId);

    /**
     * Find check-ins for a member within a date range.
     */
    List<Attendance> findByMemberIdAndCheckInTimeBetween(
            Long memberId, LocalDateTime start, LocalDateTime end);

    /**
     * Find all check-ins at a specific facility.
     */
    List<Attendance> findByFacilityName(String facilityName);

    /**
     * Count total visits for a member.
     */
    long countByMemberId(Long memberId);
}
