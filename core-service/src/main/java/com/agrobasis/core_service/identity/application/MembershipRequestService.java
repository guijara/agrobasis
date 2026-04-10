package com.agrobasis.core_service.identity.application;

import com.agrobasis.core_service.identity.api.dto.MembershipRequestResponse;
import com.agrobasis.core_service.identity.domain.MembershipRequestStatus;
import com.agrobasis.core_service.identity.domain.OrganizationMembershipRequest;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.UserAccessStatus;
import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.identity.domain.exception.MembershipRequestAlreadyExistsException;
import com.agrobasis.core_service.identity.domain.exception.MembershipRequestNotFoundException;
import com.agrobasis.core_service.identity.domain.exception.UnauthorizedOrganizationApprovalException;
import com.agrobasis.core_service.identity.domain.exception.UserAccessNotAllowedException;
import com.agrobasis.core_service.identity.domain.exception.UserNotFoundException;
import com.agrobasis.core_service.identity.infrastructure.MembershipRequestRepository;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
import com.agrobasis.core_service.identity.infrastructure.security.AuthenticatedUser;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembershipRequestService {

    private final MembershipRequestRepository membershipRequestRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional
    public MembershipRequestResponse createMembershipRequest(UUID userId, UUID organizationId) {
        User user = userRepository.findWithOrganizationById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        if (user.getAccessStatus() == UserAccessStatus.ACTIVE) {
            throw new UserAccessNotAllowedException("Usuário já possui acesso ativo a uma organização.");
        }

        if (user.getOrganization() != null) {
            throw new UserAccessNotAllowedException("Usuário já está vinculado a uma organização.");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organização não encontrada."));

        if (membershipRequestRepository.existsByUser_IdAndOrganization_IdAndStatus(
                userId, organizationId, MembershipRequestStatus.PENDING)) {
            throw new MembershipRequestAlreadyExistsException("Já existe uma solicitação pendente para esta organização.");
        }

        OrganizationMembershipRequest membershipRequest = new OrganizationMembershipRequest();
        membershipRequest.setUser(user);
        membershipRequest.setOrganization(organization);
        membershipRequest.setStatus(MembershipRequestStatus.PENDING);

        return toResponse(membershipRequestRepository.save(membershipRequest));
    }

    @Transactional
    public MembershipRequestResponse approveMembershipRequest(UUID requestId, AuthenticatedUser adminUser) {
        OrganizationMembershipRequest membershipRequest = getPendingRequest(requestId);
        validateApprover(adminUser, membershipRequest.getOrganization().getId());

        membershipRequest.setStatus(MembershipRequestStatus.APPROVED);
        membershipRequest.setReviewedAt(LocalDateTime.now());

        User user = membershipRequest.getUser();
        user.setOrganization(membershipRequest.getOrganization());
        user.setAccessStatus(UserAccessStatus.ACTIVE);

        userRepository.save(user);
        return toResponse(membershipRequestRepository.save(membershipRequest));
    }

    @Transactional
    public MembershipRequestResponse rejectMembershipRequest(UUID requestId, AuthenticatedUser adminUser) {
        OrganizationMembershipRequest membershipRequest = getPendingRequest(requestId);
        validateApprover(adminUser, membershipRequest.getOrganization().getId());

        membershipRequest.setStatus(MembershipRequestStatus.REJECTED);
        membershipRequest.setReviewedAt(LocalDateTime.now());

        User user = membershipRequest.getUser();
        user.setAccessStatus(UserAccessStatus.REJECTED);

        userRepository.save(user);
        return toResponse(membershipRequestRepository.save(membershipRequest));
    }

    @Transactional(readOnly = true)
    public List<MembershipRequestResponse> listPendingRequestsByOrganization(UUID organizationId) {
        return membershipRequestRepository.findAllByOrganization_IdAndStatus(organizationId, MembershipRequestStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrganizationMembershipRequest getPendingRequest(UUID requestId) {
        OrganizationMembershipRequest membershipRequest = membershipRequestRepository.findById(requestId)
                .orElseThrow(() -> new MembershipRequestNotFoundException("Solicitação de vínculo não encontrada."));

        if (membershipRequest.getStatus() != MembershipRequestStatus.PENDING) {
            throw new MembershipRequestNotFoundException("Solicitação de vínculo não encontrada.");
        }

        return membershipRequest;
    }

    private void validateApprover(AuthenticatedUser adminUser, UUID organizationId) {
        if (adminUser.role() != UserRole.ADMIN) {
            throw new UnauthorizedOrganizationApprovalException("Usuário não possui permissão para aprovar esta solicitação.");
        }

        if (!organizationId.equals(adminUser.organizationId())) {
            throw new UnauthorizedOrganizationApprovalException("Usuário não possui permissão para aprovar esta solicitação.");
        }
    }

    private MembershipRequestResponse toResponse(OrganizationMembershipRequest membershipRequest) {
        return new MembershipRequestResponse(
                membershipRequest.getId(),
                membershipRequest.getUser().getId(),
                membershipRequest.getOrganization().getId(),
                membershipRequest.getStatus(),
                membershipRequest.getCreatedAt(),
                membershipRequest.getReviewedAt()
        );
    }
}
