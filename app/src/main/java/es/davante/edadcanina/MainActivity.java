package es.davante.edadcanina;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import es.davante.edadcanina.databinding.ActivityMainBinding;
import es.davante.edadcanina.modelo.EdadCaninaModelo;
import es.davante.edadcanina.modelo.Validacion;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private EdadCaninaModelo modelo;

    @Override
    protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        modelo = new EdadCaninaModelo();
        binding.setModelo(modelo);
        binding.btnCalc.setOnClickListener(vista -> mostrarError(modelo.calcular()));
    }

    private void mostrarError(Validacion validacion) {
        // El resultado ya lo pinta el XML. El error pasa al adaptador en el siguiente paso
        String mensaje = null;
        switch (validacion) {
            case VACIO:
                mensaje = getString(R.string.error_vacio);
                break;
            case NO_NUMERO:
                mensaje = getString(R.string.error_no_numero);
                break;
            case FUERA_RANGO:
                mensaje = getString(R.string.error_rango,
                        EdadCaninaModelo.EDAD_MINIMA, EdadCaninaModelo.EDAD_MAXIMA);
                break;
            default:
                break;
        }
        binding.ageInput.setError(mensaje);
        if (mensaje != null) binding.ageInput.requestFocus();
    }
}
