package com.example.charadas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
        "menu" -> {}
        "cuenta" -> {}
        "juego" -> {}
        "nuevoRecord" -> {}
        "sinRecord" -> {}
        "ajustes" -> {}
    }
}