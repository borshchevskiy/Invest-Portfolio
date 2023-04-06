--liquibase formatted sql

--changeset test-iborschevskiy:1
insert into portfolios (id, name, user_id, cash)
values (1, 'adminPortfolio', 1, 90000),
       (2, 'adminPortfolio2', 1, 190000),
       (3, 'testPortfolio', 2, 1000000);