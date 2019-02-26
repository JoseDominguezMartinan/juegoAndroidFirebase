package com.example.jose.juegopreguntas


import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.util.Log

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


class MainActivity : AppCompatActivity() {
    // para filtrar los logs
    val TAG = "Servicio"

    // referencia de la base de datos

    private var database: DatabaseReference? = null
    private var database2: DatabaseReference? = null
    private var database3: DatabaseReference? = null

    // para guardar los cambios de la base de datos

    private var FCMToken: String? = null
    lateinit var misDatos: Datos
    lateinit var misPartidas: Partidas
    lateinit var mispreguntas: Preguntas

    // key unica creada automaticamente al añadir un child
    lateinit var key: String

    // para actualizar los datos necesito un hash map, crearemos uno por cada clase de datos que he creado
    val miHashMapChild = HashMap<String, Any>()
    val partidasHasMapChild = HashMap<String, Any>()
    val preguntasHasMapChild = HashMap<String, Any>()

    // variables donde almacenaremos los datos
     var respuestaJugador: String=""
     var mensaje: String=""
     var respuesta: String=""
     var acierto: String=""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        // referencia a la base de datos del proyecto en firebase

        database = FirebaseDatabase.getInstance().getReference("/dispositivos")
        database2=FirebaseDatabase.getInstance().getReference("/Preguntas")
        database3=FirebaseDatabase.getInstance().getReference("/Partidas")


        // botones de la respuesta:
        boton1.setOnClickListener(){
            respuestaJugador="true"
            if(respuestaJugador.equals(respuesta)){
                mensaje="has acertado la pregunta"
                acierto="true"
            }

            else{
                mensaje="has fallado la pregunta"
                acierto="false"
            }


        }

        boton2.setOnClickListener(){
            respuestaJugador="false"
            if(respuestaJugador.equals(respuesta)){
                mensaje="has acertado la pregunta"
                acierto="true"
            }

            else{
                mensaje="has fallado la pregunta"
                acierto="false"
            }
        }

        // boton de la plantilla

        fab.setOnClickListener { view ->

            Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG)

                    .setAction("Action", null).show()
            Log.d(TAG, "Actualizando datos")


            // Creamos el hashMap en el objeto
            misDatos = Datos(FCMToken.toString(), android.os.Build.MANUFACTURER + " " + android.os.Build.ID,acierto)
            misDatos.crearHashMapDatos()
            // actualizamos la base de datosw
            miHashMapChild.put(key.toString(), misDatos.miHashMapDatos)
            // actualizamos el child
            database!!.updateChildren(miHashMapChild)
        }

        // Obtengo el token del dispositivo

        if (savedInstanceState == null) {
            try {
                // Obtengo el token del dispositivo.
                FCMToken = FirebaseInstanceId.getInstance().token
                // creamos una entrada nueva en el child "dispositivos" con un key unico automatico
                key = database!!.push().key!!

                misDatos = Datos(FCMToken.toString(), android.os.Build.MANUFACTURER + " " + android.os.Build.ID,acierto)
                // creamos el hash map
                misDatos.crearHashMapDatos()
                // guardamos los datos en el hash map para la key creada anteriormente
                miHashMapChild.put(key.toString(), misDatos.miHashMapDatos)
                // actualizamos el child
                database!!.updateChildren(miHashMapChild)

                // insertamos una nueva partida:
                misPartidas = Partidas(nicktext1.getText().toString(),nicktext2.getText().toString(),0 , 0);
                misPartidas.crearHashMapPartidas()
                partidasHasMapChild.put("partida1",misPartidas.partidasHasMap)
                database!!.updateChildren(miHashMapChild)

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "Error escribiendo datos ${e}")
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
                var misDatosCambiados = Datos("", "","")
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
        // attach el evenListener a la basededatos
        database2!!.addChildEventListener(childEventListener)


        database2!!.child("pregunta1").child("pregunta").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val pregunta = snapshot.getValue().toString()

                miText.text = pregunta

            }


            override fun onCancelled(databaseError: DatabaseError) {}

        })
        database2!!.child("pregunta1").child("respuesta").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                 respuesta = snapshot.getValue().toString()





            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
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