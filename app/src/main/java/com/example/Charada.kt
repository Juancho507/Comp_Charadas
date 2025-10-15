package com.example.charadas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        "menu" -> Menu(record, { cat ->
            categoria = cat
            puntaje = 0
            pantallaActual = "cuenta"
        }, { pantallaActual = "ajustes" })

        "cuenta" -> CuentaRegresiva({ pantallaActual = "juego" })

        "juego" -> Juego(categoria, tiempoJuego, { puntajeFinal ->
            puntaje = puntajeFinal
            pantallaActual = "nuevoRecord"
        }, animales, peliculas, profesiones)

        "nuevoRecord" -> NuevoRecord(puntaje, { pantallaActual = "menu" })
        "sinRecord" -> SinRecord(puntaje, record, { pantallaActual = "menu" })
        "ajustes" -> Ajustes(tiempoJuego, { tiempoJuego = it }, { pantallaActual = "menu" }, animales, peliculas, profesiones)
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


@Composable fun Menu(record: Int, onSelectCategory: (String) -> Unit, onGoToSettings: () -> Unit) {}
@Composable fun Juego(categoria: String, tiempoPartida: Int, onFinish: (Int) -> Unit, animales: List<String>, peliculas: List<String>, profesiones: List<String>) {}
@Composable fun NuevoRecord(puntaje: Int, onBackToMenu: () -> Unit) {}
@Composable fun SinRecord(puntaje: Int, record: Int, onBackToMenu: () -> Unit) {}
@Composable fun Ajustes(tiempoPartida: Int, onTiempoChange: (Int) -> Unit, onBackToMenu: () -> Unit, animales: MutableList<String>, peliculas: MutableList<String>, profesiones: MutableList<String>) {}