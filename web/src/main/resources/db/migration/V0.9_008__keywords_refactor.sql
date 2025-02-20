ALTER TABLE keywords
    DROP CONSTRAINT fk_keywords_on_category;

ALTER TABLE keywords
    ADD category VARCHAR(64);

UPDATE keywords
SET category = c.name
FROM keyword_categories c
WHERE keywords.category_id = c.id;

DROP INDEX idx_keyword;

ALTER TABLE keyword_categories
    DROP CONSTRAINT uc_keyword_categories_category;

DROP TABLE keyword_categories CASCADE;

ALTER TABLE keywords
    DROP COLUMN category_id;