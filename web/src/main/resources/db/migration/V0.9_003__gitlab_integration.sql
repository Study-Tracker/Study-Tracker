CREATE TABLE assay_git_repositories
(
    assay_id          BIGINT NOT NULL,
    git_repository_id BIGINT NOT NULL,
    CONSTRAINT pk_assay_git_repositories PRIMARY KEY (assay_id, git_repository_id)
);

CREATE TABLE git_groups
(
    id               BIGINT                      NOT NULL,
    organization_id  BIGINT                      NOT NULL,
    parent_group_id  BIGINT,
    display_name     VARCHAR(255)                NOT NULL,
    web_url          VARCHAR(1024)               NOT NULL,
    active           BOOLEAN                     NOT NULL,
    git_service_type VARCHAR(255)                NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_git_groups PRIMARY KEY (id)
);

CREATE TABLE git_repositories
(
    id           BIGINT                      NOT NULL,
    git_group_id BIGINT                      NOT NULL,
    display_name VARCHAR(255)                NOT NULL,
    description  VARCHAR(255),
    web_url      VARCHAR(1024)               NOT NULL,
    ssh_url      VARCHAR(1024),
    http_url     VARCHAR(1024),
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_git_repositories PRIMARY KEY (id)
);

CREATE TABLE gitlab_groups
(
    id                    BIGINT        NOT NULL,
    gitlab_integration_id BIGINT        NOT NULL,
    git_group_id          BIGINT        NOT NULL,
    group_id              INTEGER       NOT NULL,
    name                  VARCHAR(255)  NOT NULL,
    path                  VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_gitlab_groups PRIMARY KEY (id)
);

CREATE TABLE gitlab_integrations
(
    id              BIGINT                      NOT NULL,
    organization_id BIGINT                      NOT NULL,
    name            VARCHAR(255)                NOT NULL,
    root_url        VARCHAR(255)                NOT NULL,
    username        VARCHAR(255),
    password        VARCHAR(255),
    access_token    VARCHAR(2048),
    active          BOOLEAN                     NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_gitlab_integrations PRIMARY KEY (id)
);

CREATE TABLE gitlab_repositories
(
    id                BIGINT        NOT NULL,
    gitlab_group_id   BIGINT        NOT NULL,
    git_repository_id BIGINT        NOT NULL,
    repository_id     INTEGER       NOT NULL,
    name              VARCHAR(255)  NOT NULL,
    path              VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_gitlab_repositories PRIMARY KEY (id)
);

CREATE TABLE program_git_groups
(
    git_group_id BIGINT NOT NULL,
    program_id   BIGINT NOT NULL,
    CONSTRAINT pk_program_git_groups PRIMARY KEY (git_group_id, program_id)
);

CREATE TABLE study_git_repositories
(
    git_repository_id BIGINT NOT NULL,
    study_id          BIGINT NOT NULL,
    CONSTRAINT pk_study_git_repositories PRIMARY KEY (git_repository_id, study_id)
);

ALTER TABLE git_groups
    ADD CONSTRAINT uq_git_groups UNIQUE (organization_id, display_name);

ALTER TABLE git_repositories
    ADD CONSTRAINT uq_git_repositories UNIQUE (git_group_id, display_name);

ALTER TABLE gitlab_groups
    ADD CONSTRAINT uq_gitlab_groups UNIQUE (gitlab_integration_id, path);

ALTER TABLE gitlab_integrations
    ADD CONSTRAINT uq_gitlab_integrations UNIQUE (organization_id, root_url);

ALTER TABLE gitlab_repositories
    ADD CONSTRAINT uq_gitlab_repositories UNIQUE (gitlab_group_id, repository_id);

ALTER TABLE gitlab_groups
    ADD CONSTRAINT FK_GITLAB_GROUPS_ON_GITLAB_INTEGRATION FOREIGN KEY (gitlab_integration_id) REFERENCES gitlab_integrations (id);

ALTER TABLE gitlab_groups
    ADD CONSTRAINT FK_GITLAB_GROUPS_ON_GIT_GROUP FOREIGN KEY (git_group_id) REFERENCES git_groups (id);

ALTER TABLE gitlab_integrations
    ADD CONSTRAINT FK_GITLAB_INTEGRATIONS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE gitlab_repositories
    ADD CONSTRAINT FK_GITLAB_REPOSITORIES_ON_GITLAB_GROUP FOREIGN KEY (gitlab_group_id) REFERENCES gitlab_groups (id);

ALTER TABLE gitlab_repositories
    ADD CONSTRAINT FK_GITLAB_REPOSITORIES_ON_GIT_REPOSITORY FOREIGN KEY (git_repository_id) REFERENCES git_repositories (id);

ALTER TABLE git_groups
    ADD CONSTRAINT FK_GIT_GROUPS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE git_groups
    ADD CONSTRAINT FK_GIT_GROUPS_ON_PARENT_GROUP FOREIGN KEY (parent_group_id) REFERENCES git_groups (id);

ALTER TABLE git_repositories
    ADD CONSTRAINT FK_GIT_REPOSITORIES_ON_GIT_GROUP FOREIGN KEY (git_group_id) REFERENCES git_groups (id);

ALTER TABLE assay_git_repositories
    ADD CONSTRAINT fk_assgitrep_on_assay FOREIGN KEY (assay_id) REFERENCES assays (id);

ALTER TABLE assay_git_repositories
    ADD CONSTRAINT fk_assgitrep_on_git_repository FOREIGN KEY (git_repository_id) REFERENCES git_repositories (id);

ALTER TABLE program_git_groups
    ADD CONSTRAINT fk_progitgro_on_git_group FOREIGN KEY (git_group_id) REFERENCES git_groups (id);

