package pia.estructuras;
import pia.entidades.Alerta;

public class ListaDobleAlertas {
    private NodoAlerta cabeza;
    private NodoAlerta cola;
    private int tamano;

    public ListaDobleAlertas() {
        cabeza = null;
        cola = null;
        tamano = 0;
    }

    public boolean estaVacia() { return tamano == 0; }
    public int getTamano() { return tamano; }
    
    // NUEVO MÉTODO PARA LA INTERFAZ: Extrae las alertas en un arreglo para la JTable
    public Alerta[] obtenerArregloAlertas() {
        Alerta[] arreglo = new Alerta[tamano];
        NodoAlerta actual = cabeza;
        int i = 0;
        while(actual != null) {
            arreglo[i++] = actual.dato;
            actual = actual.siguiente;
        }
        return arreglo;
    }

    public void insertarAlFinal(Alerta alerta) {
        NodoAlerta nuevo = new NodoAlerta(alerta);
        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            nuevo.anterior = cola;
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamano++;
    }

    public void insertarAlInicio(Alerta alerta) {
        NodoAlerta nuevo = new NodoAlerta(alerta);
        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            nuevo.siguiente = cabeza;
            cabeza.anterior = nuevo;
            cabeza = nuevo;
        }
        tamano++;
    }

    public Alerta buscarPorId(int id) {
        NodoAlerta actual = cabeza;
        while (actual != null) {
            if (actual.dato.getId() == id) return actual.dato;
            actual = actual.siguiente;
        }
        return null;
    }
    
    // NUEVO MÉTODO PARA LA INTERFAZ: Devuelve la posición para el panel de detalles
    public int[] obtenerPosicionYVecinos(int id) {
        NodoAlerta actual = cabeza;
        int pos = 1;
        while(actual != null) {
            if(actual.dato.getId() == id) {
                int antId = (actual.anterior != null) ? actual.anterior.dato.getId() : -1;
                int sigId = (actual.siguiente != null) ? actual.siguiente.dato.getId() : -1;
                return new int[]{pos, antId, sigId};
            }
            actual = actual.siguiente;
            pos++;
        }
        return null;
    }

    public void recorrerAdelante() {
        if (estaVacia()) { System.out.println("(historial vacío)"); return; }
        NodoAlerta actual = cabeza;
        while (actual != null) { System.out.println(actual.dato); actual = actual.siguiente; }
    }

    public void recorrerAtras() {
        if (estaVacia()) { System.out.println("(historial vacío)"); return; }
        NodoAlerta actual = cola;
        while (actual != null) { System.out.println(actual.dato); actual = actual.anterior; }
    }

    public boolean eliminarPorId(int id) {
        NodoAlerta actual = cabeza;
        while (actual != null) {
            if (actual.dato.getId() == id) {
                if (actual.anterior == null) cabeza = actual.siguiente;
                else actual.anterior.siguiente = actual.siguiente;
                if (actual.siguiente == null) cola = actual.anterior;
                else actual.siguiente.anterior = actual.anterior;
                tamano--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }
}
