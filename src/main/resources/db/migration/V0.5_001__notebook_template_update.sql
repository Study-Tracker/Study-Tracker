ALTER TABLE notebook_entry_templates
    ADD category INTEGER;

ALTER TABLE notebook_entry_templates
    ADD is_default BOOLEAN;

ALTER TABLE notebook_entry_templates
    ALTER COLUMN category SET NOT NULL;

ALTER TABLE notebook_entry_templates
    ALTER COLUMN is_default SET NOT NULL;