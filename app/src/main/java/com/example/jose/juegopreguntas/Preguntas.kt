package com.example.jose.juegopreguntas

import java.util.*

data class Preguntas(var pregunta: String ="", var respuesta: String =""){
    // contenedor para actualizar los datos
    val preguntasHasMap = HashMap<String, Any>()

    fun crearHashMapDatos() {
        preguntasHasMap.put("pregunta", pregunta)
        preguntasHasMap.put("respuesta", respuesta)


    }
}