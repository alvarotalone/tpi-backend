-- ==========================================
-- ESTADOS DE SOLICITUD
-- ==========================================
INSERT INTO estado_solicitud (id_estado_solicitud, descripcion) VALUES
(1, 'BORRADOR'),
(2, 'PROGRAMADA'),
(3, 'EN_TRANSITO'),
(4, 'ENTREGADA');

-- ==========================================
-- SOLICITUDES DE EJEMPLO
-- ==========================================

-- Solicitud en BORRADOR (solo de ejemplo, no entra al endpoint)
INSERT INTO solicitud 
(id_contenedor, id_cliente, costo_estimado, tiempo_estimado, costo_final, tiempo_real, dominio_camion, id_ruta,
 latitud_origen, longitud_origen, latitud_destino, longitud_destino, id_tarifa, id_estado_solicitud) 
VALUES
(
    1,              -- contenedor 1
    1,              -- cliente Geronimo
    150000.00,
    180,
    NULL,
    NULL,
    'AB123CD',
    1,              -- ruta 1 (ejemplo)
    -31.4167,
    -64.1833,
    -34.6037,
    -58.3816,
    5,
    1               -- BORRADOR
);

-- Solicitud EN_TRANSITO asociada al contenedor 4 y a la ruta 3
-- ESTA es la que va a usar el endpoint de ubicaciones
INSERT INTO solicitud 
(id_contenedor, id_cliente, costo_estimado, tiempo_estimado, costo_final, tiempo_real, dominio_camion, id_ruta,
 latitud_origen, longitud_origen, latitud_destino, longitud_destino, id_tarifa, id_estado_solicitud) 
VALUES
(
    4,              -- id_contenedor (En tránsito en servicio clientes)
    1,              -- id_cliente (Geronimo)
    130000.00,      -- costo_estimado
    300,            -- tiempo_estimado (min)
    NULL,           -- costo_final aún no definido
    NULL,           -- tiempo_real aún no definido
    'AA111BB',      -- mismo dominio_camion que los tramos de la ruta 3
    3,              -- id_ruta = 3 (ruta con 5 tramos, 3 finalizados)
    -31.4167,       -- latitud_origen (ej: Córdoba)
    -64.1833,       -- longitud_origen
    -34.6037,       -- latitud_destino (ej: Buenos Aires)
    -58.3816,       -- longitud_destino
    3,              -- id_tarifa (mock)
    3               -- EN_TRANSITO
);