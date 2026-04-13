CREATE TABLE commercial_adjustment_profile (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    farm_id UUID NOT NULL,
    commodity VARCHAR(50) NOT NULL,
    adjustment_per_ton NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_commercial_adjustment_profile_organization
        FOREIGN KEY (organization_id)
            REFERENCES organization (id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_commercial_adjustment_profile_farm
        FOREIGN KEY (farm_id)
            REFERENCES farm (id)
            ON DELETE RESTRICT,

    CONSTRAINT uq_commercial_adjustment_profile_org_farm_commodity
        UNIQUE (organization_id, farm_id, commodity),

    CONSTRAINT ck_commercial_adjustment_profile_adjustment_per_ton_non_negative
        CHECK (adjustment_per_ton >= 0)
);

CREATE INDEX idx_commercial_adjustment_profile_organization_id ON commercial_adjustment_profile(organization_id);
CREATE INDEX idx_commercial_adjustment_profile_farm_id ON commercial_adjustment_profile(farm_id);
