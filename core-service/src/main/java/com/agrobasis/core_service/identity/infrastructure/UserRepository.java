package com.agrobasis.core_service.identity.infrastructure;

import com.agrobasis.core_service.identity.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UUID id);

    @EntityGraph(attributePaths = {"organization"})
    Page<User> findAllByOrganization_Id(UUID organizationId, Pageable pageable);
}
