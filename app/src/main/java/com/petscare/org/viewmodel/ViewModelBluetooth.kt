package com.petscare.org.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.petscare.org.modelo.dataui.DataUIBluetooth

class ViewModelBluetooth: ViewModel() {

    val ldata_ui_bluetooth = MutableLiveData<DataUIBluetooth>()
    val ldata_bt_device = MutableLiveData<BluetoothDevice?>()

    init {
        ldata_ui_bluetooth.value = DataUIBluetooth()
        ldata_bt_device.value = null
    }
}