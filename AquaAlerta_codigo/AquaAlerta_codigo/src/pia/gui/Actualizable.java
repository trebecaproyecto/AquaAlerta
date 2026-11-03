package pia.gui;

/**
 * Las ventanas que implementan esto se pueden refrescar desde afuera
 * (por ejemplo, cuando se cierra el simulador).
 */
public interface Actualizable {
    void actualizarTodo();
}