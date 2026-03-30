package com.agrobasis.core_service.crop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CropRepository extends JpaRepository<Crop, UUID> {
}
