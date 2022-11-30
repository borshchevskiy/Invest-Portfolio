--liquibase formatted sql

--changeset iborschevskiy:1
SELECT setval('users_id_seq', 1);

