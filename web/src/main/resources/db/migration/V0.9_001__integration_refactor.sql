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
    api_token       VARCHAR(1024),
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

ALTER TABLE programs
    ADD organization_id BIGINT;

ALTER TABLE organizations
    ADD CONSTRAINT uc_organizations_name UNIQUE (name);

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

ALTER TABLE assays
    DROP CONSTRAINT fk_assays_on_storage_folder;

ALTER TABLE programs
    DROP CONSTRAINT fk_programs_on_storage_folder;

ALTER TABLE studies
    DROP CONSTRAINT fk_studies_on_storage_folder;

/*  Migrate legacy data  */

-- Add default organization
INSERT INTO organizations (id, name, description, active, created_at, updated_at)
VALUES (nextval('hibernate_sequence'), 'My Organization', 'Default organization', true, now(), now());

UPDATE programs
SET organization_id = (select max(id) from organizations)
WHERE organization_id is null;


-- Update existing program/study/assay storage folders
INSERT INTO program_storage_folders (id, program_id, storage_folder_id, is_primary)
SELECT nextval('hibernate_sequence'), p.id, p.storage_folder_id, true
FROM programs p
WHERE p.id NOT IN (SELECT program_id FROM program_storage_folders)
    AND p.storage_folder_id IS NOT NULL
;

UPDATE program_storage_folders
SET id = nextval('hibernate_sequence'), is_primary = true
WHERE id is null;
;

INSERT INTO study_storage_folders (id, study_id, storage_folder_id, is_primary)
SELECT nextval('hibernate_sequence'), s.id, s.storage_folder_id, true
FROM studies s
WHERE s.id NOT IN (SELECT study_id FROM study_storage_folders)
    AND s.storage_folder_id is not null
;

UPDATE study_storage_folders
SET id = nextval('hibernate_sequence'), is_primary = false
WHERE id is null
;

UPDATE study_storage_folders
SET is_primary = true
WHERE storage_folder_id in (
    select ssf.storage_folder_id
    from study_storage_folders ssf
             join file_store_folders fsf on ssf.storage_folder_id = fsf.id
             join file_storage_locations fsl on fsf.file_storage_location_id = fsl.id
    where fsl.type in ('EGNYTE_API', 'LOCAL_FILE_SYSTEM')
    )
;

INSERT INTO assay_storage_folders (id, assay_id, storage_folder_id, is_primary)
SELECT nextval('hibernate_sequence'), a.id, a.storage_folder_id, true
FROM assays a
WHERE a.id NOT IN (SELECT assay_id FROM assay_storage_folders)
    AND a.storage_folder_id IS NOT NULL
;

UPDATE assay_storage_folders
SET id = nextval('hibernate_sequence'), is_primary = false
WHERE id is null
;

UPDATE assay_storage_folders
SET is_primary = true
WHERE storage_folder_id in (
    select ssf.storage_folder_id
    from assay_storage_folders ssf
             join file_store_folders fsf on ssf.storage_folder_id = fsf.id
             join file_storage_locations fsl on fsf.file_storage_location_id = fsl.id
    where fsl.type in ('EGNYTE_API', 'LOCAL_FILE_SYSTEM')
)
;

ALTER TABLE storage_drive_folders
    ADD old_id BIGINT;

-- Add Egnyte integrations and drives
insert into egnyte_integrations (id, organization_id, tenant_name, root_url, api_token, qps, active, created_at, updated_at)
select nextval('hibernate_sequence'), (select max(id) from organizations), 'PLACEHOLDER', 'PLACEHOLDER', null, 1, false, n.now, n.now
from  (select now() as now) n
where exists(
    select i.id
    from integration_instances i
        join integration_definitions d on i.integration_definition_id = d.id
    where d.type = 'EGNYTE'
);

insert into storage_drives (id, organization_id, display_name, drive_type, root_path, active, created_at, updated_at)
select nextval('hibernate_sequence'), (select max(id) from organizations), 'Egnyte Shared Drive', 'EGNYTE', '/Shared', i.active as active, i.created_at, i.updated_at
from integration_instances i
    join integration_definitions d on i.integration_definition_id = d.id
