package pia.conexion;

import pia.entidades.Lectura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Lee las lecturas que insertaste en Oracle (SQL Plus).
 * Si algún nombre cambia, se modifica SOLO el texto de la consulta (SQL).
 */
public class LecturaDAO {

    // Tablas: LECTURA (id_lectura, id_punto, id_parametro, valor, fecha_hora)
    // unida con PARAMETRO para obtener el nombre del parámetro
    private static final String SQL =
        "SELECT l.id_lectura, l.id_punto, pa.nombre AS parametro, l.valor, "
        + "TO_CHAR(l.fecha_hora, 'HH24:MI:SS') AS hora "
        + "FROM LECTURA l JOIN PARAMETRO pa ON pa.id_parametro = l.id_parametro "
        + "WHERE l.id_lectura > ? ORDER BY l.id_lectura";

    // Trae las lecturas cuyo ID sea mayor a ultimoId (así no se repiten al pulsar el botón dos veces)
    public static ArrayList<Lectura> obtenerNuevas(int ultimoId) throws SQLException {
        ArrayList<Lectura> lista = new ArrayList<>();

        Connection conn = ConexionBD.obtenerConexion();
        if (conn == null) {
            throw new SQLException("No se pudo conectar a Oracle. Revisa usuario, clave y que el servicio esté activo.");
        }

        try {
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setInt(1, ultimoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_lectura");
                int idPunto = rs.getInt("id_punto");
                String parametro = rs.getString("parametro");
                double valor = rs.getDouble("valor");
                String hora = rs.getString("hora");
                lista.add(new Lectura(id, idPunto, parametro, valor, hora));
            }

            rs.close();
            ps.close();
        } finally {
            ConexionBD.cerrarConexion(conn);
        }
        return lista;
    }
}