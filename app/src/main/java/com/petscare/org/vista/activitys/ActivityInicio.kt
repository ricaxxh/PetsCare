package com.petscare.org.vista.activitys

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.petscare.org.databinding.ActivityInicioBinding

class ActivityInicio : AppCompatActivity(){

    private lateinit var binding : ActivityInicioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onStart() {
        super.onStart()

        //Verificar si hay algun usuario con sesión activa.
        // Si lo hay, mandarlo directamente al activity menu.

        val usuario = auth.currentUser
        if (usuario!=null){
            startActivity(Intent(this,ActivityMenu::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        retardarSplashScreen()
        eventosUI()
    }

    private fun retardarSplashScreen() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    Thread.sleep(1500)
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    return true
                }
            }
        )
    }

    private fun eventosUI(){

        binding.btnGoogle.setOnClickListener {
            configurarGoogleLogin()
            val googlAuthIntent = googleSignInClient.signInIntent
            result_google_auth.launch(googlAuthIntent)
        }

        binding.btnFacebook.setOnClickListener {

        }

        binding.btnTelefono.setOnClickListener {
            startActivity(Intent(this,ActivityAuth::class.java))
        }

        binding.imgLogo.setOnClickListener {
            startActivity(Intent(this,ActivityMenu::class.java))
        }

        binding.imgPajaro.setOnClickListener { startActivity(Intent(this, ActivityRegistro::class.java)) }
    }

    private fun configurarGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("285631680559-tn1de3turd5188icev788m0jnmn3nvhu.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private val result_google_auth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val cuenta = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(cuenta.idToken!!)
            } catch (e: ApiException){
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this,ActivityMenu::class.java))
                } else {
                    Toast.makeText(this,"No se pudo iniciar sesion con Google",Toast.LENGTH_SHORT).show()
                }
            }
    }

}