CREATE TABLE assay_storage_folders
(
    assay_id          BIGINT NOT NULL,
    storage_folder_id BIGINT NOT NULL,
    CONSTRAINT pk_assay_storage_folders PRIMARY KEY (assay_id, storage_folder_id)
);

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
    active                  BOOLEAN      NOT NULL,
    CONSTRAINT pk_file_storage_locations PRIMARY KEY (id)
);

CREATE TABLE integration_configuration_schema_fields
(
    id                        BIGINT       NOT NULL,
    display_name              VARCHAR(255) NOT NULL,
    field_name                VARCHAR(255) NOT NULL,
    type                      VARCHAR(255) NOT NULL,
    required                  BOOLEAN      NOT NULL,
    description               VARCHAR(1024),
    active                    BOOLEAN      NOT NULL,
    field_order               INTEGER      NOT NULL,
    integration_definition_id BIGINT       NOT NULL,
    CONSTRAINT pk_integration_configuration_schema_fields PRIMARY KEY (id)
);

CREATE TABLE integration_definitions
(
    id      BIGINT       NOT NULL,
    type    VARCHAR(255) NOT NULL,
    active  BOOLEAN      NOT NULL,
    version INTEGER      NOT NULL,
    CONSTRAINT pk_integration_definitions PRIMARY KEY (id)
);

CREATE TABLE integration_instance_configuration_values
(
    id                      BIGINT        NOT NULL,
    integration_instance_id BIGINT        NOT NULL,
    field_name              VARCHAR(255)  NOT NULL,
    value                   VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_integration_instance_configuration_values PRIMARY KEY (id)
);

CREATE TABLE integration_instances
(
    id                        BIGINT                      NOT NULL,
    integration_definition_id BIGINT                      NOT NULL,
    display_name              VARCHAR(255)                NOT NULL,
    name                      VARCHAR(255)                NOT NULL,
    active                    BOOLEAN                     NOT NULL,
    created_at                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_integration_instances PRIMARY KEY (id)
);

CREATE TABLE program_storage_folders
(
    program_id        BIGINT NOT NULL,
    storage_folder_id BIGINT NOT NULL,
    CONSTRAINT pk_program_storage_folders PRIMARY KEY (program_id, storage_folder_id)
);

CREATE TABLE study_storage_folders
(
    storage_folder_id BIGINT NOT NULL,
    study_id          BIGINT NOT NULL,
    CONSTRAINT pk_study_storage_folders PRIMARY KEY (storage_folder_id, study_id)
);


-- Add placeholder integration & storage location for existing file store folders
ALTER TABLE file_store_folders
    ADD file_storage_location_id BIGINT;

INSERT INTO integration_definitions (id, type, active, version)
values (nextval('hibernate_sequence'), 'PLACEHOLDER_FILE_STORE', false, 1);

INSERT INTO integration_instances (id, integration_definition_id, display_name, name, active, created_at, updated_at)
VALUES (nextval('hibernate_sequence'), (select max(id) from integration_definitions), 'PLACEHOLDER_FILE_STORE', 'PLACEHOLDER_FILE_STORE', false, now(), now());

INSERT INTO file_storage_locations (id, integration_instance_id, type, display_name, name, path, permissions, default_data_location, default_study_location, active)
VALUES (nextval('hibernate_sequence'), (select max(id) from integration_instances), 'LOCAL_FILE_SYSTEM', 'PLACEHOLDER_FILE_STORE', 'PLACEHOLDER_FILE_STORE', '', 'READ_ONLY', false, false, false);

UPDATE file_store_folders
SET file_storage_location_id = (select max(id) from file_storage_locations)
WHERE file_storage_location_id IS NULL;

ALTER TABLE file_store_folders
    ALTER COLUMN file_storage_location_id SET NOT NULL;


-- Update the existing assay_type_fields to have an order, defined by their ID
ALTER TABLE assay_type_fields
    ADD field_order INTEGER;

UPDATE assay_type_fields
SET field_order = f.new_field_order
FROM (
         select *, row_number() over (partition by assay_type_id order by id) as new_field_order
         from assay_type_fields
     ) f
WHERE assay_type_fields.id = f.id;

ALTER TABLE assay_type_fields
    ALTER COLUMN field_order SET NOT NULL;

ALTER TABLE integration_instances
    ADD CONSTRAINT uc_integration_instances_display_name UNIQUE (display_name);

ALTER TABLE integration_instances
    ADD CONSTRAINT uc_integration_instances_name UNIQUE (name);

ALTER TABLE integration_definitions
    ADD CONSTRAINT uc_integrationdefinition_name UNIQUE (type, version);

ALTER TABLE integration_instance_configuration_values
    ADD CONSTRAINT uc_integrationinstanceconfigurationvalue UNIQUE (integration_instance_id, field_name);

ALTER TABLE file_storage_locations
    ADD CONSTRAINT FK_FILE_STORAGE_LOCATIONS_ON_INTEGRATION_INSTANCE FOREIGN KEY (integration_instance_id) REFERENCES integration_instances (id);

ALTER TABLE file_store_folders
    ADD CONSTRAINT FK_FILE_STORE_FOLDERS_ON_FILE_STORAGE_LOCATION FOREIGN KEY (file_storage_location_id) REFERENCES file_storage_locations (id);

ALTER TABLE integration_configuration_schema_fields
    ADD CONSTRAINT FK_INTEGRATIONCONFIGURATIONSCHEMAFIELD_ON_INTEGRATIONDEFINITION FOREIGN KEY (integration_definition_id) REFERENCES integration_definitions (id);

ALTER TABLE integration_instance_configuration_values
    ADD CONSTRAINT FK_INTEGRATIONINSTANCECONFIGURATIONVALUE_ON_INTEGRATIONINSTANCE FOREIGN KEY (integration_instance_id) REFERENCES integration_instances (id);

ALTER TABLE integration_instances
    ADD CONSTRAINT FK_INTEGRATION_INSTANCES_ON_INTEGRATION_DEFINITION FOREIGN KEY (integration_definition_id) REFERENCES integration_definitions (id);

ALTER TABLE assay_storage_folders
    ADD CONSTRAINT fk_assstofol_on_assay FOREIGN KEY (assay_id) REFERENCES assays (id);

ALTER TABLE assay_storage_folders
    ADD CONSTRAINT fk_assstofol_on_file_store_folder FOREIGN KEY (storage_folder_id) REFERENCES file_store_folders (id);

ALTER TABLE program_storage_folders
    ADD CONSTRAINT fk_prostofol_on_file_store_folder FOREIGN KEY (storage_folder_id) REFERENCES file_store_folders (id);

ALTER TABLE program_storage_folders
    ADD CONSTRAINT fk_prostofol_on_program FOREIGN KEY (program_id) REFERENCES programs (id);

ALTER TABLE study_storage_folders
    ADD CONSTRAINT fk_stustofol_on_file_store_folder FOREIGN KEY (storage_folder_id) REFERENCES file_store_folders (id);

ALTER TABLE study_storage_folders
    ADD CONSTRAINT fk_stustofol_on_study FOREIGN KEY (study_id) REFERENCES studies (id);

ALTER TABLE notebook_entry_templates
    DROP CONSTRAINT fk_notebook_entry_templates_on_created_by;

ALTER TABLE notebook_entry_templates
    DROP CONSTRAINT fk_notebook_entry_templates_on_last_modified_by;

DROP TABLE notebook_entry_templates CASCADE;