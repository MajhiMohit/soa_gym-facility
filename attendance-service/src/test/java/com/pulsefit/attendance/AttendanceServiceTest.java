package com.pulsefit.attendance;

import com.pulsefit.attendance.client.MemberClient;
import com.pulsefit.attendance.client.SubscriptionClient;
import com.pulsefit.attendance.dto.CheckInRequest;
import com.pulsefit.attendance.dto.MemberDto;
import com.pulsefit.attendance.model.Attendance;
import com.pulsefit.attendance.repository.AttendanceRepository;
import com.pulsefit.attendance.service.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AttendanceService.
 * Tests check-in flow with mocked Member and Subscription Feign clients.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceService Unit Tests")
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private MemberClient memberClient;

    @Mock
    private SubscriptionClient subscriptionClient;

    @InjectMocks
    private AttendanceService attendanceService;

    private MemberDto memberDto;
    private CheckInRequest checkInRequest;
    private Attendance savedAttendance;

    @BeforeEach
    void setUp() {
        memberDto = new MemberDto(1L, "Alice Johnson", "alice@pulsefit.com",
                "alice@pulsefit.com", "9876543210", "PulseFit Downtown");

        checkInRequest = new CheckInRequest();
        checkInRequest.setMemberId(1L);
        checkInRequest.setFacilityName("PulseFit Downtown");

        savedAttendance = new Attendance(
                1L, 1L, LocalDateTime.now(), "PulseFit Downtown", "Alice Johnson"
        );
    }

    @Test
    @DisplayName("Should successfully check-in member with active subscription")
    void shouldCheckInSuccessfully() {
        when(memberClient.getMemberById(1L)).thenReturn(memberDto);
        when(subscriptionClient.checkActiveSubscription(1L))
                .thenReturn(Map.of("memberId", 1L, "hasActiveSubscription", true));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(savedAttendance);

        Attendance result = attendanceService.checkIn(checkInRequest);

        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getMemberName()).isEqualTo("Alice Johnson");
        assertThat(result.getFacilityName()).isEqualTo("PulseFit Downtown");

        verify(memberClient).getMemberById(1L);
        verify(subscriptionClient).checkActiveSubscription(1L);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Should reject check-in when member has no active subscription")
    void shouldRejectCheckInWithoutActiveSubscription() {
        when(memberClient.getMemberById(1L)).thenReturn(memberDto);
        when(subscriptionClient.checkActiveSubscription(1L))
                .thenReturn(Map.of("memberId", 1L, "hasActiveSubscription", false));

        assertThatThrownBy(() -> attendanceService.checkIn(checkInRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("does not have an active subscription");

        verify(attendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should use member's home facility when none specified in request")
    void shouldUseHomeFacilityWhenNotSpecified() {
        checkInRequest.setFacilityName(null); // no facility in request

        when(memberClient.getMemberById(1L)).thenReturn(memberDto);
        when(subscriptionClient.checkActiveSubscription(1L))
                .thenReturn(Map.of("hasActiveSubscription", true));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(inv -> inv.getArgument(0));

        Attendance result = attendanceService.checkIn(checkInRequest);

        // Should fall back to member's home facility
        assertThat(result.getFacilityName()).isEqualTo("PulseFit Downtown");
    }

    @Test
    @DisplayName("Should return all attendance records")
    void shouldReturnAllAttendance() {
        when(attendanceRepository.findAll()).thenReturn(List.of(savedAttendance));

        List<Attendance> results = attendanceService.getAllAttendance();

        assertThat(results).hasSize(1);
        verify(attendanceRepository).findAll();
    }

    @Test
    @DisplayName("Should return attendance history for a member ordered by most recent")
    void shouldReturnMemberAttendanceHistory() {
        when(attendanceRepository.findByMemberIdOrderByCheckInTimeDesc(1L))
                .thenReturn(List.of(savedAttendance));

        List<Attendance> results = attendanceService.getAttendanceByMemberId(1L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return total visit count for a member")
    void shouldReturnTotalVisitCount() {
        when(attendanceRepository.countByMemberId(1L)).thenReturn(5L);

        long count = attendanceService.getTotalVisits(1L);

        assertThat(count).isEqualTo(5L);
    }
}
