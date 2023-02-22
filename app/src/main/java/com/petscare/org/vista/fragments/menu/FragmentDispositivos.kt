package com.petscare.org.vista.fragments.menu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.petscare.org.databinding.FragmentDispositivosBinding
import com.petscare.org.vista.activitys.ActivityAgregarDispositivoBT
import com.petscare.org.vista.adaptadores.recyclers.AdaptadorIOT

class FragmentDispositivos : Fragment() {

    private var _binding: FragmentDispositivosBinding? = null
    private val binding get() = _binding!!

    //private val device_list = ArrayList<Dispositivo>()
    private lateinit var devices_adapter: AdaptadorIOT
    private lateinit var database_ref:DatabaseReference

    //private var itemTouchHelper:ItemTouchHelper? = null

    override fun onStart() {
        super.onStart()
        devices_adapter.startListener()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDispositivosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mostrarUI()
        eventosUI()

    }

    private fun eventosUI() {
        binding.fabAgregarDispositivo.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityAgregarDispositivoBT::class.java))
        }
    }

    private fun mostrarUI() {

        val id_usuario = Firebase.auth.currentUser!!.uid
        database_ref = FirebaseDatabase.getInstance().getReference("USUARIOS/$id_usuario/DISPOSITIVOS_IOT")
        devices_adapter = AdaptadorIOT(requireContext(),database_ref)
        binding.recyclerDispositivos.layoutManager = GridLayoutManager(requireContext(),2)
        binding.recyclerDispositivos.adapter = devices_adapter

        database_ref.get().addOnSuccessListener {
            if (it.childrenCount > 0){
                binding.layoutDispositivos.visibility = View.VISIBLE
                binding.layoutNoDispositivos.visibility = View.GONE
            } else{
                binding.layoutDispositivos.visibility = View.GONE
                binding.layoutNoDispositivos.visibility = View.VISIBLE
            }
        }


        /* binding.recyclerDispositivos.layoutManager = GridLayoutManager(requireContext(),2)
        adaptador_dispositivos = AdaptadorIOT(requireContext(), device_list)*/

        /*val lista_dispositivos = mutableListOf(
            Dispositivo("Dispensador de Agua 1",TipoDispositivo.DISPENSADOR_AGUA.name,true,87f,false),
            Dispositivo("Dispensador de Alimento 2",TipoDispositivo.DISPENSADOR_ALIMENTO.name,false,null),
            Dispositivo("Puerta principal",TipoDispositivo.PUERTA.name,true,null,true),
            Dispositivo("Maquina propina",TipoDispositivo.MAQUINA_PROPINAS.name,true,127.50f,true),
            Dispositivo("Aire acondicionado",TipoDispositivo.AIRE_ACONDICIONADO.name,true,22.5f,true),
            Dispositivo("Secadora", TipoDispositivo.SECADORA.name,false,null),
            Dispositivo("Foco 1", TipoDispositivo.FOCO.name,true,null,false),
            Dispositivo("Maquina propina",TipoDispositivo.MAQUINA_PROPINAS.name,true,127.50f,true),
            Dispositivo("Aire acondicionado",TipoDispositivo.AIRE_ACONDICIONADO.name,true,22.5f,true),
            Dispositivo("Secadora", TipoDispositivo.SECADORA.name,false,null),
            Dispositivo("Foco 1", TipoDispositivo.FOCO.name,true,null,false)
        )

        binding.recyclerDispositivos.setHasFixedSize(true)
        binding.recyclerDispositivos.layoutManager = GridLayoutManager(requireContext(),2)
        adaptador_dispositivos = AdaptadorDispositivos(requireContext(),lista_dispositivos,object : OnStartDragListener{
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
                itemTouchHelper!!.startDrag(viewHolder!!)
            }

        })
        binding.recyclerDispositivos.adapter = adaptador_dispositivos
        val callback = MyItemTouchHelperCallback(adaptador_dispositivos)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper!!.attachToRecyclerView(binding.recyclerDispositivos)
        */

    }

    override fun onStop() {
        super.onStop()
        devices_adapter.stopListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}