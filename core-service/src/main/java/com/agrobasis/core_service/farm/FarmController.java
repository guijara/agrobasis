package com.agrobasis.core_service.farm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
