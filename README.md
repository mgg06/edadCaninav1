# 🐶 EdadCanina · v1

Aplicación Android (Java) que calcula la edad "humana" de un perro multiplicando su edad por 7.

Esta es la **versión 1**: funciona, pero no comprueba lo que escribe el usuario. Tu tarea es convertirla en la **versión 2** (rama `v2-validacion`), que valida la entrada y controla los errores sin que la app se cierre.

---

## Objetivos

Al terminar sabrás:

- Detectar por qué una app se cierra (`NumberFormatException`) y evitarlo con `try/catch`.
- Validar datos de entrada: campo vacío, texto no numérico y rango de valores.
- Mostrar errores al usuario con `EditText.setError()`.
- Sustituir "números mágicos" por constantes.
- Llevar los textos a `strings.xml` y usar formatos con varios parámetros.
- Trabajar en una rama de Git distinta de `main`.

---

## Paso 0 · Prepara el proyecto

1. Haz un **fork** de este repositorio (botón *Fork*) y clona tu fork:
   ```bash
   git clone https://github.com/TU_USUARIO/edadCaninav1.git
   ```
2. Ábrelo en Android Studio y ejecútalo en el emulador.

## Paso 1 · Rompe la app (a propósito)

Antes de arreglar nada, comprueba qué falla. Prueba estas entradas y anota qué pasa:

| Entrada | ¿Qué ocurre? |
|---|---|
| *(campo vacío)* | |
| `abc` | |
| `3.5` | |
| `0` | |
| `-4` | |
| `99999999999` | |
| `400000000` | |

> 💡 Cuando la app se cierre, abre el **Logcat** y busca la excepción. ¿Qué método la lanza? ¿Por qué el último caso no cierra la app pero da un resultado absurdo?

## Paso 2 · Crea la rama de trabajo

```bash
git checkout -b v2-validacion
```

Todo lo que hagas a partir de aquí irá en esta rama; `main` se queda como está.

## Paso 3 · Corrige los fallos que ya trae la v1

Antes de añadir nada nuevo, limpia lo que hay:

1. **`activity_main.xml`**: busca la etiqueta de cierre `</TextView>>` y quita el `>` que sobra.
2. **`strings.xml`**: en `tu_edad_format` hay una falta de ortografía ("fuero"). Corrígela.
3. **Texto escrito "a fuego"**: el `android:hint="Ejemplo: 6"` del `EditText` está directamente en el layout. Llévalo a `strings.xml` con el nombre `hint_edad` y úsalo con `@string/hint_edad`.
   > 💡 Android Studio marca este aviso en amarillo (*Hardcoded string*). Prueba `Alt + Enter` encima.
4. **`MainActivity.java`**: hay tres `import` que no se usan (`Insets`, `ViewCompat`, `WindowInsetsCompat`). Elimínalos (`Ctrl + Alt + O`).

## Paso 4 · Añade los mensajes de error

En `res/values/strings.xml` añade tres textos nuevos:

```xml
<string name="error_vacio">Introduce la edad de tu perro</string>
<string name="error_no_numero">Introduce un número entero válido</string>
<string name="error_rango">La edad debe estar entre %1$d y %2$d años</string>
```

> ❓ ¿Qué significan `%1$d` y `%2$d`? ¿En qué orden se rellenan?

## Paso 5 · Restringe la entrada desde el layout

En el `EditText` (`@+id/age_input`) añade:

```xml
android:inputType="number"
android:maxLength="2"
android:gravity="center"
android:importantForAutofill="no"
```

| Atributo | Para qué sirve |
|---|---|
| `inputType="number"` | Muestra el teclado numérico: sin letras, sin signo menos ni decimales. |
| `maxLength="2"` | No deja escribir más de 2 cifras. |
| `gravity="center"` | Centra el número en el campo. |
| `importantForAutofill="no"` | Evita que el sistema ofrezca autocompletar. |

> ⚠️ Esto **no sustituye** a la validación en Java: el campo puede quedarse vacío y nunca conviene fiarse solo de la interfaz. Por eso seguimos con el código.

## Paso 6 · Sustituye los números mágicos por constantes

Al principio de la clase `MainActivity` declara:

