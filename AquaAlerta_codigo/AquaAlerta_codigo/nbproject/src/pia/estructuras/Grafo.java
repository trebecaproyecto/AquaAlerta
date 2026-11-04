package pia.estructuras;

import pia.entidades.PuntoMonitoreo;

public class Grafo {

    private NodoVertice primerVertice;
    private int cantidadVertices;

    public Grafo() {
        this.primerVertice = null;
        this.cantidadVertices = 0;
    }

    public int getCantidadVertices() {
        return cantidadVertices;
    }

    // Agregar un punto de monitoreo al grafo
    public void agregarPunto(PuntoMonitoreo punto) {
        NodoVertice nuevo = new NodoVertice(punto);

        nuevo.siguiente = primerVertice;
        primerVertice = nuevo;

        cantidadVertices++;
    }

    // Buscar un vertice por el ID del punto
    private NodoVertice buscarVertice(int idPunto) {
        NodoVertice actual = primerVertice;

        while (actual != null) {
            if (actual.punto.getId() == idPunto) {
                return actual;
            }

            actual = actual.siguiente;
        }

        return null;
    }

    // Obtener todos los puntos de monitoreo
    public PuntoMonitoreo[] obtenerTodosLosPuntos() {
        PuntoMonitoreo[] puntos = new PuntoMonitoreo[cantidadVertices];

        NodoVertice actual = primerVertice;
        int i = 0;

        while (actual != null) {
            puntos[i++] = actual.punto;
            actual = actual.siguiente;
        }

        return puntos;
    }

    // Obtener las adyacencias de un punto
    public int[] obtenerAdyacencias(int idOrigen) {
        NodoVertice vertice = buscarVertice(idOrigen);

        if (vertice == null) {
            return new int[0];
        }

        int contador = 0;
        NodoArista arista = vertice.aristas;

        while (arista != null) {
            contador++;
            arista = arista.siguiente;
        }

        int[] adyacencias = new int[contador];

        arista = vertice.aristas;
        int i = 0;

        while (arista != null) {
            adyacencias[i++] = arista.destino.punto.getId();
            arista = arista.siguiente;
        }

        return adyacencias;
    }

    // Calcular puntos afectados mediante BFS
    public int[] calcularAfectadosBFS(int idOrigen, int nivelesMax) {

        NodoVertice inicio = buscarVertice(idOrigen);

        if (inicio == null || nivelesMax <= 0) {
            return new int[0];
        }

        NodoVertice[] cola = new NodoVertice[cantidadVertices];
        int[] niveles = new int[cantidadVertices];
        boolean[] visitados = new boolean[cantidadVertices];

        int frente = 0;
        int fin = 0;

        // Insertar vertice inicial
        cola[fin] = inicio;
        niveles[fin] = 0;
        fin++;

        // Marcar como visitado mediante posición en el arreglo
        visitados[obtenerIndice(inicio)] = true;

        int[] temporales = new int[cantidadVertices];
        int contador = 0;

        while (frente < fin) {

            NodoVertice actual = cola[frente];
            int nivelActual = niveles[frente];
            frente++;

            if (actual != inicio) {
                temporales[contador++] = actual.punto.getId();
            }

            if (nivelActual >= nivelesMax) {
                continue;
            }

            NodoArista arista = actual.aristas;

            while (arista != null) {

                NodoVertice destino = arista.destino;
                int indice = obtenerIndice(destino);

                if (indice != -1 && !visitados[indice]) {

                    visitados[indice] = true;

                    cola[fin] = destino;
                    niveles[fin] = nivelActual + 1;
                    fin++;
                }

                arista = arista.siguiente;
            }
        }

        int[] afectados = new int[contador];

        System.arraycopy(
            temporales, 0,
            afectados, 0,
            contador
        );

        return afectados;
    }

    // Obtener la posición de un vertice dentro del grafo
    private int obtenerIndice(NodoVertice buscado) {
        NodoVertice actual = primerVertice;
        int indice = 0;

        while (actual != null) {
            if (actual == buscado) {
                return indice;
            }

            actual = actual.siguiente;
            indice++;
        }

        return -1;
    }

    // Agregar una conexión entre dos puntos
    public void agregarConexion(int idOrigen, int idDestino, double distancia) {

        NodoVertice origen = buscarVertice(idOrigen);
        NodoVertice destino = buscarVertice(idDestino);

        if (origen != null && destino != null) {

            // Conexión desde origen hacia destino
            NodoArista arista1 = new NodoArista(destino, distancia);

            arista1.siguiente = origen.aristas;
            origen.aristas = arista1;

            // Conexión desde destino hacia origen
            NodoArista arista2 = new NodoArista(origen, distancia);

            arista2.siguiente = destino.aristas;
            destino.aristas = arista2;
        }
    }

    // Método BFS original: muestra los puntos afectados
    public void estimarPuntosAfectadosBFS(int idOrigen, int nivelesMax) {

        int[] afectados = calcularAfectadosBFS(idOrigen, nivelesMax);

        System.out.println("Puntos afectados por el evento:");

        for (int id : afectados) {
            System.out.println("Punto de monitoreo: " + id);
        }
    }
}