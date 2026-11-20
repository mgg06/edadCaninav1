package es.davante.edadcanina;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import es.davante.edadcanina.controlador.EdadCaninaControlador;
import es.davante.edadcanina.databinding.ActivityMainBinding;
import es.davante.edadcanina.modelo.EdadCaninaModelo;
import es.davante.edadcanina.modelo.Validacion;

public class MainActivity extends AppCompatActivity {
    private static final String CLAVE_EDAD_TEXTO = "edad_texto";
    private static final String CLAVE_CALCULADO = "calculado";

    private EdadCaninaModelo modelo;

    @Override
    protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        EdgeToEdge.enable(this);
        modelo = new EdadCaninaModelo();
        if (estadoGuardado != null) {
            modelo.setEdadTexto(estadoGuardado.getString(CLAVE_EDAD_TEXTO, ""));
            // Repito la operación para reconstruir tanto el resultado como cualquier error
            if (estadoGuardado.getBoolean(CLAVE_CALCULADO, false)) modelo.calcular();
        }
        // Restauro antes de enlazar para que la vista reciba el estado correcto desde el inicio
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // La Activity conecta las piezas, y el XML se encarga de los clics y de pintar
        binding.setModelo(modelo);
        binding.setControlador(new EdadCaninaControlador(modelo));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle estado) {
        super.onSaveInstanceState(estado);
        estado.putString(CLAVE_EDAD_TEXTO, modelo.getEdadTexto());
        // No guardo la edad ni el enum. Entrada e intento bastan para reconstruirlos
        estado.putBoolean(CLAVE_CALCULADO, modelo.getValidacion() != Validacion.SIN_CALCULAR);
    }
}
