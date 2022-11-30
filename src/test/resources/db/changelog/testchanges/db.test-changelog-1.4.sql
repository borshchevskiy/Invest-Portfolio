--liquibase formatted sql

--changeset test-iborschevskiy:1
insert into portfolios (id, name, user_id, cash)
values (10, 'adminPortfolio2', 1, 190000);