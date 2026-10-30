package com.pulsefit.member;

import com.pulsefit.member.dto.MemberRequest;
import com.pulsefit.member.model.Member;
import com.pulsefit.member.repository.MemberRepository;
import com.pulsefit.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MemberService.
 * Tests CRUD operations with mocked MemberRepository.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService Unit Tests")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private MemberRequest memberRequest;
    private Member savedMember;

    @BeforeEach
    void setUp() {
        memberRequest = new MemberRequest();
        memberRequest.setName("Alice Johnson");
        memberRequest.setContact("alice@pulsefit.com");
        memberRequest.setEmail("alice@pulsefit.com");
        memberRequest.setPhone("9876543210");
        memberRequest.setHomeFacility("PulseFit Downtown");

        savedMember = new Member(1L, "Alice Johnson", "alice@pulsefit.com",
                "alice@pulsefit.com", "9876543210", "PulseFit Downtown");
    }

    @Test
    @DisplayName("Should create a new member successfully")
    void shouldCreateMemberSuccessfully() {
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        Member result = memberService.createMember(memberRequest);

        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice Johnson");
        assertThat(result.getContact()).isEqualTo("alice@pulsefit.com");

        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("Should return all members")
    void shouldReturnAllMembers() {
        Member member2 = new Member(2L, "Bob Smith", "bob@pulsefit.com",
                null, null, "PulseFit West");
        when(memberRepository.findAll()).thenReturn(List.of(savedMember, member2));

        List<Member> members = memberService.getAllMembers();

        assertThat(members).hasSize(2);
        assertThat(members.get(0).getName()).isEqualTo("Alice Johnson");
        assertThat(members.get(1).getName()).isEqualTo("Bob Smith");
    }

    @Test
    @DisplayName("Should retrieve member by ID successfully")
    void shouldGetMemberById() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(savedMember));

        Member result = memberService.getMemberById(1L);

        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice Johnson");
    }

    @Test
    @DisplayName("Should throw exception when member not found by ID")
    void shouldThrowWhenMemberNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Member not found with ID: 99");
    }

    @Test
    @DisplayName("Should update member details successfully")
    void shouldUpdateMemberSuccessfully() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(savedMember));
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        memberRequest.setName("Alice Updated");
        Member updated = memberService.updateMember(1L, memberRequest);

        assertThat(updated).isNotNull();
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("Should delete member by ID successfully")
    void shouldDeleteMemberSuccessfully() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(savedMember));
        doNothing().when(memberRepository).delete(savedMember);

        assertThatCode(() -> memberService.deleteMember(1L)).doesNotThrowAnyException();
        verify(memberRepository).delete(savedMember);
    }

    @Test
    @DisplayName("Should search members by name")
    void shouldSearchMembersByName() {
        when(memberRepository.findByNameContainingIgnoreCase("alice"))
                .thenReturn(List.of(savedMember));

        List<Member> results = memberService.searchByName("alice");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).containsIgnoringCase("Alice");
    }
}
