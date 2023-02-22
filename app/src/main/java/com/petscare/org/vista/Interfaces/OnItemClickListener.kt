package com.petscare.org.vista.Interfaces

import android.bluetooth.BluetoothDevice

interface OnItemClickListener {
    fun onItemClick(bt_device : BluetoothDevice)
}