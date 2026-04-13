ALTER TABLE users
    ALTER COLUMN organization_id DROP NOT NULL;

ALTER TABLE users
    ADD COLUMN access_status VARCHAR(50);

UPDATE users
SET access_status = 'ACTIVE'
WHERE access_status IS NULL;

ALTER TABLE users
    ALTER COLUMN access_status SET NOT NULL;
