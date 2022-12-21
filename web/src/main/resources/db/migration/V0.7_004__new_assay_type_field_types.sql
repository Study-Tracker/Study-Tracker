ALTER TABLE assay_type_fields
    ADD default_value VARCHAR(255);

ALTER TABLE assay_type_fields
    ADD dropdown_options VARCHAR(2048);

ALTER TABLE integration_configuration_schema_fields
    ADD default_value VARCHAR(255);

ALTER TABLE integration_configuration_schema_fields
    ADD dropdown_options VARCHAR(2048);