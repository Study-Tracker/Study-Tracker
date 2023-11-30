ALTER TABLE organizations
    DROP CONSTRAINT uc_organizations_name;

ALTER TABLE egnyte_integrations
    DROP CONSTRAINT uq_egnyte_integrations;

ALTER TABLE gitlab_integrations
    DROP CONSTRAINT uq_gitlab_integrations;

ALTER TABLE programs
    DROP CONSTRAINT uq_program_name;

ALTER TABLE storage_drives
    DROP CONSTRAINT uq_storage_drives;

ALTER TABLE git_groups
    DROP CONSTRAINT uq_git_groups;

ALTER TABLE egnyte_integrations
    ADD CONSTRAINT uc_egnyte_integrations_tenant_name UNIQUE (tenant_name);

ALTER TABLE gitlab_integrations
    ADD CONSTRAINT uc_gitlab_integrations_root_url UNIQUE (root_url);

ALTER TABLE programs
    ADD CONSTRAINT uc_programs_name UNIQUE (name);

ALTER TABLE storage_drives
    ADD CONSTRAINT uc_storage_drives_display_name UNIQUE (display_name);

ALTER TABLE aws_integrations
    DROP CONSTRAINT fk_aws_integrations_on_organization;

ALTER TABLE egnyte_integrations
    DROP CONSTRAINT fk_egnyte_integrations_on_organization;

ALTER TABLE git_groups
    DROP CONSTRAINT fk_git_groups_on_organization;

ALTER TABLE gitlab_integrations
    DROP CONSTRAINT fk_gitlab_integrations_on_organization;

ALTER TABLE ms_graph_integrations
    DROP CONSTRAINT fk_ms_graph_integrations_on_organization;

ALTER TABLE programs
    DROP CONSTRAINT fk_programs_on_organization;

ALTER TABLE storage_drives
    DROP CONSTRAINT fk_storage_drives_on_organization;

DROP TABLE organizations CASCADE;

ALTER TABLE aws_integrations
    DROP COLUMN organization_id;

ALTER TABLE egnyte_integrations
    DROP COLUMN organization_id;

ALTER TABLE git_groups
    DROP COLUMN organization_id;

ALTER TABLE gitlab_integrations
    DROP COLUMN organization_id;

ALTER TABLE ms_graph_integrations
    DROP COLUMN organization_id;

ALTER TABLE programs
    DROP COLUMN organization_id;

ALTER TABLE storage_drives
    DROP COLUMN organization_id;

ALTER TABLE git_groups
    ADD CONSTRAINT uq_git_groups UNIQUE (display_name);