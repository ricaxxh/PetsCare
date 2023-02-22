package com.petscare.org.vista.adaptadores.recyclers

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentSnapshot

abstract class DatabaseAdapter<VH : RecyclerView.ViewHolder>(private val db_reference: DatabaseReference) :
    RecyclerView.Adapter<VH>(), ChildEventListener {

    private var listener: ChildEventListener? = null
    private val items = ArrayList<DataSnapshot>()

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        items.add(0,snapshot)
        notifyItemInserted(0)
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        for((pos, item) in items.withIndex()){
            if (snapshot.key == item.key ){
                items[pos] = snapshot
                notifyItemChanged(pos)
            }
        }
    }

    override fun onCancelled(error: DatabaseError) {

    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        for ((pos,item) in items.withIndex()){
            if (snapshot.key == item.key){
                items.removeAt(pos)
                notifyItemRemoved(pos)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    protected open fun getItem(index: Int): DataSnapshot?{
        return items[index]
    }

    open fun startListener(){
        if (listener == null){
            listener = db_reference.addChildEventListener(this)
        }
    }

    open fun stopListener(){
        if (listener != null){
            db_reference.removeEventListener(listener!!)
            listener = null
        }

        items.clear()
        notifyDataSetChanged()
    }
}