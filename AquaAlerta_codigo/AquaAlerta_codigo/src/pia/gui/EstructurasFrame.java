package pia.gui;

import pia.entidades.Alerta;
import pia.entidades.Lectura;
import pia.estructuras.ColaCircularLecturas;
import pia.estructuras.ColaPrioridadAlertas;

import javax.swing.*;
import java.awt.*;

/**
 * Muestra por dentro cómo funciona cada estructura de datos (muy útil para sustentar).
 */
public class EstructurasFrame extends JFrame implements Actualizable {

    private ControladorSistema controlador;
    private JPanel panelCola;
    private JPanel panelHeap;
    private JPanel panelLista;
    private MapaPanel mapaGrafo;
    private JComboBox<String> cmbOrigen;
    private JComboBox<String> cmbNiveles;
    private JLabel lblAfectados;

    public EstructurasFrame(ControladorSistema controlador) {
        this.controlador = controlador;
        setTitle("AquaAlerta Mallorquín - Estructuras en Vivo");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.COLOR_BG);

        add(UIUtils.crearEncabezado(this, "🧩", "Estructuras en vivo",
                "Para mostrar cómo funciona cada estructura por dentro", "Estructuras", null), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(2, 2, 15, 15));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        body.add(crearTarjetaCola());
        body.add(crearTarjetaHeap());
        body.add(crearTarjetaLista());
        body.add(crearTarjetaGrafo());
        add(body, BorderLayout.CENTER);

