--liquibase formatted sql

--changeset iborschevskiy:1
ALTER TABLE positions ADD acquisition_value NUMERIC(16,8);

