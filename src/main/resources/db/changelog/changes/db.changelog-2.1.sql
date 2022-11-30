--liquibase formatted sql

--changeset iborschevskiy:1
CREATE EXTENSION IF NOT EXISTS pgcrypto;

UPDATE users SET password = crypt(password, gen_salt('bf', 8));