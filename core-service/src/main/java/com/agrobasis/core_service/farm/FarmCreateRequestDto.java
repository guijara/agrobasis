package com.agrobasis.core_service.farm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record FarmCreateRequestDto(@NotBlank(message = "O nome da fazenda é obrigatório")
             String name,
             @NotBlank(message = "A localização da fazenda é obrigatória")
             String location,
             @NotNull(message = "A área em hectares é obrigatória")
             @Positive(message = "A área deve ser maior que zero")
             Double hectareArea,
             @NotNull(message = "O ID da organização é obrigatório")
             UUID organizationId
) {
}