ALTER TABLE program_git_groups
    ADD CONSTRAINT fk_progitgro_on_program FOREIGN KEY (program_id) REFERENCES programs (id);

ALTER TABLE study_git_repositories
    ADD CONSTRAINT fk_stugitrep_on_git_repository FOREIGN KEY (git_repository_id) REFERENCES git_repositories (id);

ALTER TABLE study_git_repositories
    ADD CONSTRAINT fk_stugitrep_on_study FOREIGN KEY (study_id) REFERENCES studies (id);

-- Add integration instance for existing GitLab studies
INSERT INTO gitlab_integrations (id, organization_id, name, root_url, username, password, access_token, active, created_at, updated_at)
SELECT nextval('hibernate_sequence'), (select max(id) from organizations), 'GitLab', 'PLACEHOLDER', null, null, null, true, now(), now()
FROM (select count(*) as count from studies where attributes->>'_git.service' = 'gitlab') c
WHERE c.count > 0;

-- Add default root groups for existing GitLab studies
INSERT INTO git_groups (id, organization_id, parent_group_id, display_name, web_url, active, git_service_type, created_at, updated_at)
SELECT nextval('hibernate_sequence'), (select max(id) from organizations), null,  p.attributes->>'_git.group_parent_name', 'PLACEHOLDER', true, 'GITLAB', now(), now()
FROM programs p
WHERE p.attributes->>'_git.service' = 'gitlab'
GROUP BY p.attributes->>'_git.group_parent_name'
;

INSERT INTO gitlab_groups (id, gitlab_integration_id, git_group_id, group_id, name, path)
SELECT nextval('hibernate_sequence'), (select max(id) from gitlab_integrations), gg.id, nullif(p.parent_id, '0')::int, p.parent_name, p.parent_path
FROM
    (
        select
            attributes->>'_git.group_parent_name' as parent_name,
            attributes->>'_git.group_parent_path' as parent_path,
            attributes->>'_git.group_parent_id' as parent_id
        from programs
        where attributes->>'_git.service' = 'gitlab'
    ) p
    JOIN git_groups gg on p.parent_name = gg.display_name
-- WHERE p.attributes->>'_git.service' = 'gitlab'
GROUP BY p.parent_name, p.parent_path, p.parent_id, gg.id
;

-- Add the existing program groups
INSERT INTO git_groups (id, organization_id, parent_group_id, display_name, web_url, active, git_service_type, created_at, updated_at)
SELECT nextval('hibernate_sequence'), (select max(id) from organizations), gg.id, p.attributes->>'_git.group_name', 'PLACEHOLDER', true, 'GITLAB', now(), now()
FROM
    programs p
    JOIN git_groups gg on p.attributes->>'_git.group_parent_name' = gg.display_name
WHERE p.attributes->>'_git.service' = 'gitlab'
GROUP BY
    p.attributes->>'_git.group_name',
    p.attributes->>'_git.group_parent_name',
    gg.id
;

INSERT INTO gitlab_groups (id, gitlab_integration_id, git_group_id, group_id, name, path)
SELECT nextval('hibernate_sequence'), (select max(id) from gitlab_integrations), g.id, nullif(p.attributes->>'_git.group_id', '')::int, p.attributes->>'_git.group_name', p.attributes->>'_git.group_path'
FROM
    programs p
    join git_groups g on g.display_name = p.attributes->>'_git.group_name'
WHERE p.attributes->>'_git.service' = 'gitlab'
GROUP BY
    g.id,
    nullif(p.attributes->>'_git.group_id', '')::int,
    p.attributes->>'_git.group_name',
    p.attributes->>'_git.group_path'
;

INSERT INTO program_git_groups (program_id, git_group_id)
SELECT p.id, g.id
FROM
    programs p
    join git_groups g on g.display_name = p.attributes->>'_git.group_name'
WHERE p.attributes->>'_git.service' = 'gitlab'
;

-- Add existing repositories
INSERT INTO git_repositories (id, git_group_id, display_name, description, web_url, ssh_url, http_url, created_at, updated_at)
SELECT nextval('hibernate_sequence'), gg.id, s.attributes->>'_git.repository_name', null, s.attributes->>'_git.repository_web_url', s.attributes->>'_git.repository_ssh_url', s.attributes->>'_git.repository_http_url', now(), now()
from
    studies s
    join programs p on s.program_id = p.id
    join program_git_groups pgg on pgg.program_id = p.id
    join git_groups gg on gg.id = pgg.git_group_id
where s.attributes->>'_git.service' = 'gitlab'
;

INSERT INTO gitlab_repositories (id, gitlab_group_id, git_repository_id, repository_id, name, path)
SELECT nextval('hibernate_sequence'), glg.id, gr.id, nullif(s.attributes->>'_git.repository_id', '')::int, s.attributes->>'_git.repository_name', s.attributes->>'_git.repository_path'
FROM
    studies s
    join programs p on s.program_id = p.id
    join program_git_groups pgg on pgg.program_id = p.id
    join git_groups gg on gg.id = pgg.git_group_id
    join gitlab_groups glg on glg.git_group_id = gg.id
    join git_repositories gr on gr.display_name = s.attributes->>'_git.repository_name'
WHERE s.attributes->>'_git.service' = 'gitlab'
;

INSERT INTO study_git_repositories (study_id, git_repository_id)
SELECT s.id, gr.id
FROM
    studies s
    join git_repositories gr on gr.display_name = s.attributes->>'_git.repository_name'
WHERE s.attributes->>'_git.service' = 'gitlab'
;