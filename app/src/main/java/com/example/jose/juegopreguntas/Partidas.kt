package com.example.jose.juegopreguntas

import java.util.*

data class Partidas(var nick1: String ="",
                    var nick2: String =""){
    // contenedor para actualizar los datos
    val partidasHasMap = HashMap<String, Any>()

    fun crearHashMapPartidas() {
        partidasHasMap.put("nick1", nick1)
        partidasHasMap.put("nick2", nick2)
        partidasHasMap.put("pregunta_"+nick1,"")
        partidasHasMap.put("pregunta_"+nick2,"")
        partidasHasMap.put("respuesta_pregunta_"+nick1,"")
        partidasHasMap.put("respuesta_pregunta_"+nick2,"")


    }
}
