package com.agrobasis.core_service.identity.api.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @Schema(description = "Nome do usuário",example = "Guilherme")
        @NotBlank(message = "O nome é obrigatório") String name,
        @Schema(description = "Email do usuário",example = "guilherme@gmail.com")
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido") String email,
        @Schema(description = "Senha do usuário",example = "SenhaForte123")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        @NotBlank(message = "A senha é obrigatória") String password
) {
}
