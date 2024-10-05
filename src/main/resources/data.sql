
-- original password myPassword123
INSERT INTO t_user (id, name, email, password_hash) VALUES (1, 'Matthäus', 'matt@floaty.com', '$2a$10$X.F7s5u19RTCOuVORFf8Yue2KfJZyk8RfEpKX9PU2q/fUdeuNh7tm');
INSERT INTO t_user (id, name, email, password_hash) VALUES (2, 'Patrice', 'patrice@floaty.com', '$2a$10$VtBDoFg5XHdX4yIStcSKOuMiMNAPGfzYZhkXLjbgVvqt7jjDsgnI6');
INSERT INTO t_user (id, name, email, password_hash) VALUES (3, 'Yanik', 'yanik@floaty.com', '$2a$10$fCVak5B3oxnheO7hDWiXKOG8lNfTRqiJCFMv/qUVfJasIEj/RUZR6');

ALTER SEQUENCE user_seq RESTART WITH (SELECT MAX(id) + 1 FROM t_user);

INSERT INTO t_flight (id, user_id, take_off, duration, date_time, description) VALUES ('18009b06-d7f8-43e2-a29d-9e765dd320a5', 2, 'Weissenstein', 60, '2024-10-01 10:01:30', 'This was epic.');
INSERT INTO t_flight (id, user_id, take_off, duration, date_time, description) VALUES ('8891abd7-6008-413a-8db9-948d79328464', 3, 'Bergell', 120, '2024-05-12 08:59:10', '');
