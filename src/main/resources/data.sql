-- CONFIGURACION
-- Cliente 1: P000123
-- Reglas: Tarifa 2€/h, Máximo 15€ cada 24 horas, 0 horas gratis.
INSERT IGNORE INTO parking (parking_id, hourly_rate, max_rate_amount, max_rate_period_hours, free_initial_hours) VALUES
('P000123', 2.00, 15.00, 24, 0);

-- Cliente 2: P000456
-- Reglas: Tarifa 3€/h, Máximo 20€ cada 12 horas, 1 hora gratis.
INSERT IGNORE INTO parking (parking_id, hourly_rate, max_rate_amount, max_rate_period_hours, free_initial_hours) VALUES
('P000456', 3.00, 20.00, 12, 1);