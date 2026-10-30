package com.pulsefit.attendance.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Attendance entity representing a member check-in event.
 * Stored in the attendance-service H2 database.
 */
@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "facility_name", length = 100)
    private String facilityName;

    @Column(name = "member_name", length = 100)
    private String memberName;

    // ─── Constructors ────────────────────────────────────────────────────────────

    public Attendance() {}

    public Attendance(Long attendanceId, Long memberId, LocalDateTime checkInTime,
                      String facilityName, String memberName) {
        this.attendanceId = attendanceId;
        this.memberId = memberId;
        this.checkInTime = checkInTime;
        this.facilityName = facilityName;
        this.memberName = memberName;
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────

    public Long getAttendanceId() { return attendanceId; }
    public Long getMemberId() { return memberId; }
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public String getFacilityName() { return facilityName; }
    public String getMemberName() { return memberName; }

    // ─── Setters ─────────────────────────────────────────────────────────────────

    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    @Override
    public String toString() {
        return "Attendance{id=" + attendanceId + ", memberId=" + memberId
                + ", checkInTime=" + checkInTime + ", facility='" + facilityName + "'}";
    }
}
