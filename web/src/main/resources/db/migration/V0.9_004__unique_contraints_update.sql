ALTER TABLE assays
    ADD CONSTRAINT uq_assay_code UNIQUE (code, study_id);

ALTER TABLE assays
    ADD CONSTRAINT uq_assay_name UNIQUE (name, study_id);

ALTER TABLE programs
    ADD CONSTRAINT uq_program_name UNIQUE (name, organization_id);

ALTER TABLE studies
    ADD CONSTRAINT uq_study_code UNIQUE (code, program_id);

ALTER TABLE studies
    ADD CONSTRAINT uq_study_name UNIQUE (name, program_id);

ALTER TABLE programs
    DROP CONSTRAINT uc_programs_name;

ALTER TABLE studies
    DROP CONSTRAINT uc_studies_code;