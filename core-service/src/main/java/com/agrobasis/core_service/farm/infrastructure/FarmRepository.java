package com.agrobasis.core_service.farm.infrastructure;

import com.agrobasis.core_service.farm.domain.Farm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FarmRepository extends JpaRepository<Farm, UUID> {
    Page<Farm> findAllByOrganizationId(UUID organizationId, Pageable pageable);
}
