-- ========================================================
-- DATOS DE TABLAS BASE
-- ========================================================

-- 🔹 Tipos de Tramo
INSERT INTO tipo_tramo (id_tipo_tramo, descripcion) VALUES
(1, 'origen-destino'),
(2, 'origen-deposito'),
(3, 'deposito-deposito'),
(4, 'deposito-destino');


-- 🔹 Estados de Tramo
INSERT INTO estado_tramo (descripcion)
VALUES 
    ('Pendiente'),
    ('En curso'),
    ('Asignado'),
    ('Finalizado');

-- 🔹 Rutas
INSERT INTO ruta (cantidad_tramos, cantidad_depositos)
VALUES 
    (2, 1),
    (3, 2);

-- ========================================================
-- DATOS DE TRAMOS (con relaciones ManyToOne locales)
-- ========================================================

INSERT INTO tramo (
    latitud_origen,
    longitud_origen,
    latitud_destino,
    longitud_destino,
    id_tipo_tramo,
    id_estado_tramo,
    dominio_camion,
    id_ruta,
    fh_inicio_estimada,
    fh_fin_estimada,
    costo_aproximado,
    costo_real
) VALUES
    (-31.4123, -64.1830, -31.5001, -64.2509, 1, 1, null, 1, '2025-11-11T08:00:00', '2025-11-11T09:30:00', 1500.00, 0.00),

    (-31.5001, -64.2509, -31.6005, -64.3200, 2, 2, null, 1, '2025-11-11T09:30:00', '2025-11-11T11:00:00', 2200.00, 0.00),

    (-31.6005, -64.3200, -31.7500, -64.4000, 3, 3, null, 2, '2025-11-11T13:00:00', '2025-11-11T15:00:00', 3200.00, 3100.00);

-- 🔹 Tramo con fh_fin_real real (para ruta 1)
INSERT INTO tramo (
    latitud_origen,
    longitud_origen,
    latitud_destino,
    longitud_destino,
    id_tipo_tramo,
    id_estado_tramo,
    dominio_camion,
    id_ruta,
    fh_inicio_real,
    fh_fin_real,
    fh_inicio_estimada,
    fh_fin_estimada,
    costo_aproximado,
    costo_real
) VALUES (
    -31.6005,
    -64.3200,
    -31.7000,
    -64.3800,
    4,  -- deposito-destino
    4,  -- Finalizado
    null,
    1,  -- Ruta 1
    '2025-11-11T11:00:00',
    '2025-11-11T12:10:00',   -- fh_fin_real REAL
    '2025-11-11T11:00:00',
    '2025-11-11T12:10:00',
    1800.00,
    1750.00
);

