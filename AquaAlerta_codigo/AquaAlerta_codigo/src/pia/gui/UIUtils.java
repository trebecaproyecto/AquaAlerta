package pia.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Utilidades visuales: colores, fuentes y componentes que se repiten en todas las ventanas.
 */
public class UIUtils {

    // Paleta de colores (la misma del boceto en HTML)
    public static final Color COLOR_NAVY = new Color(11, 37, 69);
    public static final Color COLOR_NAVY2 = new Color(19, 49, 92);
    public static final Color COLOR_TEAL = new Color(19, 168, 158);
    public static final Color COLOR_AQUA = new Color(141, 219, 224);
    public static final Color COLOR_MIST = new Color(234, 244, 246);
    public static final Color COLOR_BG = new Color(243, 247, 249);
    public static final Color COLOR_CARD = new Color(255, 255, 255);
    public static final Color COLOR_LINE = new Color(220, 230, 235);
    public static final Color COLOR_INK = new Color(27, 42, 58);
    public static final Color COLOR_MUTED = new Color(107, 124, 140);
    public static final Color COLOR_OK = new Color(46, 204, 113);
    public static final Color COLOR_WARN = new Color(244, 185, 66);
    public static final Color COLOR_CRIT = new Color(229, 72, 77);
    public static final Color COLOR_AFF = new Color(242, 140, 56);
    public static final Color COLOR_TEXTO_WARN = new Color(59, 45, 0);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_EMOJI = new Font("Segoe UI Emoji", Font.PLAIN, 40);

    // ---------------------------------------------------------------
    // Funciones pequeñas de ayuda
    // ---------------------------------------------------------------

    // Color según la severidad de una alerta
    public static Color colorSeveridad(String severidad) {
        if ("CRITICA".equals(severidad)) {
            return COLOR_CRIT;
        }
        if ("MEDIA".equals(severidad)) {
            return COLOR_WARN;
        }
        return COLOR_OK;
    }

    // Color del texto que va encima del color de la severidad
    public static Color textoSeveridad(String severidad) {
        if ("MEDIA".equals(severidad)) {
            return COLOR_TEXTO_WARN;
        }
        return Color.WHITE;
    }

    // Convierte un Color a texto hexadecimal para usarlo dentro de HTML
    public static String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // Muestra 9000.0 como 9000 y 2.5 como 2.5
    public static String formatearValor(double valor) {
        if (valor == Math.floor(valor)) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }

    // ---------------------------------------------------------------
    // Botones
    // ---------------------------------------------------------------

    public static JButton crearBoton(String texto, Color fondo, Color textoColor) {
        return crearBoton(texto, fondo, textoColor, 120, 35);
    }

    public static JButton crearBoton(final String texto, final Color fondo, final Color textoColor, int ancho, int alto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                // Se oscurece un poco cuando se presiona
                if (getModel().isPressed()) {
                    g2.setColor(fondo.darker());
                } else {
                    g2.setColor(fondo);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(textoColor);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(ancho, alto));
        return btn;
    }

    // ---------------------------------------------------------------
    // Paneles y componentes con dibujo propio
    // ---------------------------------------------------------------

    // Panel con fondo en degradado
    public static class PanelDegradado extends JPanel {
        private Color color1;
        private Color color2;
        private boolean diagonal;

        public PanelDegradado(Color color1, Color color2, boolean diagonal) {
            this.color1 = color1;
            this.color2 = color2;
            this.diagonal = diagonal;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp;
            if (diagonal) {
                gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
            } else {
                gp = new GradientPaint(0, 0, color1, getWidth(), 0, color2);
            }
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Etiqueta redondeada (para el estado del encabezado)
    public static class Pildora extends JLabel {
        public Pildora(String texto) {
            super(texto);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(FONT_TEXT);
            setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 35));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Insignia de color (ej. CRÍTICA, MEDIA, BAJA)
    public static JLabel crearInsignia(String texto, final Color fondo, Color textoColor) {
        JLabel lbl = new JLabel(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setOpaque(false);
        lbl.setForeground(textoColor);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return lbl;
    }

    // Item de la leyenda del mapa: circulo de color + texto
    public static JLabel crearItemLeyenda(final Color color, String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(COLOR_MUTED);
        lbl.setIconTextGap(5);
        lbl.setIcon(new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(x, y, 10, 10);
                g2.dispose();
            }
            public int getIconWidth() { return 10; }
            public int getIconHeight() { return 10; }
        });
        return lbl;
    }

    // Leyenda completa del mapa
    public static JPanel crearLeyenda() {
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        leyenda.setOpaque(false);
        leyenda.add(crearItemLeyenda(COLOR_OK, "Normal"));
        leyenda.add(crearItemLeyenda(COLOR_WARN, "Precaución"));
        leyenda.add(crearItemLeyenda(COLOR_CRIT, "Crítico"));
        leyenda.add(crearItemLeyenda(COLOR_AFF, "Posiblemente afectado (BFS)"));
        return leyenda;
    }

