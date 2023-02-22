package com.petscare.org.modelo.dataui

data class UIAuth(
    var frag_index: Int = 0,
    var lada: String= "+52",
    var telefono: String? = null,
    var codigo: String? = null,
    var tiempo_contador: Long = 15000,
    var id_verificacion_guardado: String? = null
)
