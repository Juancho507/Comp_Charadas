package com.example.charadas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Clase principal que inicia la actividad del juego.
 * Carga el contenido de la interfaz al iniciar la aplicación.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Carga la estructura visual principal de la aplicación
        setContent {
            CharadasApp() // Llamamos al composable principal
        }
    }
}

/**
 * Composable principal que controla el flujo completo del juego.
 */
@Composable
fun CharadasApp() {
    /**
     * Estados globales:
     * - pantallaActual: controla la navegación interna entre pantallas.
     * - record: almacena el récord más alto obtenido.
     * - puntaje: almacena el puntaje final de la partida en curso.
     * - categoria: categoría seleccionada por el jugador.
     * - tiempoJuego: duración de la partida en segundos (ajustable).
     */
    var pantallaActual by rememberSaveable { mutableStateOf("menu") }
    var record by rememberSaveable { mutableStateOf(0) }
    var puntaje by rememberSaveable { mutableStateOf(0) }
    var categoria by rememberSaveable { mutableStateOf("") }
    var tiempoJuego by rememberSaveable { mutableStateOf(60) }
    // Listas dinámicas de palabras por categoría
    //Son listas mutables para poder agregar nuevas palabras desde la pantalla de ajustes.
    val animales = remember {
        mutableStateListOf(
            "Perro", "Gato", "Loro", "Conejo", "Pez", "Hamster", "Tortuga",
            "Caballo", "Jirafa", "Oso", "Cerdo", "Hormiga", "Tigre", "Mosca",
            "Tiburón", "Águila", "Lagartija", "Serpiente", "León", "Elefante"
        )
    }
    val peliculas = remember {
        mutableStateListOf(
            "Titanic", "Avatar", "Inception", "Matrix", "Avengers",
            "Joker", "Toy Story", "El Rey León", "Jurassic Park", "Harry Potter",
            "El señor de los anillos", "Spider-Man", "Frozen", "Los Minions",
            "Piratas del Caribe", "Rápidos y Furiosos", "Shrek", "Star Wars",
            "It", "Misión imposible"
        )
    }
    val profesiones = remember {
        mutableStateListOf(
            "Doctor", "Ingeniero", "Profesor", "Policía", "Bombero", "Piloto",
            "Chef", "Abogado", "Veterinario", "Psicólogo", "Fotógrafo", "Carpintero",
            "Enfermero", "Actor", "Programador", "Mecánico", "Obrero", "Agricultor",
            "Pescador", "Barbero"
        )
    }
    /**
     * Control de flujo de pantallas.
     * Según el valor de `pantallaActual` se renderiza el Composable correspondiente.
     * Las transiciones entre pantallas se realizan actualizando `pantallaActual` desde callbacks.
     */
    when (pantallaActual) {
        "menu" -> Menu(
            record = record,
            onSelectCategory = { cat: String ->
                categoria = cat
                puntaje = 0
                pantallaActual = "cuenta" // Cambia a la pantalla de cuenta regresiva
            },
            onGoToSettings = { pantallaActual = "ajustes" } //Ir a ajustes
        )

        "cuenta" -> CuentaRegresiva(
            onCountdownFinished = { pantallaActual = "juego" } // Inicia el juego
        )

        "juego" -> Juego(
            categoria = categoria,
            tiempoPartida = tiempoJuego,
            onFinish = { puntajeFinal ->
                // Al finalizar la partida se actualiza el puntaje y se decide la pantalla siguiente:
                // - Si supera el récord se muestra "nuevoRecord" y se actualiza `record`.
                // - Si no, se muestra "sinRecord".
                puntaje = puntajeFinal
                pantallaActual = if (puntajeFinal > record) {
                    record = puntajeFinal
                    "nuevoRecord"
                } else {
                    "sinRecord"
                }
            },
            animales = animales,
            peliculas = peliculas,
            profesiones = profesiones
        )

        "nuevoRecord" -> NuevoRecord(
            puntaje = puntaje,
            onBackToMenu = { pantallaActual = "menu" } //Regresa al menu
        )

        "sinRecord" -> SinRecord(
            puntaje = puntaje,
            record = record,
            onBackToMenu = { pantallaActual = "menu" }
        )

        "ajustes" -> Ajustes(
            tiempoPartida = tiempoJuego,
            onTiempoChange = { nuevoTiempo -> tiempoJuego = nuevoTiempo },
            onBackToMenu = { pantallaActual = "menu" },
            animales = animales,
            peliculas = peliculas,
            profesiones = profesiones
        )
    }
}

