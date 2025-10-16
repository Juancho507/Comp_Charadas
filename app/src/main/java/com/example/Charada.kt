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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CharadasApp()
        }
    }
}

@Composable
fun CharadasApp() {
    var pantallaActual by rememberSaveable { mutableStateOf("menu") }
    var record by rememberSaveable { mutableStateOf(0) }
    var puntaje by rememberSaveable { mutableStateOf(0) }
    var categoria by rememberSaveable { mutableStateOf("") }
    var tiempoJuego by rememberSaveable { mutableStateOf(60) }

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

    when (pantallaActual) {
        "menu" -> Menu(
            record = record,
            onSelectCategory = { cat: String ->
                categoria = cat
                puntaje = 0
                pantallaActual = "cuenta"
            },
            onGoToSettings = { pantallaActual = "ajustes" }
        )

        "cuenta" -> CuentaRegresiva(
            onCountdownFinished = { pantallaActual = "juego" }
        )

        "juego" -> Juego(
            categoria = categoria,
            tiempoPartida = tiempoJuego,
            onFinish = { puntajeFinal ->
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
            onBackToMenu = { pantallaActual = "menu" }
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

@Composable
fun CuentaRegresiva(onCountdownFinished: () -> Unit) {
    var contador by remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {
        while (contador > 0) {
            delay(1000)
            contador--
        }
        onCountdownFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (contador > 0) contador.toString() else "¡YA!",
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Yellow
        )
    }
}

@Composable
fun Menu(record: Int, onSelectCategory: (String) -> Unit, onGoToSettings: () -> Unit) {
    val scrollState = rememberScrollState()

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

@Composable
fun Juego(
    categoria: String,
    tiempoPartida: Int,
    onFinish: (Int) -> Unit,
    animales: List<String>,
    peliculas: List<String>,
    profesiones: List<String>
) {
    val palabras = remember {
        when (categoria) {
            "Animales" -> animales
            "Peliculas" -> peliculas
            "Profesiones" -> profesiones
            else -> listOf("Palabra1", "Palabra2")
        }
    }

    var indice by rememberSaveable { mutableStateOf(0) }
    var puntaje by rememberSaveable { mutableStateOf(0) }
    var tiempoRestante by rememberSaveable { mutableStateOf(tiempoPartida) }

    LaunchedEffect(Unit) {
        while (tiempoRestante > 0) {
            delay(1000)
            tiempoRestante--
        }
        onFinish(puntaje)
    }

    if (indice >= palabras.size) {
        onFinish(puntaje)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2196F3))
        ) {
            Text(
                "Categoría: $categoria",
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
            )

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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(12.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { indice++ },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Pasar", fontSize = 26.sp, color = Color.White)
                }

                Button(
                    onClick = { puntaje++; indice++ },
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
                painter = painterResource(id = R.drawable.ganador),
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
                painter = painterResource(id = R.drawable.perdedor),
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

@Composable
fun Ajustes(
    tiempoPartida: Int,
    onTiempoChange: (Int) -> Unit,
    onBackToMenu: () -> Unit,
    animales: MutableList<String>,
    peliculas: MutableList<String>,
    profesiones: MutableList<String>
) {
    var nuevaPalabra by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Animales") }
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

            TextField(
                value = nuevaPalabra,
                onValueChange = { nuevaPalabra = it },
                label = { Text("Nueva palabra para $categoriaSeleccionada") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Text("Animales: ${animales.size}   Peliculas: ${peliculas.size}   Profesiones: ${profesiones.size}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Text("⏱ Duración de la partida: $tiempoPartida s", fontSize = 20.sp)
            Slider(
                value = tiempoPartida.toFloat(),
                onValueChange = { onTiempoChange(it.toInt()) },
                valueRange = 30f..60f,
                steps = 30,
                modifier = Modifier.width(250.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onBackToMenu) { Text("Volver al Menú") }
        }
    }
}