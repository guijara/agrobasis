package com.agrobasis.core_service.identity.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MembershipRequestCreateRequest(
        @NotNull(message = "O ID do usuário é obrigatório")
        UUID userId,
        @NotNull(message = "O ID da organização é obrigatório")
        UUID organizationId
) {
}
