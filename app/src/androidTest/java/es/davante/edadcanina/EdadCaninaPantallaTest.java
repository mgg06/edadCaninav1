package es.davante.edadcanina;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

/** Compruebo la pantalla real para detectar fallos al reorganizar el código */
@RunWith(AndroidJUnit4.class)
public class EdadCaninaPantallaTest {
    @Test public void seisMuestraCuarentaYDos() { comprobarCalculo("6", 42); }
    @Test public void unoMuestraSiete() { comprobarCalculo("1", 7); }
    @Test public void treintaMuestraDoscientosDiez() { comprobarCalculo("30", 210); }
    @Test public void vacioMuestraError() { comprobarError("", R.string.error_vacio); }
    @Test public void ceroMuestraError() { comprobarError("0", R.string.error_rango); }
    @Test public void treintaYUnoMuestraError() { comprobarError("31", R.string.error_rango); }
    @Test public void textoMuestraErrorSinCerrar() { comprobarError("abc", R.string.error_no_numero); }

    private void comprobarCalculo(String texto, int resultado) {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.age_input)).perform(replaceText(texto), closeSoftKeyboard());
            onView(withId(R.id.btn_calc)).perform(click());
            onView(withId(R.id.result_text)).check(matches(withText(
                    androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
                            .getTargetContext().getString(R.string.tu_edad_format, resultado))));
        }
    }

    private void comprobarError(String texto, int recurso) {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            // Solo en la prueba retiro los filtros para llegar al try/catch con letras
            onView(withId(R.id.age_input)).perform(permitirTexto(), replaceText(texto), closeSoftKeyboard());
            onView(withId(R.id.btn_calc)).perform(click());
            String mensaje = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
                    .getTargetContext().getString(recurso, 1, 30);
            onView(withId(R.id.age_input)).check(matches(hasErrorText(mensaje)));
        }
    }

    private ViewAction permitirTexto() {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isAssignableFrom(EditText.class); }
            @Override public String getDescription() { return "Permitir texto para probar entradas inválidas"; }
            @Override public void perform(UiController interfaz, View vista) {
                EditText campo = (EditText) vista;
                campo.setInputType(InputType.TYPE_CLASS_TEXT);
                campo.setFilters(new android.text.InputFilter[0]);
            }
        };
    }
}
