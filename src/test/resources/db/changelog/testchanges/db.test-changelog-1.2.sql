--liquibase formatted sql

--changeset test-iborschevskiy:1
insert into positions (security_name, ticker, acquisition_price, total_value, position_type, quantity, portfolio_id,
                       acquisition_value)
values ('AGRO-гдр', 'AGRO', 1000, 10000, 'LONG', 10, 9, 10000),
       ('Газпром', 'GAZP', 200, 4000, 'LONG', 20, 9, 4000);