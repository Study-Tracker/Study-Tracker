-- Default data

INSERT INTO assay_types (name, description, active)
VALUES ('Generic', 'Default assay type. Can be applied to any assay that does not fit into other categories or requires no custom inputs.', true);

-- Update the hibernate sequence to offset manually added records
alter sequence hibernate_sequence restart with 3;