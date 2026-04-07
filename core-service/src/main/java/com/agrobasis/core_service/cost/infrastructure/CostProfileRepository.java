package com.agrobasis.core_service.cost.infrastructure;

import com.agrobasis.core_service.cost.domain.CostProfile;
import com.agrobasis.core_service.farm.domain.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CostProfileRepository extends JpaRepository<CostProfile, UUID> {
    boolean existsByOrganization_IdAndCommodity(UUID organizationId, Commodity commodity);

    Optional<CostProfile> findByOrganization_IdAndCommodity(UUID organizationId, Commodity commodity);

    List<CostProfile> findAllByOrganization_Id(UUID organizationId);
}
