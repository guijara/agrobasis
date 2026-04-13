package com.agrobasis.core_service.cost.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommercialAdjustmentProfileResponse(
        UUID id,
        UUID organizationId,
        UUID farmId,
        Commodity commodity,
        BigDecimal adjustmentPerTon,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
