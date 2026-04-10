package com.agrobasis.core_service.identity.infrastructure;

import com.agrobasis.core_service.identity.domain.MembershipRequestStatus;
import com.agrobasis.core_service.identity.domain.OrganizationMembershipRequest;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.UserAccessStatus;
import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MembershipRequestRepositoryIT {

    @Autowired
    private MembershipRequestRepository membershipRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("Should save membership request")
    void shouldSaveMembershipRequest() {
        Organization organization = createOrganization("AgroTech", "11.111.111/0001-11");
        User user = createUser("save@agro.com");

        OrganizationMembershipRequest request = new OrganizationMembershipRequest();
        request.setUser(user);
        request.setOrganization(organization);
        request.setStatus(MembershipRequestStatus.PENDING);

        OrganizationMembershipRequest saved = membershipRequestRepository.saveAndFlush(request);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getOrganization().getId()).isEqualTo(organization.getId());
    }

    @Test
    @DisplayName("Should find pending requests by organization")
    void shouldFindPendingRequestsByOrganization() {
        Organization target = createOrganization("AgroTech", "22.222.222/0001-22");
        Organization other = createOrganization("Other", "33.333.333/0001-33");
        membershipRequestRepository.saveAndFlush(createRequest(createUser("one@agro.com"), target, MembershipRequestStatus.PENDING));
        membershipRequestRepository.saveAndFlush(createRequest(createUser("two@agro.com"), target, MembershipRequestStatus.PENDING));
        membershipRequestRepository.saveAndFlush(createRequest(createUser("three@agro.com"), other, MembershipRequestStatus.PENDING));

        List<OrganizationMembershipRequest> result = membershipRequestRepository.findAllByOrganization_IdAndStatus(
                target.getId(), MembershipRequestStatus.PENDING);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should find requests by user")
    void shouldFindRequestsByUser() {
        Organization first = createOrganization("First", "44.444.444/0001-44");
        Organization second = createOrganization("Second", "55.555.555/0001-55");
        User user = createUser("find@agro.com");
        membershipRequestRepository.saveAndFlush(createRequest(user, first, MembershipRequestStatus.PENDING));
        membershipRequestRepository.saveAndFlush(createRequest(user, second, MembershipRequestStatus.REJECTED));

        List<OrganizationMembershipRequest> result = membershipRequestRepository.findAllByUser_Id(user.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should enforce unique pending request per user and organization")
    void shouldEnforceUniquePendingRequestPerUserAndOrganization() {
        Organization organization = createOrganization("Unique", "66.666.666/0001-66");
        User user = createUser("unique@agro.com");
        membershipRequestRepository.saveAndFlush(createRequest(user, organization, MembershipRequestStatus.PENDING));

        assertThatThrownBy(() -> membershipRequestRepository.saveAndFlush(
                createRequest(user, organization, MembershipRequestStatus.PENDING)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private OrganizationMembershipRequest createRequest(User user, Organization organization, MembershipRequestStatus status) {
        OrganizationMembershipRequest request = new OrganizationMembershipRequest();
        request.setUser(user);
        request.setOrganization(organization);
        request.setStatus(status);
        return request;
    }

    private Organization createOrganization(String name, String cnpj) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setCnpj(cnpj);
        organization.setLocation("Cuiaba");
        return organizationRepository.saveAndFlush(organization);
    }

    private User createUser(String email) {
        User user = new User();
        user.setName("User");
        user.setEmail(email);
        user.setPassword("hashed");
        user.setRole(UserRole.VIEWER);
        user.setAccessStatus(UserAccessStatus.PENDING_ORGANIZATION_APPROVAL);
        return userRepository.saveAndFlush(user);
    }
}
