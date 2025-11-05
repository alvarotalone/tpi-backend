-- Inserta tarifas de ejemplo
INSERT INTO tarifa (id_tarifa, costo_fijo_tramo, id_tipo_camion, valor_litro_combustible, valido_desde, valido_hasta)
VALUES 
    (1, 1000.00, 1, 1200.00, '2025-01-01', '2025-12-31'),

    (2, 1500.00, 2, 1250.50, '2025-01-01', '2025-12-31'),

    (3, 2000.00, 3, 1300.75, '2025-01-01', '2025-12-31');
