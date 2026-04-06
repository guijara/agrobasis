package com.agrobasis.core_service.identity.api;

import com.agrobasis.core_service.identity.api.dto.UserCreateRequest;
import com.agrobasis.core_service.identity.api.dto.UserResponse;
import com.agrobasis.core_service.identity.api.dto.UserUpdateRequest;
import com.agrobasis.core_service.shared.api.doc.ApiStandardErrors;
import com.agrobasis.core_service.identity.application.UserService;
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
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<UserResponse> postUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Busca um usuário pelo ID")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        UserResponse response = userService.findUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista usuários por Organização", description = "Retorna lista paginada de usuários vinculados a uma organização.")
    @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> listUser(
            @RequestParam UUID organizationId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UserResponse> response = userService.findAllUsersByOrganization(organizationId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualiza um usuário",
            description = "Atualiza os dados básicos do usuário. O sistema barra a atualização se o novo e-mail já estiver em uso por outra pessoa."
    )
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> putUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse response = userService.updateUser(id, request);
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