/**
 * Pantalla de cuenta regresiva antes de iniciar el juego.
 *
 * - Mostrar una cuenta regresiva (3, 2, 1) para preparar al jugador.
 * - Llamar a `onCountdownFinished()` cuando la cuenta termina para iniciar la partida.
 */
@Composable
fun CuentaRegresiva(onCountdownFinished: () -> Unit) {
    var contador by remember { mutableStateOf(3) } //Inicio de la cuenta regresiva (va desde 3)

        //Efecto que reduce el contador cada segundo
    LaunchedEffect(Unit) {
        while (contador > 0) {
            delay(1000)
            contador--
        }
        onCountdownFinished() // Llama al juego al terminar
    }

    // Diseño visual de la cuenta regresiva
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3)), //Color del fondo(Azul)
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (contador > 0) contador.toString() else "¡YA!", // Muestra el número o "¡YA!"
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Yellow
        )
    }
}

/**
 * Pantalla principal del menú.
 *
 * - Mostrar título del juego y récord actual.
 * - Permitir al jugador elegir una categoría (Animales, Películas, Profesiones).
 * - Acceder a la pantalla de ajustes.
 *
 */
@Composable
fun Menu(record: Int, onSelectCategory: (String) -> Unit, onGoToSettings: () -> Unit) {
    val scrollState = rememberScrollState() // Permite desplazar el contenido si es necesario

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 40.dp)
        ) {
            Text(
                text = "🎭 Juego de Charadas",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "🏆 Tu récord es: $record",
                fontSize = 22.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Botones de selección de categoría.
            // Cada botón ejecuta `onSelectCategory` con la categoría asociada.
            listOf(
                "🐶 Animales" to "Animales",
                "🎬 Películas" to "Peliculas",
                "👨‍⚕ Profesiones" to "Profesiones"
            ).forEach { (emojiText, cat) ->
                Button(
                    onClick = { onSelectCategory(cat) },
                    modifier = Modifier
                        .width(260.dp)
                        .height(70.dp)
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(emojiText, color = Color.Blue, fontSize = 22.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            // Botón para ir a la pantalla de ajustes
            Button(
                onClick = onGoToSettings,
                modifier = Modifier
                    .width(260.dp)
                    .height(70.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
            ) {
                Text("⚙ Ajustes", color = Color.Black, fontSize = 22.sp)
            }
        }
    }
}

/**
 * Pantalla de juego principal donde se muestran las palabras a adivinar.
*
 * - Mostrar la palabra actual según la categoría seleccionada.
 * - Controlar el tiempo restante de la partida.
 * - Permitir al usuario "Pasar" (sin puntuar) o marcar "Correcto" (sumar punto).
 * - Finalizar la partida cuando se acaba el tiempo o se terminan las palabras.
 */
@Composable
fun Juego(
    categoria: String,
    tiempoPartida: Int,
    onFinish: (Int) -> Unit,
    animales: List<String>,
    peliculas: List<String>,
    profesiones: List<String>
) {
    // Selecciona la lista de palabras según la categoría
    val palabras = remember {
        when (categoria) {
            "Animales" -> animales
            "Peliculas" -> peliculas
            "Profesiones" -> profesiones
            else -> listOf("Palabra1", "Palabra2")
        }
    }
    // Estados internos de la partida:
    // indice: índice de la palabra actual en la lista.
    // puntaje: puntos acumulados durante la partida.
    // tiempoRestante: segundos que faltan para terminar la partida.
    var indice by rememberSaveable { mutableStateOf(0) }
    var puntaje by rememberSaveable { mutableStateOf(0) }
    var tiempoRestante by rememberSaveable { mutableStateOf(tiempoPartida) }

    // Temporizador de la partida: decrementa `tiempoRestante` y llama a `onFinish` al terminar.
    LaunchedEffect(Unit) {
        while (tiempoRestante > 0) {
            delay(1000)
            tiempoRestante--
        }
        onFinish(puntaje)
    }

    // Si se acaban las palabras, termina la partida
    if (indice >= palabras.size) {
        onFinish(puntaje)
    } else {
        // Diseño visual del juego: muestra categoría, tiempo y palabra actual.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2196F3))
        ) {
            // Muestra la categoría arriba
            Text(
                "Categoría: $categoria",
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
            )
            // Muestra palabra y tiempo restante
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    "⏱ Tiempo: $tiempoRestante s",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    palabras[indice],
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Yellow,
                    maxLines = 2
                )
            }
            // Botones "Pasar" y "Correcto"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(12.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { indice++ }, // Pasa a la siguiente palabra sin puntuar
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Pasar", fontSize = 26.sp, color = Color.White)
                }

                Button(
                    onClick = { puntaje++; indice++ }, // Suma punto y avanza
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Correcto", fontSize = 26.sp, color = Color.White)
                }
            }
        }
    }
}

