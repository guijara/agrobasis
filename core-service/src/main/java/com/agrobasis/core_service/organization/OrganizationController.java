package com.agrobasis.core_service.organization;

import com.agrobasis.core_service.config.ApiStandardErrors;
import com.agrobasis.core_service.config.ErrorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
@Tag(name = "Organization", description = "Endpoints para gestão de organizações e fazendas")
@ApiStandardErrors
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "Cria uma nova organização", description = "Registra uma nova organização no sistema com validação de unicidade de CNPJ.")
    @ApiResponse(responseCode = "201", description = "Organização criada com sucesso")
    @ApiResponse(responseCode = "409", description = "Conflito: CNPJ já cadastrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping()
    public ResponseEntity<OrganizationResponseDto> createOrganization(@Valid @RequestBody OrganizationRequestDto dto){
        OrganizationResponseDto organization = organizationService.createOrganization(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(organization);
    }

    @Operation(summary = "Lista organizações paginadas", description = "Retorna uma página de organizações com suporte a ordenação.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    @GetMapping
    public ResponseEntity<Page<OrganizationResponseDto>> getAllOrganizations(
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<OrganizationResponseDto> organizations = organizationService.getAllOrganizations(pageable);
        return ResponseEntity.ok(organizations);
    }
}