package com.agrobasis.core_service.organization.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationResponse(UUID id,
                                   String name,
                                   String cnpj,
                                   String location,
                                   LocalDateTime createdAt) {
}
