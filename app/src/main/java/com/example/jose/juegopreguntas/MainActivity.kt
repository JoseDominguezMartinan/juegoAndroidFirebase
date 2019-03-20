package com.example.jose.juegopreguntas


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.util.Log
import android.widget.Toast

import com.example.juegopreguntas.R

import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.activity_main.*

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ChildEventListener

/**
 * clase principal donde ingresaremos el nombre del jugador
 * y del contricante con el que queremos jugar
 * por cada usuario se almacenara en la base de datos su nombre y token
 * y por cada partida se almacenara las preguntas que se van enviando y sus respuestas
 * por cada nueva pregunta se sustituye por la anterior
 */
class MainActivity : AppCompatActivity() {
    // para filtrar los logs
    val TAG = "Servicio"

    // referencia de la base de datos
    /**
     * almacenaremos en las siguientes variables las dos referencias que tenemos  para la
     * base de datos, database donde almacenamos las partidas y database jugadores donde almacenamos
     * el nombre con su respectivo token

     */

    private var database: DatabaseReference? = null
    private var databaseJugadores: DatabaseReference? = null


    // para guardar los cambios de la base de datos

    private var FCMToken: String? = null
    lateinit var misPartidas: Partidas
    lateinit var misDatos: Datos


    // key unica creada automaticamente al añadir un child
    lateinit var key: String

    // para actualizar los datos necesito un hash map, crearemos uno por cada clase de datos que he creado
    val miHashMapChild = HashMap<String, Any>()
    val partidasHasMapChild = HashMap<String, Any>()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        // referencia a la base de datos del proyecto en firebase


        database = FirebaseDatabase.getInstance().getReference("/partidas")
        databaseJugadores = FirebaseDatabase.getInstance().getReference("/jugadores")

        /**
         * recogemos los nick de los participantes y sus correspondientes token,
         * asi como un alias para identificar la partida,
         * los datos de la partida deberan estar debidamente cubiertos
         */
        enviarpartida.setOnClickListener() {
            if (!nicktext1.text.toString().isEmpty() && !nicktext2.text.toString().isEmpty() && !alias.text.toString().isEmpty()) {

                // insertamos una nueva partida:

                misPartidas = Partidas(nicktext1.text.toString(), nicktext2.text.toString())
                misPartidas.crearHashMapPartidas()
                partidasHasMapChild.put(alias.text.toString(), misPartidas.partidasHasMap)
                database!!.updateChildren(partidasHasMapChild)

                // Si es la primera vez que iniciamos la sesion añadimos al jugador en el registro de jugadores

                if (savedInstanceState == null) {
                    try {
                        // Obtengo el token del dispositivo.
                        FCMToken = FirebaseInstanceId.getInstance().token
                        // creamos una entrada nueva en el child con key el alias del jugador
                        key = nicktext1.text.toString()

                        misDatos = Datos(FCMToken.toString(), android.os.Build.MANUFACTURER + " " + android.os.Build.ID)
                        // creamos el hash map
                        misDatos.crearHashMapDatos()
                        // guardamos los datos en el hash map para la key creada anteriormente
                        miHashMapChild.put(key.toString(), misDatos.miHashMapDatos)
                        // actualizamos el child
                        databaseJugadores!!.updateChildren(miHashMapChild)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d(TAG, "Error escribiendo datos ${e}")
                    }

                    // abrimos la nueva activity donde miraremos todo

                    val intento1 = Intent(this, VistaUsuario::class.java)

                    intento1.putExtra("username",nicktext1.text.toString())
                    intento1.putExtra("username2",nicktext2.text.toString())
                    intento1.putExtra("alias",alias.text.toString())

                    startActivity(intento1)
                }

                } else {
                    Toast.makeText(this, "¡Oye!, cubre todos los datos", Toast.LENGTH_SHORT).show()
                }


        }
        // inicializo el listener para los eventos de la basededatos
        initListener()
    }

    /**
     * Listener para los distintos eventos de la base de datos
     */
    private fun initListener() {
        val childEventListener = object : ChildEventListener {
            override fun onChildRemoved(p0: DataSnapshot) {
                Log.d(TAG, "Datos borrados: " + p0.key)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                // creo un objeto para recojer los datos cambiados
                var misDatosCambiados = Datos("", "")
                // introduzco en el objeto los datos cambiados que vienen en el snapdhot
                misDatosCambiados = p0.getValue(Datos::class.java)!!
                // muestro datos desde el objeto
                Log.d(TAG, "Datos cambiados: " + misDatosCambiados.token + " " + misDatosCambiados.dispositivo)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.d(TAG, "Datos movidos")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                // onChildAdded() capturamos la key
                Log.d(TAG, "Datos añadidos: " + p0.key)

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "Error cancelacion")
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // Inflate the menu_main; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Handle action bar item clicks here. The action bar will

        // automatically handle clicks on the Home/Up button, so long

        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {

            R.id.action_settings -> true

            else -> super.onOptionsItemSelected(item)

        }
    }
}