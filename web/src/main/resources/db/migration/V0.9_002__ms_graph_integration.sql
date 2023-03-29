CREATE TABLE ms_graph_integrations
(
    id              BIGINT       NOT NULL,
    organization_id BIGINT       NOT NULL,
    tenant_id       VARCHAR(255) NOT NULL,
    client_id       VARCHAR(255),
    client_secret   VARCHAR(1024),
    active          BOOLEAN      NOT NULL,
    CONSTRAINT pk_ms_graph_integrations PRIMARY KEY (id)
);

CREATE TABLE onedrive_drives
(
    id                     BIGINT       NOT NULL,
    storage_drive_id       BIGINT       NOT NULL,
    msgraph_integration_id BIGINT       NOT NULL,
    name                   VARCHAR(255) NOT NULL,
    drive_id               VARCHAR(255) NOT NULL,
    web_url                VARCHAR(255) NOT NULL,
    CONSTRAINT pk_onedrive_drives PRIMARY KEY (id)
);

CREATE TABLE onedrive_folders
(
    id                      BIGINT       NOT NULL,
    storage_drive_folder_id BIGINT       NOT NULL,
    onedrive_drive_id       BIGINT       NOT NULL,
    folder_id               VARCHAR(255) NOT NULL,
    web_url                 VARCHAR(255) NOT NULL,
    path                    VARCHAR(2048),
    CONSTRAINT pk_onedrive_folders PRIMARY KEY (id)
);

CREATE TABLE sharepoint_sites
(
    id                     BIGINT       NOT NULL,
    msgraph_integration_id BIGINT       NOT NULL,
    name                   VARCHAR(255) NOT NULL,
    url                    VARCHAR(255) NOT NULL,
    site_id                VARCHAR(255),
    active                 BOOLEAN      NOT NULL,
    CONSTRAINT pk_sharepoint_sites PRIMARY KEY (id)
);

ALTER TABLE ms_graph_integrations
    ADD CONSTRAINT FK_MS_GRAPH_INTEGRATIONS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE onedrive_drives
    ADD CONSTRAINT FK_ONEDRIVE_DRIVES_ON_MSGRAPH_INTEGRATION FOREIGN KEY (msgraph_integration_id) REFERENCES ms_graph_integrations (id);

ALTER TABLE onedrive_drives
    ADD CONSTRAINT FK_ONEDRIVE_DRIVES_ON_STORAGE_DRIVE FOREIGN KEY (storage_drive_id) REFERENCES storage_drives (id);

ALTER TABLE onedrive_folders
    ADD CONSTRAINT FK_ONEDRIVE_FOLDERS_ON_ONEDRIVE_DRIVE FOREIGN KEY (onedrive_drive_id) REFERENCES onedrive_drives (id);

ALTER TABLE onedrive_folders
    ADD CONSTRAINT FK_ONEDRIVE_FOLDERS_ON_STORAGE_DRIVE_FOLDER FOREIGN KEY (storage_drive_folder_id) REFERENCES storage_drive_folders (id);

ALTER TABLE sharepoint_sites
    ADD CONSTRAINT FK_SHAREPOINT_SITES_ON_MSGRAPH_INTEGRATION FOREIGN KEY (msgraph_integration_id) REFERENCES ms_graph_integrations (id);