
-- TODO: Add something like liquibase to manage database migrations. In dev environments we have auto-ddl anyway.

--DROP TABLE IF EXISTS t_session;
--DROP TABLE IF EXISTS t_flight;
-- DROP TABLE IF EXISTS t_user;
SELECT * FROM T_USER;

--CREATE TABLE t_user
--(
--    id                  INTEGER         NOT NULL,
--    name                VARCHAR(128)    NOT NULL,
--    email               VARCHAR(128)    NOT NULL,
--    password_hash       VARCHAR(128)    NOT NULL,
--    PRIMARY KEY (id)
--);
--
--CREATE TABLE t_flight
--(
--    id                  VARCHAR(64)     NOT NULL,
--    user_id             INTEGER         NOT NULL,
--    date_time           TIMESTAMP       NOT NULL,
--    take_off            VARCHAR(128)    NOT NULL,
--    duration            INTEGER         NOT NULL,
--    description         VARCHAR(512)    NOT NULL,
--    PRIMARY KEY (id),
--    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
--);
--
--CREATE TABLE t_session
--(
--    id                  VARCHAR(64)     NOT NULL,
--    fk_user_id          INTEGER         NOT NULL,
--    token               VARCHAR(128)    NOT NULL,
--    expiration_time     TIMESTAMP       NOT NULL,
--    PRIMARY KEY (id),
--    FOREIGN KEY (fk_user_id) REFERENCES t_user(id) ON DELETE CASCADE
--);
