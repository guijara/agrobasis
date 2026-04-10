package com.agrobasis.core_service.identity.domain.exception;

public class MembershipRequestNotFoundException extends RuntimeException {
    public MembershipRequestNotFoundException(String message) {
        super(message);
    }
}