```java
private static final int FACTOR_CANINO = 7;
private static final int EDAD_MINIMA = 1;
private static final int EDAD_MAXIMA = 30; // ningún perro supera esta edad
```

y usa `FACTOR_CANINO` en lugar del `7`.

## Paso 7 · Convierte las vistas en atributos de la clase

Ahora `ageEdit` y `resultText` son variables locales de `onCreate()`. Como vas a usarlas desde otros métodos, decláralas como atributos:

```java
private EditText ageEdit;
private TextView resultText;
```

y en `onCreate()` asígnalas **sin volver a escribir el tipo**:

```java
ageEdit = findViewById(R.id.age_input);
resultText = findViewById(R.id.result_text);
```

> ❓ ¿Qué pasaría si dejaras `EditText ageEdit = findViewById(...)` dentro de `onCreate()`?

## Paso 8 · Saca la lógica del botón a un método

El `setOnClickListener` debe quedar así de corto:

```java
button1.setOnClickListener(view -> calcularEdad());
```

Toda la lógica pasa a un método nuevo: `private void calcularEdad()`.

## Paso 9 · Crea un método para mostrar errores

```java
private void mostrarError(String mensaje) {
    // 1. Mostrar el mensaje en el EditText (setError)
    // 2. Poner el foco en el EditText (requestFocus)
    // 3. Borrar el resultado anterior del TextView
}
```

## Paso 10 · Valida la entrada en `calcularEdad()`

Implementa el método siguiendo este orden. En cuanto algo falle, llama a `mostrarError(...)` y sal con `return`:

1. **Leer** el texto del `EditText` quitando espacios (`trim()`).
2. **¿Está vacío?** → `error_vacio`. Recuerda que `Integer.parseInt("")` lanza `NumberFormatException`.
3. **Convertir a `int`** dentro de un `try/catch (NumberFormatException e)` → si falla, `error_no_numero`.
4. **¿Está en rango?** Si `age < EDAD_MINIMA || age > EDAD_MAXIMA` → `error_rango`. Para rellenar `%1$d` y `%2$d`:
   ```java
   getString(R.string.error_rango, EDAD_MINIMA, EDAD_MAXIMA)
   ```
5. **Todo correcto**: quita el error del campo (`setError(null)`), calcula `age * FACTOR_CANINO` y muestra el resultado con `tu_edad_format`.

> ❓ ¿Por qué limitar la edad a 30 evita también el desbordamiento que viste en el Paso 1?

## Paso 11 · Vuelve a probar

Comprueba que **la app nunca se cierra**:

| Entrada | Resultado esperado |
|---|---|
| *(vacío)* | Error: "Introduce la edad de tu perro" |
| `0` | Error: "La edad debe estar entre 1 y 30 años" |
| `31` | Error de rango |
| `1` | "Si tu perro fuera humano tendría 7 años" |
| `30` | "Si tu perro fuera humano tendría 210 años" |

> 💡 Con el teclado numérico no puedes escribir `abc` ni `-4`. ¿Cómo comprobarías que el `try/catch` funciona? (Pista: quita `inputType` un momento.)

## Paso 12 · Guarda y sube tu trabajo

```bash
git add .
git commit -m "Validación de la entrada y control de errores aritméticos"
git push -u origin v2-validacion
```

Comprueba en GitHub que tu fork tiene ahora dos ramas: `main` y `v2-validacion`.

---

## ✅ Lista de comprobación

- [ ] La app no se cierra con ninguna entrada.
- [ ] Todos los textos están en `strings.xml` (ningún aviso *Hardcoded string*).
- [ ] No quedan números mágicos en `MainActivity`.
- [ ] `onCreate()` solo prepara la pantalla; la lógica está en `calcularEdad()`.
- [ ] El trabajo está en la rama `v2-validacion`, no en `main`.


## EdadCaninaMVC

Esta versión está en la rama `v3-mvc` y parte de la v2 con validación. Su identificador es `es.davante.edadcanina.mvc`, por lo que se instala junto a la v2

### Preguntas Piensa

**Paso 1. ¿Ha aparecido una clase nueva?**

