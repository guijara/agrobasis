package com.agrobasis.core_service.crop;

import com.agrobasis.core_service.config.ApiStandardErrors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
