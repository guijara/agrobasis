package com.agrobasis.core_service.plot;

import com.agrobasis.core_service.config.ApiStandardErrors;
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
    public ResponseEntity<PlotResponseDto> createPlot(@Valid @RequestBody PlotCreateRequestDto request) {
        PlotResponseDto response = plotService.createPlot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Retorna um talhão", description = "Resgata um talhão armazenado.")
    @ApiResponse(responseCode = "200", description = "Talhão retornado com sucesso")
    @GetMapping("/{id}")
    public ResponseEntity<PlotResponseDto> getPlot(@PathVariable UUID id){
        PlotResponseDto plot = plotService.getPlotById(id);
        return ResponseEntity.ok(plot);
    }

    @Operation(summary = "Retorna os talhões de uma fazenda", description = "Busca todos os talhões de acordo com a fazenda informada.")
    @ApiResponse(responseCode = "200", description = "Talhões retornados com sucesso")
    @GetMapping()
    public ResponseEntity<Page<PlotResponseDto>> listPlots(
            @RequestParam UUID farmId,
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable){
        Page<PlotResponseDto> plots = plotService.getAllPlotsByOrganization(farmId,pageable);
        return ResponseEntity.ok(plots);
    }

    @Operation(summary = "Atualiza dados do talhão", description = "Busca o talhão de mesmo ID existe no banco e atualiza área e nome.")
    @ApiResponse(responseCode = "200", description = "talhão atualizado com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<PlotResponseDto> putPlot(
            @PathVariable UUID id,
            @RequestBody PlotUpdateRequestDto plotUpdateRequestDto){
        PlotResponseDto plot = plotService.updatePlot(id,plotUpdateRequestDto);
        return ResponseEntity.ok(plot);
    }
}