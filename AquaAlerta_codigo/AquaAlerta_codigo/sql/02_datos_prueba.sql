-- AquaAlerta Mallorquín - DML de datos simulados
-- Ejecutar después de 01_crear_esquema.sql en un esquema recién creado.

INSERT INTO PUNTO_MONITOREO (nombre, descripcion) VALUES ('Desembocadura', 'Zona de contacto con el río Magdalena');
INSERT INTO PUNTO_MONITOREO (nombre, descripcion) VALUES ('Manglar norte', 'Franja de manglar al norte de la ciénaga');
INSERT INTO PUNTO_MONITOREO (nombre, descripcion) VALUES ('Centro', 'Zona central del espejo de agua');
INSERT INTO PUNTO_MONITOREO (nombre, descripcion) VALUES ('Arroyo León', 'Entrada del arroyo que desemboca en la ciénaga');
INSERT INTO PUNTO_MONITOREO (nombre, descripcion) VALUES ('Las Flores', 'Sector urbano cercano a la ciénaga');

INSERT INTO PARAMETRO (nombre, unidad, sentido, umbral_bajo, umbral_medio, umbral_critico, fuente)
VALUES ('Oxígeno disuelto', 'mg/L', 'MIN', 5, 4, 2, 'Referencia por confirmar (Decreto 1076/2015, art. 2.2.3.3.9.10)');
INSERT INTO PARAMETRO (nombre, unidad, sentido, umbral_bajo, umbral_medio, umbral_critico, fuente)
VALUES ('Salinidad', 'UPS', 'MIN', 20, 15, 10, 'Valor de simulación; sin criterio oficial encontrado');
INSERT INTO PARAMETRO (nombre, unidad, sentido, umbral_bajo, umbral_medio, umbral_critico, fuente)
VALUES ('Temperatura', 'C', 'MAX', 30, 32, 34, 'Referencia REDCAM 30 C; escalones de simulación');
INSERT INTO PARAMETRO (nombre, unidad, sentido, umbral_bajo, umbral_medio, umbral_critico, fuente)
VALUES ('Coliformes', 'NMP/100 mL', 'MAX', 200, 1000, 5000, 'Decreto 1076/2015, arts. 2.2.3.3.9.7 y 2.2.3.3.9.8');

INSERT INTO CONEXION_PUNTO (id_punto_origen, id_punto_destino, distancia_km) VALUES (1, 2, 1.2);
INSERT INTO CONEXION_PUNTO (id_punto_origen, id_punto_destino, distancia_km) VALUES (2, 3, 0.8);
INSERT INTO CONEXION_PUNTO (id_punto_origen, id_punto_destino, distancia_km) VALUES (3, 4, 1.5);
INSERT INTO CONEXION_PUNTO (id_punto_origen, id_punto_destino, distancia_km) VALUES (3, 5, 1.0);

INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (1, 1, 6.2, TIMESTAMP '2026-11-02 10:00:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (3, 4, 5200, TIMESTAMP '2026-11-02 10:05:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (4, 1, 3.5, TIMESTAMP '2026-11-02 10:10:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (2, 2, 28, TIMESTAMP '2026-11-02 10:15:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (3, 1, 1.8, TIMESTAMP '2026-11-02 10:20:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (5, 3, 31.2, TIMESTAMP '2026-11-02 10:25:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (1, 4, 350, TIMESTAMP '2026-11-02 10:30:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (4, 4, 1800, TIMESTAMP '2026-11-02 10:35:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (2, 1, 4.6, TIMESTAMP '2026-11-02 10:40:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (5, 2, 7.5, TIMESTAMP '2026-11-02 10:45:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (3, 3, 29.4, TIMESTAMP '2026-11-02 10:50:00');
INSERT INTO LECTURA (id_punto, id_parametro, valor, fecha_hora) VALUES (1, 1, 5.8, TIMESTAMP '2026-11-02 10:55:00');

INSERT INTO ALERTA (id_lectura, severidad, estado, fecha_generada, fecha_atencion)
VALUES (2, 'CRITICA', 'PENDIENTE', TIMESTAMP '2026-11-02 10:05:01', NULL);
INSERT INTO ALERTA (id_lectura, severidad, estado, fecha_generada, fecha_atencion)
VALUES (3, 'MEDIA', 'PENDIENTE', TIMESTAMP '2026-11-02 10:10:01', NULL);
INSERT INTO ALERTA (id_lectura, severidad, estado, fecha_generada, fecha_atencion)
VALUES (5, 'CRITICA', 'PENDIENTE', TIMESTAMP '2026-11-02 10:20:01', NULL);
INSERT INTO ALERTA (id_lectura, severidad, estado, fecha_generada, fecha_atencion)
VALUES (8, 'MEDIA', 'PENDIENTE', TIMESTAMP '2026-11-02 10:35:01', NULL);
INSERT INTO ALERTA (id_lectura, severidad, estado, fecha_generada, fecha_atencion)
VALUES (10, 'CRITICA', 'PENDIENTE', TIMESTAMP '2026-11-02 10:45:01', NULL);
INSERT INTO ALERTA (id_lectura, severidad, estado, fecha_generada, fecha_atencion)
VALUES (6, 'BAJA', 'ATENDIDA', TIMESTAMP '2026-11-02 10:25:01', TIMESTAMP '2026-11-02 11:00:00');
INSERT INTO ALERTA (id_lectura, severidad, estado, fecha_generada, fecha_atencion)
VALUES (7, 'BAJA', 'ATENDIDA', TIMESTAMP '2026-11-02 10:30:01', TIMESTAMP '2026-11-02 11:05:00');
INSERT INTO ALERTA (id_lectura, severidad, estado, fecha_generada, fecha_atencion)
VALUES (9, 'BAJA', 'ATENDIDA', TIMESTAMP '2026-11-02 10:40:01', TIMESTAMP '2026-11-02 11:10:00');

COMMIT;
