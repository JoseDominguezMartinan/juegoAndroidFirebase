package com.example.jose.juegopreguntas

import com.example.juegopreguntas.R
import android.net.Uri
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.activity_vista_usuario.*

import kotlinx.android.synthetic.main.fragment_recibir_preguntas.*
import kotlinx.android.synthetic.main.fragment_responder_pregunta.*
import kotlinx.android.synthetic.main.fragment_vista_usuario.view.*

import com.google.firebase.database.*

/**
 * vista principal del juego
 * el juego consiste en un qien es quien
 * tenemos tres fragments donde:
 * 1. en el primer fragment obtenemos la respuesta a nuestra ultima pregunta
 * 2. en el segundo fragment respondemos lo que nos haya preguntado nuestro oponente
 * 3. en el tercer fragment enviamos la pregunta a nuestra oponente
 * el primero que acierte en que esta pensando su oponente gana
 */

class VistaUsuario : AppCompatActivity() , recibir_preguntas.OnFragmentInteractionListener , enviar_preguntas.OnFragmentInteractionListener , ResponderPregunta.OnFragmentInteractionListener {
// variable donde almacenaremos la referencia a la base de datos
    private var database: DatabaseReference? = null

// variables para almacenar los datos de la partida
    lateinit var username: String
    lateinit var contricante: String
    lateinit var aliasPartida: String

// variables necesarias para la comunicacion con los fragments para poder imprimir los datos
    var respuestaPregunta = ""
    var preguntaEnviar = ""
    var respuestaRecibir=""



    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_usuario)
        // obtenemos del intent los datos introducidos en el login del juego
        username = intent.getStringExtra("username")
        contricante = intent.getStringExtra("username2")
        aliasPartida = intent.getStringExtra("alias")
        // obtenemos la referencia a la base de datos
        database = FirebaseDatabase.getInstance().getReference("/partidas/" + aliasPartida)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        // cuando pulsamos el boton de la plantilla se desencadenara una accion segun el fragment en el que estemos
        fab.setOnClickListener { view ->

            when(container.currentItem){
                0->{
                    // en este fragment solo se reciben datos cuando se modifican en la base de datos, por lo que solo mostramos un mensaje
                    Snackbar.make(view, "recibiendo ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
                1->{
                    // enviamos a la base de datos la repsuesta a la pregunta que se nos ha formulado
                    Snackbar.make(view, "Enviada respuestsa con exito ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    var miHashMapChild = HashMap<String, Any>()
                    // enviamos la respuesta a la pregunta del otro jugador
                    miHashMapChild.put("respuesta_pregunta_"+contricante,respuestaPregunta)
                    database!!.updateChildren(miHashMapChild)



                }
                2->{
                    // enviamos a la base de datos una pregunta para que nos sea respondida
                    Snackbar.make(view, "Enviada pregunta con exito ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    var miHashMapChild = HashMap<String, Any>()

                    // enviamos la pregunta que haya escrito el usuario

                    miHashMapChild.put("pregunta_" + username, preguntaEnviar)

                    // actualizamos el child
                    database!!.updateChildren(miHashMapChild)

                }
                else ->{

                }
            }

        }
        initListener()
    }
// estas funciones podran ser llamadas desde los fragments , para por ejemplo cambiar
    // el valor de las variables con los botones propios de cada fragment
    fun setVariable(valor: String){
        respuestaPregunta = valor
    }
    fun setPregunta(valor: String){
        preguntaEnviar = valor
    }
    fun setRespuesta(valor: String){
        respuestaRecibir = valor
    }
    private fun initListener() {
        val childEventListener = object : ChildEventListener {
            override fun onChildRemoved(p0: DataSnapshot) {
                Log.d("tag", "Datos borrados: " + p0.key)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.d("tag", "Datos movidos")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                // onChildAdded() capturamos la key
                Log.d("tag", "Datos añadidos: " + p0.key)

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("tag", "Error cancelacion")
            }
        }
        // attach el evenListener a la basededatos
        database!!.addChildEventListener(childEventListener)
        /**
         * cuando tenemos cambios en la base de datos lo mostramos
         * en el lugar correspondiente
         */

        database!!.child("pregunta_"+contricante).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val pregunta = snapshot.getValue().toString()
                preguntaResponder.setText(pregunta)
                setPregunta(pregunta)
            }


            override fun onCancelled(databaseError: DatabaseError) {}

        })
        // cuando se cambia la respuesta a la pregunta se actualiza

        database!!.child("respuesta_pregunta_"+username).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val respuesta = snapshot.getValue().toString()
                respuestasPreguntas.setText(respuesta)
                setRespuesta(respuesta)
            }


            override fun onCancelled(databaseError: DatabaseError) {}

        })
        database!!.child("pregunta_"+username).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val pregunta2 = snapshot.getValue().toString()
                anteriorPregunta.setText(pregunta2)
            }


            override fun onCancelled(databaseError: DatabaseError) {}

        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.menu_vista_usuario, menu)
            return true
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            val id = item.itemId

            if (id == R.id.action_settings) {
                return true
            }

            return super.onOptionsItemSelected(item)
        }


            /**
             * A [FragmentPagerAdapter] that returns a fragment corresponding to
             * one of the sections/tabs/pages.
             */
            inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {


            override fun getItem(position: Int): Fragment {
                // getItem is called to instantiate the fragment for the given page.
                // Return a PlaceholderFragment (defined as a static inner class below).
                return PlaceholderFragment.newInstance(position + 1)
            }

            override fun getCount(): Int {
                // Show 3 total pages.
                return 3
            }

        }

                /**
                 * A placeholder fragment containing a simple view.
                 */
     class PlaceholderFragment : Fragment () {




            override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                      savedInstanceState: Bundle?): View? {


                val rootView = inflater.inflate(R.layout.fragment_vista_usuario, container, false)
                rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
                return rootView
            }


            companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"
            var fragmentActual = 0
            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
                /**
                 * funcion donde se instancian los fragments en la zona de la plantilla
                 * que queramos, la plantilla recordamos que tiene tres zonas que se intercambian
                 * deslizando el dedo entre ellas, mostrando un fragment u otro
                 */
            fun newInstance(sectionNumber: Int): Fragment {
                lateinit var fragment: Fragment
                when (sectionNumber) {
                    1 ->{

                        fragment = recibir_preguntas.newInstance()

                    }

                    2 ->{
                        fragment = ResponderPregunta.newInstance()

                    }

                    3 ->{
                        fragment = enviar_preguntas.newInstance()

                    }


                }


                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)

                return fragment
            }

        }
        }
    }

