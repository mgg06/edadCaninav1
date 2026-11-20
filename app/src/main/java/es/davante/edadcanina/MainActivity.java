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
        binding.btnCalc.setOnClickListener(vista -> calcularEdad());
    }

    private void calcularEdad() {
        modelo.setEdadTexto(binding.ageInput.getText().toString());
        Validacion validacion = modelo.calcular();
        // El modelo devuelve estados, y esta parte de la vista elige los recursos
        switch (validacion) {
            case OK:
                binding.ageInput.setError(null);
                binding.resultText.setText(getString(R.string.tu_edad_format, modelo.getEdadHumana()));
                break;
            case VACIO:
                mostrarError(getString(R.string.error_vacio));
                break;
            case NO_NUMERO:
                mostrarError(getString(R.string.error_no_numero));
                break;
            case FUERA_RANGO:
                mostrarError(getString(R.string.error_rango,
                        EdadCaninaModelo.EDAD_MINIMA, EdadCaninaModelo.EDAD_MAXIMA));
                break;
            default:
                break;
        }
    }

    private void mostrarError(String mensaje) {
        binding.ageInput.setError(mensaje);
        binding.ageInput.requestFocus();
        // Borro el resultado para que no se quede la edad del cálculo anterior
        binding.resultText.setText("");
    }
}
