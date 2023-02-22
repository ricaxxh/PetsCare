package com.petscare.org.vista.adaptadores.recyclers

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DatabaseReference
import com.petscare.org.R
import com.petscare.org.domain.providers.TipoDispositivo
import com.petscare.org.modelo.objetos.Dispositivo
import com.petscare.org.vista.activitys.ActivityDeviceControl

class AdaptadorIOT(
    private val context: Context, db_reference: DatabaseReference):
    DatabaseAdapter<AdaptadorIOT.HolderIOT>(db_reference) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderIOT {
        return HolderIOT(
            LayoutInflater.from(context).inflate(R.layout.item_dispositivo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HolderIOT, position: Int) {
        getItem(position)?.let { dataSnapshot ->
            val dispositivo = dataSnapshot.getValue(Dispositivo::class.java)
            holder.mostrarDatos(dispositivo!!)
        }
    }

    inner class HolderIOT(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val card_device = itemView.findViewById<MaterialCardView>(R.id.card_device)
        private val txt_name = itemView.findViewById<TextView>(R.id.txt_nombre_dispositivo)
        private val icon_device = itemView.findViewById<ImageView>(R.id.icon_dispositivo)
        private val txt_state = itemView.findViewById<TextView>(R.id.txt_estado)

        fun mostrarDatos(dispositivo: Dispositivo) {
            txt_name.text = dispositivo.nombre

            var drawable: Drawable? = null

            when (dispositivo.tipo) {
                TipoDispositivo.DISPENSADOR_ALIMENTO.name -> {
                    drawable =
                        ContextCompat.getDrawable(context, R.drawable.ic_dispensador_alimento)
                    icon_device.background = drawable
                    txt_state.text = "${dispositivo.estado}".plus("%")
                }

                TipoDispositivo.FOCO.name -> {
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_foco)
                    icon_device.background = drawable
                    if (dispositivo.accionar == true) {
                        DrawableCompat.setTint(drawable!!, context.getColor(R.color.amarillo))
                        txt_state.text = "Encendido"
                    } else if (dispositivo.accionar == false) {
                        DrawableCompat.setTint(drawable!!, context.getColor(R.color.gris))
                        txt_state.text = "Apagado"
                    }
                }
            }

            card_device.setOnClickListener {

                val intent = Intent(context,ActivityDeviceControl::class.java)
                intent.putExtra("device_name",dispositivo.nombre)
                intent.putExtra("device_type",dispositivo.tipo)
                context.startActivity(intent)
            }
        }
    }
}

