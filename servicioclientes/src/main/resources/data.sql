-- ==========================================
-- CLIENTES DE PRUEBA
-- ==========================================
INSERT INTO CLIENTES (id, nombre, apellido, telefono, email)
VALUES
  (1, 'Geronimo', 'Negro', '123456789', 'geronegro@gmail.com'),
  (2, 'Lucia', 'Perez', '987654321', 'luciaperez@gmail.com'),
  (3, 'Martin', 'Rodriguez', '555123456', 'martinr@gmail.com');

-- ==========================================
-- ESTADOS DE CONTENEDOR
-- ==========================================
INSERT INTO estado_contenedor (id_estado_contenedor, descripcion)
VALUES
  (1, 'Disponible'),
  (2, 'En tránsito'),
  (3, 'Entregado'),
  (4, 'En reparación');

-- ==========================================
-- CONTENEDORES
-- ==========================================
INSERT INTO contenedores (id, peso, volumen, id_estado_contenedor, id_cliente)
VALUES
  (1, 1200.5, 3.6, 1, 1),  -- Disponible, Geronimo
  (2, 900.0, 2.8, 2, 2),   -- En tránsito, Lucia
  (3, 1450.3, 4.2, 3, 3);  -- Entregado, Martin
