package com.example.jose.juegopreguntas

import java.util.*

data class Partidas(var nick1: String ="",
                    var nick2: String ="", var aciertos1: Int, var aciertos2: Int){
    // contenedor para actualizar los datos
    val partidasHasMap = HashMap<String, Any>()

    fun crearHashMapPartidas() {
        partidasHasMap.put("nick1", nick1)
        partidasHasMap.put("nick2", nick2)
        partidasHasMap.put("aciertos1",aciertos1)
        partidasHasMap.put("aciertos2",aciertos2)


    }
}
