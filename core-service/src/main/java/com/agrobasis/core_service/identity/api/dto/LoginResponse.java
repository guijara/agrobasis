package com.agrobasis.core_service.identity.api.dto;

import com.agrobasis.core_service.identity.domain.UserRole;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        UUID userId,
        UUID organizationId,
        UserRole role
) {
}
