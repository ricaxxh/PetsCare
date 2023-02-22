package com.petscare.org.vista.activitys

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.petscare.org.R
import com.petscare.org.databinding.ActivityAgregarMascotaBinding
import com.petscare.org.modelo.objetos.Item1
import com.petscare.org.utilidades.CropImageUtil
import com.petscare.org.utilidades.InternetUtil
import com.petscare.org.viewmodel.ViewModelMascota
import com.petscare.org.vista.adaptadores.dialogos.AdaptadorListaOpciones
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ActivityAgregarMascota : AppCompatActivity() {

    val vmMascota: ViewModelMascota by viewModels()
    private lateinit var binding: ActivityAgregarMascotaBinding

    //Objetos necesarios para la foto de la mascota
    private lateinit var archivo_foto: File
    private lateinit var ruta_foto: String
    private lateinit var ruta_foto_absoluta: String

    private val PERMISO_CAMARA = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.THEME_TOOLBAR_ACTIVITY)
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observarLDataMascotas()
        eventosUI()

    }

    private fun observarLDataMascotas() {

        vmMascota.liveData().observe(this) { ldata_mascotas ->
            mostrarFoto(ldata_mascotas.img_foto)
            binding.ctxNombre.editText?.setText(ldata_mascotas.ctx_nombre)
            binding.ctxTipo.editText?.setText(ldata_mascotas.ctx_tipo_mascota)
            binding.ctxRaza.editText?.setText(ldata_mascotas.ctx_raza)
            binding.ctxEdad.editText?.setText(ldata_mascotas.ctx_edad)
            binding.ctxColor.editText?.setText(ldata_mascotas.ctx_color)
        }
    }

    private fun eventosUI() {
        binding.btnGuardar.setOnClickListener {
            salvarDatos()
            if (verificarCampos()) if (InternetUtil.verificarConexionInternet(this)) {
                guardarDatosMascota()
            } else {
                Toast.makeText(this, "Comprueba tu conexíon a internet", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imgFoto.setOnClickListener { mostrarSelectorFoto() }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun mostrarSelectorFoto() {
        val items = ArrayList<Item1>()
        items.add(Item1("Camara", R.drawable.ic_camera))
        items.add(Item1("Galeria", R.drawable.ic_galeria))
        items.add(Item1("Mis archivos", R.drawable.ic_carpeta))
        items.add(Item1("Cancelar", R.drawable.ic_cancelar))

        MaterialAlertDialogBuilder(this, R.style.CustomDialog)
            .setTitle("Establecer foto de perfil")
            .setAdapter(
                AdaptadorListaOpciones.getAdaptador(
                    this,
                    items
                )
            ) { dialog_interface, index ->
                when (index) {
                    0 -> verificarPermisosCamara()
                    1 -> abrirGaleria()
                    2 -> abrirExploradorArchivos()
                    3 -> dialog_interface.dismiss()
                }
            }
            .show()
    }

    private fun abrirGaleria() {
        val intent = Intent()
            .setAction(Intent.ACTION_PICK)
            .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        resultado_galeria.launch(intent)
    }

    private fun abrirExploradorArchivos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("image/*")
        resultado_explorador_archivos.launch(intent)
    }

    private fun verificarPermisosCamara() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCamara()
        } else {
            solicitarPermisosCamara()
        }
    }

    private fun solicitarPermisosCamara() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISO_CAMARA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISO_CAMARA && grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirCamara()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                val dialogo = MaterialAlertDialogBuilder(this)
                    .setTitle("Permisos denegados")
                    .setMessage(
                        "Debido a que rechazo los permisos de camara en mas de una ocasión, el sistema Android, no nos permite volver a " +
                                "solicitarle los permisos, por lo que deberá permitirlos manualmente en la siguiente pantalla de Información de la aplicación > Permisos " +
                                "> Cámara > Permitir"
                    )
                    .setPositiveButton("Aceptar") { dialogo, boton ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.setData(uri)
                        resultado_permiso_camara.launch(intent)
                    }
                    .setNegativeButton("Cancelar") { dialogo, boton ->
                        dialogo.dismiss()
                    }
                dialogo.show()
            } else {
                val dialogo = MaterialAlertDialogBuilder(this)
                    .setTitle("Solicitud de permisos")
                    .setMessage(
                        "Es necesario aceptar los permisos de uso de la camara, si desea usarla para tomarse una foto " +
                                "y establecerla como foto de perfil de la cuenta, mientras no acepte el permiso, no podra utilizar esta funcionalidad"
                    )
                    .setPositiveButton("Aceptar") { dialogo, boton ->
                        solicitarPermisosCamara()
                    }
                    .setNegativeButton("Cancelar") { dialogo, boton ->
                        dialogo.dismiss()
                    }
                dialogo.show()
            }
        }
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        if (intent.resolveActivity(packageManager) != null) {
            var archivo_foto: File? = null
            try {
                archivo_foto = crearArchivoFoto()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Hubo un error al crear el archivo de la foto",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (archivo_foto != null) {
                val foto_uri: Uri =
                    FileProvider.getUriForFile(this, "com.petscare.org", archivo_foto)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, foto_uri)
                resultado_camara.launch(intent)
            } else {
                Toast.makeText(
                    this,
                    "Hubo un error al cargar el archivo de imagen",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Hubo un error al abrir la camara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun crearArchivoFoto(): File {
        val directorio_almacenamiento = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        archivo_foto = File.createTempFile("${Date()}_foto", ".jpg", directorio_almacenamiento)
        ruta_foto = "file: ${archivo_foto.absolutePath}"
        ruta_foto_absoluta = archivo_foto.absolutePath
        return archivo_foto
    }

    private val resultado_camara =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                resultado_recorte.launch(
                    Pair(
                        Uri.fromFile(archivo_foto),
                        Uri.fromFile(archivo_foto)
                    )
                )
            }
        }

    private val resultado_galeria =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri_origen = result.data!!.data
                var archivo_foto_galeria: File? = null
                try {
                    archivo_foto_galeria = crearArchivoFoto()
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Hubo un error para crear el archivo de la foto",
                        Toast.LENGTH_SHORT
                    ).show();
                }

                val uri_destino = Uri.fromFile(archivo_foto_galeria)
                resultado_recorte.launch(Pair(uri_origen, uri_destino) as Pair<Uri, Uri>?)
            }
        }

    private val resultado_explorador_archivos =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri_origen = result.data!!.data
                var archivo_foto_explorador: File? = null
                try {
                    archivo_foto_explorador = crearArchivoFoto()
                } catch (e: java.lang.Exception) {
                    Toast.makeText(
                        this,
                        "Hubo un error para crear el archivo de la foto",
                        Toast.LENGTH_SHORT
                    ).show();
                }
                val uri_destino = Uri.fromFile(archivo_foto_explorador)
                resultado_recorte.launch(Pair(uri_origen, uri_destino) as Pair<Uri, Uri>?)
            }
        }

    private val resultado_recorte = registerForActivityResult(CropImageUtil()) {
        val uri = it ?: return@registerForActivityResult // this is the output Uri
        binding.imgFoto.setImageURI(uri)
        mostrarFoto(uri)
    }

    private val resultado_permiso_camara =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                abrirCamara()
            }
        }

    private fun mostrarFoto(uri: Uri?) {
        if (uri != null) {
            vmMascota.data().img_foto = uri
            try {
                //Crear un bitmap apartir de el archivo
                val bitmap = BitmapFactory.decodeFile(uri.path)

                //Crear un bitmap redondo apartir del bitmap anterior y establecer el radio del redondeo del circulo
                val round_bitmap = RoundedBitmapDrawableFactory.create(resources, bitmap)
                round_bitmap.cornerRadius = 300f

                //Quitar el icono (recurso src) de la imagen de foto de perfil
                binding.imgFoto.setImageResource(0)

                //Establecer de fondo la foto del usuario
                binding.imgFoto.background = round_bitmap

            } catch (e: IOException) {
                Toast.makeText(this, "Hubo un error " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verificarCampos(): Boolean {

        val bool_nombre: Boolean;
        val bool_tipo: Boolean
        val bool_raza: Boolean;
        val bool_edad: Boolean;
        val bool_color: Boolean

        //Verificar campo nombre
        if (binding.ctxNombre.editText?.text?.isNotEmpty() == true) {
            binding.ctxNombre.error = null
            bool_nombre = true
        } else {
            binding.ctxNombre.error = "Ingresa el nombre"
            bool_nombre = false
        }

        //Verificar campo tipo
        if (binding.ctxTipo.editText?.text?.isNotEmpty() == true) {
            binding.ctxTipo.error = null
            bool_tipo = true
        } else {
            binding.ctxTipo.error = "Ingresa el tipo de mascota"
            bool_tipo = false
        }

        //Verificar campo raza
        if (binding.ctxRaza.editText?.text?.isNotEmpty() == true) {
            binding.ctxRaza.error = null
            bool_raza = true
        } else {
            binding.ctxRaza.error = "Ingresa la raza de la mascota"
            bool_raza = false
        }

        //Verificar campo edad
        if (binding.ctxEdad.editText?.text?.isNotEmpty() == true) {
            binding.ctxEdad.error = null
            bool_edad = true
        } else {
            binding.ctxEdad.error = "Ingresa la edad de la mascota"
            bool_edad = false
        }

        //Verificar campo color
        if (binding.ctxColor.editText?.text?.isNotEmpty() == true) {
            binding.ctxColor.error = null
            bool_color = true
        } else {
            binding.ctxColor.error = "Ingresa el color de la mascota"
            bool_color = false
        }

        return bool_nombre && bool_tipo && bool_raza && bool_edad && bool_color

    }

    private fun guardarDatosMascota() {

        //Objetos para guardar los datos
        val usuario = Firebase.auth.currentUser?.uid
        val storage = Firebase.storage
        val db = Firebase.firestore

        //Subir foto
        if (vmMascota.data().img_foto != null) {

            //Recuperar la url de la foto en el almacenamiento local
            val url_file = vmMascota.data().img_foto

            //Obtener la referencia de storage donde se guardara la foto y subir la foto
            storage.reference.child(usuario!!)
                .child("Fotos_Mascotas/${vmMascota.data().ctx_nombre}.jpg").putFile(url_file!!)
                .addOnSuccessListener {

                    //Obtener la url de descarga de la foto
                    it.storage.downloadUrl.addOnSuccessListener { download_uri ->
                        val uri_foto = download_uri.toString()

                        //Llenar los datos de la mascota
                        val datos_mascota = hashMapOf(
                            "Nombre" to vmMascota.data().ctx_nombre,
                            "Tipo" to vmMascota.data().ctx_tipo_mascota,
                            "Raza" to vmMascota.data().ctx_raza,
                            "Edad" to vmMascota.data().ctx_edad,
                            "Color" to vmMascota.data().ctx_color,
                            "Foto" to uri_foto
                        )
                        //Subir los datos a FirestoreMascotas
                        db.collection("Usuarios").document(usuario).collection("Mascotas")
                            .document(vmMascota.data().ctx_nombre!!).set(datos_mascota).addOnSuccessListener {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Registro Fallido", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Fallo al subir la foto", Toast.LENGTH_LONG).show()
                }
        } else{
            val datos_mascota = hashMapOf(
                "Nombre" to vmMascota.data().ctx_nombre,
                "Tipo" to vmMascota.data().ctx_tipo_mascota,
                "Raza" to vmMascota.data().ctx_raza,
                "Edad" to vmMascota.data().ctx_edad,
                "Color" to vmMascota.data().ctx_color,

                )
            //Subir los datos a FirestoreMascotas
            db.collection("Usuarios").document(usuario!!).collection("Mascotas")
                .document(vmMascota.data().ctx_nombre!!).set(datos_mascota).addOnSuccessListener {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Registro Fallido", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun salvarDatos() {
        vmMascota.data().ctx_nombre = binding.ctxNombre.editText?.text.toString()
        vmMascota.data().ctx_tipo_mascota = binding.ctxTipo.editText?.text.toString()
        vmMascota.data().ctx_raza = binding.ctxRaza.editText?.text.toString()
        vmMascota.data().ctx_edad = binding.ctxEdad.editText?.text.toString()
        vmMascota.data().ctx_color = binding.ctxColor.editText?.text.toString()
    }

    override fun onPause() {
        super.onPause()
        salvarDatos()
    }
}