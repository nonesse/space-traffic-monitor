CREATE TABLE observations (
                              id UUID PRIMARY KEY,
                              object_id VARCHAR(50) NOT NULL,
                              observed_at TIMESTAMPTZ NOT NULL,
                              position_x DOUBLE PRECISION NOT NULL,
                              position_y DOUBLE PRECISION NOT NULL,
                              position_z DOUBLE PRECISION NOT NULL,
                              created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_observations_object_id
    ON observations (object_id);

CREATE INDEX idx_observations_observed_at
    ON observations (observed_at);