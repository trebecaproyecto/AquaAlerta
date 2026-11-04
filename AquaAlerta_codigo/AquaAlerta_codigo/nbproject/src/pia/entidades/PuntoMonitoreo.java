package pia.entidades;

/**
 * Punto de monitoreo de la Cienaga de Mallorquin.
 */
public class PuntoMonitoreo {

    private int id;
    private String nombre;
    private String ubicacion;

    public PuntoMonitoreo(int id, String nombre, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }
}
