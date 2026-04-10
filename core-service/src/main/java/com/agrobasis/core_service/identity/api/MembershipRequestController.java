package com.agrobasis.core_service.identity.api;

import com.agrobasis.core_service.identity.api.dto.MembershipRequestCreateRequest;
import com.agrobasis.core_service.identity.api.dto.MembershipRequestResponse;
import com.agrobasis.core_service.identity.application.MembershipRequestService;
import com.agrobasis.core_service.identity.infrastructure.security.AuthenticatedUser;
import com.agrobasis.core_service.shared.security.TenantAccessValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/identity/membership-requests")
@RequiredArgsConstructor
public class MembershipRequestController {

    private final MembershipRequestService membershipRequestService;
    private final TenantAccessValidator tenantAccessValidator;

    @PostMapping
    public ResponseEntity<MembershipRequestResponse> createMembershipRequest(
            @Valid @RequestBody MembershipRequestCreateRequest request
    ) {
        MembershipRequestResponse response = membershipRequestService.createMembershipRequest(
                request.userId(), request.organizationId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<MembershipRequestResponse> approveMembershipRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        MembershipRequestResponse response = membershipRequestService.approveMembershipRequest(id, authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<MembershipRequestResponse> rejectMembershipRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        MembershipRequestResponse response = membershipRequestService.rejectMembershipRequest(id, authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<MembershipRequestResponse>> listPendingRequests(
            @RequestParam UUID organizationId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, organizationId);
        return ResponseEntity.ok(membershipRequestService.listPendingRequestsByOrganization(organizationId));
    }
}
