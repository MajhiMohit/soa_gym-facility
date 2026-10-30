package com.pulsefit.subscription;

import com.pulsefit.subscription.client.MemberClient;
import com.pulsefit.subscription.dto.MemberDto;
import com.pulsefit.subscription.dto.SubscriptionRequest;
import com.pulsefit.subscription.model.Subscription;
import com.pulsefit.subscription.repository.SubscriptionRepository;
import com.pulsefit.subscription.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SubscriptionService.
 * Tests subscription creation with inter-service Feign mock, renewal logic, and cancellation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionService Unit Tests")
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private MemberClient memberClient;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private MemberDto memberDto;
    private Subscription activeSubscription;

    @BeforeEach
    void setUp() {
        memberDto = new MemberDto(1L, "Alice Johnson", "alice@pulsefit.com",
                "alice@pulsefit.com", "9876543210", "PulseFit Downtown");

        activeSubscription = new Subscription(
                1L, 1L,
                Subscription.PlanType.MONTHLY,
                LocalDate.now().plusDays(15),  // still active
                LocalDate.now().minusDays(15),
                Subscription.Status.ACTIVE
        );
    }

    @Test
    @DisplayName("Should create MONTHLY subscription successfully after member validation")
    void shouldCreateMonthlySubscription() {
        when(memberClient.getMemberById(1L)).thenReturn(memberDto);
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(inv -> {
                    Subscription s = inv.getArgument(0);
                    s.setSubscriptionId(1L);
                    return s;
                });

        SubscriptionRequest request = new SubscriptionRequest();
        request.setMemberId(1L);
        request.setPlanType(Subscription.PlanType.MONTHLY);

        Subscription result = subscriptionService.createSubscription(request);

        assertThat(result).isNotNull();
        assertThat(result.getPlanType()).isEqualTo(Subscription.PlanType.MONTHLY);
        assertThat(result.getStatus()).isEqualTo(Subscription.Status.ACTIVE);
        assertThat(result.getExpiryDate()).isEqualTo(LocalDate.now().plusDays(30));

        verify(memberClient).getMemberById(1L);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("Should create ANNUAL subscription with 365-day expiry")
    void shouldCreateAnnualSubscription() {
        when(memberClient.getMemberById(1L)).thenReturn(memberDto);
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        SubscriptionRequest request = new SubscriptionRequest();
        request.setMemberId(1L);
        request.setPlanType(Subscription.PlanType.ANNUAL);

        Subscription result = subscriptionService.createSubscription(request);

        assertThat(result.getExpiryDate()).isEqualTo(LocalDate.now().plusDays(365));
    }

    @Test
    @DisplayName("Should renew active subscription by extending current expiry")
    void shouldRenewActiveSubscription() {
        LocalDate originalExpiry = LocalDate.now().plusDays(15);
        activeSubscription.setExpiryDate(originalExpiry);

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(activeSubscription));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Subscription renewed = subscriptionService.renewSubscription(1L);

        // Should extend 30 days FROM current expiry (not today)
        assertThat(renewed.getExpiryDate()).isEqualTo(originalExpiry.plusDays(30));
        assertThat(renewed.getStatus()).isEqualTo(Subscription.Status.ACTIVE);
    }

    @Test
    @DisplayName("Should renew expired subscription from today")
    void shouldRenewExpiredSubscriptionFromToday() {
        Subscription expired = new Subscription(
                2L, 1L,
                Subscription.PlanType.QUARTERLY,
                LocalDate.now().minusDays(5),  // expired 5 days ago
                LocalDate.now().minusDays(95),
                Subscription.Status.ACTIVE
        );

        when(subscriptionRepository.findById(2L)).thenReturn(Optional.of(expired));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Subscription renewed = subscriptionService.renewSubscription(2L);

        // Should extend 90 days FROM today (since it was expired)
        assertThat(renewed.getExpiryDate()).isEqualTo(LocalDate.now().plusDays(90));
    }

    @Test
    @DisplayName("Should cancel subscription by setting status to CANCELLED")
    void shouldCancelSubscription() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(activeSubscription));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Subscription cancelled = subscriptionService.cancelSubscription(1L);

        assertThat(cancelled.getStatus()).isEqualTo(Subscription.Status.CANCELLED);
    }

    @Test
    @DisplayName("Should confirm active subscription status via hasActiveSubscription")
    void shouldConfirmActiveSubscription() {
        when(subscriptionRepository.findByMemberIdAndStatus(1L, Subscription.Status.ACTIVE))
                .thenReturn(Optional.of(activeSubscription));

        boolean result = subscriptionService.hasActiveSubscription(1L);

        assertThat(result).isTrue();
    }
}
