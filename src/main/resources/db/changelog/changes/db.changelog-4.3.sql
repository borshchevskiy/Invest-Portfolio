--liquibase formatted sql

--changeset iborschevskiy:1
ALTER TABLE positions RENAME deal_type TO position_type;

