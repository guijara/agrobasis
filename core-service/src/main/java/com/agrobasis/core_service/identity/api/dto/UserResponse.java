package com.agrobasis.core_service.identity.api.dto;

import com.agrobasis.core_service.identity.domain.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role,
        UUID organizationId
) {
}
