package com.petscare.org.vista.fragments.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.content.Intent
import com.petscare.org.vista.activitys.ActivityAgregarMascota
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petscare.org.R
import com.petscare.org.databinding.FragmentMascotasBinding
import com.petscare.org.viewmodel.ViewModelMascota
import com.petscare.org.vista.adaptadores.recyclers.AdaptadorMascotas

class FragmentMascotas : Fragment() {

    private var _binding: FragmentMascotasBinding? = null
    private val binding get() = _binding!!
    private lateinit var adaptador_mascotas: AdaptadorMascotas

    override fun onStart() {
        super.onStart()
        adaptador_mascotas.startListener()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMascotasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mostrarRecycler()
        eventosUI()

    }

    private fun mostrarRecycler() {

        val id_usuario = Firebase.auth.currentUser!!.uid
        val consulta = Firebase.firestore.collection("Usuarios").document(id_usuario)
            .collection("Mascotas")

        adaptador_mascotas = AdaptadorMascotas(requireContext(), consulta)
        binding.recyclerMascotas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMascotas.adapter = adaptador_mascotas

        binding.recyclerMascotas.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) binding.fabAgregar.shrink()
                else binding.fabAgregar.extend()
            }
        })

        val swipe_item_callback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(direction == ItemTouchHelper.LEFT) Toast.makeText(requireContext(), "Izquierda", Toast.LENGTH_SHORT).show()
                else if (direction == ItemTouchHelper.RIGHT) Toast.makeText(requireContext(), "Derecha", Toast.LENGTH_SHORT).show()
            }

        }

        val itemTouchHelper = ItemTouchHelper(swipe_item_callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerMascotas)
    }

    private fun eventosUI() {
        binding.fabAgregar.setOnClickListener { view: View? ->
            startActivity(Intent(requireContext(), ActivityAgregarMascota::class.java))
        }
    }

    override fun onStop() {
        super.onStop()
        adaptador_mascotas.stopListener()
    }
}