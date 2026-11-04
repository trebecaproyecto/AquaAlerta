package pia.gui;

import pia.entidades.Alerta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HistorialFrame extends JFrame implements Actualizable {

    private ControladorSistema controlador;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtBuscar;
    private JLabel lblDetalle;

    public HistorialFrame(ControladorSistema controlador) {
        this.controlador = controlador;
        setTitle("AquaAlerta Mallorquín - Historial (Lista Doble)");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.COLOR_BG);

        add(UIUtils.crearEncabezado(this, "🕘", "Historial de alertas",
                "Lista doblemente enlazada", "Historial", null), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        body.add(crearBarraBusqueda(), BorderLayout.NORTH);
        body.add(crearTarjetaTabla(), BorderLayout.CENTER);
        body.add(crearTarjetaDetalle(), BorderLayout.EAST);
        add(body, BorderLayout.CENTER);

        cargarTabla();
    }

    // ---------------------------------------------------------------
    // Construcción
    // ---------------------------------------------------------------

    private JPanel crearBarraBusqueda() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.COLOR_LINE),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izquierda.setOpaque(false);
        izquierda.add(new JLabel("🔍 Buscar por id:"));
        txtBuscar = new JTextField(8);
        izquierda.add(txtBuscar);

        JButton btnBuscar = UIUtils.crearBoton("Buscar", UIUtils.COLOR_TEAL, Color.WHITE, 90, 34);
        JButton btnAnterior = UIUtils.crearBoton("◀ Anterior", Color.WHITE, UIUtils.COLOR_NAVY, 105, 34);
        JButton btnSiguiente = UIUtils.crearBoton("Siguiente ▶", Color.WHITE, UIUtils.COLOR_NAVY, 105, 34);
        izquierda.add(btnBuscar);
        izquierda.add(btnAnterior);
        izquierda.add(btnSiguiente);
        barra.add(izquierda, BorderLayout.WEST);

        JButton btnEliminar = UIUtils.crearBoton("🗑 Eliminar seleccionada", UIUtils.COLOR_CRIT, Color.WHITE, 190, 34);
        barra.add(btnEliminar, BorderLayout.EAST);

        // Eventos
        btnBuscar.addActionListener(e -> buscarAlerta());
        txtBuscar.addActionListener(e -> buscarAlerta());
        btnAnterior.addActionListener(e -> moverSeleccion(-1));
        btnSiguiente.addActionListener(e -> moverSeleccion(1));
        btnEliminar.addActionListener(e -> eliminarSeleccionada());
        return barra;
    }

    private JPanel crearTarjetaTabla() {
        JPanel tarjeta = UIUtils.crearTarjeta("🗂 Alertas registradas", "la más reciente va primero");

        String[] columnas = {"#", "Punto", "Parámetro", "Valor", "Severidad", "Hora"};
        modelo = UIUtils.crearModelo(columnas);
        tabla = new JTable(modelo);
        UIUtils.estilizarTabla(tabla);
        tabla.setDefaultRenderer(Object.class, new UIUtils.RenderSeveridad(4));
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(160);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        tarjeta.add(scroll, BorderLayout.CENTER);
        return tarjeta;
    }

    private JPanel crearTarjetaDetalle() {
        JPanel tarjeta = UIUtils.crearTarjeta("📌 Detalle de la alerta", "");
        tarjeta.setPreferredSize(new Dimension(340, 0));

        lblDetalle = new JLabel();
        lblDetalle.setFont(UIUtils.FONT_TEXT);
        lblDetalle.setForeground(UIUtils.COLOR_INK);
        lblDetalle.setVerticalAlignment(SwingConstants.TOP);
        lblDetalle.setText("<html><span style='color:#6B7C8C'>Selecciona una alerta de la tabla para ver sus detalles.</span></html>");
        tarjeta.add(lblDetalle, BorderLayout.CENTER);
        return tarjeta;
    }

    // ---------------------------------------------------------------
    // Lógica
    // ---------------------------------------------------------------

    @Override
    public void actualizarTodo() {
        cargarTabla();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        Alerta[] arr = controlador.getHistorialAlertas().obtenerArregloAlertas();
        int i = 0;
        while (i < arr.length) {
            Alerta a = arr[i];
            modelo.addRow(new Object[]{a.getId(), a.getIdPunto(), a.getParametro(),
                UIUtils.formatearValor(a.getValor()), a.getSeveridad(), a.getFechaHora()});
            i++;
        }
    }

    // Busca una alerta por id usando la lista doble y la selecciona en la tabla
    private void buscarAlerta() {
        try {
            int id = Integer.parseInt(txtBuscar.getText().trim());
            Alerta a = controlador.getHistorialAlertas().buscarPorId(id);
            if (a == null) {
                JOptionPane.showMessageDialog(this, "No existe una alerta con id " + id);
                return;
            }
            int fila = 0;
            while (fila < modelo.getRowCount()) {
                if ((Integer) modelo.getValueAt(fila, 0) == id) {
                    tabla.setRowSelectionInterval(fila, fila);
                    tabla.scrollRectToVisible(tabla.getCellRect(fila, 0, true));
                    fila = modelo.getRowCount(); // ya se encontró: se termina el while
                }
                fila++;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Escribe un id válido (número entero)");
        }
    }

    // Moverse al nodo anterior (-1) o siguiente (+1) de la lista
    private void moverSeleccion(int direccion) {
        int total = modelo.getRowCount();
        if (total == 0) {
            return;
        }
        int actual = tabla.getSelectedRow();
        int nueva;
        if (actual == -1) {
            nueva = 0;
        } else {
            nueva = actual + direccion;
        }
        if (nueva < 0 || nueva >= total) {
            return;
        }
        tabla.setRowSelectionInterval(nueva, nueva);
        tabla.scrollRectToVisible(tabla.getCellRect(nueva, 0, true));
    }

    private void eliminarSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Primero selecciona una alerta de la tabla");
            return;
        }
        int id = (Integer) modelo.getValueAt(fila, 0);
        int opcion = JOptionPane.showConfirmDialog(this, "¿Eliminar la alerta #" + id + " del historial?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            controlador.eliminarDelHistorial(id);
            cargarTabla();
            lblDetalle.setText("<html><span style='color:#6B7C8C'>Alerta #" + id + " eliminada correctamente.</span></html>");
        }
    }

    // Llena el panel de detalle con los datos de la alerta seleccionada
    private void mostrarDetalle() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            return;
        }
        int id = (Integer) modelo.getValueAt(fila, 0);
        Alerta a = controlador.getHistorialAlertas().buscarPorId(id);
        int[] info = controlador.getHistorialAlertas().obtenerPosicionYVecinos(id);
        if (a == null || info == null) {
            return;
        }

        // Vecinos del punto en el grafo
        int[] vecinos = controlador.getGrafo().obtenerAdyacencias(a.getIdPunto());
        String textoVecinos = "";
        int i = 0;
        while (i < vecinos.length) {
            if (i > 0) {
                textoVecinos += ", ";
            }
            textoVecinos += vecinos[i];
            i++;
        }
        if (textoVecinos.isEmpty()) {
            textoVecinos = "ninguno";
        }

        String colorSev = UIUtils.hex(UIUtils.colorSeveridad(a.getSeveridad()));
        String anterior = "NULL";
        if (info[1] != -1) {
            anterior = "alerta #" + info[1];
        }
        String siguiente = "NULL";
        if (info[2] != -1) {
            siguiente = "alerta #" + info[2];
        }

        String txt = "<html><div style='width:280px'>";
        txt += "<span style='font-size:15px'><b>Alerta #" + id + "</b></span><br><br>";
        txt += "<span style='color:#6B7C8C'>Punto</span><br><b>" + a.getIdPunto() + " · "
                + controlador.nombrePunto(a.getIdPunto()) + "</b><br><br>";
        txt += "<span style='color:#6B7C8C'>Parámetro</span><br><b>" + a.getParametro() + "</b><br><br>";
        txt += "<span style='color:#6B7C8C'>Valor</span><br><b>" + UIUtils.formatearValor(a.getValor()) + " "
                + ControladorSistema.unidadParametro(a.getParametro()) + "</b><br><br>";
        txt += "<span style='color:#6B7C8C'>Severidad</span><br><b><font color='" + colorSev + "'>"
                + a.getSeveridad() + "</font></b><br><br>";
        txt += "<span style='color:#6B7C8C'>Vecinos en el grafo</span><br><b>" + textoVecinos + "</b><br><br>";
        txt += "<hr>";
        txt += "<span style='color:#6B7C8C'>Posición en la lista: " + info[0] + " de "
                + controlador.getHistorialAlertas().getTamano() + "<br>";
        txt += "Anterior: " + anterior + "<br>";
        txt += "Siguiente: " + siguiente + "</span>";
        txt += "</div></html>";
        lblDetalle.setText(txt);
    }
}