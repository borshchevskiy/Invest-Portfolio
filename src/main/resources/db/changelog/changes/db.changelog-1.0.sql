--liquibase formatted sql

--changeset iborschevskiy:1
CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    password VARCHAR(255),
    active BOOLEAN NOT NULL
);

--changeset iborschevskiy:2
CREATE TABLE IF NOT EXISTS user_role
(
    user_id BIGSERIAL NOT NULL,
    roles   VARCHAR(255)
);
