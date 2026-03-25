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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
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

        when(organizationRepository.findById(invalidOrgId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> farmService.createFarm(request))
                .isInstanceOf(OrganizationNotFoundException.class)
                .hasMessage("Organização não encontrada.");

        verify(organizationRepository, times(1)).findById(invalidOrgId);
        verify(farmRepository, never()).save(any(Farm.class));
    }

    @Test
    @DisplayName("Should return FarmResponseDto when valid ID is provided")
    void shouldReturnFarmWhenIdExists() {
        // Arrange
        UUID farmId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        Organization mockOrg = new Organization();
        mockOrg.setId(orgId);

        Farm mockFarm = new Farm();
        mockFarm.setId(farmId);
        mockFarm.setName("Fazenda");
        mockFarm.setLocation("Cuiabá");
        mockFarm.setHectareArea(1500.50);
        mockFarm.setOrganization(mockOrg);

        when(farmRepository.findById(farmId)).thenReturn(Optional.of(mockFarm));

        // Act
        FarmResponseDto result = farmService.getFarmById(farmId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(farmId);
        assertThat(result.name()).isEqualTo("Fazenda");
        assertThat(result.organizationId()).isEqualTo(orgId);

        verify(farmRepository, times(1)).findById(farmId);
    }

    @Test
    @DisplayName("Should throw FarmNotFoundException when farm ID does not exist")
    void shouldThrowExceptionWhenFarmDoesNotExist() {
        // Arrange
        UUID invalidId = UUID.randomUUID();
        when(farmRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> farmService.getFarmById(invalidId))
                .isInstanceOf(FarmNotFoundException.class)
                .hasMessage("Fazenda não encontrada.");

        verify(farmRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("Should return paginated list of farms for a specific organization")
    void shouldReturnPaginatedFarmsByOrganization() {
        // Arrange
        UUID orgId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);

        Organization mockOrg = new Organization();
        mockOrg.setId(orgId);

        Farm mockFarm = new Farm();
        mockFarm.setId(UUID.randomUUID());
        mockFarm.setName("Fazenda");
        mockFarm.setLocation("Cuiabá");
        mockFarm.setHectareArea(1500.50);
        mockFarm.setOrganization(mockOrg);

        Page<Farm> pagedFarms = new PageImpl<>(List.of(mockFarm), pageRequest, 1);

        when(farmRepository.findAllByOrganizationId(eq(orgId), any(Pageable.class))).thenReturn(pagedFarms);

        // Act
        Page<FarmResponseDto> result = farmService.getAllFarmsByOrganization(orgId, pageRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Fazenda");

        verify(farmRepository, times(1)).findAllByOrganizationId(eq(orgId), any(Pageable.class));
    }
}