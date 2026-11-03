package pia.entidades;

/**
 * Lectura de un sensor en un punto de monitoreo para un parametro.
 */
public class Lectura {
    private int id;
    private int idPunto;
    private String parametro;
    private double valor;
    private String fechaHora;

    public Lectura(int id, int idPunto, String parametro, double valor, String fechaHora) {
        this.id = id;
        this.idPunto = idPunto;
        this.parametro = parametro;
        this.valor = valor;
        this.fechaHora = fechaHora;
    }

    public int getId() { return id; }
    public int getIdPunto() { return idPunto; }
    public String getParametro() { return parametro; }
    public double getValor() { return valor; }
    public String getFechaHora() { return fechaHora; }

    @Override
    public String toString() {
        return "Lectura #" + id + " | Punto " + idPunto + " | " + parametro
                + " = " + valor + " | " + fechaHora;
    }
}
