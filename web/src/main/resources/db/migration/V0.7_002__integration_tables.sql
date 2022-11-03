CREATE TABLE file_storage_locations
(
    id                      BIGINT       NOT NULL,
    integration_instance_id BIGINT       NOT NULL,
    type                    VARCHAR(255) NOT NULL,
    display_name            VARCHAR(255) NOT NULL,
    name                    VARCHAR(255) NOT NULL,
    path                    VARCHAR(255) NOT NULL,
    reference_id            VARCHAR(255),
    url                     VARCHAR(255),
    permissions             VARCHAR(255) NOT NULL,
    default_study_location  BOOLEAN      NOT NULL,
    default_data_location   BOOLEAN      NOT NULL,
    CONSTRAINT pk_file_storage_locations PRIMARY KEY (id)
);

CREATE TABLE integration_configuration_schema_fields
(
    id                       BIGINT       NOT NULL,
    display_name             VARCHAR(255) NOT NULL,
    field_name               VARCHAR(255) NOT NULL,
    type                     VARCHAR(255) NOT NULL,
    required                 BOOLEAN      NOT NULL,
    description              VARCHAR(1024),
    active                   BOOLEAN      NOT NULL,
    "order"                  INTEGER      NOT NULL,
    supported_integration_id BIGINT       NOT NULL,
    CONSTRAINT pk_integration_configuration_schema_fields PRIMARY KEY (id)
);

CREATE TABLE integration_instance_configuration_values
(
    id                      BIGINT       NOT NULL,
    integration_instance_id BIGINT       NOT NULL,
    field_name              VARCHAR(255) NOT NULL,
    value                   VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_integration_instance_configuration_values PRIMARY KEY (id)
);

CREATE TABLE integration_instances
(
    id                       BIGINT                      NOT NULL,
    supported_integration_id BIGINT                      NOT NULL,
    display_name             VARCHAR(255)                NOT NULL,
    name                     VARCHAR(255)                NOT NULL,
    active                   BOOLEAN                     NOT NULL,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_integration_instances PRIMARY KEY (id)
);

CREATE TABLE supported_integrations
(
    id      BIGINT       NOT NULL,
    name    VARCHAR(255) NOT NULL,
    active  BOOLEAN      NOT NULL,
    version INTEGER      NOT NULL,
    CONSTRAINT pk_supported_integrations PRIMARY KEY (id)
);


-- Add placeholder integration & storage location for existing file store folders
ALTER TABLE file_store_folders
    ADD file_storage_location_id BIGINT;

INSERT INTO supported_integrations (id, name, active, version)
values (nextval('hibernate_sequence'), 'PLACEHOLDER_FILE_STORE', false, 1);

INSERT INTO integration_instances (id, supported_integration_id, display_name, name, active, created_at, updated_at)
VALUES (nextval('hibernate_sequence'), (select max(id) from supported_integrations), 'PLACEHOLDER_FILE_STORE', 'PLACEHOLDER_FILE_STORE', false, now(), now());

INSERT INTO file_storage_locations (id, integration_instance_id, type, display_name, name, path, permissions)
VALUES (nextval('hibernate_sequence'), (select max(id) from integration_instances), 'LOCAL_FILE_SYSTEM', 'PLACEHOLDER_FILE_STORE', 'PLACEHOLDER_FILE_STORE', '', 'READ_ONLY');

UPDATE file_store_folders
    SET file_storage_location_id = (select max(id) from file_storage_locations)
    WHERE file_storage_location_id IS NULL;

ALTER TABLE file_store_folders
    ALTER COLUMN file_storage_location_id SET NOT NULL;


-- Update the existing assay_type_fields to have an order, defined by their ID
ALTER TABLE assay_type_fields
    ADD "order" INTEGER;

UPDATE assay_type_fields
SET "order" = f.field_order
FROM (
         select *, row_number() over (partition by assay_type_id order by id) as field_order
         from assay_type_fields
     ) f
WHERE assay_type_fields.id = f.id;

ALTER TABLE assay_type_fields
    ALTER COLUMN "order" SET NOT NULL;

ALTER TABLE integration_instances
    ADD CONSTRAINT uc_integration_instances_display_name UNIQUE (display_name);

ALTER TABLE integration_instances
    ADD CONSTRAINT uc_integration_instances_name UNIQUE (name);

ALTER TABLE integration_instance_configuration_values
    ADD CONSTRAINT uc_integrationinstanceconfigurationvalue UNIQUE (integration_instance_id, field_name);

ALTER TABLE supported_integrations
    ADD CONSTRAINT uc_supportedintegration_name UNIQUE (name, version);

ALTER TABLE file_storage_locations
    ADD CONSTRAINT FK_FILE_STORAGE_LOCATIONS_ON_INTEGRATION_INSTANCE FOREIGN KEY (integration_instance_id) REFERENCES integration_instances (id);

ALTER TABLE file_store_folders
    ADD CONSTRAINT FK_FILE_STORE_FOLDERS_ON_FILE_STORAGE_LOCATION FOREIGN KEY (file_storage_location_id) REFERENCES file_storage_locations (id);

ALTER TABLE integration_configuration_schema_fields
    ADD CONSTRAINT FK_INTEGRATIONCONFIGURATIONSCHEMAFIELDS_ON_SUPPORTEDINTEGRATION FOREIGN KEY (supported_integration_id) REFERENCES supported_integrations (id);

ALTER TABLE integration_instance_configuration_values
    ADD CONSTRAINT FK_INTEGRATIONINSTANCECONFIGURATIONVALUE_ON_INTEGRATIONINSTANCE FOREIGN KEY (integration_instance_id) REFERENCES integration_instances (id);

ALTER TABLE integration_instances
    ADD CONSTRAINT FK_INTEGRATION_INSTANCES_ON_SUPPORTED_INTEGRATION FOREIGN KEY (supported_integration_id) REFERENCES supported_integrations (id);

ALTER TABLE notebook_entry_templates
    DROP CONSTRAINT fk_notebook_entry_templates_on_created_by;

ALTER TABLE notebook_entry_templates
    DROP CONSTRAINT fk_notebook_entry_templates_on_last_modified_by;

DROP TABLE notebook_entry_templates CASCADE;

-- Add default supported integrations

-- Egnyte
-- INSERT INTO supported_integrations (id, name, active, version)
-- VALUES (nextval('hibernate_sequence'), 'Egnyte', true, 1);
--
-- INSERT INTO integration_configuration_schema_fields (id, display_name, field_name, type, required, description, active, "order", supported_integration_id)
-- VALUES (nextval('hibernate_sequence'), 'Tenant Name', 'tenant-name', 'STRING', true, 'Tenant name, as it appears in your tenant URL. For example, if you access Egnyte at https://myorg.egnyte.com, then your tenant name is: "myorg"', true, 1, (select max(id) from supported_integrations));
-- INSERT INTO integration_configuration_schema_fields (id, display_name, field_name, type, required, description, active, "order", supported_integration_id)
-- VALUES (nextval('hibernate_sequence'), 'API Token', 'api-token', 'STRING', true, 'API token for making requests to the Egnyte API. This can be generated in the Developer portal.', true, 2, (select max(id) from supported_integrations));
-- INSERT INTO integration_configuration_schema_fields (id, display_name, field_name, type, required, description, active, "order", supported_integration_id)
-- VALUES (nextval('hibernate_sequence'), 'Root Path', 'root-path', 'STRING', true, 'Root folder to use for storing files. This folder must exist in Egnyte.', true, 3, (select max(id) from supported_integrations));