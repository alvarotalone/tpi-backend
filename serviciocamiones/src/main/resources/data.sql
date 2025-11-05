-- ========================================
-- TIPOS DE CAMIÓN
-- ========================================
INSERT INTO tipo_camion (id_tipo_camion, nombre_tipo_camion, capacidad_peso, capacidad_volumen, costo_base_km, consumo_combustible)
VALUES
  (1, 'Semi-remolque', 25000, 60.0, 120.5, 28.0),
  (2, 'Camión rígido', 18000, 40.0, 95.0, 22.0),
  (3, 'Camión liviano', 8000, 20.0, 70.0, 15.5);

-- ========================================
-- TRANSPORTISTAS
-- ========================================
INSERT INTO transportista (id_transportista, nombre, apellido, telefono, email)
VALUES
  (1, 'Juan', 'Pérez', '3516547890', 'juanperez@gmail.com'),
  (2, 'Lucía', 'Fernández', '3519876543', 'luciafdez@gmail.com'),
  (3, 'Carlos', 'Lopez', '3517539512', 'clopez@gmail.com');

-- ========================================
-- CAMIONES
-- ========================================
INSERT INTO camion (dominio, id_tipo_camion, id_transportista, disponible)
VALUES
  ('AB123CD', 1, 1, TRUE),
  ('CD456EF', 2, 2, TRUE),
  ('EF789GH', 3, 3, FALSE);
