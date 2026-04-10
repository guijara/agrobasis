package com.agrobasis.core_service.identity.infrastructure;

import com.agrobasis.core_service.identity.domain.MembershipRequestStatus;
import com.agrobasis.core_service.identity.domain.OrganizationMembershipRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRequestRepository extends JpaRepository<OrganizationMembershipRequest, UUID> {

    boolean existsByUser_IdAndStatus(UUID userId, MembershipRequestStatus status);

    boolean existsByUser_IdAndOrganization_IdAndStatus(UUID userId, UUID organizationId, MembershipRequestStatus status);

    @Override
    @EntityGraph(attributePaths = {"user", "organization", "user.organization"})
    Optional<OrganizationMembershipRequest> findById(UUID id);

    @EntityGraph(attributePaths = {"user", "organization"})
    List<OrganizationMembershipRequest> findAllByOrganization_IdAndStatus(UUID organizationId, MembershipRequestStatus status);

    @EntityGraph(attributePaths = {"user", "organization"})
    List<OrganizationMembershipRequest> findAllByUser_Id(UUID userId);
}
