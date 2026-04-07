package com.agrobasis.core_service.cost.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CostProfileResponse(
        UUID id,
        UUID organizationId,
        Commodity commodity,
        BigDecimal costPerTon,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
