package pia.estructuras;
import pia.entidades.Lectura;

public class ColaCircularLecturas {
    private Lectura[] datos;
    private int frente;
    private int fin;
    private int tamano;
    private int capacidad;

    public ColaCircularLecturas(int capacidad) {
        this.capacidad = capacidad;
        this.datos = new Lectura[capacidad];
        this.frente = 0;
        this.fin = -1;
        this.tamano = 0;
    }

    public boolean estaVacia() { return tamano == 0; }
    public boolean estaLlena() { return tamano == capacidad; }
    public int getTamano() { return tamano; }
    
    // NUEVOS MÉTODOS AÑADIDOS PARA LA INTERFAZ GRÁFICA
    public Lectura[] getDatos() { return datos; }
    public int getFrente() { return frente; }
    public int getFin() { return fin; }
    public int getCapacidad() { return capacidad; }

    public void encolar(Lectura lectura) {
        if (estaLlena()) {
            frente = (frente + 1) % capacidad;
            tamano--;
        }
        fin = (fin + 1) % capacidad;
        datos[fin] = lectura;
        tamano++;
    }

    public Lectura desencolar() {
        if (estaVacia()) return null;
        Lectura extraida = datos[frente];
        datos[frente] = null;
        frente = (frente + 1) % capacidad;
        tamano--;
        return extraida;
    }

    public Lectura verFrente() {
        if (estaVacia()) return null;
        return datos[frente];
    }
}
