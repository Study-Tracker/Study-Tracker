ALTER TABLE users
    ADD type VARCHAR(255);

UPDATE users SET type = 'STANDARD_USER' WHERE type IS NULL;
UPDATE users SET username = email WHERE email IS NOT NULL;

ALTER TABLE users
    ALTER COLUMN type SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN email DROP NOT NULL;

ALTER TABLE study_collections
    ALTER COLUMN shared SET NOT NULL;