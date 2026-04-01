package com.agrobasis.core_service.user;

import com.agrobasis.core_service.config.ApiStandardErrors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@ApiStandardErrors
@Tag(name = "Usuários", description = "Endpoints para gestão de usuários do sistema")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Registra um novo usuário")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @PostMapping
    public ResponseEntity<UserResponseDto> postUser(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Busca um usuário pelo ID")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable UUID id) {
        UserResponseDto response = userService.findUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista usuários por Organização", description = "Retorna lista paginada de usuários vinculados a uma organização.")
    @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso")
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> listUser(
            @RequestParam UUID organizationId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UserResponseDto> response = userService.findAllUsersByOrganization(organizationId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualiza um usuário",
            description = "Atualiza os dados básicos do usuário. O sistema barra a atualização se o novo e-mail já estiver em uso por outra pessoa."
    )
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> putUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequestDto request
    ) {
        UserResponseDto response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deleta um usuário", description = "Remove o usuário do sistema através do seu ID.")
    @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}