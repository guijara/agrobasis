package com.agrobasis.core_service.cost.application;

import com.agrobasis.core_service.cost.api.dto.CostProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.CostProfileResponse;
import com.agrobasis.core_service.cost.api.dto.CostProfileUpdateRequest;
import com.agrobasis.core_service.cost.domain.CostProfile;
import com.agrobasis.core_service.cost.domain.exception.CostProfileAlreadyExistsException;
import com.agrobasis.core_service.cost.domain.exception.CostProfileNotFoundException;
import com.agrobasis.core_service.cost.infrastructure.CostProfileRepository;
import com.agrobasis.core_service.farm.domain.Commodity;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
class CostProfileServiceTest {

    @Mock
    private CostProfileRepository costProfileRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private CostProfileService costProfileService;

    @Nested
    @DisplayName("createCostProfile()")
    class CreateCostProfileTests {

        @Test
        @DisplayName("Should create cost profile successfully")
        void shouldCreateCostProfileSuccessfully() {
            UUID organizationId = UUID.randomUUID();
            CostProfileCreateRequest request = new CostProfileCreateRequest(
                    organizationId,
                    Commodity.SOYBEAN,
                    new BigDecimal("45.00")
            );

            Organization organization = new Organization();
            organization.setId(organizationId);

            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(costProfileRepository.existsByOrganization_IdAndCommodity(organizationId, Commodity.SOYBEAN)).thenReturn(false);
            when(costProfileRepository.save(any(CostProfile.class))).thenAnswer(invocation -> {
                CostProfile saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                saved.setCreatedAt(LocalDateTime.of(2026, 4, 7, 12, 0));
                saved.setUpdatedAt(LocalDateTime.of(2026, 4, 7, 12, 0));
                return saved;
            });

            CostProfileResponse result = costProfileService.createCostProfile(request);

            assertThat(result.id()).isNotNull();
            assertThat(result.organizationId()).isEqualTo(organizationId);
            assertThat(result.commodity()).isEqualTo(Commodity.SOYBEAN);
            assertThat(result.costPerTon()).isEqualByComparingTo("45.00");
            verify(costProfileRepository).save(any(CostProfile.class));
        }

        @Test
        @DisplayName("Should throw exception when organization does not exist")
        void shouldThrowExceptionWhenOrganizationDoesNotExist() {
            UUID organizationId = UUID.randomUUID();
            CostProfileCreateRequest request = new CostProfileCreateRequest(
                    organizationId,
                    Commodity.SOYBEAN,
                    new BigDecimal("45.00")
            );

            when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> costProfileService.createCostProfile(request))
                    .isInstanceOf(OrganizationNotFoundException.class)
                    .hasMessage("Organização não encontrada.");

            verify(costProfileRepository, never()).save(any(CostProfile.class));
        }

