package com.petscare.org.domain.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petscare.org.modelo.objetos.Mascota

class FirestoreData {

    private lateinit var listener: ListenerRegistration
    private val id_usuario = Firebase.auth.currentUser!!.uid
    private val consulta = Firebase.firestore.collection("Usuarios")
        .document(id_usuario).collection("Mascotas")

    fun getListaMascotas(): LiveData<MutableList<Mascota>> {

        val ldata_mascotas = MutableLiveData<MutableList<Mascota>>()

        listener = consulta.addSnapshotListener{ result, error ->

            if (error != null){
                Log.e("FIRESTORE ERROR:", error.message!!)
            }

            val lista_mascota = mutableListOf<Mascota>()

            for (document in result!!){
                val id = id_usuario
                val nombre = document.getString("Nombre")
                val tipo = document.getString("Tipo")
                val raza = document.getString("Raza")
                val foto = document.getString("Foto")
                val mascota = Mascota(id, nombre, tipo, raza, null,null, foto)
                lista_mascota.add(mascota)
            }

            ldata_mascotas.value = lista_mascota

        }

        return ldata_mascotas
    }
}





