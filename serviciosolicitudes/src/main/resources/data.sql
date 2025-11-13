-- Estados
INSERT INTO estado_solicitud (id_estado_solicitud, descripcion) VALUES
(1, 'BORRADOR'),
(2, 'PROGRAMADA'),
(3, 'EN_TRANSITO'),
(4, 'ENTREGADA');

-- Solicitudes de ejemplo (SIN id_solicitud)
INSERT INTO solicitud 
(id_contenedor, id_cliente, costo_estimado, tiempo_estimado, costo_final, tiempo_real, dominio_camion, id_ruta,
 latitud_origen, longitud_origen, latitud_destino, longitud_destino, id_tarifa, id_estado_solicitud) 
VALUES
(1, 1, 150000.00, 180, NULL, NULL, 'AB123CD', 10, -31.4167, -64.1833, -34.6037, -58.3816, 5, 1),
(2, 2, 120000.00, 120, 125000.00, 130, 'AC234DE', 12, -31.4167, -64.1833, -31.4200, -64.1900, 4, 2);
