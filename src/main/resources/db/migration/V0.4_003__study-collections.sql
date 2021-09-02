CREATE TABLE study_collection_studies
(
    study_collection_id BIGINT NOT NULL,
    study_id            BIGINT NOT NULL,
    CONSTRAINT pk_study_collection_studies PRIMARY KEY (study_collection_id, study_id)
);

CREATE TABLE study_collections
(
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name             VARCHAR(255)                            NOT NULL,
    description      TEXT,
    shared           BOOLEAN,
    created_by       BIGINT                                  NOT NULL,
    last_modified_by BIGINT                                  NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_study_collections PRIMARY KEY (id)
);

ALTER TABLE study_collections
    ADD CONSTRAINT FK_STUDY_COLLECTIONS_ON_CREATEDBY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE study_collections
    ADD CONSTRAINT FK_STUDY_COLLECTIONS_ON_LAST_MODIFIED_BY FOREIGN KEY (last_modified_by) REFERENCES users (id);

ALTER TABLE study_collection_studies
    ADD CONSTRAINT fk_stucolstu_on_study FOREIGN KEY (study_id) REFERENCES studies (id);

ALTER TABLE study_collection_studies
    ADD CONSTRAINT fk_stucolstu_on_study_collection FOREIGN KEY (study_collection_id) REFERENCES study_collections (id);