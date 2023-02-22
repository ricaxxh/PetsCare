package com.petscare.org.vista.fragments.menu

import androidx.recyclerview.widget.RecyclerView
import com.petscare.org.vista.adaptadores.recyclers.AdaptadorFeed
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.petscare.org.R
import com.petscare.org.modelo.objetos.Noticia
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petscare.org.databinding.FragmentFeedBinding
import java.util.ArrayList


class FragmentFeed : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var adaptador_feed: AdaptadorFeed

    override fun onStart() {
        super.onStart()
        adaptador_feed.startListener()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFeedBinding.inflate(inflater,container,false)
        return binding.root
    }

    //SE USA EL METODO ONVIEWCREATED, de manera que infla el recycler una vez el fragment este cargado
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val consulta = Firebase.firestore.collection("Noticias")

        adaptador_feed = AdaptadorFeed(requireContext(),consulta)
        binding.recyclerViewFeed.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFeed.adapter = adaptador_feed

    }

    override fun onStop() {
        super.onStop()
        adaptador_feed.stopListener()
    }
}