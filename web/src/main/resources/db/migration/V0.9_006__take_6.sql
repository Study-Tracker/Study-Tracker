CREATE TABLE aws_integrations
(
    id                BIGINT                      NOT NULL,
    organization_id   BIGINT                      NOT NULL,
    name              VARCHAR(255)                NOT NULL,
    account_number    VARCHAR(255),
    region            VARCHAR(255)                NOT NULL,
    access_key_id     VARCHAR(1024),
    secret_access_key VARCHAR(1024),
    use_iam           BOOLEAN                     NOT NULL,
    active            BOOLEAN                     NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_aws_integrations PRIMARY KEY (id)
);

CREATE TABLE egnyte_drive_folders
(
    id                      BIGINT                      NOT NULL,
    storage_drive_folder_id BIGINT                      NOT NULL,
    egnyte_drive_id         BIGINT                      NOT NULL,
    folder_id               VARCHAR(255)                NOT NULL,
    web_url                 VARCHAR(255),
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_egnyte_drive_folders PRIMARY KEY (id)
);

CREATE TABLE egnyte_drives
(
    id                    BIGINT                      NOT NULL,
    storage_drive_id      BIGINT                      NOT NULL,
    egnyte_integration_id BIGINT                      NOT NULL,
    name                  VARCHAR(255)                NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_egnyte_drives PRIMARY KEY (id)
);

CREATE TABLE egnyte_integrations
(
    id              BIGINT                      NOT NULL,
    organization_id BIGINT                      NOT NULL,
    tenant_name     VARCHAR(255)                NOT NULL,
    root_url        VARCHAR(255)                NOT NULL,
    api_token       VARCHAR(1024)               NOT NULL,
    qps             INTEGER,
    active          BOOLEAN                     NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_egnyte_integrations PRIMARY KEY (id)
);

CREATE TABLE local_drive_folders
(
    id                      BIGINT                      NOT NULL,
    storage_drive_folder_id BIGINT                      NOT NULL,
    local_drive_id          BIGINT                      NOT NULL,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_local_drive_folders PRIMARY KEY (id)
);

CREATE TABLE local_drives
(
    id               BIGINT                      NOT NULL,
    storage_drive_id BIGINT                      NOT NULL,
    organization_id  BIGINT                      NOT NULL,
    name             VARCHAR(255)                NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_local_drives PRIMARY KEY (id)
);

CREATE TABLE organizations
(
    id          BIGINT                      NOT NULL,
    name        VARCHAR(255)                NOT NULL,
    description VARCHAR(1024),
    active      BOOLEAN                     NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_organizations PRIMARY KEY (id)
);

CREATE TABLE s3_bucket_folders
(
    id                      BIGINT                      NOT NULL,
    storage_drive_folder_id BIGINT                      NOT NULL,
    s3_bucket_id            BIGINT                      NOT NULL,
    key                     VARCHAR(255),
    e_tag                   VARCHAR(255),
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_s3_bucket_folders PRIMARY KEY (id)
);

CREATE TABLE s3_buckets
(
    id                 BIGINT                      NOT NULL,
    storage_drive_id   BIGINT                      NOT NULL,
    aws_integration_id BIGINT                      NOT NULL,
    name               VARCHAR(255)                NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_s3_buckets PRIMARY KEY (id)
);

CREATE TABLE storage_drive_folders
(
    id                BIGINT                      NOT NULL,
    storage_drive_id  BIGINT                      NOT NULL,
    path              VARCHAR(2048)               NOT NULL,
    name              VARCHAR(255)                NOT NULL,
    is_browser_root   BOOLEAN                     NOT NULL,
    is_study_root     BOOLEAN                     NOT NULL,
    is_write_enabled  BOOLEAN                     NOT NULL,
    is_delete_enabled BOOLEAN                     NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_storage_drive_folders PRIMARY KEY (id)
);

CREATE TABLE storage_drives
(
    id              BIGINT                      NOT NULL,
    organization_id BIGINT                      NOT NULL,
    display_name    VARCHAR(255)                NOT NULL,
    drive_type      VARCHAR(255)                NOT NULL,
    root_path       VARCHAR(255)                NOT NULL,
    active          BOOLEAN                     NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_storage_drives PRIMARY KEY (id)
);

