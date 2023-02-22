package com.petscare.org.vista.adaptadores.recyclers

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.petscare.org.R
import com.petscare.org.vista.activitys.ActivityNoticia

class AdaptadorFeed(private val context: Context, private val consulta: Query) :
    FirestoreAdapter<AdaptadorFeed.HolderFeed>(consulta) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFeed {
        return HolderFeed(LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false))
    }

    override fun onBindViewHolder(holder: HolderFeed, position: Int) {
        getItem(position)?.let { document ->
            holder.mostrarDatos(document)
        }
    }

    inner class HolderFeed(item_view: View) : RecyclerView.ViewHolder(item_view) {

        private val txt_titulo = item_view.findViewById<TextView>(R.id.txt_titulo_noticia)
        private val txt_autor = item_view.findViewById<TextView>(R.id.txt_autor)
        private val img_noticia = item_view.findViewById<ImageView>(R.id.img_noticia)
        private val icon_autor = item_view.findViewById<ImageView>(R.id.icon_autor)
        private val btn_compartir = item_view.findViewById<ImageButton>(R.id.btn_compartir)
        private val btn_favorito = item_view.findViewById<ToggleButton>(R.id.btn_favorito)

        fun mostrarDatos(document: DocumentSnapshot) {

            val titulo_noticia = document.getString("Titulo")
            val url_noticia = document.getString("Url")

            txt_titulo.text = titulo_noticia
            txt_autor.text = document.getString("Autor")

            Glide.with(context).load(document.getString("Imagen")).into(img_noticia)
            Glide.with(context).load(document.getString("Icono")).circleCrop().into(icon_autor)

            btn_compartir.setOnClickListener {

                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, "Hola, mira esta noticia que encontre en la app de Petscare:\n $url_noticia")
                intent.type = "text/plain"
                context.startActivity(Intent.createChooser(intent, "Compartir mediante"))
            }
            
            btn_favorito.setOnClickListener { 
                if (btn_favorito.isChecked) Toast.makeText(context, "Guardado en favoritos", Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, "Se elimino noticia de favoritos", Toast.LENGTH_SHORT).show()
            }

            itemView.setOnClickListener {

                val intent = Intent(context, ActivityNoticia::class.java)
                intent.putExtra("titulo_noticia", titulo_noticia)
                intent.putExtra("url_noticia", url_noticia)

                context.startActivity(Intent(intent))
            }
        }
    }
}