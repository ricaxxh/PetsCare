package com.petscare.org.utilidades

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class InternetUtil {
    companion object{
        fun verificarConexionInternet(context: Context):Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork
            val tipos_conexiones = cm.getNetworkCapabilities(network)
            return tipos_conexiones != null && (tipos_conexiones.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || tipos_conexiones.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        }
    }
}