        @Test
        @DisplayName("Should throw exception when cost profile already exists")
        void shouldThrowExceptionWhenCostProfileAlreadyExists() {
            UUID organizationId = UUID.randomUUID();
            CostProfileCreateRequest request = new CostProfileCreateRequest(
                    organizationId,
                    Commodity.SOYBEAN,
                    new BigDecimal("45.00")
            );

            Organization organization = new Organization();
            organization.setId(organizationId);

            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(costProfileRepository.existsByOrganization_IdAndCommodity(organizationId, Commodity.SOYBEAN)).thenReturn(true);

            assertThatThrownBy(() -> costProfileService.createCostProfile(request))
                    .isInstanceOf(CostProfileAlreadyExistsException.class)
                    .hasMessage("Perfil de custo já cadastrado para a commodity informada.");

            verify(costProfileRepository, never()).save(any(CostProfile.class));
        }
    }

    @Nested
    @DisplayName("getCostProfileById()")
    class GetCostProfileByIdTests {

        @Test
        @DisplayName("Should return cost profile when ID exists")
        void shouldReturnCostProfileWhenIdExists() {
            UUID id = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            CostProfile costProfile = createCostProfile(id, organizationId, Commodity.SOYBEAN, "45.00");

            when(costProfileRepository.findByIdAndOrganization_Id(id, organizationId)).thenReturn(Optional.of(costProfile));

            CostProfileResponse result = costProfileService.getCostProfileById(id, organizationId);

            assertThat(result.id()).isEqualTo(id);
            assertThat(result.organizationId()).isEqualTo(organizationId);
            assertThat(result.commodity()).isEqualTo(Commodity.SOYBEAN);
        }

        @Test
        @DisplayName("Should throw exception when cost profile is not found by ID")
        void shouldThrowExceptionWhenCostProfileIsNotFoundById() {
            UUID id = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();

            when(costProfileRepository.findByIdAndOrganization_Id(id, organizationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> costProfileService.getCostProfileById(id, organizationId))
                    .isInstanceOf(CostProfileNotFoundException.class)
                    .hasMessage("Perfil de custo não encontrado.");
        }

        @Test
        @DisplayName("Should throw exception when cost profile belongs to another organization")
        void shouldThrowExceptionWhenCostProfileBelongsToAnotherOrganization() {
            UUID id = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();

            when(costProfileRepository.findByIdAndOrganization_Id(id, organizationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> costProfileService.getCostProfileById(id, organizationId))
                    .isInstanceOf(CostProfileNotFoundException.class)
                    .hasMessage("Perfil de custo não encontrado.");
        }
    }

    @Nested
    @DisplayName("getCostProfileByOrganizationAndCommodity()")
    class GetCostProfileByOrganizationAndCommodityTests {

        @Test
        @DisplayName("Should return cost profile by organization and commodity")
        void shouldReturnCostProfileByOrganizationAndCommodity() {
            UUID organizationId = UUID.randomUUID();
            CostProfile costProfile = createCostProfile(UUID.randomUUID(), organizationId, Commodity.CORN, "50.00");

            when(costProfileRepository.findByOrganization_IdAndCommodity(organizationId, Commodity.CORN))
                    .thenReturn(Optional.of(costProfile));

            CostProfileResponse result = costProfileService.getCostProfileByOrganizationAndCommodity(organizationId, Commodity.CORN);

            assertThat(result.organizationId()).isEqualTo(organizationId);
            assertThat(result.commodity()).isEqualTo(Commodity.CORN);
            assertThat(result.costPerTon()).isEqualByComparingTo("50.00");
        }

        @Test
        @DisplayName("Should throw exception when cost profile by organization and commodity is not found")
        void shouldThrowExceptionWhenCostProfileByOrganizationAndCommodityIsNotFound() {
            UUID organizationId = UUID.randomUUID();

            when(costProfileRepository.findByOrganization_IdAndCommodity(organizationId, Commodity.CORN))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> costProfileService.getCostProfileByOrganizationAndCommodity(organizationId, Commodity.CORN))
                    .isInstanceOf(CostProfileNotFoundException.class)
                    .hasMessage("Perfil de custo não encontrado.");
        }
    }

    @Nested
    @DisplayName("listCostProfilesByOrganization()")
    class ListCostProfilesByOrganizationTests {

        @Test
        @DisplayName("Should list cost profiles by organization")
        void shouldListCostProfilesByOrganization() {
            UUID organizationId = UUID.randomUUID();
            List<CostProfile> costProfiles = List.of(
                    createCostProfile(UUID.randomUUID(), organizationId, Commodity.SOYBEAN, "45.00"),
                    createCostProfile(UUID.randomUUID(), organizationId, Commodity.CORN, "50.00")
            );

            when(costProfileRepository.findAllByOrganization_Id(organizationId)).thenReturn(costProfiles);

            List<CostProfileResponse> result = costProfileService.listCostProfilesByOrganization(organizationId);

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(CostProfileResponse::commodity)
                    .containsExactlyInAnyOrder(Commodity.SOYBEAN, Commodity.CORN);
        }
    }

    @Nested
    @DisplayName("updateCostProfile()")
    class UpdateCostProfileTests {

        @Test
        @DisplayName("Should update cost profile successfully")
        void shouldUpdateCostProfileSuccessfully() {
            UUID id = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            CostProfile costProfile = createCostProfile(id, organizationId, Commodity.SOYBEAN, "45.00");
            CostProfileUpdateRequest request = new CostProfileUpdateRequest(new BigDecimal("47.50"));

            when(costProfileRepository.findByIdAndOrganization_Id(id, organizationId)).thenReturn(Optional.of(costProfile));
            when(costProfileRepository.save(costProfile)).thenReturn(costProfile);

            CostProfileResponse result = costProfileService.updateCostProfile(id, organizationId, request);

            assertThat(result.costPerTon()).isEqualByComparingTo("47.50");
            verify(costProfileRepository).save(costProfile);
        }

        @Test
        @DisplayName("Should throw exception when updating missing cost profile")
        void shouldThrowExceptionWhenUpdatingMissingCostProfile() {
            UUID id = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            CostProfileUpdateRequest request = new CostProfileUpdateRequest(new BigDecimal("47.50"));

            when(costProfileRepository.findByIdAndOrganization_Id(id, organizationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> costProfileService.updateCostProfile(id, organizationId, request))
                    .isInstanceOf(CostProfileNotFoundException.class)
                    .hasMessage("Perfil de custo não encontrado.");

            verify(costProfileRepository, never()).save(any(CostProfile.class));
        }

        @Test
        @DisplayName("Should throw exception when updating cost profile from another organization")
        void shouldThrowExceptionWhenUpdatingCostProfileFromAnotherOrganization() {
            UUID id = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            CostProfileUpdateRequest request = new CostProfileUpdateRequest(new BigDecimal("47.50"));

            when(costProfileRepository.findByIdAndOrganization_Id(id, organizationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> costProfileService.updateCostProfile(id, organizationId, request))
                    .isInstanceOf(CostProfileNotFoundException.class)
                    .hasMessage("Perfil de custo não encontrado.");

            verify(costProfileRepository, never()).save(any(CostProfile.class));
        }
    }

    private CostProfile createCostProfile(UUID id, UUID organizationId, Commodity commodity, String costPerTon) {
        Organization organization = new Organization();
        organization.setId(organizationId);

        CostProfile costProfile = new CostProfile();
        costProfile.setId(id);
        costProfile.setOrganization(organization);
        costProfile.setCommodity(commodity);
        costProfile.setCostPerTon(new BigDecimal(costPerTon));
        costProfile.setCreatedAt(LocalDateTime.of(2026, 4, 7, 12, 0));
        costProfile.setUpdatedAt(LocalDateTime.of(2026, 4, 7, 12, 0));
        return costProfile;
    }
}
