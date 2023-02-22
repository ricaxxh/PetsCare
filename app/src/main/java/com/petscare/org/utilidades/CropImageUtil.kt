package com.petscare.org.utilidades

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.yalantis.ucrop.UCrop

class CropImageUtil : ActivityResultContract<Pair<Uri, Uri>, Uri?>() {

    override fun createIntent(context: Context, input: Pair<Uri, Uri>): Intent =
        UCrop.of(input.first, input.second)
            .withOptions(opcionesRecorte())
            .getIntent(context)

    private fun opcionesRecorte(): UCrop.Options {
        val opciones_recorte = UCrop.Options()
        opciones_recorte.withAspectRatio(1f,1f)
        opciones_recorte.withMaxResultSize(1000,1000)
        opciones_recorte.setCompressionQuality(70)
        opciones_recorte.setHideBottomControls(false)
        opciones_recorte.setFreeStyleCropEnabled(false)
        opciones_recorte.setStatusBarColor(Color.WHITE)
        opciones_recorte.setToolbarColor(Color.WHITE)
        opciones_recorte.setToolbarTitle("Recortar foto")
        return opciones_recorte
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK || intent == null) return null
        return UCrop.getOutput(intent)
    }
}