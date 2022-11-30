--liquibase formatted sql

--changeset iborschevskiy:1
CREATE TABLE IF NOT EXISTS portfolios
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    user_id int8,
    cash NUMERIC(16,2),
    constraint portfolio_user_fk foreign key (user_id) references users
);

--changeset iborschevskiy:2
CREATE TABLE IF NOT EXISTS positions
(
    id BIGSERIAL PRIMARY KEY,
    security_name VARCHAR(255),
    ticker VARCHAR(32),
    acquisition_price NUMERIC(16,8),
    total_value NUMERIC(16,2),
    deal_type VARCHAR(32),
    quantity int8,
    portfolio_id int8,
    constraint position_portfolio_fk foreign key (portfolio_id) references portfolios
);

--changeset iborschevskiy:3
CREATE TABLE IF NOT EXISTS deals
(
    id BIGSERIAL PRIMARY KEY,
    security_name VARCHAR(255),
    ticker VARCHAR(32),
    acquisition_price NUMERIC(16,8),
    quantity int8,
    acquisition_value NUMERIC(16,2),
    deal_type VARCHAR(32),
    market_fee NUMERIC(8,2),
    broker_fee NUMERIC(8,2),
    other_fee NUMERIC(8,2),
    total_value NUMERIC(16,2),
    deal_date DATE,
    comment VARCHAR(255),
    position_id int8,
    portfolio_id int8,
    constraint deal_position_fk foreign key (position_id) references positions,
    constraint deal_portfolio_fk foreign key (portfolio_id) references portfolios
);