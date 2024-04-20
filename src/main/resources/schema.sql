DROP TABLE IF EXISTS t_flight;
CREATE TABLE t_flight
(
    id          VARCHAR(64)     NOT NULL,
    userid      INTEGER         NOT NULL,
    takeoff     VARCHAR(128)    NOT NULL,
    duration    INTEGER         NOT NULL,
    date        VARCHAR(64)     NOT NULL,
    description VARCHAR(512)    NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user
(
    id          INTEGER         NOT NULL,
    name        VARCHAR(128)    NOT NULL,
    PRIMARY KEY (id)
);


