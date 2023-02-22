package com.petscare.org.vista.fragments.auth

import android.content.Context
import android.content.res.ColorStateList.*
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.petscare.org.R
import com.petscare.org.databinding.FragmentVerificationBinding
import com.petscare.org.viewmodel.ViewModelAuth
import com.petscare.org.vista.Interfaces.ICheckCode

class FragmentVerification : Fragment(){

    private val vmAuth: ViewModelAuth by activityViewModels()
    private var _binding: FragmentVerificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var icheck_code : ICheckCode

    //Contador de tiempo
    private lateinit var contador: CountDownTimer
    private var tiempo_contador: Long = 15000

    override fun onAttach(context: Context) {
        super.onAttach(context)
        icheck_code = context as ICheckCode
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mostrarNumero()
        observar_ldata()
        iniciarContadorTiempo()
        eventosUI()
    }

    private fun mostrarNumero() {
        binding.txtNumero.setText("Ingresa el código enviado al ".plus(vmAuth.getLada()).plus(" ${vmAuth.getTelefono()}"))
    }

    private fun observar_ldata() {
        vmAuth.ldata_auth.observe(viewLifecycleOwner) { ldata_auth ->
            binding.ctxCodigo.editText?.setText(ldata_auth.codigo)
        }
    }

    private fun iniciarContadorTiempo() {
        tiempo_contador = vmAuth.getTiempoContador()
        contador = object : CountDownTimer(tiempo_contador, 1000) {
            override fun onTick(p0: Long) {
                tiempo_contador = p0
                binding.btnReenviar.text = "Reenviar ${p0 / 1000}s"
            }

            override fun onFinish() {
                binding.btnReenviar.text = "Reenviar"
                habilitarBtnReenviar()
                contador.cancel()
            }
        }.start()
    }

    private fun eventosUI() {
        binding.btnVerificar.setOnClickListener { verificarCampoCodigo() }
    }

    private fun verificarCampoCodigo() {
        binding.ctxCodigo.error = null
        if (binding.ctxCodigo.editText?.text.toString().isNotEmpty()) {
            if (binding.ctxCodigo.editText?.text.toString().length == 6) {
                val codigo = binding.ctxCodigo.editText?.text.toString()
                icheck_code.verificarCodigo(codigo)
            } else {
                binding.ctxCodigo.error = "El código debe tener 6 digitos"
            }
        } else {
            binding.ctxCodigo.error = "Ingresa el código"
        }
    }

    private fun habilitarBtnReenviar() {
        binding.btnReenviar.isEnabled = true
        binding.btnReenviar.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.cafe)
        binding.btnReenviar.setTextColor(ContextCompat.getColor(requireContext(), R.color.blanco))
        binding.btnReenviar.compoundDrawableTintList = valueOf(ContextCompat.getColor(requireContext(), R.color.blanco))
    }

    fun setErrorCtxCodigo(error: String?){
        binding.ctxCodigo.error = error
    }

    fun setTextCtxCodigo(text: String?){
        binding.ctxCodigo.editText?.setText(text)
    }

    override fun onPause() {
        super.onPause()
        vmAuth.setTiempoContador(tiempo_contador)
        vmAuth.setCodigo(binding.ctxCodigo.editText?.text.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        contador.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}