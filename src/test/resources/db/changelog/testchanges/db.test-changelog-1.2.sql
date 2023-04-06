--liquibase formatted sql

--changeset test-iborschevskiy:1
insert into positions (security_name, ticker, acquisition_price, total_value, position_type, quantity, portfolio_id,
                       acquisition_value)
values ('AGRO-гдр', 'AGRO', 1000, 10000, 'LONG', 10, 2, 10000),
       ('Газпром', 'GAZP', 200, 4000, 'LONG', 20, 2, 4000),
       ('AGRO-гдр', 'AGRO', 1000, 10000, 'LONG', 10, 1, 10000),
       ('Газпром', 'GAZP', 200, 4000, 'LONG', 20, 1, 4000),
       ('AGRO-гдр', 'AGRO', 1000, 10000, 'LONG', 10, 3, 10000),
       ('Газпром', 'GAZP', 200, 4000, 'LONG', 20, 3, 4000);