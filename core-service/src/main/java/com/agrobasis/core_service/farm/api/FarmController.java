package com.agrobasis.core_service.farm.api;

import com.agrobasis.core_service.farm.api.dto.FarmCreateRequest;
import com.agrobasis.core_service.farm.api.dto.FarmResponse;
import com.agrobasis.core_service.farm.api.dto.FarmUpdateRequest;
import com.agrobasis.core_service.farm.application.FarmService;
import com.agrobasis.core_service.shared.api.doc.ApiStandardErrors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@ApiStandardErrors
public class FarmController {

    private final FarmService farmService;

    @Operation(summary = "Cria uma nova fazenda", description = "Registra uma fazenda vinculada a uma organização existente.")
    @ApiResponse(responseCode = "201", description = "Fazenda criada com sucesso")
    @PostMapping
    public ResponseEntity<FarmResponse> postFarm(@Valid @RequestBody FarmCreateRequest request){
        FarmResponse response = farmService.createFarm(request);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
    }

    @Operation(summary = "Busca fazenda por ID", description = "Retorna os detalhes de uma fazenda específica.")
    @ApiResponse(responseCode = "200", description = "Fazenda encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<FarmResponse> getFarm(@PathVariable UUID id){
        FarmResponse farm = farmService.getFarmById(id);
        return ResponseEntity.ok(farm);
    }

    @Operation(summary = "Lista fazendas por organização", description = "Retorna uma página de fazendas filtrada pelo ID da organização.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    @GetMapping
    public ResponseEntity<Page<FarmResponse>> listFarms(
            @RequestParam UUID organizationId,
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable){
        Page<FarmResponse> page = farmService.getAllFarmsByOrganization(organizationId,pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Atualiza uma fazenda", description = "Altera os dados (nome, localização ou área) de uma fazenda existente buscando pelo seu ID.")
    @ApiResponse(responseCode = "200", description = "Fazenda atualizada com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<FarmResponse> putFarm(
            @PathVariable UUID id,
            @Valid @RequestBody FarmUpdateRequest request){
        FarmResponse farm = farmService.updateFarm(id,request);
        return ResponseEntity.ok(farm);
    }
}
