package pia.estructuras;

import pia.entidades.PuntoMonitoreo;

// Arista: apunta al vertice destino e indica la distancia
public class NodoArista {
   NodoVertice destino;
    double distancia;
    NodoArista siguiente;

    public NodoArista(NodoVertice destino, double distancia) {
        this.destino = destino;
        this.distancia = distancia;
        this.siguiente = null;
    }
}
