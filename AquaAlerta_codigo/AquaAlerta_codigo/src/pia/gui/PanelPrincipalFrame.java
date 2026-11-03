package pia.gui;

import pia.entidades.Alerta;
import pia.estructuras.ColaCircularLecturas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelPrincipalFrame extends JFrame implements Actualizable {

    private ControladorSistema controlador;
    private MapaPanel mapaPanel;
    private JTable tablaAlertas;
    private DefaultTableModel modeloAlertas;
    private JPanel buferPanel;
    private JTextArea logArea;
    private UIUtils.Pildora lblEstado;

    private JComboBox<String> cmbPunto;
    private JComboBox<String> cmbParametro;
    private JTextField txtValor;

    public PanelPrincipalFrame(ControladorSistema controlador) {
        this.controlador = controlador;
        setTitle("AquaAlerta Mallorquín - Panel Principal");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.COLOR_BG);

        // ---------- Encabezado con el estado general ----------
        lblEstado = new UIUtils.Pildora("");
        add(UIUtils.crearEncabezado(this, "💧", "AquaAlerta Mallorquín",
                "Monitoreo en tiempo casi real", "Panel", lblEstado), BorderLayout.NORTH);

        // ---------- Cuerpo: izquierda (mapa + alertas) y derecha (simulación + búfer) ----------
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel izquierda = new JPanel(new GridLayout(2, 1, 0, 15));
        izquierda.setOpaque(false);
        izquierda.add(crearTarjetaMapa());
        izquierda.add(crearTarjetaAlertas());
        body.add(izquierda, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new BorderLayout(0, 15));
        derecha.setOpaque(false);
        derecha.setPreferredSize(new Dimension(400, 0));
        derecha.add(crearTarjetaSimulacion(), BorderLayout.NORTH);
        derecha.add(crearTarjetaBufer(), BorderLayout.CENTER);
        body.add(derecha, BorderLayout.EAST);

        add(body, BorderLayout.CENTER);

        actualizarTodo();
    }

    // ---------------------------------------------------------------
    // Construcción de las tarjetas
    // ---------------------------------------------------------------

    private JPanel crearTarjetaMapa() {
        JPanel tarjeta = UIUtils.crearTarjeta("🗺 Mapa de puntos de monitoreo", "Grafo · BFS");
        mapaPanel = new MapaPanel(controlador);
        tarjeta.add(mapaPanel, BorderLayout.CENTER);
        tarjeta.add(UIUtils.crearLeyenda(), BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel crearTarjetaAlertas() {
        JPanel tarjeta = UIUtils.crearTarjeta("🚨 Alertas pendientes", "Cola de prioridad");

        String[] columnas = {"#", "Punto", "Parámetro", "Valor", "Severidad"};
        modeloAlertas = UIUtils.crearModelo(columnas);
        tablaAlertas = new JTable(modeloAlertas);
        UIUtils.estilizarTabla(tablaAlertas);
        tablaAlertas.setDefaultRenderer(Object.class, new UIUtils.RenderSeveridad(4));
        tablaAlertas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaAlertas.getColumnModel().getColumn(1).setPreferredWidth(50);
        tablaAlertas.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane scroll = new JScrollPane(tablaAlertas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        tarjeta.add(scroll, BorderLayout.CENTER);

        JButton btnAtender = UIUtils.crearBoton("✔ Atender siguiente", UIUtils.COLOR_TEAL, Color.WHITE, 170, 36);
        btnAtender.addActionListener(e -> {
            Alerta extraida = controlador.atenderSiguiente();
            if (extraida == null) {
                JOptionPane.showMessageDialog(this, "No hay alertas pendientes");
            }
            actualizarTodo();
        });
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pBtn.setOpaque(false);
        pBtn.add(btnAtender);
        tarjeta.add(pBtn, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel crearTarjetaSimulacion() {
        JPanel tarjeta = UIUtils.crearTarjeta("⚡ Simulación de eventos", "");

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 5));
        form.setOpaque(false);

        // Botones rápidos
        JButton btnLluvia = UIUtils.crearBoton("🌧 Lluvia fuerte", UIUtils.COLOR_TEAL, Color.WHITE, 100, 38);
        JButton btnVertimiento = UIUtils.crearBoton("🛢 Vertimiento", UIUtils.COLOR_CRIT, Color.WHITE, 100, 38);
        JPanel filaRapida = new JPanel(new GridLayout(1, 2, 10, 0));
        filaRapida.setOpaque(false);
        filaRapida.add(btnLluvia);
        filaRapida.add(btnVertimiento);
        form.add(filaRapida);

        form.add(UIUtils.crearEtiqueta("Punto de monitoreo"));
        cmbPunto = new JComboBox<>(new String[]{"1 · Desembocadura", "2 · Manglar norte", "3 · Centro",
            "4 · Arroyo León", "5 · Las Flores"});
        cmbPunto.setSelectedIndex(2);
        form.add(cmbPunto);

        form.add(UIUtils.crearEtiqueta("Parámetro"));
        cmbParametro = new JComboBox<>(new String[]{"Oxígeno disuelto", "Coliformes", "Salinidad"});
        form.add(cmbParametro);

        form.add(UIUtils.crearEtiqueta("Valor de la lectura"));
        txtValor = new JTextField("2.1");
        form.add(txtValor);

        JButton btnEnviar = UIUtils.crearBoton("Enviar lectura", UIUtils.COLOR_NAVY, Color.WHITE, 100, 38);
        form.add(btnEnviar);
        tarjeta.add(form, BorderLayout.CENTER);

        // Al cambiar el parámetro se pone un valor de ejemplo
        cmbParametro.addActionListener(e -> {
            String par = (String) cmbParametro.getSelectedItem();
            if ("Oxígeno disuelto".equals(par)) {
                txtValor.setText("2.1");
            } else if ("Coliformes".equals(par)) {
                txtValor.setText("5200");
            } else {
                txtValor.setText("38");
            }
        });

        btnLluvia.addActionListener(e -> {
            controlador.simularLluvia(cmbPunto.getSelectedIndex() + 1, 80, 1);
            actualizarTodo();
        });

        btnVertimiento.addActionListener(e -> {
            controlador.simularVertimiento(cmbPunto.getSelectedIndex() + 1, 75, 1);
            actualizarTodo();
        });

        btnEnviar.addActionListener(e -> {
            try {
                double valor = Double.parseDouble(txtValor.getText().trim());
                if (valor < 0) {
                    JOptionPane.showMessageDialog(this, "El valor no puede ser negativo");
                    return;
                }
                controlador.enviarLecturaManual(cmbPunto.getSelectedIndex() + 1,
                        (String) cmbParametro.getSelectedItem(), valor);
                actualizarTodo();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Escribe un número válido (ej. 2.1)");
            }
        });
        return tarjeta;
    }

    private JPanel crearTarjetaBufer() {
        JPanel tarjeta = UIUtils.crearTarjeta("📥 Búfer de lecturas", "Cola circular");

        // Barra de llenado del búfer
        buferPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ColaCircularLecturas cola = controlador.getColaLecturas();
                int tam = cola.getTamano();
                int cap = cola.getCapacidad();
                int ancho = getWidth();
                g2.setColor(new Color(227, 236, 240));
                g2.fillRoundRect(0, 2, ancho, 10, 10, 10);
                if (cap > 0) {
                    int w = (int) (((double) tam / cap) * ancho);
                    g2.setColor(UIUtils.COLOR_TEAL);
                    g2.fillRoundRect(0, 2, w, 10, 10, 10);
                }
                g2.setColor(UIUtils.COLOR_MUTED);
                g2.setFont(UIUtils.FONT_SMALL);
                g2.drawString("Lecturas en memoria: " + tam + " / " + cap, 0, 30);
            }
        };
        buferPanel.setOpaque(false);
        buferPanel.setPreferredSize(new Dimension(100, 38));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(UIUtils.COLOR_NAVY);
        logArea.setForeground(new Color(191, 233, 236));
        logArea.setMargin(new Insets(8, 10, 8, 10));
        JScrollPane scrollLog = new JScrollPane(logArea);
        scrollLog.setBorder(BorderFactory.createEmptyBorder());

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(buferPanel, BorderLayout.NORTH);
        centro.add(scrollLog, BorderLayout.CENTER);
        tarjeta.add(centro, BorderLayout.CENTER);
        return tarjeta;
    }

    // ---------------------------------------------------------------
    // Actualización de la pantalla
    // ---------------------------------------------------------------

    @Override
    public void actualizarTodo() {
        mapaPanel.repaint();
        buferPanel.repaint();

        // Tabla de alertas pendientes (ordenadas por severidad)
        modeloAlertas.setRowCount(0);
        Alerta[] alertas = controlador.obtenerAlertasOrdenadas();
        int i = 0;
        while (i < alertas.length) {
            Alerta a = alertas[i];
            modeloAlertas.addRow(new Object[]{a.getId(), a.getIdPunto(), a.getParametro(),
                UIUtils.formatearValor(a.getValor()), a.getSeveridad()});
            i++;
        }

        // Registro de actividad
        logArea.setText(controlador.getRegistro());
        logArea.setCaretPosition(logArea.getDocument().getLength());

        // Estado general en el encabezado
        String estado = controlador.getEstadoGeneral();
        Color color = UIUtils.COLOR_OK;
        if (estado.equals("Crítico")) {
            color = UIUtils.COLOR_CRIT;
        } else if (estado.equals("Precaución")) {
            color = UIUtils.COLOR_WARN;
        }
        lblEstado.setText("<html><font color='" + UIUtils.hex(color) + "'>●</font>&nbsp; Estado: " + estado + "</html>");
    }
}