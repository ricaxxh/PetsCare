package com.petscare.org.utilidades

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.petscare.org.modelo.objetos.Dispositivo

class FirebaseHelper(private val db_reference: DatabaseReference) {

    private val device_list = ArrayList<Dispositivo>()

    fun mostrarDispositivos(): ArrayList<Dispositivo>{
        db_reference.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {


            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return device_list
    }

    private fun obtenerDatos(dataSnapshot: DataSnapshot){
        device_list.clear()
        for (data in dataSnapshot.children){

        }
    }
}