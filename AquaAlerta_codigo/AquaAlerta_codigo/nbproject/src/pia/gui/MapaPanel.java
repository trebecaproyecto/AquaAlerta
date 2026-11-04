package pia.gui;

import pia.entidades.Alerta;
import pia.entidades.PuntoMonitoreo;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Dibuja el grafo de la Ciénaga. Se usa en el Panel Principal y en Estructuras.
 *
 * Modo automático: pinta los puntos según las alertas pendientes y marca
 * con BFS los puntos afectados por los puntos críticos.
 * Modo manual (Estructuras): se elige un origen y se pinta lo que alcanza el BFS.
 */
public class MapaPanel extends JPanel {

    private ControladorSistema controlador;

    // Posición de cada punto como fracción del ancho y alto (así el mapa se adapta al tamaño)
    private double[][] posiciones = {
        {0.14, 0.73}, {0.33, 0.43}, {0.55, 0.60}, {0.78, 0.37}, {0.73, 0.80}
    };

    private int idOrigenManual = 0;   // 0 = modo automático
    private int nivelesManual = 2;

    public MapaPanel(ControladorSistema controlador) {
        this.controlador = controlador;
        setBackground(UIUtils.COLOR_MIST);
    }

    // Activa el modo manual: un origen y cuántos niveles de BFS
    public void setOrigenManual(int idOrigen, int niveles) {
        this.idOrigenManual = idOrigen;
        this.nivelesManual = niveles;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Agua de la ciénaga (una curva decorativa)
        Path2D.Double agua = new Path2D.Double();
        agua.moveTo(w * 0.05, h * 0.77);
        agua.curveTo(w * 0.19, h * 0.50, w * 0.31, h * 0.87, w * 0.50, h * 0.63);
        agua.curveTo(w * 0.69, h * 0.40, w * 0.875, h * 0.37, w * 0.97, h * 0.57);
        agua.lineTo(w * 0.97, h);
        agua.lineTo(w * 0.05, h);
        agua.closePath();
        g2.setColor(new Color(207, 233, 236, 180));
        g2.fill(agua);

        PuntoMonitoreo[] puntos = controlador.getGrafo().obtenerTodosLosPuntos();

        // Aristas (conexiones entre puntos)
        g2.setColor(new Color(138, 166, 179));
        g2.setStroke(new BasicStroke(3));
        int i = 0;
        while (i < puntos.length) {
            int id = puntos[i].getId();
            int[] ady = controlador.getGrafo().obtenerAdyacencias(id);
            int k = 0;
            while (k < ady.length) {
                // Cada conexión está guardada en los dos sentidos: se dibuja una sola vez
                if (id < ady[k]) {
                    g2.drawLine(coordX(id, w), coordY(id, h), coordX(ady[k], w), coordY(ady[k], h));
                }
                k++;
            }
            i++;
        }

        // Severidad de cada punto y puntos afectados
        int[] severidadPorPunto = new int[puntos.length + 1];
        boolean[] afectadosBFS = new boolean[puntos.length + 1];

        if (idOrigenManual > 0) {
            severidadPorPunto[idOrigenManual] = 3;
            marcarAfectados(idOrigenManual, nivelesManual, afectadosBFS);
        } else {
            Alerta[] heap = controlador.getColaAlertas().getHeap();
            int tam = controlador.getColaAlertas().getTamano();
            int j = 0;
            while (j < tam) {
                int idp = heap[j].getIdPunto();
                int s = ControladorSistema.valorSeveridad(heap[j].getSeveridad());
                if (idp < severidadPorPunto.length && s > severidadPorPunto[idp]) {
                    severidadPorPunto[idp] = s;
                }
                j++;
            }
            // Los puntos críticos afectan a sus vecinos (2 niveles)
            int p = 1;
            while (p < severidadPorPunto.length) {
                if (severidadPorPunto[p] == 3) {
                    marcarAfectados(p, 2, afectadosBFS);
                }
                p++;
            }
        }

        // Nodos
        int radio = Math.max(14, Math.min(22, Math.min(w, h) / 12));
        i = 0;
        while (i < puntos.length) {
            int id = puntos[i].getId();
            int x = coordX(id, w);
            int y = coordY(id, h);
            int sev = severidadPorPunto[id];

            Color color = UIUtils.COLOR_OK;
            if (sev == 3) {
                color = UIUtils.COLOR_CRIT;
            } else if (sev >= 1) {
                color = UIUtils.COLOR_WARN;
            } else if (afectadosBFS[id]) {
                color = UIUtils.COLOR_AFF;
            }

            // Círculo punteado alrededor de los puntos críticos
            if (sev == 3) {
                float[] guiones = {5f, 4f};
                g2.setColor(UIUtils.COLOR_CRIT);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, guiones, 0f));
                g2.drawOval(x - radio - 14, y - radio - 14, (radio + 14) * 2, (radio + 14) * 2);
            }

            int r = radio;
            if (sev == 3) {
                r = radio + 3;
            }
            g2.setColor(color);
            g2.fillOval(x - r, y - r, r * 2, r * 2);

            // Número del punto
            if (sev == 1 || sev == 2) {
                g2.setColor(UIUtils.COLOR_TEXTO_WARN);
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            FontMetrics fm = g2.getFontMetrics();
            String num = String.valueOf(id);
            g2.drawString(num, x - fm.stringWidth(num) / 2, y + fm.getAscent() / 2 - 1);

            // Nombre del punto
            g2.setColor(UIUtils.COLOR_INK);
            g2.setFont(UIUtils.FONT_TEXT);
            fm = g2.getFontMetrics();
            String nombre = puntos[i].getNombre();
            g2.drawString(nombre, x - fm.stringWidth(nombre) / 2, y + r + 16);
            i++;
        }
    }

    // Marca en el arreglo los puntos que alcanza el BFS desde un origen
    private void marcarAfectados(int idOrigen, int niveles, boolean[] marcados) {
        int[] afectados = controlador.getGrafo().calcularAfectadosBFS(idOrigen, niveles);
        int i = 0;
        while (i < afectados.length) {
            marcados[afectados[i]] = true;
            i++;
        }
    }

    private int coordX(int id, int ancho) {
        return (int) (posiciones[id - 1][0] * ancho);
    }

    private int coordY(int id, int alto) {
        return (int) (posiciones[id - 1][1] * alto);
    }
}