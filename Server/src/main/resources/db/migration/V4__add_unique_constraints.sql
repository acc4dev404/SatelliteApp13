DELETE FROM satellites
WHERE id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY name ORDER BY id) as rn
        FROM satellites
    ) t WHERE rn > 1
);

DELETE FROM constellations
WHERE id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY name ORDER BY id) as rn
        FROM constellations
    ) t WHERE rn > 1
);

ALTER TABLE satellites ADD CONSTRAINT unique_satellite_name UNIQUE (name);
ALTER TABLE constellations ADD CONSTRAINT unique_constellation_name UNIQUE (name);