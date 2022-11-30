--liquibase formatted sql

--changeset iborschevskiy:1
ALTER TABLE deals DROP CONSTRAINT deal_portfolio_fk;

--changeset iborschevskiy:2
ALTER TABLE deals DROP COLUMN portfolio_id;

