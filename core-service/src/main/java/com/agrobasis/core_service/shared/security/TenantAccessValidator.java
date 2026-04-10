package com.agrobasis.core_service.shared.security;

import com.agrobasis.core_service.identity.infrastructure.security.AuthenticatedUser;
import com.agrobasis.core_service.shared.domain.exception.TenantAccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantAccessValidator {

    public void assertOrganizationAccess(AuthenticatedUser authenticatedUser, UUID organizationId) {
        if (authenticatedUser.organizationId() == null || !authenticatedUser.organizationId().equals(organizationId)) {
            throw new TenantAccessDeniedException("Acesso negado para a organização informada.");
        }
    }
}
