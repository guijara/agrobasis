package com.agrobasis.core_service.cost.infrastructure;

import com.agrobasis.core_service.cost.domain.FreightProfile;
import com.agrobasis.core_service.farm.domain.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FreightProfileRepository extends JpaRepository<FreightProfile, UUID> {
    boolean existsByOrganization_IdAndFarm_IdAndCommodity(UUID organizationId, UUID farmId, Commodity commodity);

    Optional<FreightProfile> findByOrganization_IdAndFarm_IdAndCommodity(UUID organizationId, UUID farmId, Commodity commodity);

    List<FreightProfile> findAllByOrganization_Id(UUID organizationId);

    List<FreightProfile> findAllByOrganization_IdAndFarm_Id(UUID organizationId, UUID farmId);

    Optional<FreightProfile> findByIdAndOrganization_Id(UUID id, UUID organizationId);
}
