package com.agrobasis.core_service.plot;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlotRepository extends JpaRepository<Plot,UUID> {
    Page<Plot> findAllByFarm_Id(UUID farmId, Pageable pageable);
}
