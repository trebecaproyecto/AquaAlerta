package pia.entidades;

/**
 * Entidad que representa una alerta generada cuando una lectura
 * de un punto de monitoreo supera el umbral de un parametro.
 */
public class Alerta {
    private int id;
    private int idPunto;
    private String parametro;
    private double valor;
    private String severidad; // BAJA, MEDIA o CRITICA
    private String fechaHora;

    public Alerta(int id, int idPunto, String parametro, double valor,
                  String severidad, String fechaHora) {
        this.id = id;
        this.idPunto = idPunto;
        this.parametro = parametro;
        this.valor = valor;
        this.severidad = severidad;
        this.fechaHora = fechaHora;
    }

    public int getId() { return id; }
    public int getIdPunto() { return idPunto; }
    public String getParametro() { return parametro; }
    public double getValor() { return valor; }
    public String getSeveridad() { return severidad; }
    public String getFechaHora() { return fechaHora; }
    public void setSeveridad(String severidad) { this.severidad = severidad; }

    @Override
    public String toString() {
        return "Alerta #" + id + " | Punto " + idPunto + " | " + parametro
                + " = " + valor + " | " + severidad + " | " + fechaHora;
    }
}
