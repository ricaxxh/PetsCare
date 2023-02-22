package com.petscare.org.vista.fragments.bluethooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.airbnb.lottie.LottieDrawable
import com.petscare.org.R
import com.petscare.org.databinding.FragmentSetupBtDeviceBinding
import com.petscare.org.viewmodel.ViewModelBluetooth
import com.petscare.org.vista.Interfaces.OnFragmentNavigationListener
import java.io.IOException
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.petscare.org.modelo.objetos.Dispositivo
import java.io.InputStream
import java.io.OutputStream

class FragmentSetupBTDevice : Fragment() {

    private val vm_bluetooth: ViewModelBluetooth by activityViewModels()
    private var _binding: FragmentSetupBtDeviceBinding? = null
    private val binding get() = _binding!!
    private lateinit var change_fragment_listener: OnFragmentNavigationListener

    //BLUETOOTH
    private lateinit var bt_device: BluetoothDevice
    private lateinit var bt_adapter: BluetoothAdapter

    private lateinit var bt_handler: Handler
    private lateinit var hilo_bt_conexion: Thread
    private lateinit var hilo_comunicacion: ComunicationThread

    private val MESSAGE_READ: Int = 0
    private val MESSAGE_WRITE: Int = 1
    private val MESSAGE_TOAST: Int = 2

    private lateinit var id_usuario: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        change_fragment_listener = context as OnFragmentNavigationListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSetupBtDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        id_usuario = Firebase.auth.currentUser!!.uid

