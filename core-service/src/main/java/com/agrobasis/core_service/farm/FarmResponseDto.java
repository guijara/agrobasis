package com.agrobasis.core_service.farm;

import java.util.UUID;

public record FarmResponseDto(UUID id, String name, String location, Double hectareArea, UUID organizationId) {
}