    // ---------------------------------------------------------------
    // Encabezado (barra azul de arriba) con la navegación
    // ---------------------------------------------------------------

    /**
     * Crea el encabezado de las ventanas.
     * - ventana: la ventana donde va (si es null no se ponen los botones de navegación)
     * - activa: nombre del botón que se resalta ("Panel", "Historial", "Estructuras")
     * - pildora: etiqueta de estado a la derecha (puede ser null)
     */
    public static JPanel crearEncabezado(final JFrame ventana, String icono, String titulo,
                                         String subtitulo, String activa, JLabel pildora) {
        PanelDegradado encabezado = new PanelDegradado(COLOR_NAVY, COLOR_NAVY2, false);
        encabezado.setLayout(new BorderLayout());
        encabezado.setPreferredSize(new Dimension(100, 70));
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 17));
        izquierda.setOpaque(false);

        // Logo circular
        final String iconoTexto = icono;
        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, COLOR_AQUA, getWidth(), getHeight(), COLOR_TEAL));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(iconoTexto, (getWidth() - fm.stringWidth(iconoTexto)) / 2,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            }
        };
        logo.setOpaque(false);
        logo.setPreferredSize(new Dimension(36, 36));
        izquierda.add(logo);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(COLOR_AQUA);
        textos.add(lblTitulo);
        textos.add(lblSub);
        izquierda.add(textos);

        // Botones de navegación
        if (ventana != null) {
            izquierda.add(Box.createHorizontalStrut(20));
            String[] nombres = {"Panel", "Simulador", "Historial", "Estructuras"};
            int i = 0;
            while (i < nombres.length) {
                final String nombre = nombres[i];
                Color fondo = new Color(255, 255, 255, 30);
                if (nombre.equals(activa)) {
                    fondo = COLOR_TEAL;
                }
                JButton btn = crearBoton(nombre, fondo, Color.WHITE, 105, 34);
                btn.addActionListener(e -> {
                    if (nombre.equals("Panel")) {
                        Main.mostrarPanelPrincipal(ventana);
                    } else if (nombre.equals("Simulador")) {
                        Main.mostrarSimulador(ventana);
                    } else if (nombre.equals("Historial")) {
                        Main.mostrarHistorial(ventana);
                    } else {
                        Main.mostrarEstructuras(ventana);
                    }
                });
                izquierda.add(btn);
                i++;
            }
        }
        encabezado.add(izquierda, BorderLayout.WEST);

        if (pildora != null) {
            JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 16));
            derecha.setOpaque(false);
            derecha.add(pildora);
            encabezado.add(derecha, BorderLayout.EAST);
        }
        return encabezado;
    }

    // ---------------------------------------------------------------
    // Tarjetas (paneles blancos con título)
    // ---------------------------------------------------------------

    public static JPanel crearTarjeta(String titulo, String etiqueta) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(COLOR_CARD);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_LINE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(COLOR_NAVY);
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(FONT_SMALL);
        lblEtiqueta.setForeground(COLOR_MUTED);
        cabecera.add(lblTitulo, BorderLayout.WEST);
        cabecera.add(lblEtiqueta, BorderLayout.EAST);
        tarjeta.add(cabecera, BorderLayout.NORTH);
        return tarjeta;
    }

    // Etiqueta pequeña gris para los campos de formularios
    public static JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(COLOR_MUTED);
        return lbl;
    }

    // ---------------------------------------------------------------
    // Tablas
    // ---------------------------------------------------------------

    // Modelo de tabla que no deja editar las celdas
    public static DefaultTableModel crearModelo(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    // Le da el estilo del boceto a una JTable
    public static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(30);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFont(FONT_TEXT);
        tabla.setForeground(COLOR_INK);
        tabla.setSelectionBackground(COLOR_MIST);
        tabla.setSelectionForeground(COLOR_INK);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(FONT_BOLD);
        tabla.getTableHeader().setForeground(COLOR_MUTED);
        tabla.getTableHeader().setBackground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);
    }

    // Renderer que pinta de color la celda de severidad
    public static class RenderSeveridad extends DefaultTableCellRenderer {
        private int columnaSeveridad;

        public RenderSeveridad(int columnaSeveridad) {
            this.columnaSeveridad = columnaSeveridad;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            if (column == columnaSeveridad) {
                String sev = String.valueOf(value);
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                c.setBackground(colorSeveridad(sev));
                c.setForeground(textoSeveridad(sev));
            } else {
                setHorizontalAlignment(LEFT);
                setFont(FONT_TEXT);
                if (isSelected) {
                    c.setBackground(COLOR_MIST);
                } else {
                    c.setBackground(Color.WHITE);
                }
                c.setForeground(COLOR_INK);
            }
            return c;
        }
    }
}