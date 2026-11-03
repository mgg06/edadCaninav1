# Pruebas de EdadCanina

## Versión 1

Probé las siete entradas en la app original desde el emulador. En los cuatro cierres, Logcat mostró NumberFormatException en Integer.parseInt al pulsar Calcular en MainActivity.java, línea 32.

| Entrada | Resultado |
| --- | --- |
| Campo vacío | NumberFormatException |
| abc | NumberFormatException |
| 3.5 | NumberFormatException |
| 0 | Muestra 0 años |
| -4 | Muestra -28 años |
| 99999999999 | NumberFormatException |
| 400000000 | Muestra -1494967296 años |

La excepción sale de Integer.parseInt. En el último caso el número sí cabe en un int, pero al multiplicarlo por 7 el resultado se sale del límite y se convierte en un número negativo.

## Versión 2

| Entrada | Resultado esperado |
| --- | --- |
| Campo vacío | Introduce la edad de tu perro |
| 0 | La edad debe estar entre 1 y 30 años |
| 31 | La edad debe estar entre 1 y 30 años |
| 1 | Si tu perro fuera humano tendría 7 años |
| 30 | Si tu perro fuera humano tendría 210 años |
| abc | Introduce un número entero válido |

%1$d coge el primer número que le paso a getString, que es 1. %2$d coge el segundo, que es 30.

Si vuelvo a poner EditText delante de ageEdit en onCreate, creo otra variable con el mismo nombre. El atributo de la clase se queda sin asignar y calcularEdad no puede leer el campo.

Con 30 como máximo el resultado más grande es 210 y cabe en un int. Para probar el catch desde la pantalla puedo quitar temporalmente inputType y maxLength y escribir abc o un número que no quepa en un int.
