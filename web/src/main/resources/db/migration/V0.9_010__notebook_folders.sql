ALTER TABLE assays
    DROP CONSTRAINT fk_assays_on_notebook_folder;

ALTER TABLE programs
    DROP CONSTRAINT fk_programs_on_notebook_folder;

ALTER TABLE studies
    DROP CONSTRAINT fk_studies_on_notebook_folder;

ALTER TABLE studies
    DROP CONSTRAINT uq_study_code;

ALTER TABLE studies
    DROP CONSTRAINT uq_study_name;

CREATE TABLE assay_notebook_folders
(
    id            BIGINT  NOT NULL,
    assay_id      BIGINT  NOT NULL,
    eln_folder_id BIGINT  NOT NULL,
    is_primary    BOOLEAN NOT NULL,
    CONSTRAINT pk_assay_notebook_folders PRIMARY KEY (id)
);

CREATE TABLE program_notebook_folders
(
    id            BIGINT  NOT NULL,
    program_id    BIGINT  NOT NULL,
    eln_folder_id BIGINT  NOT NULL,
    is_primary    BOOLEAN NOT NULL,
    CONSTRAINT pk_program_notebook_folders PRIMARY KEY (id)
);

CREATE TABLE study_notebook_folders
(
    id            BIGINT  NOT NULL,
    study_id      BIGINT  NOT NULL,
    eln_folder_id BIGINT  NOT NULL,
    is_primary    BOOLEAN NOT NULL,
    CONSTRAINT pk_study_notebook_folders PRIMARY KEY (id)
);

ALTER TABLE assay_notebook_folders
    ADD CONSTRAINT uc_0be3c7d40e9c9f1610bf8d7fc UNIQUE (assay_id, eln_folder_id);

ALTER TABLE study_notebook_folders
    ADD CONSTRAINT uc_84dc744bd327755fedc46d748 UNIQUE (study_id, eln_folder_id);

ALTER TABLE program_notebook_folders
    ADD CONSTRAINT uc_fcd667a66d5d9bf49b7ba7445 UNIQUE (program_id, eln_folder_id);

ALTER TABLE assay_notebook_folders
    ADD CONSTRAINT FK_ASSAY_NOTEBOOK_FOLDERS_ON_ASSAY FOREIGN KEY (assay_id) REFERENCES assays (id);

ALTER TABLE assay_notebook_folders
    ADD CONSTRAINT FK_ASSAY_NOTEBOOK_FOLDERS_ON_ELN_FOLDER FOREIGN KEY (eln_folder_id) REFERENCES eln_folders (id);

ALTER TABLE program_notebook_folders
    ADD CONSTRAINT FK_PROGRAM_NOTEBOOK_FOLDERS_ON_ELN_FOLDER FOREIGN KEY (eln_folder_id) REFERENCES eln_folders (id);

ALTER TABLE program_notebook_folders
    ADD CONSTRAINT FK_PROGRAM_NOTEBOOK_FOLDERS_ON_PROGRAM FOREIGN KEY (program_id) REFERENCES programs (id);

ALTER TABLE study_notebook_folders
    ADD CONSTRAINT FK_STUDY_NOTEBOOK_FOLDERS_ON_ELN_FOLDER FOREIGN KEY (eln_folder_id) REFERENCES eln_folders (id);

ALTER TABLE study_notebook_folders
    ADD CONSTRAINT FK_STUDY_NOTEBOOK_FOLDERS_ON_STUDY FOREIGN KEY (study_id) REFERENCES studies (id);

/* Migrate the old folder references */

INSERT INTO program_notebook_folders (id, program_id, eln_folder_id, is_primary)
SELECT nextval('hibernate_sequence'), p.id, p.notebook_folder_id, true
FROM programs p
WHERE p.notebook_folder_id IS NOT NULL;

INSERT INTO study_notebook_folders (id, study_id, eln_folder_id, is_primary)
SELECT nextval('hibernate_sequence'), s.id, s.notebook_folder_id, true
FROM studies s
WHERE s.notebook_folder_id IS NOT NULL;

INSERT INTO assay_notebook_folders (id, assay_id, eln_folder_id, is_primary)
SELECT nextval('hibernate_sequence'), a.id, a.notebook_folder_id, true
FROM assays a
WHERE a.notebook_folder_id IS NOT NULL;

/* Drop the old columns */

ALTER TABLE assays
    DROP COLUMN notebook_folder_id;

ALTER TABLE programs
    DROP COLUMN notebook_folder_id;

ALTER TABLE studies
    DROP COLUMN notebook_folder_id;