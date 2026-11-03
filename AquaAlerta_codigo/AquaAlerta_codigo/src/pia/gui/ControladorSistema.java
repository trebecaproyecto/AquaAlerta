package pia.gui;

import pia.entidades.Alerta;
import pia.entidades.Lectura;
import pia.entidades.PuntoMonitoreo;
import pia.estructuras.ColaCircularLecturas;
import pia.estructuras.ColaPrioridadAlertas;
import pia.estructuras.Grafo;
import pia.estructuras.ListaDobleAlertas;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Une todas las estructuras de datos y guarda el estado del sistema.
 * Todas las ventanas comparten UN solo controlador.
 */
public class ControladorSistema {
    private ColaCircularLecturas colaLecturas;
    private ColaPrioridadAlertas colaAlertas;
    private ListaDobleAlertas historialAlertas;
    private Grafo grafoCienaga;

    private int idLecturas = 1;
    private int idAlertas = 1;

    // Registro de actividad (se comparte entre ventanas)
    private StringBuilder registro;

    public ControladorSistema() {
        colaLecturas = new ColaCircularLecturas(10);
        colaAlertas = new ColaPrioridadAlertas(20);
        historialAlertas = new ListaDobleAlertas();
        grafoCienaga = new Grafo();
        registro = new StringBuilder();
        inicializarGrafo();
    }

    private void inicializarGrafo() {
        grafoCienaga.agregarPunto(new PuntoMonitoreo(1, "Desembocadura", "Norte"));
        grafoCienaga.agregarPunto(new PuntoMonitoreo(2, "Manglar norte", "Noroeste"));
        grafoCienaga.agregarPunto(new PuntoMonitoreo(3, "Centro", "Centro de Ciénaga"));
        grafoCienaga.agregarPunto(new PuntoMonitoreo(4, "Arroyo León", "Sur"));
        grafoCienaga.agregarPunto(new PuntoMonitoreo(5, "Las Flores", "Este"));

        // Conexiones de prueba
        grafoCienaga.agregarConexion(1, 2, 2.5);
        grafoCienaga.agregarConexion(2, 3, 3.0);
        grafoCienaga.agregarConexion(3, 4, 4.2);
        grafoCienaga.agregarConexion(3, 5, 2.1);
    }

    // ---------------------------------------------------------------
    // Lecturas y alertas
    // ---------------------------------------------------------------

    public void registrarLectura(int idPunto, String parametro, double valor) {
        String fecha = new SimpleDateFormat("HH:mm:ss").format(new Date());
        Lectura l = new Lectura(idLecturas++, idPunto, parametro, valor, fecha);
        colaLecturas.encolar(l);
        agregarLog("Lectura #" + l.getId() + " encolada (punto " + idPunto + ")");
        evaluarLectura(l);
    }

    private void evaluarLectura(Lectura l) {
        String severidad = "";
        // Reglas simples para la demostración
        if (l.getParametro().equals("Oxígeno disuelto")) {
            if (l.getValor() < 2.5) severidad = "CRITICA";
            else if (l.getValor() < 4.0) severidad = "MEDIA";
            else if (l.getValor() < 5.0) severidad = "BAJA";
        } else if (l.getParametro().equals("Coliformes")) {
            if (l.getValor() > 8000) severidad = "CRITICA";
            else if (l.getValor() > 4000) severidad = "MEDIA";
            else if (l.getValor() > 2000) severidad = "BAJA";
        } else {
            // Salinidad
            if (l.getValor() > 40) severidad = "MEDIA";
            else if (l.getValor() > 35) severidad = "BAJA";
        }

        if (!severidad.isEmpty()) {
            Alerta a = new Alerta(idAlertas++, l.getIdPunto(), l.getParametro(), l.getValor(), severidad, l.getFechaHora());
            colaAlertas.insertar(a);
            historialAlertas.insertarAlInicio(a); // al inicio para verla primero
            agregarLog("Alerta " + severidad + " punto " + l.getIdPunto());
        }
    }

    // Envía una lectura escrita a mano. Devuelve {lecturas, alertas, afectados}
    public int[] enviarLecturaManual(int idPunto, String parametro, double valor) {
        int alertasAntes = idAlertas;
        registrarLectura(idPunto, parametro, valor);
        return new int[]{1, idAlertas - alertasAntes, 0};
    }

    // Lluvia fuerte: baja el oxígeno en el punto y en los vecinos (BFS).
    // Devuelve {lecturas nuevas, alertas nuevas, puntos afectados}
    public int[] simularLluvia(int idOrigen, int intensidad, int niveles) {
        int lecturasAntes = idLecturas;
        int alertasAntes = idAlertas;

        double oxigeno = 6.0 - intensidad * 0.045;
        oxigeno = Math.round(oxigeno * 10) / 10.0;

        registrarLectura(idOrigen, "Oxígeno disuelto", oxigeno);

        int[] afectados = grafoCienaga.calcularAfectadosBFS(idOrigen, niveles);
        int i = 0;
        while (i < afectados.length) {
            // Los vecinos se afectan menos que el origen
            registrarLectura(afectados[i], "Oxígeno disuelto", oxigeno + 1.5);
            i++;
        }
        agregarLog("Lluvia fuerte simulada en punto " + idOrigen);
        return new int[]{idLecturas - lecturasAntes, idAlertas - alertasAntes, afectados.length};
    }