ALTER TABLE assay_storage_folders
    ADD id BIGINT;

ALTER TABLE assay_storage_folders
    ADD is_primary BOOLEAN;

ALTER TABLE assay_storage_folders
    ADD storage_drive_folder_id BIGINT;

ALTER TABLE program_storage_folders
    ADD id BIGINT;

ALTER TABLE program_storage_folders
    ADD is_primary BOOLEAN;

ALTER TABLE program_storage_folders
    ADD storage_drive_folder_id BIGINT;

ALTER TABLE study_storage_folders
    ADD id BIGINT;

ALTER TABLE study_storage_folders
    ADD is_primary BOOLEAN;

ALTER TABLE study_storage_folders
    ADD storage_drive_folder_id BIGINT;

ALTER TABLE assay_storage_folders
    ALTER COLUMN is_primary SET NOT NULL;

ALTER TABLE program_storage_folders
    ALTER COLUMN is_primary SET NOT NULL;

ALTER TABLE study_storage_folders
    ALTER COLUMN is_primary SET NOT NULL;

ALTER TABLE programs
    ADD organization_id BIGINT;

ALTER TABLE programs
    ALTER COLUMN organization_id SET NOT NULL;

ALTER TABLE assay_storage_folders
    ALTER COLUMN storage_drive_folder_id SET NOT NULL;

ALTER TABLE program_storage_folders
    ALTER COLUMN storage_drive_folder_id SET NOT NULL;

ALTER TABLE study_storage_folders
    ALTER COLUMN storage_drive_folder_id SET NOT NULL;

ALTER TABLE organizations
    ADD CONSTRAINT uc_organizations_name UNIQUE (name);

ALTER TABLE program_storage_folders
    ADD CONSTRAINT uk_program_storage_folder UNIQUE (program_id, storage_drive_folder_id);

ALTER TABLE assay_storage_folders
    ADD CONSTRAINT uq_assay_storage_folders UNIQUE (storage_drive_folder_id, assay_id);

ALTER TABLE egnyte_drives
    ADD CONSTRAINT uq_egnyte_drives UNIQUE (egnyte_integration_id, name);

ALTER TABLE egnyte_integrations
    ADD CONSTRAINT uq_egnyte_integrations UNIQUE (organization_id, tenant_name);

ALTER TABLE local_drives
    ADD CONSTRAINT uq_local_drives UNIQUE (organization_id, name);

ALTER TABLE s3_buckets
    ADD CONSTRAINT uq_s3_buckets UNIQUE (aws_integration_id, name);

ALTER TABLE storage_drives
    ADD CONSTRAINT uq_storage_drives UNIQUE (organization_id, display_name);

ALTER TABLE study_storage_folders
    ADD CONSTRAINT uq_study_storage_folders UNIQUE (storage_drive_folder_id, study_id);

ALTER TABLE assay_storage_folders
    ADD CONSTRAINT FK_ASSAY_STORAGE_FOLDERS_ON_STORAGE_DRIVE_FOLDER FOREIGN KEY (storage_drive_folder_id) REFERENCES storage_drive_folders (id);

ALTER TABLE aws_integrations
    ADD CONSTRAINT FK_AWS_INTEGRATIONS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE egnyte_drives
    ADD CONSTRAINT FK_EGNYTE_DRIVES_ON_EGNYTE_INTEGRATION FOREIGN KEY (egnyte_integration_id) REFERENCES egnyte_integrations (id);

ALTER TABLE egnyte_drives
    ADD CONSTRAINT FK_EGNYTE_DRIVES_ON_STORAGE_DRIVE FOREIGN KEY (storage_drive_id) REFERENCES storage_drives (id);

ALTER TABLE egnyte_drive_folders
    ADD CONSTRAINT FK_EGNYTE_DRIVE_FOLDERS_ON_EGNYTE_DRIVE FOREIGN KEY (egnyte_drive_id) REFERENCES egnyte_drives (id);

ALTER TABLE egnyte_drive_folders
    ADD CONSTRAINT FK_EGNYTE_DRIVE_FOLDERS_ON_STORAGE_DRIVE_FOLDER FOREIGN KEY (storage_drive_folder_id) REFERENCES storage_drive_folders (id);

