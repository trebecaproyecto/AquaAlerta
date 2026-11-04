-- AquaAlerta Mallorquín - consultas de referencia
-- Ejecutar individualmente después de crear y poblar la base de datos.

-- C1. Alertas pendientes ordenadas por severidad.
SELECT id_alerta, punto, parametro, valor, severidad
FROM V_ALERTA_DETALLE
WHERE estado = 'PENDIENTE'
ORDER BY CASE severidad WHEN 'CRITICA' THEN 3 WHEN 'MEDIA' THEN 2 ELSE 1 END DESC, id_alerta;

-- C2. Historial de alertas atendidas.
SELECT id_alerta, punto, parametro, valor, severidad, fecha_atencion
FROM V_ALERTA_DETALLE
WHERE estado = 'ATENDIDA'
ORDER BY fecha_atencion;

-- C3. Total de alertas por punto y severidad.
SELECT p.nombre AS punto, a.severidad, COUNT(*) AS total
FROM ALERTA a
JOIN LECTURA l ON l.id_lectura = a.id_lectura
JOIN PUNTO_MONITOREO p ON p.id_punto = l.id_punto
GROUP BY p.nombre, a.severidad
ORDER BY p.nombre, a.severidad;

-- C4. Punto con más alertas críticas.
SELECT punto, total
FROM (SELECT punto, COUNT(*) AS total
      FROM V_ALERTA_DETALLE
      WHERE severidad = 'CRITICA'
      GROUP BY punto
      ORDER BY total DESC)
WHERE ROWNUM = 1;

-- C5. Última lectura de cada parámetro en cada punto.
SELECT p.nombre AS punto, pa.nombre AS parametro, l.valor, pa.unidad, l.fecha_hora
FROM LECTURA l
JOIN PUNTO_MONITOREO p ON p.id_punto = l.id_punto
JOIN PARAMETRO pa ON pa.id_parametro = l.id_parametro
WHERE l.fecha_hora = (SELECT MAX(l2.fecha_hora)
                      FROM LECTURA l2
                      WHERE l2.id_punto = l.id_punto
                        AND l2.id_parametro = l.id_parametro)
ORDER BY p.nombre, pa.nombre;

-- C6. Estadísticas descriptivas por parámetro.
SELECT pa.nombre AS parametro, COUNT(*) AS lecturas,
       ROUND(AVG(l.valor), 2) AS promedio,
       MIN(l.valor) AS minimo, MAX(l.valor) AS maximo
FROM LECTURA l
JOIN PARAMETRO pa ON pa.id_parametro = l.id_parametro
GROUP BY pa.nombre
ORDER BY pa.nombre;

-- C7. Vecinos directos del punto 3 (consulta no dirigida).
SELECT id_punto_destino AS vecino, distancia_km
FROM CONEXION_PUNTO WHERE id_punto_origen = 3
UNION ALL
SELECT id_punto_origen AS vecino, distancia_km
FROM CONEXION_PUNTO WHERE id_punto_destino = 3;

-- C8. Lecturas que superan el umbral bajo y no tienen alerta.
SELECT l.id_lectura, l.id_punto, pa.nombre AS parametro, l.valor
FROM LECTURA l
JOIN PARAMETRO pa ON pa.id_parametro = l.id_parametro
WHERE ((pa.sentido = 'MAX' AND l.valor > pa.umbral_bajo)
    OR (pa.sentido = 'MIN' AND l.valor < pa.umbral_bajo))
AND NOT EXISTS (SELECT 1 FROM ALERTA a WHERE a.id_lectura = l.id_lectura);

-- C9. Alertas cuya severidad guardada no coincide con la calculada.
SELECT a.id_alerta, pa.nombre AS parametro, l.valor, a.severidad AS guardada,
       CASE pa.sentido
         WHEN 'MAX' THEN CASE WHEN l.valor > pa.umbral_critico THEN 'CRITICA'
                             WHEN l.valor > pa.umbral_medio THEN 'MEDIA'
                             WHEN l.valor > pa.umbral_bajo THEN 'BAJA' END
         ELSE CASE WHEN l.valor < pa.umbral_critico THEN 'CRITICA'
                  WHEN l.valor < pa.umbral_medio THEN 'MEDIA'
                  WHEN l.valor < pa.umbral_bajo THEN 'BAJA' END
       END AS esperada
FROM ALERTA a
JOIN LECTURA l ON l.id_lectura = a.id_lectura
JOIN PARAMETRO pa ON pa.id_parametro = l.id_parametro
WHERE a.severidad <> CASE pa.sentido
         WHEN 'MAX' THEN CASE WHEN l.valor > pa.umbral_critico THEN 'CRITICA'
                             WHEN l.valor > pa.umbral_medio THEN 'MEDIA'
                             WHEN l.valor > pa.umbral_bajo THEN 'BAJA' END
         ELSE CASE WHEN l.valor < pa.umbral_critico THEN 'CRITICA'
                  WHEN l.valor < pa.umbral_medio THEN 'MEDIA'
                  WHEN l.valor < pa.umbral_bajo THEN 'BAJA' END
       END;
