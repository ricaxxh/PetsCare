package com.petscare.org.vista.fragments.registro

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.petscare.org.databinding.FragmentNombreBinding
import com.petscare.org.viewmodel.ViewModelRegistro
import com.petscare.org.vista.Interfaces.AdminDataFragments
import com.petscare.org.vista.Interfaces.OnFragmentNavigationListener


class FragmentNombre : Fragment(), AdminDataFragments {
    private val vmRegistro: ViewModelRegistro by activityViewModels()
    private lateinit var change_fragment_listener: OnFragmentNavigationListener
    private var _binding: FragmentNombreBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        change_fragment_listener = context as OnFragmentNavigationListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNombreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observarLiveData()
    }

    private fun observarLiveData() {
        vmRegistro.ldata_registro.observe(viewLifecycleOwner, { ldata_registro ->
            binding.ctxNombre.editText?.setText(ldata_registro.nombre)
            binding.ctxApellidos.editText?.setText(ldata_registro.apellidos)
        })
    }

    override fun verificarCampos() {
        val bool_nombre: Boolean;
        val bool_apellidos: Boolean

        if (binding.ctxNombre.editText?.text.toString().isNotEmpty()) {                             //Verifica que el campo de texto nombre no esta vacio
            bool_nombre = true
            binding.ctxNombre.error = null
        } else {
            bool_nombre = false
            binding.ctxNombre.error = "Ingrese el nombre"
        }

        if (binding.ctxApellidos.editText?.text.toString().isNotEmpty()) {                          //Verifica que el campo de texto apellidos no esta vacio
            bool_apellidos = true
            binding.ctxApellidos.error = null
        } else {
            bool_apellidos = false
            binding.ctxApellidos.error = "Ingrese los apellidos"
        }

        return if (bool_nombre && bool_apellidos) {                                                 //Comprueba si los 2 campos de texto cumplieron con las restricciones
            salvarDatos()
            change_fragment_listener.mostrarFragment(1)
        } else {
            salvarDatos()
        }
    }

    override fun salvarDatos() {
        vmRegistro.setNombre(binding.ctxNombre.editText?.text.toString())
        vmRegistro.setApellidos(binding.ctxApellidos.editText?.text.toString())
    }

    override fun onPause() {
        salvarDatos()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}