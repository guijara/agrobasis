package com.agrobasis.core_service.cost.application;

import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileResponse;
import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileUpdateRequest;
import com.agrobasis.core_service.cost.domain.CommercialAdjustmentProfile;
import com.agrobasis.core_service.cost.domain.exception.CommercialAdjustmentProfileAlreadyExistsException;
import com.agrobasis.core_service.cost.domain.exception.CommercialAdjustmentProfileNotFoundException;
import com.agrobasis.core_service.cost.infrastructure.CommercialAdjustmentProfileRepository;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.farm.domain.Farm;
import com.agrobasis.core_service.farm.domain.exception.FarmNotFoundException;
import com.agrobasis.core_service.farm.infrastructure.FarmRepository;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import com.agrobasis.core_service.shared.domain.exception.TenantAccessDeniedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommercialAdjustmentProfileServiceTest {

    private static final UUID ORGANIZATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FARM_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private CommercialAdjustmentProfileRepository commercialAdjustmentProfileRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private FarmRepository farmRepository;

    @InjectMocks
    private CommercialAdjustmentProfileService commercialAdjustmentProfileService;

    @Test
    @DisplayName("Should create commercial adjustment profile successfully")
    void shouldCreateCommercialAdjustmentProfileSuccessfully() {
        CommercialAdjustmentProfileCreateRequest request = new CommercialAdjustmentProfileCreateRequest(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, new BigDecimal("10.00"));
        Organization organization = createOrganization(ORGANIZATION_ID);
        Farm farm = createFarm(FARM_ID, organization);

        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
        when(farmRepository.findById(FARM_ID)).thenReturn(Optional.of(farm));
        when(commercialAdjustmentProfileRepository.existsByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN)).thenReturn(false);
        when(commercialAdjustmentProfileRepository.save(any(CommercialAdjustmentProfile.class))).thenAnswer(invocation -> {
            CommercialAdjustmentProfile saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.setCreatedAt(LocalDateTime.of(2026, 4, 13, 10, 0));
            saved.setUpdatedAt(LocalDateTime.of(2026, 4, 13, 10, 0));
            return saved;
        });

        CommercialAdjustmentProfileResponse result = commercialAdjustmentProfileService.createCommercialAdjustmentProfile(request);

        assertThat(result.id()).isNotNull();
        assertThat(result.organizationId()).isEqualTo(ORGANIZATION_ID);
        assertThat(result.farmId()).isEqualTo(FARM_ID);
        assertThat(result.commodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(result.adjustmentPerTon()).isEqualByComparingTo("10.00");
        verify(commercialAdjustmentProfileRepository).save(any(CommercialAdjustmentProfile.class));
    }

    @Test
    @DisplayName("Should fail when organization does not exist")
    void shouldFailWhenOrganizationDoesNotExist() {
        CommercialAdjustmentProfileCreateRequest request = new CommercialAdjustmentProfileCreateRequest(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, new BigDecimal("10.00"));
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commercialAdjustmentProfileService.createCommercialAdjustmentProfile(request))
                .isInstanceOf(OrganizationNotFoundException.class)
                .hasMessage("Organização não encontrada.");

        verify(commercialAdjustmentProfileRepository, never()).save(any(CommercialAdjustmentProfile.class));
    }

    @Test
    @DisplayName("Should fail when farm does not exist")
    void shouldFailWhenFarmDoesNotExist() {
        CommercialAdjustmentProfileCreateRequest request = new CommercialAdjustmentProfileCreateRequest(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, new BigDecimal("10.00"));
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(createOrganization(ORGANIZATION_ID)));
        when(farmRepository.findById(FARM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commercialAdjustmentProfileService.createCommercialAdjustmentProfile(request))
                .isInstanceOf(FarmNotFoundException.class)
                .hasMessage("Fazenda não encontrada.");

        verify(commercialAdjustmentProfileRepository, never()).save(any(CommercialAdjustmentProfile.class));
    }

    @Test
    @DisplayName("Should fail when farm does not belong to organization")
    void shouldFailWhenFarmDoesNotBelongToOrganization() {
        UUID otherOrganizationId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        CommercialAdjustmentProfileCreateRequest request = new CommercialAdjustmentProfileCreateRequest(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, new BigDecimal("10.00"));
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(createOrganization(ORGANIZATION_ID)));
        when(farmRepository.findById(FARM_ID)).thenReturn(Optional.of(createFarm(FARM_ID, createOrganization(otherOrganizationId))));

        assertThatThrownBy(() -> commercialAdjustmentProfileService.createCommercialAdjustmentProfile(request))
                .isInstanceOf(TenantAccessDeniedException.class)
                .hasMessage("A fazenda informada não pertence à organização informada.");

        verify(commercialAdjustmentProfileRepository, never()).save(any(CommercialAdjustmentProfile.class));
    }

    @Test
    @DisplayName("Should fail when commercial adjustment profile already exists")
    void shouldFailWhenCommercialAdjustmentProfileAlreadyExists() {
        CommercialAdjustmentProfileCreateRequest request = new CommercialAdjustmentProfileCreateRequest(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, new BigDecimal("10.00"));
        Organization organization = createOrganization(ORGANIZATION_ID);
        Farm farm = createFarm(FARM_ID, organization);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
        when(farmRepository.findById(FARM_ID)).thenReturn(Optional.of(farm));
        when(commercialAdjustmentProfileRepository.existsByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN)).thenReturn(true);

        assertThatThrownBy(() -> commercialAdjustmentProfileService.createCommercialAdjustmentProfile(request))
                .isInstanceOf(CommercialAdjustmentProfileAlreadyExistsException.class)
                .hasMessage("Perfil de ajuste comercial já cadastrado para a organização, fazenda e commodity informadas.");

        verify(commercialAdjustmentProfileRepository, never()).save(any(CommercialAdjustmentProfile.class));
    }

    @Test
    @DisplayName("Should get commercial adjustment profile by farm and commodity")
    void shouldGetCommercialAdjustmentProfileByFarmAndCommodity() {
        CommercialAdjustmentProfile profile = createCommercialAdjustmentProfile(UUID.randomUUID(), ORGANIZATION_ID, FARM_ID, Commodity.CORN, "15.00");
        when(commercialAdjustmentProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.CORN))
                .thenReturn(Optional.of(profile));

        CommercialAdjustmentProfileResponse result = commercialAdjustmentProfileService.getCommercialAdjustmentProfileByFarmAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.CORN);

        assertThat(result.organizationId()).isEqualTo(ORGANIZATION_ID);
        assertThat(result.farmId()).isEqualTo(FARM_ID);
        assertThat(result.commodity()).isEqualTo(Commodity.CORN);
        assertThat(result.adjustmentPerTon()).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("Should update commercial adjustment profile successfully")
    void shouldUpdateCommercialAdjustmentProfileSuccessfully() {
        UUID id = UUID.randomUUID();
        CommercialAdjustmentProfile profile = createCommercialAdjustmentProfile(id, ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, "10.00");
        CommercialAdjustmentProfileUpdateRequest request = new CommercialAdjustmentProfileUpdateRequest(new BigDecimal("12.50"));
        when(commercialAdjustmentProfileRepository.findByIdAndOrganization_Id(id, ORGANIZATION_ID)).thenReturn(Optional.of(profile));
        when(commercialAdjustmentProfileRepository.save(profile)).thenReturn(profile);

        CommercialAdjustmentProfileResponse result = commercialAdjustmentProfileService.updateCommercialAdjustmentProfile(id, ORGANIZATION_ID, request);

        assertThat(result.adjustmentPerTon()).isEqualByComparingTo("12.50");
        verify(commercialAdjustmentProfileRepository).save(profile);
    }

    @Test
    @DisplayName("Should fail when commercial adjustment profile is missing by ID")
    void shouldFailWhenCommercialAdjustmentProfileIsMissingById() {
        UUID id = UUID.randomUUID();
        CommercialAdjustmentProfileUpdateRequest request = new CommercialAdjustmentProfileUpdateRequest(new BigDecimal("12.50"));
        when(commercialAdjustmentProfileRepository.findByIdAndOrganization_Id(id, ORGANIZATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commercialAdjustmentProfileService.updateCommercialAdjustmentProfile(id, ORGANIZATION_ID, request))
                .isInstanceOf(CommercialAdjustmentProfileNotFoundException.class)
                .hasMessage("Perfil de ajuste comercial não encontrado.");

        verify(commercialAdjustmentProfileRepository, never()).save(any(CommercialAdjustmentProfile.class));
    }

    private CommercialAdjustmentProfile createCommercialAdjustmentProfile(UUID id, UUID organizationId, UUID farmId, Commodity commodity, String adjustmentPerTon) {
        Organization organization = createOrganization(organizationId);
        Farm farm = createFarm(farmId, organization);

        CommercialAdjustmentProfile profile = new CommercialAdjustmentProfile();
        profile.setId(id);
        profile.setOrganization(organization);
        profile.setFarm(farm);
        profile.setCommodity(commodity);
        profile.setAdjustmentPerTon(new BigDecimal(adjustmentPerTon));
        profile.setCreatedAt(LocalDateTime.of(2026, 4, 13, 10, 0));
        profile.setUpdatedAt(LocalDateTime.of(2026, 4, 13, 10, 0));
        return profile;
    }

    private Organization createOrganization(UUID id) {
        Organization organization = new Organization();
        organization.setId(id);
        return organization;
    }

    private Farm createFarm(UUID id, Organization organization) {
        Farm farm = new Farm();
        farm.setId(id);
        farm.setOrganization(organization);
        return farm;
    }
}
