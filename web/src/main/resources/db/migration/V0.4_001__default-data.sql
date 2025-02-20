-- Default data

INSERT INTO assay_types (name, description, active)
VALUES ('Generic', 'Default assay type. Can be applied to any assay that does not fit into other categories or requires no custom inputs.', true);

-- Update sequences to offset manually added records
alter sequence users_id_seq restart with 2;
alter sequence assay_types_id_seq restart with 2;