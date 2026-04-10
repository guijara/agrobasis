package com.agrobasis.core_service.identity.domain.exception;

public class UnauthorizedOrganizationApprovalException extends RuntimeException {
    public UnauthorizedOrganizationApprovalException(String message) {
        super(message);
    }
}
