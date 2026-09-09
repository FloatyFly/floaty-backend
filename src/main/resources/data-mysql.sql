-- Sequence reset for MySQL, where hibernate_sequence is a single-row table.
-- Runs after data.sql; see spring.sql.init.data-locations in the mysql profiles.
UPDATE hibernate_sequence SET next_val = 112;
