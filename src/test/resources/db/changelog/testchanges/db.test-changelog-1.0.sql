--liquibase formatted sql

--changeset test-iborschevskiy:1
INSERT INTO users (username, firstname, password, active)
VALUES ('test@test.com', 'test', 'test', true);

--changeset test-iborschevskiy:2
insert into user_role (user_id, roles)
values ((SELECT id FROM users WHERE username='test@test.com'), 'USER');
