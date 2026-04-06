package com.agrobasis.core_service.identity.api.dto;

import com.agrobasis.core_service.identity.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserCreateRequest(
        @Schema(description = "Nome do usuário",example = "Guilherme")
        @NotBlank(message = "O nome é obrigatório") String name,
        @Schema(description = "Email do usuário",example = "guilherme@gmail.com")
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido") String email,
        @Schema(description = "Senha do usuário",example = "SenhaForte123")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        @NotBlank(message = "A senha é obrigatória") String password,
        @Schema(description = "Nível de acesso no sistema", example = "ADMIN")
        @NotNull(message = "O nível de acesso (role) é obrigatório")
        UserRole role,
        @Schema(description = "ID da organização à qual o usuário pertence")
        @NotNull(message = "O ID da organização é obrigatório") UUID organizationId
) {
}
