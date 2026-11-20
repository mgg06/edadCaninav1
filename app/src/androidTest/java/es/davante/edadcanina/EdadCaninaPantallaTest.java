package es.davante.edadcanina;

import android.text.InputType;
import android.os.SystemClock;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.assertTrue;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import static org.junit.Assert.assertEquals;
import android.view.View;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNull;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

/** Compruebo la pantalla real para detectar fallos al reorganizar el código */
@RunWith(AndroidJUnit4.class)
public class EdadCaninaPantallaTest {
    private static final long TIEMPO_MAXIMO_GIRO_MS = 10000;
    @Test public void seisMuestraCuarentaYDos() { comprobarCalculo("6", 42); }
    @Test public void unoMuestraSiete() { comprobarCalculo("1", 7); }
    @Test public void treintaMuestraDoscientosDiez() { comprobarCalculo("30", 210); }
    @Test public void vacioMuestraError() { comprobarError("", R.string.error_vacio); }
    @Test public void ceroMuestraError() { comprobarError("0", R.string.error_rango); }
    @Test public void treintaYUnoMuestraError() { comprobarError("31", R.string.error_rango); }
    @Test public void textoMuestraErrorSinCerrar() { comprobarError("abc", R.string.error_no_numero); }

    @Test public void editarOcultaElResultado() {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.age_input)).perform(replaceText("6"), closeSoftKeyboard());
            onView(withId(R.id.btn_calc)).perform(click());
            onView(withId(R.id.age_input)).perform(replaceText("7"), closeSoftKeyboard());
            onView(withId(R.id.result_text)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)));
        }
    }

    @Test public void editarQuitaElErrorSinPulsarCalcular() {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.btn_calc)).perform(click());
            onView(withId(R.id.age_input)).perform(replaceText("6"), closeSoftKeyboard());
            onView(withId(R.id.age_input)).check((vista, fallo) -> {
                if (fallo != null) throw fallo;
                assertNull(((EditText) vista).getError());
            });
            onView(withId(R.id.result_text)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)));
        }
    }

    @Test public void limpiarBorraEntradaResultadoYError() {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.age_input)).perform(replaceText("6"), closeSoftKeyboard());
            onView(withId(R.id.btn_calc)).perform(click());
            onView(withId(R.id.btn_limpiar)).perform(click());
            onView(withId(R.id.age_input)).check(matches(withText("")));
            onView(withId(R.id.result_text)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)));
            onView(withId(R.id.btn_calc)).perform(click());
            onView(withId(R.id.btn_limpiar)).perform(click());
            onView(withId(R.id.age_input)).check((vista, fallo) -> {
                if (fallo != null) throw fallo;
                assertNull(((EditText) vista).getError());
            });
        }
    }

    @Test public void recrearConservaElResultado() {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            introducirYCalcular("6");
            escenario.recreate();
            comprobarResultadoConservado();
        }
    }

    @Test public void recrearConservaElErrorDeVacio() { comprobarErrorRecreado("", R.string.error_vacio); }
    @Test public void recrearConservaElErrorDeCero() { comprobarErrorRecreado("0", R.string.error_rango); }
    @Test public void recrearConservaElErrorDeTreintaYUno() { comprobarErrorRecreado("31", R.string.error_rango); }

    @Test public void girarConservaResultadoYPermiteLimpiar() {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            introducirYCalcular("6");
            girarYEsperar(escenario, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE);
            // Desplazo con un gesto real después del giro, igual que en el emulador
            onView(withId(R.id.main)).perform(swipeUp());
            escenario.onActivity(actividad -> assertEquals(Configuration.ORIENTATION_LANDSCAPE,
                    actividad.getResources().getConfiguration().orientation));
            comprobarResultadoConservado();
            onView(withId(R.id.btn_limpiar)).perform(scrollTo(), click());
            onView(withId(R.id.age_input)).check(matches(withText("")));
            onView(withId(R.id.result_text)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)));
            girarYEsperar(escenario, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT);
            onView(withId(R.id.age_input)).check(matches(isDisplayed()));
        }
    }

    @Test public void girarConservaElErrorDeRango() {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            introducirYCalcular("31");
            girarYEsperar(escenario, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE);
            onView(withId(R.id.main)).perform(swipeUp());
            escenario.onActivity(actividad -> assertEquals(Configuration.ORIENTATION_LANDSCAPE,
                    actividad.getResources().getConfiguration().orientation));
            onView(withId(R.id.age_input)).check(matches(hasErrorText(mensaje(R.string.error_rango))));
            girarYEsperar(escenario, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT);
            onView(withId(R.id.age_input)).check(matches(isDisplayed()));
        }
    }

    @Test public void modoOscuroYClaroConservanElResultado() {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            introducirYCalcular("6");
            cambiarModo(escenario, AppCompatDelegate.MODE_NIGHT_YES, Configuration.UI_MODE_NIGHT_YES);
            comprobarResultadoConservado();
            cambiarModo(escenario, AppCompatDelegate.MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_NO);
            comprobarResultadoConservado();
        }
    }

    @Test public void modoOscuroConservaElError() {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            introducirYCalcular("31");
            cambiarModo(escenario, AppCompatDelegate.MODE_NIGHT_YES, Configuration.UI_MODE_NIGHT_YES);
            onView(withId(R.id.age_input)).check(matches(hasErrorText(mensaje(R.string.error_rango))));
            cambiarModo(escenario, AppCompatDelegate.MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_NO);
            onView(withId(R.id.age_input)).check(matches(hasErrorText(mensaje(R.string.error_rango))));
        }
    }

    @Test public void recrearNoCalculaUnaEntradaPendiente() {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.age_input)).perform(replaceText("6"), closeSoftKeyboard());
            escenario.recreate();
            onView(withId(R.id.age_input)).check(matches(withText("6")));
            onView(withId(R.id.result_text)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)));
            onView(withId(R.id.age_input)).check((vista, fallo) -> {
                if (fallo != null) throw fallo;
                assertNull(((EditText) vista).getError());
            });
        }
    }

    private void girarYEsperar(ActivityScenario<MainActivity> escenario, int orientacion, int esperada) {
        AtomicReference<MainActivity> anterior = new AtomicReference<>();
        escenario.onActivity(actividad -> {
            anterior.set(actividad);
            actividad.setRequestedOrientation(orientacion);
        });
        AtomicBoolean lista = new AtomicBoolean(false);
        long limite = SystemClock.uptimeMillis() + TIEMPO_MAXIMO_GIRO_MS;
        // El cambio lo inicia Android fuera del hilo de la prueba. Espero a la nueva Activity
        while (!lista.get() && SystemClock.uptimeMillis() < limite) {
            escenario.onActivity(actividad -> lista.set(actividad != anterior.get()
                    && actividad.getResources().getConfiguration().orientation == esperada));
            if (!lista.get()) SystemClock.sleep(50);
        }
        assertTrue("La Activity debe recrearse con la orientación solicitada", lista.get());
    }

    private void cambiarModo(ActivityScenario<MainActivity> escenario, int modo, int esperado) {
        escenario.onActivity(actividad -> actividad.getDelegate().setLocalNightMode(modo));
        // Espresso espera al hilo principal antes de comprobar la nueva configuración
        onView(withId(R.id.age_input)).check(matches(isDisplayed()));
        escenario.onActivity(actividad -> assertEquals(esperado,
                actividad.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK));
    }

    private void comprobarErrorRecreado(String entrada, int recurso) {
        try (ActivityScenario<MainActivity> escenario = ActivityScenario.launch(MainActivity.class)) {
            introducirYCalcular(entrada);
            escenario.recreate();
            onView(withId(R.id.age_input)).check(matches(withText(entrada)));
            onView(withId(R.id.age_input)).check(matches(hasErrorText(mensaje(recurso))));
            onView(withId(R.id.result_text)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)));
        }
    }

    private void introducirYCalcular(String texto) {
        onView(withId(R.id.age_input)).perform(replaceText(texto), closeSoftKeyboard());
        onView(withId(R.id.btn_calc)).perform(scrollTo(), click());
    }

    private void comprobarResultadoConservado() {
        onView(withId(R.id.age_input)).check(matches(withText("6")));
        onView(withId(R.id.result_text)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
        onView(withId(R.id.result_text)).check(matches(withText(mensajeResultado(42))));
    }

    private String mensaje(int recurso) {
        return androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getString(recurso, 1, 30);
    }

    private String mensajeResultado(int edad) {
        return androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getString(R.string.tu_edad_format, edad);
    }

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
