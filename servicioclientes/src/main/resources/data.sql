-- ==========================================
-- CLIENTES DE PRUEBA
-- ==========================================
INSERT INTO clientes (nombre, apellido, telefono, email)
VALUES
  ('Geronimo', 'Negro', '123456789', 'geronegro@gmail.com'),
  ('Lucia', 'Perez', '987654321', 'luciaperez@gmail.com'),
  ('Martin', 'Rodriguez', '555123456', 'martinr@gmail.com');

-- ==========================================
-- ESTADOS DE CONTENEDOR
-- ==========================================
INSERT INTO estado_contenedor (descripcion)
VALUES
  ('Disponible'),
  ('En tránsito'),
  ('Entregado'),
  ('En reparación');

-- ==========================================
-- CONTENEDORES
-- ==========================================
INSERT INTO contenedores (peso, volumen, id_estado_contenedor, id_cliente)
VALUES
  (1200.5, 3.6, 1, 1),  -- Disponible, Geronimo
  (900.0,  2.8, 2, 2),  -- En tránsito, Lucia
  (1450.3, 4.2, 3, 3),  -- Entregado, Martin
  (1100.0, 3.2, 2, 1);  -- id = 4, En tránsito, Geronimo