package com.agrobasis.core_service.cost.infrastructure;

import com.agrobasis.core_service.cost.domain.CommercialAdjustmentProfile;
import com.agrobasis.core_service.farm.domain.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommercialAdjustmentProfileRepository extends JpaRepository<CommercialAdjustmentProfile, UUID> {
    boolean existsByOrganization_IdAndFarm_IdAndCommodity(UUID organizationId, UUID farmId, Commodity commodity);

    Optional<CommercialAdjustmentProfile> findByOrganization_IdAndFarm_IdAndCommodity(UUID organizationId, UUID farmId, Commodity commodity);

    List<CommercialAdjustmentProfile> findAllByOrganization_Id(UUID organizationId);

    List<CommercialAdjustmentProfile> findAllByOrganization_IdAndFarm_Id(UUID organizationId, UUID farmId);

    Optional<CommercialAdjustmentProfile> findByIdAndOrganization_Id(UUID id, UUID organizationId);
}
