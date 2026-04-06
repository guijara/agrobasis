package com.agrobasis.core_service.farm.api.dto;

import java.util.UUID;

public record FarmResponse(UUID id, String name,
                           String location, Double hectareArea,
                           UUID organizationId) {
}
