package com.petscare.org.modelo.objetos

import com.google.android.gms.maps.model.LatLng

data class PetService(
    var nombre : String? = null,
    var ubicacion : LatLng? = null
)
