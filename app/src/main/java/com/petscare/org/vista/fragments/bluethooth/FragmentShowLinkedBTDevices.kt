package com.petscare.org.vista.fragments.bluethooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.petscare.org.databinding.FragmentShowLinkedBtDevicesBinding
import com.petscare.org.viewmodel.ViewModelBluetooth
import com.petscare.org.vista.Interfaces.OnFragmentNavigationListener
import com.petscare.org.vista.Interfaces.OnItemClickListener
import com.petscare.org.vista.adaptadores.recyclers.AdaptadorDispositivosBT

class FragmentShowLinkedBTDevices : Fragment() {

    private val vm_bluetooth : ViewModelBluetooth by activityViewModels()
    private var _binding: FragmentShowLinkedBtDevicesBinding? = null
    private val binding get() = _binding!!
    private lateinit var change_fragment_listener: OnFragmentNavigationListener

    private var bt_adapter: BluetoothAdapter? = null
    private var bt_devices_adapter: AdaptadorDispositivosBT? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        change_fragment_listener = context as OnFragmentNavigationListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShowLinkedBtDevicesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        eventosUI()
        checkisDeviceBtCompatible()
    }

    private fun eventosUI() {
        binding.opcAgregarBt.setOnClickListener {
            change_fragment_listener.mostrarFragment(1)
        }
    }

    private fun checkisDeviceBtCompatible() {
        bt_adapter = (requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        if (bt_adapter != null) checkBluetoothIsActive()
        else Toast.makeText(requireContext(), "Su dispositivo no es compatible con bluetooth", Toast.LENGTH_SHORT).show()
    }

    private fun checkBluetoothIsActive() {
        if (bt_adapter!!.isEnabled) getLinkedDevices()
        else {
            val enable_bt_intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            result_active_bt.launch(enable_bt_intent)
        }
    }

    private fun getLinkedDevices() {
        val bt_devices: Set<BluetoothDevice> = bt_adapter!!.bondedDevices
        if (bt_devices.isNotEmpty()) {
            showLinkedDevices(bt_devices)
        } else {
            Toast.makeText(requireContext(), "NO HAY DISPOSITIVOS VINCULADOS", Toast.LENGTH_LONG).show()
        }
    }

    private fun showLinkedDevices(bt_devices: Set<BluetoothDevice>) {
        binding.recyclerDispositivosBt.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        bt_devices_adapter = AdaptadorDispositivosBT(requireContext(), bt_devices,
            object : OnItemClickListener {
                override fun onItemClick(bt_device: BluetoothDevice) {
                    vm_bluetooth.ldata_bt_device.value = bt_device
                    change_fragment_listener.mostrarFragment(2)
                }
            })
        binding.recyclerDispositivosBt.adapter = bt_devices_adapter
        bt_devices_adapter!!.notifyDataSetChanged()
    }

    private val result_active_bt =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                getLinkedDevices()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Activar conexion bluethooth")
                    .setMessage("Para agregar un nuevo dispositivo es necesario que habilites la conexión bluethooth.")
                    .setPositiveButton("Aceptar") { dialogo, boton ->
                        checkBluetoothIsActive()
                    }
                    .setNegativeButton("Cancelar"){ dialogo, boton ->
                        dialogo.dismiss()
                        requireActivity().finish()
                    }
                    .show()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}