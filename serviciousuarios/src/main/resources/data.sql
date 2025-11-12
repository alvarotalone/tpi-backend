-- ====== ROLES ======
INSERT INTO rol (descripcion) VALUES ('ADMIN');
INSERT INTO rol (descripcion) VALUES ('OPERADOR');
INSERT INTO rol (descripcion) VALUES ('CLIENTE');
INSERT INTO rol (descripcion) VALUES ('TRANSPORTISTA');

-- ====== USUARIOS ======
INSERT INTO usuario (nombre_user, contraseña, id_rol) VALUES ('admin', 'admin123', 1);
INSERT INTO usuario (nombre_user, contraseña, id_rol) VALUES ('operador1', 'op123', 2);
INSERT INTO usuario (nombre_user, contraseña, id_rol) VALUES ('cliente1', 'cli123', 3);
INSERT INTO usuario (nombre_user, contraseña, id_rol) VALUES ('transportista1', 'trans123', 4);
