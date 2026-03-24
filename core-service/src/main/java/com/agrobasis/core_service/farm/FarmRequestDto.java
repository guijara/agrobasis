package com.agrobasis.core_service.farm;

import java.util.UUID;

public record FarmRequestDto(String name, String location, Double hectareArea, UUID organizationId) {
}
