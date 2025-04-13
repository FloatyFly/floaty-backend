-- Drop existing tables if they exist
DROP TABLE IF EXISTS t_email_verification_token;
DROP TABLE IF EXISTS t_password_reset_token;
DROP TABLE IF EXISTS t_session_token;
DROP TABLE IF EXISTS t_flight;
DROP TABLE IF EXISTS t_user;
DROP TABLE IF EXISTS user_seq;
DROP TABLE IF EXISTS hibernate_sequence;

-- Create sequence table if it doesn't exist
CREATE TABLE IF NOT EXISTS hibernate_sequence (
    next_val BIGINT
);
-- Create sequence table if it doesn't exist
CREATE TABLE IF NOT EXISTS user_seq (
    next_val BIGINT
);
-- Initialize the sequence if empty
INSERT INTO user_seq (next_val) SELECT 1 WHERE NOT EXISTS (SELECT * FROM user_seq);
INSERT INTO hibernate_sequence (next_val) SELECT 1 WHERE NOT EXISTS (SELECT * FROM hibernate_sequence);
-- Otherwise update it
UPDATE user_seq SET next_val = 1;
UPDATE hibernate_sequence SET next_val = 1;

-- Create tables
CREATE TABLE t_user
(
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    name                VARCHAR(128)    NOT NULL,
    email               VARCHAR(128)    NOT NULL,
    email_verified      BOOLEAN         DEFAULT FALSE,
    password_hash       VARCHAR(128)    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE t_flight
(
    id                  VARCHAR(64)     NOT NULL,
    user_id             BIGINT          NOT NULL,
    date_time           TIMESTAMP       NOT NULL,
    take_off            VARCHAR(128)    NOT NULL,
    duration            BIGINT          NOT NULL,
    description         VARCHAR(4096)   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE TABLE t_session_token
(
    id                  BIGINT          NOT NULL,
    fk_user_id          BIGINT          NOT NULL,
    token               VARCHAR(128)    NOT NULL,
    expiration_time     TIMESTAMP       NOT NULL,
    revoked             BOOLEAN         DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (fk_user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE TABLE t_email_verification_token
(
    id                  BIGINT          NOT NULL,
    fk_user_id          BIGINT          NOT NULL,
    token               VARCHAR(128)    NOT NULL,
    expiration_time     TIMESTAMP       NOT NULL,
    revoked             BOOLEAN         DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (fk_user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE TABLE t_password_reset_token
(
    id                  BIGINT          NOT NULL,
    fk_user_id          BIGINT          NOT NULL,
    token               VARCHAR(128)    NOT NULL,
    expiration_time     TIMESTAMP       NOT NULL,
    revoked             BOOLEAN         DEFAULT FALSE,
    used                BOOLEAN         DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (fk_user_id) REFERENCES t_user(id) ON DELETE CASCADE
);
