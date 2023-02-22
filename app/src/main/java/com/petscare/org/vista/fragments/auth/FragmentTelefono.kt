package com.petscare.org.vista.fragments.auth

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.petscare.org.R
import com.petscare.org.databinding.FragmentTelefonoBinding
import com.petscare.org.modelo.objetos.Pais
import com.petscare.org.viewmodel.ViewModelAuth
import com.petscare.org.vista.Interfaces.AdminDataFragments
import com.petscare.org.vista.Interfaces.OnFragmentNavigationListener
import com.petscare.org.vista.adaptadores.dialogos.AdaptadorSelectorPaises

class FragmentTelefono : Fragment(), AdminDataFragments{

    private val vmAuth: ViewModelAuth by activityViewModels()
    private var _binding: FragmentTelefonoBinding? = null
    private val binding get() = _binding!!
    private lateinit var change_fragment_listener : OnFragmentNavigationListener

    private lateinit var telefono: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        change_fragment_listener = context as OnFragmentNavigationListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTelefonoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observar_ldata()
        eventosUI()
    }

    private fun observar_ldata() {
        vmAuth.ldata_auth.observe(viewLifecycleOwner, Observer { ldata_Auth ->
            binding.btnLada.setText(ldata_Auth.lada)
            binding.ctxTelefono.editText?.setText(ldata_Auth.telefono)
        })
    }

    private fun eventosUI() {
        binding.btnCodigo.setOnClickListener { verificarCampos() }
        binding.btnLada.setOnClickListener { mostrarSelectorLada() }
    }

    private fun mostrarSelectorLada() {
        val lista_paises = ArrayList<Pais>()
        lista_paises.add(Pais("México","52", R.drawable.ic_mexico))
        lista_paises.add(Pais("Estados Unidos","1", R.drawable.ic_estados_unidos))
        lista_paises.add(Pais("Alemania","49", R.drawable.ic_alemania))
        lista_paises.add(Pais("Argentina","54", R.drawable.ic_argentina))
        lista_paises.add(Pais("Brasil","55", R.drawable.ic_brasil))
        lista_paises.add(Pais("Canada","1", R.drawable.ic_canada))
        lista_paises.add(Pais("Chile","56", R.drawable.ic_chile))
        lista_paises.add(Pais("China","86", R.drawable.ic_china))
        lista_paises.add(Pais("Colombia","57", R.drawable.ic_colombia))
        lista_paises.add(Pais("Corea del sur","82", R.drawable.ic_corea_del_sur))
        lista_paises.add(Pais("Costa Rica","506", R.drawable.ic_costa_rica))
        lista_paises.add(Pais("Ecuador","593", R.drawable.ic_ecuador))
        lista_paises.add(Pais("España","34", R.drawable.ic_espana))
        lista_paises.add(Pais("Francia","33", R.drawable.ic_francia))
        lista_paises.add(Pais("Inglaterra","44", R.drawable.ic_inglaterra))
        lista_paises.add(Pais("Italia","39", R.drawable.ic_italia))
        lista_paises.add(Pais("Japon","81", R.drawable.ic_japon))
        lista_paises.add(Pais("Panama","507", R.drawable.ic_panama))
        lista_paises.add(Pais("Paraguay","595", R.drawable.ic_paraguay))
        lista_paises.add(Pais("Peru","51", R.drawable.ic_peru))
        lista_paises.add(Pais("Portugal","351", R.drawable.ic_portugal))
        lista_paises.add(Pais("Rusia","7", R.drawable.ic_rusia))
        lista_paises.add(Pais("Uruguay","598", R.drawable.ic_uruguay))
        lista_paises.add(Pais("Venezuela","58", R.drawable.ic_venezuela))

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Elige tu país")
            .setAdapter(
                AdaptadorSelectorPaises(requireActivity(),
                    R.layout.items_selector_paises,lista_paises)
            ) { dialogInterface, index ->
                binding.btnLada.text = "+".plus(lista_paises.get(index).lada)

            }
            .show()
    }

    override fun verificarCampos() {
        telefono = binding.ctxTelefono.editText?.text.toString()
        if (telefono.isNotEmpty()){
            if (telefono.length == 10){
                salvarDatos()
                change_fragment_listener.mostrarFragment(1)
            } else{
                binding.ctxTelefono.error = "Mínimo 10 dígitos"
            }
        } else{
            binding.ctxTelefono.error = "Ingrese el teléfono"
        }
    }

    override fun onPause() {
        super.onPause()
        salvarDatos()
    }

    override fun salvarDatos() {
        vmAuth.setLada(binding.btnLada.text.toString())
        vmAuth.setTelefono(binding.ctxTelefono.editText?.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}