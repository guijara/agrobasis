package com.agrobasis.core_service.organization;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationResponseDto(UUID id,
                                      String name,
                                      String cnpj,
                                      String location,
                                      LocalDateTime createdAt) {
}
