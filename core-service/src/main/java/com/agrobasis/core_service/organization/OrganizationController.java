package com.agrobasis.core_service.organization;

import lombok.RequiredArgsConstructor;
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
    public OrganizationResponseDto createOrganization(@RequestBody OrganizationCreateRequest dto){
        OrganizationResponseDto organization = organizationService.createOrganization(dto);
        return organization;
    }
}
