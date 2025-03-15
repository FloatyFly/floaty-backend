-- Drop existing tables
DROP TABLE IF EXISTS t_email_verification_token;
DROP TABLE IF EXISTS t_password_reset_token;
DROP TABLE IF EXISTS t_session_token;
DROP TABLE IF EXISTS t_flight;
DROP TABLE IF EXISTS t_user;

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

UPDATE user_seq SET next_val = 1;

CREATE TABLE t_flight
(
    id                  VARCHAR(64)     NOT NULL,
    user_id             BIGINT          NOT NULL,
    date_time           TIMESTAMP       NOT NULL,
    take_off            VARCHAR(128)    NOT NULL,
    duration            INTEGER         NOT NULL,
    description         VARCHAR(4096)   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE TABLE t_session_token
(
    id                  VARCHAR(64)     NOT NULL,
    fk_user_id          BIGINT          NOT NULL,
    token               VARCHAR(128)    NOT NULL,
    expiration_time     TIMESTAMP       NOT NULL,
    revoked             BOOLEAN         DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (fk_user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE TABLE t_email_verification_token
(
    id                  VARCHAR(64)     NOT NULL,
    fk_user_id          BIGINT          NOT NULL,
    token               VARCHAR(128)    NOT NULL,
    expiration_time     TIMESTAMP       NOT NULL,
    revoked             BOOLEAN         DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (fk_user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE TABLE t_password_reset_token
(
    id                  VARCHAR(64)     NOT NULL,
    fk_user_id          BIGINT          NOT NULL,
    token               VARCHAR(128)    NOT NULL,
    expiration_time     TIMESTAMP       NOT NULL,
    revoked             BOOLEAN         DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (fk_user_id) REFERENCES t_user(id) ON DELETE CASCADE
);