/**
 * Pantalla mostrada cuando el jugador rompe el récord.
*
 * - Felicitar al jugador, mostrar puntaje y permitir volver al menú.
 * - Presentación en fondo verde con imagen de ganador.
 */
@Composable
fun NuevoRecord(puntaje: Int, onBackToMenu: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ganador), //Imagen de victoria
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
            Text("🎉 ¡Rompiste Récord!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Puntaje: $puntaje", fontSize = 24.sp, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onBackToMenu) { Text("Volver al Menú") }
        }
    }
}

/**
 * Pantalla mostrada cuando el jugador no supera el récord.
*
 * - Informar el resultado, mostrar puntaje y récord actual.
 * - Permitir volver al menú desde una interfaz con fondo rojo.
 */
@Composable
fun SinRecord(puntaje: Int, record: Int, onBackToMenu: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF44336)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.perdedor), //Imagen de derrota
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
            Text("😞 ¡No superaste el récord!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Puntaje: $puntaje", fontSize = 24.sp, color = Color.White)
            Text("Récord: $record", fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onBackToMenu) { Text("Volver al Menú") }
        }
    }
}

/**
 * Pantalla de ajustes para modificar opciones del juego.
*
 * - Permitir agregar nuevas palabras a las listas por categoría.
 * - Mostrar el conteo de palabras por categoría.
 * - Ajustar la duración de la partida mediante un slider (30 a 60 s).
 * - Volver al menú principal.
 * - Las palabras añadidas se guardan directamente en las listas mutables pasadas como parámetros,
 *   por lo que estarán disponibles en la siguiente partida.
 */
@Composable
fun Ajustes(
    tiempoPartida: Int,
    onTiempoChange: (Int) -> Unit,
    onBackToMenu: () -> Unit,
    animales: MutableList<String>,
    peliculas: MutableList<String>,
    profesiones: MutableList<String>
) {
    var nuevaPalabra by remember { mutableStateOf("") }  //Nueva palabra para agregar
    var categoriaSeleccionada by remember { mutableStateOf("Animales") } //Categoria activa
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFC107)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text("⚙ Ajustes", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            // Botones para elegir categoría a modificar
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Animales", "Peliculas", "Profesiones").forEach { cat ->
                    Button(
                        onClick = { categoriaSeleccionada = cat },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (categoriaSeleccionada == cat) Color.Blue else Color.Gray
                        )
                    ) {
                        Text(cat, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de texto para nueva palabra
            TextField(
                value = nuevaPalabra,
                onValueChange = { nuevaPalabra = it },
                label = { Text("Nueva palabra para $categoriaSeleccionada") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Boton para agregar la nueva palabra a la categoria elegida
            Button(onClick = {
                if (nuevaPalabra.isNotBlank()) {
                    when (categoriaSeleccionada) {
                        "Animales" -> animales.add(nuevaPalabra.trim())
                        "Peliculas" -> peliculas.add(nuevaPalabra.trim())
                        "Profesiones" -> profesiones.add(nuevaPalabra.trim())
                    }
                    nuevaPalabra = ""
                }
            }) {
                Text("Agregar palabra")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Muestra cuántas palabras hay en cada categoría
            Text("Animales: ${animales.size}   Peliculas: ${peliculas.size}   Profesiones: ${profesiones.size}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // Slider para modificar el tiempo del juego
            Text("⏱ Duración de la partida: $tiempoPartida s", fontSize = 20.sp)
            Slider(
                value = tiempoPartida.toFloat(),
                onValueChange = { onTiempoChange(it.toInt()) },
                valueRange = 30f..60f, // Rango de duracion
                steps = 30,
                modifier = Modifier.width(250.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onBackToMenu) { Text("Volver al Menú") }
        }
    }
}