package com.agrobasis.core_service.farm.api.dto;

import java.util.UUID;

public record PlotResponse(
        UUID id,
        String name,
        Double hectareArea,
        UUID farmId
) {
}
