ALTER TABLE file_storage_locations
    ALTER COLUMN default_data_location DROP NOT NULL;

ALTER TABLE file_storage_locations
    ALTER COLUMN default_study_location DROP NOT NULL;

ALTER TABLE study_collections
    ALTER COLUMN shared DROP NOT NULL;

ALTER TABLE onedrive_drives
    ALTER COLUMN web_url TYPE VARCHAR(2048) USING (web_url::VARCHAR(2048));

ALTER TABLE onedrive_folders
    ALTER COLUMN web_url TYPE VARCHAR(2048) USING (web_url::VARCHAR(2048));