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

ALTER TABLE egnyte_integrations
    ADD CONSTRAINT FK_EGNYTE_INTEGRATIONS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE local_drives
    ADD CONSTRAINT FK_LOCAL_DRIVES_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE local_drives
    ADD CONSTRAINT FK_LOCAL_DRIVES_ON_STORAGE_DRIVE FOREIGN KEY (storage_drive_id) REFERENCES storage_drives (id);

ALTER TABLE s3_buckets
    ADD CONSTRAINT FK_S3_BUCKETS_ON_AWS_INTEGRATION FOREIGN KEY (aws_integration_id) REFERENCES aws_integrations (id);

ALTER TABLE s3_buckets
    ADD CONSTRAINT FK_S3_BUCKETS_ON_STORAGE_DRIVE FOREIGN KEY (storage_drive_id) REFERENCES storage_drives (id);

ALTER TABLE storage_drives
    ADD CONSTRAINT FK_STORAGE_DRIVES_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);


-- Add default organization
INSERT INTO organizations (id, name, description, active, created_at, updated_at)
VALUES (nextval('hibernate_sequence'), 'My Organization', 'Default organization', true, now(), now());


-- Add AWS integration and buckets
insert into aws_integrations (id, name, region, organization_id, active, use_iam, created_at, updated_at)
select nextval('hibernate_sequence'), 'Default AWS Integration', 'us-east-1', (select max(id) from organizations), false, false, now(), now()
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

-- Add Egnyte integrations and drives
insert into egnyte_integrations (id, organization_id, tenant_name, root_url, api_token, qps, active, created_at, updated_at)
select nextval('hibernate_sequence'), (select max(id) from organizations), 'PLACEHOLDER', 'PLACEHOLDER', 'PLACEHOLDER', 1, false, now(), now()
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

-- Add local drives
insert into storage_drives (id, organization_id, display_name, drive_type, root_path, active, created_at, updated_at)
select nextval('hibernate_sequence'), (select max(id) from organizations), 'Local Drive', 'LOCAL', 'PLACEHOLDER', i.active, i.created_at, i.updated_at
from integration_instances i
    join integration_definitions d on i.integration_definition_id = d.id
where d.type = 'LOCAL_FILE_SYSTEM'
;

insert into local_drives (id, storage_drive_id, organization_id, name, created_at, updated_at)
select nextval('hibernate_sequence'), s.id, (select max(id) from organizations), 'Local Drive', s.created_at, s.updated_at
from storage_drives s
where s.drive_type = 'LOCAL'
;