where d.type = 'EGNYTE'
;

insert into egnyte_drives (id, storage_drive_id, egnyte_integration_id, name, created_at, updated_at)
select nextval('hibernate_sequence'), s.id, (select max(id) from egnyte_integrations), 'Shared', s.created_at, s.updated_at
from storage_drives s
where s.drive_type = 'EGNYTE'
;

insert into storage_drive_folders (id, storage_drive_id, path, name, is_browser_root, is_study_root, is_write_enabled, is_delete_enabled, created_at, updated_at)
select
    nextval('hibernate_sequence'),
    (select max(s.id) from storage_drives s where s.drive_type = 'EGNYTE'),
    l.path,
    l.name,
    true,
    l.default_study_location,
    true,
    false,
    now(),
    now()
from file_storage_locations l
where l.type = 'EGNYTE_API'
;

insert into egnyte_drive_folders (id, storage_drive_folder_id, egnyte_drive_id, folder_id, web_url, created_at, updated_at)
select
    nextval('hibernate_sequence'),
    s.id,
    (select max(e.id) from egnyte_drives e),
    'PLACEHOLDER',
    null,
    s.created_at,
    s.updated_at
from
    file_storage_locations l
    join storage_drive_folders s on l.path = s.path and l.name = s.name
where l.type = 'EGNYTE_API'
;

insert into storage_drive_folders (id, storage_drive_id, path, name, is_browser_root, is_study_root, is_write_enabled, is_delete_enabled, created_at, updated_at, old_id)
select
    nextval('hibernate_sequence'),
    (select max(s.id) from storage_drives s where s.drive_type = 'EGNYTE'),
    f.path,
    f.name,
    false,
    false,
    true,
    false,
    now(),
    now(),
    f.id
from file_store_folders f
    join file_storage_locations fsl on f.file_storage_location_id = fsl.id
where fsl.type = 'EGNYTE_API'
;

insert into egnyte_drive_folders (id, storage_drive_folder_id, egnyte_drive_id, folder_id, web_url, created_at, updated_at)
select
    nextval('hibernate_sequence'),
    s.id,
    (select max(e.id) from egnyte_drives e),
    COALESCE(f.reference_id, 'PLACEHOLDER'),
    f.url,
    s.created_at,
    s.updated_at
from
    file_store_folders f
    join file_storage_locations fsl on f.file_storage_location_id = fsl.id
    join storage_drive_folders s on f.path = s.path and f.name = s.name
where fsl.type = 'EGNYTE_API'
;


-- Add AWS integration and buckets
insert into aws_integrations (id, name, region, organization_id, active, use_iam, created_at, updated_at)
select nextval('hibernate_sequence'), 'Default AWS Integration', 'us-east-1', (select max(id) from organizations), false, false, n.now, n.now
from (select now() as now) n
where exists(
      select i.id
      from integration_instances i
               join integration_definitions d on i.integration_definition_id = d.id
      where d.type = 'AWS_S3'
  );

insert into storage_drives (id, organization_id, display_name, drive_type, root_path, active, created_at, updated_at)
select nextval('hibernate_sequence'), (select max(id) from organizations), concat('S3: ', regexp_replace(i.name, 'aws-s3-', '')) as display_name, 'S3', '', i.active as active, i.created_at, i.updated_at
from integration_instances i
    join integration_definitions d on i.integration_definition_id = d.id
where d.type = 'AWS_S3'
;

insert into s3_buckets (id, aws_integration_id, storage_drive_id, name, created_at, updated_at)
select nextval('hibernate_sequence'), (select max(id) from aws_integrations), s.id, regexp_replace(s.display_name, 'S3: ', ''), s.created_at, s.updated_at
from storage_drives s
where s.drive_type = 'S3'
;

insert into storage_drive_folders (id, storage_drive_id, path, name, is_browser_root, is_study_root, is_write_enabled, is_delete_enabled, created_at, updated_at)
select
    nextval('hibernate_sequence'),
    d.id,
    l.path,
    l.display_name,
    true,
    l.default_study_location,
    true,
    false,
    now(),
    now()
