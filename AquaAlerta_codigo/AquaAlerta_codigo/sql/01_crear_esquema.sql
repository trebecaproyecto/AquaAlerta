-- AquaAlerta Mallorquín - DDL
-- Oracle XE 21c o superior. Ejecutar en el esquema destinado al proyecto.
-- Elimina objetos existentes con estos nombres para permitir recrear el esquema.
BEGIN EXECUTE IMMEDIATE 'DROP VIEW V_ALERTA_DETALLE';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE ALERTA CASCADE CONSTRAINTS PURGE';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE LECTURA CASCADE CONSTRAINTS PURGE';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE CONEXION_PUNTO CASCADE CONSTRAINTS PURGE';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE PARAMETRO CASCADE CONSTRAINTS PURGE';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE PUNTO_MONITOREO CASCADE CONSTRAINTS PURGE';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/

CREATE TABLE PUNTO_MONITOREO (
    id_punto NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(80) NOT NULL,
    descripcion VARCHAR2(200),
    CONSTRAINT pk_punto_monitoreo PRIMARY KEY (id_punto),
    CONSTRAINT uq_punto_nombre UNIQUE (nombre)
);

CREATE TABLE PARAMETRO (
    id_parametro NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(60) NOT NULL,
    unidad VARCHAR2(20) NOT NULL,
    sentido VARCHAR2(3) NOT NULL,
    umbral_bajo NUMBER(10,2) NOT NULL,
    umbral_medio NUMBER(10,2) NOT NULL,
    umbral_critico NUMBER(10,2) NOT NULL,
    fuente VARCHAR2(200),
    CONSTRAINT pk_parametro PRIMARY KEY (id_parametro),
    CONSTRAINT uq_parametro_nombre UNIQUE (nombre),
    CONSTRAINT ck_param_sentido CHECK (sentido IN ('MAX', 'MIN')),
    CONSTRAINT ck_param_umbrales CHECK (
        (sentido = 'MAX' AND umbral_bajo < umbral_medio AND umbral_medio < umbral_critico)
        OR (sentido = 'MIN' AND umbral_bajo > umbral_medio AND umbral_medio > umbral_critico)
    )
);

CREATE TABLE LECTURA (
    id_lectura NUMBER GENERATED ALWAYS AS IDENTITY,
    id_punto NUMBER NOT NULL,
    id_parametro NUMBER NOT NULL,
    valor NUMBER(10,2) NOT NULL,
    fecha_hora TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_lectura PRIMARY KEY (id_lectura),
    CONSTRAINT fk_lectura_punto FOREIGN KEY (id_punto) REFERENCES PUNTO_MONITOREO (id_punto),
    CONSTRAINT fk_lectura_param FOREIGN KEY (id_parametro) REFERENCES PARAMETRO (id_parametro)
);

CREATE TABLE ALERTA (
    id_alerta NUMBER GENERATED ALWAYS AS IDENTITY,
    id_lectura NUMBER NOT NULL,
    severidad VARCHAR2(7) NOT NULL,
    estado VARCHAR2(10) DEFAULT 'PENDIENTE' NOT NULL,
    fecha_generada TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    fecha_atencion TIMESTAMP,
    CONSTRAINT pk_alerta PRIMARY KEY (id_alerta),
    CONSTRAINT uq_alerta_lectura UNIQUE (id_lectura),
    CONSTRAINT fk_alerta_lectura FOREIGN KEY (id_lectura) REFERENCES LECTURA (id_lectura),
    CONSTRAINT ck_alerta_sev CHECK (severidad IN ('BAJA', 'MEDIA', 'CRITICA')),
    CONSTRAINT ck_alerta_estado CHECK (estado IN ('PENDIENTE', 'ATENDIDA')),
    CONSTRAINT ck_alerta_atencion CHECK (
        (estado = 'PENDIENTE' AND fecha_atencion IS NULL)
        OR (estado = 'ATENDIDA' AND fecha_atencion IS NOT NULL)
    )
);

CREATE TABLE CONEXION_PUNTO (
    id_conexion NUMBER GENERATED ALWAYS AS IDENTITY,
    id_punto_origen NUMBER NOT NULL,
    id_punto_destino NUMBER NOT NULL,
    distancia_km NUMBER(6,2) NOT NULL,
    CONSTRAINT pk_conexion PRIMARY KEY (id_conexion),
    CONSTRAINT uq_conexion_par UNIQUE (id_punto_origen, id_punto_destino),
    CONSTRAINT fk_conexion_orig FOREIGN KEY (id_punto_origen) REFERENCES PUNTO_MONITOREO (id_punto),
    CONSTRAINT fk_conexion_dest FOREIGN KEY (id_punto_destino) REFERENCES PUNTO_MONITOREO (id_punto),
    CONSTRAINT ck_conexion_orden CHECK (id_punto_origen < id_punto_destino),
    CONSTRAINT ck_conexion_dist CHECK (distancia_km > 0)
);

CREATE INDEX idx_lectura_punto ON LECTURA (id_punto);
CREATE INDEX idx_lectura_param ON LECTURA (id_parametro);
CREATE INDEX idx_alerta_estado ON ALERTA (estado);
CREATE INDEX idx_conexion_dest ON CONEXION_PUNTO (id_punto_destino);

CREATE OR REPLACE VIEW V_ALERTA_DETALLE AS
SELECT a.id_alerta, p.id_punto, p.nombre AS punto, pa.nombre AS parametro,
       pa.unidad, l.valor, a.severidad, a.estado,
       l.fecha_hora AS fecha_lectura, a.fecha_generada, a.fecha_atencion
FROM ALERTA a
JOIN LECTURA l ON l.id_lectura = a.id_lectura
JOIN PUNTO_MONITOREO p ON p.id_punto = l.id_punto
JOIN PARAMETRO pa ON pa.id_parametro = l.id_parametro;
