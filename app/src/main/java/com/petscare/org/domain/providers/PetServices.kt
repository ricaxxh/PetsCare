package com.petscare.org.domain.providers

import com.google.android.gms.maps.model.LatLng
import com.petscare.org.modelo.objetos.PetService

class PetServices {
    companion object{
        fun getSaludServices(): ArrayList<PetService>{
            val servicios = arrayListOf<PetService>(
                PetService("VetCare", LatLng(20.6595289,-105.2280749)),
                PetService("Central Animal", LatLng(20.7169996,-105.2163615)),
                PetService("Welsh", LatLng(20.6389035,-105.2147676)),
                PetService("Wolfs", LatLng(20.6348308,-105.2288588)),
                PetService("Guesos Vallarta", LatLng(20.628998,-105.2288289)),
            )
            return servicios
        }

        fun getAlimentoServices(): ArrayList<PetService>{
            val servicios = arrayListOf<PetService>(
                PetService("El campamento", LatLng(20.6542947,-105.2267271)),
                PetService("Pet Party", LatLng(20.6756667,-105.2110162)),
                PetService("Pastureria", LatLng(20.6702984,-105.2195792)),
                PetService("Chucho y tonchi", LatLng(20.6343168,-105.2180137)),
                PetService("Kuali", LatLng(20.6365681,-105.2158631)),
            )
            return servicios
        }
    }
}