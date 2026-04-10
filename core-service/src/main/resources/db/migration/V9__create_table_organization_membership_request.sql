CREATE TABLE organization_membership_request (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    reviewed_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_membership_request_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),
    CONSTRAINT fk_membership_request_organization
        FOREIGN KEY (organization_id)
            REFERENCES organization (id)
);

CREATE INDEX idx_membership_request_user_id ON organization_membership_request(user_id);
CREATE INDEX idx_membership_request_organization_id ON organization_membership_request(organization_id);
CREATE INDEX idx_membership_request_status ON organization_membership_request(status);
CREATE UNIQUE INDEX uq_membership_request_pending_user_org
    ON organization_membership_request(user_id, organization_id)
    WHERE status = 'PENDING';
