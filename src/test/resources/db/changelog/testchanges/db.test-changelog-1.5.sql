--liquibase formatted sql

--changeset test-iborschevskiy:1
SELECT setval('portfolios_id_seq',
    (SELECT COUNT(*)
              FROM portfolios)
                  );