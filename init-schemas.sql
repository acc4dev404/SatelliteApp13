CREATE SCHEMA IF NOT EXISTS server_schema;

CREATE SCHEMA IF NOT EXISTS telemetry_schema;

GRANT ALL ON SCHEMA server_schema TO postgres;
GRANT ALL ON SCHEMA telemetry_schema TO postgres;

ALTER ROLE postgres SET search_path TO server_schema, telemetry_schema, public;