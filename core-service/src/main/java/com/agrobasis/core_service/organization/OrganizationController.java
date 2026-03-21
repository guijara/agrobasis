package com.agrobasis.core_service.organization;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping()
    public ResponseEntity<OrganizationResponseDto> createOrganization(@Valid @RequestBody OrganizationCreateRequest dto){
        OrganizationResponseDto organization = organizationService.createOrganization(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(organization);
    }
}
