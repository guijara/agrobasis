package com.agrobasis.core_service.plot;

import java.util.UUID;

public record PlotResponseDto(
        UUID id,
        String name,
        Double hectareArea,
        UUID farmId
) {
}
