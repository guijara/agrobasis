CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       organization_id UUID NOT NULL,
                       created_at TIMESTAMP NOT NULL,

                       CONSTRAINT fk_user_organization
                           FOREIGN KEY (organization_id)
                               REFERENCES organization (id)
);

CREATE INDEX idx_user_email ON users(email);