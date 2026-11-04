package pia.estructuras;

import pia.entidades.PuntoMonitoreo;

public class NodoVertice {

    PuntoMonitoreo punto;
    NodoArista aristas;
    NodoVertice siguiente;

    public NodoVertice(PuntoMonitoreo punto) {
        this.punto = punto;
        this.aristas = null;
        this.siguiente = null;
    }
}
