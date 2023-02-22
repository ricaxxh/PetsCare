package com.petscare.org.modelo.dataui

import java.io.File

data class DataUIRUsuario(
    var frag_index: Int = 0,
    var UID: String = "",
    var nombre: String = "",
    var apellidos: String = "",
    var fecha_nacimiento: String = "",
    var genero: String = "",
    var lada: String = "",
    var telefono: String = "",
    var correo: String = "",
    var contrasena: String = "",
    var archivo_foto: File? = null
)
