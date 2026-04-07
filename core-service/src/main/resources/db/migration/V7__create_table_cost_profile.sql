CREATE TABLE cost_profile (
       id UUID PRIMARY KEY,
       organization_id UUID NOT NULL,
       commodity VARCHAR(50) NOT NULL,
       cost_per_ton NUMERIC(12, 2) NOT NULL,
       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
       updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

       CONSTRAINT fk_cost_profile_organization
           FOREIGN KEY (organization_id)
               REFERENCES organization (id)
               ON DELETE RESTRICT,

       CONSTRAINT uq_cost_profile_org_commodity
           UNIQUE (organization_id, commodity)
);

CREATE INDEX idx_cost_profile_organization_id ON cost_profile(organization_id);
