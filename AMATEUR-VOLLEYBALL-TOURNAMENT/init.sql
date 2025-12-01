-- Create the logical folders (schemas) if they don't exist
-- Run from terminal if db structure changes 'docker-compose down -v'
CREATE SCHEMA IF NOT EXISTS teams_module;
CREATE SCHEMA IF NOT EXISTS tournament_module;
CREATE SCHEMA IF NOT EXISTS ticketing_module;
CREATE SCHEMA IF NOT EXISTS users_module;