package com.petscare.org.vista.activitys

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.petscare.org.R
import com.petscare.org.databinding.ActivityRegistroBinding
import com.petscare.org.modelo.objetos.Item1
import com.petscare.org.utilidades.CropImageUtil
import com.petscare.org.utilidades.FileUtil
import com.petscare.org.utilidades.KeyboardUtil
import com.petscare.org.viewmodel.ViewModelRegistro
import com.petscare.org.vista.Interfaces.OnFragmentNavigationListener
import com.petscare.org.vista.adaptadores.dialogos.AdaptadorListaOpciones
import com.petscare.org.vista.fragments.registro.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ActivityRegistro : AppCompatActivity(), OnFragmentNavigationListener {

    private val vmRegistro: ViewModelRegistro by viewModels()
    private lateinit var binding: ActivityRegistroBinding

    private lateinit var network_callback: ConnectivityManager.NetworkCallback

    private var index: Int = 0
    private lateinit var transaction : FragmentTransaction

    private lateinit var frag_nombre: FragmentNombre
    private lateinit var frag_edad_genero: FragmenteEdadGenero
    private lateinit var frag_correo_Correo_contrasena: FragmentCorreoContrasena
    private lateinit var frag_terminar: FragmentTerminar

    private lateinit var archivo_foto: File
    private lateinit var ruta_foto: String
    private lateinit var ruta_foto_absoluta: String

    private val PERMISO_CAMARA = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.THEME_GLOBAL_APP)
        super.onCreate(savedInstanceState)

        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        crearFragments()
        mostrarFragment(index)
        salvarDatos()
        observarConexionInternet()

        if (verificarConexionInternet()){
            binding.layoutNoConection.visibility = View.GONE
            binding.layoutRegistro.visibility = View.VISIBLE
        } else{
            binding.layoutNoConection.visibility = View.VISIBLE
            binding.layoutRegistro.visibility = View.GONE
            binding.animNoInternet.playAnimation()
        }

        if (vmRegistro.getArchivoFoto()!=null){
            mostrarFoto()
        }
        observarTeclado()
        eventosUI()
    }

    private fun crearFragments() {
        this.index = vmRegistro.getIndex()!!
        frag_nombre = FragmentNombre()
        frag_edad_genero = FragmenteEdadGenero()
        frag_correo_Correo_contrasena = FragmentCorreoContrasena()
        frag_terminar = FragmentTerminar()
    }

    override fun mostrarFragment(index: Int) {
        transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
        this.index = index
        when (this.index) {
            0 -> {
                transaction.replace(R.id.contenedor_frags_registro, frag_nombre).commit()

            }
            1 -> transaction.replace(R.id.contenedor_frags_registro, frag_edad_genero).commit()
            2 -> transaction.replace(R.id.contenedor_frags_registro, frag_correo_Correo_contrasena)
                .commit()
            3 -> {
                transaction.replace(R.id.contenedor_frags_registro, frag_terminar).commit()
                binding.txtInfo.visibility = View.GONE
                binding.btnNext.text = "Terminar"
            }
        }
    }

    private fun salvarDatos() {
        val bundle = intent.extras
        vmRegistro.setLada(bundle?.getString("lada")!!)
        vmRegistro.setTelefono(bundle.getString("telefono")!!)
        vmRegistro.setUID(bundle.getString("UID")!!)
    }

    private fun verificarConexionInternet():Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val tipos_conexiones = cm.getNetworkCapabilities(network)
        return tipos_conexiones != null && (tipos_conexiones.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || tipos_conexiones.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun observarConexionInternet() {
        network_callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                runOnUiThread(Runnable{
                    binding.layoutNoConection.visibility = View.GONE
                    binding.layoutRegistro.visibility = View.VISIBLE
                })
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                runOnUiThread(Runnable{
                    KeyboardUtil.cerrarTeclado(binding.root)
                    binding.layoutNoConection.visibility = View.VISIBLE
                    binding.layoutRegistro.visibility = View.GONE
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

    private fun observarTeclado() {
        KeyboardUtil.addKeyboardToggleListener(this,
            object : KeyboardUtil.SoftKeyboardToggleListener {
                override fun onToggleSoftKeyboard(isVisible: Boolean) {
                    if (isVisible) {
                        binding.appBar.setExpanded(false, true)
                        binding.btnNext.shrink()
                    } else {
                        binding.appBar.setExpanded(true, true)
                        binding.btnNext.extend()
                    }
                }
            })
    }

    private fun eventosUI() {
        binding.imgFoto.setOnClickListener { mostrarSelectorFoto() }

        binding.btnNext.setOnClickListener {
            KeyboardUtil.cerrarTeclado(binding.root)
            when(this.index){
                0 -> frag_nombre.verificarCampos()
                1 -> frag_edad_genero.verificarCampos()
                2 -> frag_correo_Correo_contrasena.verificarCampos()
                3 -> {
                    startActivity(Intent(this,ActivityMenu::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    finish()
                }
            }
        }
    }

    private fun mostrarSelectorFoto() {
        val items = ArrayList<Item1>()
        items.add(Item1("Camara", R.drawable.ic_camera))
        items.add(Item1("Galeria", R.drawable.ic_galeria))
        items.add(Item1("Mis archivos",R.drawable.ic_carpeta))
        items.add(Item1("Cancelar", R.drawable.ic_cancelar))

        val dialogo = MaterialAlertDialogBuilder(this)
            .setTitle("Establecer foto de perfil")
            .setAdapter(AdaptadorListaOpciones.getAdaptador(this, items)) { dialog_interface, index ->
                when (index) {
                    0 -> verificarPermisosCamara()
                    1 -> abrirGaleria()
                    2 -> abrirExploradorArchivos()
                    3 -> dialog_interface.dismiss()
                }
            }.show()
    }

    private fun verificarPermisosCamara(){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
           abrirCamara()
        } else {
            solicitarPermisosCamara()
        }
    }

    private fun solicitarPermisosCamara() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),PERMISO_CAMARA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISO_CAMARA && grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            abrirCamara()
        } else{
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                val dialogo = MaterialAlertDialogBuilder(this)
                    .setTitle("Permisos denegados")
                    .setMessage("Debido a que rechazo los permisos de camara en mas de una ocasión, el sistema Android, no nos permite volver a " +
                            "solicitarle los permisos, por lo que deberá permitirlos manualmente en la siguiente pantalla de Información de la aplicación > Permisos " +
                            "> Cámara > Permitir")
                    .setPositiveButton("Aceptar") {dialogo, boton ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package",packageName,null)
                        intent.setData(uri)
                        resultado_permiso_camara.launch(intent)
                    }
                    .setNegativeButton("Cancelar") {dialogo, boton ->
                        dialogo.dismiss()
                    }
                dialogo.show()
            }else{
                val dialogo = MaterialAlertDialogBuilder(this)
                    .setTitle("Solicitud de permisos")
                    .setMessage("Es necesario aceptar los permisos de uso de la camara, si desea usarla para tomarse una foto " +
                            "y establecerla como foto de perfil de la cuenta, mientras no acepte el permiso, no podra utilizar esta funcionalidad")
                    .setPositiveButton("Aceptar") {dialogo, boton ->
                        solicitarPermisosCamara()
                    }
                    .setNegativeButton("Cancelar") {dialogo, boton ->
                        dialogo.dismiss()
                    }
                dialogo.show()
            }
        }
    }

    private fun crearArchivoFoto(): File {
        val directorio_almacenamiento = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        archivo_foto = File.createTempFile("${Date()}_foto", ".jpg", directorio_almacenamiento)
        ruta_foto = "file: ${archivo_foto.absolutePath}"
        ruta_foto_absoluta = archivo_foto.absolutePath
        return archivo_foto
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        if (intent.resolveActivity(packageManager) != null) {
            var archivo_foto: File? = null
            try {
                archivo_foto = crearArchivoFoto()
            } catch (e: Exception) {
                Toast.makeText(this, "Hubo un error al crear el archivo de la foto", Toast.LENGTH_SHORT).show()
            }

            if (archivo_foto != null) {
                val foto_uri: Uri = FileProvider.getUriForFile(this, "com.petscare.org", archivo_foto)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, foto_uri)
                resultado_camara.launch(intent)
            } else {
                Toast.makeText(this, "Hubo un error al cargar el archivo de imagen", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Hubo un error al abrir la camara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirGaleria() {
        val intent = Intent()
            .setAction(Intent.ACTION_PICK)
            .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        resultado_galeria.launch(intent)
    }

    private fun abrirExploradorArchivos(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("image/*")
        resultado_explorador_archivos.launch(intent)
    }

    private val resultado_camara = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK){
            resultado_recorte.launch(Pair(Uri.fromFile(archivo_foto),Uri.fromFile(archivo_foto)))
        }
    }

    private val resultado_galeria = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK){
            val uri_origen = result.data!!.data
            var archivo_foto_galeria :File? = null
            try {
                archivo_foto_galeria = crearArchivoFoto()
            } catch (e : Exception){
                Toast.makeText(this, "Hubo un error para crear el archivo de la foto", Toast.LENGTH_SHORT).show();
            }

            val uri_destino = Uri.fromFile(archivo_foto_galeria)
            resultado_recorte.launch(Pair(uri_origen,uri_destino) as Pair<Uri, Uri>?)
        }
    }

    private val resultado_explorador_archivos = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val uri_origen = result.data!!.data
            var archivo_foto_explorador : File? = null
            try {
                archivo_foto_explorador = crearArchivoFoto()
            } catch (e : java.lang.Exception){
                Toast.makeText(this, "Hubo un error para crear el archivo de la foto", Toast.LENGTH_SHORT).show();
            }
            val uri_destino = Uri.fromFile(archivo_foto_explorador)
            resultado_recorte.launch(Pair(uri_origen,uri_destino) as Pair<Uri, Uri>?)
        }
    }

    private val resultado_recorte = registerForActivityResult(CropImageUtil()) {
        val uri = it ?: return@registerForActivityResult // this is the output Uri
        binding.imgFoto.setImageURI(uri)
        mostrarFoto(uri)
        }

    private val resultado_permiso_camara = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            abrirCamara()
        }
    }

    private fun mostrarFoto(uri: Uri) {
        try {
            //Intentar crear un arvhivo con la ruta de la imagen
            archivo_foto = FileUtil.from(this, uri)
            vmRegistro.setArchivoFoto(archivo_foto)

            //Crear un bitmap apartir de el archivo
            val bitmap = BitmapFactory.decodeFile(archivo_foto.absolutePath)

            //Crear un bitmap redondo apartir del bitmap anterior y establecer el radio del redondeo del circulo
            val round_bitmap = RoundedBitmapDrawableFactory.create(resources,bitmap)
            round_bitmap.cornerRadius = 300f

            //Quitar el icono (recurso src) de la imagen de foto de perfil
            binding.imgFoto.setImageResource(0)

            //Establecer de fondo la foto del usuario
            binding.imgFoto.background = round_bitmap

        } catch (e : IOException){
            Toast.makeText(this, "Hubo un error " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun mostrarFoto(){
        try {
            //Crear un bitmap apartir de el archivo
            val bitmap = BitmapFactory.decodeFile(vmRegistro.getArchivoFoto()?.absolutePath)

            //Crear un bitmap redondo apartir del bitmap anterior y establecer el radio del redondeo del circulo
            val round_bitmap = RoundedBitmapDrawableFactory.create(resources,bitmap)
            round_bitmap.cornerRadius = 300f

            //Quitar el icono (recurso src) de la imagen de foto de perfil
            binding.imgFoto.setImageResource(0)

            //Establecer de fondo la foto del usuario
            binding.imgFoto.background = round_bitmap
        } catch (e: IOException){
            Toast.makeText(this, "Hubo un error " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        when (--index) {
            -1 -> {
                Toast.makeText(this, "No puedes cancelar el proceso de registro de datos de la cuenta",Toast.LENGTH_SHORT).show()
                index++
            }
            0,1 -> mostrarFragment(index)
            else -> index++
        }
    }

    override fun onStop() {
        super.onStop()
        vmRegistro.setIndex(this.index)
    }

    override fun onDestroy() {
        super.onDestroy()
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).unregisterNetworkCallback(network_callback)
    }
}