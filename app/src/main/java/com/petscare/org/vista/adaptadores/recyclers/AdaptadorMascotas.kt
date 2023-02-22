package com.petscare.org.vista.adaptadores.recyclers

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.petscare.org.R
import com.petscare.org.modelo.objetos.Mascota

class AdaptadorMascotas(private val context: Context, consulta: Query): FirestoreAdapter<AdaptadorMascotas.HolderMascotas>(consulta) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderMascotas {
        val view = LayoutInflater.from(context).inflate(R.layout.item_mascota,parent,false)
        return HolderMascotas(view)
    }

    override fun onBindViewHolder(holder: HolderMascotas, position: Int) {
        getItem(position)?.let { document ->
            holder.mostrarDatos(document)
        }
    }

    inner class HolderMascotas(vista_item: View): RecyclerView.ViewHolder(vista_item) {

        private val txt_nombre = vista_item.findViewById<TextView>(R.id.txt_nombre)
        private val txt_tipo = vista_item.findViewById<TextView>(R.id.txt_tipo)
        private val txt_raza = vista_item.findViewById<TextView>(R.id.txt_raza)
        private val img_foto = vista_item.findViewById<ImageView>(R.id.img_foto_mascota)

        fun mostrarDatos(document: DocumentSnapshot?) {
            val mascota = createMascota(document)
            txt_nombre.text = mascota.nombre
            txt_tipo.text = "Mascota: ".plus(mascota.tipo)
            txt_raza.text = "Raza: ".plus(mascota.raza)
            Glide.with(context).load(mascota.foto).circleCrop().into(img_foto)

            img_foto.setOnClickListener {
                val dialogo = Dialog(context)
                dialogo.setContentView(R.layout.dialogo_foto)
                val img_foto = dialogo.findViewById<ImageView>(R.id.img_foto_dm)
                val txt_nombre = dialogo.findViewById<TextView>(R.id.txt_nombre_dm)
                Glide.with(context).load(mascota.foto).into(img_foto)
                txt_nombre.text = mascota.nombre
                dialogo.show()
            }
        }
    }

    private fun createMascota(document: DocumentSnapshot?): Mascota {
        val nombre = document!!.getString("Nombre")
        val tipo = document.getString("Tipo")
        val raza = document.getString("Raza")
        val foto = document.getString("Foto")
        return Mascota(null,nombre,tipo,raza,null,null,foto)
    }
}