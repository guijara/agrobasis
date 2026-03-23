package com.agrobasis.core_service.organization;

import com.agrobasis.core_service.config.ErrorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
@Tag(name = "Organization", description = "Endpoints para gestão de organizações e fazendas")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "Cria uma nova organização", description = "Registra uma nova organização no sistema com validação de unicidade de CNPJ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organização criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos fornecidos no payload", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito: CNPJ já cadastrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping()
    public ResponseEntity<OrganizationResponseDto> createOrganization(@Valid @RequestBody OrganizationCreateRequest dto){
        OrganizationResponseDto organization = organizationService.createOrganization(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(organization);
    }
}
