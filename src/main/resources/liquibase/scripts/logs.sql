-- liquibase formatted sql

-- changeset kostusonline:1
CREATE TABLE logs (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP,
    level VARCHAR(10),
    message TEXT,
    logger VARCHAR(255),
    thread VARCHAR(255),
    exception TEXT
);
