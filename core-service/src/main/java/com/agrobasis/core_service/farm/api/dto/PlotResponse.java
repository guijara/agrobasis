package com.agrobasis.core_service.farm.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;

import java.util.UUID;

public record PlotResponse(
        UUID id,
        String name,
        Double hectareArea,
        Commodity commodity,
        UUID farmId
) {
}
