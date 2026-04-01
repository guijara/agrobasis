package com.agrobasis.core_service.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDto(
        @Schema(description = "Nome do usuário",example = "Guilherme")
        @NotBlank(message = "O nome é obrigatório") String name,
        @Schema(description = "Email do usuário",example = "guilherme@gmail.com")
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido") String email
) {
}
