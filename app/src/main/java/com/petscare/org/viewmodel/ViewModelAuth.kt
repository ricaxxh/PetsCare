package com.petscare.org.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.petscare.org.modelo.dataui.UIAuth

class ViewModelAuth : ViewModel() {

    val ldata_auth = MutableLiveData<UIAuth>()

    init {
        ldata_auth.value = UIAuth()
    }

    fun setIndex(index: Int){
        ldata_auth.value?.frag_index = index
    }

    fun setLada(lada: String){
        ldata_auth.value?.lada = lada
    }

    fun setTelefono(telefono: String){
        ldata_auth.value?.telefono = telefono
    }

    fun setCodigo(codigo: String){
        ldata_auth.value?.codigo = codigo
    }

    fun setTiempoContador(tiempo: Long){
        ldata_auth.value?.tiempo_contador = tiempo
    }

    fun setIdVerificacionGuardado(id:String?){
        ldata_auth.value?.id_verificacion_guardado = id
    }

    fun getIndex(): Int?{
        return ldata_auth.value?.frag_index
    }

    fun getLada(): String?{
        return ldata_auth.value?.lada
    }

    fun getTelefono(): String? {
        return ldata_auth.value?.telefono
    }

    fun getCodigo(): String?{
        return ldata_auth.value?.codigo
    }

    fun getTiempoContador(): Long{
        return ldata_auth.value!!.tiempo_contador
    }

    fun getCodigoVerificacionGuardado(): String?{
        return ldata_auth.value?.id_verificacion_guardado
    }
}