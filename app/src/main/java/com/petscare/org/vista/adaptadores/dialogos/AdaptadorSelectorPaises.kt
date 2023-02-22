package com.petscare.org.vista.adaptadores.dialogos

import android.app.Activity
import com.petscare.org.modelo.objetos.Pais
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import com.petscare.org.R
import android.widget.TextView
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat

class AdaptadorSelectorPaises(private val actividad: Activity, layout: Int, private val lista_items: ArrayList<Pais>)
    : ArrayAdapter<Pais>(actividad, layout, lista_items) {
    override fun getCount(): Int {
        return lista_items.size
    }

    override fun getItem(poscision: Int): Pais {
        return lista_items[poscision]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var fila = convertView
        val inflater = LayoutInflater.from(context)
        if (fila == null) {
            fila = inflater.inflate(R.layout.items_selector_paises, parent, false)
        }

        val txt_nombre_pais = fila!!.findViewById<TextView>(R.id.txt_nombre_pais)
        txt_nombre_pais.text = lista_items.get(position).nombre_pais

        val txt_lada = fila.findViewById<TextView>(R.id.txt_lada)
        txt_lada.setText("+".plus(lista_items.get(position).lada))

        val img_bandera = fila.findViewById<ImageView>(R.id.img_bandera)
        val icono = ContextCompat.getDrawable(actividad, lista_items.get(position).icon)
        img_bandera.setImageDrawable(icono)

        return fila
    }
}