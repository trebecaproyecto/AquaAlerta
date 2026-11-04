package pia.estructuras;

import pia.entidades.Alerta;

// Nodo de la lista doblemente enlazada: guarda una alerta y dos referencias
public class NodoAlerta {
    Alerta dato;
    NodoAlerta anterior;
    NodoAlerta siguiente;

    public NodoAlerta(Alerta dato) {
        this.dato = dato;
        this.anterior = null;
        this.siguiente = null;
    }
}
