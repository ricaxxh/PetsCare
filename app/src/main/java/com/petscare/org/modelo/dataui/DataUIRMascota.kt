package com.petscare.org.modelo.dataui

import android.net.Uri

data class DataUIRMascota(
    var ctx_nombre: String? = null,
    var ctx_tipo_mascota: String? = null,
    var ctx_raza: String? = null,
    var ctx_edad: String? = null,
    var ctx_color: String? = null,
    var img_foto: Uri? = null
)
