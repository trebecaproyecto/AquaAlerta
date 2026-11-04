package pia.gui;

import javax.swing.*;
import java.awt.*;

public class InicioFrame extends JFrame {

    private ControladorSistema controlador;

    public InicioFrame(ControladorSistema controlador) {
        this.controlador = controlador;
        setTitle("AquaAlerta Mallorquín - Inicio");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Fondo con degradado diagonal
        UIUtils.PanelDegradado fondo = new UIUtils.PanelDegradado(UIUtils.COLOR_NAVY, UIUtils.COLOR_TEAL, true);
        fondo.setLayout(new GridLayout(1, 2));

        // ---------- Lado izquierdo: textos y botones ----------
        JPanel izquierda = new JPanel();
        izquierda.setOpaque(false);
        izquierda.setLayout(new BoxLayout(izquierda, BoxLayout.Y_AXIS));
        izquierda.setBorder(BorderFactory.createEmptyBorder(60, 60, 40, 20));

        JLabel lblIcono = new JLabel("💧");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 44));
        lblIcono.setForeground(Color.WHITE);
        lblIcono.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitulo = new JLabel("<html>AquaAlerta<br>Mallorquín</html>");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 44));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><div style='width:360px'>Sistema de alerta temprana para la calidad del agua de la "
                + "Ciénaga de Mallorquín. ODS 14: Vida submarina.</div></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDesc.setForeground(UIUtils.COLOR_AQUA);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnIniciar = UIUtils.crearBoton("▶ Iniciar monitoreo", UIUtils.COLOR_TEAL, Color.WHITE, 190, 46);
        JButton btnHistorial = UIUtils.crearBoton("Historial", new Color(255, 255, 255, 45), Color.WHITE, 130, 46);

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaBotones.setOpaque(false);
        filaBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaBotones.setMaximumSize(new Dimension(400, 46));
        filaBotones.add(btnIniciar);
        filaBotones.add(Box.createHorizontalStrut(12));
        filaBotones.add(btnHistorial);

        JLabel lblPie = new JLabel("<html><div style='width:380px'>Proyecto Integrador 2026-2 · Estructura de Datos y Base de Datos · "
                + "Universidad Simón Bolívar</div></html>");
        lblPie.setFont(UIUtils.FONT_SMALL);
        lblPie.setForeground(new Color(159, 195, 207));
        lblPie.setAlignmentX(Component.LEFT_ALIGNMENT);

        izquierda.add(lblIcono);
        izquierda.add(Box.createVerticalStrut(8));
        izquierda.add(lblTitulo);
        izquierda.add(Box.createVerticalStrut(16));
        izquierda.add(lblDesc);
        izquierda.add(Box.createVerticalStrut(28));
        izquierda.add(filaBotones);
        izquierda.add(Box.createVerticalGlue());
        izquierda.add(lblPie);

        // ---------- Lado derecho: dibujo decorativo del grafo ----------
        JPanel derecha = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int lado = Math.min(w, h) - 40;
                int cx = w / 2;
                int cy = h / 2;

                // Círculos de fondo
                g2.setColor(new Color(141, 219, 224, 28));
                g2.fillOval(cx - lado / 2, cy - lado / 2, lado, lado);
                g2.setColor(new Color(141, 219, 224, 32));
                g2.fillOval(cx - lado / 3, cy - lado / 3, lado * 2 / 3, lado * 2 / 3);

                // Posiciones de los nodos (fracciones del cuadrado)
                double[][] pos = {{0.21, 0.60}, {0.40, 0.40}, {0.62, 0.50}, {0.79, 0.33}, {0.71, 0.71}};
                int[] px = new int[5];
                int[] py = new int[5];
                int i = 0;
                while (i < 5) {
                    px[i] = cx - lado / 2 + (int) (pos[i][0] * lado);
                    py[i] = cy - lado / 2 + (int) (pos[i][1] * lado);
                    i++;
                }

                // Líneas
                g2.setColor(new Color(141, 219, 224, 180));
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(px[0], py[0], px[1], py[1]);
                g2.drawLine(px[1], py[1], px[2], py[2]);
                g2.drawLine(px[2], py[2], px[3], py[3]);
                g2.drawLine(px[2], py[2], px[4], py[4]);

                // Nodos
                Color[] colores = {UIUtils.COLOR_OK, UIUtils.COLOR_OK, UIUtils.COLOR_WARN, UIUtils.COLOR_OK, UIUtils.COLOR_CRIT};
                i = 0;
                while (i < 5) {
                    int r = 16;
                    if (i == 2) {
                        r = 20;
                    }
                    g2.setColor(colores[i]);
                    g2.fillOval(px[i] - r, py[i] - r, r * 2, r * 2);
                    i++;
                }
                // Anillo alrededor del punto crítico
                g2.setColor(new Color(229, 72, 77, 130));
                g2.drawOval(px[4] - 30, py[4] - 30, 60, 60);
            }
        };
        derecha.setOpaque(false);

        fondo.add(izquierda);
        fondo.add(derecha);
        add(fondo);

        // Eventos
        btnIniciar.addActionListener(e -> Main.mostrarPanelPrincipal(this));
        btnHistorial.addActionListener(e -> Main.mostrarHistorial(this));
    }
}