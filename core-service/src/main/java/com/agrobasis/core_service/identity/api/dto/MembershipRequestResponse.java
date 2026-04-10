package com.agrobasis.core_service.identity.api.dto;

import com.agrobasis.core_service.identity.domain.MembershipRequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MembershipRequestResponse(
        UUID id,
        UUID userId,
        UUID organizationId,
        MembershipRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt
) {
}
