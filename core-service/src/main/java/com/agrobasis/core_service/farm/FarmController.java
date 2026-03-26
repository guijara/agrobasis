package com.agrobasis.core_service.farm;

import com.agrobasis.core_service.config.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/farm")
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;

    @Operation(summary = "Cria uma nova fazenda", description = "Registra uma fazenda vinculada a uma organização existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fazenda criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: área nula ou nome vazio)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Organização dona não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<FarmResponseDto> postFarm(@Valid @RequestBody FarmCreateRequestDto request){
        FarmResponseDto response = farmService.createFarm(request);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
    }

    @Operation(summary = "Busca fazenda por ID", description = "Retorna os detalhes de uma fazenda específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fazenda encontrada"),
            @ApiResponse(responseCode = "404", description = "Fazenda não encontrada")
    })
    @GetMapping("/{organizationId}")
    public ResponseEntity<FarmResponseDto> getFarm(@PathVariable UUID organizationId){
        FarmResponseDto farm = farmService.getFarmById(organizationId);
        return ResponseEntity.ok(farm);
    }

    @Operation(summary = "Lista fazendas por organização", description = "Retorna uma página de fazendas filtrada pelo ID da organização.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    })
    @GetMapping
    public ResponseEntity<Page<FarmResponseDto>> listFarms(
            @RequestParam UUID organizationId,
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable){
        Page<FarmResponseDto> page = farmService.getAllFarmsByOrganization(organizationId,pageable);
        return ResponseEntity.ok(page);
    }
}
