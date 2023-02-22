package com.petscare.org.vista.fragments.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petscare.org.databinding.FragmentPerfilBinding
import com.petscare.org.vista.activitys.ActivityInicio

class FragmentPerfil : Fragment() {

    private var binding: FragmentPerfilBinding? = null
    private var id_usuario: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mostrarFotoNombre()
        eventosUI()
    }

    private fun mostrarFotoNombre() {

        id_usuario = Firebase.auth.currentUser!!.uid

        binding!!.txtIdUsuario.text = id_usuario

        Firebase.firestore.collection("Usuarios").document(id_usuario!!).get()
            .addOnSuccessListener { document ->
                binding!!.txtNombreUsuario.setText(document.getString("Nombre").plus(" " + document.getString("Apellidos")))
                //Glide.with(requireContext()).load(document.getString())
            }
    }

    private fun eventosUI() {

        /*binding.btnCerrarSesion.setOnClickListener(view -> {
            if (InternetUtil.Companion.verificarConexionInternet(requireContext())){
                cerrarSesion();
            } else {
                Toast.makeText(requireContext(),"Comprueba tu conexíon a internet",Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnPrueba.setOnClickListener(view ->{

        });*/
    }

    private fun cerrarSesion() {
        val firebaseAuth: FirebaseAuth
        val auth_listener = AuthStateListener { auth: FirebaseAuth ->
            if (auth.currentUser == null) {
                startActivity(Intent(context, ActivityInicio::class.java))
                requireActivity().finish()
            }
        }
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.addAuthStateListener(auth_listener)
        firebaseAuth.signOut()
    }
}