package com.agrobasis.core_service.cost.application;

import com.agrobasis.core_service.cost.api.dto.FreightProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.FreightProfileResponse;
import com.agrobasis.core_service.cost.api.dto.FreightProfileUpdateRequest;
import com.agrobasis.core_service.cost.domain.FreightProfile;
import com.agrobasis.core_service.cost.domain.exception.FreightProfileAlreadyExistsException;
import com.agrobasis.core_service.cost.domain.exception.FreightProfileNotFoundException;
import com.agrobasis.core_service.cost.infrastructure.FreightProfileRepository;
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
class FreightProfileServiceTest {

    private static final UUID ORGANIZATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FARM_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private FreightProfileRepository freightProfileRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private FarmRepository farmRepository;

    @InjectMocks
    private FreightProfileService freightProfileService;

    @Test
    @DisplayName("Should create freight profile successfully")
    void shouldCreateFreightProfileSuccessfully() {
        FreightProfileCreateRequest request = new FreightProfileCreateRequest(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, new BigDecimal("20.00"));
        Organization organization = createOrganization(ORGANIZATION_ID);
        Farm farm = createFarm(FARM_ID, organization);

        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
        when(farmRepository.findById(FARM_ID)).thenReturn(Optional.of(farm));
        when(freightProfileRepository.existsByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN)).thenReturn(false);
        when(freightProfileRepository.save(any(FreightProfile.class))).thenAnswer(invocation -> {
            FreightProfile saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.setCreatedAt(LocalDateTime.of(2026, 4, 13, 10, 0));
            saved.setUpdatedAt(LocalDateTime.of(2026, 4, 13, 10, 0));
            return saved;
        });

        FreightProfileResponse result = freightProfileService.createFreightProfile(request);

        assertThat(result.id()).isNotNull();
        assertThat(result.organizationId()).isEqualTo(ORGANIZATION_ID);
        assertThat(result.farmId()).isEqualTo(FARM_ID);
        assertThat(result.commodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(result.freightPerTon()).isEqualByComparingTo("20.00");
        verify(freightProfileRepository).save(any(FreightProfile.class));
    }

    @Test
    @DisplayName("Should fail when organization does not exist")
    void shouldFailWhenOrganizationDoesNotExist() {
        FreightProfileCreateRequest request = new FreightProfileCreateRequest(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, new BigDecimal("20.00"));
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> freightProfileService.createFreightProfile(request))
                .isInstanceOf(OrganizationNotFoundException.class)
                .hasMessage("Organização não encontrada.");

