package es.davante.edadcanina.modelo;

import org.junit.Test;
import androidx.databinding.Observable;
import es.davante.edadcanina.BR;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/** Las reglas se pueden probar con JUnit sin iniciar Android */
public class EdadCaninaModeloTest {
    @Test public void empiezaSinCalcular() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        assertEquals("", modelo.getEdadTexto());
        assertEquals(0, modelo.getEdadHumana());
        assertEquals(Validacion.SIN_CALCULAR, modelo.getValidacion());
    }
    @Test public void seisDaCuarentaYDos() { comprobar("6", Validacion.OK, 42); }
    @Test public void minimoDaSiete() { comprobar("1", Validacion.OK, 7); }
    @Test public void maximoDaDoscientosDiez() { comprobar("30", Validacion.OK, 210); }
    @Test public void vacioDaError() { comprobar("", Validacion.VACIO, 0); }
    @Test public void espaciosDanVacio() { comprobar("   ", Validacion.VACIO, 0); }
    @Test public void nullSeTrataComoVacio() { comprobar(null, Validacion.VACIO, 0); }
    @Test public void letrasDanError() { comprobar("abc", Validacion.NO_NUMERO, 0); }
    @Test public void decimalDaError() { comprobar("3.5", Validacion.NO_NUMERO, 0); }
    @Test public void ceroDaError() { comprobar("0", Validacion.FUERA_RANGO, 0); }
    @Test public void treintaYUnoDaError() { comprobar("31", Validacion.FUERA_RANGO, 0); }
    @Test public void negativoDaError() { comprobar("-4", Validacion.FUERA_RANGO, 0); }
    @Test public void enteroEnormeNoProvocaDesbordamiento() { comprobar("99999999999", Validacion.NO_NUMERO, 0); }
    @Test public void numeroGrandeNoSeMultiplica() { comprobar("400000000", Validacion.FUERA_RANGO, 0); }
    @Test public void aceptaEspaciosAlrededor() { comprobar(" 6 ", Validacion.OK, 42); }
    @Test public void ceroInicialEsValido() { comprobar("05", Validacion.OK, 35); }
    @Test public void signoPositivoEsValido() { comprobar("+5", Validacion.OK, 35); }

    @Test public void editarInvalidaElResultadoAnterior() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        modelo.setEdadTexto("6");
        modelo.calcular();
        modelo.setEdadTexto("7");
        assertEquals(Validacion.SIN_CALCULAR, modelo.getValidacion());
        assertEquals(0, modelo.getEdadHumana());
    }

    @Test public void asignarElMismoTextoConservaElResultado() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        modelo.setEdadTexto("6");
        modelo.calcular();
        modelo.setEdadTexto(new String("6"));
        assertEquals(Validacion.OK, modelo.getValidacion());
        assertEquals(42, modelo.getEdadHumana());
    }

    @Test public void calcularAvisaDeEdadYValidacion() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        modelo.setEdadTexto("6");
        List<Integer> avisos = escuchar(modelo);
        modelo.calcular();
        assertEquals(java.util.Arrays.asList(BR.edadHumana, BR.validacion), avisos);
    }

    @Test public void repetirCalculoNoAvisaSiNadaCambia() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        modelo.setEdadTexto("6");
        modelo.calcular();
        List<Integer> avisos = escuchar(modelo);
        modelo.calcular();
        modelo.setEdadTexto("6");
        assertTrue(avisos.isEmpty());
    }

    @Test public void editarAvisaDeEntradaYCaducidad() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        modelo.setEdadTexto("6");
        modelo.calcular();
        List<Integer> avisos = escuchar(modelo);
        modelo.setEdadTexto("7");
        assertTrue(avisos.contains(BR.edadTexto));
        assertTrue(avisos.contains(BR.edadHumana));
        assertTrue(avisos.contains(BR.validacion));
        assertEquals(3, avisos.size());
    }

    @Test public void editarDespuesDeErrorLoDejaSinCalcular() {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        modelo.calcular();
        modelo.setEdadTexto("6");
        assertEquals(Validacion.SIN_CALCULAR, modelo.getValidacion());
    }

    private List<Integer> escuchar(EdadCaninaModelo modelo) {
        List<Integer> avisos = new ArrayList<>();
        modelo.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override public void onPropertyChanged(Observable origen, int propiedad) {
                avisos.add(propiedad);
            }
        });
        return avisos;
    }

    private void comprobar(String entrada, Validacion esperado, int edadEsperada) {
        EdadCaninaModelo modelo = new EdadCaninaModelo();
        modelo.setEdadTexto(entrada);
        assertEquals(esperado, modelo.calcular());
        assertEquals(esperado, modelo.getValidacion());
        assertEquals(edadEsperada, modelo.getEdadHumana());
    }
}
