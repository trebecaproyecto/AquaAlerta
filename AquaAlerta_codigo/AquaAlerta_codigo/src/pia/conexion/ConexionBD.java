/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pia.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    // Parámetros de conexión para Oracle XE Local
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE"; // O :ORCL segun tu instalacion
    private static final String USUARIO = "TU_USUARIO_ORACLE"; // Ej. SYSTEM o tu esquema
    private static final String CLAVE = "TU_CONTRASEÑA";

    public static Connection obtenerConexion() {
        Connection conn = null;
        try {
            // Cargar el driver JDBC de Oracle
            Class.forName("oracle.jdbc.OracleDriver");
            // Establecer la conexión
            conn = DriverManager.getConnection(URL, USUARIO, CLAVE);
            System.out.println("Conexión a Oracle exitosa.");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Driver OJDBC no encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos Oracle.");
            e.printStackTrace();
        }
        return conn;
    }

    public static void cerrarConexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