        bt_adapter = (requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        bt_device = vm_bluetooth.ldata_bt_device.value!!
        connectBtDevice()
        eventosUI()

        //RECIBIR DATOS DEL BLUETOOTH
        bt_handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_READ) {
                    val message = msg.obj as String
                    Toast.makeText(requireContext(), "Datos recibidos: $message", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun eventosUI() {
        binding.btnBtSiguiente.setOnClickListener {
            if (binding.ctxNombreBtDevice.editText!!.text.isNotEmpty()) mostrarUISetupWifi()
            else binding.ctxNombreBtDevice.error = "Ingrese el nombre"
        }
        binding.btnFinalizarWifi.setOnClickListener {
            val bool_ssid: Boolean
            val bool_ssid_pass: Boolean

            if (binding.ctxSsid.editText!!.text.isNotEmpty()) {
                binding.ctxSsid.error = null
                bool_ssid = true
            } else {
                binding.ctxSsid.error = "Ingrese el nombre de la red"
                bool_ssid = false
            }

            if (binding.ctxSsidPassword.editText!!.text.isNotEmpty()) {
                binding.ctxSsidPassword.error = null
                bool_ssid_pass = true
            } else {
                binding.ctxSsidPassword.error = "Ingrese el nombre de la red"
                bool_ssid_pass = false
            }

            if (bool_ssid && bool_ssid_pass) {
                //mostrarUIConectando()
                enviarDatosBT()
            }
        }
    }

    private fun mostrarUIConectando() {
        binding.layoutWifi.visibility = View.GONE
        binding.layoutConectandoBt.visibility = View.VISIBLE
        binding.txtEstadoBt.text = "Conectandose a internet"
        binding.txtDescripccionEstadoBt.text =
            "El dispositivo se esta conectando a internet y agregando a su cuenta de petscare+."
        binding.animConnectBtDevice.setAnimation(R.raw.anim_wifi)
        binding.animConnectBtDevice.playAnimation()
        binding.animConnectBtDevice.repeatCount = LottieDrawable.INFINITE
    }

    private fun enviarDatosBT() {
        Toast.makeText(requireContext(), "ENVIANDO DATOS", Toast.LENGTH_SHORT).show()

        val nombre_dispositivo = binding.ctxNombreBtDevice.editText!!.text.toString()
        val wifi_ssid = binding.ctxSsid.editText!!.text.toString()
        val wifi_password = binding.ctxSsidPassword.editText!!.text.toString()
        Toast.makeText(requireContext(), wifi_password, Toast.LENGTH_SHORT).show()

        hilo_comunicacion.write(id_usuario.plus("#"))
        hilo_comunicacion.write(nombre_dispositivo.plus("#"))
        hilo_comunicacion.write(wifi_ssid.plus("#"))
        hilo_comunicacion.write(wifi_password.plus("#"))

        Toast.makeText(requireContext(), "DATOS ENVIADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show()

        val id_usuario = Firebase.auth.currentUser!!.uid
        val db_reference = FirebaseDatabase.getInstance().reference.child("USUARIOS/$id_usuario/DISPOSITIVOS_IOT/$nombre_dispositivo")
        db_reference.setValue(Dispositivo(false,"null",nombre_dispositivo,"null")).addOnSuccessListener {
            requireActivity().finish()
        }
    }

    private fun connectBtDevice() {
        hilo_bt_conexion = connectBTDevice()
        hilo_bt_conexion.start()
    }

    inner class connectBTDevice : Thread() {

        //Crear la conexión bluetooth
        val BT_UUID = bt_device.uuids[0].uuid
        private val bt_socket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bt_device.createRfcommSocketToServiceRecord(BT_UUID)
        }

        override fun run() {
            //Cancelar la busqueda de dispositivos
            bt_adapter.cancelDiscovery()

            //Establecer la conexión bluethooth
            try {

                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Estableciendo conexion...", Toast.LENGTH_SHORT).show()
                }

                bt_socket!!.connect()

                requireActivity().runOnUiThread {
                    hilo_comunicacion = ComunicationThread(bt_socket!!)
                    mostrarUiBTData()
                }
                //

            } catch (e: IOException) {
                requireActivity().runOnUiThread {
                    change_fragment_listener.mostrarFragment(0)
                    Toast.makeText(requireContext(), "Dispositivo no disponible", Toast.LENGTH_SHORT).show()
                }
                try {
                    bt_socket!!.close()
                } catch (close_e: IOException) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error al cerrar la conexion: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fun cancel() {
            try {
                bt_socket!!.close()
            } catch (e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al cerrar la conexion: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun mostrarUiBTData() {
        binding.layoutConectandoBt.visibility = View.GONE
        binding.layoutDatos.visibility = View.VISIBLE

        binding.txtNombreBtDevice.text = bt_device.name
        binding.txtMacBtDevice.text = bt_device.address
        binding.ctxNombreBtDevice.editText!!.setText(bt_device.name)
    }

    private fun mostrarUISetupWifi() {
        binding.layoutDatos.visibility = View.GONE
        binding.layoutWifi.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hilo_bt_conexion.interrupt()
        _binding = null
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ComunicationThread(private val bt_socket: BluetoothSocket) : Thread() {

        private var in_stream: InputStream?
        private var out_stream: OutputStream?
        private val buffer: ByteArray = ByteArray(1024)

        init {
            try {
                in_stream = bt_socket.inputStream
                out_stream = bt_socket.outputStream
            } catch (e: IOException) {
                in_stream = null
                out_stream = null
            }
        }

        override fun run() {
            var num_bytes: Int

            //El hilo se mantiene en modo escucha para obtener los datos de ingreso
            while (true) {
                // Leer del flujo de entrada.
                num_bytes = try {
                    in_stream!!.read(buffer)
                } catch (e: IOException) {
                    Log.d("APP", "Se desconectó el flujo de entrada", e)
                    break
                }

                // Envía los bytes obtenidos a la actividad de la IU.
                val readMsg = bt_handler.obtainMessage(
                    MESSAGE_READ, num_bytes, -1,
                    buffer
                )
                readMsg.sendToTarget()
            }
        }

        // Llame a esto desde la actividad principal para enviar datos al dispositivo remoto.
        fun write(dato: String) {
            try {
                out_stream!!.write(dato.toByteArray())
            } catch (e: IOException) {
                Log.e("APP", "Ocurrió un error al enviar datos", e)
                // Envía un mensaje de error a la actividad.
                val writeErrorMsg = bt_handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "No se pudieron enviar datos al otro dispositivo")
                }
                writeErrorMsg.data = bundle
                bt_handler.sendMessage(writeErrorMsg)
                return
            }

            // Comparte el mensaje enviado con la actividad de la interfaz de usuario.
            val writtenMsg = bt_handler.obtainMessage(
                MESSAGE_WRITE, -1, -1, buffer)
            writtenMsg.sendToTarget()
        }
    }

}



