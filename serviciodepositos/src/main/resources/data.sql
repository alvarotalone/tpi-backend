-- Creamos datos para el ServicioDepositos (H2 Database)

-- 1. Provincias
INSERT INTO provincias (id_provincia, descripcion) VALUES
(1, 'Córdoba'),
(2, 'Santa Fe');

-- 2. Ciudades (asociadas a las provincias)
INSERT INTO ciudades (id_ciudad, descripcion, id_provincia) VALUES
(1, 'Córdoba Capital', 1),
(2, 'Rosario', 2);

-- 3. Ubicaciones (asociadas a las ciudades)
INSERT INTO ubicaciones (id_ubicacion, latitud, longitud, direccion_textual, id_ciudad) VALUES
(1, -31.4135, -64.1810, 'Av. Colón 123', 1),
(2, -32.9442, -60.6506, 'Bv. Oroño 456', 2);

-- 4. Depósitos (asociados a las ubicaciones)
-- Este es el dato clave que ServicioTarifa necesita
INSERT INTO depositos (id_deposito, nombre, costo_estadia_diario, id_ubicacion) VALUES
(1, 'Deposito Central Córdoba', 1500.50, 1),
(2, 'Deposito Sur Rosario', 1200.00, 2);