    // Vertimiento: sube los coliformes en el punto y en los vecinos (BFS).
    public int[] simularVertimiento(int idOrigen, int intensidad, int niveles) {
        int lecturasAntes = idLecturas;
        int alertasAntes = idAlertas;

        double coliformes = 3000 + intensidad * 80;
        coliformes = Math.round(coliformes);

        registrarLectura(idOrigen, "Coliformes", coliformes);

        int[] afectados = grafoCienaga.calcularAfectadosBFS(idOrigen, niveles);
        int i = 0;
        while (i < afectados.length) {
            registrarLectura(afectados[i], "Coliformes", Math.round(coliformes * 0.5));
            i++;
        }
        agregarLog("Vertimiento simulado en punto " + idOrigen);
        return new int[]{idLecturas - lecturasAntes, idAlertas - alertasAntes, afectados.length};
    }

    // Atiende (extrae) la alerta de mayor prioridad
    public Alerta atenderSiguiente() {
        Alerta extraida = colaAlertas.extraerMaximo();
        if (extraida != null) {
            agregarLog("Atendida alerta #" + extraida.getId() + " - " + extraida.getSeveridad());
        }
        return extraida;
    }

    // Saca la lectura más antigua del búfer
    public Lectura desencolarLectura() {
        Lectura l = colaLecturas.desencolar();
        if (l != null) {
            agregarLog("Lectura #" + l.getId() + " desencolada");
        }
        return l;
    }

    // Borra una alerta del historial (lista doble)
    public boolean eliminarDelHistorial(int id) {
        boolean ok = historialAlertas.eliminarPorId(id);
        if (ok) {
            agregarLog("Alerta #" + id + " eliminada del historial");
        }
        return ok;
    }

    // ---------------------------------------------------------------
    // Consultas para las ventanas
    // ---------------------------------------------------------------

    public static int valorSeveridad(String severidad) {
        if ("CRITICA".equals(severidad)) return 3;
        if ("MEDIA".equals(severidad)) return 2;
        if ("BAJA".equals(severidad)) return 1;
        return 0;
    }

    // Copia las alertas pendientes y las ordena de mayor a menor severidad
    // (la cola de prioridad NO se modifica)
    public Alerta[] obtenerAlertasOrdenadas() {
        Alerta[] heap = colaAlertas.getHeap();
        int tam = colaAlertas.getTamano();
        Alerta[] copia = new Alerta[tam];
        int i = 0;
        while (i < tam) {
            copia[i] = heap[i];
            i++;
        }

        // Ordenamiento burbuja: severidad mayor primero, y a igualdad el id menor primero
        boolean huboCambio = true;
        while (huboCambio) {
            huboCambio = false;
            int j = 0;
            while (j < tam - 1) {
                int sevA = valorSeveridad(copia[j].getSeveridad());
                int sevB = valorSeveridad(copia[j + 1].getSeveridad());
                boolean cambiar = false;
                if (sevB > sevA) {
                    cambiar = true;
                } else if (sevB == sevA && copia[j + 1].getId() < copia[j].getId()) {
                    cambiar = true;
                }
                if (cambiar) {
                    Alerta temp = copia[j];
                    copia[j] = copia[j + 1];
                    copia[j + 1] = temp;
                    huboCambio = true;
                }
                j++;
            }
        }
        return copia;
    }

    // Estado general: Normal, Precaución o Crítico
    public String getEstadoGeneral() {
        Alerta[] heap = colaAlertas.getHeap();
        int tam = colaAlertas.getTamano();
        int maxima = 0;
        int i = 0;
        while (i < tam) {
            int v = valorSeveridad(heap[i].getSeveridad());
            if (v > maxima) {
                maxima = v;
            }
            i++;
        }
        if (maxima == 3) return "Crítico";
        if (maxima >= 1) return "Precaución";
        return "Normal";
    }

    public PuntoMonitoreo buscarPunto(int id) {
        PuntoMonitoreo[] puntos = grafoCienaga.obtenerTodosLosPuntos();
        int i = 0;
        while (i < puntos.length) {
            if (puntos[i].getId() == id) {
                return puntos[i];
            }
            i++;
        }
        return null;
    }

    public String nombrePunto(int id) {
        PuntoMonitoreo p = buscarPunto(id);
        if (p == null) {
            return "Punto " + id;
        }
        return p.getNombre();
    }

    public static String unidadParametro(String parametro) {
        if ("Oxígeno disuelto".equals(parametro)) return "mg/L";
        if ("Coliformes".equals(parametro)) return "NMP/100 mL";
        return "ppt";
    }

    // ---------------------------------------------------------------
    // Registro de actividad
    // ---------------------------------------------------------------

    public void agregarLog(String mensaje) {
        String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
        registro.append("> ").append(hora).append(" ").append(mensaje).append("\n");
    }

    public String getRegistro() {
        return registro.toString();
    }

    // Getters para compartir el estado
    public Grafo getGrafo() { return grafoCienaga; }
    public ColaCircularLecturas getColaLecturas() { return colaLecturas; }
    public ColaPrioridadAlertas getColaAlertas() { return colaAlertas; }
    public ListaDobleAlertas getHistorialAlertas() { return historialAlertas; }
}