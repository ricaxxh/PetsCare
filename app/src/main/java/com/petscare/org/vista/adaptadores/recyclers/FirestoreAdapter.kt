package com.petscare.org.vista.adaptadores.recyclers

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener

abstract class FirestoreAdapter<VH: RecyclerView.ViewHolder>(
    private val query: Query
    ): RecyclerView.Adapter<VH>(), EventListener<QuerySnapshot> {

    private var registro: ListenerRegistration? = null
    private val items = ArrayList<DocumentSnapshot>()

    override fun onEvent(value: QuerySnapshot?, exception: FirebaseFirestoreException?) {

        //Mostrar si hay un error en el logcat
        if (exception != null){
            Log.e("OnEvent: Error", exception.toString())
            return
        }

        //Escuchar los eventos en FirestoreMascotas
        for(cambio in value!!.documentChanges){
            when(cambio.type){
                DocumentChange.Type.ADDED -> itemAgregado(cambio)
                DocumentChange.Type.MODIFIED -> itemModificado(cambio)
                DocumentChange.Type.REMOVED -> itemEliminado(cambio)
            }
        }
    }

    protected open fun itemAgregado(cambio: DocumentChange){
        items.add(cambio.newIndex,cambio.document)
        notifyItemInserted(cambio.newIndex)
    }

    protected open  fun itemModificado(cambio: DocumentChange){
        if (cambio.oldIndex == cambio.newIndex){
            items[cambio.oldIndex] = cambio.document
            notifyItemChanged(cambio.oldIndex)
        } else{
            items.removeAt(cambio.oldIndex)
            items.add(cambio.newIndex,cambio.document)
            notifyItemMoved(cambio.oldIndex,cambio.newIndex)
        }
    }

    protected open fun itemEliminado(cambio: DocumentChange){
        items.removeAt(cambio.oldIndex)
        notifyItemRemoved(cambio.oldIndex)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    protected open fun getItem(index: Int): DocumentSnapshot?{
        return items[index]
    }

    open fun startListener(){
        if (registro == null){
            registro = query.addSnapshotListener(this)
        }
    }

    open fun stopListener(){
        if (registro != null){
            registro!!.remove()
            registro = null
        }

        items.clear()
        notifyDataSetChanged()
    }
}
