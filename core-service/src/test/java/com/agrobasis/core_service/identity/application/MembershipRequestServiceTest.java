package com.agrobasis.core_service.identity.application;

import com.agrobasis.core_service.identity.api.dto.MembershipRequestResponse;
import com.agrobasis.core_service.identity.domain.MembershipRequestStatus;
import com.agrobasis.core_service.identity.domain.OrganizationMembershipRequest;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.UserAccessStatus;
import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.identity.domain.exception.MembershipRequestAlreadyExistsException;
import com.agrobasis.core_service.identity.domain.exception.UnauthorizedOrganizationApprovalException;
import com.agrobasis.core_service.identity.infrastructure.MembershipRequestRepository;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
import com.agrobasis.core_service.identity.infrastructure.security.AuthenticatedUser;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipRequestServiceTest {

    @Mock
    private MembershipRequestRepository membershipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private MembershipRequestService membershipRequestService;

    @Test
    @DisplayName("Should create membership request successfully")
    void shouldCreateMembershipRequestSuccessfully() {
        User user = pendingUser();
        Organization organization = organization();
        when(userRepository.findWithOrganizationById(user.getId())).thenReturn(Optional.of(user));
        when(organizationRepository.findById(organization.getId())).thenReturn(Optional.of(organization));
        when(membershipRequestRepository.existsByUser_IdAndOrganization_IdAndStatus(user.getId(), organization.getId(), MembershipRequestStatus.PENDING))
                .thenReturn(false);
        when(membershipRequestRepository.save(any(OrganizationMembershipRequest.class))).thenAnswer(invocation -> {
            OrganizationMembershipRequest request = invocation.getArgument(0);
            request.setId(UUID.randomUUID());
            return request;
        });

        MembershipRequestResponse response = membershipRequestService.createMembershipRequest(user.getId(), organization.getId());

        assertThat(response.userId()).isEqualTo(user.getId());
        assertThat(response.organizationId()).isEqualTo(organization.getId());
        assertThat(response.status()).isEqualTo(MembershipRequestStatus.PENDING);
    }

    @Test
    @DisplayName("Should fail when organization does not exist")
    void shouldFailWhenOrganizationDoesNotExist() {
        User user = pendingUser();
        UUID organizationId = UUID.randomUUID();
        when(userRepository.findWithOrganizationById(user.getId())).thenReturn(Optional.of(user));
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> membershipRequestService.createMembershipRequest(user.getId(), organizationId))
                .isInstanceOf(OrganizationNotFoundException.class)
                .hasMessage("Organização não encontrada.");
    }

    @Test
    @DisplayName("Should fail when pending request already exists")
    void shouldFailWhenPendingRequestAlreadyExists() {
        User user = pendingUser();
        Organization organization = organization();
        when(userRepository.findWithOrganizationById(user.getId())).thenReturn(Optional.of(user));
        when(organizationRepository.findById(organization.getId())).thenReturn(Optional.of(organization));
        when(membershipRequestRepository.existsByUser_IdAndOrganization_IdAndStatus(user.getId(), organization.getId(), MembershipRequestStatus.PENDING))
                .thenReturn(true);

        assertThatThrownBy(() -> membershipRequestService.createMembershipRequest(user.getId(), organization.getId()))
                .isInstanceOf(MembershipRequestAlreadyExistsException.class)
                .hasMessage("Já existe uma solicitação pendente para esta organização.");
    }

    @Test
    @DisplayName("Should approve membership request successfully")
    void shouldApproveMembershipRequestSuccessfully() {
        Organization organization = organization();
        User user = pendingUser();
        OrganizationMembershipRequest request = pendingRequest(user, organization);
        AuthenticatedUser adminUser = new AuthenticatedUser(UUID.randomUUID(), organization.getId(), "admin@email.com", UserRole.ADMIN);
        when(membershipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(membershipRequestRepository.save(any(OrganizationMembershipRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(user)).thenReturn(user);

        MembershipRequestResponse response = membershipRequestService.approveMembershipRequest(request.getId(), adminUser);

        assertThat(response.status()).isEqualTo(MembershipRequestStatus.APPROVED);
        assertThat(user.getOrganization()).isEqualTo(organization);
        assertThat(user.getAccessStatus()).isEqualTo(UserAccessStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should reject membership request successfully")
    void shouldRejectMembershipRequestSuccessfully() {
        Organization organization = organization();
        User user = pendingUser();
        OrganizationMembershipRequest request = pendingRequest(user, organization);
        AuthenticatedUser adminUser = new AuthenticatedUser(UUID.randomUUID(), organization.getId(), "admin@email.com", UserRole.ADMIN);
        when(membershipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(membershipRequestRepository.save(any(OrganizationMembershipRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(user)).thenReturn(user);

        MembershipRequestResponse response = membershipRequestService.rejectMembershipRequest(request.getId(), adminUser);

        assertThat(response.status()).isEqualTo(MembershipRequestStatus.REJECTED);
        assertThat(user.getAccessStatus()).isEqualTo(UserAccessStatus.REJECTED);
    }

    @Test
    @DisplayName("Should prevent approval by non admin user")
    void shouldPreventApprovalByNonAdminUser() {
        Organization organization = organization();
        OrganizationMembershipRequest request = pendingRequest(pendingUser(), organization);
        AuthenticatedUser operator = new AuthenticatedUser(UUID.randomUUID(), organization.getId(), "op@email.com", UserRole.OPERATOR);
        when(membershipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> membershipRequestService.approveMembershipRequest(request.getId(), operator))
                .isInstanceOf(UnauthorizedOrganizationApprovalException.class)
                .hasMessage("Usuário não possui permissão para aprovar esta solicitação.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should prevent approval by admin from another organization")
    void shouldPreventApprovalByAdminFromAnotherOrganization() {
        Organization organization = organization();
        OrganizationMembershipRequest request = pendingRequest(pendingUser(), organization);
        AuthenticatedUser otherAdmin = new AuthenticatedUser(UUID.randomUUID(), UUID.randomUUID(), "admin@email.com", UserRole.ADMIN);
        when(membershipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> membershipRequestService.approveMembershipRequest(request.getId(), otherAdmin))
                .isInstanceOf(UnauthorizedOrganizationApprovalException.class)
                .hasMessage("Usuário não possui permissão para aprovar esta solicitação.");
    }

    @Test
    @DisplayName("Should list pending requests by organization")
    void shouldListPendingRequestsByOrganization() {
        Organization organization = organization();
        OrganizationMembershipRequest request = pendingRequest(pendingUser(), organization);
        when(membershipRequestRepository.findAllByOrganization_IdAndStatus(organization.getId(), MembershipRequestStatus.PENDING))
                .thenReturn(List.of(request));

        List<MembershipRequestResponse> responses = membershipRequestService.listPendingRequestsByOrganization(organization.getId());

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().organizationId()).isEqualTo(organization.getId());
    }

    private User pendingUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setAccessStatus(UserAccessStatus.PENDING_ORGANIZATION_APPROVAL);
        user.setRole(UserRole.VIEWER);
        return user;
    }

    private Organization organization() {
        Organization organization = new Organization();
        organization.setId(UUID.randomUUID());
        return organization;
    }

    private OrganizationMembershipRequest pendingRequest(User user, Organization organization) {
        OrganizationMembershipRequest request = new OrganizationMembershipRequest();
        request.setId(UUID.randomUUID());
        request.setUser(user);
        request.setOrganization(organization);
        request.setStatus(MembershipRequestStatus.PENDING);
        return request;
    }
}
