package com.petscare.org.vista.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentTransaction
import com.petscare.org.R
import com.petscare.org.viewmodel.ViewModelBluetooth
import com.petscare.org.vista.Interfaces.OnFragmentNavigationListener
import com.petscare.org.vista.fragments.bluethooth.FragmentLinkNewBTDevice
import com.petscare.org.vista.fragments.bluethooth.FragmentShowLinkedBTDevices
import com.petscare.org.vista.fragments.bluethooth.FragmentSetupBTDevice

class ActivityAgregarDispositivoBT : AppCompatActivity(), OnFragmentNavigationListener {

    private val vm_bluetooth : ViewModelBluetooth by viewModels()

    private var index = 0
    private lateinit var frag_showLinked_btDevices : FragmentShowLinkedBTDevices
    private lateinit var frag_agregar_bt : FragmentLinkNewBTDevice
    private lateinit var frag_configurar_bt: FragmentSetupBTDevice
    private lateinit var transaction : FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.THEME_TOOLBAR_ACTIVITY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_dispositivo_bt)
        init()
    }

    private fun init(){
        crearFragments()
        mostrarFragment(vm_bluetooth.ldata_ui_bluetooth.value!!.frag_index)
    }

    private fun crearFragments() {
        frag_showLinked_btDevices = FragmentShowLinkedBTDevices()
        frag_agregar_bt = FragmentLinkNewBTDevice()
        frag_configurar_bt = FragmentSetupBTDevice()
    }

    override fun mostrarFragment(index: Int) {
        transaction = supportFragmentManager.beginTransaction()
        when(index){
            0 -> transaction.replace(R.id.contenedor_frags_bluethooth,frag_showLinked_btDevices).commit()
            1 -> transaction.replace(R.id.contenedor_frags_bluethooth,frag_agregar_bt).commit()
            2 -> transaction.replace(R.id.contenedor_frags_bluethooth,frag_configurar_bt).commit()
        }
        this.index = index
        vm_bluetooth.ldata_ui_bluetooth.value!!.frag_index = this.index
    }

    override fun onBackPressed() {
        when(index){
            0 -> finish()
            1 -> mostrarFragment(0)
        }
    }
}