CREATE TABLE freight_profile (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    farm_id UUID NOT NULL,
    commodity VARCHAR(50) NOT NULL,
    freight_per_ton NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_freight_profile_organization
        FOREIGN KEY (organization_id)
            REFERENCES organization (id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_freight_profile_farm
        FOREIGN KEY (farm_id)
            REFERENCES farm (id)
            ON DELETE RESTRICT,

    CONSTRAINT uq_freight_profile_org_farm_commodity
        UNIQUE (organization_id, farm_id, commodity)
);

CREATE INDEX idx_freight_profile_organization_id ON freight_profile(organization_id);
CREATE INDEX idx_freight_profile_farm_id ON freight_profile(farm_id);