Activar DataBinding prepara el proyecto, pero todavía no genera `ActivityMainBinding`. Para crear esa clase hace falta que `activity_main.xml` tenga la raíz `<layout>` y volver a compilar

### Comprobaciones por paso

Las pruebas de pantalla están en `app/src/androidTest` y se ejecutan en el emulador. Retiran los filtros de entrada solo durante la prueba de texto para comprobar la validación Java, manteniendo el teclado numérico de la aplicación


**Paso 2. ¿Cuándo se detecta un id incorrecto?**

Si escribo mal `binding.ageInput`, Java lo detecta al compilar porque ese campo no existe en `ActivityMainBinding`. Con `findViewById`, un nombre de recurso inexistente también falla al compilar, pero un id válido de otra pantalla puede devolver `null` en ejecución, y un tipo equivocado puede causar un error de conversión. El binding evita estas búsquedas y ofrece el tipo correcto para este layout

La clase generada permite ver los campos `ageInput`, `resultText` y `btnCalc`. El nombre del archivo `activity_main.xml` se transforma en `ActivityMainBinding`, y los ids pasan a camelCase


**Paso 3. ¿Hay que tocar el modelo para traducir la app?**

No. El modelo devuelve valores de `Validacion` y una edad numérica, sin mensajes ni recursos de Android. La vista obtiene los textos de `strings.xml`. Una traducción se añade en `res/values-en/strings.xml` sin modificar las reglas ni las pruebas del modelo

### Reto elegido: casos raros

He decidido aceptar `05` y `+5` como el entero 5, por lo que ambos dan 35 años humanos. El cero inicial y el signo positivo no cambian su valor. `Integer.parseInt` ya reconoce estos formatos, así que no hace falta añadir otra regla. Hay una prueba JUnit para cada uno. El teclado numérico sigue restringiendo la entrada habitual, y las pruebas llaman al modelo directamente para comprobar también datos que la interfaz puede filtrar

Los espacios exteriores se eliminan al validar. Un decimal o un valor que no cabe en `int` devuelve `NO_NUMERO`. Un entero fuera de 1 a 30 devuelve `FUERA_RANGO` antes de multiplicarlo, de modo que no se produce desbordamiento


**Paso 4. ¿Dónde se corta el viaje de ida y vuelta?**

Escribo una cifra y `@={modelo.edadTexto}` llama al setter. Si cambia el texto, el modelo lo guarda y avisa con `BR.edadTexto`. El binding actualiza las expresiones que dependen de esa propiedad. Si llega otra asignación del mismo texto, `nuevoTexto.equals(edadTexto)` hace que el setter salga sin avisar. Así no se repiten cambios innecesarios ni se invalida un resultado sin haber cambiado la entrada

El resultado utiliza `INVISIBLE` porque reserva su espacio y mantiene los botones en su sitio. Con `GONE`, el hueco desaparece y los controles inferiores suben al ocultarlo. La edad se obtiene del modelo, el XML solo aplica el formato y decide su visibilidad


**Paso 5. ¿Por qué el adaptador está en vista?**

Porque convierte un estado del modelo en un mensaje traducido, lo muestra con `setError` y gestiona el foco. Todo eso depende de Android y de cómo se presenta la pantalla. En el modelo solo debe estar la decisión de si el dato es válido. Al escribir otro texto, la validación pasa a `SIN_CALCULAR`, el adaptador recibe el cambio y quita el error sin pulsar Calcular


**Paso 6. ¿Es buena señal que el controlador tenga pocas líneas?**

Sí, porque solo recibe las acciones y llama a `calcular()` o `limpiar()`. Las reglas están en el modelo y los textos y errores se presentan desde la vista. Si el controlador tuviera que convertir números o acceder a un `EditText`, estaría mezclando responsabilidades

Los dos botones comparten una fila horizontal con el mismo peso. La pantalla puede desplazarse al poner el móvil en horizontal, para que la imagen y los controles no dejen los botones fuera de alcance


**Experimento previo al paso 7:** al recrear la Activity después de calcular 6, se pierden tanto el texto de entrada como el resultado. El modelo nuevo empieza vacío, y el enlace de datos lleva ese estado a la pantalla. La prueba del paso 6 deja comprobado este comportamiento antes de añadir el guardado