        verify(freightProfileRepository, never()).save(any(FreightProfile.class));
    }

    @Test
    @DisplayName("Should fail when farm does not exist")
    void shouldFailWhenFarmDoesNotExist() {
        FreightProfileCreateRequest request = new FreightProfileCreateRequest(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, new BigDecimal("20.00"));
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(createOrganization(ORGANIZATION_ID)));
        when(farmRepository.findById(FARM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> freightProfileService.createFreightProfile(request))
                .isInstanceOf(FarmNotFoundException.class)
                .hasMessage("Fazenda não encontrada.");

        verify(freightProfileRepository, never()).save(any(FreightProfile.class));
    }

    @Test
    @DisplayName("Should fail when farm does not belong to organization")
    void shouldFailWhenFarmDoesNotBelongToOrganization() {
        UUID otherOrganizationId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        FreightProfileCreateRequest request = new FreightProfileCreateRequest(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, new BigDecimal("20.00"));
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(createOrganization(ORGANIZATION_ID)));
        when(farmRepository.findById(FARM_ID)).thenReturn(Optional.of(createFarm(FARM_ID, createOrganization(otherOrganizationId))));

        assertThatThrownBy(() -> freightProfileService.createFreightProfile(request))
                .isInstanceOf(TenantAccessDeniedException.class)
                .hasMessage("A fazenda informada não pertence à organização informada.");

        verify(freightProfileRepository, never()).save(any(FreightProfile.class));
    }

    @Test
    @DisplayName("Should fail when freight profile already exists")
    void shouldFailWhenFreightProfileAlreadyExists() {
        FreightProfileCreateRequest request = new FreightProfileCreateRequest(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, new BigDecimal("20.00"));
        Organization organization = createOrganization(ORGANIZATION_ID);
        Farm farm = createFarm(FARM_ID, organization);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
        when(farmRepository.findById(FARM_ID)).thenReturn(Optional.of(farm));
        when(freightProfileRepository.existsByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN)).thenReturn(true);

        assertThatThrownBy(() -> freightProfileService.createFreightProfile(request))
                .isInstanceOf(FreightProfileAlreadyExistsException.class)
                .hasMessage("Perfil de frete já cadastrado para a organização, fazenda e commodity informadas.");

        verify(freightProfileRepository, never()).save(any(FreightProfile.class));
    }

    @Test
    @DisplayName("Should get freight profile by farm and commodity")
    void shouldGetFreightProfileByFarmAndCommodity() {
        FreightProfile freightProfile = createFreightProfile(UUID.randomUUID(), ORGANIZATION_ID, FARM_ID, Commodity.CORN, "30.00");
        when(freightProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.CORN))
                .thenReturn(Optional.of(freightProfile));

        FreightProfileResponse result = freightProfileService.getFreightProfileByFarmAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.CORN);

        assertThat(result.organizationId()).isEqualTo(ORGANIZATION_ID);
        assertThat(result.farmId()).isEqualTo(FARM_ID);
        assertThat(result.commodity()).isEqualTo(Commodity.CORN);
        assertThat(result.freightPerTon()).isEqualByComparingTo("30.00");
    }

    @Test
    @DisplayName("Should update freight profile successfully")
    void shouldUpdateFreightProfileSuccessfully() {
        UUID id = UUID.randomUUID();
        FreightProfile freightProfile = createFreightProfile(id, ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN, "20.00");
        FreightProfileUpdateRequest request = new FreightProfileUpdateRequest(new BigDecimal("22.50"));
        when(freightProfileRepository.findByIdAndOrganization_Id(id, ORGANIZATION_ID)).thenReturn(Optional.of(freightProfile));
        when(freightProfileRepository.save(freightProfile)).thenReturn(freightProfile);

        FreightProfileResponse result = freightProfileService.updateFreightProfile(id, ORGANIZATION_ID, request);

        assertThat(result.freightPerTon()).isEqualByComparingTo("22.50");
        verify(freightProfileRepository).save(freightProfile);
    }

    @Test
    @DisplayName("Should fail when freight profile is missing by ID")
    void shouldFailWhenFreightProfileIsMissingById() {
        UUID id = UUID.randomUUID();
        FreightProfileUpdateRequest request = new FreightProfileUpdateRequest(new BigDecimal("22.50"));
        when(freightProfileRepository.findByIdAndOrganization_Id(id, ORGANIZATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> freightProfileService.updateFreightProfile(id, ORGANIZATION_ID, request))
                .isInstanceOf(FreightProfileNotFoundException.class)
                .hasMessage("Perfil de frete não encontrado.");

        verify(freightProfileRepository, never()).save(any(FreightProfile.class));
    }

    private FreightProfile createFreightProfile(UUID id, UUID organizationId, UUID farmId, Commodity commodity, String freightPerTon) {
        Organization organization = createOrganization(organizationId);
        Farm farm = createFarm(farmId, organization);

        FreightProfile freightProfile = new FreightProfile();
        freightProfile.setId(id);
        freightProfile.setOrganization(organization);
        freightProfile.setFarm(farm);
        freightProfile.setCommodity(commodity);
        freightProfile.setFreightPerTon(new BigDecimal(freightPerTon));
        freightProfile.setCreatedAt(LocalDateTime.of(2026, 4, 13, 10, 0));
        freightProfile.setUpdatedAt(LocalDateTime.of(2026, 4, 13, 10, 0));
        return freightProfile;
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
