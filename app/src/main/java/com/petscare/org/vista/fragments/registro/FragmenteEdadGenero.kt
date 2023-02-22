package com.petscare.org.vista.fragments.registro

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.petscare.org.R
import com.petscare.org.databinding.FragmentEdadGeneroBinding
import com.petscare.org.utilidades.KeyboardUtil
import com.petscare.org.viewmodel.ViewModelRegistro
import com.petscare.org.vista.Interfaces.AdminDataFragments
import com.petscare.org.vista.Interfaces.OnFragmentNavigationListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FragmenteEdadGenero : Fragment(), AdminDataFragments {

    private val vmRegistro: ViewModelRegistro by activityViewModels()
    private lateinit var change_fragment_listener: OnFragmentNavigationListener
    private var _binding: FragmentEdadGeneroBinding? = null
    private val binding get() = _binding!!

    private val formatear_fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEdadGeneroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventosUI()
        observar_ldata()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        change_fragment_listener = context as OnFragmentNavigationListener
    }

    private fun observar_ldata() {
        vmRegistro.ldata_registro.observe(viewLifecycleOwner, { ldata_registro ->
                binding.ctxFechaNacimiento.editText?.setText(ldata_registro.fecha_nacimiento)
                binding.ctxGenero.editText?.setText(ldata_registro.genero)
            })
    }

    private fun eventosUI() {

        binding.ctxFechaNacimiento.setStartIconOnClickListener { mostrarSelectorFecha() }
        //Formatear la fecha en tiempo real
        binding.ctxFechaNacimiento.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(texto: CharSequence?, start: Int, before: Int, count: Int) {
                if (texto?.length == 2 && start == 1) {
                    binding.ctxFechaNacimiento.editText?.text?.append("/")
                } else if (texto?.length == 5 && start == 4) {
                    binding.ctxFechaNacimiento.editText?.text?.append("/")
                }
            }
        })

        binding.spinnerGenero.setOnFocusChangeListener { view, has_focus ->
            if (has_focus){
                KeyboardUtil.cerrarTeclado(binding.root)
                //Llenar el espinner genero con sus valores
                val lista_genero = listOf("Masculino", "Femenino")
                val adapter_genero = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, lista_genero)
                binding.spinnerGenero.setAdapter(adapter_genero)
            }
        }
    }

    private fun mostrarSelectorFecha() {
        val constraintsBuilder = CalendarConstraints.Builder().setEnd(MaterialDatePicker.todayInUtcMilliseconds())
        val calendario = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Fecha de nacimiento")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
        calendario.show(requireActivity().supportFragmentManager, "Calendario")
        calendario.addOnPositiveButtonClickListener {
            binding.ctxFechaNacimiento.editText?.setText(formatear_fecha.format(calendario.selection))
        }
    }

    private fun validarFecha(fecha: String): Boolean {
        if (fecha.isNotEmpty()){
            if (fecha.length == 10 && fecha.get(2).toString().equals("/") && fecha.get(5).toString().equals("/")){
                val dia = fecha.subSequence(0,2).toString().toInt()
                val mes = fecha.subSequence(3,5).toString().toInt()
                val anio = fecha.subSequence(6,10).toString().toInt()

                if(dia in 1..99 && mes in 1..12 && anio in 1900..2100){
                    when(mes){ // Validar el día segun el mes
                        2 -> {
                            val calendario = GregorianCalendar()
                            if (!calendario.isLeapYear(anio)){                                      //Año no biciesto (28 días)
                                if (dia == 29){
                                    binding.ctxFechaNacimiento.error = "Fecha no válida"
                                    Toast.makeText(context,"Febrero no tiene día 29 en años no biciestos",Toast.LENGTH_LONG).show()
                                    return false
                                } else if (dia > 29){
                                    binding.ctxFechaNacimiento.error = "Fecha no válida"
                                    Toast.makeText(context,"Febrero no tiene mas de 28  días",Toast.LENGTH_LONG).show()
                                    return false
                                }
                            } else{                                                                 //Año biciesto (29 días)
                                if (dia > 29){
                                    binding.ctxFechaNacimiento.error = "Fecha no válida"
                                    Toast.makeText(context,"Febrero no tiene mas de 29  días",Toast.LENGTH_LONG).show()
                                    return false
                                }
                            }
                        }
                        4,6,9,11 -> {
                            if (dia>30){
                                binding.ctxFechaNacimiento.error = "Fecha no válida"
                                Toast.makeText(context, "El mes ingresado solo tiene 30 días",Toast.LENGTH_LONG).show()
                                return false
                            }
                        }
                        1,3,5,7,8,10,12 -> {
                            if (dia>31){
                                binding.ctxFechaNacimiento.error = "Fecha no válida"
                                Toast.makeText(context, "El mes ingresado solo tiene 31 días",Toast.LENGTH_LONG).show()
                                return false
                            }
                        }
                    }

                    //Comprobar si tiene mas de 13 años
                    val calendario = Calendar.getInstance()

                    var anios = calendario.get(Calendar.YEAR) - anio
                    val meses = (calendario.get(Calendar.MONTH) +1) - mes
                    val dias = calendario.get(Calendar.DAY_OF_MONTH) - dia

                    if (meses < 0 || (meses == 0 && dias <0)){
                        anios--
                    }

                    if (anios >= 13){
                        return true
                    } else{
                        binding.ctxFechaNacimiento.error = "Debes tener al menos 13 años"
                        return false
                    }

                } else{
                    binding.ctxFechaNacimiento.error = "Fecha fuera de rango"
                    Toast.makeText(context,"El rango de fechas valido es del 01/01/1900 al 31/12/2100",Toast.LENGTH_LONG).show()
                    return false
                }

            } else{
                binding.ctxFechaNacimiento.error = "Formato de fecha no valido"
                Toast.makeText(context,"La fecha debe tener 10 caracteres con el siguiente formato: dd/mm/aaaa ",Toast.LENGTH_LONG).show()
                return false
            }
        } else{
            binding.ctxFechaNacimiento.error = "Ingrese su fecha de nacimiento"
            return false
        }
    }

    override fun salvarDatos() {
        vmRegistro.setFechaNacimiento(binding.ctxFechaNacimiento.editText?.text.toString())
        vmRegistro.setGenero(binding.ctxGenero.editText?.text.toString())
    }

    override fun verificarCampos() {
        val bool_fecha: Boolean
        val bool_genero: Boolean


        if (validarFecha(binding.ctxFechaNacimiento.editText?.text.toString())) {
            bool_fecha = true
            binding.ctxFechaNacimiento.error = null
        } else {
            bool_fecha = false
        }

        if (binding.ctxGenero.editText?.text.toString().isNotEmpty()) {
            bool_genero = true
            binding.ctxGenero.error = null
        } else {
            bool_genero = false
            binding.ctxGenero.error = "Seleccione su genero"
        }

        return if (bool_fecha && bool_genero) {
            salvarDatos()
            change_fragment_listener.mostrarFragment(2)
        } else {
            salvarDatos()
        }
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