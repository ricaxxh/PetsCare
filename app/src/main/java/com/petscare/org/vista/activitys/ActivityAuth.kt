package com.petscare.org.vista.activitys

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petscare.org.R
import com.petscare.org.databinding.ActivityAuthBinding
import com.petscare.org.utilidades.KeyboardUtil
import com.petscare.org.viewmodel.ViewModelAuth
import com.petscare.org.vista.Interfaces.ICheckCode
import com.petscare.org.vista.Interfaces.OnFragmentNavigationListener
import com.petscare.org.vista.fragments.auth.FragmentTelefono
import com.petscare.org.vista.fragments.auth.FragmentVerification
import java.lang.Exception
import java.util.concurrent.TimeUnit

class ActivityAuth : AppCompatActivity(), OnFragmentNavigationListener, ICheckCode {

   //Objetos para la vinculación de vistas y persistencia de datos
    private val vmAuth: ViewModelAuth by viewModels()
    private lateinit var binding: ActivityAuthBinding

    //Objetos para el uso de Firebase Phone Auth
    private lateinit var auth: FirebaseAuth
    private lateinit var mcallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var id_verificacion_guardado: String
    private lateinit var token_reenvio: PhoneAuthProvider.ForceResendingToken

    private lateinit var network_callback: ConnectivityManager.NetworkCallback

    //Objetos para el uso de los fragments
    private var index: Int = 0
    private lateinit var transaction: FragmentTransaction
    private lateinit var frag_telefono: FragmentTelefono
    private lateinit var frag_verification: FragmentVerification

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.THEME_TOOLBAR_ACTIVITY)
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        crearFragments()
        mostrarFragment(index)
        observarConexionInternet()

        if (verificarConexionInternet()){
            binding.layoutNoConection.visibility = View.GONE
            binding.layoutNormal.visibility = View.VISIBLE
        } else{
            binding.layoutNoConection.visibility = View.VISIBLE
            binding.layoutNormal.visibility = View.GONE
            binding.animNoInternet.playAnimation()
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun crearFragments() {
        this.index = vmAuth.getIndex()!!
        frag_telefono = FragmentTelefono()
        frag_verification = FragmentVerification()
    }

    override fun mostrarFragment(index: Int) {
        transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        this.index = index
        when (this.index) {
            0 -> transaction.replace(R.id.contenedor_frags_auth, frag_telefono).commit()
            1 -> {
                transaction.replace(R.id.contenedor_frags_auth, frag_verification).commit()
                verificarCodigoEnviado()
            }
        }
    }

    private fun observarConexionInternet() {
        network_callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                runOnUiThread(Runnable {
                    binding.layoutNoConection.visibility = View.GONE
                    binding.layoutNormal.visibility = View.VISIBLE
                })

                if (index == 1){
                    verificarCodigoEnviado()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                runOnUiThread(Runnable {
                    KeyboardUtil.cerrarTeclado(binding.root)
                    binding.layoutNoConection.visibility = View.VISIBLE
                    binding.layoutNormal.visibility = View.GONE
                    binding.animNoInternet.playAnimation()
                })
            }
        }

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(network_callback)
        } else {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            cm.registerNetworkCallback(networkRequest, network_callback)
        }
    }

    private fun verificarConexionInternet():Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val tipos_conexiones = cm.getNetworkCapabilities(network)
        return tipos_conexiones != null && (tipos_conexiones.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || tipos_conexiones.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    fun verificarCodigoEnviado() {
        if (vmAuth.getCodigoVerificacionGuardado() == null){
            getCodeListener()
            enviarCodigo()
        }
    }

    private fun enviarCodigo() {
        auth.useAppLanguage()
        val options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber("${vmAuth.getLada()}".plus(vmAuth.getTelefono()))
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mcallback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verificarCodigo(codigo: String) {
        val credential = PhoneAuthProvider.getCredential(vmAuth.getCodigoVerificacionGuardado()!!,codigo)
        signInWithPhoneAuthCredential(credential)
    }

    private fun getCodeListener() {
        mcallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                frag_verification.setErrorCtxCodigo(null)
                frag_verification.setTextCtxCodigo(credential.smsCode)
                Toast.makeText(this@ActivityAuth, "Verificacion exitosa", Toast.LENGTH_SHORT).show()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(firebaseException: FirebaseException) {
                verificarErrores(firebaseException)
            }

            override fun onCodeSent(verification_id: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verification_id, token)
                Toast.makeText(this@ActivityAuth, "Se envió el código de verificación", Toast.LENGTH_SHORT).show()
                vmAuth.setIdVerificacionGuardado(verification_id)
                token_reenvio = token
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val db = Firebase.firestore
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val info = db.collection("Usuarios").document(task.result.user!!.uid)
                    info.get().addOnCompleteListener(this) { task_data ->
                        if (task_data.isSuccessful){
                            val document_info = task_data.result
                            if (document_info.exists()){
                                cambiarActivity(0,null)
                            } else{
                                val dialogo = MaterialAlertDialogBuilder(this,R.style.CustomDialog)
                                val vista = layoutInflater.inflate(R.layout.dialog_registrar_datos, findViewById(R.id.contenedor_dialog_rd),false)
                                dialogo.setView(vista)
                                dialogo.setCancelable(false)
                                val alertDialog = dialogo.create()
                                val btn_continuar = vista.findViewById<Button>(R.id.btn_continuar)
                                btn_continuar.setOnClickListener {
                                    alertDialog.dismiss()
                                    cambiarActivity(1, task.result.user!!.uid)
                                }
                                alertDialog.show()
                                }
                        } else{
                            Toast.makeText(this,"Hubo un error",Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    verificarErrores(task.exception)
                }
            }
    }

    private fun verificarErrores(exception: Exception?) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> Toast.makeText(this, "El número de telefono ingresado ya esta asociado a otra cuenta de Petscare", Toast.LENGTH_SHORT).show()
            is FirebaseNetworkException -> Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show()
            is FirebaseAuthInvalidCredentialsException -> frag_verification.setErrorCtxCodigo("Codigo Incorrecto")
            else -> Toast.makeText(this, "Hubo un error: ${exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cambiarActivity(opc : Int, UID: String?) {
        val intent = Intent()
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (opc == 0){
            intent.setClass(this,ActivityMenu::class.java)
        } else {
            val bundle = Bundle()
            bundle.putString("lada", vmAuth.getLada())
            bundle.putString("telefono", vmAuth.getTelefono())
            bundle.putString("UID", UID)
            intent.putExtras(bundle)
            intent.setClass(this,ActivityRegistro::class.java)
        }
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (index == 0) {
            finish()
        } else if (index == 1) {
            Toast.makeText(
                this,
                "No puedes cancelar el proceso de verificación de la cuenta.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPause() {
        super.onPause()
        vmAuth.setIndex(this.index)
    }

    override fun onDestroy() {
        super.onDestroy()
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).unregisterNetworkCallback(
            network_callback
        )
    }
}