        calcularBFS();
    }

    // ---------------------------------------------------------------
    // 1) Cola circular
    // ---------------------------------------------------------------

    private JPanel crearTarjetaCola() {
        JPanel tarjeta = UIUtils.crearTarjeta("🔁 Cola circular", "Arreglo de tamaño fijo");

        panelCola = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = prepararGraficos(g);
                ColaCircularLecturas cola = controlador.getColaLecturas();
                Lectura[] arr = cola.getDatos();
                int cap = cola.getCapacidad();
                int frente = cola.getFrente();
                int fin = cola.getFin();

                g2.setColor(UIUtils.COLOR_MUTED);
                g2.setFont(UIUtils.FONT_TEXT);
                g2.drawString("frente = " + frente + "  ·  fin = " + fin + "  ·  tamaño = "
                        + cola.getTamano() + "/" + cap, 0, 16);

                int separacion = 6;
                int ancho = (getWidth() - separacion * (cap - 1)) / cap;
                if (ancho > 60) {
                    ancho = 60;
                }
                int alto = 54;
                int y = 60;
                int i = 0;
                while (i < cap) {
                    int x = i * (ancho + separacion);
                    if (arr[i] != null) {
                        g2.setColor(UIUtils.COLOR_TEAL);
                        g2.fillRoundRect(x, y, ancho, alto, 10, 10);
                        g2.setColor(Color.WHITE);
                        g2.setFont(UIUtils.FONT_BOLD);
                        centrarTexto(g2, "L" + arr[i].getId(), x, ancho, y + 32);
                    } else {
                        g2.setColor(new Color(185, 198, 207));
                        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                                10f, new float[]{4f, 3f}, 0f));
                        g2.drawRoundRect(x, y, ancho, alto, 10, 10);
                        g2.setStroke(new BasicStroke(1f));
                    }
                    // Número de posición del arreglo
                    g2.setColor(UIUtils.COLOR_MUTED);
                    g2.setFont(UIUtils.FONT_SMALL);
                    centrarTexto(g2, "[" + i + "]", x, ancho, y + alto + 14);

                    // Marcas de frente y fin
                    g2.setFont(UIUtils.FONT_BOLD);
                    if (i == frente) {
                        g2.setColor(UIUtils.COLOR_CRIT);
                        centrarTexto(g2, "frente ↓", x, ancho, y - 8);
                    }
                    if (i == fin) {
                        g2.setColor(UIUtils.COLOR_AFF);
                        centrarTexto(g2, "↑ fin", x, ancho, y + alto + 30);
                    }
                    i++;
                }
            }
        };
        panelCola.setOpaque(false);
        tarjeta.add(panelCola, BorderLayout.CENTER);

        JButton btnEncolar = UIUtils.crearBoton("Encolar lectura", UIUtils.COLOR_TEAL, Color.WHITE, 140, 34);
        JButton btnDesencolar = UIUtils.crearBoton("Desencolar", Color.WHITE, UIUtils.COLOR_NAVY, 120, 34);
        btnEncolar.addActionListener(e -> {
            // Lectura de prueba: punto al azar y salinidad normal (no genera alerta)
            int punto = (int) (Math.random() * 5) + 1;
            double valor = Math.round((10 + Math.random() * 20) * 10) / 10.0;
            controlador.registrarLectura(punto, "Salinidad", valor);
            actualizarTodo();
        });
        btnDesencolar.addActionListener(e -> {
            Lectura l = controlador.desencolarLectura();
            if (l == null) {
                JOptionPane.showMessageDialog(this, "La cola está vacía");
            }
            actualizarTodo();
        });
        tarjeta.add(crearFilaBotones(btnEncolar, btnDesencolar), BorderLayout.SOUTH);
        return tarjeta;
    }

    // ---------------------------------------------------------------
    // 2) Cola de prioridad (heap)
    // ---------------------------------------------------------------

    private JPanel crearTarjetaHeap() {
        JPanel tarjeta = UIUtils.crearTarjeta("🌳 Cola de prioridad", "Max-heap binario");

        panelHeap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = prepararGraficos(g);
                ColaPrioridadAlertas heap = controlador.getColaAlertas();
                Alerta[] arr = heap.getHeap();
                int tam = heap.getTamano();

                if (tam == 0) {
                    g2.setColor(UIUtils.COLOR_MUTED);
                    g2.setFont(UIUtils.FONT_TEXT);
                    g2.drawString("El heap está vacío. Genera una alerta para verla aquí.", 10, 30);
                    return;
                }

                // Cuántos niveles tiene el árbol
                int niveles = 1;
                while ((1 << niveles) - 1 < tam) {
                    niveles++;
                }
                int separacionY = 52;
                if (niveles > 1 && (getHeight() - 40) / (niveles - 1) < separacionY) {
                    separacionY = (getHeight() - 40) / (niveles - 1);
                }
                int radio = 18;

                // Primero las líneas (padre -> hijos), para que queden debajo de los círculos
                g2.setColor(new Color(138, 166, 179));
                g2.setStroke(new BasicStroke(2));
                int i = 1;
                while (i < tam) {
                    int padre = (i - 1) / 2;
                    g2.drawLine(posX(padre), posY(padre, separacionY), posX(i), posY(i, separacionY));
                    i++;
                }

                // Después los nodos
                i = 0;
                while (i < tam) {
                    int x = posX(i);
                    int y = posY(i, separacionY);
                    String sev = arr[i].getSeveridad();
                    g2.setColor(UIUtils.colorSeveridad(sev));
                    g2.fillOval(x - radio, y - radio, radio * 2, radio * 2);
                    g2.setColor(UIUtils.textoSeveridad(sev));
                    g2.setFont(UIUtils.FONT_BOLD);
                    centrarTexto(g2, "A" + arr[i].getId(), x - radio, radio * 2, y + 5);
                    i++;
                }
                g2.setColor(UIUtils.COLOR_MUTED);
                g2.setFont(UIUtils.FONT_SMALL);
                g2.drawString("Raíz = alerta de mayor prioridad", 0, getHeight() - 4);
            }

            // Posición horizontal de un nodo según su nivel
            private int posX(int indice) {
                int nivel = 0;
                while ((1 << (nivel + 1)) - 1 <= indice) {
                    nivel++;
                }
                int posEnNivel = indice - ((1 << nivel) - 1);
                int cantidadEnNivel = 1 << nivel;
                return (int) ((posEnNivel + 0.5) / cantidadEnNivel * getWidth());
            }

            private int posY(int indice, int separacionY) {
                int nivel = 0;
                while ((1 << (nivel + 1)) - 1 <= indice) {
                    nivel++;
                }
                return 26 + nivel * separacionY;
            }
        };
        panelHeap.setOpaque(false);
        tarjeta.add(panelHeap, BorderLayout.CENTER);

        JButton btnGenerar = UIUtils.crearBoton("Generar alerta", UIUtils.COLOR_TEAL, Color.WHITE, 140, 34);
        JButton btnAtender = UIUtils.crearBoton("Atender (extraer)", Color.WHITE, UIUtils.COLOR_NAVY, 150, 34);
        btnGenerar.addActionListener(e -> {
            // Lecturas de ejemplo que generan alertas de distinta severidad
            String[] parametros = {"Oxígeno disuelto", "Oxígeno disuelto", "Coliformes", "Coliformes", "Salinidad", "Salinidad"};
            double[] valores = {2.0, 3.5, 9000, 5000, 38, 45};
            int pos = (int) (Math.random() * parametros.length);
            int punto = (int) (Math.random() * 5) + 1;
            controlador.registrarLectura(punto, parametros[pos], valores[pos]);
            actualizarTodo();
        });
        btnAtender.addActionListener(e -> {
            if (controlador.atenderSiguiente() == null) {
                JOptionPane.showMessageDialog(this, "No hay alertas pendientes");
            }
            actualizarTodo();
        });
        tarjeta.add(crearFilaBotones(btnGenerar, btnAtender), BorderLayout.SOUTH);
        return tarjeta;
    }

    // ---------------------------------------------------------------
    // 3) Lista doblemente enlazada
    // ---------------------------------------------------------------

    private JPanel crearTarjetaLista() {
        JPanel tarjeta = UIUtils.crearTarjeta("🔗 Lista doblemente enlazada", "cabeza ↔ cola");

        panelLista = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = prepararGraficos(g);
                Alerta[] arr = controlador.getHistorialAlertas().obtenerArregloAlertas();

                if (arr.length == 0) {
                    g2.setColor(UIUtils.COLOR_MUTED);
                    g2.setFont(UIUtils.FONT_TEXT);
                    g2.drawString("La lista está vacía (historial sin alertas).", 10, 30);
                    return;
                }

                int ancho = 66;
                int alto = 50;
                int hueco = 36;
                int y = 60;
                int cabenTodos = (getWidth() + hueco) / (ancho + hueco);
                if (cabenTodos < 1) {
                    cabenTodos = 1;
                }
                int mostrar = arr.length;
                if (mostrar > cabenTodos) {
                    mostrar = cabenTodos;
                }

                int i = 0;
                while (i < mostrar) {
                    int x = i * (ancho + hueco);
                    Color color = UIUtils.colorSeveridad(arr[i].getSeveridad());
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(x, y, ancho, alto, 10, 10);
                    g2.setColor(UIUtils.COLOR_NAVY);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(x, y, ancho, alto, 10, 10);
                    g2.setColor(color);
                    g2.fillRect(x + 8, y + alto - 12, ancho - 16, 5);
                    g2.setColor(UIUtils.COLOR_INK);
                    g2.setFont(UIUtils.FONT_BOLD);
                    centrarTexto(g2, "A" + arr[i].getId(), x, ancho, y + 26);

                    // Marcas de cabeza y cola
                    g2.setFont(UIUtils.FONT_SMALL);
                    g2.setColor(UIUtils.COLOR_MUTED);
                    if (i == 0) {
                        centrarTexto(g2, "cabeza", x, ancho, y - 8);
                    }
                    if (i == arr.length - 1) {
                        centrarTexto(g2, "cola", x, ancho, y - 8);
                    }

                    // Flecha doble hacia el siguiente nodo
                    if (i < mostrar - 1) {
                        dibujarFlechaDoble(g2, x + ancho + 4, x + ancho + hueco - 4, y + alto / 2);
                    }
                    i++;
                }
                if (mostrar < arr.length) {
                    g2.setColor(UIUtils.COLOR_MUTED);
                    g2.setFont(UIUtils.FONT_TEXT);
                    g2.drawString("… y " + (arr.length - mostrar) + " más", 0, y + alto + 30);
                }
                g2.setColor(UIUtils.COLOR_MUTED);
                g2.setFont(UIUtils.FONT_SMALL);
                g2.drawString("Cabeza = alerta más reciente · Cola = alerta más antigua. "
                        + "Se recorre hacia adelante o hacia atrás.", 0, getHeight() - 4);
            }
        };
        panelLista.setOpaque(false);
        tarjeta.add(panelLista, BorderLayout.CENTER);
        return tarjeta;
    }

    // ---------------------------------------------------------------
    // 4) Grafo y BFS
    // ---------------------------------------------------------------

    private JPanel crearTarjetaGrafo() {
        JPanel tarjeta = UIUtils.crearTarjeta("🕸 Grafo y BFS", "lista de adyacencia");

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controles.setOpaque(false);
        controles.add(UIUtils.crearEtiqueta("Origen:"));
        cmbOrigen = new JComboBox<>(new String[]{"1 · Desembocadura", "2 · Manglar norte", "3 · Centro",
            "4 · Arroyo León", "5 · Las Flores"});
        cmbOrigen.setSelectedIndex(2);
        controles.add(cmbOrigen);
        controles.add(UIUtils.crearEtiqueta("Niveles:"));
        cmbNiveles = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        cmbNiveles.setSelectedIndex(1);
        controles.add(cmbNiveles);

        cmbOrigen.addActionListener(e -> calcularBFS());
        cmbNiveles.addActionListener(e -> calcularBFS());

        mapaGrafo = new MapaPanel(controlador);
        lblAfectados = new JLabel(" ");
        lblAfectados.setFont(UIUtils.FONT_BOLD);
        lblAfectados.setForeground(UIUtils.COLOR_AFF);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.setOpaque(false);
        centro.add(controles, BorderLayout.NORTH);
        centro.add(mapaGrafo, BorderLayout.CENTER);
        centro.add(lblAfectados, BorderLayout.SOUTH);
        tarjeta.add(centro, BorderLayout.CENTER);
        return tarjeta;
    }

    // Ejecuta el BFS desde el origen elegido y muestra el resultado
    private void calcularBFS() {
        int origen = cmbOrigen.getSelectedIndex() + 1;
        int niveles = cmbNiveles.getSelectedIndex() + 1;
        mapaGrafo.setOrigenManual(origen, niveles);

        int[] afectados = controlador.getGrafo().calcularAfectadosBFS(origen, niveles);
        String texto = "BFS desde el punto " + origen + " → afectados: ";
        if (afectados.length == 0) {
            texto += "ninguno";
        }
        int i = 0;
        while (i < afectados.length) {
            if (i > 0) {
                texto += ", ";
            }
            texto += afectados[i];
            i++;
        }
        lblAfectados.setText(texto);
    }

    // ---------------------------------------------------------------
    // Ayudas de dibujo
    // ---------------------------------------------------------------

    @Override
    public void actualizarTodo() {
        panelCola.repaint();
        panelHeap.repaint();
        panelLista.repaint();
    }

    private Graphics2D prepararGraficos(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    // Dibuja un texto centrado dentro de un ancho dado
    private void centrarTexto(Graphics2D g2, String texto, int x, int ancho, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, x + (ancho - fm.stringWidth(texto)) / 2, y);
    }

    // Flecha con punta en los dos extremos (↔)
    private void dibujarFlechaDoble(Graphics2D g2, int x1, int x2, int y) {
        g2.setColor(UIUtils.COLOR_NAVY);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(x1, y, x2, y);
        g2.drawLine(x1, y, x1 + 6, y - 4);
        g2.drawLine(x1, y, x1 + 6, y + 4);
        g2.drawLine(x2, y, x2 - 6, y - 4);
        g2.drawLine(x2, y, x2 - 6, y + 4);
    }

    private JPanel crearFilaBotones(JButton b1, JButton b2) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        fila.add(b1);
        fila.add(b2);
        return fila;
    }
}