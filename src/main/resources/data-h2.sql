-- Sequence reset for H2, where hibernate_sequence is a real SEQUENCE.
-- Runs after data.sql; see spring.sql.init.data-locations in the h2 profiles.
ALTER SEQUENCE hibernate_sequence RESTART WITH 112;
