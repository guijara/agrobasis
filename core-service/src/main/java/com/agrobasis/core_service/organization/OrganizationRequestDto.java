package com.agrobasis.core_service.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OrganizationRequestDto(
        @NotBlank(message = "O nome da organização não pode estar vazio")
        @Schema(description = "Nome da Organização.", example = "AgroTech") String name,
        @NotBlank(message = "O CNPJ é obrigatório")
        @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$",
        message = "Formato de CNPJ inválido. Ex: 00.000.000/0000-00")
        @Schema(description = "CNPJ válido da organização", example = "11.111.111/0001-11")
        String cnpj,
        @NotBlank(message = "A localização não pode estar vazia")
        @Schema(description = "Cidade onde a Organização está localizada.", example = "Cuiabá")
        String location) {
}
