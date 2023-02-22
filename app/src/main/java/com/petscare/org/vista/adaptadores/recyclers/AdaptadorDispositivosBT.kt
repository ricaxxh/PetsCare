package com.petscare.org.vista.adaptadores.recyclers

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.petscare.org.R
import com.petscare.org.vista.Interfaces.OnItemClickListener

class AdaptadorDispositivosBT(private val context: Context, private val lista_dispositivos_bt: Set<BluetoothDevice>, private val listener: OnItemClickListener)
    : RecyclerView.Adapter<AdaptadorDispositivosBT.HolderDispositivosBT>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorDispositivosBT.HolderDispositivosBT {
        return HolderDispositivosBT(LayoutInflater.from(parent.context).inflate(R.layout.item_bluethoth_device,parent,false))
    }

    override fun onBindViewHolder(
        holder: AdaptadorDispositivosBT.HolderDispositivosBT, position: Int) {
        holder.mostrarDatos(lista_dispositivos_bt.elementAt(position))
    }

    override fun getItemCount(): Int {
        return lista_dispositivos_bt.size
    }

    inner class HolderDispositivosBT(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val txt_nombre = itemView.findViewById<TextView>(R.id.txt_nombre_bt)
        private val txt_mac = itemView.findViewById<TextView>(R.id.txt_mac)

        fun mostrarDatos(dispositivo_bt : BluetoothDevice) {

            itemView.setOnClickListener {
                listener.onItemClick(dispositivo_bt)
            }

            txt_nombre.text = dispositivo_bt.name
            txt_mac.text = dispositivo_bt.address
        }
    }
}