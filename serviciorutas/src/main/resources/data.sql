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
    (2, 1),   -- id_ruta = 1
    (3, 2);   -- id_ruta = 2

-- Tramo ejemplo para ruta 1
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
    NULL,
    1,  -- Ruta 1
    '2025-11-11T11:00:00',
    '2025-11-11T12:10:00',
    '2025-11-11T11:00:00',
    '2025-11-11T12:10:00',
    1800.00,
    1750.00
);

-- ========================================================
-- NUEVA RUTA PARA SOLICITUD EN TRÁNSITO
-- ========================================================
-- Ruta 3 con 5 tramos (3 finalizados, 2 en curso)
INSERT INTO ruta (cantidad_tramos, cantidad_depositos, duracion_estimada)
VALUES 
    (5, 2, 480);  -- id_ruta = 3

-- ========================================================
-- TRAMOS DE LA RUTA 3
-- ========================================================

-- Tramo 1 - Finalizado
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
    -31.4200,
    -64.1900,
    -31.4300,
    -64.2000,
    1,   -- origen-destino
    4,   -- Finalizado
    'AA111BB',
    3,   -- Ruta 3
    '2025-11-14T08:00:00',
    '2025-11-14T09:30:00',
    '2025-11-14T08:00:00',
    '2025-11-14T09:30:00',
    30000.00,
    29500.00
);

-- Tramo 2 - Finalizado
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
    -31.4300,
    -64.2000,
    -31.4400,
    -64.2100,
    2,   -- origen-deposito
    4,   -- Finalizado
    'AA111BB',
    3,
    '2025-11-14T10:00:00',
    '2025-11-14T11:10:00',
    '2025-11-14T10:00:00',
    '2025-11-14T11:10:00',
    25000.00,
    24800.00
);

-- Tramo 3 - Finalizado (el más reciente)
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
    -31.4400,
    -64.2100,
    -31.4500,
    -64.2200,
    3,   -- deposito-deposito
    4,   -- Finalizado
    'AA111BB',
    3,
    '2025-11-14T12:00:00',
    '2025-11-14T13:05:00',
    '2025-11-14T12:00:00',
    '2025-11-14T13:05:00',
    28000.00,
    27900.00
);

-- Tramo 4 - En curso (sin fh_fin_real)
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
    -31.4500,
    -64.2200,
    -31.4600,
    -64.2300,
    3,   -- deposito-deposito
    2,   -- En curso
    'AA111BB',
    3,
    '2025-11-14T14:00:00',
    NULL,
    '2025-11-14T14:00:00',
    '2025-11-14T15:15:00',
    26000.00,
    NULL
);

-- Tramo 5 - En curso (sin fh_fin_real)
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
    -31.4600,
    -64.2300,
    -31.4700,
    -64.2400,
    4,   -- deposito-destino
    2,   -- En curso
    'AA111BB',
    3,
    '2025-11-14T16:00:00',
    NULL,
    '2025-11-14T16:00:00',
    '2025-11-14T17:30:00',
    30000.00,
    NULL
);