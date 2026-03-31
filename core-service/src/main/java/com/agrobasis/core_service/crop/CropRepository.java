package com.agrobasis.core_service.crop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface CropRepository extends JpaRepository<Crop, UUID> {

    @Query("""
        SELECT COUNT(c) > 0 FROM Crop c 
        WHERE c.plot.id = :plotId 
        AND (:startDate <= c.endDate AND :endDate >= c.startDate)
    """)
    boolean existsOverlappingCrop(
            @Param("plotId") UUID plotId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    Page<Crop> findAllByPlot_Id(UUID plotId, Pageable pageable);
}