from file_storage_locations l
    join integration_instances i on l.integration_instance_id = i.id
    join storage_drives d on d.display_name = concat('S3: ', regexp_replace(i.name, 'aws-s3-', '')) and d.drive_type = 'S3'
where l.type = 'AWS_S3'
;

insert into storage_drive_folders (id, storage_drive_id, path, name, is_browser_root, is_study_root, is_write_enabled, is_delete_enabled, created_at, updated_at, old_id)
select
    nextval('hibernate_sequence'),
    d.id,
    f.path,
    f.name,
    false,
    false,
    true,
    false,
    now(),
    now(),
    f.id
from
    file_store_folders f
    join file_storage_locations l on f.file_storage_location_id = l.id
    join integration_instances i on l.integration_instance_id = i.id
    join storage_drives d on d.display_name = concat('S3: ', regexp_replace(i.name, 'aws-s3-', '')) and d.drive_type = 'S3'
where l.type = 'AWS_S3'
;

insert into s3_bucket_folders (id, storage_drive_folder_id, s3_bucket_id, created_at, updated_at)
select
    nextval('hibernate_sequence'),
    f.id,
    s.id,
    s.created_at,
    s.updated_at
from
    storage_drive_folders f
        join storage_drives d on f.storage_drive_id = d.id
        join s3_buckets s on d.id = s.storage_drive_id
;


-- Add local drives
insert into storage_drives (id, organization_id, display_name, drive_type, root_path, active, created_at, updated_at)
select nextval('hibernate_sequence'), (select max(id) from organizations), 'Local Drive', 'LOCAL', 'PLACEHOLDER', i.active, i.created_at, i.updated_at
from integration_instances i
    join file_storage_locations l on l.integration_instance_id = i.id
where l.type = 'LOCAL_FILE_SYSTEM';
;

insert into local_drives (id, storage_drive_id, organization_id, name, created_at, updated_at)
select nextval('hibernate_sequence'), s.id, (select max(id) from organizations), 'Local Drive', s.created_at, s.updated_at
from storage_drives s
where s.drive_type = 'LOCAL'
;

insert into storage_drive_folders (id, storage_drive_id, path, name, is_browser_root, is_study_root, is_write_enabled, is_delete_enabled, created_at, updated_at)
select
    nextval('hibernate_sequence'),
    (select max(s.id) from storage_drives s where s.drive_type = 'LOCAL'),
    l.path,
    l.name,
    true,
    l.default_study_location,
    true,
    false,
    now(),
    now()
from file_storage_locations l
where l.type = 'LOCAL_FILE_SYSTEM'
;

insert into local_drive_folders (id, storage_drive_folder_id, local_drive_id, created_at, updated_at)
select
    nextval('hibernate_sequence'),
    s.id,
    (select max(e.id) from local_drives e),
    s.created_at,
    s.updated_at
from
    file_storage_locations l
        join storage_drive_folders s on l.path = s.path and l.name = s.name
where l.type = 'LOCAL_FILE_SYSTEM'
;

insert into storage_drive_folders (id, storage_drive_id, path, name, is_browser_root, is_study_root, is_write_enabled, is_delete_enabled, created_at, updated_at, old_id)
select
    nextval('hibernate_sequence'),
    (select max(s.id) from storage_drives s where s.drive_type = 'LOCAL'),
    f.path,
    f.name,
    false,
    false,
    true,
    false,
    now(),
    now(),
    f.id
from file_store_folders f
         join file_storage_locations fsl on f.file_storage_location_id = fsl.id
where fsl.type = 'LOCAL_FILE_SYSTEM'
;

insert into local_drive_folders (id, storage_drive_folder_id, local_drive_id, created_at, updated_at)
select
    nextval('hibernate_sequence'),
    s.id,
    (select max(e.id) from local_drives e),
    s.created_at,
    s.updated_at
from
    file_store_folders f
        join file_storage_locations fsl on f.file_storage_location_id = fsl.id
        join storage_drive_folders s on f.path = s.path and f.name = s.name
