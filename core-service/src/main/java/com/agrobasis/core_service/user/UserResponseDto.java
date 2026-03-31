package com.agrobasis.core_service.user;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String email,
        UserRole role,
        UUID organizationId
) {
}
