package com.petscare.org.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.petscare.org.domain.data.FirestoreData
import com.petscare.org.modelo.dataui.DataUIRMascota
import com.petscare.org.modelo.objetos.Mascota

class ViewModelMascota: ViewModel() {

    private val ldata_mascotas = MutableLiveData<DataUIRMascota>()

    init {
        ldata_mascotas.value = DataUIRMascota()
    }

    fun data(): DataUIRMascota {
        return ldata_mascotas.value!!
    }

    fun liveData(): LiveData<DataUIRMascota>{
        return ldata_mascotas
    }

    override fun onCleared() {
        super.onCleared()
    }
}