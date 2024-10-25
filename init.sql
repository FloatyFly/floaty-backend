-- init.sql

-- Create the user if it doesn't exist
CREATE USER IF NOT EXISTS 'db-user'@'%' IDENTIFIED BY 'db-password';

-- Grant privileges to the user
GRANT ALL PRIVILEGES ON `floaty-db`.* TO 'db-user'@'%';

-- Flush privileges to ensure that MySQL acknowledges the changes
FLUSH PRIVILEGES;
