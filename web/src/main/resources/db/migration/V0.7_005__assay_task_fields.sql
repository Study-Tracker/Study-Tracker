CREATE TABLE assay_task_fields
(
    id               BIGINT       NOT NULL,
    display_name     VARCHAR(255) NOT NULL,
    field_name       VARCHAR(255) NOT NULL,
    type             VARCHAR(255) NOT NULL,
    required         BOOLEAN      NOT NULL,
    description      VARCHAR(1024),
    active           BOOLEAN      NOT NULL,
    field_order      INTEGER      NOT NULL,
    dropdown_options VARCHAR(2048),
    default_value    VARCHAR(255),
    assay_task_id    BIGINT       NOT NULL,
    CONSTRAINT pk_assay_task_fields PRIMARY KEY (id)
);

ALTER TABLE assay_tasks
    ADD data JSON;

ALTER TABLE assay_task_fields
    ADD CONSTRAINT FK_ASSAY_TASK_FIELDS_ON_ASSAY_TASK FOREIGN KEY (assay_task_id) REFERENCES assay_tasks (id);