package com.pulsefit.member.controller;

import com.pulsefit.member.dto.MemberRequest;
import com.pulsefit.member.model.Member;
import com.pulsefit.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Member operations.
 * All routes are protected by JWT (enforced at API Gateway).
 *
 * Base URL: /api/members
 */
@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * POST /api/members
     * Create a new fitness club member.
     */
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody MemberRequest request) {
        Member created = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/members
     * Retrieve all registered members.
     * Optional: filter by name using ?name=query or by facility using ?facility=name
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String facility) {

        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(memberService.searchByName(name));
        }
        if (facility != null && !facility.isBlank()) {
            return ResponseEntity.ok(memberService.getMembersByFacility(facility));
        }
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    /**
     * GET /api/members/{id}
     * Retrieve a specific member by their ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    /**
     * PUT /api/members/{id}
     * Update an existing member's information.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    /**
     * DELETE /api/members/{id}
     * Remove a member from the system.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(Map.of(
                "message", "Member with ID " + id + " deleted successfully"
        ));
    }

    /**
     * GET /api/members/health
     * Service health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "member-service"
        ));
    }
}
