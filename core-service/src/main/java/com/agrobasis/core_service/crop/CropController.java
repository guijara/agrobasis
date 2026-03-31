package com.agrobasis.core_service.crop;

import com.agrobasis.core_service.config.ApiStandardErrors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/crop")
@RequiredArgsConstructor
@ApiStandardErrors
public class CropController {

    private final CropService cropService;

    @Operation(
            summary = "Registra uma nova safra",
            description = "Cria um ciclo de cultivo vinculado a um talhão, validando se não há sobreposição de datas."
    )
    @ApiResponse(responseCode = "201", description = "Safra criada com sucesso")
    @PostMapping
    public ResponseEntity<CropResponseDto> createCrop(@Valid @RequestBody CropRequestDto request) {
        CropResponseDto response = cropService.createCrop(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Busca uma safra pelo ID")
    @ApiResponse(responseCode = "200", description = "Safra encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<CropResponseDto> getCrop(@PathVariable UUID id){
        CropResponseDto response = cropService.getCropById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista as safras de um talhão", description = "Retorna safras paginadas. Padrão: mais recentes primeiro.")
    @ApiResponse(responseCode = "200", description = "Listagem realizada")
    @GetMapping("/{plotId}")
    public ResponseEntity<Page<CropResponseDto>> listCrop(@RequestParam UUID plotId,
              @ParameterObject @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable){
        Page<CropResponseDto> response = cropService.ListCropByPlot(plotId,pageable);
        return ResponseEntity.ok(response);
    }
}
