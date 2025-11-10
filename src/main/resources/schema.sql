CREATE TABLE IF NOT EXISTS parking (
    parking_id VARCHAR(10) NOT NULL,
    hourly_rate DECIMAL(5, 2) NOT NULL,
    max_rate_amount DECIMAL(5, 2) NULL,             -- Puede ser NULL si no hay limite de precio
    max_rate_period_hours INT NULL,                 -- Periodo en horas (ejemplo. 24 para un día, 12 para medio dia)
    free_initial_hours INT NOT NULL,                -- Horas gratuitas iniciales (0 o más)
    PRIMARY KEY (parking_id)
);