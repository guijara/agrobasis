package com.agrobasis.core_service.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OrganizationCreateRequest(
        @NotBlank(message = "O nome da organização não pode estar vazio")String name,
        @NotBlank(message = "O CNPJ é obrigatório")
        @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$",
        message = "Formato de CNPJ inválido. Ex: 00.000.000/0000-00")String cnpj,
        @NotBlank(message = "A localização não pode estar vazia")String location) {
}
