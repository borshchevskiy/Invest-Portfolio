--liquibase formatted sql

--changeset iborschevskiy:1
INSERT INTO users (id, username, firstname, password, active)
VALUES (1, 'admin@admin.com', 'admin', 'admin', true);

--changeset iborschevskiy:2

insert into user_role (user_id, roles)
values (1, 'USER'), (1,'ADMIN');