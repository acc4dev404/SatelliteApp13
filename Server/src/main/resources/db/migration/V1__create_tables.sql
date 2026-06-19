CREATE TABLE IF NOT EXISTS constellations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS energy_systems (
    id BIGSERIAL PRIMARY KEY,
    battery_level DOUBLE PRECISION NOT NULL DEFAULT 0.5
);

CREATE TABLE IF NOT EXISTS satellite_states (
    id BIGSERIAL PRIMARY KEY,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    status_message VARCHAR(255) DEFAULT 'Не активирован'
);

CREATE TABLE IF NOT EXISTS satellites (
    id BIGSERIAL PRIMARY KEY,
    satellite_type VARCHAR(31) NOT NULL,
    name VARCHAR(255) NOT NULL,
    constellation_id BIGINT,
    energy_system_id BIGINT,
    satellite_state_id BIGINT,
    bandwidth DOUBLE PRECISION,
    resolution DOUBLE PRECISION,
    photos_taken INTEGER DEFAULT 0,
    FOREIGN KEY (constellation_id) REFERENCES constellations(id) ON DELETE CASCADE,
    FOREIGN KEY (energy_system_id) REFERENCES energy_systems(id) ON DELETE CASCADE,
    FOREIGN KEY (satellite_state_id) REFERENCES satellite_states(id) ON DELETE CASCADE
);

CREATE INDEX idx_constellation_name ON constellations(name);
CREATE INDEX idx_satellite_name ON satellites(name);