ALTER TABLE egnyte_integrations
    ADD CONSTRAINT FK_EGNYTE_INTEGRATIONS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE local_drives
    ADD CONSTRAINT FK_LOCAL_DRIVES_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE local_drives
    ADD CONSTRAINT FK_LOCAL_DRIVES_ON_STORAGE_DRIVE FOREIGN KEY (storage_drive_id) REFERENCES storage_drives (id);

ALTER TABLE local_drive_folders
    ADD CONSTRAINT FK_LOCAL_DRIVE_FOLDERS_ON_LOCAL_DRIVE FOREIGN KEY (local_drive_id) REFERENCES local_drives (id);

ALTER TABLE local_drive_folders
    ADD CONSTRAINT FK_LOCAL_DRIVE_FOLDERS_ON_STORAGE_DRIVE_FOLDER FOREIGN KEY (storage_drive_folder_id) REFERENCES storage_drive_folders (id);

ALTER TABLE programs
    ADD CONSTRAINT FK_PROGRAMS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE program_storage_folders
    ADD CONSTRAINT FK_PROGRAM_STORAGE_FOLDERS_ON_STORAGE_DRIVE_FOLDER FOREIGN KEY (storage_drive_folder_id) REFERENCES storage_drive_folders (id);

ALTER TABLE s3_buckets
    ADD CONSTRAINT FK_S3_BUCKETS_ON_AWS_INTEGRATION FOREIGN KEY (aws_integration_id) REFERENCES aws_integrations (id);

ALTER TABLE s3_buckets
    ADD CONSTRAINT FK_S3_BUCKETS_ON_STORAGE_DRIVE FOREIGN KEY (storage_drive_id) REFERENCES storage_drives (id);

ALTER TABLE s3_bucket_folders
    ADD CONSTRAINT FK_S3_BUCKET_FOLDERS_ON_S3_BUCKET FOREIGN KEY (s3_bucket_id) REFERENCES s3_buckets (id);

ALTER TABLE s3_bucket_folders
    ADD CONSTRAINT FK_S3_BUCKET_FOLDERS_ON_STORAGE_DRIVE_FOLDER FOREIGN KEY (storage_drive_folder_id) REFERENCES storage_drive_folders (id);

ALTER TABLE storage_drives
    ADD CONSTRAINT FK_STORAGE_DRIVES_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE storage_drive_folders
    ADD CONSTRAINT FK_STORAGE_DRIVE_FOLDERS_ON_STORAGE_DRIVE FOREIGN KEY (storage_drive_id) REFERENCES storage_drives (id);

ALTER TABLE study_storage_folders
    ADD CONSTRAINT FK_STUDY_STORAGE_FOLDERS_ON_STORAGE_DRIVE_FOLDER FOREIGN KEY (storage_drive_folder_id) REFERENCES storage_drive_folders (id);

ALTER TABLE assays
    DROP CONSTRAINT fk_assays_on_storage_folder;

ALTER TABLE assay_storage_folders
    DROP CONSTRAINT fk_assstofol_on_file_store_folder;

ALTER TABLE programs
    DROP CONSTRAINT fk_programs_on_storage_folder;

ALTER TABLE program_storage_folders
    DROP CONSTRAINT fk_prostofol_on_file_store_folder;

ALTER TABLE studies
    DROP CONSTRAINT fk_studies_on_storage_folder;

ALTER TABLE study_storage_folders
    DROP CONSTRAINT fk_stustofol_on_file_store_folder;

ALTER TABLE assay_storage_folders
    DROP COLUMN storage_folder_id;

ALTER TABLE program_storage_folders
    DROP COLUMN storage_folder_id;

ALTER TABLE study_storage_folders
    DROP COLUMN storage_folder_id;

ALTER TABLE assays
    DROP COLUMN storage_folder_id;

ALTER TABLE programs
    DROP COLUMN storage_folder_id;

ALTER TABLE studies
    DROP COLUMN storage_folder_id;

ALTER TABLE assay_storage_folders
    ADD CONSTRAINT pk_assay_storage_folders PRIMARY KEY (id);

ALTER TABLE program_storage_folders
    ADD CONSTRAINT pk_program_storage_folders PRIMARY KEY (id);

ALTER TABLE study_storage_folders
    ADD CONSTRAINT pk_study_storage_folders PRIMARY KEY (id);