package es.davante.edadcanina.vista;

import android.content.Context;
import android.widget.EditText;

import androidx.databinding.BindingAdapter;

import es.davante.edadcanina.R;
import es.davante.edadcanina.modelo.EdadCaninaModelo;
import es.davante.edadcanina.modelo.Validacion;

/** Traduce los estados a la presentación, sin llevar mensajes al modelo */
public final class AdaptadoresVista {
    private AdaptadoresVista() { }

    @BindingAdapter("errorValidacion")
    public static void mostrarErrorValidacion(EditText campo, Validacion validacion) {
        Context contexto = campo.getContext();
        String mensaje = null;
        if (validacion != null) {
            switch (validacion) {
                case VACIO:
                    mensaje = contexto.getString(R.string.error_vacio);
                    break;
                case NO_NUMERO:
                    mensaje = contexto.getString(R.string.error_no_numero);
                    break;
                case FUERA_RANGO:
                    mensaje = contexto.getString(R.string.error_rango,
                            EdadCaninaModelo.EDAD_MINIMA, EdadCaninaModelo.EDAD_MAXIMA);
                    break;
                default:
                    break;
            }
        }
        // null quita también un error anterior cuando el usuario empieza a corregir
        campo.setError(mensaje);
        if (mensaje != null) campo.requestFocus();
    }
}
