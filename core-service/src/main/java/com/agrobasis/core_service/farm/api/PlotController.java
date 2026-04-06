package com.agrobasis.core_service.farm.api;

import com.agrobasis.core_service.farm.api.dto.PlotCreateRequest;
import com.agrobasis.core_service.farm.api.dto.PlotResponse;
import com.agrobasis.core_service.farm.api.dto.PlotUpdateRequest;
import com.agrobasis.core_service.farm.application.PlotService;
import com.agrobasis.core_service.shared.api.doc.ApiStandardErrors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/plot")
@RequiredArgsConstructor
@Tag(name = "Plot", description = "Endpoints para gestão de talhões das fazendas")
@ApiStandardErrors
public class PlotController {

    private final PlotService plotService;

    @Operation(summary = "Cria um novo talhão", description = "Registra um talhão vinculado a uma fazenda existente.")
    @ApiResponse(responseCode = "201", description = "Talhão criado com sucesso")
    @PostMapping
    public ResponseEntity<PlotResponse> createPlot(@Valid @RequestBody PlotCreateRequest request) {
        PlotResponse response = plotService.createPlot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Retorna um talhão", description = "Resgata um talhão armazenado.")
    @ApiResponse(responseCode = "200", description = "Talhão retornado com sucesso")
    @GetMapping("/{id}")
    public ResponseEntity<PlotResponse> getPlot(@PathVariable UUID id){
        PlotResponse plot = plotService.getPlotById(id);
        return ResponseEntity.ok(plot);
    }

    @Operation(summary = "Retorna os talhões de uma fazenda", description = "Busca todos os talhões de acordo com a fazenda informada.")
    @ApiResponse(responseCode = "200", description = "Talhões retornados com sucesso")
    @GetMapping()
    public ResponseEntity<Page<PlotResponse>> listPlots(
            @RequestParam UUID farmId,
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable){
        Page<PlotResponse> plots = plotService.getAllPlotsByOrganization(farmId,pageable);
        return ResponseEntity.ok(plots);
    }

    @Operation(summary = "Atualiza dados do talhão", description = "Busca o talhão de mesmo ID existe no banco e atualiza área e nome.")
    @ApiResponse(responseCode = "200", description = "talhão atualizado com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<PlotResponse> putPlot(
            @PathVariable UUID id,
            @RequestBody PlotUpdateRequest plotUpdateRequest){
        PlotResponse plot = plotService.updatePlot(id, plotUpdateRequest);
        return ResponseEntity.ok(plot);
    }
}