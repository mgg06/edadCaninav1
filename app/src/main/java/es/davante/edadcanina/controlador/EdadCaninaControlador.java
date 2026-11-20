package es.davante.edadcanina.controlador;

import es.davante.edadcanina.modelo.EdadCaninaModelo;

/** Recibe las acciones del usuario y deja las reglas en el modelo */
public class EdadCaninaControlador {
    private final EdadCaninaModelo modelo;

    public EdadCaninaControlador(EdadCaninaModelo modelo) {
        // Comparto el mismo modelo que observa el binding, sin guardar ninguna vista
        this.modelo = modelo;
    }

    public void onCalcularClick() {
        modelo.calcular();
    }

    public void onLimpiarClick() {
        modelo.limpiar();
    }
}
