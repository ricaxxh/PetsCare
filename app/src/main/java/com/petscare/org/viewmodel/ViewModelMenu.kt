package com.petscare.org.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.petscare.org.modelo.dataui.DataUIMenu

class ViewModelMenu: ViewModel() {

    private val ldata_menu = MutableLiveData<DataUIMenu>()

    init {
        ldata_menu.value = DataUIMenu()
    }

    fun Data(): DataUIMenu{
        return ldata_menu.value!!
    }

    fun liveData(): LiveData<DataUIMenu>{
        return ldata_menu
    }
}
