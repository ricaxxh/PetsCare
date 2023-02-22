package com.petscare.org.vista.fragments.registro

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.petscare.org.databinding.FragmentCorreoContrasenaBinding
import com.petscare.org.viewmodel.ViewModelRegistro
import com.petscare.org.vista.Interfaces.AdminDataFragments
import com.petscare.org.vista.Interfaces.OnFragmentNavigationListener

class FragmentCorreoContrasena : Fragment(), AdminDataFragments {

    private val vmRegistro: ViewModelRegistro by activityViewModels()
    private lateinit var change_fragment_listener: OnFragmentNavigationListener
    private var _binding: FragmentCorreoContrasenaBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        change_fragment_listener = context as OnFragmentNavigationListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCorreoContrasenaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observarLiveData()
    }

    private fun observarLiveData() {
        vmRegistro.ldata_registro.observe(viewLifecycleOwner, Observer { ldata_registro ->
            binding.ctxCorreo.editText?.setText(ldata_registro.correo)
            binding.ctxContrasena.editText?.setText(ldata_registro.contrasena)
        })
    }

    override fun verificarCampos() {
        val bool_correo : Boolean
        val bool_contrasena: Boolean

        if (binding.ctxCorreo.editText?.text?.isNotEmpty() == true){                                //Verifica que el campo de texto contraseña no este vacio
            if (verificarCorreoValido(binding.ctxCorreo.editText?.text.toString())){
                binding.ctxCorreo.error = null
                bool_correo = true
            } else{
                binding.ctxCorreo.error = "Correo no válido"
                bool_correo = false
            }
        } else{
            binding.ctxCorreo.error = null
            bool_correo = true
        }

        if (binding.ctxContrasena.editText?.text.toString().isNotEmpty()) {                         //Verifica que el campo de texto contraseña no este vacio
            binding.ctxContrasena.error = null
            if (binding.ctxContrasena.editText?.text.toString().length >= 8) {                      //Verifica que el campo contrasena contenta al menos 8 caracteres
                bool_contrasena = true
                binding.ctxContrasena.error = null
            } else {
                bool_contrasena = false
                binding.ctxContrasena.error = "Mínimo 8 caracteres"
            }
        } else {
            bool_contrasena = false
            binding.ctxContrasena.error = "Ingrese una contraseña"
        }

        if (bool_correo && bool_contrasena){
            salvarDatos()
            registrarDatosUsuario()
            //change_fragment_listener.mostrarFragment(3)
        } else{
            salvarDatos()
        }
    }

    private fun registrarDatosUsuario() {
        val db = Firebase.firestore
        val user_info = hashMapOf(
            "Nombre" to vmRegistro.getNombre(),
            "Apellidos" to vmRegistro.getApellidos(),
            "Fecha de nacimiento" to vmRegistro.getFechaNacimiento(),
            "Genero" to vmRegistro.getGenero(),
            "Correo" to vmRegistro.getCorreo(),
            "Contraseña" to vmRegistro.getContrasena(),
            "Lada" to vmRegistro.getLada(),
            "Telefono" to vmRegistro.getTelefono()
        )

        //Subir foto de perfil
        val storage = Firebase.storage
        val uri_foto_file = Uri.fromFile(vmRegistro.getArchivoFoto())
        val fotoPerfilReferencia = storage.reference.child(vmRegistro.getUID()!!).child("Foto de Perfil").child(uri_foto_file.lastPathSegment!!)
        fotoPerfilReferencia.putFile(uri_foto_file)

        db.collection("Usuarios").document(vmRegistro.getUID()!!).set(user_info).addOnSuccessListener {
            change_fragment_listener.mostrarFragment(3)
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"Registro de datos fallido: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun verificarCorreoValido(correo : String): Boolean {
        val patron = Patterns.EMAIL_ADDRESS
        return patron.matcher(correo).matches()
    }

    override fun onPause() {
        salvarDatos()
        super.onPause()
    }

    override fun salvarDatos() {
        vmRegistro.setCorreo(binding.ctxCorreo.editText?.text.toString())
        vmRegistro.setContrasena(binding.ctxContrasena.editText?.text.toString())
    }
}