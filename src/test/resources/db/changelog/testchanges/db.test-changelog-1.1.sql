--liquibase formatted sql

--changeset test-iborschevskiy:1
insert into portfolios (id, name, user_id, cash)
values (9, 'adminPortfolio', 1, 90000);