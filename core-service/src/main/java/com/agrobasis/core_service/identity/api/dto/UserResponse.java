package com.agrobasis.core_service.identity.api.dto;

import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.identity.domain.UserAccessStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role,
        UserAccessStatus accessStatus,
        UUID organizationId
) {
}
