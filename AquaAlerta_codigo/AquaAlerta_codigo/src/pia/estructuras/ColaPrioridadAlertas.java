package pia.estructuras;
import pia.entidades.Alerta;

public class ColaPrioridadAlertas {
    private Alerta[] heap;
    private int tamano;
    private int capacidad;

    public ColaPrioridadAlertas(int capacidad) {
        this.capacidad = capacidad;
        this.heap = new Alerta[capacidad];
        this.tamano = 0;
    }

    public boolean estaVacia() { return tamano == 0; }
    public int getTamano() { return tamano; }
    
    // NUEVOS MÉTODOS AÑADIDOS PARA LA INTERFAZ GRÁFICA
    public Alerta[] getHeap() { return heap; }
    public int getCapacidad() { return capacidad; }

    private int obtenerValorSeveridad(String severidad) {
        if (severidad == null) return 0;
        switch (severidad.toUpperCase()) {
            case "CRITICA": return 3;
            case "MEDIA":   return 2;
            case "BAJA":    return 1;
            default:        return 0;
        }
    }

    private boolean esMayorPrioridad(Alerta a1, Alerta a2) {
        int p1 = obtenerValorSeveridad(a1.getSeveridad());
        int p2 = obtenerValorSeveridad(a2.getSeveridad());
        return p1 > p2;
    }

    public void insertar(Alerta alerta) {
        if (tamano == capacidad) return;
        heap[tamano] = alerta;
        int i = tamano;
        tamano++;
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (esMayorPrioridad(heap[i], heap[padre])) {
                Alerta temp = heap[i];
                heap[i] = heap[padre];
                heap[padre] = temp;
                i = padre;
            } else {
                break;
            }
        }
    }

    public Alerta extraerMaximo() {
        if (estaVacia()) return null;
        Alerta maximo = heap[0];
        heap[0] = heap[tamano - 1];
        heap[tamano - 1] = null;
        tamano--;
        int i = 0;
        while (true) {
            int izq = 2 * i + 1;
            int der = 2 * i + 2;
            int mayor = i;
            if (izq < tamano && esMayorPrioridad(heap[izq], heap[mayor])) mayor = izq;
            if (der < tamano && esMayorPrioridad(heap[der], heap[mayor])) mayor = der;
            if (mayor != i) {
                Alerta temp = heap[i];
                heap[i] = heap[mayor];
                heap[mayor] = temp;
                i = mayor;
            } else {
                break;
            }
        }
        return maximo;
    }

    public Alerta verMaximo() {
        if (estaVacia()) return null;
        return heap[0];
    }
}