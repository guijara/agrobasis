package com.agrobasis.core_service.farm.application;

import com.agrobasis.core_service.farm.api.dto.FarmCreateRequest;
import com.agrobasis.core_service.farm.api.dto.FarmResponse;
import com.agrobasis.core_service.farm.api.dto.FarmUpdateRequest;
import com.agrobasis.core_service.farm.domain.Farm;
import com.agrobasis.core_service.farm.domain.exception.FarmNotFoundException;
import com.agrobasis.core_service.farm.infrastructure.FarmRepository;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FarmServiceTest {

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private FarmService farmService;

    @Nested
    @DisplayName("createFarm()")
    class CreateFarmTests {

        @Test
        @DisplayName("Should create farm successfully")
        void shouldCreateFarmSuccessfully() {
            UUID organizationId = UUID.randomUUID();
            FarmCreateRequest request = new FarmCreateRequest(
                    "Fazenda Boa Terra",
                    "Cuiaba",
                    1500.5,
                    organizationId
            );

            Organization organization = new Organization();
            organization.setId(organizationId);

            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(farmRepository.save(any(Farm.class))).thenAnswer(invocation -> {
                Farm farm = invocation.getArgument(0);
                farm.setId(UUID.randomUUID());
                return farm;
            });

            FarmResponse result = farmService.createFarm(request);

            assertThat(result.id()).isNotNull();
            assertThat(result.name()).isEqualTo("Fazenda Boa Terra");
            assertThat(result.location()).isEqualTo("Cuiaba");
            assertThat(result.hectareArea()).isEqualTo(1500.5);
            assertThat(result.organizationId()).isEqualTo(organizationId);

            verify(organizationRepository).findById(organizationId);
            verify(farmRepository).save(any(Farm.class));
        }

        @Test
        @DisplayName("Should throw exception when organization is not found")
        void shouldThrowExceptionWhenOrganizationIsNotFound() {
            UUID organizationId = UUID.randomUUID();
            FarmCreateRequest request = new FarmCreateRequest("Fazenda Boa Terra", "Cuiaba", 1500.5, organizationId);

            when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.createFarm(request))
                    .isInstanceOf(OrganizationNotFoundException.class)
                    .hasMessage("Organização não encontrada.");

            verify(farmRepository, never()).save(any(Farm.class));
        }
    }

    @Nested
    @DisplayName("getFarmById()")
    class GetFarmByIdTests {

        @Test
        @DisplayName("Should return farm when ID exists")
        void shouldReturnFarmWhenIdExists() {
            UUID farmId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();

            Organization organization = new Organization();
            organization.setId(organizationId);

            Farm farm = new Farm();
            farm.setId(farmId);
            farm.setName("Fazenda Boa Terra");
            farm.setLocation("Cuiaba");
            farm.setHectareArea(1500.5);
            farm.setOrganization(organization);

            when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));

            FarmResponse result = farmService.getFarmById(farmId);

            assertThat(result.id()).isEqualTo(farmId);
            assertThat(result.name()).isEqualTo("Fazenda Boa Terra");
            assertThat(result.location()).isEqualTo("Cuiaba");
            assertThat(result.hectareArea()).isEqualTo(1500.5);
            assertThat(result.organizationId()).isEqualTo(organizationId);
        }

        @Test
        @DisplayName("Should throw exception when farm is not found by ID")
        void shouldThrowExceptionWhenFarmIsNotFoundById() {
            UUID farmId = UUID.randomUUID();

            when(farmRepository.findById(farmId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.getFarmById(farmId))
                    .isInstanceOf(FarmNotFoundException.class)
                    .hasMessage("Fazenda não encontrada.");
        }
    }

    @Nested
    @DisplayName("getAllFarmsByOrganization()")
    class GetAllFarmsByOrganizationTests {

        @Test
        @DisplayName("Should return paginated farms by organization")
        void shouldReturnPaginatedFarmsByOrganization() {
            UUID farmId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Organization organization = new Organization();
            organization.setId(organizationId);

            Farm farm = new Farm();
            farm.setId(farmId);
            farm.setName("Fazenda Boa Terra");
            farm.setLocation("Cuiaba");
            farm.setHectareArea(1500.5);
            farm.setOrganization(organization);

            Page<Farm> farms = new PageImpl<>(List.of(farm), pageable, 1);

            when(farmRepository.findAllByOrganizationId(organizationId, pageable)).thenReturn(farms);

            Page<FarmResponse> result = farmService.getAllFarmsByOrganization(organizationId, pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().id()).isEqualTo(farmId);
            assertThat(result.getContent().getFirst().organizationId()).isEqualTo(organizationId);
            verify(farmRepository).findAllByOrganizationId(organizationId, pageable);
        }
    }

    @Nested
    @DisplayName("updateFarm()")
    class UpdateFarmTests {

        @Test
        @DisplayName("Should update farm successfully")
        void shouldUpdateFarmSuccessfully() {
            UUID farmId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            FarmUpdateRequest request = new FarmUpdateRequest("Fazenda Nova", "Rondonopolis", 1750.0);

            Organization organization = new Organization();
            organization.setId(organizationId);

            Farm farm = new Farm();
            farm.setId(farmId);
            farm.setName("Fazenda Antiga");
            farm.setLocation("Cuiaba");
            farm.setHectareArea(1500.5);
            farm.setOrganization(organization);

            when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
            when(farmRepository.save(farm)).thenReturn(farm);

            FarmResponse result = farmService.updateFarm(farmId, request);

            assertThat(result.id()).isEqualTo(farmId);
            assertThat(result.name()).isEqualTo("Fazenda Nova");
            assertThat(result.location()).isEqualTo("Rondonopolis");
            assertThat(result.hectareArea()).isEqualTo(1750.0);
            assertThat(result.organizationId()).isEqualTo(organizationId);
            verify(farmRepository).save(farm);
        }

        @Test
        @DisplayName("Should throw exception when updating missing farm")
        void shouldThrowExceptionWhenUpdatingMissingFarm() {
            UUID farmId = UUID.randomUUID();
            FarmUpdateRequest request = new FarmUpdateRequest("Fazenda Nova", "Rondonopolis", 1750.0);

            when(farmRepository.findById(farmId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.updateFarm(farmId, request))
                    .isInstanceOf(FarmNotFoundException.class)
                    .hasMessage("Fazenda não encontrada.");

            verify(farmRepository, never()).save(any(Farm.class));
        }
    }
}
