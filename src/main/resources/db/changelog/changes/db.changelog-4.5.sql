--liquibase formatted sql

--changeset iborschevskiy:1
ALTER TABLE deals DROP CONSTRAINT deal_position_fk;
--changeset iborschevskiy:2
ALTER TABLE deals ADD CONSTRAINT deal_position_fk FOREIGN KEY (position_id) references positions
    ON DELETE CASCADE ;

