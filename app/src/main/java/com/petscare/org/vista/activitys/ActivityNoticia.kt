package com.petscare.org.vista.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.petscare.org.R
import com.petscare.org.databinding.ActivityNoticiaBinding

class ActivityNoticia : AppCompatActivity() {

    private lateinit var binding: ActivityNoticiaBinding

    private var titulo_noticia:String? = null
    private var url_noticia: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.THEME_GLOBAL_APP)
        super.onCreate(savedInstanceState)
        binding = ActivityNoticiaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        llenarDatos()
        cargarNoticia()
        eventosUI()
    }

    private fun llenarDatos() {

        titulo_noticia = intent.extras!!.getString("titulo_noticia")!!
        url_noticia = intent.extras!!.getString("url_noticia")!!

        binding.txtTituloNoticiaWeb.text = titulo_noticia
        binding.txtUrlNoticiaWeb.text = url_noticia
    }

    private fun cargarNoticia() {
        binding.webNoticia.loadUrl(url_noticia!!)
    }

    private fun eventosUI(){
        binding.btnCerrarNoticia.setOnClickListener {
            finish()
        }

        binding.btnCompartirNoticiaWeb.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "Hola, mira esta noticia que encontre en la app de Petscare:\n $url_noticia")
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent,"Compartir mediante"))
        }
    }
}