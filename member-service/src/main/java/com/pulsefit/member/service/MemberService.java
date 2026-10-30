package com.pulsefit.member.service;

import com.pulsefit.member.dto.MemberRequest;
import com.pulsefit.member.model.Member;
import com.pulsefit.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic layer for Member operations.
 * Provides CRUD operations and search functionality.
 */
@Service
@Transactional
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Create a new member.
     */
    public Member createMember(MemberRequest request) {
        Member member = new Member();
        mapRequestToMember(request, member);
        return memberRepository.save(member);
    }

    /**
     * Retrieve all members.
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    /**
     * Retrieve a member by ID. Throws exception if not found.
     */
    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + id));
    }

    /**
     * Update an existing member's details.
     */
    public Member updateMember(Long id, MemberRequest request) {
        Member member = getMemberById(id);
        mapRequestToMember(request, member);
        return memberRepository.save(member);
    }

    /**
     * Delete a member by ID.
     */
    public void deleteMember(Long id) {
        Member member = getMemberById(id);
        memberRepository.delete(member);
    }

    /**
     * Search members by name (partial, case-insensitive).
     */
    @Transactional(readOnly = true)
    public List<Member> searchByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Find members by home facility.
     */
    @Transactional(readOnly = true)
    public List<Member> getMembersByFacility(String facility) {
        return memberRepository.findByHomeFacility(facility);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────────

    private void mapRequestToMember(MemberRequest request, Member member) {
        member.setName(request.getName());
        member.setContact(request.getContact());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setHomeFacility(request.getHomeFacility());
    }
}