where fsl.type = 'LOCAL_FILE_SYSTEM'
;


UPDATE program_storage_folders
SET storage_drive_folder_id = (SELECT id FROM storage_drive_folders WHERE old_id = program_storage_folders.storage_folder_id)
WHERE storage_drive_folder_id IS NULL;

UPDATE study_storage_folders
SET storage_drive_folder_id = (SELECT id FROM storage_drive_folders WHERE old_id = study_storage_folders.storage_folder_id)
WHERE storage_drive_folder_id IS NULL;

UPDATE assay_storage_folders
SET storage_drive_folder_id = (SELECT id FROM storage_drive_folders WHERE old_id = assay_storage_folders.storage_folder_id)
WHERE storage_drive_folder_id IS NULL;

-- Update tables & constraints

ALTER TABLE assays
    DROP COLUMN storage_folder_id;

ALTER TABLE programs
    DROP COLUMN storage_folder_id;

ALTER TABLE studies
    DROP COLUMN storage_folder_id;

ALTER TABLE programs
    ALTER COLUMN organization_id SET NOT NULL;


ALTER TABLE program_storage_folders
    ALTER COLUMN storage_drive_folder_id SET NOT NULL;

ALTER TABLE program_storage_folders
    ALTER COLUMN is_primary SET NOT NULL;

ALTER TABLE program_storage_folders
    ADD CONSTRAINT uk_program_storage_folder UNIQUE (program_id, storage_drive_folder_id);

ALTER TABLE program_storage_folders
    ADD CONSTRAINT FK_PROGRAM_STORAGE_FOLDERS_ON_STORAGE_DRIVE_FOLDER FOREIGN KEY (storage_drive_folder_id) REFERENCES storage_drive_folders (id);

ALTER TABLE program_storage_folders
    DROP CONSTRAINT fk_prostofol_on_file_store_folder;

ALTER TABLE program_storage_folders
    DROP COLUMN storage_folder_id;

ALTER TABLE program_storage_folders
    ADD CONSTRAINT pk_program_storage_folders PRIMARY KEY (id);


ALTER TABLE study_storage_folders
    ALTER COLUMN storage_drive_folder_id SET NOT NULL;

ALTER TABLE study_storage_folders
    ALTER COLUMN is_primary SET NOT NULL;

ALTER TABLE study_storage_folders
    ADD CONSTRAINT uq_study_storage_folders UNIQUE (storage_drive_folder_id, study_id);

ALTER TABLE study_storage_folders
    ADD CONSTRAINT FK_STUDY_STORAGE_FOLDERS_ON_STORAGE_DRIVE_FOLDER FOREIGN KEY (storage_drive_folder_id) REFERENCES storage_drive_folders (id);

ALTER TABLE study_storage_folders
    DROP CONSTRAINT fk_stustofol_on_file_store_folder;

ALTER TABLE study_storage_folders
    DROP COLUMN storage_folder_id;

ALTER TABLE study_storage_folders
    ADD CONSTRAINT pk_study_storage_folders PRIMARY KEY (id);


ALTER TABLE assay_storage_folders
    ALTER COLUMN storage_drive_folder_id SET NOT NULL;

ALTER TABLE assay_storage_folders
    ALTER COLUMN is_primary SET NOT NULL;

ALTER TABLE assay_storage_folders
    ADD CONSTRAINT FK_ASSAY_STORAGE_FOLDERS_ON_STORAGE_DRIVE_FOLDER FOREIGN KEY (storage_drive_folder_id) REFERENCES storage_drive_folders (id);

ALTER TABLE assay_storage_folders
    DROP CONSTRAINT fk_assstofol_on_file_store_folder;

ALTER TABLE assay_storage_folders
    DROP COLUMN storage_folder_id;

ALTER TABLE assay_storage_folders
    ADD CONSTRAINT pk_assay_storage_folders PRIMARY KEY (id);

ALTER TABLE assay_storage_folders
    ADD CONSTRAINT uq_assay_storage_folders UNIQUE (storage_drive_folder_id, assay_id);

ALTER TABLE storage_drive_folders
    DROP COLUMN old_id;

