--liquibase formatted sql

--changeset iborschevskiy:1
ALTER TABLE deals RENAME market_fee TO market_commission;
--changeset iborschevskiy:2
ALTER TABLE deals RENAME broker_fee TO broker_commission;
--changeset iborschevskiy:3
ALTER TABLE deals RENAME other_fee TO other_commission;
