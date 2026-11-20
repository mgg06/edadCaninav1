package es.davante.edadcanina.controlador;

import org.junit.Test;
import es.davante.edadcanina.modelo.EdadCaninaModelo;
import es.davante.edadcanina.modelo.Validacion;
import static org.junit.Assert.*;

/** Simulo las acciones sin necesitar botones ni una Activity */
public class EdadCaninaControladorTest {
    @Test public void calcularDaLaOrdenAlModelo() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        EdadCaninaControlador controlador = new EdadCaninaControlador(modelo);
        modelo.setEdadTexto("6");
        controlador.onCalcularClick();
        assertEquals(42, modelo.getEdadHumana());
        assertEquals(Validacion.OK, modelo.getValidacion());
    }

    @Test public void calcularVacioConservaElErrorDelModelo() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        new EdadCaninaControlador(modelo).onCalcularClick();
        assertEquals(Validacion.VACIO, modelo.getValidacion());
    }

    @Test public void calcularFueraDeRangoNoMuestraUnResultado() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        modelo.setEdadTexto("31");
        new EdadCaninaControlador(modelo).onCalcularClick();
        assertEquals(Validacion.FUERA_RANGO, modelo.getValidacion());
        assertEquals(0, modelo.getEdadHumana());
    }

    @Test public void limpiarBorraEntradaYResultado() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        EdadCaninaControlador controlador = new EdadCaninaControlador(modelo);
        modelo.setEdadTexto("6");
        controlador.onCalcularClick();
        controlador.onLimpiarClick();
        assertEquals("", modelo.getEdadTexto());
        assertEquals(0, modelo.getEdadHumana());
        assertEquals(Validacion.SIN_CALCULAR, modelo.getValidacion());
    }

    @Test public void limpiarQuitaUnErrorAunqueElTextoYaEsteVacio() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        EdadCaninaControlador controlador = new EdadCaninaControlador(modelo);
        controlador.onCalcularClick();
        controlador.onLimpiarClick();
        assertEquals("", modelo.getEdadTexto());
        assertEquals(Validacion.SIN_CALCULAR, modelo.getValidacion());
    }
}
