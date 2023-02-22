package com.petscare.org.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.petscare.org.modelo.dataui.DataUIRUsuario
import java.io.File

class ViewModelRegistro : ViewModel() {
    val ldata_registro = MutableLiveData<DataUIRUsuario>()

    init {
        ldata_registro.value = DataUIRUsuario()
    }

    fun setUID(UID: String){
        ldata_registro.value?.UID = UID
    }

    fun setIndex(index: Int) {
        ldata_registro.value?.frag_index = index
    }

    fun setNombre(nombre: String) {
        ldata_registro.value?.nombre = nombre
    }

    fun setApellidos(apellidos: String) {
        ldata_registro.value?.apellidos = apellidos
    }

    fun setFechaNacimiento(fecha_nacimiento: String) {
        ldata_registro.value?.fecha_nacimiento = fecha_nacimiento
    }

    fun setGenero(genero: String) {
        ldata_registro.value?.genero = genero
    }

    fun setLada(lada : String){
        ldata_registro.value?.lada = lada
    }

    fun setTelefono(telefono : String){
        ldata_registro.value?.telefono = telefono
    }

    fun setCorreo(correo: String) {
        ldata_registro.value?.correo = correo
    }

    fun setContrasena(contrasena: String) {
        ldata_registro.value?.contrasena = contrasena
    }

    fun setArchivoFoto(archivo: File?){
        ldata_registro.value?.archivo_foto = archivo
    }

    fun getUID(): String?{
        return ldata_registro.value?.UID
    }

    fun getIndex(): Int?{
        return ldata_registro.value?.frag_index
    }

    fun getNombre(): String? {
        return ldata_registro.value?.nombre
    }

    fun getApellidos(): String? {
        return ldata_registro.value?.apellidos
    }

    fun getFechaNacimiento(): String? {
        return ldata_registro.value?.fecha_nacimiento
    }

    fun getGenero(): String? {
        return ldata_registro.value?.genero
    }

    fun getLada(): String? {
        return ldata_registro.value?.lada
    }

    fun getTelefono(): String? {
        return ldata_registro.value?.telefono
    }

    fun getCorreo(): String? {
        return ldata_registro.value?.correo
    }

    fun getContrasena(): String? {
        return ldata_registro.value?.contrasena
    }

    fun getArchivoFoto(): File?{
        return ldata_registro.value?.archivo_foto
    }

}