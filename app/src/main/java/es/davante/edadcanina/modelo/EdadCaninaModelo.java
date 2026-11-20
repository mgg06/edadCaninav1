package es.davante.edadcanina.modelo;

/** Guarda la entrada y las reglas sin depender de una pantalla de Android */
public class EdadCaninaModelo {
    private static final int FACTOR_CANINO = 7;
    public static final int EDAD_MINIMA = 1;
    public static final int EDAD_MAXIMA = 30;

    private String edadTexto = "";
    private int edadHumana;
    private Validacion validacion = Validacion.SIN_CALCULAR;

    public String getEdadTexto() { return edadTexto; }
    public int getEdadHumana() { return edadHumana; }
    public Validacion getValidacion() { return validacion; }

    public void setEdadTexto(String nuevoTexto) {
        // null equivale a un campo vacío, incluso si el dato llega sin una vista
        if (nuevoTexto == null) nuevoTexto = "";
        if (nuevoTexto.equals(edadTexto)) return;
        edadTexto = nuevoTexto;
        // Un resultado solo sirve para la entrada con la que se calculó
        setEdadHumana(0);
        setValidacion(Validacion.SIN_CALCULAR);
    }

    private void setEdadHumana(int nuevaEdad) {
        if (nuevaEdad == edadHumana) return;
        edadHumana = nuevaEdad;
    }

    private void setValidacion(Validacion nuevaValidacion) {
        if (nuevaValidacion == validacion) return;
        validacion = nuevaValidacion;
    }

    public Validacion calcular() {
        String texto = edadTexto.trim();
        setEdadHumana(0);
        if (texto.isEmpty()) {
            setValidacion(Validacion.VACIO);
            return validacion;
        }

        int edadPerro;
        try {
            // Acepto 05 y +5 como enteros. También controlo números que no caben en int
            edadPerro = Integer.parseInt(texto);
        } catch (NumberFormatException excepcion) {
            setValidacion(Validacion.NO_NUMERO);
            return validacion;
        }

        if (edadPerro < EDAD_MINIMA || edadPerro > EDAD_MAXIMA) {
            setValidacion(Validacion.FUERA_RANGO);
            return validacion;
        }

        // Validar antes de multiplicar limita el resultado a 210 y evita desbordamientos
        setEdadHumana(edadPerro * FACTOR_CANINO);
        setValidacion(Validacion.OK);
        return validacion;
    }
}
