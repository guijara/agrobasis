package com.agrobasis.core_service.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization,UUID> {
    boolean existsByCnpj(String cnpj);

    Optional<Organization> getOrganizationById(UUID id);
}
