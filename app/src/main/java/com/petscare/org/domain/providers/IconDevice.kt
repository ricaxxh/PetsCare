package com.petscare.org.domain.providers

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.petscare.org.R
import com.petscare.org.modelo.objetos.Dispositivo

class IconDevice {
    companion object{

        /*fun config(context: Context, dispositivo: Dispositivo): Drawable?{

            var icon : Drawable? = null

            when(dispositivo.tipo){
                TipoDispositivo.DISPENSADOR_AGUA.name -> icon = AppCompatResources.getDrawable(context,R.drawable.ic_dispensador_agua)
                TipoDispositivo.DISPENSADOR_ALIMENTO.name -> icon = AppCompatResources.getDrawable(context,R.drawable.ic_dispensador_alimento)
                TipoDispositivo.PUERTA.name -> icon = AppCompatResources.getDrawable(context,R.drawable.ic_puerta_cerrada)
                TipoDispositivo.MAQUINA_PROPINAS.name -> icon = AppCompatResources.getDrawable(context,R.drawable.ic_maquina_propinas)
                TipoDispositivo.SECADORA.name -> icon = AppCompatResources.getDrawable(context,R.drawable.ic_secadora)
                TipoDispositivo.AIRE_ACONDICIONADO.name -> icon = AppCompatResources.getDrawable(context,R.drawable.ic_aire_acondicionado)
                TipoDispositivo.FOCO.name -> icon = AppCompatResources.getDrawable(context,R.drawable.ic_foco)
            }

            val icon_color = DrawableCompat.wrap(icon!!)

            if (dispositivo.conectado == true){
                Toast.makeText(context,"Conectado",Toast.LENGTH_SHORT).show()
                if (dispositivo.valor_digital != null){ // Digital
                    if (dispositivo.valor_digital == true){
                        DrawableCompat.setTint(icon_color,ContextCompat.getColor(context,R.color.verde))
                    } else{
                        DrawableCompat.setTint(icon_color,ContextCompat.getColor(context,R.color.rojo_pastel))
                    }
                } else{ // Analogico
                    when(dispositivo.valor_analogico!!.toInt()){
                        in 0..29 -> DrawableCompat.setTint(icon_color,ContextCompat.getColor(context,R.color.rojo_pastel))
                        in 30..69 -> DrawableCompat.setTint(icon_color,ContextCompat.getColor(context,R.color.naranja))
                        in 70..100 -> DrawableCompat.setTint(icon_color,ContextCompat.getColor(context,R.color.verde))
                    }
                }
            } else{
                DrawableCompat.setTint(icon_color,ContextCompat.getColor(context,R.color.parrafos))
                Toast.makeText(context,"Desconectado",Toast.LENGTH_SHORT).show()
            }

            return icon_color
        }*/
    }
}