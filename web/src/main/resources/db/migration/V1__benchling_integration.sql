CREATE TABLE benchling_integrations
(
    id            BIGINT       NOT NULL,
    name          VARCHAR(255) NOT NULL,
    tenant_name   VARCHAR(255) NOT NULL,
    root_url      VARCHAR(255) NOT NULL,
    client_id     VARCHAR(255),
    client_secret VARCHAR(1024),
    username      VARCHAR(255),
    password      VARCHAR(255),
    CONSTRAINT pk_benchling_integrations PRIMARY KEY (id)
);