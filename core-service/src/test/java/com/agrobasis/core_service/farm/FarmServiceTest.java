package com.agrobasis.core_service.farm;

import com.agrobasis.core_service.organization.Organization;
import com.agrobasis.core_service.organization.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.OrganizationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FarmServiceTest {

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private FarmService farmService;

    @Test
    @DisplayName("Should create farm successfully when organization exists")
    void shouldCreateFarmSuccessfully() {
        // Arrange
        UUID orgId = UUID.randomUUID();
        FarmRequestDto request = new FarmRequestDto("Fazenda", "Cuiabá", 1500.50, orgId);

        Organization mockOrg = new Organization();
        mockOrg.setId(orgId);
        mockOrg.setName("AgroTech");

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(mockOrg));

        Farm savedFarm = new Farm();
        savedFarm.setId(UUID.randomUUID());
        savedFarm.setName(request.name());
        savedFarm.setLocation(request.location());
        savedFarm.setHectareArea(request.hectareArea());
        savedFarm.setOrganization(mockOrg);

        // Ensinamos o repositório a salvar a fazenda
        when(farmRepository.save(any(Farm.class))).thenReturn(savedFarm);

        // Act
        FarmResponseDto result = farmService.createFarm(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Fazenda");
        assertThat(result.location()).isEqualTo("Cuiabá");
        assertThat(result.hectareArea()).isEqualTo(1500.50);
        assertThat(result.organizationId()).isEqualTo(orgId);

        verify(organizationRepository, times(1)).findById(orgId);
        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    @DisplayName("Should throw OrganizationNotFoundException when creating farm for invalid organization")
    void shouldThrowExceptionWhenOrganizationDoesNotExist() {
        // Arrange
        UUID invalidOrgId = UUID.randomUUID();
        FarmRequestDto request = new FarmRequestDto("Fazenda", "Cuiabá", 1500.50, invalidOrgId);

        // Ensinamos o repositório que a organização NÃO existe
        when(organizationRepository.findById(invalidOrgId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> farmService.createFarm(request))
                .isInstanceOf(OrganizationNotFoundException.class)
                .hasMessage("Organização não encontrada.");

        // Garantimos que o sistema barrou a operação e NUNCA chamou o save()
        verify(organizationRepository, times(1)).findById(invalidOrgId);
        verify(farmRepository, never()).save(any(Farm.class));
    }
}