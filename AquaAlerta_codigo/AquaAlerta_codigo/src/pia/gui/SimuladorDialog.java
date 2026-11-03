package pia.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimuladorDialog extends JDialog {

    private ControladorSistema controlador;
    private JFrame padre;

    // 0 = lluvia, 1 = vertimiento, 2 = lectura manual
    private int tipoSeleccionado = 0;
    private JPanel[] tarjetasTipo = new JPanel[3];

    private JComboBox<String> cmbPunto;
    private JSlider sldIntensidad;
    private JLabel lblIntensidad;
    private JSpinner spnNiveles;
    private JComboBox<String> cmbParametro;
    private JTextField txtValor;

    private JLabel lblResAlertas;
    private JLabel lblResAfectados;
    private JLabel lblResLecturas;

    public SimuladorDialog(JFrame padre, ControladorSistema controlador) {
        super(padre, "AquaAlerta Mallorquín - Simulador de eventos", true);
        this.controlador = controlador;
        this.padre = padre;
        setSize(1050, 640);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());

        add(UIUtils.crearEncabezado(null, "⚡", "Simulador de eventos",
                "Elige un evento y mira cómo reacciona el sistema", null, null), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(UIUtils.COLOR_BG);
        body.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ---------- Fila de tarjetas ----------
        JPanel fila = new JPanel(new GridLayout(1, 4, 15, 0));
        fila.setOpaque(false);

        tarjetasTipo[0] = crearTarjetaTipo("🌧", "Lluvia fuerte",
                "Arrastra residuos a la ciénaga. Baja el oxígeno disuelto en el punto y en sus vecinos.",
                "Afecta varios puntos", UIUtils.COLOR_WARN, UIUtils.COLOR_TEXTO_WARN, 0);
        tarjetasTipo[1] = crearTarjetaTipo("🛢", "Vertimiento",
                "Descarga de aguas residuales en un punto. Sube mucho los coliformes y se propaga a los vecinos.",
                "Afecta 1 punto + vecinos", UIUtils.COLOR_CRIT, Color.WHITE, 1);
        tarjetasTipo[2] = crearTarjetaTipo("🎚", "Lectura manual",
                "Escribe tú el valor de un parámetro en un punto para probar los umbrales.",
                "Personalizada", UIUtils.COLOR_OK, Color.WHITE, 2);
        fila.add(tarjetasTipo[0]);
        fila.add(tarjetasTipo[1]);
        fila.add(tarjetasTipo[2]);
        fila.add(crearTarjetaConfiguracion());
        body.add(fila, BorderLayout.CENTER);

        body.add(crearTarjetaResultado(), BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);

        seleccionarTipo(0);
    }

    // ---------------------------------------------------------------
    // Tarjetas
    // ---------------------------------------------------------------

    private JPanel crearTarjetaTipo(String emoji, String titulo, String descripcion,
                                    String insignia, Color colorInsignia, Color textoInsignia, final int indice) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblEmoji = new JLabel(emoji);
        lblEmoji.setFont(UIUtils.FONT_EMOJI);
        lblEmoji.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitulo.setForeground(UIUtils.COLOR_NAVY);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><div style='width:190px'>" + descripcion + "</div></html>");
        lblDesc.setFont(UIUtils.FONT_TEXT);
        lblDesc.setForeground(UIUtils.COLOR_MUTED);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInsignia = UIUtils.crearInsignia(insignia, colorInsignia, textoInsignia);
        lblInsignia.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(lblEmoji);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(lblDesc);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(lblInsignia);

        // Al hacer clic en la tarjeta se selecciona ese tipo de evento
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarTipo(indice);
            }
        });
        return tarjeta;
    }

    private JPanel crearTarjetaConfiguracion() {
        JPanel tarjeta = UIUtils.crearTarjeta("⚙ Configuración", "");

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 3));
        form.setOpaque(false);

        form.add(UIUtils.crearEtiqueta("Punto de origen"));
        cmbPunto = new JComboBox<>(new String[]{"1 · Desembocadura", "2 · Manglar norte", "3 · Centro",
            "4 · Arroyo León", "5 · Las Flores"});
        cmbPunto.setSelectedIndex(2);
        form.add(cmbPunto);

        lblIntensidad = UIUtils.crearEtiqueta("Intensidad: 60");
        form.add(lblIntensidad);
        sldIntensidad = new JSlider(0, 100, 60);
        sldIntensidad.setOpaque(false);
        sldIntensidad.addChangeListener(e -> lblIntensidad.setText("Intensidad: " + sldIntensidad.getValue()));
        form.add(sldIntensidad);

        form.add(UIUtils.crearEtiqueta("Niveles de propagación (BFS)"));
        spnNiveles = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
        form.add(spnNiveles);

        form.add(UIUtils.crearEtiqueta("Parámetro (solo lectura manual)"));
        cmbParametro = new JComboBox<>(new String[]{"Oxígeno disuelto", "Coliformes", "Salinidad"});
        form.add(cmbParametro);

        form.add(UIUtils.crearEtiqueta("Valor (solo lectura manual)"));
        txtValor = new JTextField("2.1");
        form.add(txtValor);
        tarjeta.add(form, BorderLayout.CENTER);

        JButton btnLanzar = UIUtils.crearBoton("▶ Lanzar evento", UIUtils.COLOR_CRIT, Color.WHITE, 100, 40);
        btnLanzar.addActionListener(e -> lanzarEvento());
        tarjeta.add(btnLanzar, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel crearTarjetaResultado() {
        JPanel tarjeta = UIUtils.crearTarjeta("📊 Vista previa del resultado", "Se actualiza al lanzar el evento");

        lblResAlertas = crearNumeroGrande(UIUtils.COLOR_CRIT);
        lblResAfectados = crearNumeroGrande(UIUtils.COLOR_AFF);
        lblResLecturas = crearNumeroGrande(UIUtils.COLOR_TEAL);

        JPanel numeros = new JPanel(new FlowLayout(FlowLayout.LEFT, 35, 0));
        numeros.setOpaque(false);
        numeros.add(crearBloqueNumero(lblResAlertas, "alertas generadas"));
        numeros.add(crearBloqueNumero(lblResAfectados, "puntos posiblemente afectados"));
        numeros.add(crearBloqueNumero(lblResLecturas, "lecturas nuevas en el búfer"));

        JButton btnCancelar = UIUtils.crearBoton("Cerrar", Color.WHITE, UIUtils.COLOR_NAVY, 100, 38);
        JButton btnVer = UIUtils.crearBoton("Ver en el panel →", UIUtils.COLOR_TEAL, Color.WHITE, 160, 38);
        btnCancelar.addActionListener(e -> dispose());
        btnVer.addActionListener(e -> {
            dispose();
            // Si veníamos de otra ventana, se abre el panel principal
            if (!(padre instanceof PanelPrincipalFrame)) {
                Main.mostrarPanelPrincipal(padre);
            }
        });
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnVer);

        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.add(numeros, BorderLayout.WEST);
        fila.add(botones, BorderLayout.EAST);
        tarjeta.add(fila, BorderLayout.CENTER);
        return tarjeta;
    }

    private JLabel crearNumeroGrande(Color color) {
        JLabel lbl = new JLabel("0");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lbl.setForeground(color);
        return lbl;
    }

    private JPanel crearBloqueNumero(JLabel numero, String texto) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lbl = UIUtils.crearEtiqueta(texto);
        numero.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(numero);
        p.add(lbl);
        return p;
    }

    // ---------------------------------------------------------------
    // Lógica
    // ---------------------------------------------------------------

    // Resalta la tarjeta elegida y activa/desactiva los campos de lectura manual
    private void seleccionarTipo(int indice) {
        tipoSeleccionado = indice;
        int i = 0;
        while (i < tarjetasTipo.length) {
            if (i == indice) {
                tarjetasTipo[i].setBackground(new Color(241, 251, 250));
                tarjetasTipo[i].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIUtils.COLOR_TEAL, 3),
                        BorderFactory.createEmptyBorder(12, 14, 12, 14)));
            } else {
                tarjetasTipo[i].setBackground(Color.WHITE);
                tarjetasTipo[i].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIUtils.COLOR_LINE, 1),
                        BorderFactory.createEmptyBorder(14, 16, 14, 16)));
            }
            i++;
        }
        boolean manual = (indice == 2);
        cmbParametro.setEnabled(manual);
        txtValor.setEnabled(manual);
        sldIntensidad.setEnabled(!manual);
        spnNiveles.setEnabled(!manual);
    }

    private void lanzarEvento() {
        int idPunto = cmbPunto.getSelectedIndex() + 1;
        int intensidad = sldIntensidad.getValue();
        int niveles = ((Integer) spnNiveles.getValue()).intValue();
        int[] resultado;

        if (tipoSeleccionado == 0) {
            resultado = controlador.simularLluvia(idPunto, intensidad, niveles);
        } else if (tipoSeleccionado == 1) {
            resultado = controlador.simularVertimiento(idPunto, intensidad, niveles);
        } else {
            try {
                double valor = Double.parseDouble(txtValor.getText().trim());
                if (valor < 0) {
                    JOptionPane.showMessageDialog(this, "El valor no puede ser negativo");
                    return;
                }
                resultado = controlador.enviarLecturaManual(idPunto, (String) cmbParametro.getSelectedItem(), valor);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Escribe un número válido (ej. 2.1)");
                return;
            }
        }

        // resultado = {lecturas, alertas, afectados}
        lblResLecturas.setText(String.valueOf(resultado[0]));
        lblResAlertas.setText(String.valueOf(resultado[1]));
        lblResAfectados.setText(String.valueOf(resultado[2]));
    }
}