package com.agrobasis.core_service.organization.infrastructure;

import com.agrobasis.core_service.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization,UUID> {
    boolean existsByCnpj(String cnpj);

}
