package com.petscare.org.vista.activitys

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.petscare.org.R
import com.petscare.org.databinding.ActivityDeviceControlBinding
import com.petscare.org.domain.providers.TipoDispositivo
import com.petscare.org.modelo.objetos.Dispositivo
import ir.mahozad.android.PieChart

class ActivityDeviceControl : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceControlBinding
    private lateinit var device_type: String
    private lateinit var device_name: String
    private lateinit var db_reference: DatabaseReference
    private lateinit var listener: ValueEventListener
    private var accionar: Boolean = false

    private lateinit var drawable_icon : Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.THEME_TOOLBAR_ACTIVITY)
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mostrarVista()
        obtenerDatos()
        evntosUI()
    }

    private fun mostrarVista() {
        device_type = intent.extras!!.getString("device_type")!!
        if (device_type == TipoDispositivo.DISPENSADOR_ALIMENTO.name){
            binding.layoutDispensador.visibility = View.VISIBLE
            binding.layoutFoco.visibility = View.GONE
        } else if (device_type == TipoDispositivo.FOCO.name){
            binding.layoutDispensador.visibility = View.GONE
            binding.layoutFoco.visibility = View.VISIBLE
        }
    }

    private fun obtenerDatos() {

        device_name = intent.extras!!.getString("device_name")!!
        val id_usuario = Firebase.auth.currentUser!!.uid

        db_reference = FirebaseDatabase.getInstance()
            .getReference("USUARIOS/$id_usuario/DISPOSITIVOS_IOT/$device_name")

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dispositivo: Dispositivo? = snapshot.getValue(Dispositivo::class.java)
                binding.toolbarDeviceControl.title = dispositivo!!.nombre

                if (dispositivo.tipo == TipoDispositivo.DISPENSADOR_ALIMENTO.name) {

                    //Info dispensador ----------------------------------------------------------------------------------------------------------------
                    if (dispositivo.accionar == true) {
                        binding.btnDispensar.text = "Parar"
                        binding.txtEstadoDispensador.text = "Dispensando"
                        binding.txtEstadoDispensador.setTextColor(ContextCompat.getColor(this@ActivityDeviceControl, R.color.verde))
                        accionar = true
                    } else {
                        binding.btnDispensar.text = "Dispensar"
                        binding.txtEstadoDispensador.text = "En reposo"
                        binding.txtEstadoDispensador.setTextColor(Color.RED)
                        accionar = false
                    }

                    //Info grafica
                    val cant_alimento = dispositivo.estado!!.toFloat()
                    val alimento_rest:Float = 100f - cant_alimento
                    binding.pieChart.slices = listOf(
                        PieChart.Slice(cant_alimento.div(100), ContextCompat.getColor(this@ActivityDeviceControl, R.color.verde)),
                        PieChart.Slice(alimento_rest.div(100), Color.RED)
                    )

                    //Info Foco ----------------------------------------------------------------------------------------------------------------

                } else if (dispositivo.tipo == TipoDispositivo.FOCO.name){
                    drawable_icon = ContextCompat.getDrawable(this@ActivityDeviceControl,R.drawable.ic_foco)!!
                    binding.icFoco.background = drawable_icon
                    if (dispositivo.accionar== true){
                        accionar = true
                        binding.txtEstadoFoco.text = "Encendido"
                        binding.txtEstadoFoco.setTextColor(ContextCompat.getColor(this@ActivityDeviceControl,R.color.verde))
                        DrawableCompat.setTint(drawable_icon, getColor(R.color.amarillo))
                    } else{
                        accionar = false
                        binding.txtEstadoFoco.text = "Apagado"
                        binding.txtEstadoFoco.setTextColor(Color.RED)
                        DrawableCompat.setTint(drawable_icon, getColor(R.color.gris))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        db_reference.addValueEventListener(listener)
    }

    private fun evntosUI() {
        binding.toolbarDeviceControl.setNavigationOnClickListener {
            db_reference.removeEventListener(listener)
            finish()
        }

        binding.btnDispensar.setOnClickListener {
            if (accionar){
                db_reference.updateChildren(mapOf("accionar" to false))
            } else{
                db_reference.updateChildren(mapOf("accionar" to true))
            }
        }

        binding.btnControlarFoco.setOnClickListener {
            if (accionar){
                db_reference.updateChildren(mapOf("accionar" to false))
            } else{
                db_reference.updateChildren(mapOf("accionar" to true))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db_reference.removeEventListener(listener)
    }

}