package com.agrobasis.core_service.identity.infrastructure.security;

import com.agrobasis.core_service.identity.domain.UserRole;

import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        UUID organizationId,
        String email,
        UserRole role
) {
}
