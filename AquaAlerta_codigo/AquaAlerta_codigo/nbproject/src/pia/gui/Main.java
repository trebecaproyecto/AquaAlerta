package pia.gui;

import javax.swing.*;

public class Main {

    private static ControladorSistema controlador;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Un único controlador para toda la aplicación
                controlador = new ControladorSistema();

                // Abrir ventana inicial
                mostrarInicio(null);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error al iniciar el sistema: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void mostrarInicio(JFrame ventanaActual) {
        cambiarVentana(ventanaActual, new InicioFrame(controlador));
    }

    public static void mostrarPanelPrincipal(JFrame ventanaActual) {
        cambiarVentana(ventanaActual, new PanelPrincipalFrame(controlador));
    }

    public static void mostrarHistorial(JFrame ventanaActual) {
        cambiarVentana(ventanaActual, new HistorialFrame(controlador));
    }

    public static void mostrarEstructuras(JFrame ventanaActual) {
        cambiarVentana(ventanaActual, new EstructurasFrame(controlador));
    }

    // Abre el simulador encima de la ventana actual (sin cerrarla)
    public static void mostrarSimulador(JFrame ventanaPadre) {
        SimuladorDialog simulador = new SimuladorDialog(ventanaPadre, controlador);
        simulador.setVisible(true);

        // Cuando se cierra el simulador, se refresca la ventana de atrás
        if (ventanaPadre instanceof Actualizable) {
            ((Actualizable) ventanaPadre).actualizarTodo();
        }
    }

    // Método central para cambiar de ventana
    private static void cambiarVentana(JFrame ventanaActual, JFrame nuevaVentana) {
        nuevaVentana.setVisible(true);
        if (ventanaActual != null) {
            ventanaActual.dispose();
        }
    }

    public static ControladorSistema getControlador() {
        return controlador;
    }
}