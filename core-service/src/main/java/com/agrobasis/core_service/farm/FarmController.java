package com.agrobasis.core_service.farm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/farm")
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;

    @PostMapping
    public ResponseEntity<FarmResponseDto> postFarm(@Valid @RequestBody FarmRequestDto request){
        FarmResponseDto response = farmService.createFarm(request);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<FarmResponseDto> getFarm(@PathVariable UUID organizationId){
        FarmResponseDto farm = farmService.getFarmById(organizationId);
        return new ResponseEntity.ok(farm);
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<Page<FarmResponseDto>> listFarms(@PathVariable UUID organizationId){
        Page<FarmResponseDto> page = farmService.getAllFarmsByOrganization(organizationId,);
        return new ResponseEntity.ok(page)
    }
}
