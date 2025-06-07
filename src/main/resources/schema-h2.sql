-- Create sequence
CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;

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


CREATE TABLE t_spot
(
    id                  BIGINT          NOT NULL,
    name                VARCHAR(128)    NOT NULL,
    fk_user_id          BIGINT          NOT NULL,
    latitude            DOUBLE          NOT NULL,
    longitude           DOUBLE          NOT NULL,
    altitude            DOUBLE          NOT NULL,
    description         VARCHAR(128)    NOT NULL,
    is_launch_site      BOOLEAN         NOT NULL,
    is_landing_site     BOOLEAN         NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (fk_user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE TABLE t_glider
(
    id                  BIGINT          NOT NULL,
    fk_user_id          BIGINT          NOT NULL,
    manufacturer        VARCHAR(128)    NOT NULL,
    model               VARCHAR(128)    NOT NULL
);


CREATE TABLE t_flight
(
    id                  VARCHAR(64)     NOT NULL,
    user_id             BIGINT          NOT NULL,
    date_time           TIMESTAMP       NOT NULL,
    take_off            VARCHAR(128)    NOT NULL,
    duration            BIGINT          NOT NULL,
    description         VARCHAR(4096)   NOT NULL,
    fk_launch_site_id   BIGINT          NOT NULL,
    fk_landing_site_id  BIGINT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
    FOREIGN KEY (fk_launch_site_id) REFERENCES t_spot(id),
    FOREIGN KEY (fk_landing_site_id) REFERENCES t_spot(id)
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

