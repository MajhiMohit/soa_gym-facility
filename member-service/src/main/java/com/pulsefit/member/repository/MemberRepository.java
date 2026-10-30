package com.pulsefit.member.repository;

import com.pulsefit.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for Member entities.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Search members by name (case-insensitive).
     */
    List<Member> findByNameContainingIgnoreCase(String name);

    /**
     * Find member by contact (phone/email).
     */
    Optional<Member> findByContact(String contact);

    /**
     * Find members belonging to a specific facility.
     */
    List<Member> findByHomeFacility(String homeFacility);

    /**
     * Check if a member exists by contact.
     */
    boolean existsByContact(String contact);
}
