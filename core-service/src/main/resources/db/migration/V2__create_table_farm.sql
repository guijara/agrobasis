CREATE TABLE farm (
      id UUID PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      location VARCHAR(255) NOT NULL,
      hectare_area NUMERIC(10, 2) NOT NULL,
      organization_id UUID NOT NULL,
      created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

      CONSTRAINT fk_farm_organization
          FOREIGN KEY (organization_id)
              REFERENCES organization (id)
              ON DELETE RESTRICT
);


CREATE INDEX idx_farm_organization_id ON farm(organization_id);